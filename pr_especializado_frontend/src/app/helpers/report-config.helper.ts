import dayjs from 'dayjs';
import { EnumIdEleccion } from './enums';
import { formatNameElection } from './funciones';

export interface ReportConfigDataLike {
  eleccion: string;
  eleccionId: number;
  fechaInicio: string;
  horaInicio: string;
  id: string;
  tipoReporte: number;
  tipoGeneracionReporte: number;
  tipoGeneracionReporteVal: number;
  estado?: number;
}

export interface ReportConfigFrontLike {
  icono: string;
  tipoDeEleccion: string;
  tipoDePeriodo: string;
  fechaDeInicio: string;
  periodo: string;
  horaDeInicio: string;
  id?: string;
  estado?: number;
  tipoGeneracionReporteVal?: number;
  fechaInicio?: string;
}

const ELECTION_ICON_MAP: Record<number, string> = {
  [EnumIdEleccion.ID_ELECCION_PRESIDENCIAL]: 'assets/icons-administrador/icono_presidencial.svg',
  [EnumIdEleccion.ID_ELECCION_PARLAMENTO_ANDINO]: 'assets/icons-administrador/icono_parlamentoandino.svg',
  [EnumIdEleccion.ID_ELECCION_CONGRESAL]: 'assets/icons-administrador/icono_diputados.svg',
  [EnumIdEleccion.ID_ELECCION_DIPUTADOS]: 'assets/icons-administrador/icono_diputados.svg',
  [EnumIdEleccion.ID_ELECCION_SENADORES_MULTIPLE]: 'assets/icons-administrador/icono_senadoresDEM.svg',
  [EnumIdEleccion.ID_ELECCION_SENADORES_UNICO]: 'assets/icons-administrador/icono_senadoresDEU.svg',
};

export function getReportIconFromElectionId(electionId: number): string {
  return ELECTION_ICON_MAP[electionId] ?? '';
}

export function getReportHour(hora: string): string {
  const horaArr = hora.split('.');
  return `${horaArr[0]} horas`;
}

export function getReportPeriod(tipoGeneracionReporte: number, tipoGeneracionReporteVal: number): string {
  if (!tipoGeneracionReporteVal) return '-';

  if (tipoGeneracionReporte === 1) {
    if (tipoGeneracionReporteVal < 60) return `${tipoGeneracionReporteVal} minutos`;

    const horas = tipoGeneracionReporteVal / 60;
    return `${horas} ${horas === 1 ? 'hora' : 'horas'}`;
  }

  if (tipoGeneracionReporte === 2) {
    return `${tipoGeneracionReporteVal} %`;
  }

  return '';
}

export function mapReportConfigList(list: ReportConfigDataLike[]): ReportConfigFrontLike[] {
  return list
    .map((elem): ReportConfigFrontLike => ({
      id: elem.id,
      fechaDeInicio: dayjs(elem.fechaInicio).format('DD/MM/YYYY'),
      horaDeInicio: getReportHour(elem.horaInicio),
      icono: getReportIconFromElectionId(elem.eleccionId),
      periodo: getReportPeriod(elem.tipoGeneracionReporte, elem.tipoGeneracionReporteVal),
      tipoDeEleccion: formatNameElection(elem.eleccion),
      tipoDePeriodo: elem.tipoReporte === 1 ? 'Tiempo' : 'PORCENTAJE',
      tipoGeneracionReporteVal: elem.tipoGeneracionReporteVal,
      fechaInicio: elem.fechaInicio,
      estado: elem.estado,
    }))
    .reverse();
}
