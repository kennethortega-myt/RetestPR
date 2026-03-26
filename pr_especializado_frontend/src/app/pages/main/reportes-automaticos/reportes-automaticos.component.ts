import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { catchError, of, Subject, take, takeUntil } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { MatBottomSheet } from '@angular/material/bottom-sheet';

import {
  IReportListData,
  ReporteApiService,
} from '../../../services/reporte-api.service';
import {
  ARCHIVO_NO_DISPONIBLE,
  REPORTE_NO_DISPONIBLE,
  TAMANIO_PAGINA,
  TIME_BETWWEN_MODULES,
} from '../../../helpers/constantes';
import { TipoEleccion } from '../../../interfaces/output/tipo-eleccion.model';
import { getEncryptStorageEleccionValue } from '../../../helpers/encrypt-storage-eleccion';
import { ListarEleccionesResumenGeneralInput } from '../../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { ELECCION_DEFAULT, EnumTipoFiltro } from '../../../helpers/enums';
import { ResumenGeneralApiService } from '../../../services/resumen-general-api.service';
import { ReporteListarInput } from '../../../interfaces/input/reporte/reporte-listar-input';
import { LoadingService } from '../../../components/loading/loading.service';
import { PopupFiltroEleccionComponent } from '../../../components/popup-filtro-eleccion/popup-filtro-eleccion.component';
import { FiltroEleccionData } from '../../../interfaces/output/filtro/eleccion.model';
import {
  mapAutomaticosReportRows,
  ReportesListadoRow,
  ReportesListadoTableInfo,
} from '../../../helpers/reportes-list.helper';
import { ReportesListadoComponent } from '../shared/reportes-listado/reportes-listado.component';
import { DialogService } from '../../../services/dialog.service';

@Component({
  selector: 'app-reportes-automaticos',
  templateUrl: './reportes-automaticos.component.html',
  imports: [ReportesListadoComponent],
})
export class ReportesAutomaticosComponent implements OnInit, OnDestroy {
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

  private readonly formBuilder = inject(FormBuilder);
  private readonly _bottomSheet = inject(MatBottomSheet);
  private readonly reporteApiService = inject(ReporteApiService);
  private readonly resumenGeneralApiService = inject(ResumenGeneralApiService);
  private readonly loadingService = inject(LoadingService);
  private readonly dialogService = inject(DialogService);
  criterioSeleccionado: string = 'SELECCIONA EL CRITERIO DE BÚSQUEDA';

  form = this.formBuilder.group({
    tipoEleccion: [{ value: 0, disabled: false }],
    tipoReporte: [{ value: 0, disabled: false }],
  });

  listaEleccion: TipoEleccion[] = [];

  public tableInformation: ReportesListadoTableInfo = {
    dataSourceTotals: [],
    pageSize: TAMANIO_PAGINA,
    totalPages: 0,
    totalRegisters: 0,
    currentPage: 0,
  } as ReportesListadoTableInfo;

  public isLoadingReportList = false;
  public isFirstRequest = true;
  isContentReady = false;

  private readonly destroy$ = new Subject<boolean>();

  ngOnInit(): void {
    this.loadingService.show();
    this.isContentReady = false;
    setTimeout(() => {
      this.listarOpciones();
      this.valuesChanges();
      this.loadingService.hide();
      this.loadReportesAutomaticos();
    }, TIME_BETWWEN_MODULES);
  }

