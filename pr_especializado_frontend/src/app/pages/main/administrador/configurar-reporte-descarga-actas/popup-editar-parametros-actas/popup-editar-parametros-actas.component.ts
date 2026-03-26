import { Component, inject, Inject } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { TranslateModule } from '@ngx-translate/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IConfigRequestParams, IPeriodoItem, LISTA_DE_HORAS_DE_INICIO, PERIODO_EN_TIEMPO, PERIODO_IN_TIME } from '../../../../../interfaces/configurar-reportes.interfaces';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter, MatMomentDateModule, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { MY_DATE_FORMATS } from '../../../../../helpers/datetime-helper.common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import dayjs from 'dayjs';
import { DialogService } from '../../../../../services/dialog.service';
import { take } from 'rxjs';
import { convertTo24Hour } from '../../../../../helpers/horas.helper';
import { SettingReportActasService } from '../../../../../services/setting-report-actas.service';
import { ResumenGeneralApiService } from '../../../../../services/resumen-general-api.service';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import 'moment/locale/es';

dayjs.extend(customParseFormat);

interface FormConfigReport {
  fechaInicio: FormControl<Date | null>;
  tipoGeneracionReporteVal: FormControl<number>;
  horaInicio: FormControl<string>;
}

interface IDialogData {
  id: string;
  eleccion: string;
  eleccionId: number;
  fechaInicio: Date;
  horaDeInicio: string;
  horaInicio: string;
  tipoGeneracionReporteVal: number;
}

interface IFormConfigValues {
  fechaInicio: Date | null;
  tipoGeneracionReporteVal: number;
  horaInicio: string;
}

@Component({
  selector: 'app-popup-editar-parametros-actas',
  imports: [
    ReactiveFormsModule,
    MatSelectModule,
    TranslateModule,
    CommonModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatInputModule,
    MatCheckboxModule
  ],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' }
  ],
  templateUrl: './popup-editar-parametros-actas.component.html'
})
export class PopupEditarParametrosActasComponent {

  listaDePeriodos: IPeriodoItem[] = PERIODO_EN_TIEMPO;
  listaDeHorasDeInicios = LISTA_DE_HORAS_DE_INICIO;
  minDate = new Date();
  maxDate = new Date();
  private fechaProceso?: Date;

  private readonly formBuilder = inject(FormBuilder);
  private readonly dialogService = inject(DialogService);

  public initialFormValues?: IFormConfigValues;

  form: FormGroup<FormConfigReport> = this.formBuilder.group({
    fechaInicio: new FormControl<Date | null>(null),
    tipoGeneracionReporteVal: new FormControl<number>(0, { nonNullable: true }),
    horaInicio: new FormControl<string>('', { nonNullable: true }),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private readonly settingReportService: SettingReportActasService,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    public dialogRef?: MatDialogRef<PopupEditarParametrosActasComponent>,
  ) {
    this.loadProcesoActivo();
    const horaLimpia = data.horaDeInicio.replace(' horas', '').trim();
    const fecha = new Date(`2025-01-01T${horaLimpia}`);
    data.horaInicio = fecha.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });

    this.form.patchValue(data);
    this.initialFormValues = this.form.getRawValue() as IFormConfigValues;
  }

  isFormUnchanged(): boolean {
    const currentValues = this.form.getRawValue();
    const initialValues = this.initialFormValues;

    if (!initialValues) return true;

    const isFechaSame = dayjs(currentValues.fechaInicio).isSame(dayjs(initialValues.fechaInicio), 'day');
    const isTipoSame = currentValues.tipoGeneracionReporteVal === initialValues.tipoGeneracionReporteVal;
    const isHoraSame = currentValues.horaInicio === initialValues.horaInicio;

    return isFechaSame && isTipoSame && isHoraSame;
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

          const initialDate = dayjs(initialDateNumber).subtract(2, 'month').toDate();

          this.minDate = initialDate;
          this.fechaProceso = initialDate;
          this.maxDate = dayjs(initialDateNumber).add(2, 'month').toDate();

        } else {
          console.error('loadProcesoActivo error');
        }
      });
  }

  guardarConfiguracion(): void {
    const fechaSeleccionada = this.form.controls.fechaInicio.value;
    const horaSeleccionada = this.form.controls.horaInicio.value;

    if (fechaSeleccionada && horaSeleccionada && this.fechaProceso) {
      const fechaHoraCombinada = dayjs(fechaSeleccionada)
        .hour(dayjs(horaSeleccionada, 'hh:mm A').hour())
        .minute(dayjs(horaSeleccionada, 'hh:mm A').minute())
        .second(0);

      // Forzamos 17:00:00 para la fecha del proceso para evitar colisiones de milisegundos
      const fechaHoraProceso = dayjs(this.fechaProceso)
        .set('hour', 17)
        .set('minute', 0)
        .set('second', 0)
        .set('millisecond', 0);

      if (fechaHoraCombinada.isBefore(fechaHoraProceso)) {
        this.dialogService.mostrarMensajeAdvertencia(
          `La fecha y hora seleccionadas deben ser iguales o posteriores al ${fechaHoraProceso.format('DD/MM/YYYY hh:mm a')}.`
        );
        return;
      }
    }

    this.dialogService.mostrarMensajeConfirmacion(`¿Está seguro de realizar la operación?`).subscribe({
      next: (result) => {
        if (result) {
          const fechaInicioValue = this.form.controls.fechaInicio.value;

          if (!fechaInicioValue) {
            throw new Error('Fecha de inicio requerida');
          }

          const params: IConfigRequestParams = {
            id: this.data.id,
            eleccion: this.data.eleccion,
            eleccionId: this.data.eleccionId,
            fechaInicio: this.getStringDateFromNumberDate(fechaInicioValue),
            horaInicio: convertTo24Hour(this.form.controls.horaInicio.value),
            tipoReporte: 1,
            tipoGeneracionReporte: PERIODO_IN_TIME,
            tipoGeneracionReporteVal: this.form.controls.tipoGeneracionReporteVal.value,
          };

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
        console.error('Error en confirmación:', err);
        this.dialogService.cerrarUltimoDialog();
      },
    });
  }

  private getStringDateFromNumberDate(date: Date): string {
    return dayjs(date).format('YYYY-MM-DD');
  }
}
