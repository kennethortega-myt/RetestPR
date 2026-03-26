import { getAllHourListFromDay } from '../helpers/horas.helper';

export interface ISelectableItem {
  value: number;
  text: string;
}

export interface IConfigEleccionesItem extends ISelectableItem {}
export interface ITipoDePeriodoItem extends ISelectableItem {}
export interface IPeriodoItem extends ISelectableItem {}

export const PERIODO_IN_TIME = 1;
export const PERIODO_IN_PERCENTAGE = 2;
export const TIPOS_DE_PERIODOS: ITipoDePeriodoItem[] = [
  { text: 'Tiempo', value: PERIODO_IN_TIME },
  { text: 'Porcentaje', value: PERIODO_IN_PERCENTAGE },
];

export const PERIODO_EN_PORCENTAGES: IPeriodoItem[] = [
  { text: 'Cada 5 %', value: 5 },
  { text: 'Cada 10 %', value: 10 },
  { text: 'Cada 20 %', value: 20 },
];

export const PERIODO_EN_TIEMPO: IPeriodoItem[] = [
  { text: 'Cada 30 min', value: 30 },
  { text: 'Cada 1 hora', value: 60 },
  { text: 'Cada 2 horas', value: 120 },
  { text: 'Cada 4 horas', value: 240 },
];

export const LISTA_DE_HORAS_DE_INICIO = getAllHourListFromDay(30);

export interface IConfigRequestParams {
  id?: string;
  eleccion: string;
  eleccionId: number;
  fechaInicio: string;
  horaInicio: string;
  usuario?: string;
  estado?: number;
  tipoReporte: number;
  tipoGeneracionReporte: number;
  tipoGeneracionReporteVal: number;
}
