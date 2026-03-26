import { Component, inject, Inject } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { IConfigRequestParams, IPeriodoItem, LISTA_DE_HORAS_DE_INICIO, PERIODO_EN_TIEMPO, PERIODO_IN_TIME } from '../configurar-reportes.interfaces';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import dayjs from 'dayjs';
import { take } from 'rxjs';
import { MY_DATE_FORMATS } from '../../../../helpers/datetime-helper.common';
import { convertTo24Hour, formatTo12Hour } from '../../../../helpers/horas.helper';
import { DialogService } from '../../../../services/dialog.service';
import { SettingReportService } from '../../../../services/setting-report.service';
import { ResumenGeneralApiService } from '../../../../services/resumen-general-api.service';
import { DialogConfirmComponent } from '../../../../components/dialog/dialog-confirm/dialog-confirm.component';

export interface IFormConfigValues {
  fechaInicio: Date | null;
  tipoGeneracionReporteVal: number;
  horaInicio: string;
}

@Component({
  selector: 'app-popup-editar-parametros',
  imports: [
    ReactiveFormsModule,
    MatSelectModule,
    CommonModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatInputModule,
    MatCheckboxModule
  ],
  templateUrl: './popup-editar-parametros.component.html',
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS }
  ]
})
export class PopupEditarParametrosComponent {

  public listaDePeriodos: IPeriodoItem[] = PERIODO_EN_TIEMPO;
  public listaDeHorasDeInicios = LISTA_DE_HORAS_DE_INICIO;
  public minDate = new Date();
  public maxDate = new Date();
  public fechaProceso?: Date;
  private initialFormValues: IFormConfigValues | null = null;

  private formBuilder = inject(FormBuilder);
  private readonly dialogService = inject(DialogService);

  public form = this.formBuilder.group({
    fechaInicio: new FormControl<Date | null>(null),
    tipoGeneracionReporteVal: new FormControl<number>(0),
    horaInicio: new FormControl<string>(''),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    private resumenGeneralApiService: ResumenGeneralApiService,
    private settingReportService: SettingReportService,
    public dialogRef?: MatDialogRef<PopupEditarParametrosComponent>
  ) {
    this.loadProcesoActivo();
    const horaLimpia = data.horaDeInicio.replace(' horas', '').trim();
    const fecha = new Date(`2025-01-01T${horaLimpia}`);
    // Use helper to format into 12-hour AM/PM string (keeps behavior consistent and testable)
    data.horaInicio = formatTo12Hour(fecha);
    this.form.patchValue(data);
    this.initialFormValues = this.form.getRawValue() as IFormConfigValues;
  }

  cerrar(): void {
    this.dialogRef?.close(false);
  }

  private loadProcesoActivo() {
    this.resumenGeneralApiService
      .getProcesoElectoralActivo$()
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          const initialDateNumber = response.data?.fechaConvocatoria!;

          const initialDate = dayjs(initialDateNumber).toDate();

          this.fechaProceso = initialDate;
          this.minDate = dayjs(initialDate).subtract(2, 'month').toDate();
          this.maxDate = dayjs(initialDate).add(2, 'month').toDate();

        } else {
          console.error('loadProcesoActivo error');
        }
      });
  }


  public guardarConfiguracion(): void {

    // Validación por minuto (fecha y hora exacta)
    const fechaSeleccionada = dayjs(this.form.controls['fechaInicio'].value);
    const horaStr = convertTo24Hour(this.form.controls['horaInicio'].value ?? ''); // HH:mm
    const [horas, minutos] = horaStr.split(':');

    const fechaHoraConfig = fechaSeleccionada
      .hour(parseInt(horas))
      .minute(parseInt(minutos))
      .second(0);

    const fechaHoraProceso = dayjs(this.fechaProceso)
      .set('hour', 17)
      .set('minute', 0)
      .set('second', 0)
      .set('millisecond', 0);
    if (fechaHoraConfig.isBefore(fechaHoraProceso.subtract(2, 'month'))) {
      this.dialogService.mostrarMensajeAdvertencia(
        `La fecha y hora seleccionadas deben ser iguales o posteriores al ${fechaHoraProceso.subtract(2, 'month').format('DD/MM/YYYY hh:mm a')}.`
      );
      return;
    }

    const dialogConfirm = this.dialog.open(DialogConfirmComponent, {
      width: '20%',
      minWidth: '320px',
      maxWidth: '100%',
    });
    dialogConfirm.afterClosed()
      .subscribe({
        next: (resultConfirm) => {
          if (resultConfirm) {
            const params = {
              id: this.data.id,
              eleccion: this.data.eleccion,
              eleccionId: this.data.eleccionId,
              fechaInicio: this.getStringDateFromNumberDate(this.form.controls['fechaInicio'].value!),
              horaInicio: horaStr,
              tipoReporte: 1,
              tipoGeneracionReporte: PERIODO_IN_TIME, // Siempre por tiempo
              tipoGeneracionReporteVal: this.form.controls['tipoGeneracionReporteVal'].value,
            } as IConfigRequestParams;

            this.settingReportService
              .updateReportConfiguration$(params)
              .pipe(take(1))
              .subscribe((response) => {
                if (response.success) {
                  this.dialogRef?.close(true);
                  this.form.reset();
                } else {
                  console.error('guardarConfiguracion updateReportConfiguration error');
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

  private getStringDateFromNumberDate(date: Date): string {
    return dayjs(date).format('YYYY-MM-DD');
  }

  isFormUnchanged(): boolean {
    const currentValues = this.form.getRawValue() as IFormConfigValues;
    const initialValues = this.initialFormValues;
    if (!initialValues) return true;

    const isFechaSame = dayjs(currentValues.fechaInicio).isSame(dayjs(initialValues.fechaInicio), 'day');
    const isTipoSame = currentValues.tipoGeneracionReporteVal === initialValues.tipoGeneracionReporteVal;
    const isHoraSame = currentValues.horaInicio === initialValues.horaInicio;

    return isFechaSame && isTipoSame && isHoraSame;
  }
}
