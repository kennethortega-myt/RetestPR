import { FormGroup, FormControl } from "@angular/forms";
import { IBodyToParams } from "../helpers/transformBodyParams";
import { BarChartItem } from "./common.interfaces";
import {
  IUbigeoFormValues,
  FilterFunctionality,
  SelectedFilters,
  FilterByLocationParams,
  RegionValue,
} from "./filtro-settings";
import { GenericResponse } from "./response.common";

export interface DepartmentParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico: number;
}
export interface DepartmentsResponse extends GenericResponse {
  data: Department[];
}
export interface Department {
  nombre: string;
  ubigeo: string;
}

// PROVINCES

export interface ProvinceParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico: number;
  idUbigeoDepartamento: string;
}
export interface ProvincesResponse extends GenericResponse {
  data: Province[];
}
export interface Province {
  idUbigeo?: string;
  nombre: string;
  ubigeo: string;
}

// DISTRICTS

export interface DistrictParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico: number;
  idUbigeoProvincia: string;
}
export interface DistrictsResponse extends GenericResponse {
  data: District[];
}
export interface District {
  idUbigeo?: string;
  nombre: string;
  ubigeo: string;
}

// LOCAL DE VOTACIÓN

export interface LocalParams extends IBodyToParams {
  idUbigeo: number;
}
export interface LocalVotacion {
  nombreLocalVotacion: string;
  codigoLocalVotacion: string;
}
export interface LocalsResponse extends GenericResponse {
  data: LocalVotacion[];
}

// REGIONES

export interface Region {
  codigo: number;
  nombre: string;
  ubigeo: string;
}
export interface RegionesResponse extends GenericResponse {
  data: Region[];
}

// INFORMACION PARA FILTROS INTERNACIONALES

export interface InternationalParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico: number;
  idUbigeo?: string;
}
export interface InternationalUbigeo {
  nombre: string;
  ubigeo: string;
}
export interface InternationalUbigeoResponse extends GenericResponse {
  data: InternationalUbigeo[];
}

// VOTE LOCATION

export interface VoteLocationResponse extends GenericResponse {
  data: VoteLocation[];
}
export interface VoteLocation {
  idUbigeo: string;
  ubigeo: string;
}

// PRESIDENTIAL ELECTION

export interface GeneralSummaryTotalsParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
}
export interface GeneralSummaryTotalsResponse extends GenericResponse {
  data: GeneralSummaryTotals;
}
export interface GeneralSummaryTotals {
  actasContabilizadas: number;
  totalActas: number;
  participacionCiudadana: number;
  actasEnviadasJee: number;
  actasPendientesJee: number;
  fechaActualizacion: number;
  idUbigeoDepartamento: number;
  idUbigeoProvincia: number;
  idUbigeoDistrito: number;
}

// ALL INFORMATION ABOUT ELECTION

export interface AllInformationAboutElection {
  organizations: PoliticalOrganization[];
  candidates: Candidate[];
  electoralTables: ElectoralTable[];
}

// POLITICAL ORGANIZATION

export interface PoliticalOrganizationsResponse extends GenericResponse {
  data: PoliticalOrganization[];
}
export interface PoliticalOrganization {
  ID_OP: string;
  DESC_OP: string;
}

// CANDIDATES

export interface CandidatesResponse extends GenericResponse {
  data: Candidate[];
}
export interface Candidate {
  ID_CANDIDATO: string;
  NOM_CANDIDATO: string;
}

// SUMMARY BY CANDIDATE

export interface SummaryByCandidateParams extends IBodyToParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  idUbigeoDepartamento?: string;
  idUbigeoProvincia?: string;
  idUbigeoDistrito?: string;
  idDistritoElectoral?: number;
}
export interface SummaryByCandidatesResponse extends GenericResponse {
  data: SummaryByCandidate[];
}
export interface SummaryByCandidate {
  codigoAgrupacionPolitica?: string;
  nombreAgrupacionPolitica: string;
  idFotoAgrupacionPolitica: string;
  nombreCandidato: string;
  idFotoCandidato: string;
  totalVotosValidos: number;
}

