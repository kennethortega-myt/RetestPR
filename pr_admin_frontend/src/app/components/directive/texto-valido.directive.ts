import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[textoValido]',
  standalone: false,
})
export class TextoValidoDirective {
  private readonly regex: RegExp = /^[a-zA-Z0-9_.-]*$/;

  @HostListener('keypress', ['$event'])
  onKeyPress(event: KeyboardEvent) {
    const inputChar = event.key;
    if (!this.regex.test(inputChar)) {
      event.preventDefault(); // Bloquea el ingreso del carácter
    }
  }
}
