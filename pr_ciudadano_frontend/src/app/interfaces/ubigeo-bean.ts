import { GenericResponse } from "./response.common";

export interface UbigeoBean {
  success: boolean;
  message: string;
  data: Array<Ubigeo>;
}

export interface Ubigeo {
  idUbigeo?: string;
  ubigeo: string;
  nombre: string;
}

export interface LocalVotacionResponse extends GenericResponse {
  data: [LocalVotacion];
}

export interface LocalVotacion {
  codigoLocalVotacion: string;
  nombreLocalVotacion: string;
}

export interface RegionResponse extends GenericResponse {
  data: Region[];
}

export interface Region {
  codigo: number;
  nombre: string;
  ubigeo: number;
}

export interface DistritoElectoralResponse extends GenericResponse {
  data: [DistritoElectoral];
}
export interface DistritoElectoral {
  codigo: number;
  nombre: string;
}
