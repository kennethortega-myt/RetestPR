import dayjs from 'dayjs';
import { IReportListData } from '../services/reporte-api.service';
import { DatePipe } from '@angular/common';

export interface ReportesListadoRow {
  fecha: string;
  tipoReporte: string;
  tipoEleccion: string;
  porcentaje: string;
  ambito: string;
  dc: string;
  pp: string;
  de: string;
  estadoDescripcion: string;
  estadoCodigo: number;
  repositorio: string | number | null;
}

export interface ReportesListadoTableInfo {
  dataSourceTotals: ReportesListadoRow[];
  pageSize: number;
  totalPages: number;
  totalRegisters: number;
  currentPage: number;
}

export function mapDescargasReportRows(responseData: IReportListData[]): ReportesListadoRow[] {
  const sortedData = [...responseData].sort((a, b) => b.fechaConsulta - a.fechaConsulta);
  const datePipe = new DatePipe('es-PE');

  return sortedData.map((item) => ({
    fecha: datePipe.transform(item.fechaConsulta, 'dd/MM/yyyy hh:mm:ss a') ?? '',
    tipoReporte: item.tipoReporte,
    tipoEleccion: item.tipoEleccion,
    porcentaje: item.porcentajeActasContabilizadas === '' ? '0' : `${item.porcentajeActasContabilizadas}`,
    ambito: item.ambitoGeografico,
    dc: item.ubigeoNivel1,
    pp: item.ubigeoNivel2,
    de: item.ubigeoNivel3,
    estadoDescripcion: item.estadoDescripcion,
    estadoCodigo: Number(item.estado),
    repositorio: item.idArchivo ?? null,
  }));
}

export function mapAutomaticosReportRows(responseData: IReportListData[]): ReportesListadoRow[] {
  const sortedData = [...responseData].sort((a, b) => b.fechaConsulta - a.fechaConsulta);

  return sortedData.map((item) => ({
    fecha: dayjs(item.fechaConsulta).format('DD/MM/YYYY hh:mm a'),
    tipoReporte: item.tipoReporte,
    tipoEleccion: item.tipoEleccion,
    porcentaje: item.porcentajeActasContabilizadas,
    ambito: item.ambitoGeografico,
    dc: item.ubigeoNivel1,
    pp: item.ubigeoNivel2,
    de: item.ubigeoNivel3,
    estadoDescripcion: item.estadoDescripcion,
    estadoCodigo: Number(item.estado),
    repositorio: item.idArchivo ?? null,
  }));
}

export function getEstadoIconByRow(row: ReportesListadoRow): { icon: string; alt: string } {
  if (row.estadoDescripcion === 'Terminado' || row.estadoCodigo === 2) {
    return { icon: 'assets/icon-check.svg', alt: 'Terminado' };
  }
  if (row.estadoDescripcion === 'En Proceso' || row.estadoCodigo === 1) {
    return { icon: 'assets/icon-timer.svg', alt: 'En Proceso' };
  }
  if (row.estadoDescripcion === 'Sin Data en BD' || row.estadoCodigo === 3) {
    return { icon: 'assets/icono_no_data.svg', alt: 'Sin Data en BD' };
  }

  return { icon: 'assets/icono_no_data.svg', alt: 'No subido al sftp' };
}

export function hasRepositorio(repositorio: string | number | null | undefined): boolean {
  if (repositorio === null || repositorio === undefined) return false;
  return String(repositorio).trim().length > 0;
}

export function getRepositorioIcon(repositorio: string | number | null | undefined): string {
  return hasRepositorio(repositorio)
    ? 'assets/icon-file-active.svg'
    : 'assets/icon-file-inactive.svg';
}
