export interface MaeProcesoElectoralBean {
  id: number;
  cnombre: string;
  nombre?: string;
  cacronimo: string;
  dfechaConvocatoria: Date;
  ntipoAmbitoElectoral: number;
  nactivo: number;
  caudUsuarioCreacion: string;
  daudFechaCreacion: Date;
  caudUsuarioModificacion: string;
  daudFechaModificacion: Date;
}
