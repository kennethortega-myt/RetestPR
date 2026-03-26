import { Ubigeo } from "./ubigeo-bean";
import { Department, District, InternationalUbigeo, Province, Region } from "./elections.interfaces";

export function getOptimizedObject<T extends { [key: string]: any }>(object: T): T {
  Object.keys(object).forEach((key) => {
    if (object[key] == undefined || object[key] == null || object[key] == "" || object[key] == "0") {
      delete object[key];
    }
  });
  return object;
}

export function getOptimizedObjectRevoca<T extends { [key: string]: any }>(object: T): T {
  Object.keys(object).forEach((key) => {
    if (object[key] == undefined || object[key] == null || object[key] == "" || object[key] == "0" || object[key] == "TODOS") {
      delete object[key];
    }
  });
  return object;
}

export const REGION_TODOS = "TODOS";
export const REGION_PERU = "PERÚ";
export const REGION_EXTRAJERO = "EXTRANJERO";// For only_regiones
export const REGION_PERU_METROPOLITAN_LIMA = "LIMA METROPOLITANA";
export const REGION_NEW_VALUES = [REGION_TODOS, REGION_PERU, REGION_EXTRAJERO];
export const REGION_VALUES = [REGION_PERU, REGION_EXTRAJERO];
export type RegionValue = "PERÚ" | "EXTRANJERO" | "TODOS" | 'LIMA METROPOLITANA';


export interface IUbigeoFormValues {
  region?: RegionValue;
  departmentUbigeo?: string;
  provinceUbigeo?: string;
  districtUbigeo?: string;
  location?: string;
  continent?: string;
  country?: string;
  state?: string;
}

export type FilterFunctionality = "peru_and_international" | "only_peru" | "only_regiones" | "peru_and_regiones" | "peru_intern_and_all_join";

export interface FiltroBusqueda {
  departmentUbigeoId: number;
  provinceUbigeoId: number;
  districtUbigeoId: number;
}

export interface GenericFilterParams {
  idAmbitoGeografico?: number;
  tipoFiltro?: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
  electoralDistrictId?: number;
}

export interface FilterParamsWithLists extends GenericFilterParams {
  listRegiones?: string[];
  listDepartamento?: Ubigeo[];
  listProvincia?: Ubigeo[];
  listDistrito?: Ubigeo[];
  listContinentals?: Ubigeo[];
  listCountries?: Ubigeo[];
  listStates?: Ubigeo[];
}

export interface FilterBase {
  departmentUbigeoId?: string;
  provinceUbigeoId?: string;
  districtUbigeoId?: string;
  electoralDistrictId?: number;
  revocatoriaDistrictId?: number;
  codigoLocalVotacion?: number;
  regionString?: RegionValue;
}

export interface SelectedFilters extends FilterBase {
  region?: RegionValue;
}

export interface FilterByLocationParams extends FilterBase {
  region?: number;
}

export interface IDataForBreadcrumb {
  regionValue: RegionValue;
  selectedUbigeoFormValues?: SelectedFilters;
  listDepartamento: Department[];
  listProvincia: Province[];
  listDistrito: District[];
  listContinentals: InternationalUbigeo[];
  listCountries: InternationalUbigeo[];
  listStates: InternationalUbigeo[];
  listRegiones?: Region[];
}
