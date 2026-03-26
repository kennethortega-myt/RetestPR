import { GenericResponse } from "./response.common";

export interface ParticipanteResponse extends GenericResponse {
  data: Participante[];
}

export interface Participante {
  codigoAgrupacionPolitica: string;
  nombreAgrupacionPolitica: string;
  idFotoAgrupacionPolitica: string;
  
  dniCandidato: string;
  nombreCandidato: string;
  idFotoCandidato: string;
  
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  posicion: number;
  totalCandidatos?: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  urlAgrupacionImage: string;
}

export interface ParticipantePorCandidatoResponse extends GenericResponse {
  data: ParticipantePorCandidato[];
}
export interface ContenidoPaginadoCandidatoDiputado {
  content: ParticipantePorCandidato[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
}
export interface ParticipantePorCandidato {
  codigoAgrupacionPolitica: string;
  dniCandidato: string;
  idFotoAgrupacionPolitica: string;
  idFotoCandidato: string;
  lista: number;
  nombreAgrupacionPolitica: string;
  nombreCandidato: string;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  grupo?: number;
}

export interface AgrupacionResponse extends GenericResponse {
  data: Agrupacion[];
}

export interface Agrupacion {
  idAgrupacionPolitica: number;
  codigoAgrupacionPolitica?: string;
  nombreAgrupacionPolitica: string;
}

export interface CandidatoResponse extends GenericResponse {
  data: Candidato[];
  totalVotosPorOP?: number;
  porcentajeVotoEmitido?: number;
  porcentajeVotoValido?: number;
}

export interface Candidato {
  codigoAgrupacionPolitica: string;
  nombreAgrupacionPolitica: string;
  idAgrupacionPolitica: number;
  idFotoAgrupacionPolitica: string;
  dniCandidato: string;
  nombreCandidato: string;
  idFotoCandidato: string;
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  posicion: number;
  totalCandidatos: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  grupo: number;
  lista: number;
  urlAgrupacionImage: string;
}
