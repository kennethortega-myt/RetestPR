import { DetalleDataPresidencialBean } from "./detalle-data-presidencial-bean";
import { GenericResponse } from "./response.common";

export interface MesaBean {
  NUM_MESA: string;
  UBIGEO: string;
  REGION: string;
  ELECT_HABILES: number;
  DATA_PRESIDENCIAL: Array<DetalleDataPresidencialBean>;
  ACTAS_CONTABILIZADAS: number;
  ACTAS_JNE: number;
  ACTAS_PENDIENTES: number;
}

export interface MesaResponse extends GenericResponse {
  data: Mesa;
}

export interface Mesa {
  mesasInstaladas: number;
  mesasNoInstaladas: number;
  mesasPendientes: number;
}
