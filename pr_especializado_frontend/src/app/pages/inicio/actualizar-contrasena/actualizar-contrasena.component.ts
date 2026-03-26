import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ComponentsModule } from '../../../components/components.module';
import { NgIf } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

export interface ActualizarContrasenaForm {
  contrasenia: FormControl<string | null>;
  contrasenia2: FormControl<string | null>;
}

@Component({
  selector: 'app-password-update',
  templateUrl: './actualizar-contrasena.component.html',
  styleUrl: './actualizar-contrasena.component.scss',
  imports: [NgIf, ReactiveFormsModule, MatIconModule, MatDialogModule, MatFormFieldModule, MatInputModule, ComponentsModule, TranslateModule],
})
export class ActualizarContrasenaComponent implements OnInit, OnDestroy {
  public formGroupParent!: FormGroup<ActualizarContrasenaForm>;
  showPassword = false;
  showPassword2 = false;
  onKeyDown: any;

  private readonly destroy$ = new Subject<void>();
  private readonly fb = inject(FormBuilder);

  constructor(public dialogRef: MatDialogRef<ActualizarContrasenaComponent>) { }

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.formGroupParent = this.fb.group<ActualizarContrasenaForm>({
      contrasenia: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30),
        this.passwordComplexityValidator()
      ]),
      contrasenia2: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30)
      ])
    });

    this.formGroupParent.get('contrasenia')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        this.handleMaxLengthValidation('contrasenia', value);
      });

    this.formGroupParent.get('contrasenia2')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        this.handleMaxLengthValidation('contrasenia2', value);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private handleMaxLengthValidation(controlName: keyof ActualizarContrasenaForm, value: string | null): void {
    if (!value) return;
    const control = this.formGroupParent.get(controlName);
    if (value.length > 30) {
      const truncated = value.substring(0, 31);
      control?.setValue(truncated, { emitEvent: false });
      control?.setErrors({ ...control.errors, maxlengthExceeded: true });
      control?.updateValueAndValidity({ emitEvent: false });
    } else if (control?.hasError('maxlengthExceeded')) {
      const errors = { ...control.errors };
      delete errors['maxlengthExceeded'];
      control.setErrors(Object.keys(errors).length ? errors : null);
    }
  }

  passwordComplexityValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const errors: ValidationErrors = {};

      // Mínimo 8 caracteres
      if (control.value.length < 8) {
        errors['complexityLength'] = true;
      }

      // Mayúscula, minúscula y dígito
      const hasUpper = /[A-Z]/.test(control.value);
      const hasLower = /[a-z]/.test(control.value);
      const hasDigit = /\d/.test(control.value);

      if (!hasUpper || !hasLower || !hasDigit) {
        errors['complexityRequiredChars'] = true;
      }

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }



  onNoClick(): void {
    this.dialogRef.close();
  }

  checkButton(): boolean {
    const contrasenia = this.formGroupParent.get('contrasenia')?.value;
    const contrasenia2 = this.formGroupParent.get('contrasenia2')?.value;

    return (
      this.formGroupParent.valid &&
      contrasenia === contrasenia2 &&
      contrasenia !== ''
    );
  }



  isComplexityInvalid(tipo: number, errorType: 'complexityLength' | 'complexityRequiredChars'): boolean {
    const controlName = tipo === 1 ? 'contrasenia' : 'contrasenia2';
    const control = this.formGroupParent.get(controlName);
    return !!(control?.hasError(errorType));
  }

  arePasswordsMismatched(): boolean {
    const contrasenia = this.formGroupParent.get('contrasenia')?.value;
    const contrasenia2 = this.formGroupParent.get('contrasenia2')?.value;

    return !!(contrasenia2 && contrasenia !== contrasenia2);
  }

  hasRequiredError(tipo: number): boolean {
    const controlName = tipo === 1 ? 'contrasenia' : 'contrasenia2';
    const control = this.formGroupParent.get(controlName);
    return !!(control?.hasError('required') && control?.touched);
  }


  enviar(): void {
    if (this.checkButton()) {
      const { contrasenia, contrasenia2 } = this.formGroupParent.value;
      this.dialogRef.close({ clave: contrasenia, clave2: contrasenia2 });
    }
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  togglePasswordVisibility2() {
    this.showPassword2 = !this.showPassword2;
  }

  bloquearEvent(event: any) {
    event.preventDefault();
  }
}
