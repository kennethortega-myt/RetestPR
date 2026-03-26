import { Component, inject, OnInit } from '@angular/core';
import { filter, finalize, switchMap, take, tap } from 'rxjs';

import {
  IReportConfigData,
  IReportConfigFront,
} from '../../shared/detalle-reportes-list/detalle-reportes-list.interfaces';
import { MatDialog } from '@angular/material/dialog';
import { SettingReportActasService } from '../../../../services/setting-report-actas.service';
import { DialogService } from '../../../../services/dialog.service';
import { PopupEditarParametrosActasComponent } from '../configurar-reporte-descarga-actas/popup-editar-parametros-actas/popup-editar-parametros-actas.component';
import { LoadingService } from '../../../../components/loading/loading.service';
import { IConfigRequestParams } from '../../../../interfaces/configurar-reportes.interfaces';
import { mapReportConfigList } from '../../../../helpers/report-config.helper';
import { DetalleReportesListComponent } from '../../shared/detalle-reportes-list/detalle-reportes-list.component';

@Component({
  selector: 'app-detalle-reportes-actas',
  templateUrl: './detalle-reportes-actas.component.html',
  imports: [DetalleReportesListComponent]

})
export class DetalleReportesActasComponent implements OnInit {
  private readonly dialogService = inject(DialogService);

  public reportConfigList: IReportConfigFront[] = [];

  constructor(
    private readonly settingReportService: SettingReportActasService,
    public readonly dialog: MatDialog,
    private readonly loadingService: LoadingService
  ) { }

  ngOnInit(): void {
    this.listReports();
  }

  editarparametros(item: IReportConfigFront): void {
    const dialogRef = this.dialog.open(PopupEditarParametrosActasComponent, {
      width: '800px', // Ancho del modal
      maxWidth: '98%',
      panelClass: 'modal-resolucion',
      data: item,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadingService.hide();
        this.listReports();
        this.alertSuccess();
      }
    });
  }

  private alertSuccess(): void {
    this.loadingService.hide();
    this.dialogService.mostrarMensajeExitoConCallback(
      `Actualización exitosa`,
      () => {
        this.dialogService.cerrarUltimoDialog();
      }
    );
  }

  listReports() {
    this.settingReportService
      .getAllReportsConfigurations$()
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success && response.data) {
          this.reportConfigList = mapReportConfigList(response.data as IReportConfigData[]) as IReportConfigFront[];
        } else {
          console.error('getAllReportsConfigurations error');
        }
      });
  }

  detenerReporte(item: IReportConfigFront): void {
    const nuevoEstado = item.estado === 1 ? 0 : 1;

    const mensaje = nuevoEstado === 0
      ? '¿Está seguro de detener la generación de los archivos .zip con las imágenes de los documentos electorales?'
      : '¿Está seguro de reanudar la generación de los archivos .zip con las imágenes de los documentos electorales?';

    this.dialogService.mostrarMensajeConfirmacion(mensaje)
      .pipe(
        take(1),
        filter(Boolean), // Solo continúa si confirma
        tap(() => this.loadingService.show()),
        switchMap(() =>
          this.settingReportService.updateReportConfiguration$({
            id: item.id,
            estado: nuevoEstado
          } as IConfigRequestParams)
        ),
        take(1),
        finalize(() => this.loadingService.hide())
      )
      .subscribe({
        next: ({ success }) => {
          if (!success) {
            console.error('Error al actualizar configuración de reporte');
            return;
          }

          this.listReports();
          this.alertSuccess();
        },
        error: (err) => {
          console.error('Error en el proceso:', err);
          this.dialogService.cerrarUltimoDialog();
        }
      });
  }

  /**
   * 2.1 Lógica de Ejecución Manual
   */
  generarReporteManual(item: IReportConfigFront): void {
    if (!item?.id) return;

    const mensaje = '¿Está seguro de generar el reporte?';

    this.dialogService.mostrarMensajeConfirmacion(mensaje)
      .pipe(
        take(1),
        filter(Boolean),
        tap(() => this.loadingService.show()),
        switchMap(() => this.settingReportService.generarReporteManual$(item.id!)),
        take(1),
        finalize(() => this.loadingService.hide())
      )
      .subscribe({
        next: ({ success, data }) => {
          if (!success) {
            this.dialogService.mostrarMensajeError(
              data?.mensaje || 'Ocurrió un error inesperado'
            );
            return;
          }

          this.dialogService.mostrarMensajeExitoConCallback(
            data?.mensaje || 'Proceso completado con éxito.',
            () => {
              this.dialogService.cerrarUltimoDialog();
              this.listReports();
            }
          );
        },
        error: () => {
          this.dialogService.mostrarMensajeError(
            'Error de conexión con el servidor'
          );
        }
      });
  }

}
