import { CommonModule, DatePipe } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
  HostListener,
} from '@angular/core';
import { FormControl, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Subject, take, forkJoin, of, takeUntil } from 'rxjs';

import { ChartMapaComponent } from '../../../components/chart-mapa/chart-mapa.component';
import { FiltroBehaviorService } from '../../../components/filtro/filtro.behavior.service';
import {
  Archivo,
  ModalVisorPdfComponent,
} from '../../../components/modal-visor-pdf/modal-visor-pdf.component';
import { ResumenEtiquetaComponent } from '../../../components/resumen-etiqueta/resumen-etiqueta.component';
import { ResumenGraficaModeloDosComponent } from '../../../components/resumen-grafica-modelo-dos/resumen-grafica-modelo-dos.component';
import {
  TAMANIO_PAGINA_ACTAS_OBS,
  REPORTE_OBSERVADAS,
  ACTAS_OPTIONS_V,
  MAIN_OPTIONS_ACTAS_FILTER,
  TIME_BETWWEN_MODULES,
  TAMANIO_PAGINA_ACTAS_OBS_MOVIL,
  CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO,
  AMBITO,
} from '../../../helpers/constantes';
import {
  EnumIdEleccion,
  EnumCodigoEstadoActa,
  EnumTipoFiltro,
  TYPE_FOR_PDF,
} from '../../../helpers/enums';
import { getGenericFilterType } from '../../../helpers/filter-type.common';
import { formatNameElection, mapearCamposResumenTotalesObservadas } from '../../../helpers/funciones';
import { getIconImageForElectionId } from '../../../helpers/icon-image-for-election-id';
import { getOptimizedObject } from '../../../helpers/object.utils';
import { FiltroModel } from '../../../interfaces/filtro.model';
import { ActaObservadaInput } from '../../../interfaces/input/acta/acta-observada-input';
import { ObtenerMapaCalorResumenGeneralInput } from '../../../interfaces/input/resumen-general/obtener-mapa-calor-resumen-general-input';
import { ObtenerTotalesResumenGeneralObservadasInput } from '../../../interfaces/input/resumen-general/obtener-totales-resumen-general-observadas-input';
import { ContentActaObservada } from '../../../interfaces/output/acta-observada/acta-observada.model';
import { Acta, Detalle } from '../../../interfaces/output/acta-observada/detalle-acta-observada.model';
import { LineaTiempo } from '../../../interfaces/output/acta-observada/linea-tiempo.model';
import { TablaEscrutinioModeloUno } from '../../../components/tabla-escrutinio-modelo-uno/tabla-escrutinio-modelo-uno.model';
import { TablaEscrutinioModeloDos } from '../../../components/tabla-escrutinio-modelo-dos/tabla-escrutinio-modelo-dos.model';
import { ResumenTotal } from '../../../interfaces/output/resumen-total.model';
import { ActaApiService } from '../../../services/acta-api.service';
import {
  GeneratorFileReporterService,
  IGenerateReportParams,
} from '../../../services/generator-file-reporter.service';
import { ResumenGeneralApiService } from '../../../services/resumen-general-api.service';
import { ModalDescargaComponent } from '../tabla/resultado/modal-descarga/modal-descarga.component';
import {
  IDownloadStatusType,
  IModalDescargaData,
  MODAL_MESSGAGE,
} from '../tabla/resultado/modal-descarga/modal-message';
import { ComponentsModule } from '../../../components/components.module';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { IReportListTableInformation } from '../../../interfaces/report-list.interfaces';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LoadingService } from '../../../components/loading/loading.service';
import { PopupCausalesObservacionComponent } from '../../../components/popup-causales-observacion/popup-causales-observacion.component';
import { DateAdapter, MatOptionSelectionChange } from '@angular/material/core';
import { CustomDateAdapter } from '../../../helpers/datetime-helper.common';
import { DialogService } from '../../../services/dialog.service';
import { ActaOptionFilter } from '../../../interfaces/input/acta/acta-filter.interface';
import { OnpeDatePipe } from '../../../pipes/onpe-date.pipe';

@Component({
  selector: 'app-actas-observadas',
  templateUrl: './actas-observadas.component.html',
  providers: [
    { provide: DatePipe },
    { provide: DateAdapter, useClass: CustomDateAdapter },
  ],
  imports: [
    ComponentsModule,
    CommonModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatPaginatorModule,
    MatSlideToggleModule,
    TranslateModule,
    OnpeDatePipe
  ],
})

export class ActasObservadasComponent implements OnDestroy, OnInit {
  public actasOptionsV = ACTAS_OPTIONS_V;
  public mainOptionsActasV = MAIN_OPTIONS_ACTAS_FILTER;

  selectedTextsSubStatusFilter: string[] = [];
  private isModalOpen: boolean = false;
  private resizeDebounceTimer: ReturnType<typeof setTimeout> | null = null;

  openPopup2() {
    const dialogRef = this.dialog.open(PopupCausalesObservacionComponent, {
      width: "1100px",
      maxHeight: "100%",
      panelClass: "popup-centrado2",
    });
    dialogRef.afterClosed().subscribe((result) => { });
  }

