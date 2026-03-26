import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-c-input',
  templateUrl: './c-input.component.html',
  standalone: false
})
export class CInputComponent {
  @Input() placeholder: string = '';
  @Input() label: string = '';
  @Input() type: string = '';
}
