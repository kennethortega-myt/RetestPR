export interface IReportConfigData {
  eleccion: string;
  eleccionId: number;
  fechaInicio: string;
  horaInicio: string;
  id: string;
  tipoReporte: number;
  tipoGeneracionReporte: number;
  tipoGeneracionReporteVal: number;
}

export interface IReportConfigFront {
  icono: string;
  tipoDeEleccion: string;
  tipoDePeriodo: string;
  fechaDeInicio: string;
  periodo: string;
  horaDeInicio: string;
  id?: string;
  estado?: number;
}

export const ICONOS = {
  diputados: 'assets/icons-administrador/icono_diputados.svg',
  parlamento_andino: 'assets/icons-administrador/icono_parlamentoandino.svg',
  presidencial: 'assets/icons-administrador/icono_presidencial.svg',
  senadores: 'assets/icons-administrador/icono_senadores.svg',
  senadoresDEU: 'assets/icons-administrador/icono_senadoresDEU.svg',
  senadoresDEM: 'assets/icons-administrador/icono_senadoresDEM.svg',
}