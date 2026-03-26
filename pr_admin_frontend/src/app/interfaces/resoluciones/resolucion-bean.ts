import { ActaBean } from './acta-jee-bean';

export interface ResumenResoluciones {
  numResolucionesAplicadas: number;
  numResolucionesSinAplicar: number;
  numTotalResoluciones: number;
  resoluciones: ResolucionAsociadosRequest[];
}

export interface ResolucionAsociadosRequest {
  id: string;
  idArchivo: string;
  nombreArchivo: string;
  procedencia: number;
  fechaResolucion: Date;
  fechaResolucion2: Date;
  fechaRegistro: string;
  numeroExpediente: string;
  numeroResolucion: string;
  tipoResolucion: number;
  estadoResolucion: string;
  estadoDigitalizacion: string;
  descripcionEstadoResolucion: string;
  numeroPaginas: number;
  descripcionEstadoDigitalizacion: string;
  actasAsociadas: ActaBean[];
}
