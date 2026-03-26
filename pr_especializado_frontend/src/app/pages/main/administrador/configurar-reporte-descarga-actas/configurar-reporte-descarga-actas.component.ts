import {
  Component,
  inject,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {
  IConfigRequestParams,
  IPeriodoItem,
  LISTA_DE_HORAS_DE_INICIO,
  PERIODO_EN_TIEMPO,
  PERIODO_IN_TIME,
} from '../../../../interfaces/configurar-reportes.interfaces';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import dayjs from 'dayjs';
import { Subscription, take } from 'rxjs';
import { ResumenGeneralApiService } from '../../../../services/resumen-general-api.service';
import { TipoEleccion } from '../../../../interfaces/output/tipo-eleccion.model';
import { ListarEleccionesResumenGeneralInput } from '../../../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { getEncryptStorageEleccionValue } from '../../../../helpers/encrypt-storage-eleccion';
import { EnumTipoFiltro } from '../../../../helpers/enums';
import { DialogService } from '../../../../services/dialog.service';
import { LoadingService } from '../../../../components/loading/loading.service';
import { SettingReportActasService } from '../../../../services/setting-report-actas.service';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { ReporteConfigFormComponent } from '../../shared/reporte-config-form/reporte-config-form.component';

dayjs.extend(customParseFormat);
@Component({
  selector: 'app-configurar-reporte-descarga-actas',
  imports: [
    ReporteConfigFormComponent
  ],
  standalone: true,
  templateUrl: './configurar-reporte-descarga-actas.component.html'
})

export class ConfigurarReporteDescargaActasComponent implements OnInit, OnDestroy {
  private readonly dialogService = inject(DialogService);

  // Solo períodos de tiempo
  listaDePeriodos: IPeriodoItem[] = PERIODO_EN_TIEMPO;
  listaDeHorasDeInicios = LISTA_DE_HORAS_DE_INICIO;
  listaEleccion: TipoEleccion[] = [];
  private readonly formBuilder = inject(FormBuilder);

  form = this.formBuilder.group({
    tipoDeEleccion: new FormControl<number | null>(null, [Validators.required]),
    fechaDeInicioDeGeneracion: new FormControl<Date | null>(null, [Validators.required]),
    periodoDeGeneracion: new FormControl<number | null>(null, [Validators.required]),
    horaDeInicio: new FormControl<string | null>(null, [Validators.required]),
  });

  private readonly subscriptions: Subscription[] = [];
  private readonly reportConfigId?: string;
  minDate = new Date();
  maxDate = new Date();
  private fechaProceso?: Date;

  constructor(
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly settingReportService: SettingReportActasService,
    private readonly loadingService: LoadingService,
  ) { }

  ngOnInit(): void {
    this.loadProcesoActivo();
    this.listarOpciones();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((elem) => elem.unsubscribe());
  }

  listarOpciones(): void {
    const idProcesoPrincipal = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');

    let data: ListarEleccionesResumenGeneralInput = new ListarEleccionesResumenGeneralInput();
    data.activo = 1;
    data.idProceso = idProcesoPrincipal;
    data.tipoFiltro = EnumTipoFiltro.ELECCION;

    this.resumenGeneralApiService
      .listarEleccionesParaConfigurarReporteActas(data)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaEleccion = result.data;
          }
        },
      });
  }

  private alertSuccess(): void {
    this.loadingService.hide();
    this.dialogService.mostrarMensajeExitoConCallback(
      `Registro exitoso`,
      () => {
        this.dialogService.cerrarUltimoDialog();
      }
    );
  }

  public guardarConfiguracion(): void {
    const fechaSeleccionada = this.fechaDeInicioDeGeneracionCtrl.value;
    const horaSeleccionada = this.horaDeInicioCtrl.value;

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
          const currentElection = this.listaEleccion.find(
            (ele) => ele.value == this.tipoDeEleccionCtrl.value
          );

          const fechaInicioValue = this.fechaDeInicioDeGeneracionCtrl.value;

          if (!fechaInicioValue) {
            throw new Error('Fecha de inicio requerida');
          }

          const params = {
            eleccion: currentElection?.text,
            eleccionId: currentElection?.value,
            fechaInicio: this.getStringDateFromNumberDate(fechaInicioValue),
            horaInicio: this.convertirA24Horas(this.horaDeInicioCtrl.value),
            tipoReporte: 1,
            tipoGeneracionReporte: PERIODO_IN_TIME, // Siempre por tiempo
            tipoGeneracionReporteVal: this.periodoDeGeneracionCtrl.value,
          } as IConfigRequestParams;

          if (this.reportConfigId) {
            params.id = this.reportConfigId;
            this.settingReportService
              .updateReportConfiguration$(params)
              .pipe(take(1))
              .subscribe((response) => {
                if (response.success) {
                  this.listarOpciones();

                  this.form.reset();

                  this.alertSuccess();
                } else {
                  console.error('guardarConfiguracion updateReportConfiguration error');
                }
              });
          } else {
            this.settingReportService
              .saveReportConfiguration$(params)
              .pipe(take(1))
              .subscribe((response) => {
                this.loadingService.hide();
                if (response.success) {
                  this.listarOpciones();

                  this.form.reset();

                  this.alertSuccess();
                } else {
                  console.error('guardarConfiguracion saveReportConfiguration error');
                }
              });
          }
        }
      },
      error: (err) => {
        console.error('Error en confirmación:', err)
        this.dialogService.cerrarUltimoDialog();
      },
    });
  }

  public get isValidForm(): boolean {
    return this.form.valid && this.listaEleccion.length > 0;
  }

  // MÉTODOS PRIVADOS

  private loadProcesoActivo(): void {
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

  private convertirA24Horas(hora12h: any): string {
    if (!hora12h) return '';
    return dayjs(hora12h, 'hh:mm A').format('HH:mm:ss');
  }

  private disableFechaDeInicio(): void {
    this.fechaDeInicioDeGeneracionCtrl.disable();
  }

  private getNumberDateFromStringDate(fechaRegistrada: string): Date {
    return dayjs(fechaRegistrada).toDate();
  }

  private getStringDateFromNumberDate(date: Date): string {
    return dayjs(date).format('YYYY-MM-DD');
  }

  // GET CONTROLES

  private get tipoDeEleccionCtrl() {
    return this.form.controls.tipoDeEleccion;
  }

  private get fechaDeInicioDeGeneracionCtrl() {
    return this.form.controls.fechaDeInicioDeGeneracion;
  }

  private get periodoDeGeneracionCtrl() {
    return this.form.controls.periodoDeGeneracion;
  }

  private get horaDeInicioCtrl() {
    return this.form.controls.horaDeInicio;
  }
}
