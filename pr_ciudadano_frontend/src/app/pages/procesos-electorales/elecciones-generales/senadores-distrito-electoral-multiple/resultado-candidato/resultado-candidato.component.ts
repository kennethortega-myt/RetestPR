import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild, HostListener } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Subject, forkJoin, take, takeUntil } from "rxjs";
import { IBarInfo } from "../../../../../components/grafica-congresal-candidato/grafica-congresal-candidato.interfaces";
import { TIPO_FILTRO, ID_AMBITO_GEOGRAFICO, MENSAJE_REPORTE, CANTIDAD_LIMITE_CANDIDATOS } from "../../../../../helpers/constantes";
import { EnumMensajeGeneral } from "../../../../../helpers/estado-enum";
import { descargarPdf } from "../../../../../helpers/funciones";
import { makeScaleValues } from "../../../../../helpers/handler-chart-data.common";
import { Mesa } from "../../../../../interfaces/acta-bean";
import { Candidato } from "../../../../../interfaces/eleccion-congresal-bean";
import { Region } from "../../../../../interfaces/elections.interfaces";
import { Resumen } from "../../../../../interfaces/resumen-bean";
import { MapaCalor } from "../../../../../interfaces/resumen-general-bean";
import { encryptStorageEleccion } from "../../../../../settings/encrypt-storage.settings";
import { DistritoElectoral } from "../../../../../interfaces/ubigeo-bean";
import { BehaviorResumenService } from "../../../../../services/elecciones-generales/behavior-resumen.service";
import { MesaService } from "../../../../../services/elecciones-generales/mesa.service";
import { RandomImageService } from "../../../../../services/elecciones-generales/random-image.service";
import { ReporteService } from "../../../../../services/elecciones-generales/reporte.service";
import { ResumenGeneralService } from "../../../../../services/elecciones-generales/resumen-general.service";
import { SenadoresDistritoElectoralMultipleService } from "../../../../../services/elecciones-generales/senadores-distrito-electoral-multiple.service";
import { SnackbarService } from "../../../../../services/elecciones-generales/snackbar.service";
import { UbigeoService } from "../../../../../services/elecciones-generales/ubigeo.service";
import { BreakpointObserver } from "@angular/cdk/layout";
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGrafic } from "../../../../../helpers/responsive-dimentions.helper";
import { NUMBER_OF_BAR_IN_GRAFIC } from "../../../../../settings/responsive.settings";
import { GenericFilterUbigeoComponent } from "../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component";
import { GenericFilterParams } from "../../../../../interfaces/filtro-settings";

@Component({
  selector: "app-resultado-candidato",
  templateUrl: "./resultado-candidato.component.html",
  styleUrls: ["./resultado-candidato.component.scss"],
  standalone: false,
})
export class ResultadoCandidatoComponent implements OnInit, OnDestroy {
  @ViewChild(GenericFilterUbigeoComponent, { static: false }) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  isUbigeoReady = false;

  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;

