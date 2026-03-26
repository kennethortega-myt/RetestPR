import { MiembroMesaBean } from './miembro-mesa-bean';

export interface FirmaBean {
  tipoActa: number;
  tipoValidacion: number;
  miembrosMesa: Array<MiembroMesaBean>;
}
