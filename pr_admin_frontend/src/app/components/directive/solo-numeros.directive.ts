import {
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Output,
} from '@angular/core';

@Directive({
  selector: '[sceSoloNumeros]',
  standalone: false,
})
export class SoloNumerosDirective {
  @Output() ngModelChange: EventEmitter<any> = new EventEmitter();

  constructor(public element: ElementRef) {}

  @HostListener('input', ['$event']) onInputChange(event: Event) {
    const initialValue = this.element.nativeElement.value;
    this.element.nativeElement.value = initialValue.replace(/\D/g, '');
    if (initialValue !== this.element.nativeElement.value) {
      this.ngModelChange.emit(this.element.nativeElement.value);
      event.stopPropagation();
    }
  }
}
