export interface IGenericInterface<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface IDatosGeneralResponse {
  id: number;
  nombre: string;
  img?: string;
  archivo?: any;
}

export interface IDatosGeneralRequest {
  id?: number;
  nombre: string;
  activo: number;
  usuario: string;
}

export interface GenericRequestInterface {
  id?: number;
  estado?: number;
}

export interface IDocumentoElectoralRequest {
  id?: number;
  nombre: string;
  abreviatura?: string;
  tipoImagen?: number;
  escanerAmbasCaras?: number;
  tamanioHoja?: number;
  multipagina?: number;
  codigoBarraOrientacion?: number;
  activo: number;
  usuario: string;
}

export interface IDocumentoElectoralResponse {
  id: number;
  nombre: string;
  img?: string;
  archivo?: any;

  abreviatura?: string;
  tipoImagen?: number;
  escanerAmbasCaras?: number;
  tamanioHoja?: number;
  multipagina?: number;
  codigoBarraOrientacion?: number;
}
