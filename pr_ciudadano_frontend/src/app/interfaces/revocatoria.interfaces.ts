import { IBodyToParams } from "../helpers/transformBodyParams";
import { SumaryRevocatoria } from "./elections.interfaces";
import { GenericResponse } from "./response.common";

// INTERFACES PARA LAS VISTAS DE REVOCATORIA

export const OPCIONES_DE_VOTO = {
  si: "SI",
  no: "NO",
};

export interface IRevocatoriaAutoridadItem {
  autoridadImgSrc: string;
  cargoAndRegion: string;
  nombre: string;
  sexo?: number;
  porcentageSI: number;
  porcentageNO: number;
  numeroDeVotosSI: number;
  numeroDeVotosNO: number;
  porcentageBarra: string;
  restanteBarra: string;
  ubigeoNivel03: number;
  codigoAgrupacionPolitica: string;
}

export interface IRevocatoriaAutoridadInformation {
  numberOfAutoridades: number;
  tipoDeAutoridad: string;
  items: IRevocatoriaAutoridadItem[];
}

// interface para el backend

export interface InformationDistritalPorCargo extends IBodyToParams {
  idEleccion: number;
  tipoFiltro: string;
  idAmbitoGeografico: number;
  ubigeoNivel1: string;
  ubigeoNivel2: string;
  ubigeoNivel3: string;
  codigoAgrupacionPolitica: string;
}

/**
 * Estos valores serán usados en la URL para traer data de participantes en revocatoria
 */
export type categoryTypeForParticipant = "Alcalde" | "Regidor";

// RESUMEN DE REVOCATORIA

export interface RevocatoriaResumenResponse extends GenericResponse {
  data: RevocatoriaResumenResponseData;
}

export interface RevocatoriaResumenResponseData {
  total: number;
  totalAlcaldes: number;
  totalRegidores: number;
}

// LISTA DE AUTORIDADES PARA REVOCATORIA

export interface AutoridadesRevocatoriaInformationResponse extends GenericResponse {
  data: AutoridadesRevocatoriaInformationResponseData[];
}

export interface AutoridadesRevocatoriaInformationResponseData {
  ubigeoNivel03: number;
  ubigeoDesc: string;
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
  cargo: string;
  sexo?: number;
  candidato: AutoridadRevocatoria[];
}

export interface AutoridadRevocatoria {
  posicionOpcionVoto: number;
  codigoOpcionVoto: string;
  descripcionOpcionVoto: string;
  totalVotos: number;
  porcentajeVotosValidos?: number;
  porcentajeVotosEmitidos: number;
}

export interface CargoParaRevocatoriaContentItem {
  detalle: SumaryRevocatoria[];
  ubigeoNivel03: number;
  ubigeoDesc: string;
}

// DETALLE DE ALCALDE DISTRITAL

export interface InformationDistritalPorCargoResponse extends GenericResponse {
  data: InformationDistritalPorCargoResponseData[];
}

export interface InformationDistritalPorCargoResponseData {
  nombreCandidato: string;
  dniCandidato: string;
  totalVotosValidos: number;
  totalVotosEmitidos: number;
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
  candidato: Candidato[];
}

export interface Candidato {
  votos: number;
  posicionOpcionVoto: number;
  codigoOpcionVoto: string;
  descripcionOpcionVoto: string;
  porcentajeVotosValidos: number;
  porcentajeVotosEmitidos: number;
  porcentageBarra?: string;
}

// interface para el frontend

export interface RecovatoriaResumenData {
  total: number;
  totalAlcaldesDistritales: number;
  totalRegidoresDistritales: number;
  totalAlcaldesProvinciales: number;
  totalRegidoresProvinciales: number;
  totalGobernadores: number;
  totalConsejeros: number;
}
