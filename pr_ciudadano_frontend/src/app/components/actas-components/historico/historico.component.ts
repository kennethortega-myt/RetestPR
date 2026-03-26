import { Component, OnInit, Input } from "@angular/core";

import { obtenerImagenForActa } from "../../../helpers/actas.obtener-imagen";
import { ACTA_LINEAT_CODIGO_ESTADO } from "../../../helpers/constantes";
import { EstadoActa } from "../../../helpers/estado-enum";
import { LineaTiempo, Mesa } from "../../../interfaces/acta-bean";

@Component({
  selector: "app-historico",
  templateUrl: "./historico.component.html",
  standalone: false,
})
export class HistoricoComponent implements OnInit {
  ACTA_LINEAT_CODIGO_ESTADO = ACTA_LINEAT_CODIGO_ESTADO;

  @Input() estado: string | object;
  @Input() lineaTiempo: LineaTiempo[];
  @Input() detalleActa: Mesa;
  observado: boolean = false;
  pendiente: boolean = false;
  contabilizada: boolean = true;

  public lineaTiempoText = 'historico.lineaTiempoText';

  ngOnInit() {
    this.actualizarEstado(this.estado);
  }

  constructor() {
    this.actualizarEstado(this.estado);
  }

  actualizarEstado(estado: string | object) {
    if (estado == EstadoActa.OBSERVADA) {
      this.observado = true;
      this.pendiente = true;
      this.contabilizada = true;
    } else if (estado == EstadoActa.PENDIENTE) {
      this.observado = false;
      this.pendiente = true;
      this.contabilizada = false;
    } else if (estado == EstadoActa.CONTABILIZADA) {
      this.observado = false;
      this.pendiente = true;
      this.contabilizada = true;
    }
  }

  obtenerIcono(lineaTiempo: LineaTiempo): string {
    return obtenerImagenForActa(lineaTiempo.codigoEstadoActa);
  }
  obtenerImagen(codigoEstadoActa: string): string {
    return obtenerImagenForActa(codigoEstadoActa);
  }
}
