import { Component } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { ROUTE_PATHS } from '../../../../services/settings/app-routing.settings';

@Component({
  selector: 'app-modal-guardar',
  templateUrl: './modal-guardar.component.html',
  imports: [MatDialogModule, RouterModule],
})
export class ModalGuardarComponent {
  public redirectionRoute = `/home/${ROUTE_PATHS.lista_configuraciones}`;
}
