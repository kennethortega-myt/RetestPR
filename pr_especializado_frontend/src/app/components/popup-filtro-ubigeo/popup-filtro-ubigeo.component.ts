import { Component, Inject, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { Ubigeo } from '../../interfaces/output/ubigeo.model';
import { AmbitoGeografico } from '../../interfaces/output/ambito-geografico.model';
import { UbigeoNivel01Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-01-input';
import { UbigeoNivel02Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-02-input';
import { UbigeoNivel03Input } from '../../interfaces/input/filtro-ubigeo/ubigeo-nivel-03-input';
import { UbigeoApiService } from '../../services/ubigeo-api.service';
import {
  AMBITO_DEFAULT,
  ELECCION_DEFAULT,
  EnumIdAmbito,
  EnumIdDistrito,
  EnumIdEleccionDistritoElectoral,
} from '../../helpers/enums';
import { Subject, take } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FiltroUbigeoData, PopupFiltroUbigeoForm } from '../../interfaces/output/filtro/eleccion.model';

@Component({
  selector: 'app-popup-filtro-ubigeo',
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule,
    TranslateModule,
    CommonModule,
  ],
  templateUrl: './popup-filtro-ubigeo.component.html',
})
export class PopupFiltroUbigeoComponent implements OnInit, OnDestroy {

  listaDistritoElectoral: Ubigeo[] = [];
  listaNivelUno: Ubigeo[] = [];
  listaNivelDos: Ubigeo[] = [];
  listaNivelTres: Ubigeo[] = [];

  listaAmbitoGeografico: AmbitoGeografico[] = [
    { value: 0, text: 'TODOS' },
    { value: 1, text: 'PERÚ' },
    { value: 2, text: 'EXTRANJERO' },
  ];

  nombreEtiquetaAmbito: string = 'REGIÓN';
  nombreEtiquetaNivelUno: string = 'REGIÓN';
  nombreEtiquetaNivelDos: string = 'PROVINCIA';
  nombreEtiquetaNivelTres: string = 'DISTRITO';

  readonly eleccionesDistritoElectoral = [
    EnumIdEleccionDistritoElectoral.ID_ELECCION_DIPUTADOS,
    EnumIdEleccionDistritoElectoral.ID_ELECCION_SENADORES_MULTIPLE,
  ];

  private readonly formBuilder = inject(FormBuilder);
  form: FormGroup<PopupFiltroUbigeoForm> = this.formBuilder.nonNullable.group({
    distritoElectoral: EnumIdDistrito.TODOS,
    ambitoGeografico: AMBITO_DEFAULT,
    nivelUbigeoUno: '0',
    nivelUbigeoDos: '0',
    nivelUbigeoTres: '0',
  });

  private readonly destroy$ = new Subject<void>();

  constructor(
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: FiltroUbigeoData,
    private readonly bottomSheetRef: MatBottomSheetRef<PopupFiltroUbigeoComponent>,
    private readonly ubigeoApiService: UbigeoApiService,
    private readonly translateService: TranslateService) {}

  cerrarmenu(): void {
    this.bottomSheetRef.dismiss();
  }

  ngOnInit(): void {
    this.inicializarFormulario();
    this.suscribirCambios();
  }

  esEleccionParaDistritoElectoral(): boolean {
    return this.eleccionesDistritoElectoral.includes(Number(this.data.tipoEleccion));
  }

  private inicializarFormulario(): void {
    this.precargarCamposFormulario();
    this.precargarListas();
  }

  private precargarCamposFormulario(): void {
    const c = this.form.controls;
    if (this.data.distritoElectoral !== undefined) c.distritoElectoral.setValue(this.data.distritoElectoral, { emitEvent: false });
    if (this.data.nivelUbigeoUno !== undefined) c.nivelUbigeoUno.setValue(this.data.nivelUbigeoUno, { emitEvent: false });
    if (this.data.nivelUbigeoDos !== undefined) c.nivelUbigeoDos.setValue(this.data.nivelUbigeoDos, { emitEvent: false });
    if (this.data.nivelUbigeoTres !== undefined) c.nivelUbigeoTres.setValue(this.data.nivelUbigeoTres, { emitEvent: false });
    if (this.data.ambitoGeografico !== undefined) {
      c.ambitoGeografico.setValue(this.data.ambitoGeografico, { emitEvent: false });
      this.actualizarEtiquetasAmbito(this.data.ambitoGeografico);
    }
  }

  private precargarListas(): void {
    if (this.esEleccionParaDistritoElectoral()) {
      this.listarDistritosElectorales();
      return;
    }
    const ambito = this.data.ambitoGeografico ?? AMBITO_DEFAULT;
    if (ambito === EnumIdAmbito.TODOS) return;
    this.listarUbigeoNivel01(() => {
      if (!this.data.nivelUbigeoUno || this.data.nivelUbigeoUno === '0') return;
      this.listarUbigeoNivel02(() => {
        if (this.data.nivelUbigeoDos && this.data.nivelUbigeoDos !== '0') this.listarUbigeoNivel03();
      });
    });
  }

  private suscribirCambios(): void {
    this.form.controls.ambitoGeografico.valueChanges.subscribe(valor => {
      this.listaNivelUno = [];
      this.listaNivelDos = [];
      this.listaNivelTres = [];
      this.form.controls.nivelUbigeoUno.setValue('0', { emitEvent: false });
      this.form.controls.nivelUbigeoDos.setValue('0', { emitEvent: false });
      this.form.controls.nivelUbigeoTres.setValue('0', { emitEvent: false });
      this.actualizarEtiquetasAmbito(Number(valor));
      if (Number(valor) !== EnumIdAmbito.TODOS) this.listarUbigeoNivel01();
    });

    this.form.controls.nivelUbigeoUno.valueChanges.subscribe(valor => {
      this.listaNivelDos = [];
      this.listaNivelTres = [];
      this.form.controls.nivelUbigeoDos.setValue('0', { emitEvent: false });
      this.form.controls.nivelUbigeoTres.setValue('0', { emitEvent: false });
      if (valor && valor !== '0') this.listarUbigeoNivel02();
    });

    this.form.controls.nivelUbigeoDos.valueChanges.subscribe(valor => {
      this.listaNivelTres = [];
      this.form.controls.nivelUbigeoTres.setValue('0', { emitEvent: false });
      if (valor && valor !== '0') this.listarUbigeoNivel03();
    });
  }

  private actualizarEtiquetasAmbito(ambito: number): void {
       this.nombreEtiquetaAmbito = this.translateService.instant('popup-filtro.ambito');
    if (ambito === EnumIdAmbito.EXTRANJERO) {
        this.nombreEtiquetaNivelUno = this.translateService.instant('genericUbigeo.continente');
        this.nombreEtiquetaNivelDos = this.translateService.instant('genericUbigeo.pais');
        this.nombreEtiquetaNivelTres = this.translateService.instant('genericUbigeo.ciudad');
    } else if (ambito === EnumIdAmbito.NACIONAL) {
        this.nombreEtiquetaNivelUno = this.translateService.instant('genericUbigeo.region');
        this.nombreEtiquetaNivelDos = this.translateService.instant('genericUbigeo.provincia');
        this.nombreEtiquetaNivelTres = this.translateService.instant('genericUbigeo.distrito');
    } else {
      this.nombreEtiquetaNivelUno = '';
      this.nombreEtiquetaNivelDos = '';
      this.nombreEtiquetaNivelTres = '';
    }
  }

  listarDistritosElectorales(): void {
    this.ubigeoApiService.listarDistritosElectorales().pipe(take(1)).subscribe({
      next: result => { if (result.success) this.listaDistritoElectoral = result.data; },
    });
  }

  listarUbigeoNivel01(callback?: () => void): void {
    const param = new UbigeoNivel01Input();
    param.idAmbitoGeografico = Number(this.form.controls.ambitoGeografico.value);
    param.idEleccion = Number(this.data.tipoEleccion);
    if (param.idEleccion === ELECCION_DEFAULT) return;
    this.ubigeoApiService.listarNivel01(param).pipe(take(1)).subscribe({
      next: result => {
        if (result.success) {
          this.listaNivelUno = (result.data ?? []).sort((a: any, b: any) =>
            (a.text ?? '').localeCompare(b.text, 'es', { sensitivity: 'base' })
          );
        }
        callback?.();
      },
    });
  }

  listarUbigeoNivel02(callback?: () => void): void {
    const param = new UbigeoNivel02Input();
    param.idAmbitoGeografico = Number(this.form.controls.ambitoGeografico.value);
    param.idEleccion = Number(this.data.tipoEleccion);
    param.idUbigeoDepartamento = Number(this.form.controls.nivelUbigeoUno.value);
    this.ubigeoApiService.listarNivel02(param).pipe(take(1)).subscribe({
      next: result => {
        if (result.success) {
          this.listaNivelDos = (result.data ?? []).sort((a: any, b: any) =>
            (a.text ?? '').localeCompare(b.text, 'es', { sensitivity: 'base' })
          );
        }
        callback?.();
      },
    });
  }

  listarUbigeoNivel03(): void {
    const param = new UbigeoNivel03Input();
    param.idAmbitoGeografico = Number(this.form.controls.ambitoGeografico.value);
    param.idEleccion = Number(this.data.tipoEleccion);
    param.idUbigeoProvincia = Number(this.form.controls.nivelUbigeoDos.value);
    this.ubigeoApiService.listarNivel03(param).pipe(take(1)).subscribe({
      next: result => {
        if (result.success) {
          this.listaNivelTres = (result.data ?? []).sort((a: any, b: any) =>
            (a.text ?? '').localeCompare(b.text, 'es', { sensitivity: 'base' })
          );
        }
      },
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get tieneFiltroUbigeoActivo(): boolean {
    if (!this.data.mostrarUbigeo) return false;
    const v = this.form.getRawValue();
    if (this.esEleccionParaDistritoElectoral()) {
      return String(v.distritoElectoral) !== String(EnumIdDistrito.TODOS);
    }
    return (
      Number(v.ambitoGeografico) !== AMBITO_DEFAULT ||
      v.nivelUbigeoUno !== '0' ||
      v.nivelUbigeoDos !== '0' ||
      v.nivelUbigeoTres !== '0'
    );
  }

  limpiarFiltros(): void {
    this.listaNivelUno = [];
    this.listaNivelDos = [];
    this.listaNivelTres = [];
    this.form.controls.distritoElectoral.setValue(EnumIdDistrito.TODOS, { emitEvent: false });
    this.form.controls.nivelUbigeoUno.setValue('0', { emitEvent: false });
    this.form.controls.nivelUbigeoDos.setValue('0', { emitEvent: false });
    this.form.controls.nivelUbigeoTres.setValue('0', { emitEvent: false });
    this.form.controls.ambitoGeografico.setValue(AMBITO_DEFAULT, { emitEvent: false });
    this.actualizarEtiquetasAmbito(AMBITO_DEFAULT);
  }

  cerrarConDatos(): void {
    const v = this.form.getRawValue();
    const result: FiltroUbigeoData = {
      tipoEleccion: this.data.tipoEleccion,
      distritoElectoral: v.distritoElectoral,
      ambitoGeografico: v.ambitoGeografico,
      nivelUbigeoUno: v.nivelUbigeoUno,
      nivelUbigeoDos: v.nivelUbigeoDos,
      nivelUbigeoTres: v.nivelUbigeoTres,
      esEleccionParaDistritoElectoral: this.esEleccionParaDistritoElectoral(),
      distritoElectoralText: this.listaDistritoElectoral.find(x => String(x.value) === String(v.distritoElectoral))?.text,
      nivelUbigeoUnoText: this.listaNivelUno.find(x => x.value === v.nivelUbigeoUno)?.text,
      nivelUbigeoDosText: this.listaNivelDos.find(x => x.value === v.nivelUbigeoDos)?.text,
      nivelUbigeoTresText: this.listaNivelTres.find(x => x.value === v.nivelUbigeoTres)?.text,
    };
    setTimeout(() => this.bottomSheetRef.dismiss(result), 250);
  }
}
