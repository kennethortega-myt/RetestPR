import { FilterByLocationParams, GenericFilterParams, SelectedFilters } from "../interfaces/filtro-settings";
import { ILoadGeneralSummaryComponent } from "../interfaces/resumen-general.interfaces";
import { UBIGEO_LEVELS } from "./constantes";

export function getFilterTypeForBackend(params?: FilterByLocationParams | SelectedFilters): string {
  if (params == undefined) {
    return UBIGEO_LEVELS.ALL_LABEL;
  }
  if (params.districtUbigeoId && params.districtUbigeoId != "" && Number(params.districtUbigeoId) > 0) {
    return UBIGEO_LEVELS.LEVEL_03;
  }
  if (params.provinceUbigeoId && params.provinceUbigeoId != "" && Number(params.provinceUbigeoId) > 0) {
    return UBIGEO_LEVELS.LEVEL_02;
  }
  if (params.departmentUbigeoId && params.departmentUbigeoId != "" && Number(params.departmentUbigeoId) > 0) {
    return UBIGEO_LEVELS.LEVEL_01;
  }
  return UBIGEO_LEVELS.ALL_LABEL;
}

export function getFilterTypeForBackend2(params?: GenericFilterParams): string {
  if (params == undefined) {
    return UBIGEO_LEVELS.ALL_LABEL;
  }
  if (params.ubigeoNivel3 && params.ubigeoNivel3 != "" && Number(params.ubigeoNivel3) > 0) {
    return UBIGEO_LEVELS.LEVEL_03;
  }
  if (params.ubigeoNivel2 && params.ubigeoNivel2 != "" && Number(params.ubigeoNivel2) > 0) {
    return UBIGEO_LEVELS.LEVEL_02;
  }
  if (params.ubigeoNivel1 && params.ubigeoNivel1 != "" && Number(params.ubigeoNivel1) > 0) {
    return UBIGEO_LEVELS.LEVEL_01;
  }
  return UBIGEO_LEVELS.ALL_LABEL;
}

export function scrollToMainSectionBase(parent: ILoadGeneralSummaryComponent): void {
  const element = parent.elementRef.nativeElement.querySelector(`#main-all-section`);
  if (element) {
    element.scrollIntoView({ behavior: "smooth", block: "start" });
  }
}

/**
 * El ubigeo debe tener al menos 5 caracteres o dígitos
 */
export function getUbigeoLevel02FromLevel03(ubigeoLevel03: string | number): string {
  const currentUbigeoLevel03 = String(ubigeoLevel03);
  const ubigeoLevel02 = currentUbigeoLevel03.slice(0, -2) + "00";
  return ubigeoLevel02;
}

/**
 * El ubigeo debe tener al menos 5 caracteres o dígitos
 */
export function getUbigeoLevel01FromLevel02(ubigeoLevel02: string | number): string {
  const currentUbigeoLevel02 = String(ubigeoLevel02);
  const ubigeoLevel01 = currentUbigeoLevel02.slice(0, -4) + "0000";
  return ubigeoLevel01;
}
