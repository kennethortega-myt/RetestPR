import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { ComponentsModule } from '../../../components/components.module';

@Component({
  selector: 'app-password-update',
  templateUrl: './password-update.component.html',
  imports: [ReactiveFormsModule, MatIconModule, MatDialogModule, MatFormFieldModule, MatInputModule, CommonModule, ComponentsModule],
})
export class PasswordUpdateComponent implements OnInit {
  formGroup!: FormGroup;
  showPassword = false;
  showPassword2 = false;

  private readonly PASSWORD_PATTERN = /^(?!\s)(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,30}(?<!\s)$/;

  constructor(
    public dialogRef: MatDialogRef<PasswordUpdateComponent>,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.formGroup = this.formBuilder.group({
      contrasenia: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30),
        Validators.pattern(this.PASSWORD_PATTERN)
      ]),
      contrasenia2: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30)
      ])
    });

    this.setupPasswordValueChanges();
  }

  private setupPasswordValueChanges(): void {
    ['contrasenia', 'contrasenia2'].forEach(controlName => {
      this.formGroup.get(controlName)?.valueChanges.subscribe(value => {
        const otherControlName = controlName === 'contrasenia' ? 'contrasenia2' : 'contrasenia';

        if (value && value.length > 30) {
          const truncated = value.substring(0, 31);
          this.formGroup.get(controlName)?.setValue(truncated, { emitEvent: false });
          this.formGroup.get(controlName)?.setErrors({ maxlengthExceeded: true });
          this.formGroup.get(controlName)?.updateValueAndValidity({ emitEvent: false });
        } else if (this.formGroup.get(controlName)?.hasError('maxlengthExceeded')) {
          const errors = { ...this.formGroup.get(controlName)?.errors };
          delete errors['maxlengthExceeded'];
          this.formGroup.get(controlName)?.setErrors(Object.keys(errors).length ? errors : null);
        }

        // Sincronizar validación de coincidencia
        this.formGroup.get(otherControlName)?.updateValueAndValidity({ emitEvent: false });
      });
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  checkButton(): boolean {
    return this.formGroup.valid &&
      this.formGroup.get('contrasenia')?.value === this.formGroup.get('contrasenia2')?.value;
  }

  enviar(): void {
    if (!this.formGroup.valid || this.formGroup.get('contrasenia')?.value !== this.formGroup.get('contrasenia2')?.value) {
      this.formGroup.markAllAsTouched();
      return;
    }

    this.dialogRef.close({
      clave: this.formGroup.get('contrasenia')?.value,
      clave2: this.formGroup.get('contrasenia2')?.value
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  togglePasswordVisibility2(): void {
    this.showPassword2 = !this.showPassword2;
  }

  bloquearEvent(event: Event): void {
    event.preventDefault();
  }
}