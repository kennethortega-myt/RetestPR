import {
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Output,
} from '@angular/core';

@Directive({
  selector: '[sceSoloAlfabeticos]',
  standalone: false,
})
export class SoloAlfabeticosDirective {
  @Output() ngModelChange: EventEmitter<any> = new EventEmitter();

  constructor(public element: ElementRef) {}

  @HostListener('input', ['$event']) onInputChange(event: Event) {
    const initalValue = this.element.nativeElement.value;
    this.element.nativeElement.value = initalValue.replace(
      /[^a-zA-ZÁÉÍÓÚáéíóúñÑ\s]/g,
      ''
    );
    if (initalValue !== this.element.nativeElement.value) {
      this.ngModelChange.emit(this.element.nativeElement.value);
      event.stopPropagation();
    }
  }
}
