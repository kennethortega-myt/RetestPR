import 'moment/locale/es';
import {
  Component,
  OnInit,
  inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  MatOptionModule,
} from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDatepickerModule } from '@angular/material/datepicker';

import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { take } from 'rxjs';

import {
  IConfigRequestParams,
  IPeriodoItem,
  LISTA_DE_HORAS_DE_INICIO,
  PERIODO_EN_TIEMPO,
  PERIODO_IN_TIME,
} from './configurar-reportes.interfaces';
import { ResumenGeneralApiService } from '../../../services/resumen-general-api.service';
import { SettingReportService } from '../../../services/setting-report.service';
import { DialogService } from '../../../services/dialog.service';
import { LoadService } from '../../../services/load.service_';
import { MY_DATE_FORMATS } from '../../../helpers/datetime-helper.common';
import { TipoEleccion } from '../../../interfaces/output/tipo-eleccion.model';
import { ListarEleccionesResumenGeneralInput } from '../../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { EnumTipoFiltro } from '../../../helpers/enums';
import { DialogConfirmComponent } from '../../../components/dialog/dialog-confirm/dialog-confirm.component';

dayjs.extend(customParseFormat);

@Component({
  selector: 'app-configurar-reportes',
  standalone: true,
  templateUrl: './configurar-reportes.component.html',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    MatTooltipModule,
    MatDatepickerModule,
  ],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },
  ],
  styles: [`
    .form-group {
      position: relative;
    }
    .error-eleccion {
      color: #DF0A14;
      font-size: 11px;
      position: absolute;
      bottom: -12px;
      margin-bottom: 8px;
      left: 0;
      display: block;
    }
  `]
})
export class ConfigurarReportesComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly dialog = inject(MatDialog);
  private readonly resumenService = inject(ResumenGeneralApiService);
  private readonly settingReportService = inject(SettingReportService);
  private readonly dialogService = inject(DialogService);
  private readonly loadingService = inject(LoadService);
  private readonly dateAdapter = inject(DateAdapter<Date>);

  readonly listaDePeriodos: IPeriodoItem[] = PERIODO_EN_TIEMPO;
  readonly listaDeHorasDeInicios = LISTA_DE_HORAS_DE_INICIO;

  listaEleccion: TipoEleccion[] = [];
  idProceso = 0;
  reportConfigId?: string;
  fechaProceso?: Date;

  minDate = new Date();
  maxDate = new Date();

  readonly form = this.fb.group({
    tipoDeEleccion: new FormControl<number | null>(null, [Validators.required]),
    fechaDeInicioDeGeneracion: new FormControl<Date | null>(null, [Validators.required]),
    periodoDeGeneracion: new FormControl<number | null>(null, [Validators.required]),
    horaDeInicio: new FormControl<string | null>(null, [Validators.required]),
  });

  constructor() {
    this.dateAdapter.setLocale('es');
  }

  ngOnInit(): void {
    this.loadProcesoActivo();
  }

  listarOpciones(): void {
    const input: ListarEleccionesResumenGeneralInput = {
      activo: 1,
      idProceso: this.idProceso,
      tipoFiltro: EnumTipoFiltro.ELECCION,
    };

    this.resumenService
      .listarEleccionesParaConfigurarReporte(input)
      .pipe(take(1))
      .subscribe((resp) => {
        if (resp.success) {
          this.listaEleccion = resp.data ?? [];
        }
      });
  }

  guardarConfiguracion(): void {
    if (!this.isValidForm) return;

    // Validación por minuto (fecha y hora exacta)
    const fechaSeleccionada = dayjs(this.fechaCtrl.value);
    const horaStr = this.to24Hours(this.horaCtrl.value); // HH:mm
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

    this.dialog
      .open(DialogConfirmComponent, {
        width: '20%',
        minWidth: '320px',
        maxWidth: '100%',
      })
      .afterClosed()
      .pipe(take(1))
      .subscribe((confirmado) => {
        if (!confirmado) return;
        this.persistirConfiguracion();
      });
  }

  get isValidForm(): boolean {
    return this.form.valid && this.listaEleccion.length > 0;
  }

  private loadProcesoActivo(): void {
    this.resumenService
      .getProcesoElectoralActivo$()
      .pipe(take(1))
      .subscribe((resp) => {
        if (!resp.success) return;

        const { id, fechaConvocatoria } = resp.data!;
        this.idProceso = id;

        this.fechaProceso = dayjs(fechaConvocatoria).toDate();
        this.minDate = dayjs(fechaConvocatoria).subtract(2, 'month').toDate();
        this.maxDate = dayjs(fechaConvocatoria).add(2, 'month').toDate();

        this.listarOpciones();
      });
  }

  private persistirConfiguracion(): void {
    const eleccion = this.listaEleccion.find(
      (e) => e.value === this.tipoDeEleccionCtrl.value
    );

    const params: IConfigRequestParams = {
      id: this.reportConfigId,
      eleccion: eleccion?.text!,
      eleccionId: eleccion?.value!,
      fechaInicio: this.formatDate(this.fechaCtrl.value!),
      horaInicio: this.to24Hours(this.horaCtrl.value),
      tipoReporte: 1,
      tipoGeneracionReporte: PERIODO_IN_TIME,
      tipoGeneracionReporteVal: this.periodoCtrl.value!,
    };

    const request$ = this.reportConfigId
      ? this.settingReportService.updateReportConfiguration$(params)
      : this.settingReportService.saveReportConfiguration$(params);

    request$.pipe(take(1)).subscribe((resp) => {
      if (resp.success) {
        this.form.reset();
        this.listarOpciones();
        this.alertSuccess();
      }
    });
  }

  private alertSuccess(): void {
    this.loadingService.hide();
    this.dialogService.mostrarMensajeExitoConCallback(
      'Registro exitoso',
      () => this.dialogService.cerrarUltimoDialog()
    );
  }

  private to24Hours(value: string | null): string {
    if (!value) return '';

    // Limpiamos los puntos del a.m. / p.m. y pasamos a mayúsculas
    const cleanValue = value.replace(/\./g, '').toUpperCase();

    return dayjs(cleanValue, 'hh:mm A').format('HH:mm');
  }

  private formatDate(date: Date): string {
    return dayjs(date).format('YYYY-MM-DD');
  }

  private get tipoDeEleccionCtrl() {
    return this.form.controls.tipoDeEleccion;
  }

  private get fechaCtrl() {
    return this.form.controls.fechaDeInicioDeGeneracion;
  }

  private get periodoCtrl() {
    return this.form.controls.periodoDeGeneracion;
  }

  private get horaCtrl() {
    return this.form.controls.horaDeInicio;
  }
}
