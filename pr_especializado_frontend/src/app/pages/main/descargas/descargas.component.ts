import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  IReportListData,
  ReporteApiService,
} from '../../../services/reporte-api.service';
import { catchError, of, Subject, take, takeUntil } from 'rxjs';
import { PageEvent } from '@angular/material/paginator';

import { JwtDecodeService } from '../../../services/jwt-decode.service';
import {
  MENSAJE_BUSQUEDA_POR_FECHAS,
  LISTA_TIPO_REPORTE,
  TIME_BETWWEN_MODULES,
  ARCHIVO_NO_DISPONIBLE,
  REPORTE_NO_DISPONIBLE,
} from '../../../helpers/constantes';
import { CustomDateAdapter } from '../../../helpers/datetime-helper.common';
import { ReporteListarInput } from '../../../interfaces/input/reporte/reporte-listar-input';
import { TipoEleccion } from '../../../interfaces/output/tipo-eleccion.model';
import { ResumenGeneralApiService } from '../../../services/resumen-general-api.service';
import { getEncryptStorageEleccionValue } from '../../../helpers/encrypt-storage-eleccion';
import { ListarEleccionesResumenGeneralInput } from '../../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { ELECCION_DEFAULT, EnumTipoFiltro } from '../../../helpers/enums';
import { LoadingService } from '../../../components/loading/loading.service';
import { PopupFiltroEleccionComponent } from "../../../components/popup-filtro-eleccion/popup-filtro-eleccion.component";
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FiltroEleccionData } from '../../../interfaces/output/filtro/eleccion.model';
import {
  mapDescargasReportRows,
  ReportesListadoRow,
  ReportesListadoTableInfo,
} from '../../../helpers/reportes-list.helper';
import { ReportesListadoComponent } from '../shared/reportes-listado/reportes-listado.component';
import { DialogService } from '../../../services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { UpperCasePipe } from '@angular/common';

@Component({
  selector: 'app-descargas',
  templateUrl: './descargas.component.html',
  providers: [
    { provide: CustomDateAdapter },
  ],
  imports: [
    ReportesListadoComponent,
    UpperCasePipe
  ],
})
export class DescargasComponent implements OnInit, OnDestroy {
  destroy$ = new Subject<boolean>();
  select1: string = '0';
  select2: string = '0';
  select3: string = '0';
  select4: string = '0';

  displayedColumns: string[] = [
    'fecha',
    'tipoReporte',
    'tipoEleccion',
    'porcentaje',
    'ambito',
    'dc',
    'pp',
    'de',
    'estado',
    'repositorio',
  ];

  // Inicializa con el mínimo deseado para el selector (por ejemplo 5)
  public tableInformation: ReportesListadoTableInfo = {
    dataSourceTotals: [],
    pageSize: 10, // mínimo al cargar
    totalPages: 0,
    totalRegisters: 0,
    currentPage: 0,
  } as ReportesListadoTableInfo;

  fechaFiltro = new FormGroup({
    inicio: new FormControl(null),
    fin: new FormControl(null),
  });
  totalRegistros: number = 0;

  criterioSeleccionado: string = '';
  public mensajeBuscarPorFechas: string = MENSAJE_BUSQUEDA_POR_FECHAS;
  public mensajeNoDataExists:string = ''
  public isLoadingReportList = false;
  public isFirstRequest = true;
  private readonly dialogService = inject(DialogService);

  public isPoliticOrganization = false;

  private readonly formBuilder = inject(FormBuilder);
  form = this.formBuilder.group({
    tipoEleccion: [{ value: 0, disabled: false }],
    tipoReporte: [{ value: 0, disabled: false }],
  });

  listaReporte: TipoEleccion[] = [];
  listaEleccion: TipoEleccion[] = [];
  isContentReady: boolean = false;

  constructor(
    private readonly reporteApiService: ReporteApiService,
    private readonly jwtDecodeService: JwtDecodeService,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly loadingService: LoadingService,
    private readonly customDateAdapter: CustomDateAdapter,
    private readonly translate: TranslateService
  ) {
    this.isPoliticOrganization = this.jwtDecodeService.isPoliticOrganization;
    this.mensajeNoDataExists = this.customDateAdapter.generarMensajeMisReportes();
    this.cargarCriterioInicial();
  }

  ngOnInit(): void {
    this.loadingService.show();
    this.isContentReady = false;
    
    // Suscribirse a cambios de idioma
    this.translate.onLangChange.subscribe(() => {
      this.cargarCriterioInicial();
    });
    
    setTimeout(() => {
      this.filtrar(); // primera carga con mínimo
      this.valuesChanges();
      this.listarOpciones();
      this.loadingService.hide();
      this.isContentReady = true;
    }, TIME_BETWWEN_MODULES);
  }

