export type RLDisplayedColumnsTypeKeys =
  'fecha' | 
  'tipo' | 
  'porcentaje' |
  'ambito' | 
  'continente' | 
  'pais' | 
  'estadon' | 
  'local' | 
  'estado' | 
  'repositorio' |
  'descripcion' |
  'tipoReporte';

export const DISPLAYED_COLUMNS_KEY: RLDisplayedColumnsTypeKeys[] = [
  'fecha',
  'tipoReporte',
  'tipo',
  'porcentaje',
  'ambito',
  'continente',
  'pais',
  'estadon',
  'estado',
  'repositorio',
];

export type RLPeriodicElement = {
  [key in RLDisplayedColumnsTypeKeys]: string;
};

export interface IReportListTableInformation {
  dataSourceTotals: RLPeriodicElement[];
  pageSize: number;
  totalPages: number;
  totalRegisters: number;
  currentPage: number;
}