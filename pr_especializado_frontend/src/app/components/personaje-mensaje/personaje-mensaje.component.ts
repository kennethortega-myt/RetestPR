import { Component, Input } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-personaje-mensaje',
  templateUrl: './personaje-mensaje.component.html',
  imports: [TranslateModule],
})
export class PersonajeMensajeComponent {
  @Input({ required: true }) mensaje: string = '';
  @Input() mensajeParams?: Record<string, string | number | boolean | null | undefined>;
}
