import { Component, ElementRef, Input, OnInit, ViewChild, HostListener, OnDestroy } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Observable, Subject, forkJoin, take, takeUntil, tap, debounceTime, distinctUntilChanged } from "rxjs";
import { MatSelect } from "@angular/material/select";

import { IChartBarInfo } from "../../../../../interfaces/chart-bar-info.interface";
import { TIPO_FILTRO, ID_AMBITO_GEOGRAFICO, MENSAJE_REPORTE, DISTRITO_ELECTORAL_LIMA_ID } from "../../../../../helpers/constantes";
import { EnumMensajeGeneral } from "../../../../../helpers/estado-enum";
import { descargarPdf } from "../../../../../helpers/funciones";
import { makeScaleValues, makeScaleValuesFromGenericArray } from "../../../../../helpers/handler-chart-data.common";
import { Mesa } from "../../../../../interfaces/acta-bean";
import { Participante } from "../../../../../interfaces/eleccion-congresal-bean";
import { Region } from "../../../../../interfaces/elections.interfaces";
import { Resumen } from "../../../../../interfaces/resumen-bean";
import { MapaCalor } from "../../../../../interfaces/resumen-general-bean";
import { encryptStorageEleccion } from "../../../../../settings/encrypt-storage.settings";
import { DistritoElectoral } from "../../../../../interfaces/ubigeo-bean";
import { BehaviorResumenService } from "../../../../../services/elecciones-generales/behavior-resumen.service";
import { EleccionCongresalService } from "../../../../../services/elecciones-generales/eleccion-congresal.service";
import { MesaService } from "../../../../../services/elecciones-generales/mesa.service";
import { RandomImageService } from "../../../../../services/elecciones-generales/random-image.service";
import { ReporteService } from "../../../../../services/elecciones-generales/reporte.service";
import { ResumenGeneralService } from "../../../../../services/elecciones-generales/resumen-general.service";
import { SnackbarService } from "../../../../../services/elecciones-generales/snackbar.service";
import { UbigeoService } from "../../../../../services/elecciones-generales/ubigeo.service";
import { FrontendResponse } from "../../../../../interfaces/response.common";
import { GenericFilterParams } from "../../../../../interfaces/filtro-settings";
import { GenericFilterUbigeoComponent } from "../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component";
import { NUMBER_OF_BAR_IN_GRAFIC } from "../../../../../settings/responsive.settings";
import { BreakpointObserver } from "@angular/cdk/layout";
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGrafic } from "../../../../../helpers/responsive-dimentions.helper";
import { ModalDetailVotes } from "../../../../../interfaces/modal-detail-votes.interface";
import { ModalDetailVotesComponent } from "../../../../../components/modal-detail-votes/modal-detail-votes.component";
import { ModalDetailVotesService } from "../../../../../services/common/modal-detail-votes.service";
import { DELAY_SEARCH } from "../../../../../constants/search-incremental.constants";
import { mapWithPoliticImage } from "../../../../../helpers/get-images.helper";

@Component({
  selector: "app-resultado-por-ubicacion-geografica",
  templateUrl: "./resultado-por-ubicacion-geografica.component.html",
  styleUrls: ["./resultado-por-ubicacion-geografica.component.scss"],
  standalone: false,
})
export class ResultadoPorUbicacionGeograficaComponent implements OnInit, OnDestroy {
  
  @ViewChild("graficaA") graficaA: ElementRef;
  @ViewChild(GenericFilterUbigeoComponent) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  idDistritoElectoralSeleccionado: number = DISTRITO_ELECTORAL_LIMA_ID;
  rulerPercentage = {

  }

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

  private resizeObserver: ResizeObserver;
  heightGrafica: number;

  public randomImageUrl: string;
  @Input() idEleccion = 0;
  @Input() resumen: Resumen;

  vermas1: boolean = false;
  detalle1: boolean = true;
  filterButtonIsDisabled: boolean = true;
  listOrganizacionPolitica: Participante[] = [];
  listOrganizacionPoliticaOrigin: Participante[] = [];
  listRegion: DistritoElectoral[] = [];
  totalCandidatos = 0;
  public dataForDistrict: IChartBarInfo[] = [];
  public dataForDistrictOrigin: IChartBarInfo[] = [];

