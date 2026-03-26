import { Component } from "@angular/core";

@Component({
  selector: "app-footer",
  templateUrl: "./footer.component.html",
  standalone: false,
})
export class FooterComponent {
  public labelOficinaKey = 'footer.labelOficina';
  public textDireccionKey = 'footer.textDireccion';
  public labelTelefonoKey = 'footer.labelTelefono';
  public textTelefonoKey = 'footer.textTelefono';
  public labelEscribenosKey = 'footer.labelEscribenos';
  public textEscribenosKey = 'footer.textEscribenos';
  public labelRedesKey = 'footer.labelRedes';
}
