import { ElementRef } from "@angular/core";
import { GenericResponse } from "./response.common";
import { Resumen } from "./resumen-bean";
import { FilterByLocationParams, RegionValue } from "./filtro-settings";
import { IBodyToParams } from "../helpers/transformBodyParams";

export interface TotalsParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  idUbigeoDepartamento?: string;
  idUbigeoProvincia?: string;
  idUbigeoDistrito?: string;
  idDistritoElectoral?: number;
}
export interface TotalsResponse extends GenericResponse {
  data: Totals;
}
export interface Totals {
  contabilizadas?: number;
  actasContabilizadas: number;
  totalActas: number;
  participacionCiudadana: number;
  actasEnviadasJee: number;
  enviadasJee?: number;
  actasPendientesJee: number;
  pendientesJee?: number;
  fechaActualizacion: number;
  idUbigeoDepartamento: number;
  idUbigeoProvincia: number;
  idUbigeoDistrito: number;
  porcentajeVotosEmitidos: number;
  porcentajeVotosValidos: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
}

// BARS CHART INFORMATION

export interface BarsChartParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico: number;
  tipoFiltro: string;
  idUbigeoDepartamento: string;
  idUbigeoProvincia: string;
  idUbigeoDistrito: string;
}
export interface BarsChartInformationResponse extends GenericResponse {
  data: BarChartInfo[];
}
export interface BarChartInfo {
  nombreAgrupacionPolitica: string;
  idFotoAgrupacionPolitica: string;
  nombreCandidato: string;
  idFotoCandidato: string;
  totalVotosValidos: number;
}

export interface ILoadGeneralSummaryComponent {
  electionID?: number;
  electionId?: number;
  resumen: Resumen;
  regionValue: RegionValue;
  selectedFilterParams: FilterByLocationParams;
  elementRef: ElementRef;
}
