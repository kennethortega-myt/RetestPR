import { Observable } from "rxjs";

export type EstadoSemaforo = 'ESPERA' | 'OK' | 'ERROR';

export interface ValidacionResponse {
  success: boolean;
  data: {
    estado: boolean;
  };
}

export interface SemaforoItem {
  key: 'db' | 'rabbit' | 'fileserver' | 'firma';
  label: string;
  estado: EstadoSemaforo;
  validar: () => Observable<ValidacionResponse>;
}