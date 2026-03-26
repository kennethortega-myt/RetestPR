import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { ACTAS_OPTIONS, ACTAS_OPTIONS_V, DEFAULT_MAIN_OPTION, MAIN_OPTIONS_ACTAS_FILTER } from "./actas-filters.constants";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { ActaOptionFilter } from "./actas-filters.interface";
import { Subject, takeUntil } from "rxjs";

@Component({
  selector: "app-actas-filters",
  templateUrl: "./actas-filters.component.html",
  standalone: false
})
export class ActasFiltersComponent implements OnInit, OnDestroy  {
  @Output() actasFiltersForApplyEvent = new EventEmitter<string>();
  @Input() form: FormGroup<{ filtro: FormControl<boolean>; }>;
  @Output() formChange = new EventEmitter();

  public mainOptions = MAIN_OPTIONS_ACTAS_FILTER;
  public actasOptions = ACTAS_OPTIONS;
  public actasOptionsV = ACTAS_OPTIONS_V;
  public actualtipoObservacion = [];

  private readonly formBuilder = inject(FormBuilder);
  private readonly destroy$ = new Subject<void>()

  public mainForm = this.formBuilder.group({
    filtro: new FormControl(DEFAULT_MAIN_OPTION),
  });

  public actasForm = this.formBuilder.group({
    observadas: new FormControl(false),
    observadas_sin_datos: new FormControl(false),
    observadas_incompleta: new FormControl(false),
    observadas_error_material: new FormControl(false),
    observadas_ilegibilidad: new FormControl(false),
    observadas_sin_firmas: new FormControl(false),
    impugnadas: new FormControl(false),
    nulidad: new FormControl(false),
    extraviadas: new FormControl(false),
    siniestradas: new FormControl(false),
    observadas_dos_o_mas: new FormControl(false),
    tipoObservacion: new FormControl([])
  });

  public FiltrarActas = "actas-filters.FiltrarActas"
  public MotivoEnvioJEE = "actas-filters.MotivoEnvioJEE";

  constructor(){}

  showLoading():void {
    // Loading functionality removed
  }

  hideLoading():void {
    // Loading functionality removed
  }

  ngOnInit(): void {
    this.selectOption();
  }

  public selectOption() {
    this.mainForm.get('filtro').valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.emitEventToUpddateActasFilters();
      });
  }
  
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }


  public emitEventObservadasCheckedMovil(optionClicked: ActaOptionFilter){
    let actualValue = this.actasForm.controls.tipoObservacion.value;

    if(optionClicked.value == this.actasOptions.observadas.value) {
      const valores = optionClicked?.children.map(op => op.value);

      if (actualValue.includes(this.actasOptions.observadas.value)) {
        let actualValue = this.actasForm.controls.tipoObservacion.value;
        const sinDuplicados = [...new Set(actualValue.concat(valores))];
        this.actasForm.controls.tipoObservacion.setValue(sinDuplicados);
      } else {
        const opcionesFiltradas = actualValue.filter(value => !valores.includes(value));
        this.actasForm.controls.tipoObservacion.setValue(opcionesFiltradas);
      }

      this.emitEventToUpddateActasFilters();
    }
  }

  public emitEventToUpddateActasFilters(_?: MatCheckboxChange, option?: ActaOptionFilter) {
    this.showLoading();
    if(option?.children?.length > 0 ){
      this.checkActasObservadas(_);
    } else {
      this.form = this.mainForm;
      this.formChange.emit(this.form);
      const value = this.getTotalStringFilters();
      this.actasFiltersForApplyEvent.emit(value);
    }
    this.hideLoading();
  }

  public checkActasObservadas(_: MatCheckboxChange) {
    const value = this.actasForm.controls.observadas.value;
    if (value) {
      this.selectAllObservadas();
    } else {
      this.unSelectAllObservadas();
    }
    this.emitEventToUpddateActasFilters(_);
  }

  private selectAllObservadas() {
    this.actasForm.controls.observadas_sin_datos.setValue(true);
    this.actasForm.controls.observadas_incompleta.setValue(true);
    this.actasForm.controls.observadas_error_material.setValue(true);
    this.actasForm.controls.observadas_ilegibilidad.setValue(true);
    this.actasForm.controls.observadas_sin_firmas.setValue(true);
  }

  private unSelectAllObservadas() {
    this.actasForm.controls.observadas_sin_datos.setValue(false);
    this.actasForm.controls.observadas_incompleta.setValue(false);
    this.actasForm.controls.observadas_error_material.setValue(false);
    this.actasForm.controls.observadas_ilegibilidad.setValue(false);
    this.actasForm.controls.observadas_sin_firmas.setValue(false);
  }

  private getTotalStringFilters() {
    const arr = this.getObjectOfValue();
    const arrFilteredObjects = arr.filter((ele) => ele.isChecked);
    const arrFilteredStrings = arrFilteredObjects.map((ele) => ele.stringValue);
    return arrFilteredStrings.join(", ");
  }

  private getObjectOfValue() {
    const {
      observadas,
      observadas_sin_datos,
      observadas_incompleta,
      observadas_error_material,
      observadas_ilegibilidad,
      observadas_sin_firmas,
      impugnadas,
      nulidad,
      extraviadas,
      siniestradas,
      observadas_dos_o_mas,
      tipoObservacion,
    } = this.actasForm.controls;

    return [
      { isChecked: observadas.value, stringValue: ACTAS_OPTIONS.observadas.value },
      { isChecked: observadas_sin_datos.value, stringValue: ACTAS_OPTIONS.observadas_sin_datos.value },
      { isChecked: observadas_incompleta.value, stringValue: ACTAS_OPTIONS.observadas_incompleta.value },
      { isChecked: observadas_error_material.value, stringValue: ACTAS_OPTIONS.observadas_error_material.value },
      { isChecked: observadas_ilegibilidad.value, stringValue: ACTAS_OPTIONS.observadas_ilegibilidad.value },
      { isChecked: observadas_sin_firmas.value, stringValue: ACTAS_OPTIONS.observadas_sin_firmas.value },
      { isChecked: impugnadas.value, stringValue: ACTAS_OPTIONS.impugnadas.value },
      { isChecked: nulidad.value, stringValue: ACTAS_OPTIONS.nulidad.value },
      { isChecked: extraviadas.value, stringValue: ACTAS_OPTIONS.extraviadas.value },
      { isChecked: siniestradas.value, stringValue: ACTAS_OPTIONS.siniestradas.value },
      { isChecked: observadas_dos_o_mas.value, stringValue: ACTAS_OPTIONS.observadas_dos_o_mas.value },
      { isChecked: true, stringValue: tipoObservacion.value },
    ];
  }
}
