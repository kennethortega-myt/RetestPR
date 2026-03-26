export type StatusType = "OK" | "NOK";

export interface GenericResponse {
  success: boolean;
  message?: string;
}

export const GENERIC_RESPONSE = {
  success: false,
} as GenericResponse;

export interface FrontendResponse<T> extends GenericResponse {
  data?: T;
  totalVotosPorOP?: number;
  porcentajeVotoEmitido?: number;
  porcentajeVotoValido?: number;
}

export interface BaseUbigeo {
  ubigeoNivel01: number;
  ubigeoNivel02: number;
  ubigeoNivel03: number;
}
