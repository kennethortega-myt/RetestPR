import { Component, inject, Input, OnInit, ViewChild, HostListener, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject, forkJoin, take, takeUntil, tap } from 'rxjs';
import { IBarInfo } from '../../../../../components/grafica-congresal-candidato/grafica-congresal-candidato.interfaces';
import { TIPO_FILTRO, ID_AMBITO_GEOGRAFICO, MENSAJE_REPORTE, CANTIDAD_LIMITE_CANDIDATOS } from '../../../../../helpers/constantes';
import { descargarPdf } from '../../../../../helpers/funciones';
import { makeScaleValues } from '../../../../../helpers/handler-chart-data.common';
import { Mesa } from '../../../../../interfaces/acta-bean';
import { ParticipantePorCandidato } from '../../../../../interfaces/eleccion-congresal-bean';
import { Region } from '../../../../../interfaces/elections.interfaces';
import { IDescargarPdfCommonParams } from '../../../../../interfaces/reporte.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { MapaCalor } from '../../../../../interfaces/resumen-general-bean';
import { encryptStorageEleccion } from '../../../../../settings/encrypt-storage.settings';
import { DistritoElectoral } from '../../../../../interfaces/ubigeo-bean';
import { BehaviorResumenService } from '../../../../../services/elecciones-generales/behavior-resumen.service';
import { EleccionCongresalService } from '../../../../../services/elecciones-generales/eleccion-congresal.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { ReporteService } from '../../../../../services/elecciones-generales/reporte.service';
import { ResumenGeneralService } from '../../../../../services/elecciones-generales/resumen-general.service';
import { SnackbarService } from '../../../../../services/elecciones-generales/snackbar.service';
import { UbigeoService } from '../../../../../services/elecciones-generales/ubigeo.service';
import { MesaService } from '../../../../../services/elecciones-generales/mesa.service';
import { FrontendResponse } from '../../../../../interfaces/response.common';
import { GenericFilterParams } from '../../../../../interfaces/filtro-settings';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { NUMBER_OF_BAR_IN_GRAFIC } from '../../../../../settings/responsive.settings';
import { BreakpointObserver } from '@angular/cdk/layout';
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGrafic } from '../../../../../helpers/responsive-dimentions.helper';

