export type StatusType = 'OK' | 'NOK';

export interface GenericResponse {
  success: boolean;
  message?: string;
}

export const GENERIC_RESPONSE = {
  success: false,
  message: '',
} as GenericResponse;

export interface FrontendResponse<T> extends GenericResponse {
  data?: T;
}
