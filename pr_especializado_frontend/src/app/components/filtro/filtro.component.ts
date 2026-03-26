import {
  Component,
  EventEmitter,
  inject,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { pairwise, startWith, Subject, Subscription, take, takeUntil } from 'rxjs';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { FiltroBehaviorService } from './filtro.behavior.service';
import {
  EnumTipoFiltro,
  EnumIdAmbito,
  EnumIdNivelUbigeo,
  EnumIdEleccionDistritoElectoral,
  AMBITO_DEFAULT,
  EnumIdDistrito,
  ELECCION_DEFAULT,
} from '../../helpers/enums';
import { UbigeoLocalVotacionInput } from '../../interfaces/input/filtro-ubigeo/ubigeo-local-votacion-input';
import { UbigeoNivel01Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-01-input';
import { UbigeoNivel02Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-02-input';
import { UbigeoNivel03Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-03-input';
import { ListarEleccionesResumenGeneralInput } from '../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { AmbitoGeografico } from '../../interfaces/output/ambito-geografico.model';
import { LocalVotacion } from '../../interfaces/output/local-votacion.model';
import { TipoEleccion } from '../../interfaces/output/tipo-eleccion.model';
import { Ubigeo } from '../../interfaces/output/ubigeo.model';
import { ResumenGeneralApiService } from '../../services/resumen-general-api.service';
import { UbigeoApiService } from '../../services/ubigeo-api.service';
import { FiltroModel } from '../../interfaces/filtro.model';
import { TablaService } from '../../services/tabla.service';
import { getEncryptStorageEleccionValue } from '../../helpers/encrypt-storage-eleccion';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { PopupFiltroUbigeoComponent } from '../../../app/components/popup-filtro-ubigeo/popup-filtro-ubigeo.component';
import { PopupFiltroEleccionComponent } from '../../../app/components/popup-filtro-eleccion/popup-filtro-eleccion.component';
import { MENSAJE_ORIGINAL, VALOR_TODOS } from '../../helpers/constantes';
import { FiltroUbigeoData, FiltroEleccionData } from '../../interfaces/output/filtro/eleccion.model';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MatRippleModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-filtro',
  templateUrl: './filtro.component.html',
  standalone: true,
  imports: [CommonModule, MatSelectModule, MatRippleModule, TranslateModule, ReactiveFormsModule],
})
export class FiltroComponent implements OnInit, OnDestroy {
  listaEleccion: TipoEleccion[] = [];
  listaDistritoElectoral: Ubigeo[] = [];
  listaAmbitoGeografico: AmbitoGeografico[] = [
    {
      value: 0,
      text: 'TODOS',
    },
    {
      value: 1,
      text: 'PERÚ',
    },
    {
      value: 2,
      text: 'EXTRANJERO',
    },
  ];
  listaNivelUno: Ubigeo[] = [];
  listaNivelDos: Ubigeo[] = [];
  listaNivelTres: Ubigeo[] = [];
  listaLocalVotacion: LocalVotacion[] = [];

  private readonly formBuilder = inject(FormBuilder);
  form = this.formBuilder.group({
    tipoEleccion: [{ value: 0, disabled: false }],
    distritoElectoral: [{ value: 30, disabled: false }],
    ambitoGeografico: [{ value: AMBITO_DEFAULT, disabled: false }],
    nivelUbigeoUno: [{ value: '0', disabled: true }],
    nivelUbigeoDos: [{ value: '0', disabled: true }],
    nivelUbigeoTres: [{ value: '0', disabled: true }],
    localVotacion: [{ value: 0, disabled: true }],
  });

  nombreEtiquetaDistritoElectoral: string = 'DISTRITO ELECTORAL';
  nombreEtiquetaNivelUno: string = 'REGIÓN';
  nombreEtiquetaNivelDos: string = 'PROVINCIA';
  nombreEtiquetaNivelTres: string = 'DISTRITO';
  mensaje: string = '';

  @Output() mensajeEvent = new EventEmitter<string>();
  @Output() filtrarEvent = new EventEmitter<FiltroModel>();
  @Output() cambiosFiltrosDinamicosEvent = new EventEmitter<FiltroModel>();
  @Output() limpiarFiltrarEvent = new EventEmitter<{filtro: FiltroModel; absolute: boolean}>();

  subscriptions$?: Subscription;
  subscriptions1$?: Subscription;
  subscriptions2$?: Subscription;

  private readonly destroy$ = new Subject<void>();

  eleccionesDistritoElectoral = [EnumIdEleccionDistritoElectoral.ID_ELECCION_DIPUTADOS,EnumIdEleccionDistritoElectoral.ID_ELECCION_SENADORES_MULTIPLE]
  filtroSeleccionado: string = 'TEleccion';

  constructor(
    private tablaService: TablaService,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly ubigeoApiService: UbigeoApiService,
    private readonly filtroBehaviorService: FiltroBehaviorService,
    private readonly translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.listaAmbitoGeografico[0].text = this.translate.instant('TODOS');

    this.init();
    this.listarElecciones();
    this.valuesChanges();
    this.hideUbigeosNivel123();
    this.translate.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.listaAmbitoGeografico[0].text = this.translate.instant('TODOS');
    });
  }

  init() {
    this.tablaService.ambitoGeograficoSeleccionado = {
      value: AMBITO_DEFAULT,
      text: this.listaAmbitoGeografico.find((x) => x.value === AMBITO_DEFAULT)
        ?.text ?? '',
    };
  }

  esEleccionParaDistritoElectoral(): boolean{
    let idEleccion = Number(this.form.get('tipoEleccion')?.value);

    return this.eleccionesDistritoElectoral.includes(idEleccion);
  }

  listarElecciones(): void {
    const idProcesoPrincipal = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');

    let data: ListarEleccionesResumenGeneralInput =
      new ListarEleccionesResumenGeneralInput();
    data.activo = 1;
    data.idProceso = idProcesoPrincipal //1;
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

  listarDistritosElectorales(): void {
    this.ubigeoApiService
      .listarDistritosElectorales()
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaDistritoElectoral = result.data;
          }
        },
      });
  }

  listarUbigeoNivel01(): void {
    let param: UbigeoNivel01Input = new UbigeoNivel01Input();

    param.idAmbitoGeografico = Number(this.form.get('ambitoGeografico')?.value);
    param.idEleccion = Number(this.form.get('tipoEleccion')?.value);

    if (param.idEleccion == ELECCION_DEFAULT) {
      return;
    }

    this.ubigeoApiService
      .listarNivel01(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaNivelUno = (result.data ?? []).sort((a: any, b: any) =>
              (a.text ?? "").localeCompare(b.text, 'es', { sensitivity: 'base' })
            );
          }
          this.form.get('nivelUbigeoUno')?.setValue('0', { emitEvent: false });
        },
      });
  }

  listarUbigeoNivel02(): void {
    let param: UbigeoNivel02Input = new UbigeoNivel02Input();
    param.idAmbitoGeografico = Number(this.form.get('ambitoGeografico')?.value);
    param.idEleccion = Number(this.form.get('tipoEleccion')?.value);
    param.idUbigeoDepartamento = Number(this.form.get('nivelUbigeoUno')?.value);

    this.ubigeoApiService
      .listarNivel02(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaNivelDos = (result.data ?? []).sort((a: any, b: any) =>
              (a.text ?? "").localeCompare(b.text, 'es', { sensitivity: 'base' })
            );
          }
          this.form.get('nivelUbigeoDos')?.setValue('0', { emitEvent: false });
        },
      });
  }

  listarUbigeoNivel03(): void {
    let param: UbigeoNivel03Input = new UbigeoNivel03Input();
    param.idAmbitoGeografico = Number(this.form.get('ambitoGeografico')?.value);
    param.idEleccion = Number(this.form.get('tipoEleccion')?.value);
    param.idUbigeoProvincia = Number(this.form.get('nivelUbigeoDos')?.value);

    this.ubigeoApiService
      .listarNivel03(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listaNivelTres = (result.data ?? []).sort((a: any, b: any) =>
              (a.text ?? "").localeCompare(b.text, 'es', { sensitivity: 'base' })
            );
          }
          this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
        },
      });
  }

  listarLocalesVotacion(): void {
    let param: UbigeoLocalVotacionInput = new UbigeoLocalVotacionInput();
    param.idUbigeo = Number(this.form.get('nivelUbigeoTres')?.value);

    this.ubigeoApiService
      .listarLocalesVotaciones(param)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.listaLocalVotacion = result.data;
          this.form.get('localVotacion')?.setValue(0, { emitEvent: false });
        },
      });
  }

  filtrar(): void {
    const {
      tipoEleccion: eleccion,
      distritoElectoral: distrito,
      ambitoGeografico: ambito,
      nivelUbigeoUno: ubigeo1,
      nivelUbigeoDos: ubigeo2,
      nivelUbigeoTres: ubigeo3,
    } = this.form.getRawValue();

    if (!eleccion || eleccion === ELECCION_DEFAULT) {
      this.mensaje = 'constantes.MENSAJE_ORIGINAL';
      this.mensajeEvent.emit(this.mensaje);
      return;
    }
    this.mensaje = '';
    this.mensajeEvent.emit(this.mensaje);


    setTimeout(() => {
      if (eleccion) this.procesarTipoEleccion(Number(eleccion));
      if (ambito) this.procesarAmbitoGeografico(Number(ambito));
      if (distrito) this.procesarDistritoElectoral(distrito);
      if (ubigeo1) this.procesarUbigeo('uno', ubigeo1);
      if (ubigeo2) this.procesarUbigeo('dos', ubigeo2);
      if (ubigeo3) this.procesarUbigeo('tres', ubigeo3);

      let data: FiltroModel = this.cargarDatosFiltroAmbitoGeografico();
      this.cambiosFiltrosDinamicosEvent.emit(data);
      this.emitirEventoFiltrar();
    }, 200);
  }

  private procesarTipoEleccion(value: number) {
    this.tablaService.eleccionSeleccionado = {
      value: value,
      text: this.listaEleccion.find((x) => x.value === value)?.text ?? '',
    };
  }

  private procesarDistritoElectoral(value: number) {
    this.tablaService.distritoElectoral = {
      value: value.toString(),
      text: this.listaDistritoElectoral.find((x) => String(x.value) === String(value))
        ?.text ?? '',
    };
  }

  private procesarAmbitoGeografico(value: number) {
    this.tablaService.ambitoGeograficoSeleccionado = {
      value: value,
      text: this.listaAmbitoGeografico.find((x) => x.value === value)
        ?.text ?? '',
    };
  }

  private procesarUbigeo(nivel: 'uno' | 'dos' | 'tres', value: string) {
    const listas = {
      uno: this.listaNivelUno,
      dos: this.listaNivelDos,
      tres: this.listaNivelTres,
    };

    const propiedad = `ubigeoNivel${nivel[0].toUpperCase()}${nivel.slice(1)}Seleccionado` as
      | 'ubigeoNivelUnoSeleccionado'
      | 'ubigeoNivelDosSeleccionado'
      | 'ubigeoNivelTresSeleccionado';

    this.tablaService[propiedad] = {
      value: value.toString(),
      text:
        value === '0'
          ? ''
          : listas[nivel].find((x) => String(x.value) === value)?.text ?? '',
    };
  }

  emitirEventoFiltrar() {
    let filtroModel: FiltroModel = new FiltroModel();
    filtroModel.idTipoEleccion = this.form.get('tipoEleccion')?.value ?? 0;
    filtroModel.idDistritoElectoral = Number(this.form.get('distritoElectoral')?.value ?? 0);
    filtroModel.idAmbitoGeografico = this.form.get('ambitoGeografico')?.value ?? 0;

    filtroModel.idUbigeoNivel01 = this.form.get('nivelUbigeoUno')?.value ?? '0';
    filtroModel.idUbigeoNivel02 = this.form.get('nivelUbigeoDos')?.value ?? '0';
    filtroModel.idUbigeoNivel03 = this.form.get('nivelUbigeoTres')?.value ?? '0';
    filtroModel.idLocalVotacion = this.form.get('localVotacion')?.value ?? 0;

    filtroModel.esEleccionParaDistritoElectoral = this.esEleccionParaDistritoElectoral();

    filtroModel.nombreTipoEleccion = this.tablaService.eleccionSeleccionado?.text ?? '';
    filtroModel.nombreDistritoElectoral = this.tablaService.distritoElectoral?.text;
    filtroModel.nombreAmbitoGeografico = this.tablaService.ambitoGeograficoSeleccionado?.text;

    filtroModel.nombreUbigeoNivel01 = this.form.get('nivelUbigeoUno')?.value === '0' ? '' : this.tablaService.ubigeoNivelUnoSeleccionado?.text;
    filtroModel.nombreUbigeoNivel02 = this.form.get('nivelUbigeoDos')?.value === '0' ? '' : this.tablaService.ubigeoNivelDosSeleccionado?.text;
    filtroModel.nombreUbigeoNivel03 = this.form.get('nivelUbigeoTres')?.value === '0' ? '' : this.tablaService.ubigeoNivelTresSeleccionado?.text;
    filtroModel.nombreLocalVotacion = this.form.get('localVotacion')?.value === 0 ? '' : this.tablaService.localVotacionSeleccionado?.text;

    if (filtroModel.idAmbitoGeografico !== VALOR_TODOS) {
      this.habilitarNivelUbigeos();
    }

    this.filtrarEvent.emit(filtroModel);
  }

  habilitarNivelUbigeos() {
    const controls = ['nivelUbigeoUno', 'nivelUbigeoDos', 'nivelUbigeoTres'] as const;
    for (const ctrl of controls) {
      this.form.get(ctrl)?.enable({ emitEvent: false });
    }
  }

  deshabilitarNivelUbigeos() {
    const controls = ['nivelUbigeoUno', 'nivelUbigeoDos', 'nivelUbigeoTres'] as const;
    for (const ctrl of controls) {
      this.form.get(ctrl)?.disable({ emitEvent: false });
      this.form.get(ctrl)?.setValue('0', { emitEvent: false });
    }
  }

  valuesChanges(): void {
    this.valuesChangeTipoEleccion();
    this.valuesChangeDistritoElectoral();
    this.valuesChangesAmbitoGeografico();
    this.valuesChangeNivelUbigeoUno();
    this.valuesChangeNivelUbigeoDos();
    this.valuesChangeNivelUbigeoTres();
    this.behaviorComponenteFiltro();
  }

  private resetearFiltrosBasicos(): void {
    this.form.get('distritoElectoral')?.setValue(30, { emitEvent: false });
    this.form.get('ambitoGeografico')?.setValue(0, { emitEvent: false });
  }

  valuesChangeTipoEleccion() {
    this.form
      .get('tipoEleccion')
      ?.valueChanges.pipe(
      startWith(this.form.get('tipoEleccion')?.value),
      pairwise(),
      takeUntil(this.destroy$)
    )
    .subscribe({
      next: ([previo, actual]) => {
        const esPrevioDistrital = this.eleccionesDistritoElectoral.includes(Number(previo));
        const esActualDistrital = this.eleccionesDistritoElectoral.includes(Number(actual));

        // Si se selecciona "ninguno"
        if (actual === ELECCION_DEFAULT) {
          this.limpiar(true);

          return;
        }

        this.limpiar(true);

        // Si el tipo de elección cambió de ámbito (distrital ↔ no distrital)
        if (esPrevioDistrital !== esActualDistrital) {
          this.resetearFiltrosBasicos();
          this.init();
        }

        this.mensaje = '';

        // Lógica para cargar ubigeos o distritos
        const ambito = this.form.get('ambitoGeografico')?.value;
        const nivel1 = this.form.get('nivelUbigeoUno')?.value;

        if (esActualDistrital) {
          // Solo para elecciones de tipo distrital (diputados, senadores)
          this.listarDistritosElectorales();
          this.deshabilitarNivelUbigeos();
        } else if (ambito !== EnumIdAmbito.TODOS && nivel1 === '0') {
          // Para elecciones no distritales con ámbito ya seleccionado (Perú/Extranjero)
          this.listarUbigeoNivel01();
          this.habilitarNivelUbigeos();
        }
        // Si no es distrital y ambito es TODOS, no se hace nada:
        // el usuario aún no eligió Perú/Extranjero

        // Ejecutar filtrado asíncrono
        if (actual !== ELECCION_DEFAULT) {
          Promise.resolve().then(() => this.filtrar());
        }
      },
    });
  }

  valuesChangeDistritoElectoral() {
    this.form
      .get('distritoElectoral')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          Promise.resolve().then(() => this.filtrar());
        },
      });
  }

  valuesChangesAmbitoGeografico() {
    this.form
      .get('ambitoGeografico')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          if (value === EnumIdAmbito.TODOS) {
            this.hideUbigeosNivel123();
          }

          this.tablaService.ubigeoNivelUnoSeleccionado = null;
          this.tablaService.ubigeoNivelDosSeleccionado = null;
          this.tablaService.ubigeoNivelTresSeleccionado = null;

          this.listaNivelDos = [];
          this.listaNivelTres = [];
          this.listaLocalVotacion = [];

          this.form.get('nivelUbigeoUno')?.setValue('0', { emitEvent: false });
          this.form.get('nivelUbigeoDos')?.setValue('0', { emitEvent: false });
          this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
          this.form.get('localVotacion')?.setValue(0, { emitEvent: false });

          if (value !== EnumIdAmbito.TODOS) {
            this.listarUbigeoNivel01();
            this.habilitarNivelUbigeos();
          }
          Promise.resolve().then(() => this.filtrar());
        },
      });
  }

  valuesChangeNivelUbigeoUno() {
    this.form
      .get('nivelUbigeoUno')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.tablaService.ubigeoNivelDosSeleccionado = null;
          this.tablaService.ubigeoNivelTresSeleccionado = null;

          this.listaNivelDos = [];
          this.listaNivelTres = [];
          this.listaLocalVotacion = [];

          this.form.get('nivelUbigeoDos')?.setValue('0', { emitEvent: false });
          this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
          this.form.get('localVotacion')?.setValue(0, { emitEvent: false });
          if (value !== '0') {
            this.listarUbigeoNivel02();
          }
          Promise.resolve().then(() => this.filtrar());
        },
      });
  }

  valuesChangeNivelUbigeoDos() {
    this.form
      .get('nivelUbigeoDos')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.tablaService.ubigeoNivelTresSeleccionado = null;
          this.listaNivelTres = [];
          this.listaLocalVotacion = [];

          this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
          this.form.get('localVotacion')?.setValue(0, { emitEvent: false });
          if (value !== '0') {
            this.listarUbigeoNivel03();
          }
          Promise.resolve().then(() => this.filtrar());
        },
      });
  }

  valuesChangeNivelUbigeoTres() {
    this.form
      .get('nivelUbigeoTres')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          Promise.resolve().then(() => this.filtrar());
        },
      });
  }

  behaviorComponenteFiltro() {
    this.subscriptions$ = this.filtroBehaviorService.seleccionarUbigeoObservable$.subscribe({
      next: (result) => {
        this.seleccionarUbigeoSegunMapa(result);
      },
    });

    this.subscriptions1$ = this.filtroBehaviorService.seleccionarDistritoElectoralObservable$.subscribe({
      next: (result) => {
        this.seleccionarDistritoElectoralSegunMapa(result);
      },
    });

    this.subscriptions2$ = this.filtroBehaviorService.seleccionarIdAmbitoGeograficoObservable$.subscribe({
      next: (result) => {
        if (this.form.get('tipoEleccion')?.value !== ELECCION_DEFAULT) {
          this.seleccionarIdAmbitoGeograficoSegunMapa(result);
        }
      },
    });
  }

  private cargarDatosFiltro(): FiltroModel {
    let filtroModel: FiltroModel = new FiltroModel();
    filtroModel.idTipoEleccion = this.form.get('tipoEleccion')?.value ?? 0;
    filtroModel.idDistritoElectoral = Number(this.form.get('distritoElectoral')?.value ?? 0);
    filtroModel.idAmbitoGeografico = this.form.get('ambitoGeografico')?.value ?? 0;
    filtroModel.idUbigeoNivel01 = this.form.get('nivelUbigeoUno')?.value ?? '0';
    filtroModel.idUbigeoNivel02 = this.form.get('nivelUbigeoDos')?.value ?? '0';
    filtroModel.idUbigeoNivel03 = this.form.get('nivelUbigeoTres')?.value ?? '0';
    filtroModel.idLocalVotacion = this.form.get('localVotacion')?.value ?? 0;

    filtroModel.esEleccionParaDistritoElectoral = this.esEleccionParaDistritoElectoral();

    filtroModel.nombreTipoEleccion = this.tablaService.eleccionSeleccionado?.text ?? '';

    filtroModel.nombreDistritoElectoral = this.tablaService.distritoElectoral?.text;

    filtroModel.nombreAmbitoGeografico = this.tablaService.ambitoGeograficoSeleccionado?.text;

    filtroModel.nombreUbigeoNivel01 =
      this.form.get('nivelUbigeoUno')?.value === '0'
        ? ''
        : this.tablaService.ubigeoNivelUnoSeleccionado?.text;
    filtroModel.nombreUbigeoNivel02 =
      this.form.get('nivelUbigeoDos')?.value === '0'
        ? ''
        : this.tablaService.ubigeoNivelDosSeleccionado?.text;
    filtroModel.nombreUbigeoNivel03 =
      this.form.get('nivelUbigeoTres')?.value === '0'
        ? ''
        : this.tablaService.ubigeoNivelTresSeleccionado?.text;
    filtroModel.nombreLocalVotacion =
      this.form.get('localVotacion')?.value === 0
        ? ''
        : this.tablaService.localVotacionSeleccionado?.text;

    return filtroModel;
  }

  private cargarDatosFiltroAmbitoGeografico(): FiltroModel {
    let filtroModel: FiltroModel = new FiltroModel();
    filtroModel.idTipoEleccion = this.form.get('tipoEleccion')?.value ?? 0;
    filtroModel.idAmbitoGeografico = this.form.get('ambitoGeografico')?.value ?? 0;
    filtroModel.idUbigeoNivel01 = '0';
    filtroModel.idUbigeoNivel02 = '0';
    filtroModel.idUbigeoNivel03 = '0';
    filtroModel.idLocalVotacion = 0;

    filtroModel.esEleccionParaDistritoElectoral = this.esEleccionParaDistritoElectoral();

    filtroModel.nombreTipoEleccion = this.tablaService.eleccionSeleccionado?.text ?? '';
    filtroModel.nombreDistritoElectoral = this.tablaService.distritoElectoral?.text;
    filtroModel.nombreAmbitoGeografico = this.tablaService.ambitoGeograficoSeleccionado?.text;
    filtroModel.nombreUbigeoNivel01 = '';
    filtroModel.nombreUbigeoNivel02 = '';
    filtroModel.nombreUbigeoNivel03 = '';
    filtroModel.nombreLocalVotacion = '';

    return filtroModel;
  }

  public seleccionarUbigeoSegunMapa(value: any) {
    const valueStr = value.toString();
    const nivelUbigeo = this.identificarNivelUbigeo(valueStr);

    if (nivelUbigeo === EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO) {
      return;
    }

    if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_01) {
      this.procesarSeleccionNivel01(valueStr);
    } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_02) {
      this.procesarSeleccionNivel02(valueStr);
    } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_03) {
      this.procesarSeleccionNivel03(valueStr);
    } else {
      this.procesarSeleccionNivelOtro(valueStr);
    }
  }

  private procesarSeleccionNivel01(value: string): void {
    this.tablaService.ubigeoNivelUnoSeleccionado = {
      value: value,
      text: value === '0'
        ? ''
        : this.listaNivelUno.find((x) => String(x.value) === value)?.text ?? '',
    };
    this.form.get('nivelUbigeoUno')?.setValue(value, { emitEvent: false });
    this.listarUbigeoNivel02();
    this.emitirEventoFiltrar();
  }

  private procesarSeleccionNivel02(value: string): void {
    this.tablaService.ubigeoNivelDosSeleccionado = {
      value: value,
      text: value === '0'
        ? ''
        : this.listaNivelDos.find((x) => String(x.value) === value)?.text ?? '',
    };
    this.form.get('nivelUbigeoDos')?.setValue(value, { emitEvent: false });
    this.listarUbigeoNivel03();
    this.emitirEventoFiltrar();
  }

  private procesarSeleccionNivel03(value: string): void {
    const foundText = value === '0'
      ? ''
      : this.listaNivelTres.find((x) => String(x.value) === value)?.text;

    this.tablaService.ubigeoNivelTresSeleccionado = {
      value: value,
      text: foundText ?? '',
    };
    this.form.get('nivelUbigeoTres')?.setValue(value, { emitEvent: false });
    this.listarLocalesVotacion();

    if (foundText === undefined && value !== '0') {
      const param = new UbigeoNivel03Input();
      param.idAmbitoGeografico = Number(this.form.get('ambitoGeografico')?.value);
      param.idEleccion = Number(this.form.get('tipoEleccion')?.value);
      param.idUbigeoProvincia = Number(this.form.get('nivelUbigeoDos')?.value);

      this.ubigeoApiService
        .listarNivel03(param)
        .pipe(take(1))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.listaNivelTres = (result.data ?? []).sort((a: any, b: any) =>
                (a.text ?? '').localeCompare(b.text, 'es', { sensitivity: 'base' })
              );
              const text = this.listaNivelTres.find((x) => String(x.value) === value)?.text ?? '';
              this.tablaService.ubigeoNivelTresSeleccionado = { value, text };
            }
            this.emitirEventoFiltrar();
          },
        });
    } else {
      this.emitirEventoFiltrar();
    }
  }

  private procesarSeleccionNivelOtro(value: string): void {
    const ambitoValue = Number(this.form.get('ambitoGeografico')?.value ?? 0);
    if (ambitoValue === EnumIdAmbito.NACIONAL) {
      this.form.get('nivelUbigeoTres')?.setValue(value, { emitEvent: false });
      this.tablaService.ubigeoNivelTresSeleccionado = {
        value: value,
        text: value === '0'
          ? ''
          : this.listaNivelTres.find((x) => String(x.value) === value)?.text ?? '',
      };
      this.emitirEventoFiltrar();
    }
  }

  public seleccionarDistritoElectoralSegunMapa(value: any) {
    const parsedValue = Number.parseInt(value, 10);
    if (!Number.isNaN(parsedValue)) {
      this.form.get('distritoElectoral')?.setValue(parsedValue, { emitEvent: false });
      this.procesarDistritoElectoral(parsedValue);
      this.emitirEventoFiltrar();
    }
  }

  private seleccionarIdAmbitoGeograficoSegunMapa(value: number) {
    this.form.get('ambitoGeografico')?.setValue(value, { emitEvent: false });
    this.form.get('nivelUbigeoUno')?.setValue('0', { emitEvent: false });
    this.form.get('nivelUbigeoDos')?.setValue('0', { emitEvent: false });
    this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
    this.form.get('localVotacion')?.setValue(0, { emitEvent: false });

    if (value === EnumIdAmbito.EXTRANJERO) {
      this.nombreEtiquetaNivelUno = this.translate.instant('genericUbigeo.continente');
      this.nombreEtiquetaNivelDos = this.translate.instant('genericUbigeo.pais');
      this.nombreEtiquetaNivelTres = this.translate.instant('genericUbigeo.ciudad');
    } else if (value === EnumIdAmbito.NACIONAL) {
      this.nombreEtiquetaNivelUno = this.translate.instant('genericUbigeo.region');
      this.nombreEtiquetaNivelDos = this.translate.instant('genericUbigeo.provincia');
      this.nombreEtiquetaNivelTres = this.translate.instant('genericUbigeo.distrito');
    } else {
      this.hideUbigeosNivel123();
    }

    this.listaNivelDos = [];
    this.listaNivelTres = [];
    this.listaLocalVotacion = [];
    this.listarUbigeoNivel01();
    this.emitirEventoFiltrar();
  }

  private hideUbigeosNivel123() {
    this.nombreEtiquetaNivelUno = '';
    this.nombreEtiquetaNivelDos = '';
    this.nombreEtiquetaNivelTres = '';
  }

  private identificarNivelUbigeo(ubigeo: string): number {
    if (ubigeo === '' || ubigeo === undefined) {
      return EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO;
    }

    const ubigeoTmp01 = ubigeo.toString().substring(2, 6);
    const ubigeoTmp02 = ubigeo.toString().substring(4, 6);
    if (ubigeoTmp01 === '0000' || ubigeo.length === 2) {
      return EnumIdNivelUbigeo.NIVEL_UBIGEO_01;
    } else if (ubigeoTmp02 === '00') {
      return EnumIdNivelUbigeo.NIVEL_UBIGEO_02;
    } else {
      return EnumIdNivelUbigeo.NIVEL_UBIGEO_03;
    }
  }

  limpiar(absolute: boolean = false) {

    this.tablaService.distritoElectoral = null;
    this.tablaService.ambitoGeograficoSeleccionado = null;

    this.tablaService.ubigeoNivelUnoSeleccionado = null;
    this.tablaService.ubigeoNivelDosSeleccionado = null;
    this.tablaService.ubigeoNivelTresSeleccionado = null;
    this.tablaService.localVotacionSeleccionado = null;

    this.form.get('distritoElectoral')?.setValue(30, { emitEvent: false });
    this.form.get('ambitoGeografico')?.setValue(0, { emitEvent: false });

    this.form.get('nivelUbigeoUno')?.setValue('0', { emitEvent: false });
    this.form.get('nivelUbigeoDos')?.setValue('0', { emitEvent: false });
    this.form.get('nivelUbigeoTres')?.setValue('0', { emitEvent: false });
    this.form.get('localVotacion')?.setValue(0, { emitEvent: false });
    this.hideUbigeosNivel123();

    const filtroModel = this.cargarDatosFiltro();
    this.emitirEvento(filtroModel, absolute);
  }

  ocultarLimpiar(): boolean {
    return !!(
      this.form.get('tipoEleccion')?.value !== ELECCION_DEFAULT &&
      ((!(this.nombreEtiquetaDistritoElectoral && this.esEleccionParaDistritoElectoral()) && this.form.get('ambitoGeografico')?.value !== AMBITO_DEFAULT)
      || ((this.nombreEtiquetaDistritoElectoral && this.esEleccionParaDistritoElectoral()) && this.form.get('distritoElectoral')?.value !== EnumIdDistrito.TODOS))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions2$?.unsubscribe();
    this.subscriptions$?.unsubscribe();
    this.subscriptions1$?.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }

  private readonly _bottomSheet = inject(MatBottomSheet);

  openpopupfiltroeleccion(): void {
    const popupData: FiltroEleccionData = {
      tipoEleccion: this.form.get('tipoEleccion')?.value ?? ELECCION_DEFAULT,
    };
    const ref = this._bottomSheet.open(PopupFiltroEleccionComponent, {
      panelClass: 'menu-movil',
      data: popupData
    });

    ref.afterDismissed().subscribe((result: FiltroEleccionData | undefined) => {
      if (!result) return;
      const tipoEleccion = result.tipoEleccion;
      // Emitir el event para que valuesChangeTipoEleccion dispare el resto del flujo
      this.form.get('tipoEleccion')?.setValue(tipoEleccion);
      // Actualizar el label del botón de miga_pan
      const eleccionEncontrada = this.listaEleccion.find(item => item.value === tipoEleccion);
      if (eleccionEncontrada && tipoEleccion !== ELECCION_DEFAULT) {
        this.filtroSeleccionado = eleccionEncontrada.text ?? '';
      } else {
        this.filtroSeleccionado = 'TEleccion';
      }
    });
  }

  openpopupfiltroubigeo(): void {
    // Solo se abre si ya hay una elección seleccionada
    if ((this.form.get('tipoEleccion')?.value ?? ELECCION_DEFAULT) === ELECCION_DEFAULT) return;
    const popupData: FiltroUbigeoData = {
      tipoEleccion: this.form.get('tipoEleccion')?.value ?? ELECCION_DEFAULT,
      mostrarUbigeo: true,
      distritoElectoral: Number(this.form.get('distritoElectoral')?.value ?? EnumIdDistrito.TODOS),
      ambitoGeografico: this.form.get('ambitoGeografico')?.value ?? AMBITO_DEFAULT,
      nivelUbigeoUno: this.form.get('nivelUbigeoUno')?.value ?? '0',
      nivelUbigeoDos: this.form.get('nivelUbigeoDos')?.value ?? '0',
      nivelUbigeoTres: this.form.get('nivelUbigeoTres')?.value ?? '0',
      esEleccionParaDistritoElectoral: this.esEleccionParaDistritoElectoral(),
    };
    const ref = this._bottomSheet.open(PopupFiltroUbigeoComponent, {
      panelClass: 'menu-movil',
      data: popupData
    });

    ref.afterDismissed().subscribe((result: FiltroUbigeoData | undefined) => {
      if (!result) return;

      this.form.get('distritoElectoral')?.setValue(result.distritoElectoral ?? EnumIdDistrito.TODOS, { emitEvent: false });
      this.form.get('ambitoGeografico')?.setValue(result.ambitoGeografico ?? AMBITO_DEFAULT, { emitEvent: false });
      this.form.get('nivelUbigeoUno')?.setValue(result.nivelUbigeoUno ?? '0', { emitEvent: false });
      this.form.get('nivelUbigeoDos')?.setValue(result.nivelUbigeoDos ?? '0', { emitEvent: false });
      this.form.get('nivelUbigeoTres')?.setValue(result.nivelUbigeoTres ?? '0', { emitEvent: false });

      // Actualizar etiquetas del ámbito según la selección
      const ambito = result.ambitoGeografico ?? AMBITO_DEFAULT;
      if (ambito === EnumIdAmbito.EXTRANJERO) {
        this.nombreEtiquetaNivelUno = this.translate.instant('genericUbigeo.continente');
        this.nombreEtiquetaNivelDos = this.translate.instant('genericUbigeo.pais');
        this.nombreEtiquetaNivelTres = this.translate.instant('genericUbigeo.ciudad');
      } else if (ambito === EnumIdAmbito.NACIONAL) {
        this.nombreEtiquetaNivelUno = this.translate.instant('genericUbigeo.region');
        this.nombreEtiquetaNivelDos = this.translate.instant('genericUbigeo.provincia');
        this.nombreEtiquetaNivelTres = this.translate.instant('genericUbigeo.distrito');
      } else {
        this.hideUbigeosNivel123();
      }

      // Poblar las listas locales con los items del popup para que filtrar() encuentre los textos
      this.poblarListasDesdePopup(result);

      // Si es elección distrital, cargar la lista completa en FiltroComponent
      if (result.esEleccionParaDistritoElectoral) {
        this.listarDistritosElectorales();
      }

      // Ejecutar el filtrar normal
      this.filtrar();
    });
  }

  /** Items para el breadcrumb móvil: cada elemento es el texto del nivel seleccionado */
  get migaPanItems(): string[] {
    if (this.esEleccionParaDistritoElectoral()) return this.migaPanItemsDistritoElectoral();
    return this.migaPanItemsUbigeo();
  }

  private migaPanItemsDistritoElectoral(): string[] {
    const dText = this.tablaService.distritoElectoral?.text;
    return dText ? [dText] : [];
  }

  private migaPanItemsUbigeo(): string[] {
    const items: string[] = [];
    const ambito = this.form.get('ambitoGeografico')?.value;
    const n1 = this.form.get('nivelUbigeoUno')?.value;
    const n2 = this.form.get('nivelUbigeoDos')?.value;
    const n3 = this.form.get('nivelUbigeoTres')?.value;
    const aText = this.listaAmbitoGeografico.find(x => x.value === ambito)?.text;
    if (aText && ambito !== AMBITO_DEFAULT) items.push(aText);
    const n1Text = this.tablaService.ubigeoNivelUnoSeleccionado?.text;
    if (n1 && n1 !== '0' && n1Text) items.push(n1Text);
    const n2Text = this.tablaService.ubigeoNivelDosSeleccionado?.text;
    if (n2 && n2 !== '0' && n2Text) items.push(n2Text);
    const n3Text = this.tablaService.ubigeoNivelTresSeleccionado?.text;
    if (n3 && n3 !== '0' && n3Text) items.push(n3Text);
    return items;
  }

  emitirEvento(filtroModel: FiltroModel, absolute: boolean = false) {
    this.limpiarFiltrarEvent.emit({ filtro: filtroModel, absolute });
  }

  /** Garantiza que las listas locales contengan los items seleccionados en el popup */
  private poblarListasDesdePopup(result: FiltroUbigeoData): void {
    if (result.esEleccionParaDistritoElectoral) {
      const vd = result.distritoElectoral;
      if (vd !== undefined && result.distritoElectoralText) {
        if (!this.listaDistritoElectoral.some(x => Number(x.value) === vd)) {
          this.listaDistritoElectoral = [{ value: vd, text: result.distritoElectoralText }];
        }
      }
      return;
    }
    const n1 = result.nivelUbigeoUno ?? '0';
    if (n1 !== '0' && result.nivelUbigeoUnoText && !this.listaNivelUno.some(x => x.value === n1)) {
      this.listaNivelUno = [{ value: n1, text: result.nivelUbigeoUnoText }];
    }
    const n2 = result.nivelUbigeoDos ?? '0';
    if (n2 !== '0' && result.nivelUbigeoDosText && !this.listaNivelDos.some(x => x.value === n2)) {
      this.listaNivelDos = [{ value: n2, text: result.nivelUbigeoDosText }];
    }
    const n3 = result.nivelUbigeoTres ?? '0';
    if (n3 !== '0' && result.nivelUbigeoTresText && !this.listaNivelTres.some(x => x.value === n3)) {
      this.listaNivelTres = [{ value: n3, text: result.nivelUbigeoTresText }];
    }
  }

  onChangeAmbitGeografic(value: number): void {
    this.updateTranslation(value);
  }

  private updateTranslation(code: number): void {
    const isStranger = code === EnumIdAmbito.NACIONAL;
    if (isStranger) {
      this.nombreEtiquetaNivelUno = 'genericUbigeo.region';
      this.nombreEtiquetaNivelDos = 'genericUbigeo.provincia';
      this.nombreEtiquetaNivelTres = 'genericUbigeo.distrito';
    } else {
      this.nombreEtiquetaNivelUno = 'genericUbigeo.continente';
      this.nombreEtiquetaNivelDos = 'genericUbigeo.pais';
      this.nombreEtiquetaNivelTres = 'genericUbigeo.ciudad';
    }
  }
}