    if (!this.esPantallaChica) {
      this.mostrarMapa2 = true;
    } else {
      this.mostrarMapa2 = false;
    }
  }

  toggleMapa() {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  // imagen aleatoria
  public randomImageUrl: string;
  listRegion: DistritoElectoral[] = [];
  candidatosGrafica: Candidato[];
  filterButtonIsDisabled: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  primeraVez: boolean = true;
  public myFormCandidato: FormGroup = this.fb.group({
    nomCandidato: [null, Validators.required],
  });
  public myFormUbigeo: FormGroup = this.fb.group({
    region: [0, Validators.required],
  });
  esExtranjero: boolean = false;
  @Input() idEleccion = 0;
  @Input() resumen: Resumen;
  @Input() active: boolean = false;

  @Output() updateResumenGeneral = new EventEmitter<number>();

  listaCandidatos: Candidato[];
  listaCandidatosGrafica: Candidato[];
  listaCandidatosGraficaOriginal: Candidato[];
  escalaTotalVotos: number[];
  valorMaximo: number = 0;

  valorMaximoParticipante: number = 0;
  public dataForDistrict: IBarInfo[] = [];
  public scalesForDistrict: number[] = [];
  public numberCandidatesForDistrict = NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;;
  totalPaginasGrafica: number = 8;

  mapaCalor: MapaCalor[];
  regionSeleccionada: Region;
  mesaTotales: Mesa;
  datosCandidato: Candidato[];
  paginaActual: number = 0;
  totalPagina: number = 0;

  mensaje: string = EnumMensajeGeneral.MENSAJE_INFORMACION_SELECCIONADA;
  deshabilitarBotonGenerarReporte: boolean = false;

  valorFiltroDistritoElectoral: number = null;
  valorFiltroDistritoElectoralButtonReporte: number = null;

  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;
  private resizeObserver: ResizeObserver;
  heightGrafica: number;
  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private readonly fb: FormBuilder,
    public dialog: MatDialog,
    private readonly senadoresDistritoElectoralMultipleService: SenadoresDistritoElectoralMultipleService,
    private readonly ubigeoService: UbigeoService,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly mesaService: MesaService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService,
    // imagen aleatoria
    private readonly randomImageService: RandomImageService,
    private readonly breakpointObserver: BreakpointObserver
  ) {
    this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).subscribe((result) => {
      this.numberCandidatesForDistrict = getNumberOfBarsForGrafic(result.breakpoints);
    });
  }

  get cantidadLimiteCandidatos(){
    return CANTIDAD_LIMITE_CANDIDATOS.SENADORES_DEM;
  }

  ngOnInit(): void {
    this.cargarResumen();
    this.cargarDatosInicial();
    // imagen aleatoria
    this.randomImageUrl = this.randomImageService.getRandomImage();
  }


  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        const height = entry.contentRect.height;
        
        if (this.timeoutId !== null) {
          clearTimeout(this.timeoutId);
          this.timeoutId = null;
        }

        this.timeoutId = setTimeout(() => {
          this.heightGrafica = Math.round(height);
          this.timeoutId = null;
        }, 1000);
      }
    });
  }

  cargarDatosInicial(): void {
    let idEleccion = this.idEleccion;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.resumen.idUbigeoDistritoElectoral;
    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    this.valorFiltroDistritoElectoral = this.resumen.idUbigeoDistritoElectoral;
    const serviciosCombinados = forkJoin({
      regiones: this.ubigeoService.listarDistritoElectorales().pipe(takeUntil(this.destroy$)),
      listaParticipantes: this.senadoresDistritoElectoralMultipleService
        .listarParticipantesCandidato({
          idDistritoElectoral: ubigeoNivel1,
          idEleccion: idEleccion,
          tipoFiltro: tipoFiltro,
        })
        .pipe(takeUntil(this.destroy$)),
      mapaCalor: this.resumenGeneralService
        .listarMapaCalor("0", ambitoGeografico, this.idEleccion, tipoFiltro, 0, 0, 0)
        .pipe(takeUntil(this.destroy$)),
    });

    serviciosCombinados.pipe(takeUntil(this.destroy$)).subscribe({
      next: (result) => {
        this.listRegion = result.regiones.data;

        const initialRegion = Number(this.resumen.idUbigeoDistritoElectoral);

        this.valorFiltroDistritoElectoral = initialRegion;

        this.myFormUbigeo.get("region")?.setValue(initialRegion, { emitEvent: false });

        this.isUbigeoReady = true;

        setTimeout(() => {
          if (!this.mainFiltroUbigeoComponent) return;
          this.mainFiltroUbigeoComponent.setElectoralRegion(initialRegion);
          this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();
          this.mainFiltroUbigeoComponent.breadcrumbString =
            this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
        }, 0);
        
        this.mapaCalor = result.mapaCalor.data;
        this.listaCandidatosGraficaOriginal = result.listaParticipantes.data;
        this.cargarDataGrafica(result.listaParticipantes.data);
        if (result.listaParticipantes.success) {
          this.datosCandidato = result.listaParticipantes.data;
          this.listaCandidatosGraficaOriginal = this.separarPorGrupo(
            result.listaParticipantes.data,
            this.numberCandidatesForDistrict
          );
        } else {
          this.mensaje = EnumMensajeGeneral.MENSAJE_INFORMACION_SELECCIONADA;
          this.deshabilitarBotonGenerarReporte = true;
        }
        this.continueFilter();
      },
      error: (err) => {
        console.error("Error al cargar datos", err);
      }
    });
  }

  private separarPorGrupo(lista: Candidato[] = [], tamanioGrupo: number): Candidato[] {
    let listaResult: Candidato[] = [];
    let grupo: number = 0;
    let conta: number = 1;
    for (const element of lista) {
      if (conta > tamanioGrupo) {
        grupo = grupo + 1;
        conta = 1;
      }
      element.grupo = grupo;
      listaResult.push(element);
      conta = conta + 1;
    }
    this.totalPagina = grupo;
    return listaResult;
  }

  private cargarDataGrafica(data: Candidato[]): void {
    this.listaCandidatosGrafica = data;
    const valorMax = data?.length ? Math.max(...data.map((x) => x.totalVotosValidos)) : 0;
    this.valorMaximoParticipante = valorMax;

    this.dataForDistrict = [];
    if (data != undefined) {
      this.dataForDistrict = data.map((x) => {
        let data: IBarInfo = {
          percentage_of_valid_votes:
            this.valorMaximoParticipante == 0 ? 0 : (x.totalVotosValidos / this.valorMaximoParticipante) * 100,
          number_of_valid_votes: x.totalVotosValidos,
          url_candidate_image: null,
          urlAgrupacionImage: null,
          name_of_candidate: x.nombreCandidato,
          name_of_politic_group: x.nombreAgrupacionPolitica,
          number_of_list: x.lista,
          code_of_politic_group: x.codigoAgrupacionPolitica,
          number_of_candidate: x.dniCandidato,
          group: x.grupo,
        };
        return data;
      });

      this.scalesForDistrict = makeScaleValues(this.listaCandidatosGrafica, this.numberCandidatesForDistrict);
    }
  }

  filtrar(params: GenericFilterParams): void {
    const regionValue = Number(params?.electoralDistrictId);
    if (!regionValue) return;

    if (this.valorFiltroDistritoElectoral === regionValue) return;

    this.valorFiltroDistritoElectoral = regionValue;

    // sincroniza form
    this.myFormUbigeo.get("region")?.setValue(regionValue, { emitEvent: false });

    // sincroniza breadcrumb
    if (this.mainFiltroUbigeoComponent) {
      this.mainFiltroUbigeoComponent.setElectoralRegion(regionValue);
      this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();
      this.mainFiltroUbigeoComponent.breadcrumbString =
        this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
    }

    // UI + data
    this.dataForDistrict = [];
    this.deshabilitarBotonGenerarReporte = true;
    this.mensaje = EnumMensajeGeneral.MENSAJE_INFORMACION_SELECCIONADA;

    this.continueFilter();
  }

  continueFilter(){
    this.paginaActual = 0;
    this.behaviorResumenService.setActualizarResumen(this.myFormUbigeo.get("region").value);
    this.listarCandidatosGrafica();
    this.listarParticipacionCiudadana();
  }

  listarCandidatosGrafica(): void {
    let idEleccion = this.idEleccion;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.myFormUbigeo.get("region").value;
    this.listaCandidatosGraficaOriginal = [];
    this.senadoresDistritoElectoralMultipleService
      .listarParticipantesCandidato({
        idDistritoElectoral: ubigeoNivel1,
        idEleccion: idEleccion,
        tipoFiltro: tipoFiltro,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaCandidatosGraficaOriginal = this.separarPorGrupo(result.data, this.numberCandidatesForDistrict);
            this.deshabilitarBotonGenerarReporte = false;
          } else {
            this.deshabilitarBotonGenerarReporte = true;
          }

          this.cargarDataGrafica(this.listaCandidatosGraficaOriginal);
        },
        error: (err) => {
          console.error("Error al cargar datos", err);
        }
      });
  }

  private obtenerTotalesMesa(): void {
    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let tipoFiltro = "distrito_electoral";
    let ubigeoNivel1 = 0;
    let ubigeoNivel2 = 0;
    let ubigeoNivel3 = 0;
    let distritoElectoral = this.myFormUbigeo.get("region").value;
    this.mesaService
      .obtenerTotales(ambitoGeografico, tipoFiltro, ubigeoNivel1, ubigeoNivel2, ubigeoNivel3, distritoElectoral)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.mesaTotales = result.data;
        },
      });
  }

  listarParticipacionCiudadana() {
    let codigoAgrupacionPolitica = null;
    let idAmbitoGeografico = 0;
    let idUbigeoDepartamento = null;
    let idUbigeoProvincia = null;
    let idUbigeoDistrito = null;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    if (this.myFormUbigeo.get("region").value == 27) {
      idAmbitoGeografico = 2;
      tipoFiltro = TIPO_FILTRO.UBIGEO_NIVEL_01;
    }
    this.resumenGeneralService
      .listarMapaCalor(
        codigoAgrupacionPolitica,
        idAmbitoGeografico,
        this.idEleccion,
        tipoFiltro,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.esExtranjero = this.myFormUbigeo.get("region").value == 27;
          this.mapaCalor = value.data;
        },
      });
  }

  descargarReporte(tipoReporte: number): void {
    const regionValue = this.myFormUbigeo.get("region").value;

    if (regionValue === 0) {
      return;
    }

    if (this.shouldUpdateFiltro(regionValue, tipoReporte)) {
      this.valorFiltroDistritoElectoralButtonReporte = regionValue;
    } else {
      this.snackbarService.showSnackbarWithSuccessMessage("La descarga es una vez por ubicación.", "ambar");
      return;
    }

    this.updateButtonClickState(tipoReporte);

    const idAmbitoGeografico = regionValue > 26 ? 2 : 1;
    const idDistritoElectoral = regionValue;
    const idEleccion = this.idEleccion;

    const objProceso = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO"));
    const tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    const nombreProceso = objProceso.nombre;
    const nombreEleccion = "Elección de Senadores 27 / Resultado por Candidato";

    this.reporteService
      .descargarPdfEleccionSenadoresMultipleCandidato(
        tipoReporte,
        idAmbitoGeografico,
        idDistritoElectoral,
        idEleccion,
        nombreProceso,
        nombreEleccion,
        tipoFiltro
      )
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          descargarPdf("RESULTADO_POR_UBICACION_GEOGRAFICA", result);
          this.snackbarService.showSnackbarWithSuccessMessage();
        },
        error: (err) => {
          this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
          console.error("Error al cargar datos", err);
        },
      });
  }

  private shouldUpdateFiltro(regionValue: number, tipoReporte: number): boolean {
    return (
      !this.valorFiltroDistritoElectoralButtonReporte ||
      this.valorFiltroDistritoElectoralButtonReporte !== regionValue ||
      (tipoReporte === 1 && !this.buttonReportePdfclick) ||
      (tipoReporte === 2 && !this.buttonReporteCsvclick)
    );
  }

  private updateButtonClickState(tipoReporte: number): void {
    if (tipoReporte === 1) {
      this.buttonReportePdfclick = true;
    } else if (tipoReporte === 2) {
      this.buttonReporteCsvclick = true;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();

    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
      this.resizeObserver = null;
    }

    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }

  seleccionarDistritoElectoralPorId(value: number): void {
    this.filtrar({ electoralDistrictId: Number(value) });
  }

  seleccionarDistritoElectoralPorParams(params: any): void {
    if (params?.electoralDistrictId) {
      this.seleccionarDistritoElectoralPorId(params.electoralDistrictId);
    }
  }

  cargarResumen(): void {
    this.behaviorResumenService.setActualizarResumen(this.myFormUbigeo.get("region").value);
  }
}