export interface SummaryByCandidate2 extends BarChartItem {}

// Revocatoria

export interface SumaryRevocatoria {
  nombreAgrupacionPolitica: string;
  codigoAgrupacionPolitica: number;
  cargo: string;
  candidato: Candidato[];
}

export interface Candidato {
  posicionOpcionVoto: number;
  porcentajeVotosValidos: number;
  porcentajeVotosEmitidos: number;
  descripcionOpcionVoto: string;
  codigoOpcionVoto: string;
  totalVotos: number;
}

export interface SummaryRevocatoriaByCandidatesResponse extends GenericResponse {
  data: SumaryRevocatoria[];
}

// TABLES

export interface ElectoralTablesResponse extends GenericResponse {
  data: ElectoralTable[];
}
export interface ElectoralTable {
  NUM_MESA: string;
  UBIGEO: string;
  REGION: string;
  ELECT_HABILES: number;
  DATA_PRESIDENCIAL: CandidateElectionInfo[];
  ACTAS_CONTABILIZADAS: number;
  ACTAS_JNE: number;
  ACTAS_PENDIENTES: number;
}
export interface CandidateElectionInfo {
  ESTADO: string;
  VOTOS_CANDIDATO: CandidateVotesDetail[];
  VOTOS_BLANCO: number;
  VOTOS_NULOS: number;
  VOTOS_VALIDOS: number;
}
export interface CandidateVotesDetail {
  ID_CANDIDATO: number;
  NOM_CANDIDATO: string;
  IMG_CANDIDATO: string;
  ID_OP: number;
  DESC_OP: string;
  IMG_OP: string;
  TOTAL_VOTOS: number;
  FAVORITO: boolean;
}

// ELECTION LIST

export interface IElectionTypesResponse extends GenericResponse {
  data: IElectionTypeBackend[];
}
export interface IElectionType {
  id: number;
  nombre: string;
  icono: string;
  orden: number;
  idEleccion: number;
  url: string; // this url is for redirect web page into web application
}

export interface IElectionTypeBackend {
  id: number;
  nombre: string;
  icono: string;
  orden: number;
  idEleccion?: number | string;
  url: string; // this url is for redirect web page into web application
}

export interface IBaseFiltroUbigeo {
  electionId: number;

  regiones: Region[];
  listRegiones: string[];
  listDepartamento: Department[];
  listProvincia: Province[];
  listDistrito: District[];
  listLocales: LocalVotacion[];
  listContinentals: InternationalUbigeo[];
  listCountries: InternationalUbigeo[];
  listStates: InternationalUbigeo[];
}

export interface IFiltroUbigeo extends IBaseFiltroUbigeo {
  ubigeoForm?: FormGroup<{
    region: FormControl<RegionValue>;
    department: FormControl<string>;
    province: FormControl<string>;
    district: FormControl<string>;
    location: FormControl<string>;
    continent: FormControl<string>;
    country: FormControl<string>;
    state: FormControl<string>;
  }>; // form group

  electoralDistrictForm?: FormGroup<{
    region: FormControl<number>;
  }>; // form group

  electoralRevocatoriaForm?: FormGroup<{
    region: FormControl<string>;
    location: FormControl<string>;
  }>; // form group

  revocatoriaForm?: FormGroup<{
    region: FormControl<string>;
    location: FormControl<string>;
  }>; // form group

  ubigeoFormValues?: IUbigeoFormValues;

  filterFunctionality?: FilterFunctionality;

  ubigeoInitialValue?: number;
  showCleanButton?: boolean;

  selectedUbigeoFormValues?: SelectedFilters | FilterByLocationParams;
  appliedUbigeoFormValues?: SelectedFilters | FilterByLocationParams;

  filterButtonIsDisabled?: boolean;
  filterButton2IsDisabled?: boolean;

  setRegion?(region: RegionValue): void;

  applyUbigeoFilters?(): void;
  applyElectoralDistrictFilters?(): void;
  applyElectoralDistrictFiltersForRevocatoria?(defaultUbigeo: string): void;

  updateInitialUbigeoFormValues?(): void;
  updateInitialUbigeoForAllTheWorld?(): void;
}
