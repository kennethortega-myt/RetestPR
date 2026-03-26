import { Component, inject, OnInit } from '@angular/core';
import { take } from 'rxjs';

import { SettingReportService } from '../../../../services/setting-report.service';
import {
  IReportConfigData,
  IReportConfigFront,
} from '../../shared/detalle-reportes-list/detalle-reportes-list.interfaces';
import { MatDialog } from '@angular/material/dialog';
import { PopupEditarParametrosComponent } from '../configurar-reportes/popup-editar-parametros/popup-editar-parametros.component';
import { DialogService } from '../../../../services/dialog.service';
import { LoadingService } from '../../../../components/loading/loading.service';
import { IConfigRequestParams } from '../../../../interfaces/configurar-reportes.interfaces';
import { mapReportConfigList } from '../../../../helpers/report-config.helper';
import { DetalleReportesListComponent } from '../../shared/detalle-reportes-list/detalle-reportes-list.component';

@Component({
  selector: 'app-detalle-reportes',
  templateUrl: './detalle-reportes.component.html',
  imports: [DetalleReportesListComponent],
})
export class DetalleReportesComponent implements OnInit {

  private readonly dialogService = inject(DialogService);
  private readonly settingReportService = inject(SettingReportService);
  public reportConfigList: IReportConfigFront[] = [];

  constructor(
    public dialog: MatDialog,
    private readonly loadingService: LoadingService
  ) { }

  ngOnInit(): void {
    this.listReports();
  }

  private alertSuccess(): void {
    this.dialogService.mostrarMensajeExitoConCallback(
      `Actualización exitosa`,
      () => {
        this.dialogService.cerrarUltimoDialog();
      }
    );
  }

  detenerReporte(item: IReportConfigFront) {
    const nuevoEstado = item.estado === 1 ? 0 : 1;
    const mensaje =
      nuevoEstado === 0
        ? '¿Está seguro de detener la generación automática de reportes?'
        : '¿Está seguro de reanudar la generación automática de reportes?';
    this.dialogService.mostrarMensajeConfirmacion(mensaje).subscribe({
      next: (result) => {
        if (result) {
          const params: Partial<IConfigRequestParams> = {
            id: item.id,
            estado: nuevoEstado
          };
          this.settingReportService
            .updateReportConfiguration$(params as IConfigRequestParams)
            .pipe(take(1))
            .subscribe((response) => {
              if (response.success) {
                this.reportConfigList = this.reportConfigList.map(report =>
                  report.id === item.id ? { ...report, estado: nuevoEstado } : report
                );
                this.alertSuccess();
              }
            });
        }
      },
      error: () => {
        this.dialogService.cerrarUltimoDialog();
      },
    });
  }

  /**
   * 2.1 Lógica de Ejecución Manual
   */
  generarReporteManual(item: IReportConfigFront) {
    if (!item.id) return;
    const mensaje = '¿Está seguro de generar el reporte?';
    this.dialogService.mostrarMensajeConfirmacion(mensaje).subscribe((result) => {
      if (result) {
        this.settingReportService.generarReporteManual$(item.id!)
          .pipe(take(1))
          .subscribe({
            next: (response) => {
              if (response.success) {
                this.dialogService.mostrarMensajeExitoConCallback(
                  response.data?.mensaje || 'Proceso completado con éxito.',
                  () => {
                    this.dialogService.cerrarUltimoDialog();
                    this.listReports();
                  }
                );
              } else {
                this.dialogService.mostrarMensajeError(
                  response.data?.mensaje || 'Ocurrió un error inesperado'
                );
              }
            },
            error: () => {
              this.dialogService.mostrarMensajeError('Error de conexión con el servidor');
            }
          });
      }
    });
  }

  editarparametros(item: IReportConfigFront) {
    const dialogRef = this.dialog.open(PopupEditarParametrosComponent, {
      width: '800px',
      maxWidth: '98%',
      panelClass: 'modal-resolucion',
      data: item,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.listReports();
        this.alertSuccess();
      }
    });
  }

  listReports() {
    this.settingReportService
      .getAllReportsConfigurations$()
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success && response.data) {
          this.reportConfigList = mapReportConfigList(response.data as IReportConfigData[]) as IReportConfigFront[];
        }
      });
  }
}