  public maxValueForScaleName: number[] = [];
  public scalesForDistrict: number[] = [];
  public numberCandidatesForDistrict = NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;
  destroy$: Subject<boolean> = new Subject<boolean>();
  private readonly searchDestroy$ = new Subject<void>();

  escalaTotalVotos: number[];
  valorMaximo: number = 0;
  valorMaximoParticipante: number = 0;
  mapaCalor: MapaCalor[];
  mesaTotales: Mesa;
  public myFormOrganizacion: FormGroup = this.fb.group({
    nomAgrupacion: [null, Validators.required],
  });
  public myFormUbigeo: FormGroup = this.fb.group({
    region: [0, Validators.required],
  });

  regionSeleccionada: Region;
  primeraVez: boolean = true;
  totalVotosEmitidos: number = 0;
  totalVotosValidos: number = 0;
  totalVotos: number = 0;
  totalPaginasGrafica: number = -1;
  mostrarTotalVotos: boolean = true;
  deshabilitarBotonGenerarReporte: boolean = false;
  esExtranjero: boolean = false;
  mensaje: string = EnumMensajeGeneral.MENSAJE_PRESIONE_BOTON_FILTRAR;
  mensajeNoResultado: string = EnumMensajeGeneral.MENSAJE_NO_SE_ENCONTRARON_RESULTADOS;
  valorFiltroDistritoElectoral: number = null;
  labelFiltroDistritoElectoral: string = null;
  valorFiltroDistritoElectoralButtonReporte: number = null;
  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;
  votosBlanco: Participante = null;
  votosNulo: Participante = null;
  observadorResizeTemporal: any;
  triggerReset: boolean = false;

