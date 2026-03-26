import { GenericResponse } from "./response.common";

export interface ResumenResponse extends GenericResponse {
  data: Resumen;
}

export interface Resumen {
  contabilizadas?: number;
  actasContabilizadas: number;
  actasEnviadasJee: number;
  enviadasJee?: number;
  actasPendientes: number;
  pendientesJee?: number;
  fechaActualizacion: number;
  idUbigeoDepartamento: number;
  idUbigeoProvincia: number;
  idUbigeoDistrito: number;
  participacionCiudadana: number;
  totalActas: number;
  actasPendientesJee: number;
  idUbigeoDistritoElectoral: number;
  totalVotosEmitidos?: number;
  totalVotosValidos?: number;
  porcentajeVotosEmitidos?: number;
  porcentajeVotosValidos?: number;
}
