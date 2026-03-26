export interface MonitoreoListActasItemBean {
  total: number;
  totalNormales: number;
  totalObservadas: number;
  totalEnviadasJne: number;
  fechaRegistro: string;
  listActaItems: Array<ListActasBean>;
}
export interface ListActasBean {
  actaId: number;
  mesa: string;
  estado: string;
  fecha: string;
  imagenInstalacion: string;
  imagenEscrutinio: string;
}
