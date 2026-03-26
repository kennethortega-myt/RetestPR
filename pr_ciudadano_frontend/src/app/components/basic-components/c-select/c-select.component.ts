import { Component, Input } from '@angular/core';

interface Demoselect {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-c-select',
  templateUrl: './c-select.component.html',
  standalone: false
})
export class CSelectComponent {
  demos: Demoselect[] = [
    {value: 'Select-0', viewValue: 'Opción 1'},
    {value: 'Select-1', viewValue: 'Opción 2'},
    {value: 'Select-2', viewValue: 'Opción 3'},
  ];
  @Input() placeholder: string = '';
  @Input() label: string = '';
}