  select1: string = '0';
  select2: string = '0';
  select3: number = 0;
  select4: string = '0';
  select5: string[] = [];
  subFiltros: string = "";
  resumenTotal?: ResumenTotal;
  mensaje: string = 'constantes.MENSAJE_ORIGINAL';
  mensajeListaActasObservada: string = '';
  verDetalleActa: boolean = false;
  actasObservadas: ContentActaObservada[] = [];
  idTipoEleccion?: number;
  paginaActual: number = 0;
  public mapaCalor: boolean = false;

  @ViewChild('resumenEtiquetaComponent')
  resumenEtiquetaComponent!: ResumenEtiquetaComponent;
  @ViewChild(ResumenGraficaModeloDosComponent)
  resumenGraficaModeloDosComponent?: ResumenGraficaModeloDosComponent;
  @ViewChild(ChartMapaComponent) chartMapaComponent?: ChartMapaComponent;

  EnumIdEleccion = EnumIdEleccion;
  EnumCodigoEstadoActa = EnumCodigoEstadoActa;
  private readonly destroy$ = new Subject<void>();

  lineasTiempo?: [LineaTiempo];
  estadoDescripcionActaResolucion?: string;
  acta?: Acta;
  contentActaObservada?: ContentActaObservada;
  listaTablaEscrutinioModeloUno: TablaEscrutinioModeloUno[] = [];
  listaTablaEscrutinioModeloDos: TablaEscrutinioModeloDos[] = [];
  archivos: Archivo[] = [];
  idAmbito: number = 1;

  private readonly formBuilder = inject(FormBuilder);
  private readonly dialogService = inject(DialogService);

  isContentReady: boolean = false;
  haHabidoResultadosPrevios: boolean = false;

  form = this.formBuilder.group({
    estadoActa: [{ value: 0, disabled: false }],
    filtroActas: [{ value: ['motivo_envio_JEE'], disabled: false }]
  });
  filtroModelActual?: FiltroModel;
  orderBy: string = 'Posicion';
  ctrlToggle = new FormControl(true);

  detActaEleccion: string = '';
  private readonly customDateAdapter = inject(DateAdapter);

  public tableInformation: IReportListTableInformation = {
    dataSourceTotals: [],
    pageSize: TAMANIO_PAGINA_ACTAS_OBS,
    totalPages: 0,
    totalRegisters: 0,
    currentPage: 0,
  } as IReportListTableInformation;

  public detalleActa: Acta = {} as Acta;
  private readonly cd = inject(ChangeDetectorRef);

  mostrarDatosActa = false;
  esPantallaChica = this.checkPantallaChica();

  // Nuevas propiedades para el mapa colapsible
  public mostrarMapa: boolean = false;

  public detalleActaText = 'ActasEnviadasJEE.DetalleActa';
  public electoresHabiles = 'ActasEnviadasJEE.ElectoresHabiles';
  public totalVotantes = 'ActasEnviadasJEE.TotalVotantes';
  public participacionCiudadana = 'ActasEnviadasJEE.ParticipacionCiudadana';
  public estadoActa = 'ActasEnviadasJEE.EstadoActa';
  public ambito = 'ActasEnviadasJEE.Ambito';

  codigoSolucion = CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO;
  nombreAmbito: string = '';

  public centroPoblado = 'ActasEnviadasJEE.CentroPoblado';
  public solucionTecnologica = 'ActasEnviadasJEE.SolucionTecnologica';
  dateUpdate?: string;
  pageSizeOptions: number[] = [];

  constructor(
    public dialog: MatDialog,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly actaApiService: ActaApiService,
    private readonly filtroBehaviorService: FiltroBehaviorService,
    private readonly generatorFileReporterService: GeneratorFileReporterService,
    private readonly translate: TranslateService,
    private readonly loadingService: LoadingService
  ) {
    this.cargarMensajeInicial();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: UIEvent): void {
    const isSmall = (event.target as Window).innerWidth < 960;
    this.esPantallaChica = isSmall;
    this.mostrarDatosActa = !isSmall;
    this.mostrarMapa = !isSmall;

    if (this.isModalOpen) {
      this.tableInformation.pageSize = this.tamanioPaginaActas;
      this.pageSizeOptions = [this.tamanioPaginaActas];
      this.cd.detectChanges();
      return;
    }

    if (this.resizeDebounceTimer !== null) {
      clearTimeout(this.resizeDebounceTimer);
    }
    this.resizeDebounceTimer = setTimeout(() => {
      this.resizeDebounceTimer = null;
      this.updatePageSizeOptions();
    }, 250);
  }

  toggleDatosActa() {
    if (this.esPantallaChica) {
      this.mostrarDatosActa = !this.mostrarDatosActa;
    }
  }

  private checkPantallaChica(): boolean {
    const isSmall = window.innerWidth < 960;
    // Inicializar estado del mapa basado en el tamaño de pantalla
    this.mostrarMapa = !isSmall;
    return isSmall;
  }

