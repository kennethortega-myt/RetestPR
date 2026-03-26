import { Perfil } from './perfil-bean';

export interface Usuario {
  userId: number;
  perfil: Perfil;
  usuario: string;
  acronimoProceso: string;
  codigoCentroComputo: string;
  nombreCentroComputo: string;
  nombre: string;
}
