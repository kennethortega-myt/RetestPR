import { Component, inject, OnInit } from '@angular/core';
import { take } from 'rxjs';
import dayjs from 'dayjs';


import {
  ICONOS,
  IReportConfigData,
  IReportConfigFront,
} from './detalle-reportes.interfaces';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { PopupEditarParametrosComponent } from '../configurar-reportes/popup-editar-parametros/popup-editar-parametros.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { IConfigRequestParams } from '../configurar-reportes/configurar-reportes.interfaces';
import { EnumIdEleccion } from '../../../helpers/enums';
import { DialogService } from '../../../services/dialog.service';
import { SettingReportService } from '../../../services/setting-report.service';
import { LoadService } from '../../../services/load.service_';
import { formatNameElection } from '../../../helpers/funciones';
import { ComponentsModule } from '../../../components/components.module';
import { DialogConfirmComponent } from '../../../components/dialog/dialog-confirm/dialog-confirm.component';

@Component({
  selector: 'app-detalle-reportes',
  templateUrl: './detalle-reportes.component.html',
  styleUrl: './detalle-reportes.component.scss',
  imports: [DatePipe, MatCheckboxModule, MatTooltipModule, ComponentsModule],
})
export class DetalleReportesComponent implements OnInit {

  private readonly dialogService = inject(DialogService);
  private readonly settingReportService = inject(SettingReportService);
  public reportConfigList: IReportConfigFront[] = [];

  constructor(
    public dialog: MatDialog,
    private loadingService: LoadService
  ) { }

  ngOnInit(): void {
    this.listReports();
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

  detenerReporte(item: IReportConfigFront) {
    const nuevoEstado = item.estado === 1 ? 0 : 1;
    const mensaje =
      nuevoEstado === 0
        ? '¿Está seguro de detener la generación automática de reportes?'
        : '¿Está seguro de reanudar la generación automática de reportes?';


    const dialogConfirm = this.dialog.open(DialogConfirmComponent, {
      width: '20%',
      minWidth: '320px',
      maxWidth: '100%',
      data: { mensaje }
    });
    dialogConfirm.afterClosed()
      .subscribe({
        next: (resultConfirm) => {
          if (resultConfirm) {

            const params = {
              id: item.id,
              estado: nuevoEstado
            } as IConfigRequestParams;
            this.settingReportService
              .updateReportConfiguration$(params)
              .pipe(take(1))
              .subscribe((response) => {
                if (response.success) {
                  this.loadingService.hide();
                  this.listReports();
                  this.alertSuccess();
                } else {
                  console.error('Error al actualizar configuración de reporte');
                }
              });
          }
        },
        error: (err) => {
          console.error('Error en confirmación:', err)
          this.dialogService.cerrarUltimoDialog();
        }
      });
  }

  generarReporteManual(item: IReportConfigFront) {
    if (!item.id) return;

    const dialogConfirm = this.dialog.open(DialogConfirmComponent, {
      width: '20%',
      minWidth: '320px',
      maxWidth: '100%',
    });

    dialogConfirm.afterClosed().subscribe({
      next: (resultConfirm) => {
        if (resultConfirm) {
          this.loadingService.show();
          this.settingReportService.generarReporteManual$(item.id!)
            .pipe(take(1))
            .subscribe({
              next: (response) => {
                this.loadingService.hide();

                if (response.data?.success) {
                  this.dialogService.mostrarMensajeExitoConCallback(
                    response.data.mensaje || 'El proceso diario se ejecutó correctamente.',
                    () => {
                      this.dialogService.cerrarUltimoDialog();
                      this.listReports();
                    }
                  );
                } else {
                  this.dialogService.mostrarMensajeError(
                    response.data?.mensaje || 'Ocurrió un error'
                  );
                }
              },
              error: (err) => {
                this.loadingService.hide();
                console.error('Error al generar reporte:', err);
                this.dialogService.mostrarMensajeError('Ocurrió un error');
              }
            });
        }
      },
      error: (err) => {
        console.error('Error en confirmación:', err);
        this.dialogService.cerrarUltimoDialog();
      }
    });
  }

  editarparametros(item: any) {
    const dialogRef = this.dialog.open(PopupEditarParametrosComponent, {
      width: 'auto',
      minWidth: '320px',
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

  listReports() {
    this.settingReportService
      .getAllReportsConfigurations$()
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.reportConfigList = this.getFormattedList(response.data!);
        } else {
          console.error('getAllReportsConfigurations error');
        }
      });
  }

  private getFormattedList(list: IReportConfigData[]): IReportConfigFront[] {
    const currentList = list.map((elem) => {
      return {
        ...elem,
        tipoDeEleccion: formatNameElection(elem.eleccion),
        fechaDeInicio: dayjs(elem.fechaInicio).format('DD/MM/YYYY'),
        horaDeInicio: this.getHora(elem.horaInicio),
        icono: this.getIconFromElectionId(elem.eleccionId),
        periodo: this.getPeriodo(elem),
        tipoDePeriodo: elem.tipoReporte == 1 ? 'TIEMPO' : 'PORCENTAJE'
      } as IReportConfigFront;
    });
    const sortList: IReportConfigFront[] = [];
    currentList.forEach((_, index) => {
      sortList.push(currentList[currentList.length - index - 1]);
    });
    return sortList;
  }

  private getIconFromElectionId(electionId: number): string {
    if (electionId == EnumIdEleccion.ID_ELECCION_PRESIDENCIAL) {
      return ICONOS.presidencial;
    }
    if (electionId == EnumIdEleccion.ID_ELECCION_PARLAMENTO_ANDINO) {
      return ICONOS.parlamento_andino;
    }
    if (electionId == EnumIdEleccion.ID_ELECCION_CONGRESAL ||
      electionId == EnumIdEleccion.ID_ELECCION_DIPUTADOS) {
      return ICONOS.diputados;
    }
    if (electionId == EnumIdEleccion.ID_ELECCION_SENADORES_MULTIPLE) {
      return ICONOS.senadoresDEM;
    }
    if (electionId == EnumIdEleccion.ID_ELECCION_SENADORES_UNICO) {
      return ICONOS.senadoresDEU;
    }
    return '';
  }

  private getPeriodo(elem: IReportConfigData): string {
    if (!elem.tipoGeneracionReporteVal) {
      return '-';
    }
    if (elem.tipoGeneracionReporte == 1) {
      //
      return elem.tipoGeneracionReporteVal < 60
        ? `${elem.tipoGeneracionReporteVal} minutos`
        : elem.tipoGeneracionReporteVal / 60 == 1
          ? `${elem.tipoGeneracionReporteVal / 60} hora`
          : `${elem.tipoGeneracionReporteVal / 60} horas`;
    }
    if (elem.tipoGeneracionReporte == 2) {
      return `${elem.tipoGeneracionReporteVal} %`;
    }
    return '';
  }

  private getHora(hora: string): string {
    const horaArr = hora.split('.');
    const currentHora = horaArr[0];
    return `${currentHora} horas`;
  }
}
