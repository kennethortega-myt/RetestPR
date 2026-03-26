// PRESIDENTIALES TOTALES

import { IBodyToParams } from "../helpers/transformBodyParams";
import { BarChartItem } from "./common.interfaces";
import { GenericResponse } from "./response.common";

// ORGANIZACIONES POLITICAS PARA SELECCIONAR

export interface PoliticOrganizationsForSelectResponse extends GenericResponse {
  data: PoliticOrganizationsForSelectItem[];
}
export interface PoliticOrganizationsForSelectItem {
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
}

// COMMON

export interface CommonPresidentialParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
}

// GEOGRAPHICAL LOCATION
export interface GeographicalLocationParams extends CommonPresidentialParams, IBodyToParams {}
export interface GeographicalLocationResponse extends GenericResponse {
  data: GeographicalLocationItem[];
}
export interface GeographicalLocationItem extends BarChartItem {} // GENERIC BAR CHART INTERFACE

// POLITICAL ORGANIZATION
export interface PoliticalOrganizationParams extends CommonPresidentialParams, IBodyToParams {}
export interface PoliticalOrganizationResponse extends GenericResponse {
  data: PoliticalOrganizationItem[];
}
export interface PoliticalOrganizationItem {
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
export interface GeographicalLocationNameParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  nombreApellidoPartido?: string;
  ubigeoNivel1: string;
  ubigeoNivel2: string;
  ubigeoNivel3: string;
}
export interface GeographicalLocationNameResponse extends GenericResponse {
  data: GeographicalLocationNameItem[];
}
export interface GeographicalLocationNameInfo {
  list: GeographicalLocationNameItem[];
  listForScales: GeographicalLocationNameItem[];
  emptyVotes: GeographicalLocationNameItem;
  nullVotes: GeographicalLocationNameItem;
  totals: GeographicalLocationNameItem;
}
export interface GeographicalLocationNameItem extends BarChartItem{
  nombreAgrupacionPolitica: string;
  nombreCandidato: string;
  dniCandidato: string;
  totalVotosValidos: number;
  porcentajeVotosValidos: number;
  porcentajeVotosEmitidos: number;
  totalCandidatos?: number;

  urlCandidateImage?: string;
  urlAgrupacionImage?: string;
}

// GEOGRAPHICAL LOCATION
export interface ResultOfParticipantsParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
  idAgrupacionPolitica?: number;
  nombreCandidato?: string;
}
export interface ResultOfParticipantsItem {
  nombreAgrupacionPolitica: string; // mandatory
  codigoAgrupacionPolitica: string;
  nombreCandidato: string; // mandatory
  dniCandidato: string;
  totalVotosValidos: number;
  lista?: number;
}
export interface ResultOfParticipantsResponse extends GenericResponse {
  data: ResultOfParticipantsItem[];
  totalVotosPorOP?: number;
  porcentajeVotoEmitido?: number;
  porcentajeVotoValido?: number;
}

export interface DatosOP {
  totalVotosPorOP?: number;
  porcentajeVotoEmitido?: number;
  porcentajeVotoValido?: number;
}