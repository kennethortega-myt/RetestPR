import { Component, Inject, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TranslateModule } from '@ngx-translate/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { CommonModule } from '@angular/common';
import { Subject, take } from 'rxjs';

import { TipoEleccion } from '../../interfaces/output/tipo-eleccion.model';
import { ResumenGeneralApiService } from '../../services/resumen-general-api.service';
import { getEncryptStorageEleccionValue } from '../../helpers/encrypt-storage-eleccion';
import { ListarEleccionesResumenGeneralInput } from '../../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { ELECCION_DEFAULT, EnumTipoFiltro } from '../../helpers/enums';
import { ID_ELECCION_PRINCIPAL, LISTA_TIPO_REPORTE } from '../../helpers/constantes';
import { FiltroEleccionData, PopupFiltroEleccionForm } from '../../interfaces/output/filtro/eleccion.model';

@Component({
  selector: 'app-popup-filtro-eleccion',
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule,
    TranslateModule,
    CommonModule,
  ],
  templateUrl: './popup-filtro-eleccion.component.html',
})
export class PopupFiltroEleccionComponent implements OnInit, OnDestroy {

  listaEleccion: TipoEleccion[] = [];
  listaReporte: TipoEleccion[] = [];

  private readonly formBuilder = inject(FormBuilder);
  form: FormGroup<PopupFiltroEleccionForm> = this.formBuilder.nonNullable.group({
    tipoEleccion: ELECCION_DEFAULT,
    tipoReporte: ELECCION_DEFAULT,
  });

  private readonly destroy$ = new Subject<void>();

  constructor(
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: FiltroEleccionData,
    private readonly bottomSheetRef: MatBottomSheetRef<PopupFiltroEleccionComponent>,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
  ) {}

  cerrarmenu(): void {
    this.bottomSheetRef.dismiss();
  }

  ngOnInit(): void {
    this.listarElecciones();
    this.precargarFormulario();
    if (this.data.mostrarReporte) {
      this.listaReporte = LISTA_TIPO_REPORTE;
    }
  }

  private precargarFormulario(): void {
    if (this.data.tipoEleccion !== undefined) {
      this.form.controls.tipoEleccion.setValue(this.data.tipoEleccion, { emitEvent: false });
    }
    if (this.data.mostrarReporte && this.data.tipoReporte !== undefined) {
      this.form.controls.tipoReporte.setValue(this.data.tipoReporte, { emitEvent: false });
    }
  }

  listarElecciones(): void {
    const idProcesoPrincipal = getEncryptStorageEleccionValue(ID_ELECCION_PRINCIPAL);
    const data = new ListarEleccionesResumenGeneralInput();
    data.activo = 1;
    data.idProceso = idProcesoPrincipal;
    data.tipoFiltro = EnumTipoFiltro.ELECCION;
    this.resumenGeneralApiService.listarElecciones(data).pipe(take(1)).subscribe({
      next: result => { if (result.success) this.listaEleccion = result.data; },
    });
  }

  cerrarConDatos(): void {
    const v = this.form.getRawValue();
    const result: FiltroEleccionData = {
      tipoEleccion: v.tipoEleccion,
      tipoReporte: v.tipoReporte,
    };
    setTimeout(() => this.bottomSheetRef.dismiss(result), 250);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
