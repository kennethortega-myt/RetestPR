import { Component, Input } from "@angular/core";
import { MesasDetail } from "../../interfaces/mesas-de-votacion.interfaces";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: "app-resumen-mesas",
  templateUrl: "./resumen-mesas.component.html",
  standalone: false,
})
export class ResumenMesasComponent {
  public MesasInstaKey = 'resumen-mesas.mesasInstaladas';
  public MesasNoInstaKey = 'resumen-mesas.mesasNoInstaladas';
  public MesasPendKey = 'resumen-mesas.mesasPendientes';

  @Input() mesasDetail: MesasDetail = {} as MesasDetail;
}
