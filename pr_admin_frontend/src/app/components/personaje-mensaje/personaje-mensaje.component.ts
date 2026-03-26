import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-personaje-mensaje',
  templateUrl: './personaje-mensaje.component.html',
  standalone: false,
})
export class PersonajeMensajeComponent {
  @Input({ required: true }) mensaje: string = '';
}
