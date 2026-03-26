import { Component, ElementRef, EventEmitter, forwardRef, Input, OnDestroy, Output, ViewChild } from "@angular/core";
import { ControlValueAccessor, FormBuilder, FormControl, FormGroup, NG_VALUE_ACCESSOR, Validators } from "@angular/forms";
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import { map, Observable, startWith, Subject, takeUntil } from "rxjs";

@Component({
  selector: "app-autocomplete-input",
  templateUrl: "./autocomplete-input.component.html",
  styleUrls: ["./autocomplete-input.component.scss"],
  standalone: false,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AutocompleteInputComponent),
      multi: true,
    },
  ],
})
export class AutocompleteInputComponent<T extends Record<string, any>> implements ControlValueAccessor, OnDestroy {
  @Input() form: FormGroup = this.fb.group({});
  @Input() formControlName: string = "";
  @Input() placeholder: string = "...";

  @Input() arrayValues: T[] = [];
  @Input() displayKey: keyof T = "name" as keyof T;
  @Input() valueKey: keyof T = "id" as keyof T;
  @Output() optionValueSelected: EventEmitter<string | number> = new EventEmitter();
  @Output() optionItemSelected: EventEmitter<T> = new EventEmitter();

  @ViewChild('inputSearchAutoComplete') inputSearchAutoComplete!: ElementRef<HTMLInputElement>;
  @ViewChild(MatAutocompleteTrigger) autocompleteTrigger!: MatAutocompleteTrigger;

  private suppressOpen = false;

  formControLabel: string = "";
  filteredOptions: Observable<any[]>;
  private readonly destroy$ = new Subject<void>();
  selected: T = {} as T;
  value: keyof T = null as keyof T;

  private _disabled = false;

  @Input()
  set disabled(value: boolean) {
    this._disabled = value;
  }
  get disabled(): boolean {
    return this._disabled;
  }

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.inicicializarSelected();
    this.formControLabel = this.formControlName + "_label";

    // Modificación aquí: pasa el valor de disabled al crear el control.
    const formControl = new FormControl({ value: '', disabled: this.disabled }, Validators.required);
    this.form.addControl(this.formControLabel, formControl);
  }


  ngAfterViewInit() {
    const control = this.form.get(this.formControLabel);
    if (control) {
      this.filteredOptions = control.valueChanges.pipe(
        takeUntil(this.destroy$),
        startWith(""),
        map((value) => this._filter(value || ""))
      );
    }
  }

  inicicializarSelected() {
    this.selected = {
      [this.valueKey]: null,
      [this.displayKey]: "",
    } as T;
  }

  getDisplayValue(item: T): string {
    return item[this.displayKey] as string;
  }

  
  ngOnDestroy(): void {
    this.destroy$.next();  
    this.destroy$.complete();
  }

  getValue(item: T): any {
    return item[this.valueKey];
  }

  _filter(value: string): any[] {
    if (!this.arrayValues || this.arrayValues.length === 0) {
      return [];
    }

    const filterValue = String(value || "").toLowerCase();

    return this.arrayValues.filter((item) =>
      String(item[this.displayKey]).toLowerCase().includes(filterValue)
    );
  }

  onInputClick(): void {
    if (!this.form.get(this.formControLabel)?.value) {
      this.form.get(this.formControLabel).setValue("");
    }
  }

  onOptionSelected(event: any) {
    this.selected = this.arrayValues.find((item) => item[this.valueKey] === event.option.value);
    this.form.get(this.formControlName).setValue(this.selected[this.valueKey], { emitEvent: true });
    this.form.get(this.formControLabel).setValue(this.selected[this.displayKey], { emitEvent: true });
    this.optionValueSelected.emit(this.selected[this.valueKey]);
    this.optionItemSelected.emit(this.selected);
  }

  getSelectedByLabel(func) {
    const labelValue = this.form.get(this.formControLabel).value;
    const selectedClose = this.arrayValues.find((item) => item[this.displayKey] === labelValue);
    if (selectedClose == undefined) {
      func();
    }
  }

  onAutocompleteClosed() {
    this.getSelectedByLabel(() => {
      if(this.selected[this.valueKey] !== null){
        this.limpiarSelected();
      }
    });
  }

  limpiarAutoComplete() {
    this.arrayValues = [];
    this.limpiarSelected();
  }

  limpiarSelected(): void {
    
    this.suppressOpen = true;
    if (this.autocompleteTrigger?.panelOpen && !this.suppressOpen) {
      this.autocompleteTrigger.closePanel();
    }

    this.inicicializarSelected();
    this.form.get(this.formControlName)?.setValue(0, { emitEvent: true });
    this.form.get(this.formControLabel)?.setValue('', { emitEvent: true });

    this.optionValueSelected.emit(0);

    if (this.inputSearchAutoComplete) {
      this.inputSearchAutoComplete.nativeElement.value = '';
      this.inputSearchAutoComplete.nativeElement.dispatchEvent(new Event('input'));
    }

    if (this.autocompleteTrigger?.autocomplete) {
      this.autocompleteTrigger.autocomplete.options.forEach(o => o.deselect());
    }
    
    setTimeout(() => {
      this.suppressOpen = false;
    }, 100);
  }

  onChange = (value: any) => {};

  onTouched = () => {};

  writeValue(value: any): void {
    this.value = value;
    if (value === null || value === '') {
      this.form.get(this.formControLabel)?.setValue("", { emitEvent: true });
      this.inicicializarSelected();
    } else {
      // console.log('this.arrayValues:', this.arrayValues)
      this.arrayValues = this.arrayValues ?? [];
      const selected = this.arrayValues.find((item) => this.getValue(item) == value);
      if (selected) {
        this.selected = selected;
        if (this.form.get(this.formControLabel)) {
          this.form.get(this.formControLabel).setValue(this.getDisplayValue(selected), { emitEvent: false });
        }
      }
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;  // Esto actualizará el valor de `disabled`
  }

  updateValue(value: any) {
    this.value = value;
    this.onChange(value);
  }
}