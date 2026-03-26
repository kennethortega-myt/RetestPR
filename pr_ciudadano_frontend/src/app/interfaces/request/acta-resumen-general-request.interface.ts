export interface ActaResumentGeneralResquest {
    idProceso?: number;
    idEleccion: number;
    activo: number;
    ubigeoNivel03: number;
    idAmbitoGeografico: number;
    tipoFiltro: string;
    ubigeoNivel01: number;
    ubigeoNivel02: number;
}

export interface ActaResumenGeneralTotalRequest {
  idEleccion: number;
  idAmbitoGeografico: number;
  tipoFiltro: string;
  idUbigeoDepartamento?: number;
  idUbigeoProvincia?: number;
  idUbigeoDistrito?: number;
  idDistritoElectoral?: number;
}