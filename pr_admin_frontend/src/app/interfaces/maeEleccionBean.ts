import { MaeProcesoElectoralBean } from './maeProcesoElectoralBean';

export interface MaeEleccionBean {
  id: string;
  procesoElectoral: MaeProcesoElectoralBean;
  cnombre: string;
  nombre: string;
  nactivo: number;
  caudUsuarioCreacion: string;
  daudFechaCreacion: Date;
  caudUsuarioModificacion: string;
  daudFechaModificacion: Date;
}
