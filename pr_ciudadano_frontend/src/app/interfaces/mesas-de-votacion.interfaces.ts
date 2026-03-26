import { IBodyToParams } from "../helpers/transformBodyParams";
import { GenericResponse } from "./response.common";

export interface MesasDetailParams extends IBodyToParams {
  tipoFiltro?: string;
  ambitoGeografico?: number;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
}
export interface MesasDetailResponse extends GenericResponse {
  data: MesasDetail;
}
export interface MesasDetail {
  mesasInstaladas: number;
  mesasNoInstaladas: number;
  mesasPendientes: number;
}
