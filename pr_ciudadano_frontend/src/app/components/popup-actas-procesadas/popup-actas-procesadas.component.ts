import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-popup-actas-procesadas',
  templateUrl: './popup-actas-procesadas.component.html',
  standalone: false
})
export class PopupActasProcesadasComponent {
  public tituloPopupKey = 'pop-actas.titulo';
  public descripcionPopupKey = 'pop-actas.descripcion';
  public botonCerrarKey = 'pop-actas.cerrar';

  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(PopupActasProcesadasComponent);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }
}