  ngOnDestroy(): void {
    this.destroy$.next(false);
  }

  private cargarCriterioInicial(): void {
    this.translate.get('Descargas.SeleccionaCriterio').subscribe((text: string) => {
      this.criterioSeleccionado = text;
    });
  }

  valuesChanges(): void {
    this.form
      .get('tipoReporte')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.form.get('tipoReporte')?.setValue(value, { emitEvent: false });
          // reinicia a primera página manteniendo el pageSize actual
          this.tableInformation.currentPage = 0;
          this.filtrar();
        },
      });
    this.form
      .get('tipoEleccion')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.form.get('tipoEleccion')?.setValue(value, { emitEvent: false });
          this.form.get('tipoReporte')?.setValue(0, { emitEvent: false });
          // reinicia a primera página manteniendo el pageSize actual
          this.tableInformation.currentPage = 0;
          this.filtrar();
        },
      });
  }

  listarOpciones(){
    const idProcesoPrincipal = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');

    let data: ListarEleccionesResumenGeneralInput = new ListarEleccionesResumenGeneralInput();
    data.activo = 1;
    data.idProceso = idProcesoPrincipal;
    data.tipoFiltro = EnumTipoFiltro.ELECCION;

    this.resumenGeneralApiService
    .listarElecciones(data)
    .pipe(take(1))
    .subscribe({
      next: (result) => {
        if (result.success) {
          this.listaReporte = LISTA_TIPO_REPORTE;
          this.listaEleccion = result.data;
        }
      },
    });
  }

  filtrar() {

    const data: ReporteListarInput = this.getFormData();

    // Siempre solicitar página 0 con el pageSize actual (mínimo por defecto)
    const pageIndex = 0;
    const pageSize = this.tableInformation.pageSize;

    //limpiar
    this.tableInformation.dataSourceTotals = [];
    this.tableInformation.totalRegisters = 0;

    this.isLoadingReportList = true;
    this.reporteApiService
      .listarReportes$(data, pageIndex, pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          const content = result.data?.content;

          if (!content) {
            this.tableInformation.dataSourceTotals = [];
            this.tableInformation.totalPages = 0;
            this.tableInformation.totalRegisters = 0;
            this.tableInformation.currentPage = 0;
            this.tableInformation.pageSize = pageSize;
            return;
          }

          if (result.success) {
            this.tableInformation.dataSourceTotals = this.getFormattedDataSource(content);
            this.tableInformation.totalPages = result.data?.totalPaginas ?? 0;
            this.tableInformation.totalRegisters = result.data?.totalRegistros ?? 0;
            this.tableInformation.currentPage = result.data?.paginaActual ?? 0;
            // Importante: NO pisar el pageSize con content.length
            this.tableInformation.pageSize = pageSize;
          } else {
            console.error('listarReportes$ error');
          }
        },
        error: (err) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          this.tableInformation.dataSourceTotals = [];
          this.tableInformation.totalPages = 0;
          this.tableInformation.totalRegisters = 0;
          this.tableInformation.currentPage = 0;
          this.tableInformation.pageSize = pageSize;
          console.error('listarReportes$ exception', err);
        }
      });
  }

  limpiar(){
    this.form.get('tipoReporte')?.setValue(0, { emitEvent: false });
    this.form.get('tipoEleccion')?.setValue(0, { emitEvent: false });
    this.actualizarCriterioSeleccionado(0, 0);
    // reinicia a primera página y conserva el pageSize actual (mínimo por defecto)
    this.tableInformation.currentPage = 0;
    this.filtrar();
  }

  getFormData() : ReporteListarInput{
    const {
      tipoReporte: reporte,
      tipoEleccion: eleccion,
    } = this.form.value as any;

    let data: ReporteListarInput = new ReporteListarInput();
    data.usuarioConsulta = 'usuario';
    if (reporte != 0) data.tipoReporte = Number(reporte);
    if (eleccion != 0) data.tipoEleccion = Number(eleccion);

    return data;
  }

  public getFormattedDataSource(
    responseData: IReportListData[]
  ): ReportesListadoRow[] {
    return mapDescargasReportRows(responseData);
  }

  descargarDocumento(element: ReportesListadoRow): void {
    this.reporteApiService.descargarZip(String(element.repositorio))
      .pipe(
        take(1),
        catchError(() => of(null))
      )
      .subscribe((result: { url: string; fileName: string } | null) => {
        if (!result || !result.url) {
          this.dialogService.mostrarMensajeAdvertencia(REPORTE_NO_DISPONIBLE,ARCHIVO_NO_DISPONIBLE);
          return;
        }
        const link = document.createElement("a");
        link.href = result.url;
        link.download = result.fileName;
        link.click();
        URL.revokeObjectURL(result.url);
        link.remove();
      });
  }

  public handlePageEvent(pageEvent: PageEvent) {
    const data: ReporteListarInput = this.getFormData();

    // Actualiza estado local con la selección del usuario
    this.tableInformation.currentPage = pageEvent.pageIndex;
    this.tableInformation.pageSize = pageEvent.pageSize;

    this.tableInformation.dataSourceTotals = [];
    this.isLoadingReportList = true;
    this.reporteApiService
      .listarReportes$(data, pageEvent.pageIndex, pageEvent.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          const content = response.data?.content;
          if (!content) {
            this.tableInformation.dataSourceTotals = [];
            this.tableInformation.currentPage = pageEvent.pageIndex;
            this.tableInformation.pageSize = pageEvent.pageSize;
            return;
          }

          if (response.success) {
            this.tableInformation.dataSourceTotals = this.getFormattedDataSource(content);
            this.tableInformation.currentPage = response.data?.paginaActual ?? pageEvent.pageIndex;
            // Mantén el pageSize seleccionado por el usuario
            this.tableInformation.pageSize = pageEvent.pageSize;
          } else {
            console.error('listarReportes$ error');
          }
        },
        error: (err) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          this.tableInformation.dataSourceTotals = [];
          this.tableInformation.currentPage = pageEvent.pageIndex;
          this.tableInformation.pageSize = pageEvent.pageSize;
          console.error('listarReportes$ page exception', err);
        }
      });
  }

  get rangeLabel(): string {
    if (!this.tableInformation) return '';
    const { currentPage, pageSize, totalRegisters } = this.tableInformation;

    const startIndex = currentPage * pageSize;
    const endIndex = Math.min(startIndex + pageSize, totalRegisters);

    return this.translate.instant('Descargas.MostrandoRegistros', {
      inicio: startIndex + 1,
      fin: endIndex,
      total: totalRegisters
    });
  }

  private readonly _bottomSheet = inject(MatBottomSheet);
  openpopupfiltroubigeo(): void {

    const popupData: FiltroEleccionData = {
      tipoEleccion: this.form.get('tipoEleccion')?.value ?? ELECCION_DEFAULT,
      tipoReporte: this.form.get('tipoReporte')?.value ?? ELECCION_DEFAULT,
      mostrarReporte: true
    };

    const ref = this._bottomSheet.open(PopupFiltroEleccionComponent, {
      panelClass: 'menu-movil',
      data: popupData
    });

    ref.afterDismissed().subscribe((valorSeleccionado: FiltroEleccionData | undefined) => {
      if (!valorSeleccionado) return;
      valorSeleccionado.tipoEleccion != null &&
        this.form.get('tipoEleccion')?.setValue(valorSeleccionado.tipoEleccion, { emitEvent: false });

      valorSeleccionado.tipoReporte != null &&
        this.form.get('tipoReporte')?.setValue(valorSeleccionado.tipoReporte, { emitEvent: false });

      this.actualizarCriterioSeleccionado(valorSeleccionado.tipoEleccion, valorSeleccionado.tipoReporte);
      this.filtrar();
    });
  }

  private actualizarCriterioSeleccionado(tipoEleccion?: number, tipoReporte?: number): void {
    const nombreTipoEleccion = this.obtenerNombreTipoEleccion(tipoEleccion);
    const nombreTipoReporte = this.obtenerNombreTipoReporte(tipoReporte);
    if (nombreTipoEleccion && nombreTipoReporte) {
      this.criterioSeleccionado = `${nombreTipoEleccion} | ${nombreTipoReporte}`;
    } else if (nombreTipoEleccion) {
      this.criterioSeleccionado = nombreTipoEleccion;
    } else if (nombreTipoReporte) {
      this.criterioSeleccionado = nombreTipoReporte;
    } else {
      this.translate.get('Descargas.SeleccionaCriterio').subscribe((text: string) => {
        this.criterioSeleccionado = text;
      });
    }
  }

  private obtenerNombreTipoEleccion(valor?: number): string {
    if (valor == null) return '';

    const eleccion = this.listaEleccion?.find((item: TipoEleccion) => item.value === valor);
    return eleccion?.text || '';
  }

  private obtenerNombreTipoReporte(valor?: number): string {
    if (valor == null) return '';
    const reporte = this.listaReporte?.find((item: TipoEleccion) => item.value === valor);
    return reporte?.text ? this.translate.instant(reporte.text) : '';
  }
}
