import { EstadosBean } from './estadosBean';

export interface DetalleActaBean {
  grupoEscaneo: string;
  numeroMesa: string;
  estadoMesa: string;
  usuarioModificacion: string;
  fechaModificacion: string;
  estadosActa: Array<EstadosBean>;
  estadosProceso: Array<EstadosBean>;
}
