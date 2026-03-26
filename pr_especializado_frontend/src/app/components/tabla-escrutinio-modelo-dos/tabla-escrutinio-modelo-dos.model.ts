import { Base, TablaEscrutinioBase } from '../../interfaces/output/base.model';


export class TablaEscrutinioModeloDos extends TablaEscrutinioBase {
  verDetalle: boolean = false;
  candidatos: CandidatoModeloDos[] = [];
  cargoDatos: boolean = false;
}

export class CandidatoEscrutinio extends Base {
  declare data?: CandidatoModeloDos[];
}

export class CandidatoModeloDos {
  lista?: number;
  nombreCompleto?: string;
  votos?: number;
  documentoIdentidad?: string;
}
