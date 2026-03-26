import { IBodyToParams } from "../helpers/transformBodyParams";
import { BaseUbigeo, GenericResponse } from "../interfaces/response.common";

// COMMONS
export interface CommonParams {
  idAmbitoGeografico?: number;
  idLocalVotacion?: number;
  tipoFiltro: string;
  ubigeoNivel01?: number;
  ubigeoNivel02?: number;
  ubigeoNivel03?: number;
}

// DEPARTMENTS
export interface DepartmentParams extends IBodyToParams {
  tipoFiltro?: string;
}
export interface DepartmentsDataDetail {
  totalElectoresHabiles: number;
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
}
export interface DepartmentsResponse extends GenericResponse {
  data: DepartmentsDataDetail[];
}

// TOTALS
export interface TotalesParams extends CommonParams, IBodyToParams {}
export interface TotalesDataDetail {
  idLocalVotacion: number;
  porcentajeAsistentes: number;
  porcentajeAusentes: number;
  totalAsistentes: number;
  totalAusentes: number;
  totalElectoresHabiles: number;
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
}
export interface TotalesResponse extends GenericResponse {
  data: TotalesDataDetail;
}

// UBIGEOS
export interface UbigeosParams extends CommonParams, IBodyToParams {}
export interface UbigeosDetail {
  idLocalVotacion: number;
  porcentajeAsistentes?: number;
  porcentajeAusentes?: number;
  ambitoGeografico?: number;
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
  name?: string; // this attribute is set after request
}
export interface UbigeosData {
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
  ubigeos: UbigeosDetail[];
}
export interface UbigeosResponse extends GenericResponse {
  data: UbigeosData;
}

// UBIGEOS TOTAL
export interface UbigeosTotalParams extends CommonParams {}

export interface UbigeosTotalResponse extends GenericResponse {
  data: UbigeosDetail[];
}

// MAPA DE CALOR
export interface MapaDeCalorParams extends IBodyToParams {
  idAmbitoGeografico?: number;
  idEleccion: number;
  ubigeoNivel01?: string;
  ubigeoNivel02?: string;
  ubigeoNivel03?: string;
  tipoFiltro: string;
  codigoAgrupacionPolitica?: string;
}
export interface MapaDeCalorParticipante {
  nombreCandidato: string;
  porcentajeVotosValidos: number;
  totalVotosValidos: number;
}
export interface MapaDeCalorData {
  actasContabilizadas: number;
  porcentajeActasContabilizadas: number;
  porcentajeAsistentes?: number;
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
  participante?: MapaDeCalorParticipante;
}
export interface MapaDeCalorResponse extends GenericResponse {
  data: MapaDeCalorData[];
}

export interface ParticipacionCiudadanaResponse extends GenericResponse {
  data: [ParticipacionCiudadano];
}

export interface ParticipacionCiudadano extends BaseUbigeo {
  totalElectoresHabiles: number;
}
