import { IBodyToParams } from "../helpers/transformBodyParams";

export interface IGetActasObserbadasParams extends IBodyToParams {
  codigoLocalVotacion: string;
  idAmbitoGeografico: number;
  idUbigeo: number;
  resueltas?: boolean;
  descripcionActaResolucion?: string;
}
