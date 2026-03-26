import { MainHotMapComponent } from "../components/main-hot-map/main-hot-map.component";
import { FilterByLocationParams } from "../interfaces/filtro-settings";

export function commonUpdateMapFromFilter(filters: FilterByLocationParams, mainHotMapComponent: MainHotMapComponent) {
  const { region } = filters;
  if (existUbigeoDepProDis(filters)) {
    reloadMapFromUbigeo(filters, mainHotMapComponent);
  } else if (region == 1) {
    mainHotMapComponent.loadInitialUbigeoPeru();
  } else if (region == 2) {
    mainHotMapComponent.loadInitialUbigeoInternational();
  } else {
    mainHotMapComponent.loadInitialUbigeoWorld();
  }
}

function existUbigeoDepProDis(filters: FilterByLocationParams) {
  const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = filters;
  return districtUbigeoId || provinceUbigeoId || departmentUbigeoId;
}

function reloadMapFromUbigeo(filters: FilterByLocationParams, mainHotMapComponent: MainHotMapComponent): void {
  const { region, departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = filters;

  if (districtUbigeoId) {
    loadDistrict(region!, mainHotMapComponent, departmentUbigeoId!, provinceUbigeoId!, districtUbigeoId);
  } else if (provinceUbigeoId) {
    loadProvince(region!, mainHotMapComponent, departmentUbigeoId!, provinceUbigeoId);
  } else {
    loadDepartmentOrContinent(region!, mainHotMapComponent, departmentUbigeoId!);
  }
}

function loadDistrict(
  region: number,
  mainHotMapComponent: MainHotMapComponent,
  departmentUbigeoId: string,
  provinceUbigeoId: string,
  districtUbigeoId: string
): void {
  if (region === 1) {
    mainHotMapComponent.loadUbigeoDistrict(departmentUbigeoId, provinceUbigeoId, districtUbigeoId);
  } else if (region === 2) {
    mainHotMapComponent.loadUbigeoState(departmentUbigeoId, provinceUbigeoId, districtUbigeoId);
  }
}

function loadProvince(
  region: number,
  mainHotMapComponent: MainHotMapComponent,
  departmentUbigeoId: string,
  provinceUbigeoId: string
): void {
  if (region === 1) {
    mainHotMapComponent.loadUbigeoProvince(departmentUbigeoId, provinceUbigeoId);
  } else if (region === 2) {
    mainHotMapComponent.loadUbigeoCountry(departmentUbigeoId, provinceUbigeoId);
  }
}

function loadDepartmentOrContinent(
  region: number,
  mainHotMapComponent: MainHotMapComponent,
  departmentUbigeoId: string
): void {
  if (region === 1) {
    mainHotMapComponent.loadUbigeoDepartamento(departmentUbigeoId);
  } else if (region === 2) {
    mainHotMapComponent.loadUbigeoContinent(departmentUbigeoId);
  }
}
