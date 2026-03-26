import { Directive } from '@angular/core';
import { NG_VALIDATORS, AbstractControl, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[validaPassword]',
  standalone: false,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: ValidaPasswordDirective,
      multi: true
    }
  ]
})
export class ValidaPasswordDirective implements Validator {

  validate(control: AbstractControl): ValidationErrors | null {
    const value = control.value;

    if (!value) return null; // no valida si está vacío (para que Validators.required actúe aparte)

    const regex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;

    console.log('value:', value)
    
    const valid = regex.test(value);

    return valid ? null : {
      validaPassword: {
        mensaje: 'La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un número.'
      }
    };
  }
}
