export interface WsProgress {
  porcentaje: number;
  texto?: string;
  estado: '0' | '1' | '2';
}