  valuesChanges(): void {
    this.form
      .get('tipoEleccion')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.form.get('tipoEleccion')?.setValue(value, { emitEvent: false });
          this.loadReportesAutomaticos();
        },
      });
  }

  listarOpciones(): void {
    const idProcesoPrincipal = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');

    const data = new ListarEleccionesResumenGeneralInput();
    data.activo = 1;
    data.idProceso = idProcesoPrincipal;
    data.tipoFiltro = EnumTipoFiltro.ELECCION;

    this.resumenGeneralApiService
      .listarElecciones(data)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaEleccion = result.data;
          }
        },
      });
  }


  public descargarDocumentoAutomaticos(element: ReportesListadoRow): void {
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

  public loadReportesAutomaticos(): void {
    setTimeout(() => {
      const data: ReporteListarInput = this.getFormData();

      this.tableInformation.dataSourceTotals = [];
      this.isLoadingReportList = true;
      this.reporteApiService
        .listarReportesAutomaticos$(data)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.isFirstRequest = false;
            this.isLoadingReportList = false;
            const content = response.data?.content;
            if (!content) {
              this.tableInformation.dataSourceTotals = [];
              this.tableInformation.totalPages = 0;
              this.tableInformation.totalRegisters = 0;
              this.tableInformation.currentPage = 0;
              this.tableInformation.pageSize = TAMANIO_PAGINA;
              return;
            }
            if (response.success) {
              this.tableInformation.dataSourceTotals = this.getFormattedDataSource(content);
              this.tableInformation.totalPages = response.data?.totalPaginas ?? 0;
              this.tableInformation.totalRegisters = response.data?.totalRegistros ?? 0;
              this.tableInformation.currentPage = 0;
              this.tableInformation.pageSize = TAMANIO_PAGINA;
            } else {
              this.tableInformation.totalRegisters = 0;
              console.error('listarReportesAutomaticos$ error');
            }
          },
          error: (err) => {
            this.isFirstRequest = false;
            this.isLoadingReportList = false;
            this.tableInformation.dataSourceTotals = [];
            this.tableInformation.totalPages = 0;
            this.tableInformation.totalRegisters = 0;
            this.tableInformation.currentPage = 0;
            this.tableInformation.pageSize = TAMANIO_PAGINA;
            console.error('listarReportesAutomaticos$ exception', err);
          }
        });
      this.isContentReady = true;
    }, 0);
  }

  limpiar(): void {
    this.form.get('tipoEleccion')?.setValue(0, { emitEvent: false });
    this.actualizarCriterioSeleccionado(0);
    this.loadReportesAutomaticos();
  }

  getFormData(): ReporteListarInput {
    const { tipoEleccion: eleccion } = this.form.value;

    const data = new ReporteListarInput();
    data.usuarioConsulta = 'automatico';
    if (eleccion != 0) data.tipoEleccion = Number(eleccion);

    return data;
  }

  public handlePageEvent(pageEvent: PageEvent): void {
    const data: ReporteListarInput = this.getFormData();

    this.tableInformation.dataSourceTotals = [];
    this.isLoadingReportList = true;
    this.reporteApiService
      .listarReportesAutomaticos$(data, pageEvent.pageIndex, TAMANIO_PAGINA)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          const content = response.data?.content;
          if (!content) {
            this.tableInformation.dataSourceTotals = [];
            this.tableInformation.currentPage = pageEvent.pageIndex;
            this.tableInformation.pageSize = TAMANIO_PAGINA;
            return;
          }
          if (response.success) {
            this.tableInformation.dataSourceTotals = this.getFormattedDataSource(content);
            this.tableInformation.currentPage = response.data?.paginaActual ?? 0;
            this.tableInformation.pageSize = TAMANIO_PAGINA;
          } else {
            console.error('listarReportesAutomaticos$ error');
          }
        },
        error: (err) => {
          this.isFirstRequest = false;
          this.isLoadingReportList = false;
          this.tableInformation.dataSourceTotals = [];
          this.tableInformation.currentPage = pageEvent.pageIndex;
          this.tableInformation.pageSize = TAMANIO_PAGINA;
          console.error('listarReportesAutomaticos$ page exception', err);
        }
      });
  }

  private getFormattedDataSource(responseData: IReportListData[]): ReportesListadoRow[] {
    return mapAutomaticosReportRows(responseData);
  }

  openModalBusqueda(): void {
    const popupData: FiltroEleccionData = {
      tipoEleccion: this.form.get('tipoEleccion')?.value ?? ELECCION_DEFAULT,
      mostrarReporte: false
    };

    const ref = this._bottomSheet.open(PopupFiltroEleccionComponent, {
      panelClass: 'menu-movil',
      data: popupData
    });

    ref.afterDismissed().subscribe((valorSeleccionado) => {
      if (!valorSeleccionado) return;
      if (valorSeleccionado.tipoEleccion != null) {
        this.form.get('tipoEleccion')?.setValue(valorSeleccionado.tipoEleccion);
        this.actualizarCriterioSeleccionado(valorSeleccionado.tipoEleccion)
      }
    });
  }

  private actualizarCriterioSeleccionado(tipoEleccion?: number): void {
    const nombreTipoEleccion = this.obtenerNombreTipoEleccion(tipoEleccion);
    if (nombreTipoEleccion) {
      this.criterioSeleccionado = nombreTipoEleccion;
    } else {
      this.criterioSeleccionado = 'SELECCIONA EL CRITERIO DE BÚSQUEDA';
    }
  }

  private obtenerNombreTipoEleccion(valor?: number): string {
    if (valor == null) return '';

    const eleccion = this.listaEleccion?.find((item: TipoEleccion) => item.value === valor);
    return eleccion?.text || '';
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
}
