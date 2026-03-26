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
