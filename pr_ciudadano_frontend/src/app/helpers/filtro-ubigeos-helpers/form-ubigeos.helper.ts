import { FormBuilder } from "@angular/forms";
import {
  GenericFilterParams,
  IDataForBreadcrumb,
  REGION_EXTRAJERO,
  REGION_PERU,
  REGION_TODOS,
  RegionValue,
  SelectedFilters,
} from "../../interfaces/filtro-settings";
import { DISTRITO_ELECTORAL_LIMA_ID, UBIGEO_LEVELS } from "../constantes";
import { formatPascalCaseInText } from "../basic-helpers/string.helper";
import { IBaseFiltroUbigeo } from "../../interfaces/elections.interfaces";
import { isRevocatoria } from "../storage-helpers/encrypt-storage.helper";

export function getUbigeoForm(formBuilder: FormBuilder) {
  return formBuilder.group({
    region: formBuilder.control<RegionValue>(REGION_TODOS, {
      nonNullable: true,
    }),
    // controls for Perú
    department: formBuilder.control<string>("", { nonNullable: true }),
    province: formBuilder.control<string>("", { nonNullable: true }),
    district: formBuilder.control<string>("", { nonNullable: true }),
    location: formBuilder.control<string>("", { nonNullable: true }),
    // controls for World
    continent: formBuilder.control<string>("", { nonNullable: true }),
    country: formBuilder.control<string>("", { nonNullable: true }),
    state: formBuilder.control<string>("", { nonNullable: true }),
  });
}

export function getElectoralDistrictForm(formBuilder: FormBuilder) {
  let idDistritoDefault = DISTRITO_ELECTORAL_LIMA_ID;
  if (isRevocatoria()) {
    idDistritoDefault = null;
  }
  return formBuilder.group({
    region: formBuilder.control<number>(idDistritoDefault, { nonNullable: true }),
  });
}

export function getElectoralRevocatoriaForm(formBuilder: FormBuilder) {
  return formBuilder.group({
    region: formBuilder.control<string>("", { nonNullable: true }),
    location: formBuilder.control<string>("", { nonNullable: true }),
  });
}

export function getRegionString(regionNumber?: number): RegionValue {
  if (!regionNumber) {
    return REGION_TODOS;
  }
  return regionNumber == 1 ? REGION_PERU : REGION_EXTRAJERO;
}

export function getBreadcrumbForSelectedFilters(data: IDataForBreadcrumb, params?: GenericFilterParams) {

  if (params?.tipoFiltro == UBIGEO_LEVELS.DISTRITO_ELECTORAL){
    return data.listRegiones.find(e => e.codigo === params.electoralDistrictId)?.nombre;
  }

  const breadcrumbArr: string[] = [];
  const regionValue = data.regionValue;
  breadcrumbArr.push(regionValue);

  if (!data.selectedUbigeoFormValues) {
    return formatPascalCaseInText(regionValue);
  }

  const { departmentUbigeoId: d, provinceUbigeoId: p, districtUbigeoId: ds } = data.selectedUbigeoFormValues;
  const isPeru = regionValue == REGION_PERU;
  
  if (isPeru) {
    if (d) {
      breadcrumbArr.push(data.listDepartamento?.find(e => e.ubigeo === String(d))?.nombre);
    }
    if (p) {
      breadcrumbArr.push(data.listProvincia?.find(e => e.ubigeo === String(p))?.nombre);
    }
    if (ds) {
      breadcrumbArr.push(data.listDistrito?.find(e => e.ubigeo === String(ds))?.nombre);
    }
  } else {
    if (d) {
      breadcrumbArr.push(data.listContinentals?.find(e => e.ubigeo === String(d))?.nombre);
    }
    if (p) {
      breadcrumbArr.push(data.listCountries?.find(e => e.ubigeo === String(p))?.nombre);
    }
    if (ds) {
      breadcrumbArr.push(data.listStates?.find(e => e.ubigeo === String(ds))?.nombre);
    }
  }

  const capilatizedBreadcrumbArr = breadcrumbArr.map((elem) => formatPascalCaseInText(elem));
  const breadcrumbString = capilatizedBreadcrumbArr.join(" / ");
  return breadcrumbString;
}

export function getIDataForBreadcrumb(
  params: GenericFilterParams,
  popupInformationInstance: IBaseFiltroUbigeo
): IDataForBreadcrumb {
  const regionValue = getRegionString(params.idAmbitoGeografico);
  const tipoFiltro = params.tipoFiltro;

  if (tipoFiltro == UBIGEO_LEVELS.DISTRITO_ELECTORAL){
    return {
      selectedUbigeoFormValues: {
        electoralDistrictId: params.electoralDistrictId
      },
      listRegiones: popupInformationInstance.regiones,
    } as IDataForBreadcrumb;
  }

  const ubigeos = [params.ubigeoNivel1, params.ubigeoNivel2, params.ubigeoNivel3];
  const selectedUbigeos = ubigeos.some((e) => e)
    ? ({
        departmentUbigeoId: params.ubigeoNivel1 ? String(params.ubigeoNivel1) : "",
        provinceUbigeoId: params.ubigeoNivel2 ? String(params.ubigeoNivel2) : "",
        districtUbigeoId: params.ubigeoNivel3 ? String(params.ubigeoNivel3) : "",
      } as SelectedFilters)
    : undefined;

  if (regionValue == REGION_PERU) {
    return {
      regionValue,
      selectedUbigeoFormValues: selectedUbigeos,
      listDepartamento: popupInformationInstance.listDepartamento,
      listProvincia: popupInformationInstance.listProvincia,
      listDistrito: popupInformationInstance.listDistrito,
    } as IDataForBreadcrumb;
  }
  if (regionValue == REGION_EXTRAJERO) {
    return {
      regionValue,
      selectedUbigeoFormValues: selectedUbigeos,
      listContinentals: popupInformationInstance.listContinentals,
      listCountries: popupInformationInstance.listCountries,
      listStates: popupInformationInstance.listStates,
    } as IDataForBreadcrumb;
  } else {
    return {
      regionValue,
    } as IDataForBreadcrumb;
  }
}
