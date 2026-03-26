import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { MY_DATE_FORMATS } from '../../../../helpers/datetime-helper.common';
import { TipoEleccion } from '../../../../interfaces/output/tipo-eleccion.model';

type SelectItem = {
  text: string;
  value: number | string | null;
};

@Component({
  selector: 'app-reporte-config-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    MatTooltipModule,
    MatDatepickerModule,
    TranslateModule,
  ],
  templateUrl: './reporte-config-form.component.html',
  styleUrl: './reporte-config-form.component.scss',
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
  ],
})
export class ReporteConfigFormComponent {
  @Input({ required: true }) titleKey!: string;
  @Input() headerClass = 'px-0-movil mt-2';
  @Input({ required: true }) form!: FormGroup;
  @Input({ required: true }) listaEleccion: TipoEleccion[] = [];
  @Input({ required: true }) listaDePeriodos: SelectItem[] = [];
  @Input({ required: true }) listaDeHorasDeInicios: SelectItem[] = [];
  @Input({ required: true }) minDate!: Date;
  @Input({ required: true }) maxDate!: Date;
  @Input() horaTooltipAriaLabel = 'La fecha no puede ser antes del proceso electoral';
  @Input() isValidForm = false;

  @Output() guardar = new EventEmitter<void>();

  public onGuardar(): void {
    this.guardar.emit();
  }
}