  constructor(
    public dialog: MatDialog,
    private readonly fb: FormBuilder,
    private readonly eleccionCongresalService: EleccionCongresalService,
    private readonly ubigeoService: UbigeoService,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly mesaService: MesaService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService,
    private readonly randomImageService: RandomImageService,
    private readonly breakpointObserver: BreakpointObserver,
    private readonly modalDetailVotesService: ModalDetailVotesService
  ) {
    this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.numberCandidatesForDistrict = getNumberOfBarsForGrafic(result.breakpoints);
      this.cargarListaInicial();
    });
  }

  ngOnInit() {
    this.cargarListaInicial();
    // imagen aleatoria
    this.randomImageUrl = this.randomImageService.getRandomImage();

    // Configurar búsqueda incremental con debounce de 500ms
    this.myFormOrganizacion.get('nomAgrupacion')?.valueChanges
      .pipe(
        debounceTime(DELAY_SEARCH),
        distinctUntilChanged(),
        takeUntil(this.searchDestroy$)
      )
      .subscribe((searchTerm: string) => {
        this.performSearch(searchTerm);
      });
  }

  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        // Obtener la altura del div que está siendo observado
        const height = entry.contentRect.height;
        setTimeout(() => {
          this.heightGrafica = Math.round(height);
        }, 1000);

      }
    });
    // Comenzar a observar el div graficaA
    if(this.graficaA){
      this.resizeObserver.observe(this.graficaA.nativeElement);
    }
  }

  ngOnDestroy() {
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
    }
    // Limpiar suscripciones de búsqueda
    this.searchDestroy$.next();
    this.searchDestroy$.complete();
  }

  cargarListaInicial(): void {
    if(this.resumen == undefined || this.resumen == null) {
      return;
    }

    let codigoAgrupacionPolitica = "0";
    let idAmbitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let idUbigeoDepartamento = 0;
    let idUbigeoProvincia = 0;
    let idUbigeoDistrito = 0;
    let idEleccion = this.idEleccion;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let distritoElectoral = this.resumen.idUbigeoDistritoElectoral;
    this.valorFiltroDistritoElectoral = this.resumen.idUbigeoDistritoElectoral;

    let nombreApellidoPartido = null;
    let ubigeoNivel1 = this.resumen.idUbigeoDistritoElectoral;

    let subMapaCalor = this.resumenGeneralService
      .listarMapaCalor(
        codigoAgrupacionPolitica,
        idAmbitoGeografico,
        this.idEleccion,
        TIPO_FILTRO.DISTRITO_ELECTORAL,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito
      )
      .pipe(takeUntil(this.destroy$));

    let subListaOrganizaciones = this.eleccionCongresalService.listarOrganizaciones(
      nombreApellidoPartido,
      idEleccion,
      tipoFiltro,
      ubigeoNivel1
    );

    let subTotales = this.mesaService
      .obtenerTotales(
        idAmbitoGeografico,
        tipoFiltro,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito,
        distritoElectoral
      )
      .pipe(takeUntil(this.destroy$));

    let subRegiones = this.ubigeoService.listarDistritoElectorales().pipe(takeUntil(this.destroy$));

    const serviciosCombinados = forkJoin([
      subMapaCalor,
      subRegiones,
      subListaOrganizaciones,
      subTotales,
    ]);

    serviciosCombinados.subscribe({
      next: (result) => {
        const [
          { data: mapaCalorData } = { data: [] },
          { data: listRegionData } = { data: [] },
          { data: politicalOrganizationData } = { data: [] },
          { data: mesaTotalesData } = { data: [] },
        ] = result;

        this.mapaCalor = mapaCalorData;
        this.listRegion = listRegionData;
        this.mesaTotales = mesaTotalesData as Mesa;
        
        this.listOrganizacionPolitica = mapWithPoliticImage(politicalOrganizationData);
        this.listOrganizacionPolitica =  this.filtrarAchurados(this.listOrganizacionPolitica)
        this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(this.resumen.idUbigeoDistritoElectoral);        
        this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
        this.votosBlanco = this.getItemVotosBlancosNulos(politicalOrganizationData, "80");
        this.votosNulo = this.getItemVotosBlancosNulos(politicalOrganizationData, "81");

        this.listOrganizacionPoliticaOrigin = this.listOrganizacionPolitica;
        this.calcularTotales();
        this.totalCandidatos = this.filtrarAchurados(this.listOrganizacionPolitica).length;

        this.cargarGraficaBar(this.listOrganizacionPolitica);

        this.primeraVez = false;
        this.hideLoading();

        this.maxValueForScaleName = makeScaleValuesFromGenericArray(
          politicalOrganizationData.map((item) => item.totalVotosValidos),
          5
        ).reverse();
      },
      error: (err) => this.hideLoading(),
    });
  }

  private hideLoading() {
  }

  private getItemVotosBlancosNulos(organizations: Participante[], codigoAgrupacionPolitica) {
    return organizations.find((f) => f.codigoAgrupacionPolitica === codigoAgrupacionPolitica);
  }

  private cargarGraficaBar(data): void {
    this.listOrganizacionPolitica = data;

    let valorMax = Math.max(...this.listOrganizacionPolitica.map((x) => x.totalVotosValidos));
    this.valorMaximoParticipante = valorMax;
    this.votosBlanco = this.getItemVotosBlancosNulos(this.listOrganizacionPoliticaOrigin, "80");
    this.votosNulo = this.getItemVotosBlancosNulos(this.listOrganizacionPoliticaOrigin, "81");

    let listaTmp = data.sort((a, b) => {
      if (a.totalVotosValidos > b.totalVotosValidos) return -1;
      if (a.totalVotosValidos < b.totalVotosValidos) return 1;
      return 0;
    });

    this.dataForDistrict = listaTmp.map((x) => {
      let data: IChartBarInfo = {
        percentage_for_chart:
          this.valorMaximoParticipante == 0 ? 0 : (x.totalVotosValidos / this.valorMaximoParticipante) * 100,
        number_of_valid_votes: x.totalVotosValidos,
        url_candidate_image: null,
        urlAgrupacionImage: null,
        name_of_candidate: x.nombreCandidato,
        name_of_politic_group: x.nombreAgrupacionPolitica,
        number_of_list: null,
        code_of_politic_group: x.codigoAgrupacionPolitica,
        percentage_valid_votes: x.porcentajeVotosValidos,
      };
      return data;
    });

    this.scalesForDistrict = makeScaleValues(this.listOrganizacionPolitica, this.numberCandidatesForDistrict);
    this.dataForDistrictOrigin = this.separarPorGrupo(this.dataForDistrict, this.numberCandidatesForDistrict);
    this.calcularEscala(this.listOrganizacionPoliticaOrigin);
  }

  private separarPorGrupo(lista: IChartBarInfo[], tamanioGrupo: number): IChartBarInfo[] {
    let listaResult: IChartBarInfo[] = [];
    let grupo: number = 0;
    let conta: number = 1;
    for (const element of lista) {
      if (conta > tamanioGrupo) {
        grupo = grupo + 1;
        conta = 1;
      }
      element.group = grupo;
      listaResult.push(element);
      conta = conta + 1;
    }
    this.totalPaginasGrafica = grupo;
    return listaResult;
  }

  listarRegion(): void {
    this.ubigeoService
      .listarDistritoElectorales()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.listRegion = result.data;

          if (this.primeraVez) {
            let findDistrito = this.listRegion.find((x) => x.codigo == this.resumen.idUbigeoDistritoElectoral);

            this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(Number(findDistrito), { emitEvent: false });

            this.regionSeleccionada = findDistrito as Region;
            this.primeraVez = false;
            this.filtrar();
          }
        },
      });
  }

  onRegionChanged($event: Event, mat: MatSelect): void {
    this.filterButtonIsDisabled = false;
    this.deshabilitarBotonGenerarReporte = true;
    this.filterButtonIsDisabled = false;
    this.dataForDistrictOrigin = [];
    this.listOrganizacionPolitica = [];
    this.listOrganizacionPoliticaOrigin = [];
    this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
  }

  filtrar(params?: GenericFilterParams): void {
    const region_value = params.electoralDistrictId;
    this.idDistritoElectoralSeleccionado = params.electoralDistrictId;
    
    this.mainFiltroUbigeoComponent.setElectoralRegion(Number(region_value));    
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();

    if (this.valorFiltroDistritoElectoral != region_value) {
      this.valorFiltroDistritoElectoral = region_value;
      this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
    } else {
      return;
    }

    this.behaviorResumenService.setActualizarResumen(region_value);
    this.deshabilitarBotonGenerarReporte = false;

    const participacion$ = this.listarParticipacionCiudadana();
    const organizacion$ = this.listarOrganizacionPoliticaOnline("");

    // Loading functionality removed
    forkJoin([participacion$, organizacion$]).subscribe({
      next: ([participacion, organizacion]) => {
        this.esExtranjero = region_value == 27;
        this.mapaCalor = participacion.data;
      },
      error: (err) => {
        console.error("Error al cargar datos", err);
      }
    });
    this.obtenerTotalesMesa();
  }

  listarOrganizacionPoliticaOnline(nombreCandidato: string): Observable<FrontendResponse<[Participante]>> {
    let nombreApellidoPartido = nombreCandidato;
    let idEleccion = this.idEleccion;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value;
    let nombreAgrupacion = this.myFormOrganizacion.get("nomAgrupacion").value;
    return this.eleccionCongresalService
      .listarOrganizaciones(nombreApellidoPartido, idEleccion, tipoFiltro, ubigeoNivel1)
      .pipe(
        takeUntil(this.destroy$),
        tap((result) => {
          if (!result.success) {
            this.listOrganizacionPolitica = [];
            this.dataForDistrictOrigin = [];
            return;
          }
          this.listOrganizacionPolitica = result.data;
          this.listOrganizacionPoliticaOrigin = result.data;
          if(nombreAgrupacion && nombreAgrupacion.trim() !== ""){
            const filtro = nombreAgrupacion.trim().toLowerCase();            
            this.listOrganizacionPolitica = mapWithPoliticImage(
              this.listOrganizacionPoliticaOrigin.filter(
                org => org.nombreAgrupacionPolitica && org.nombreAgrupacionPolitica.trim().toLowerCase().includes(filtro)
              )
            );
          }else {            
            this.listOrganizacionPolitica = mapWithPoliticImage([...this.listOrganizacionPoliticaOrigin]);
          }
          this.cargarGraficaBar(this.filtrarAchurados(this.listOrganizacionPolitica));
          this.totalCandidatos = this.listOrganizacionPolitica.length;
          this.calcularTotales();
          this.calcularEscala(this.listOrganizacionPoliticaOrigin);
        })
      );
  }

  filtrarAchurados(data: Participante[]) {
    return data.filter(org =>
      org.codigoAgrupacionPolitica !== "80" &&
      org.codigoAgrupacionPolitica !== "81" &&
      org.totalCandidatos > 0
    );
  }

  private performSearch(searchTerm: string): void {
    const nombreAgrupacion = (searchTerm ?? "").trim() === "" ? null : searchTerm;
    this.listarOrganizacionPolitica(nombreAgrupacion);
  }

  buscarAgrupacion(): void {
    let nombreAgrupacion =
      this.myFormOrganizacion.get("nomAgrupacion").value == ""
        ? null
        : this.myFormOrganizacion.get("nomAgrupacion").value;
    this.listarOrganizacionPolitica(nombreAgrupacion);
  }

  listarOrganizacionPolitica(nombreCandidato: string): void {
    let nombreApellidoPartido = nombreCandidato;

    this.mensaje = "";
    if (nombreApellidoPartido) {
      this.mostrarTotalVotos = false;
      this.listOrganizacionPolitica = mapWithPoliticImage(
        this.listOrganizacionPoliticaOrigin.filter((x) =>
          x.nombreAgrupacionPolitica.toLowerCase().includes(nombreApellidoPartido.toLowerCase())
        )
      );
      if (this.listOrganizacionPolitica.length == 0) {
        this.mensaje =
          "Lo sentimos, no se encontraron resultados que coincidan con su búsqueda. Por favor, verifique los criterios ingresados e inténtelo nuevamente.";
      }
    } else {
      this.mostrarTotalVotos = true;
      this.listOrganizacionPolitica = mapWithPoliticImage(this.listOrganizacionPoliticaOrigin);
    }
    this.totalCandidatos = !nombreApellidoPartido
      ? this.listOrganizacionPolitica.length - 2
      : this.listOrganizacionPolitica.length;
  }


  calcularEscala(lista: Participante[]): void {
    this.escalaTotalVotos = [];
    let valorMax = Math.max(...lista.map((x) => x.totalVotosValidos));
    this.valorMaximo = valorMax;

    if (valorMax == 0) {
      this.escalaTotalVotos.push(0);
      this.escalaTotalVotos.push(70);
      this.escalaTotalVotos.push(140);
      this.escalaTotalVotos.push(220);
      this.escalaTotalVotos.push(300);
    } else {
      let obtenerAgregadoPorcentual = valorMax * 0.05;
      let valorMaxTmp = valorMax + obtenerAgregadoPorcentual;

      let divisor = Math.ceil(valorMaxTmp / 4);

      for (let index = 0; index < 5; index++) {
        const valor = divisor * index;
        this.escalaTotalVotos.push(valor);
      }
    }
  }

  listarParticipacionCiudadana(): Observable<FrontendResponse<[MapaCalor]>> {
    let codigoAgrupacionPolitica = null;
    let idAmbitoGeografico = 0;
    let idUbigeoDepartamento = null;
    let idUbigeoProvincia = null;
    let idUbigeoDistrito = null;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;

    return this.resumenGeneralService
      .listarMapaCalor(
        codigoAgrupacionPolitica,
        idAmbitoGeografico,
        this.idEleccion,
        tipoFiltro,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito
      )
      .pipe(takeUntil(this.destroy$));
  }

  private obtenerTotalesMesa(): void {
    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = 0;
    let ubigeoNivel2 = 0;
    let ubigeoNivel3 = 0;
    let distritoElectoral = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value;

    this.mesaService
      .obtenerTotales(ambitoGeografico, tipoFiltro, ubigeoNivel1, ubigeoNivel2, ubigeoNivel3, distritoElectoral)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.mesaTotales = result.data;
        },
      });
  }

  public calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return "0";
    }

    let agregado = this.valorMaximo * 0.05;
    let votoCalculado = this.valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + "%";
  }

  irSeccionBaja($element: HTMLElement): void {
    let top = $element.getBoundingClientRect().top + window.scrollY - 110;

    window.scrollTo({ top, left: 0, behavior: "smooth" });
  }

  irSeccionArriba($element: HTMLElement): void {
    window.scrollTo({ top: 1, left: 0, behavior: "smooth" });
  }

  calcularTotales(): void {
    this.totalVotosEmitidos = this.listOrganizacionPolitica
      .map((t) => t["porcentajeVotosEmitidos"] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotosValidos = this.listOrganizacionPolitica
      .map((t) => t["porcentajeVotosValidos"] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotos = this.listOrganizacionPolitica
      .map((t) => t["totalVotosValidos"] ?? 0)
      .reduce((acc, value) => acc + value, 0);
  }

  limpiarLista(): void {
    this.myFormOrganizacion.get("nomAgrupacion").setValue(null);    
    this.listOrganizacionPolitica = mapWithPoliticImage(this.listOrganizacionPoliticaOrigin);
    this.totalCandidatos = this.listOrganizacionPolitica.length - 2;
    this.mostrarTotalVotos = true;
    this.mensaje = "";
    this.triggerReset = !this.triggerReset;
  }

  descargarReporte(tipoReporte: number): void {
    const regionValue = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value

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
    const nombreEleccion = "Elección Congresal / Resultado por Ubicación Geográfica";

    this.reporteService
      .descargarPdfEleccionDiputados(
        tipoReporte,
        idAmbitoGeografico,
        idDistritoElectoral,
        idEleccion,
        tipoFiltro,
        nombreProceso,
        nombreEleccion
      )
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          descargarPdf("Reporte_Congresales_Ubicacion_Geografica", result);
          this.snackbarService.showSnackbarWithSuccessMessage();
        },
        error: (err) => {
          this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
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

  seleccionarDistritoElectoralPorId(value): void {
    if (value <= 27) {
      this.filtrar({
        electoralDistrictId: value
      });
    }
  }

  seleccionarDistritoElectoralPorParams(params: any): void {
    if (params?.electoralDistrictId) {
      this.seleccionarDistritoElectoralPorId(params.electoralDistrictId);
    }
  }

  cargarResumen(): void {
    this.behaviorResumenService.setActualizarResumen(this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value);
  }

  public getPercentageOfEmptyAndNullVotes(totalVotes: number): string {
    if (this.maxValueForScaleName.length == 0) {
      return "0%";
    }
    const maxValue = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    const percentage = (totalVotes / maxValue) * 100;
    const currentPercentage = percentage > 100 ? 100 : percentage;
    return (maxValue != 0 ? currentPercentage : 0) + "%";
  }
  
  getPosicionRegla(valor: number, index: number, cantidad = 5): string {
    // Validar que la cantidad sea mayor que 0
    if (cantidad <= 0) {
      throw new Error('La cantidad debe ser mayor que 0');
    }
    // Validar que la posición esté dentro del rango válido
    if (index < 0 || index >= cantidad) {
      throw new Error(`La posición debe estar entre 0 y ${cantidad - 1}`);
    }
    // Calcular el porcentaje
    const percentage = (index / (cantidad - 1)) * 100;

    return `${percentage.toFixed(2)}%`;
  }

  public getReglaValorIteracion(valor: number, index: number): number {
    if(this.maxValueForScaleName.length - 1 == index) {
      return valor + (this.maxValueForScaleName[this.maxValueForScaleName.length - 1] == 300 ? 0 : 1);
    }

    return valor;
  }

  detailVote(value: Participante): void {
    if(this.esPantallaChica) {
      const data: Partial<ModalDetailVotes> = {
        politicalPartyImage: value.urlAgrupacionImage,
        politicalPartyName: value.nombreAgrupacionPolitica,
        candidateImageShow: false,
        candidateNameShow: false,
        votesNumber: value.totalVotosValidos,
        votesEmittedPercentage: value.porcentajeVotosEmitidos,
        votesValidPercentage: value.porcentajeVotosValidos,
      };
      this.openModalDetailVotes(data);
    }
  }

  detailTypeVotes(value: Participante, title: string): void {
    if(!this.esPantallaChica) {
      return;
    }
    const data: Partial<ModalDetailVotes> = {
      politicalPartyShow: false,
      candidateImageShow: false,
      candidateName: title,
      votesNumber: value?.totalVotosValidos ?? null,
      votesEmittedPercentage: value?.porcentajeVotosEmitidos ?? null,
      votesValidPercentage: value?.porcentajeVotosValidos,
    };
    this.openModalDetailVotes(data);
  }

  private openModalDetailVotes(data: Partial<ModalDetailVotes>): void {
    this.modalDetailVotesService.setData(data);
    this.dialog.open(ModalDetailVotesComponent, {
      width: '400px',
      maxWidth: '80vw',
      panelClass: 'popup-votos-detalle',
    });
  }
}