@Component({
  selector: 'app-resultado-por-candidato',
  templateUrl: './resultado-por-candidato.component.html',
  styleUrls: ['./resultado-por-candidato.component.scss'],
  standalone: false
})
export class ResultadoPorCandidatoComponent implements OnInit, OnDestroy {
  private readonly formBuilder = inject(FormBuilder);
  @Input() idEleccion = 0;
  @Input() resumen: Resumen;
  @ViewChild(GenericFilterUbigeoComponent) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  randomImageUrl: string;
  listRegion: DistritoElectoral[] = [];
  candidatosGrafica: ParticipantePorCandidato[];
  filterButtonIsDisabled: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  primeraVez: boolean = true;
  myFormCandidato: FormGroup = this.formBuilder.group({
    nomCandidato: [null, Validators.required]
  });
  myFormUbigeo: FormGroup = this.formBuilder.group({
    region: ['0', Validators.required]
  });
  esExtranjero: boolean = false;
  listaCandidatos: ParticipantePorCandidato[];
  listaCandidatosGrafica: ParticipantePorCandidato[];
  listaCandidatosGraficaOriginal: ParticipantePorCandidato[];
  escalaTotalVotos: number[];
  valorMaximo: number = 0;
  valorMaximoParticipante: number = 0;
  dataForDistrict: IBarInfo[] = [];
  scalesForDistrict: number[] = [];
  numberCandidatesForDistrict = NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;
  mapaCalor: MapaCalor[];
  regionSeleccionada: Region;
  mesaTotales: Mesa;
  datosCandidato: ParticipantePorCandidato[];
  paginaActual: number = 0;
  totalPagina: number = 0;
  deshabilitarBotonGenerarReporte: boolean = false;
  valorFiltroDistritoElectoral: number = null;
  labelFiltroDistritoElectoral: string = null;
  cantidadBarra = NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;
  valorFiltroDistritoElectoralButtonReporte: number = null;
  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;
  heightGrafica: number;
  private resizeObserver: ResizeObserver;
  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  constructor(
    public dialog: MatDialog,
    private readonly eleccionCongresalService: EleccionCongresalService,
    private readonly ubigeoService: UbigeoService,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly mesaService: MesaService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService,
    private readonly randomImageService: RandomImageService,
    private readonly breakpointObserver: BreakpointObserver
  ) {
    this.breakpointObserver
      .observe(BAR_GRAFIC_BREAKPOINTS)
      .pipe(takeUntil(this.destroy$))
      .subscribe((result) => {
        this.numberCandidatesForDistrict = getNumberOfBarsForGrafic(result.breakpoints);
        this.cantidadBarra = this.numberCandidatesForDistrict;
      });
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

  ngOnInit(): void {
    this.inicializarPeticiones();
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

  get cantidadLimiteCandidatos(){
    return CANTIDAD_LIMITE_CANDIDATOS.DIPUTADOS;
  }

  toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  onRegionChanged($event: Event): void {
    this.filterButtonIsDisabled = false;
    this.dataForDistrict = [];
    this.deshabilitarBotonGenerarReporte = true;
    this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
  }

  listarRegion(): void {
    this.ubigeoService
      .listarDistritoElectorales()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.listRegion = result.data;
          if (this.primeraVez) {
            this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(
              this.resumen.idUbigeoDistritoElectoral,
              {
                emitEvent: false
              }
            );
            this.primeraVez = false;
            this.filtrar();
          }
        }
      });
  }

  ngOnDestroy() {
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
      this.resizeObserver = null;
    }
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }

  inicializarPeticiones() {
    let idEleccion = this.idEleccion;
    let nombreApellidoPartido = this.myFormCandidato.get('nomCandidato').value;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.resumen.idUbigeoDistritoElectoral;

    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let pagina: number = this.paginaActual;
    let tamanio: number = 10;
    this.valorFiltroDistritoElectoral = this.resumen.idUbigeoDistritoElectoral;

    // Loading functionality removed

    const serviciosCombinados = forkJoin({
      regiones: this.ubigeoService.listarDistritoElectorales().pipe(takeUntil(this.destroy$)),
      listaParticipantes: this.eleccionCongresalService
        .listarParticipantesPorCandidato(idEleccion, nombreApellidoPartido, tipoFiltro, ubigeoNivel1, pagina, tamanio)
        .pipe(takeUntil(this.destroy$)),

      mapaCalor: this.resumenGeneralService
        .listarMapaCalor('0', ambitoGeografico, this.idEleccion, TIPO_FILTRO.DISTRITO_ELECTORAL, 0, 0, 0)
        .pipe(takeUntil(this.destroy$))
    });

    serviciosCombinados.pipe(takeUntil(this.destroy$)).subscribe({
      next: (result) => {
        this.listRegion = result.regiones.data;
        this.datosCandidato = result.listaParticipantes.data;
        if (result.listaParticipantes.success) {
          this.listaCandidatosGraficaOriginal = this.separarPorGrupo(
            result.listaParticipantes.data,
            this.cantidadBarra
          );
          this.cargarDataGrafica(this.listaCandidatosGraficaOriginal);
        } else {
          this.deshabilitarBotonGenerarReporte = true;
        }

        this.mapaCalor = result.mapaCalor.data;

        if (this.primeraVez) {
          this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(
            this.resumen.idUbigeoDistritoElectoral
          );
          setTimeout(() => {
            this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
          }, 0);
          this.primeraVez = false;
        }
        // Loading functionality removed
      },
      error: (err) => {/* Loading functionality removed */}
    });
  }

  private separarPorGrupo(lista: ParticipantePorCandidato[], tamanioGrupo: number): ParticipantePorCandidato[] {
    let listaResult: ParticipantePorCandidato[] = [];
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

  private cargarDataGrafica(data: ParticipantePorCandidato[]): void {
    this.listaCandidatosGrafica = data || [];

    const valorMax =
      this.listaCandidatosGrafica.length > 0
        ? Math.max(...this.listaCandidatosGrafica.map((x) => x.totalVotosValidos))
        : 0;

    this.valorMaximoParticipante = valorMax;

    this.dataForDistrict = this.listaCandidatosGrafica.map((x) => ({
      percentage_of_valid_votes: valorMax === 0 ? 0 : (x.totalVotosValidos / valorMax) * 100,
      number_of_valid_votes: x.totalVotosValidos,
      url_candidate_image: '',
      urlAgrupacionImage: '',
      name_of_candidate: x.nombreCandidato,
      name_of_politic_group: x.nombreAgrupacionPolitica,
      number_of_list: x.lista,
      code_of_politic_group: x.codigoAgrupacionPolitica,
      number_of_candidate: x.dniCandidato,
      group: x.grupo
    }));

    this.scalesForDistrict = makeScaleValues(this.listaCandidatosGrafica, this.numberCandidatesForDistrict);
  }

  filtrar(params?: GenericFilterParams): void {
    this.valorFiltroDistritoElectoral = params?.electoralDistrictId ?? null;
    this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();

    if (this.valorFiltroDistritoElectoral == 0) {
      return;
    }
    // Loading functionality removed

    this.paginaActual = 0;
    this.behaviorResumenService.setActualizarResumen(this.valorFiltroDistritoElectoral);

    const participacion$ = this.listarParticipacionCiudadana();
    const candidatos$ = this.listarCandidatosGrafica();
    forkJoin([participacion$, candidatos$]).subscribe({
      next: ([participacion, candidatos]) => {
        this.esExtranjero = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value == 27;
        this.mapaCalor = participacion.data;
      },
      error: (err) => {
        console.error('Error al cargar datos', err);
      },
      complete: () => {
        setTimeout(() => {
          // Loading functionality removed
        }, 1000);
      }
    });
  }

  desabilitarUbigeo() {
    this.paginaActual = 0;
    this.totalPagina = 0;
    this.scalesForDistrict = [];
    this.dataForDistrict = [];
    this.listaCandidatosGraficaOriginal = [];
    this.listaCandidatosGrafica = [];
    this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(
      this.resumen.idUbigeoDistritoElectoral
    );

    this.behaviorResumenService.setActualizarResumen(
      this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value
    );
    this.listarCandidatosGrafica();
    this.obtenerTotalesMesa();
  }

  buscar(): void {
    this.listarCandidatos();
  }
  listarCandidatos(): void {
    let idEleccion = this.idEleccion;
    let nombreApellidoPartido = this.myFormCandidato.get('nomCandidato').value;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value;

    let pagina: number = 0;
    let tamanio: number = 10;
    this.eleccionCongresalService
      .listarParticipantesPorCandidato(idEleccion, nombreApellidoPartido, tipoFiltro, ubigeoNivel1, pagina, tamanio)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.listaCandidatos = result.data;
          this.calcularEscala(this.listaCandidatos);
        }
      });
  }

  listarCandidatosGraficaLocal(): void {
    let listaTmp = this.listaCandidatosGraficaOriginal.filter((x) => x.grupo == this.paginaActual);
    this.cargarDataGrafica(listaTmp);
  }

  listarCandidatosGrafica(): Observable<any> {
    let pagina: number = this.paginaActual;
    let tamanio: number = 10;

    let idEleccion = this.idEleccion;
    let nombreApellidoPartido = '';
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = this.valorFiltroDistritoElectoral;
    this.listaCandidatosGraficaOriginal = [];
    return this.eleccionCongresalService
      .listarParticipantesPorCandidato(idEleccion, nombreApellidoPartido, tipoFiltro, ubigeoNivel1, pagina, tamanio)
      .pipe(
        takeUntil(this.destroy$),
        tap((result) => {
          if (result.success) {
            this.listaCandidatosGraficaOriginal = this.separarPorGrupo(result.data, this.cantidadBarra);
            this.deshabilitarBotonGenerarReporte = false;
          } else {
            this.deshabilitarBotonGenerarReporte = true;
          }
          this.cargarDataGrafica(this.listaCandidatosGraficaOriginal);
        })
      );
  }

  calcularEscala(lista: ParticipantePorCandidato[]): void {
    this.escalaTotalVotos = [];

    const valorMax = lista?.length ? Math.max(...lista.map((x) => x.totalVotosValidos)) : 0;
    this.valorMaximo = valorMax;

    if (valorMax === 0) {
      this.escalaTotalVotos = [0, 500, 1000, 1500, 2000];
    } else {
      const valorMaxTmp = valorMax + 500;
      const divisor = Math.ceil(valorMaxTmp / 4);

      this.escalaTotalVotos = Array.from({ length: 5 }, (_, index) => divisor * index);
    }
  }

  listarParticipacionCiudadana(): Observable<FrontendResponse<[MapaCalor]>> {
    let codigoAgrupacionPolitica = null;
    let idAmbitoGeografico = 0;
    let idUbigeoDepartamento = null;
    let idUbigeoProvincia = null;
    let idUbigeoDistrito = null;

    return this.resumenGeneralService
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
  }

  private obtenerTotalesMesa(): void {
    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let tipoFiltro = 'distrito_electoral';
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
        }
      });
  }

  public calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }

    let agregado = this.valorMaximo * 0.05;
    let votoCalculado = this.valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }
  irSeccionBaja(): void {
    window.scrollTo({ top: 850, left: 0, behavior: 'smooth' });
  }
  irSeccionArriba(): void {
    window.scrollTo({ top: 1, left: 0, behavior: 'smooth' });
  }

  siguiente(value: number): void {
    this.paginaActual = value;
    this.listarCandidatosGraficaLocal();
  }

  anterior(value: number): void {
    this.paginaActual = value;
    this.listarCandidatosGraficaLocal();
  }

  descargarReporte(tipoReporte: number): void {
    const regionValue = this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value;

    if (regionValue === 0) {
      return;
    }

    const shouldResetFiltro = this.valorFiltroDistritoElectoralButtonReporte !== regionValue;
    const isFirstPdfClick = tipoReporte === 1 && !this.buttonReportePdfclick;
    const isFirstCsvClick = tipoReporte === 2 && !this.buttonReporteCsvclick;
    const shouldUpdateFiltro =
      !this.valorFiltroDistritoElectoralButtonReporte || shouldResetFiltro || isFirstPdfClick || isFirstCsvClick;

    if (shouldUpdateFiltro) {
      this.valorFiltroDistritoElectoralButtonReporte = regionValue;
    } else {
      this.snackbarService.showSnackbarWithSuccessMessage('La descarga es una vez por ubicación.', 'ambar');
      return;
    }

    if (tipoReporte === 1) this.buttonReportePdfclick = true;
    if (tipoReporte === 2) this.buttonReporteCsvclick = true;

    const idAmbitoGeografico = regionValue > 26 ? 2 : 1;
    const idDistritoElectoral = regionValue;
    const idEleccion = this.idEleccion;

    const objProceso = JSON.parse(encryptStorageEleccion.getItem('PROCESO_ELECTORAL_ACTIVO'));
    const tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    const nombreProceso = objProceso.nombre;
    const nombreEleccion = 'Elección Congresal / Resultado por Ubicación Geográfica';

    // Loading functionality removed

    const params: IDescargarPdfCommonParams = {
      tipoReporte,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      nombreProceso,
      nombreEleccion,
      tipoFiltro
    };

    this.reporteService
      .descargarPdfEleccionDiputadosCandidato(params)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          // Loading functionality removed
          descargarPdf('Reporte_Congresales_Candidato', result);
          this.snackbarService.showSnackbarWithSuccessMessage();
        },
        error: (err) => {
          // Loading functionality removed
          this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, 'ambar');
        }
      });
  }

  seleccionarDistritoElectoralPorId(value: number): void {
    this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.setValue(Number(value), { emitEvent: false });
    this.filtrar();
  }

  seleccionarDistritoElectoralPorParams(params: any): void {
    if (params?.electoralDistrictId) {
      this.seleccionarDistritoElectoralPorId(params.electoralDistrictId);
    }
  }

  cargarResumen(): void {
    this.behaviorResumenService.setActualizarResumen(
      this.mainFiltroUbigeoComponent.electoralDistrictForm.controls.region.value
    );
  }
}
