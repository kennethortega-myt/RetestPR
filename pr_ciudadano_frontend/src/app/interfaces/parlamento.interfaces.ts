// PRESIDENTIALES TOTALES

import { IBodyToParams } from "../helpers/transformBodyParams";
import { BarChartItem } from "./common.interfaces";
import { GenericResponse } from "./response.common";

// ORGANIZACIONES POLITICAS PARA SELECCIONAR

export interface ParlPoliticOrganizationsForSelectResponse extends GenericResponse {
  data: ParlPoliticOrganizationsForSelectItem[];
}
export interface ParlPoliticOrganizationsForSelectItem {
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
}

// COMMON

export interface ParlCommonPresidentialParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel1: string;
  ubigeoNivel2: string;
  ubigeoNivel3: string;
}

// GEOGRAPHICAL LOCATION
export interface ParlGeographicalLocationParams extends ParlCommonPresidentialParams {}
export interface ParlGeographicalLocationResponse extends GenericResponse {
  data: ParlGeographicalLocationItem[];
}
export interface ParlGeographicalLocationItem extends BarChartItem {} // GENERIC BAR CHART INTERFACE

// POLITICAL ORGANIZATION
export interface ParlPoliticalOrganizationParams extends ParlCommonPresidentialParams {}
export interface ParlPoliticalOrganizationResponse extends GenericResponse {
  data: ParlPoliticalOrganizationItem[];
}
export interface ParlPoliticalOrganizationItem {
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
  nombreCandidato: string;
  dniCandidato: string;
  totalVotosValidos: number;
  porcentajeVotosValidos: number;
  porcentajeVotosEmitidos: number;

  urlCandidateImage?: string;
  urlAgrupacionImage?: string;
}

// GEOGRAPHICAL LOCATION NAME
export interface ParlGeographicalLocationNameParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  nombrePartidoPolitico?: string;
  ubigeoNivel1: string;
  ubigeoNivel2: string;
  ubigeoNivel3: string;
}
export interface ParlGeographicalLocationNameResponse extends GenericResponse {
  data: ParlGeographicalLocationNameItem[];
}
export interface ParlGeographicalLocationNameInfo {
  list: ParlGeographicalLocationNameItem[];
  emptyVotes: ParlGeographicalLocationNameItem;
  nullVotes: ParlGeographicalLocationNameItem;
  totals: ParlGeographicalLocationNameItem;
}
export interface ParlGeographicalLocationNameItem {
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
  nombreCandidato: string;
  dniCandidato: string;
  totalVotosValidos: number;
  porcentajeVotosValidos: number;
  porcentajeVotosEmitidos: number;

  urlCandidateImage?: string;
  urlAgrupacionImage?: string;
}

// GEOGRAPHICAL LOCATION
export interface ParlResultOfParticipantsParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
  idAgrupacionPolitica?: number;
  nombreCandidato?: string;
}
export interface ParlResultOfParticipantsItem {
  nombreAgrupacionPolitica: string; // mandatory
  codigoAgrupacionPolitica: string;
  nombreCandidato: string; // mandatory
  dniCandidato: string;
  totalVotosEmitidos: number;
  lista?: number;
}
export interface ParlResultOfParticipantsResponse extends GenericResponse {
  data: ParlResultOfParticipantsItem[];
}
