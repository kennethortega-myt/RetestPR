import { IBodyToParams } from "../helpers/transformBodyParams";
import { GenericResponse } from "./response.common";

export interface CandidatoReponse extends GenericResponse {
  data: Candidato[];
  totalVotosPorOP?: number;
  porcentajeVotoEmitido?: number;
  porcentajeVotoValido?: number;
}

export interface Candidato {
  codigoAgrupacionPolitica: string;
  dniCandidato: string;
  idAgrupacionPolitica: number;
  idFotoAgrupacionPolitica: string;
  idFotoCandidato: string;
  nombreAgrupacionPolitica: string;
  nombreCandidato: string;
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  posicion: number;
  totalCandidatos: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  grupo: number;
  lista: number;
}
export interface RequestCandidatoBase extends IBodyToParams {
  idAgrupacionPolitica?: number;
  idDistritoElectoral?: number;
  idEleccion?: number;
  tipoFiltro?: string;
}
