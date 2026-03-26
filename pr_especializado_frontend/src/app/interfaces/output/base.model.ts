export class Base {
  success?: boolean;
  message?: string;
  data?: any;
}

export class BasePaginado {
  success?: boolean;
  message?: string;
  data?: any;
}
export class Paginado {
  paginaActual?: number;
  totalPaginas?: number;
  totalRegistros?: number;
}

export abstract class TablaEscrutinioBase {
  idPartidoPolitico: number = 0;
  nombrePartidoPolitico: string = '';
  totalCandidatos: number = 0;
  votosEmitido: number = 0;
  votosValido: number = 0;
  totalVotos: number = 0;
  posicion: number = 0;
  estado?: number;
  ccodigo?: string;
}

