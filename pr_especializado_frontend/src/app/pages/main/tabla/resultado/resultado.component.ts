import { ChangeDetectorRef, Component, inject, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { take, Subject } from 'rxjs';

import { ChartMapaComponent } from '../../../../components/chart-mapa/chart-mapa.component';
import { FiltroBehaviorService } from '../../../../components/filtro/filtro.behavior.service';
import { ResumenGraficaModeloUnoComponent } from '../../../../components/resumen-grafica-modelo-uno/resumen-grafica-modelo-uno.component';
import { REPORTE_GENERALES, TAMANIO_PAGINA } from '../../../../helpers/constantes';
import { EnumIdAmbito, EnumAmbito, EnumTipoFiltro, TYPE_FOR_PDF } from '../../../../helpers/enums';
import { getGenericFilterType } from '../../../../helpers/filter-type.common';
import { formatNameElection, mapearCamposResumenTotales } from '../../../../helpers/funciones';
import { getOptimizedObject } from '../../../../helpers/object.utils';
import { ChartPie } from '../../../../interfaces/chart-pie.model';
import { FiltroModel } from '../../../../interfaces/filtro.model';
import { ObtenerMapaCalorResumenGeneralInput } from '../../../../interfaces/input/resumen-general/obtener-mapa-calor-resumen-general-input';
import { ResumenTotal } from '../../../../interfaces/output/resumen-total.model';
import { ActaApiService, IActasParams, IActasResponseData } from '../../../../services/acta-api.service';
import {
  GeneratorFileReporterService,
  IGenerateReportParams
} from '../../../../services/generator-file-reporter.service';
import { JwtDecodeService } from '../../../../services/jwt-decode.service';
import { ResumenGeneralApiService } from '../../../../services/resumen-general-api.service';
import { TablaService } from '../../../../services/tabla.service';
import { DISPLAYED_COLUMNS_KEY, ITableInformation } from './interfaces/actas.interfaces';
import { ModalDescargaComponent } from './modal-descarga/modal-descarga.component';
import { IDownloadStatusType, IModalDescargaData, MODAL_MESSGAGE } from './modal-descarga/modal-message';
import { ComponentsModule } from '../../../../components/components.module';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { DatePipe, NgIf, NgClass, UpperCasePipe, CommonModule } from '@angular/common';
import {
  IModalVisorConfig,
  ModalVisorPdfComponent
} from '../../../../components/modal-visor-pdf/modal-visor-pdf.component';
import { ResumenEtiquetaComponent } from '../../../../components/resumen-etiqueta/resumen-etiqueta.component';
import { TranslateModule } from '@ngx-translate/core';
import { DateAdapter } from '@angular/material/core';
import { CustomDateAdapter } from '../../../../helpers/datetime-helper.common';
import {
  CTableMobileComponent,
  MobileTableColumn,
  MobileTableRow
} from '../../../../components/c-table-mobile/c-table-mobile.component';
import { DialogService } from '../../../../services/dialog.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { LoadingService } from '../../../../components/loading/loading.service';

@Component({
  selector: 'app-resultado',
  standalone: true,
  templateUrl: './resultado.component.html',
  providers: [{ provide: DatePipe }, { provide: DateAdapter, useClass: CustomDateAdapter }],
  imports: [
    CommonModule,
    ComponentsModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    NgIf,
    NgClass,
    TranslateModule,
    UpperCasePipe,
    CTableMobileComponent,
    MatExpansionModule
  ]
})
export class ResultadoComponent implements OnInit, OnDestroy {
  @ViewChild(ChartMapaComponent) chartMapaComponent?: ChartMapaComponent;
  @ViewChild(ResumenGraficaModeloUnoComponent) resumenGraficaModeloUnoComponent?: ResumenGraficaModeloUnoComponent;
  @ViewChild('resumenEtiquetaComponent') resumenEtiquetaComponent!: ResumenEtiquetaComponent;

  resumenTotal?: ResumenTotal;
  listaActasChartPieDatos: ChartPie[] = [];
  listaElectoresChartPieDatos: ChartPie[] = [];
  idTipoEleccion?: number;
  cadenaFiltro: string = '';
  mensaje: string = '';
  contabilizadasPorcentaje?: number;

  public displayedColumns = DISPLAYED_COLUMNS_KEY;

  mostrarEstadoActa: boolean = false;

  toggleEstadoActa(): void {
    this.mostrarEstadoActa = !this.mostrarEstadoActa;
  }

  seleccionarEstado(valor: string): void {
    this.form.get('estadoActa')?.setValue(valor);
    this.select3 = valor;
    this.mostrarEstadoActa = false;
    this.estadoActaHasChanged();
  }

  obtenerTextoEstado(): string {
    const valor = this.form.get('estadoActa')?.value;

    switch (valor) {
      case 'C':
        return 'Contabilizadas';
      case 'E':
        return 'Para envío al JEE';
      case 'P':
        return 'Pendientes';
      case '':
      default:
        return 'Estado de acta';
    }
  }

  // Columnas para la tabla móvil
  public mobileDisplayedColumns: MobileTableColumn[] = [
    { key: 'detalle-estado', label: 'Detalle', type: 'detalle-estado' },
    { key: 'acciones', label: '', type: 'acciones' }
  ];

  // ** AHORA dataSource es MatTableDataSource<MobileTableRow> **
  public tableInformation: ITableInformation = {
    dataSource: new MatTableDataSource<MobileTableRow>([]),
    pageSize: TAMANIO_PAGINA,
    totalPages: 0,
    totalRegisters: 0,
    currentPage: 0
  } as ITableInformation;

  public isPoliticalOrganization: boolean = false;
  public esPantallaChica: boolean = false;
  public mostrarMapa2: boolean = false;
  public mapaCalor: boolean = false;

  public filtroModel: FiltroModel = {} as FiltroModel;

  private readonly formBuilder = inject(FormBuilder);
  private readonly cd = inject(ChangeDetectorRef);
  private readonly customDateAdapter = inject(DateAdapter);
  private readonly dialogService = inject(DialogService);
  private readonly destroy$ = new Subject<void>();
  private readonly boundHandleResize = this.handleResize.bind(this);

  constructor(
    public dialog: MatDialog,
    public tablaService: TablaService,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly filtroBehaviorService: FiltroBehaviorService,
    private readonly actaApiService: ActaApiService,
    private readonly generatorFileReporterService: GeneratorFileReporterService,
    private readonly jwtDecodeService: JwtDecodeService,
    private readonly loadingService: LoadingService
  ) {
    this.isPoliticalOrganization = this.jwtDecodeService.isPoliticOrganization;
    if (!this.isPoliticalOrganization) {
      this.displayedColumns = this.displayedColumns.filter((ele) => ele != 'votos');
    }
  }

  form = this.formBuilder.group({
    estadoActa: ['']
  });

  ngOnInit(): void {
    this.detectarTamanoPantalla();
    window.addEventListener('resize', this.boundHandleResize);
  }

  private handleResize(): void {
    this.detectarTamanoPantalla();
  }

  protected verActa(element: any) {
    const modalConfig = {
      multiple: true,
      numeroDeActa: element.mesa,
      nombreDeActa: element.tipo,
      archivos: element.archivosActa,
      esPantallaChica: window.innerWidth < 959
    } as IModalVisorConfig;
    this.openModal(modalConfig);
  }

  public verResolucion(element: any) {
    const modalConfig = {
      multiple: true,
      numeroDeActa: element.mesa,
      nombreDeActa: element.tipo,
      archivos: element.archivosResolucion,
      esPantallaChica: window.innerWidth < 959
    } as IModalVisorConfig;
    this.openModal(modalConfig);
  }

  private openModal(modalConfig: IModalVisorConfig) {
    this.cd.detectChanges();
    this.dialogService
      .openComponent(ModalVisorPdfComponent, {
        maxWidth: '100%',
        width: '90vw',
        maxHeight: '95vh',
        panelClass: 'popup-acta',
        data: modalConfig
      })
      .pipe(take(1))
      .subscribe(() => {
        this.cd.detectChanges();
      });
  }

  private detectarTamanoPantalla(): void {
    this.esPantallaChica = window.innerWidth < 959;
    this.cd.detectChanges();
  }

  openDialog2(status: IDownloadStatusType) {
    this.dialog.open<any, IModalDescargaData>(ModalDescargaComponent, {
      panelClass: 'modal-descarga',
      data: { message: MODAL_MESSGAGE[status] }
    });
  }

  select1: string = '0';
  select2: string = '0';
  select3: string = '0';
  select4: string = '0';

  formatNameElection(string: string) {
    return formatNameElection(string);
  }

  filtrar(filtroModel: FiltroModel): void {
    this.filtroModel.nombreTipoEleccion = formatNameElection(this.filtroModel.nombreTipoEleccion);
    this.idTipoEleccion = filtroModel.idTipoEleccion;
    this.filtroModel = filtroModel;
    this.resumenEtiquetaComponent?.obtenerDatosFiltro(filtroModel);
    this.obtenerMapaCalor(filtroModel);
    this.obtenerResumenTotales(filtroModel);
    this.loadPaginatedActas(filtroModel);
  }

  private loadPaginatedActas(filtroModel: FiltroModel) {
    // limpiar dataSource correctamente (MatTableDataSource)
    this.tableInformation.dataSource = new MatTableDataSource<MobileTableRow>([]);
    const params = this.getActasParams(filtroModel);

    this.actaApiService
      .getActas$(params, { pagina: 0, tamanio: TAMANIO_PAGINA })
      .pipe(take(1))
      .subscribe((response) => {
        this.mensaje = (this.customDateAdapter as CustomDateAdapter).generarMensajeConsultasAvanzadas();
        this.tableInformation.totalRegisters = 0;

        if (response.success) {
          const data = response.data;

          if (data && Array.isArray(data.content) && data.content.length > 0) {
            this.mensaje = '';
            const formattedData = this.getFormattedDataSource(data);
            this.tableInformation.dataSource.data = formattedData;
            this.tableInformation.totalPages = data.totalPaginas ?? 0;
            this.tableInformation.totalRegisters = data.totalRegistros ?? 0;
            this.tableInformation.currentPage = 0;
            this.tableInformation.pageSize = Math.min(data.totalRegistros, TAMANIO_PAGINA);
          }
        } else {
          console.error('getActas$ error: ', response);
        }
      });
  }

  private getActasParams(filtroModel: FiltroModel): IActasParams {
    const params = {
      idAmbitoGeografico: filtroModel.idAmbitoGeografico,
      idEleccion: filtroModel.idTipoEleccion,
      codigoEstadoActa: null,
      ubigeoNivel01: filtroModel.idUbigeoNivel01,
      ubigeoNivel02: filtroModel.idUbigeoNivel02,
      idUbigeo: filtroModel.idUbigeoNivel03,
      codigoLocalVotacion: filtroModel.idLocalVotacion,
      idDistritoElectoral: filtroModel.esEleccionParaDistritoElectoral ? filtroModel.idDistritoElectoral : undefined
    } as IActasParams;
    return getOptimizedObject(params);
  }

  public toggleMapa(): void {
    this.mostrarMapa2 = !this.mostrarMapa2;
  }

  public getFormattedDataSource(responseData: IActasResponseData): MobileTableRow[] {
    return responseData.content.map((actaItem) => {
      const archivoForActa =
        actaItem.archivos
          ?.filter(
            (file) =>
              file.tipo == TYPE_FOR_PDF['ID_ACTA_ESCRUTINIO'] ||
              file.tipo == TYPE_FOR_PDF['ID_ACTA_INSTALACION_Y_SUFRAGIO'] ||
              file.tipo == TYPE_FOR_PDF['ID_ACTA_INSTALACION'] ||
              file.tipo == TYPE_FOR_PDF['ID_ACTA_SUFRAGIO']
          )
          .sort((a, b) => a.tipo - b.tipo) || [];

      const archivoForResolutions =
        actaItem.archivos
          ?.filter((file) => file.tipo == TYPE_FOR_PDF['ID_RESOLUTION'])
          .sort((a, b) => {
            const numA = this.parseNumeroResolucion(a.descripcion);
            const numB = this.parseNumeroResolucion(b.descripcion);
            return numA - numB;
          }) || [];

      return {
        ambito: actaItem.descripcionAmbitoGeografico,
        departamento: actaItem.ubigeoNivel01,
        provincia: actaItem.ubigeoNivel02,
        distrito: actaItem.ubigeoNivel03,
        documento: actaItem.descripcionEstadoActa,
        electores: actaItem.totalElectoresHabiles ? actaItem.totalElectoresHabiles.toString() : '0',
        estadoacta: actaItem.estadoActa,
        local: actaItem.nombreLocalVotacion,
        mesa: actaItem.codigoMesa,
        tipo: actaItem.descripcionEleccion,
        votos: actaItem.totalVotosEmitidos ? actaItem.totalVotosEmitidos.toString() : '0',
        archivosActa: archivoForActa,
        archivosResolucion: archivoForResolutions
      } as MobileTableRow;
    });
  }

  private parseNumeroResolucion(descripcion: string): number {
    const resultado = Number.parseInt(descripcion.replaceAll(/\D/g, ''), 10);
    return Number.isNaN(resultado) ? 0 : resultado;
  }

  public generarReporte() {
    const {
      idAmbitoGeografico,
      idTipoEleccion,
      idUbigeoNivel01,
      idUbigeoNivel02,
      idUbigeoNivel03,
      idLocalVotacion,
      nombreUbigeoNivel01,
      nombreUbigeoNivel02,
      nombreUbigeoNivel03
    } = this.filtroModel;

    const params = {
      idEleccion: idTipoEleccion,
      idAmbitoGeografico: idAmbitoGeografico,
      tipoFiltro: this.getFilterType(),
      porcentajeContabilizada: this.contabilizadasPorcentaje,
      ubigeoNivel01: idUbigeoNivel01,
      ubigeoNivel02: idUbigeoNivel02,
      idUbigeo: idUbigeoNivel03,
      codigoLocalVotacion: idLocalVotacion,
      descripcionUbigeoNivel1: nombreUbigeoNivel01,
      descripcionUbigeoNivel2: nombreUbigeoNivel02,
      descripcionUbigeoNivel3: nombreUbigeoNivel03,
      tipoReporte: REPORTE_GENERALES,
      codigoUsuario: 'usuario',
      descripcionUsuario: 'EN MI PECHO LLEVO TUS COLORES'
    } as IGenerateReportParams;
    const currentParams = getOptimizedObject(params);

    this.generatorFileReporterService
      .createReporterFile$(currentParams)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.openDialog2('init_download');
        } else {
          this.openDialog2('in_progress');
          console.error('getReporterFile$ error', response.message);
        }
      });
  }

  private getFilterType() {
    const { idAmbitoGeografico, idUbigeoNivel01, idUbigeoNivel02, idUbigeoNivel03, idLocalVotacion } = this.filtroModel;
    return getGenericFilterType({
      ambito: idAmbitoGeografico,
      ubigeo01: idUbigeoNivel01,
      ubigeo02: idUbigeoNivel02,
      ubigeo03: idUbigeoNivel03,
      local: idLocalVotacion
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
    const params = this.getActasParams(this.filtroModel);

    if ('EstadoActa' == this.form.controls.estadoActa.value) {
      this.actaApiService
        .getActas$(params, {
          pagina: pageEvent.pageIndex,
          tamanio: TAMANIO_PAGINA
        })
        .pipe(take(1))
        .subscribe((response) => {
          if (!response.success || !response.data) {
            console.error('getActas$ error: ', response);
            return;
          }

          const { totalPaginas = 0, paginaActual = 0 } = response.data;

          this.tableInformation.dataSource.data = this.getFormattedDataSource(response.data);

          this.tableInformation.totalPages = totalPaginas;
          this.tableInformation.currentPage = paginaActual;
          this.tableInformation.pageSize = TAMANIO_PAGINA;
        });
    } else {
      this.estadoActaHasChanged(pageEvent);
    }
  }

  // Métodos para el paginador móvil personalizado
  public goToPreviousPage(): void {
    if (this.tableInformation.currentPage > 0) {
      const pageEvent: PageEvent = {
        pageIndex: this.tableInformation.currentPage - 1,
        pageSize: this.tableInformation.pageSize,
        length: this.tableInformation.totalRegisters
      };
      this.handlePageEvent(pageEvent);
    }
  }

  public goToNextPage(): void {
    if (this.tableInformation.currentPage < this.tableInformation.totalPages - 1) {
      const pageEvent: PageEvent = {
        pageIndex: this.tableInformation.currentPage + 1,
        pageSize: this.tableInformation.pageSize,
        length: this.tableInformation.totalRegisters
      };
      this.handlePageEvent(pageEvent);
    }
  }

  public goToPage(page: number | string): void {
    if (typeof page === 'number' && page > 0 && page <= this.tableInformation.totalPages) {
      const pageEvent: PageEvent = {
        pageIndex: page - 1,
        pageSize: this.tableInformation.pageSize,
        length: this.tableInformation.totalRegisters
      };
      this.handlePageEvent(pageEvent);
    }
  }

  public getVisiblePages(): (number | string)[] {
    const totalPages = this.tableInformation.totalPages;
    const currentPage = this.tableInformation.currentPage + 1;
    const maxPagesToShow = 3;

    if (totalPages <= maxPagesToShow) {
      const pages: (number | string)[] = [];
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
      return pages;
    }

    const pages: (number | string)[] = [];

    pages.push(1);

    if (currentPage > maxPagesToShow) {
      pages.push('...');
    }

    let startPage = Math.max(2, currentPage - 1);
    let endPage = Math.min(totalPages - 1, currentPage + 1);

    if (currentPage <= maxPagesToShow) {
      startPage = 2;
      endPage = Math.min(totalPages - 1, maxPagesToShow);
    }

    for (let i = startPage; i <= endPage; i++) {
      if (!pages.includes(i)) {
        pages.push(i);
      }
    }

    if (endPage < totalPages - 1) {
      pages.push('...');
    }

    if (endPage < totalPages) {
      pages.push(totalPages);
    }

    return pages;
  }

  public estadoActaHasChanged(pageEvent?: PageEvent): void {
    const estadoActa = this.form.controls.estadoActa.value;
    const params = this.getActasParams(this.filtroModel);
    params.codigoEstadoActa = estadoActa;
    const currentParams = getOptimizedObject(params);
    this.tableInformation.totalRegisters = 0;
    this.tableInformation.dataSource = new MatTableDataSource<MobileTableRow>([]);

    let paginaIndex = 0;
    if (estadoActa !== 'EstadoActa' && pageEvent) {
      paginaIndex = pageEvent.pageIndex;
    }

    this.actaApiService
      .getActas$(currentParams, {
        pagina: paginaIndex,
        tamanio: TAMANIO_PAGINA
      })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          const data = response.data;
          if (data && Array.isArray(data.content) && data.content.length > 0) {
            this.mensaje = '';
            const formattedData = this.getFormattedDataSource(data);
            this.tableInformation.dataSource.data = formattedData as unknown as MobileTableRow[];
            this.tableInformation.totalPages = data.totalPaginas ?? 0;
            this.tableInformation.totalRegisters = data.totalRegistros ?? 0;
            this.tableInformation.currentPage = data?.paginaActual ?? 0;
            this.tableInformation.pageSize = Math.min(data.totalRegistros, TAMANIO_PAGINA);
          } else {
            this.mensaje = (this.customDateAdapter as CustomDateAdapter).generarMensajeConsultasAvanzadas();
          }
        } else {
          console.error('getActas$ error: ', response);
        }
      });
  }

  // Resto de métodos del mapa y otros existentes...
  obtenerMapaCalor(data: FiltroModel) {
    this.loadingService.show();

    let param: ObtenerMapaCalorResumenGeneralInput = new ObtenerMapaCalorResumenGeneralInput();
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
      .obtenerMapaCalor(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.mapaCalor = true;

            if (Number(data.idUbigeoNivel03) !== 0) {
              param.ubigeoNivel03 = Number(data.idUbigeoNivel03);
              const dataDistrito = result.data.filter((el: any) => el.ubigeoNivel03 == param.ubigeoNivel03);
              result.data = dataDistrito;
            }

            const idAmbitoGeograficoBase = data.idAmbitoGeografico ?? 0;
            const ubigeoNivel01 = param.ubigeoNivel01 ?? 0;
            let idAmbitoGeograficoTemp =
              idAmbitoGeograficoBase === 0 && (ubigeoNivel01 === 0 || ubigeoNivel01 === undefined)
                ? 0
                : idAmbitoGeograficoBase;

            if (param.tipoFiltro == 'distrito_electoral') {
              const idDistrito = param.idDistritoElectoral ?? 0;
              idAmbitoGeograficoTemp = idDistrito === 27 ? 0 : 1;
            }

            setTimeout(() => {
              const ubigeoNivel01Value = param.ubigeoNivel01 ?? 0;
              const ubigeoNivel02Value = param.ubigeoNivel02 ?? 0;
              const ubigeoNivel03Value = param.ubigeoNivel03 ?? 0;

              this.chartMapaComponent?.cargaChartMapa(
                'chartdivmap',
                idAmbitoGeograficoTemp,
                param.tipoFiltro ?? '',
                param.tipoFiltro == 'distrito_electoral' ? (param.idDistritoElectoral ?? -1) : -1,
                result.data,
                this.obtenerUbigeoMapaCalor(ubigeoNivel01Value, ubigeoNivel02Value, ubigeoNivel03Value) ?? '000000'
              );
            }, 525);
          } else {
            setTimeout(() => {
              this.chartMapaComponent?.cargaChartMapa('chartdivmap', -1, '', -1, [], '000000');
            }, 525);
          }
        }
      });
  }

  obtenerNombreAmbitoGeografico(idAmbitoGeografico: number): string {
    if (idAmbitoGeografico == EnumIdAmbito.NACIONAL) {
      return EnumAmbito.NACIONAL;
    } else {
      return EnumAmbito.EXTRANJERO;
    }
  }

  obtenerUbigeoMapaCalor(ubigeoNivel01: number, ubigeoNivel02: number, ubigeoNivel03: number): string | null {
    if (ubigeoNivel03 !== 0 && ubigeoNivel03 !== null && ubigeoNivel03 !== undefined) {
      return ubigeoNivel03.toString().padStart(6, '0');
    } else if (ubigeoNivel02 !== 0 && ubigeoNivel02 !== null && ubigeoNivel02 !== undefined) {
      return ubigeoNivel02.toString().padStart(6, '0');
    } else if (ubigeoNivel01 !== 0 && ubigeoNivel01 !== null && ubigeoNivel01 !== undefined) {
      return ubigeoNivel01.toString().padStart(6, '0');
    }
    return null;
  }

  obtenerResumenTotales(data: FiltroModel) {
    const params = mapearCamposResumenTotales(data);

    this.resumenGeneralApiService
      .obtenerResumenTotales(params)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.resumenTotal = result.data;
            this.contabilizadasPorcentaje = this.resumenTotal?.contabilizadasPorcentaje;
            this.resumenGraficaModeloUnoComponent?.cargarDatos(this.resumenTotal!);
            this.resumenEtiquetaComponent?.obtenerDatosFiltro(data, this.resumenTotal);
          }
        }
      });
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

  ubigeoMapaSeleccionado(value: any) {
    if (value.tipoFiltro == EnumTipoFiltro.DISTRITO_ELECTORAL) {
      this.filtroBehaviorService.actualizarSeleccionDistritoElectoral(value.idDistritoElectoral);
    } else {
      this.filtroBehaviorService.actualizarSeleccionUbigeo(value.idUbigeo);
    }
  }

  cambioIdAmbitoGeograficoDesdeMapa(value: number) {
    if (this.filtroModel.esEleccionParaDistritoElectoral) {
      this.filtroBehaviorService.actualizarSeleccionDistritoElectoral(String(value));
    } else {
      this.filtroBehaviorService.cambiarIdAmbitoGeografico(value);
    }
  }

  limpiar({ filtro, absolute }: { filtro: FiltroModel; absolute: boolean }): void {
    this.tableInformation.dataSource = new MatTableDataSource<MobileTableRow>([]);
    this.form.get('estadoActa')?.setValue('', { emitEvent: false });
    this.chartMapaComponent?.destroyChart('chartdivmap');
    this.mapaCalor = false;
    if (!absolute) this.filtrar(filtro);
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.boundHandleResize);
    this.destroy$.next();
    this.destroy$.complete();
  }
}
