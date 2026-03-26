import { GenericResponse } from "./response.common";

export interface ParticipanteResponse extends GenericResponse {
  data: Participante[];
}

export interface Participante {
  codigoAgrupacionPolitica: string;
  idFotoAgrupacionPolitica: string;
  dniCandidato: string;
  idFotoCandidato: string;
  nombreAgrupacionPolitica: string;
  nombreCandidato: string;
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  totalVotosValidos: number;
  posicion?: number;
}

export interface ObtenerParticipantesParams {
  agrupacionPolitica: string;
  idAmbitoGeografico: number;
  idEleccion: number;
  idUbigeoDepartamento: number;
  idUbigeoProvincia: number;
  idUbigeoDistrito: number;
  nombreCandidato: string;
  tipoFiltro: string;
}
