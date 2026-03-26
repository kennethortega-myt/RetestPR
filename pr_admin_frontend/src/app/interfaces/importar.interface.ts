export interface ImportarInterface {}

export interface IImportarRequest {
  acronimoProceso: string;
  getBd: number;
  getVistaEleccion: number;
  getVistaResumen: number;
  getVistaActa: number;
  getVistaParticipacionCiudadana: number;
  getVistaMesa: number;
  getVistaTotalCandidatosPorAgrupacionPolitica: number;
  getMenu: number;
}

export interface IImportarReqDto {
  etiqueta: string;
  sucess: boolean;
}

export interface IMaeImportar {
  activo?: boolean | null;
  descripcion?: string;
  icono?: string;
  value?: number;
  id?: number;
  etiqueta?: string;
  atributo: string;
  exito?: boolean;
}

export interface IMaeImportarComplemento {
  atributo: string;
  icono: string;
  descripcion?: string;
}

export interface IMaeImportarCandidato {
  value: number;
  icono: string;
  descripcion?: string;
}

export interface ImportarWsRequest {
  [key: string]: number;
}
