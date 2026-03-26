import { formatPascalCaseInText } from "./basic-helpers/string.helper";
import { REGION_PERU, RegionValue } from "../interfaces/filtro-settings";
import { UBIGEO_DESCRIPTION_TEXTS } from "../constants/participacion-ciudadana.constants";

export function getUbigeoDescriptionForNivel01(regionValue: RegionValue, departmentName: string) {
  if (!departmentName) {
    return "";
  }
  return regionValue === REGION_PERU
    ? UBIGEO_DESCRIPTION_TEXTS.descripcion_departamento.replace(
        "[departamento]",
        formatPascalCaseInText(departmentName)
      )
    : UBIGEO_DESCRIPTION_TEXTS.descripcion_continente.replace("[continente]", formatPascalCaseInText(departmentName));
}

export function getUbigeoDescriptionForNivel02(regionValue: RegionValue, provinceName: string) {
  if (!provinceName) {
    return "";
  }
  return regionValue === REGION_PERU
    ? UBIGEO_DESCRIPTION_TEXTS.descripcion_provincia.replace("[provincia]", formatPascalCaseInText(provinceName))
    : UBIGEO_DESCRIPTION_TEXTS.descripcion_pais.replace("[pais]", formatPascalCaseInText(provinceName));
}

export function getUbigeoDescriptionForNivel03(districtName: string) {
  if (!districtName) {
    return "";
  }
  return UBIGEO_DESCRIPTION_TEXTS.descripcion_distrito.replace("[distrito]", formatPascalCaseInText(districtName));
}
