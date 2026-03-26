import { TablaEscrutinioBase } from '../../interfaces/output/base.model';
import { TablaEscrutinioModeloDos } from '../tabla-escrutinio-modelo-dos/tabla-escrutinio-modelo-dos.model';

export class TablaEscrutinioModeloUno extends TablaEscrutinioBase {
  candidato?: CandidatoModeloUno[];
  porcentajeBarra?: number;
}

export class CandidatoModeloUno {
  apellidoMaterno: string = '';
  apellidoPaterno: string = '';
  nombres: string = '';
  cdocumentoIdentidad: string = '';
}

export interface TipoEscrutinioModel {
  tablaModel: TablaEscrutinioModeloUno | TablaEscrutinioModeloDos;
  tipoModel: boolean;
  esModeloDos?: boolean;
}