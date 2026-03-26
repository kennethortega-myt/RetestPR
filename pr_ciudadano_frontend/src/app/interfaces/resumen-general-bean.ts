import { BaseUbigeo, GenericResponse } from "./response.common";

export interface ResumenGeneralResponse extends GenericResponse {
  data: ResumenGeneral[];
}

export interface ContentActaResumenGeneral {  
  content:[ResumenGeneral]
}
export interface ResumenGeneral {  
  id: number;
  nombre: string;
  actasContabilizadas: number;
  actasObservadasEnviadas: number;
  actasPendientes: number;
  participacionCiudadana: number;
  porcentajeActasContabilizadas: number;
  porcentajeActasObservadasEnviadas: number;
  porcentajeActasPendientes: number;
  porcentajeParticipacionCiudadana: number;
  totalActas: number;
  totalElectoresHabiles: number;
  distritoElectoral?: number;
  ubigeoDesc: string;
  ubigeoNivel03: string;  
}

export interface ResumenResponse extends GenericResponse {
  data: ResumenGeneral;
}
export interface MapaCalorResponse extends GenericResponse {
  data: [MapaCalor];
}

export interface AgrupacionPoliticaResponse extends GenericResponse {
  data: [AgrupacionPolitica];
}

export interface MapaCalor extends BaseUbigeo {
  idUbigeo: number;
  porcentajeActasContabilizadas: number;
  actasContabilizadas: number;
  ambitoGeografico: number;
  distritoElectoral?: number;
}

export interface AgrupacionPolitica {
  codigoAgrupacionPolitica: number;
  dniCandidato: string;
  idFotoAgrupacionPolitica: string;
  nombreAgrupacionPolitica: string;
  nombreCandidato: string;
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  totalVotosValidos: number;
}
