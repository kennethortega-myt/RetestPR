import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-popup-causales-observacion',
  imports: [MatButtonModule, MatDialogModule, TranslateModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './popup-causales-observacion.component.html',
  styleUrl: './popup-causales-observacion.component.scss',
})
export class PopupCausalesObservacionComponent {
  public TituloKey = 'pop-causales.titulo';
  public ActasObservadasKey = 'pop-causales.actas-observadas';
  public NKey = 'pop-causales.n';
  public TipoKey = 'pop-causales.tipo';
  public Descripcionkey = 'pop-causales.descripcion';
  public ActasObsKey = 'pop-causales.actasObs';
  public SinDatosKey = 'pop-causales.sinDato';
  public SinDatosDescKey = 'pop-causales.sinDatoDesc';
  public IncompletaKey = 'pop-causales.incompleta';
  public incompletaDescKey = 'pop-causales.inconpletaDesc';
  public errorAritmetricoKey = 'pop-causales.errorAritmetrico';
  public ErrorMaterialDescKey = 'pop-causales.errorMaterialDesc';
  public IlegibilidadKey = 'pop-causales.ilegibilidad';
  public IlegibilidadDescKey = 'pop-causales.ilegibilidadDesc';
  public SinFirmasKey = 'pop-causales.sinFirmas';
  public SinFirmasDescKey = 'pop-causales.sinFirmasDesc';
  public VotosImpugnadosKey = 'pop-causales.votosImpugnados';
  public VotosImpugnadosDescKey = 'pop-causales.votosImpugnadosDesc';
  public SolicitudNulidadKey = 'pop-causales.solicitudNulidad';
  public SolicitudNulidadDescKey = 'pop-causales.solicitudNulidadDesc';
  public ActaExtraviadaKey = 'pop-causales.actaExtravida';
  public ActaExtraviadaDescKey = 'pop-causales.actaExtravidaDesc';
  public ActaSiniestradaKey = 'pop-causales.actaSiniestrada';
  public ActaSiniestradaDescKey = 'pop-causales.actaSiniestradaDesc';
  public ObservacionesMultiplesKey = 'pop-causales.obsrvacionesMultiples';
  public ObservacionesMultiplesDescKey = 'pop-causales.obsrvacionesMultiplesDesc';
  public BotonCerrarKey = 'pop-causales.cerrar';

  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(PopupCausalesObservacionComponent);

    dialogRef.afterClosed().subscribe(result => {});
  }
}
