import { FilterFunctionality, RegionValue, SelectedFilters } from "../interfaces/filtro-settings";
import {
  DISTRITO_ELECTORAL_EXTRENGERA_ID,
  GEOGRAPHIC_SCOPE,
  GEOGRAPHIC_SCOPE_EXTRANJERA,
  UBIGEO_LEVELS,
} from "./constantes";
import { getFilterTypeForBackend } from "./ubigeo-level.common";

export function getGenericFilterType(
  filterFunctionality: FilterFunctionality,
  region: RegionValue,
  filters?: SelectedFilters
): string {
  if (filterFunctionality == "only_regiones") {
    return UBIGEO_LEVELS.DISTRITO_ELECTORAL;
  }

  if (filterFunctionality == "peru_intern_and_all_join" || filterFunctionality == "peru_and_international") {
    if (region == "TODOS") {
      return UBIGEO_LEVELS.ELECTION;
    }

    return getFilterTypeForBackend(filters);
  }

  return UBIGEO_LEVELS.ELECTION;
}

export function getGenericGeographicalScope(
  filterFunctionality: FilterFunctionality,
  region: RegionValue,
  electoralDistrictId?: number
): number | null {
  if (filterFunctionality == "only_regiones") {
    return electoralDistrictId == DISTRITO_ELECTORAL_EXTRENGERA_ID ? GEOGRAPHIC_SCOPE_EXTRANJERA : GEOGRAPHIC_SCOPE;
  }

  if (region == "PERÚ") {
    return GEOGRAPHIC_SCOPE;
  }

  if (region == "EXTRANJERO") {
    return GEOGRAPHIC_SCOPE_EXTRANJERA;
  }

  return null;
}
