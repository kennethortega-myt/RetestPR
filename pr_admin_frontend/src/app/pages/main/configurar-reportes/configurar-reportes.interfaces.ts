import { getAllHourListFromDay } from "../../../helpers/horas.helper";


export interface ISelectableItem {
  value: number;
  text: string;
}

export interface IConfigEleccionesItem extends ISelectableItem {}
export interface IPeriodoItem extends ISelectableItem {}
export interface IHoraDeInicioItem {
  value: string;
  text: string;
}

// Solo mantener el constante de tiempo
export const PERIODO_IN_TIME = 1;

// Solo períodos de tiempo
export const PERIODO_EN_TIEMPO: IPeriodoItem[] = [
  { text: 'Cada 30 min', value: 30 },
  { text: 'Cada 1 hora', value: 60 },
  { text: 'Cada 2 horas', value: 120 },
  { text: 'Cada 4 horas', value: 240 },
];

export const LISTA_DE_HORAS_DE_INICIO = getAllHourListFromDay(30);

export interface IConfigRequestParams {
  eleccion: string;
  eleccionId: number;
  fechaInicio: string;
  horaInicio: string;
  id?: string;
  estado?: number;
  tipoReporte: number;
  tipoGeneracionReporte: number;
  tipoGeneracionReporteVal: number;
}
