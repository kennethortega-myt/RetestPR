import { Component } from '@angular/core';
import { ROUTE_PATHS } from '../../../../../settings/app-routing.settings';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-modal-guardar',
  templateUrl: './modal-guardar.component.html',
  imports: [MatDialogModule, RouterModule],
})
export class ModalGuardarComponent {
  public redirectionRoute = `/home/${ROUTE_PATHS.lista_configuraciones}`;
}
