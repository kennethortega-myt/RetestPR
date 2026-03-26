import { Component } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';

interface Ipsum {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-filtro-ubicacion',
  templateUrl: './filtro-ubicacion.component.html',
  imports: [MatSelectModule],
})
export class FiltroUbicacionComponent {
  ipsums: Ipsum[] = [
    { value: 'valor-0', viewValue: 'Loren Ipsum 1' },
    { value: 'valor-1', viewValue: 'Loren Ipsum 2' },
    { value: 'valor-2', viewValue: 'Loren Ipsum 3' },
  ];
  selectedIpsum = this.ipsums[2].value;
}
