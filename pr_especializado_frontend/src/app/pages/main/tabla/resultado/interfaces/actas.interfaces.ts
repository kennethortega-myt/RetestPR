import { MatTableDataSource } from '@angular/material/table';
import { MobileTableRow } from '../../../../../components/c-table-mobile/c-table-mobile.component';

export type displayedColumnsTypeKeys =
  // | 'tipo'
  | 'ambito'
  | 'departamento'
  | 'provincia'
  | 'distrito'
  | 'local'
  | 'mesa'
  | 'electores'
  | 'votos'
  | 'estadoacta'
  | 'documento';

export const DISPLAYED_COLUMNS_KEY: displayedColumnsTypeKeys[] = [
  // 'tipo',
  'ambito',
  'departamento',
  'provincia',
  'distrito',
  'mesa',
  'local',
  'electores',
  'votos',
  'estadoacta',
  'documento',
];

export type PeriodicElement = {
  [key in displayedColumnsTypeKeys]: string | string[];
};

// 👇 CAMBIADO: ahora el datasource es MatTableDataSource
export interface ITableInformation {
  dataSource: MatTableDataSource<MobileTableRow>;
  pageSize: number;
  totalPages: number;
  totalRegisters: number;
  currentPage: number;
}