  // Nuevo método para toggle del mapa
  public toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa = !this.mostrarMapa;
    }
  }

  ngOnInit(): void {
    this.loadingService.show();
    this.setDefaultMotivoEnvioJEE();
    this.updatePageSizeOptions();

    setTimeout(() => {
      this.valuesChangeEstadoActa();
      this.valuesChangeFiltroActas();
      this.valuesChangeOrdenTabla();
      this.loadingService.hide();
      this.isContentReady = true;
    }, TIME_BETWWEN_MODULES);
  }

  public generarReporte() {
    if (!this.filtroModelActual) {
      return;
    }
    const {
      idAmbitoGeografico,
      idTipoEleccion,
      idUbigeoNivel01,
      idUbigeoNivel02,
      idUbigeoNivel03,
      idLocalVotacion,
      nombreUbigeoNivel01,
      nombreUbigeoNivel02,
      nombreUbigeoNivel03,
    } = this.filtroModelActual;

    const params = {
      idEleccion: idTipoEleccion,
      idAmbitoGeografico: idAmbitoGeografico,
      tipoFiltro: this.getFilterType(),
      ubigeoNivel01: idUbigeoNivel01,
      ubigeoNivel02: idUbigeoNivel02,
      idUbigeo: idUbigeoNivel03,
      codigoLocalVotacion: idLocalVotacion,
      descripcionUbigeoNivel1: nombreUbigeoNivel01,
      descripcionUbigeoNivel2: nombreUbigeoNivel02,
      descripcionUbigeoNivel3: nombreUbigeoNivel03,
      tipoReporte: REPORTE_OBSERVADAS,
      codigoUsuario: 'usuario',
      descripcionUsuario: 'EN MI PECHO LLEVO TUS COLORES',
    } as IGenerateReportParams;
    const currentParams = getOptimizedObject(params);

    this.generatorFileReporterService
      .createReporterObservadasFile$(currentParams)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.openDialog2('init_download');
        } else {
          this.openDialog2('in_progress');
        }
      });
  }

  private getFilterType() {
    if (!this.filtroModelActual) {
      return;
    }
    const {
      idAmbitoGeografico,
      idUbigeoNivel01,
      idUbigeoNivel02,
      idUbigeoNivel03,
      idLocalVotacion,
    } = this.filtroModelActual;
    return getGenericFilterType({
      ambito: idAmbitoGeografico,
      ubigeo01: idUbigeoNivel01,
      ubigeo02: idUbigeoNivel02,
      ubigeo03: idUbigeoNivel03,
      local: idLocalVotacion,
    });
  }

  public getIconImageForElectionId(electionId?: number) {
    return getIconImageForElectionId(electionId);
  }

  openDialog2(status: IDownloadStatusType): void {
    this.dialog.open<any, IModalDescargaData>(ModalDescargaComponent, {
      panelClass: 'modal-descarga',
      data: { message: MODAL_MESSGAGE[status] },
    });
  }

  valuesChangeEstadoActa(): void {
    this.form.get('estadoActa')?.valueChanges.subscribe({
      next: (value) => {
        if (value === 0) {
          this.filtroModelActual!.resueltas = null;
        } else {
          this.filtroModelActual!.resueltas = value === 1;
        }
        this.listarActasObservadasPorEstadoActado(this.filtroModelActual!);
      }
    });
  }

  getSelectedTextsSubStatusFilter(): void {
    const selectedValues = this.form.get('filtroActas')?.value ?? [];

    const allOptions = this.actasOptionsV.flatMap(opt => [
      { value: opt.value, text: opt.text },
      ...(opt.children ?? [])
    ]);

    this.selectedTextsSubStatusFilter = allOptions.filter(opt =>
      selectedValues.includes(opt.value!)).map(opt => this.translate.instant(opt.text));
  }

  valuesChangeFiltroActas(): void {
    this.form.get('filtroActas')?.valueChanges.subscribe({
      next: (values: string[] | null) => {
        if (this.filtroModelActual && values) {
          this.subFiltros = this.getTotalStringFilters(values);
          this.listarActasObservadasPorEstadoActado(this.filtroModelActual);
        }

        this.getSelectedTextsSubStatusFilter();
      }
    });
  }

  onParentSelectionChange(event: MatOptionSelectionChange, parentOption: ActaOptionFilter): void {
    if (!event.isUserInput) return;

    const control = this.form.get('filtroActas');
    const current: string[] = control?.value ? [...control.value] : [];
    const childrenVals = (parentOption.children ?? []).map((c: ActaOptionFilter) => c.value);

    if (event.source.selected) {
      const merged = Array.from(new Set([...current, parentOption.value, ...childrenVals]));
      control?.setValue(merged as string[]);
    } else {
      const filtered = current.filter(v => v !== parentOption.value && !childrenVals.includes(v));
      control?.setValue(filtered);
    }
  }

  removeSelectedItem(text?: string): void {
    const control = this.form.get('filtroActas');
    const selectedValues: string[] = control?.value ?? [];

    const allOptions = this.actasOptionsV.flatMap(opt => [
      { value: opt.value, text: opt.text, isParent: true, children: opt.children },
      ...(opt.children ?? []).map(child => ({ value: child.value, text: child.text, isParent: false }))
    ]);

    if (text === undefined) {
      control?.setValue([], { emitEvent: false });
    } else {
      const itemToRemove = allOptions.find(opt =>  this.translate.instant(opt.text) === text);

      if (itemToRemove) {
        let valuesToFilterOut: string[] = [itemToRemove.value!];

        if (itemToRemove.isParent) {
          const parentOption = this.actasOptionsV.find(opt => opt.value === itemToRemove.value);
          if (parentOption?.children) {
            const childrenVals = parentOption.children.map(c => c.value);
            valuesToFilterOut = [...valuesToFilterOut, ...childrenVals] as string[];
          }
        }

        const updated = selectedValues.filter((v: any) => !valuesToFilterOut.includes(v));
        control?.setValue(updated);
      }
    }
  }

  valuesChangeOrdenTabla() {
    this.ctrlToggle.valueChanges.subscribe({
      next: (value) => {
        if (value) {
          this.orderBy = 'Votos';
        } else {
          this.orderBy = 'Posicion';
        }
        this.toggleOrderVotosPosicion2(this.orderBy);
      },
    });
  }

  toggleOrderVotosPosicion2(orden: string): void {
    const compareVotos = (a: any, b: any) => b.totalVotos - a.totalVotos;
    const comparePosicion = (a: any, b: any) => a.posicion - b.posicion;
    const compareFn = orden === 'Votos' ? compareVotos : comparePosicion;

    if (this.contentActaObservada?.idEleccion === EnumIdEleccion.ID_ELECCION_PRESIDENCIAL) {
      this.listaTablaEscrutinioModeloUno = [...(this.listaTablaEscrutinioModeloUno ?? [])].sort(compareFn);
    } else {
      this.listaTablaEscrutinioModeloDos = [...(this.listaTablaEscrutinioModeloDos ?? [])].sort(compareFn);
    }

    this.orderBy = this.orderBy === 'Posicion' ? 'Votos' : 'Posicion';
  }

  protected verActa(data?: ContentActaObservada) {
    this.openModal({
      multiple: true,
      archivos: data?.archivosActa,
      esPantallaChica: window.innerWidth < 960,
    });
  }

  public verResolucion(data?: ContentActaObservada) {
    this.openModal({
      multiple: true,
      archivos: data?.archivosResolucion,
      esPantallaChica: window.innerWidth < 960,
    });
  }

  private openModal(modalConfig: any) {
    this.isModalOpen = true;

    this.cd.detectChanges();
    this.dialogService.openComponent(
      ModalVisorPdfComponent,
      {
        width: '1024px',
        minWidth: '23%',
        maxHeight: '95vh',
        panelClass: 'popup-acta',
        disableClose: false,
        maxWidth: '100%',
        data: {
          ...modalConfig,
          numeroDeActa: this.detalleActa.codigoMesa,
          nombreDeActa: this.detActaEleccion
        }
      }
    )
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.isModalOpen = false;
          this.cd.detectChanges();
        },
        error: () => {
          this.isModalOpen = false;
          this.cd.detectChanges();
        }
      });
  }

  private setDefaultMotivoEnvioJEE() {
    if (this.filtroModelActual) {
      (this.filtroModelActual as any).motivo_envio_JEE = true;
    }
  }

  descargarPdf(archivo: Archivo) {
    this.actaApiService
      .descargarPdf(archivo.id ?? 0)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.dialog.open(ModalVisorPdfComponent, {
            width: '1024px',
            height: '715px',
            disableClose: false,
            data: {
              file: result,
            },
          });
        },
      });
  }

  filtrar(filtroModel: FiltroModel) {
    this.idTipoEleccion = filtroModel.idTipoEleccion;
    this.verDetalleActa = false;
    this.filtroModelActual = filtroModel;
    if (filtroModel.idTipoEleccion === 0) {
      this.mensajeListaActasObservada = '';
    } else {
      this.ejecutarConsultaConjunta(filtroModel);
    }
  }

  ejecutarConsultaConjunta(filtroModel: FiltroModel) {
    forkJoin([
      of(this.resumenEtiquetaComponent?.obtenerDatosFiltro(filtroModel)),
      of(this.obtenerResumenTotalesObservadas(filtroModel)),
      of(this.listarActasObservadas(filtroModel)),
      of(this.obtenerMapaCalorObservadas(filtroModel)),
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe({ next: (result) => { } });
  }

  obtenerResumenTotalesObservadas(data: FiltroModel) {
    const param: ObtenerTotalesResumenGeneralObservadasInput = mapearCamposResumenTotalesObservadas(data);

    this.resumenGeneralApiService
      .obtenerResumenTotalesObservadas(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.resumenTotal = result.data;
            this.dateUpdate = result.data?.fechaActualizacion;
            this.resumenGraficaModeloDosComponent?.cargarDatos(
              this.resumenTotal ?? {} as ResumenTotal
            );
            this.resumenEtiquetaComponent?.obtenerDatosFiltro(
              data,
              this.resumenTotal
            );
          }
        },
      });
  }

  get rangeLabel(): string {
    if (!this.tableInformation) return '';
    const { currentPage, pageSize, totalRegisters } = this.tableInformation;

    const startIndex = currentPage * pageSize;
    const endIndex = Math.min(startIndex + pageSize, totalRegisters);

    return `Mostrando ${startIndex + 1} – ${endIndex} de ${totalRegisters} registros`;
  }

  public handlePageEvent(pageEvent: PageEvent) {
    this.paginaActual = pageEvent.pageIndex;
    if (this.filtroModelActual) {
      this.cargarActasObservadas(this.filtroModelActual, false);
    }
  }

  private cargarActasObservadas(
    filtroModel: FiltroModel,
    mostrarMensajeSiVacio: boolean = true,
    mensajeMostrar: string = 'No se encontraron registros.'
  ) {
    const data: ActaObservadaInput = this.mapearRequestListaActaObservadas(filtroModel);
    this.verDetalleActa = false;

    this.resumenEtiquetaComponent?.obtenerDatosFiltro(filtroModel);
    this.actaApiService
      .listarActasObservadasPaginada(data, this.paginaActual, this.tamanioPaginaActas)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.mapaCalor = true;
          if (!result.success) return;

          const contenido = result.data?.content ?? [];
          const hayRegistros = contenido.length > 0;

          if (hayRegistros) {
            this.actasObservadas = contenido;
            this.mensajeListaActasObservada = '';
            this.haHabidoResultadosPrevios = true;
          } else {
            this.actasObservadas = contenido;
            if (mostrarMensajeSiVacio) {
              this.mensajeListaActasObservada = mensajeMostrar;
            }
          }

          this.tableInformation = {
            dataSourceTotals: [],
            totalPages: result.data?.totalPaginas ?? 0,
            totalRegisters: result.data?.totalRegistros ?? 0,
            currentPage: result.data?.paginaActual ?? 0,
            pageSize: this.tamanioPaginaActas
          };
        },
      });
  }

  listarActasObservadas(filtroModel: FiltroModel) {
    const mensaje = (this.customDateAdapter as CustomDateAdapter).generarMensajeActasObservadas();
    this.cargarActasObservadas(filtroModel, true, mensaje);
  }

  listarActasObservadasPorEstadoActado(filtroModel: FiltroModel) {
    const mensaje = (this.customDateAdapter as CustomDateAdapter).generarMensajeActasObservadas();
    this.cargarActasObservadas(filtroModel, true, mensaje);
  }

  mapearRequestListaActaObservadas(
    filtroModel: FiltroModel
  ): ActaObservadaInput {

    const data = new ActaObservadaInput();

    data.idEleccion = filtroModel.idTipoEleccion;

    data.descripcionActaResolucion =
      (!this.subFiltros || this.subFiltros === 'motivo_envio_JEE')
        ? undefined
        : this.subFiltros;

    const reglas = [
      {
        cond: filtroModel.esEleccionParaDistritoElectoral
          && filtroModel.idDistritoElectoral != null,
        apply: () => {
          data.idDistritoElectoral = filtroModel.idDistritoElectoral;
        }
      },
      {
        cond: !filtroModel.esEleccionParaDistritoElectoral,
        apply: () => {
          data.codigoLocalVotacion = filtroModel.idLocalVotacion;
          data.idAmbitoGeografico = filtroModel.idAmbitoGeografico;

          if (filtroModel.idUbigeoNivel01 && filtroModel.idUbigeoNivel01 !== '0') {
            data.ubigeoNivel01 = filtroModel.idUbigeoNivel01;
          }
          if (filtroModel.idUbigeoNivel02 && filtroModel.idUbigeoNivel02 !== '0') {
            data.ubigeoNivel02 = filtroModel.idUbigeoNivel02;
          }
          if (filtroModel.idUbigeoNivel03 && filtroModel.idUbigeoNivel03 !== '0') {
            data.idUbigeo = filtroModel.idUbigeoNivel03;
          }
        }
      }
    ];

    reglas.find(r => r.cond)?.apply();

    if (filtroModel.resueltas != null) {
      data.resueltas = filtroModel.resueltas;
    }

    return data;
  }

  cambioIdAmbitoGeograficoDesdeMapa(value: number) {
    this.filtroBehaviorService.cambiarIdAmbitoGeografico(value);
  }

  ubigeoMapaSeleccionado(value: any) {
    if (value.tipoFiltro === EnumTipoFiltro.DISTRITO_ELECTORAL) {
      this.filtroBehaviorService.actualizarSeleccionDistritoElectoral(value.idDistritoElectoral);
    } else {
      this.filtroBehaviorService.actualizarSeleccionUbigeo(value.idUbigeo);
    }
  }

  obtenerMapaCalorObservadas(data: FiltroModel) {
    this.loadingService.show();

    const param: ObtenerMapaCalorResumenGeneralInput = new ObtenerMapaCalorResumenGeneralInput();
    param.idEleccion = data.idTipoEleccion;
    if (data.esEleccionParaDistritoElectoral) {
      param.idDistritoElectoral = data.idDistritoElectoral;
      param.tipoFiltro = this.obtenerTipoFiltroMapaCalor(param);
    } else {
      const esTodos = data.idAmbitoGeografico === 0 &&
        Number(data.idUbigeoNivel01) === 0 &&
        Number(data.idUbigeoNivel02) === 0;

      if (esTodos) {
        param.tipoFiltro = EnumTipoFiltro.TOTAL;
      } else {
        param.idAmbitoGeografico = data.idAmbitoGeografico;
        param.ubigeoNivel01 = Number(data.idUbigeoNivel01);
        param.ubigeoNivel02 = Number(data.idUbigeoNivel02);
        param.ubigeoNivel03 = 0;
        param.tipoFiltro = this.obtenerTipoFiltroMapaCalor(param);
      }
    }

    this.resumenGeneralApiService
      .obtenerMapaCalorObservada(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            if (Number(data.idUbigeoNivel03) !== 0) {
              param.ubigeoNivel03 = Number(data.idUbigeoNivel03);
              const dataDistrito = result.data.filter((el: any) => el.ubigeoNivel03 === param.ubigeoNivel03);
              result.data = dataDistrito;
            }

            const idAmbitoGeograficoBase = ((data.idAmbitoGeografico ?? 0) === 0 && ((param.ubigeoNivel01 ?? 0) === 0 || param.ubigeoNivel01 === undefined)) ? 0 : (data.idAmbitoGeografico ?? 0);

            let idAmbitoGeograficoTemp = idAmbitoGeograficoBase;
            if (param.tipoFiltro === 'distrito_electoral') {
              idAmbitoGeograficoTemp = 1;
              idAmbitoGeograficoTemp = (param.idDistritoElectoral ?? 0) === 27 ? 0 : idAmbitoGeograficoTemp;
            }

            setTimeout(() => {
              this.chartMapaComponent?.cargaChartMapa(
                'chartdivmap',
                idAmbitoGeograficoTemp,
                param.tipoFiltro ?? '',
                param.tipoFiltro === 'distrito_electoral' ? (param.idDistritoElectoral ?? -1) : -1,
                result.data,
                this.obtenerUbigeoMapaCalor(
                  param.ubigeoNivel01 ?? 0,
                  param.ubigeoNivel02 ?? 0,
                  param.ubigeoNivel03 ?? 0
                ) ?? '000000'
              );
            }, 1000);
          } else {
            setTimeout(() => {
              this.chartMapaComponent?.cargaChartMapa('chartdivmap', -1, '', -1, [], '000000');
            }, 1000);
          }
        },
      });
  }

  obtenerUbigeoMapaCalor(
    ubigeoNivel01: number,
    ubigeoNivel02: number,
    ubigeoNivel03: number
  ): string | null {
    if (ubigeoNivel03 !== 0 && ubigeoNivel03 !== null && ubigeoNivel03 !== undefined) {
      return ubigeoNivel03.toString().padStart(6, '0');
    } else if (ubigeoNivel02 !== 0 && ubigeoNivel02 !== null && ubigeoNivel02 !== undefined) {
      return ubigeoNivel02.toString().padStart(6, '0');
    } else if (ubigeoNivel01 !== 0 && ubigeoNivel01 !== null && ubigeoNivel01 !== undefined) {
      return ubigeoNivel01.toString().padStart(6, '0');
    }
    return null;
  }

  private obtenerTipoFiltroMapaCalor(
    param: ObtenerMapaCalorResumenGeneralInput
  ): string {
    const reglas = [
      { cond: param.idDistritoElectoral != null, tipo: EnumTipoFiltro.DISTRITO_ELECTORAL },
      { cond: param.ubigeoNivel03 != null && param.ubigeoNivel03 !== 0, tipo: EnumTipoFiltro.UBIGEO_NIVEL_03 },
      { cond: param.ubigeoNivel02 != null && param.ubigeoNivel02 !== 0, tipo: EnumTipoFiltro.UBIGEO_NIVEL_02 },
      { cond: param.ubigeoNivel01 != null && param.ubigeoNivel01 !== 0, tipo: EnumTipoFiltro.UBIGEO_NIVEL_01 },
    ];

    return reglas.find(r => r.cond)?.tipo ?? EnumTipoFiltro.AMBITO_GEOGRAFICO;
  }


  private getTotalStringFilters(filtros: string[]) {
    return filtros.join(", ");
  }

  private cargarMensajeInicial(): void {
    this.translate.get('MensajeOriginal').subscribe((text: string) => {
      this.mensaje = text;
    });
  }

  mostrarMensaje(value: string) {
    this.mensaje = value;
    this.mensajeListaActasObservada = '';
    this.listaTablaEscrutinioModeloUno = [];
    this.listaTablaEscrutinioModeloDos = [];
    this.actasObservadas = [];
    this.verDetalleActa = false;
    if (value !== 'constantes.MENSAJE_ORIGINAL') {
      this.haHabidoResultadosPrevios = false;
    }

  }

  cambiosFiltroDinamicos(data: FiltroModel) {
    this.mensaje = '';
    this.mensajeListaActasObservada = '';
    this.verDetalleActa = false;
    this.actasObservadas = [];
    setTimeout(() => {
      this.resumenEtiquetaComponent.obtenerDatosFiltro(data);
    }, 100);
  }

  mostrarDetalleActa(data: ContentActaObservada) {

    this.orderBy = 'Votos';
    this.ctrlToggle.setValue(true);
    // Reset responsive state: en pantallas chicas no mostrar datos automáticamente
    this.mostrarDatosActa = !this.esPantallaChica;
    this.modificarEstadoEstilo(data);
    this.contentActaObservada = data;
    this.obtenerDatosActaObservada(data);
  }

  modificarEstadoEstilo(data: ContentActaObservada) {
    this.actasObservadas = this.actasObservadas.map((x) => {
      if (x.id === data.id) {
        x.activo = true;
      } else {
        x.activo = false;
      }
      return x;
    });
  }

  obtenerDatosActaObservada(data: ContentActaObservada) {

    this.actaApiService
      .obtenerDetalleActa(data.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            if (result.data) {
              this.detalleActa = result.data;
              this.verDetalleActa = true;
              this.resumenEtiquetaComponent?.obtenerDatosFiltro(this.filtroModelActual ?? {} as FiltroModel);


              this.detActaEleccion = (() => {
                const nombre = this.resumenEtiquetaComponent.obtenerEleccion();
                if (!nombre) return '';

                return formatNameElection(nombre)
              })();

              this.estadoDescripcionActaResolucion =
                result.data?.estadoDescripcionActaResolucion;
              this.cargarDatosLineaTiempo(result.data);
              this.cargarDatosTablaEscrutinio(result.data);
              this.archivos = result.data.archivos ?? [];
              this.nombreAmbitoSelect();
              const archivoForActa =
                this.archivos
                  ?.filter(
                    (file) =>
                      file.tipo === TYPE_FOR_PDF['ID_ACTA_ESCRUTINIO'] ||
                      file.tipo === TYPE_FOR_PDF['ID_ACTA_INSTALACION_Y_SUFRAGIO'] ||
                      file.tipo === TYPE_FOR_PDF['ID_ACTA_INSTALACION'] ||
                      file.tipo === TYPE_FOR_PDF['ID_ACTA_SUFRAGIO']
                  )
                  .sort((a, b) => {
                    return a.tipo - b.tipo;
                  }) ?? [];

              const archivoForResolutions =
                this.archivos
                  ?.filter((file) => file.tipo === TYPE_FOR_PDF['ID_RESOLUTION'])
                  .sort((a, b) => {
                    const numA = Number.parseInt(a.descripcion.replaceAll(/\D/g, ''), 10) ?? 0;
                    const numB = Number.parseInt(b.descripcion.replaceAll(/\D/g, ''), 10) ?? 0;
                    return numA - numB;
                  }) ?? [];

              if (this.contentActaObservada) {
                this.contentActaObservada.archivosActa = archivoForActa;
                this.contentActaObservada.archivosResolucion = archivoForResolutions;
              }
            } else {
              this.verDetalleActa = false;
            }
          }
        },
      });
  }

  private getFormattedActa(acta: Acta): Acta {
    const { totalVotosEmitidos, totalElectoresHabiles } = acta;
    if (totalElectoresHabiles) {
      const formattedActa = {
        ...acta,
        porcentajeParticipacionCiudadana:
          ((totalVotosEmitidos ?? 0) / totalElectoresHabiles) * 100,
      };
      return formattedActa;
    } else {
      return acta;
    }
  }

  cargarDatosTablaEscrutinio(data: Acta) {
    if (data.idEleccion === EnumIdEleccion.ID_ELECCION_PRESIDENCIAL) {
      this.listaTablaEscrutinioModeloUno = this.mapearDatosTablaEscrutinioUno(
        data?.detalle ?? [] as Detalle[])
        .sort((a, b) => b.totalVotos - a.totalVotos);
    } else {
      this.listaTablaEscrutinioModeloDos = this.mapearDatosTablaEscrutinioDos(
        data?.detalle ?? [] as Detalle[])
        .sort((a, b) => b.totalVotos - a.totalVotos);
    }

    setTimeout(() => {
      document.getElementById('detalle-view-222')?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
        inline: 'nearest',
      });
    }, 200);
  }

  mapearDatosTablaEscrutinioUno(lista: Detalle[]) {
    return lista.map((value) => {
      const tabla: TablaEscrutinioModeloUno = new TablaEscrutinioModeloUno();
      tabla.idPartidoPolitico = value.nagrupacionPolitica ?? 0;
      tabla.nombrePartidoPolitico = value.descripcion ?? '';
      tabla.totalCandidatos = value.totalCandidatos ?? 0;
      tabla.totalVotos = value.nvotos ?? 0;
      tabla.votosEmitido = value.nporcentajeVotosEmitidos ?? 0;
      tabla.votosValido = value.nporcentajeVotosValidos ?? 0;
      tabla.posicion = value.nposicion ?? 0;
      tabla.estado = value.estado ?? 0;
      tabla.candidato = value.candidato;
      tabla.ccodigo = value.ccodigo;
      return tabla;
    });
  }

  mapearDatosTablaEscrutinioDos(lista: Detalle[]) {
    return lista.map((value) => {
      const tabla: TablaEscrutinioModeloDos = new TablaEscrutinioModeloDos();
      tabla.idPartidoPolitico = value.nagrupacionPolitica ?? 0;
      tabla.nombrePartidoPolitico = value.descripcion ?? '';
      tabla.totalCandidatos = value.totalCandidatos ?? 0;
      tabla.totalVotos = value.nvotos ?? 0;
      tabla.votosEmitido = value.nporcentajeVotosEmitidos ?? 0;
      tabla.votosValido = value.nporcentajeVotosValidos ?? 0;
      tabla.posicion = value.nposicion ?? 0;
      tabla.estado = value.estado ?? 0;
      tabla.ccodigo = value.ccodigo;
      return tabla;
    });
  }

  cargarDatosLineaTiempo(data: Acta) {
    this.lineasTiempo = data?.lineaTiempo;
  }

  limpiar({ filtro, absolute }: { filtro: FiltroModel; absolute: boolean }): void {
    this.mensaje = absolute ? 'constantes.MENSAJE_ORIGINAL' : '';
    this.mensajeListaActasObservada = '';
    this.actasObservadas = [];
    this.verDetalleActa = false;
    this.archivos = [];
    this.haHabidoResultadosPrevios = false;
    this.form.get('estadoActa')?.setValue(0, { emitEvent: false });
    this.form.get('filtroActas')?.setValue([], { emitEvent: false });
    filtro.nombreUbigeoNivel01 = undefined;
    filtro.nombreUbigeoNivel02 = undefined;
    filtro.nombreUbigeoNivel03 = undefined;
    this.chartMapaComponent?.destroyChart('chartdivmap');
    this.selectedTextsSubStatusFilter = [];
    if (!absolute) this.filtrar(filtro);
    this.removeSelectedItem();
  }

  ngOnDestroy(): void {
    if (this.resizeDebounceTimer !== null) {
      clearTimeout(this.resizeDebounceTimer);
    }
    this.destroy$.next();
    this.destroy$.complete();
  }

  public mostrarNombreNivel(nivel: number, capital: boolean = false): string {
    return this.mostrarNombreNivelBase(this.detalleActa.idAmbitoGeografico ?? 1, nivel, capital);
  }

  private mostrarNombreNivelBase(
    idAmbito: number,
    nivel: number,
    capital: boolean = false
  ): string {
    const nombresPorNivel: { [key: number]: string[] } = {
      1: [
        this.translate.instant('genericUbigeo.region'),
        this.translate.instant('genericUbigeo.provincia'),
        this.translate.instant('genericUbigeo.distrito'),
        this.translate.instant('genericUbigeo.localVotacion')
      ],
      2: [
        this.translate.instant('genericUbigeo.continente'),
        this.translate.instant('genericUbigeo.pais'),
        this.translate.instant('genericUbigeo.ciudad'),
        'EMBAJADA'
      ],
    };
    const nombreNivel = nombresPorNivel[idAmbito]?.[nivel] ?? '';
    return this.capitalizeFirstLetter(nombreNivel);
  }

  capitalizeFirstLetter(str: string) {
    return (
      str.toLowerCase().charAt(0).toUpperCase() +
      str.toLowerCase().toLowerCase().slice(1)
    );
  }

  mostrarValorTotalVotantes(detalle: Acta): string {
    if (this.isMesaNoInstalada(detalle)) return '-';
    return this.shouldHideText(detalle) ? '' : this.stringify(detalle.totalVotosEmitidos);
  }

  mostrarValorParticipacionCiudadana(detalle: Acta): string {
    if (this.isMesaNoInstalada(detalle)) return '-';
    if (detalle.codigoEstadoActa === 'E') return '';
    if (this.shouldHideText(detalle)) return '';
    return detalle.porcentajeParticipacionCiudadana === null
      ? ''
      : `${detalle.porcentajeParticipacionCiudadana} %`;
  }

  protected mostrarEtiquetaAdicionalEstadoActa(detalle: Acta): string {
    const descripcion =
      detalle?.descripcionSubEstadoActa ?? detalle?.estadoDescripcionActaResolucion;
    const upperDesc = descripcion ? ` (${descripcion.toUpperCase()})` : '';

    if (this.isMesaNoInstalada(detalle)) {
      return upperDesc;
    }

    const { estadoActaResolucion, codigoEstadoActa } = detalle;
    const esExtraviada = estadoActaResolucion === 'X';
    const esSiniestrada = estadoActaResolucion === 'Y';
    const esParaEnvioOJEE = codigoEstadoActa === 'E';
    const esContabilizada = codigoEstadoActa === 'C';

    return (esParaEnvioOJEE || esContabilizada) && (esExtraviada || esSiniestrada)
      ? upperDesc
      : '';
  }

  private stringify(value?: number | undefined): string {
    return value !== null && value !== undefined ? String(value) : '';
  }

  private isMesaNoInstalada(detalle: Acta): boolean {
    return (
      detalle.codigoEstadoActa === 'C' &&
      detalle.estadoActa === 'N'
    );
  }

  private shouldHideText(detalle: Acta): boolean {
    const esContabilizada = detalle.codigoEstadoActa === 'C';
    return (!esContabilizada) || detalle.codigoEstadoActa === 'P';
  }

  // MÉTODO ADICIONAL SOLICITADO: mostrarTotalElectoresHabiles
  mostrarTotalElectoresHabiles(detalle: any): string {
    return this.stringify(detalle?.totalElectoresHabiles);
  }

  chipsOpen = false;
  toggleChips() {
    this.chipsOpen = !this.chipsOpen;
  }

  get tamanioPaginaActas(): number {
    return this.esPantallaChica ? TAMANIO_PAGINA_ACTAS_OBS_MOVIL : TAMANIO_PAGINA_ACTAS_OBS;
  }

  private updatePageSizeOptions(): void {
    const size = this.tamanioPaginaActas;
    const sizeChanged = this.tableInformation.pageSize !== size;
    this.pageSizeOptions = [size];

    // Forzar actualización del paginator
    this.tableInformation.pageSize = size;

    // Solo recargar datos si el tamaño de página cambió (cruce del umbral 960px)
    if (sizeChanged) {
      this.reloadData();
    }
  }

  private reloadData(): void {
    if (!this.filtroModelActual) return;

    this.paginaActual = 0;
    this.cargarActasObservadas(this.filtroModelActual, false);
  }

  protected nombreAmbitoSelect() {
    const id = this.detalleActa.idAmbitoGeografico ?? 0;
    this.nombreAmbito = AMBITO[id as keyof typeof AMBITO];
    return this.nombreAmbito;
  }
}
