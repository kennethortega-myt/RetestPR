export class Login {
  username?: string;
  password?: string;
  recaptcha?: string;
}

export class Restore {
  usuario?: string;
  recaptcha?: string;
}

export interface Profile {
  idPerfil: number;
  nombre: string;
  descripcion: string;
  abreviatura: string;
}

export interface Usuario {
  username?: string;
  idUsuario?: number;
  claveNueva?: number;
  codigoCentroComputo?: string;
  nombreCentroComputo?: string;
  codigoOp?: string;
  nombres?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  numeroDocumento?: string;
  tipoDocumento?: number;
  tipoAutenticacion?: number;
  token?: string;
  refreshToken?: string;
  superAdmin?: number;
  tipoGeneracion?: number;
  personaAsignada?: number;
  idAmbitoElectoral?: number | null;
  tipoAmbito?: number | null;
  nombreAmbito?: string | null;
  abreviaturaAmbito?: string | null;
  idUnidadOrganica?: number;
  siglasUnidadOrganica?: string;
  acronimoProceso?: string;
  idProcesoElectoral?: number | null;
  nombreProceso?: string;
  tipoProceso?: number | null;
}

export interface AccesoRequest {
  idUsuario?: number;
  idPerfil?: number;
  acronimoProceso?: string;
}

export interface Module {
  idModulo?: number;
  nombre: string;
  descripcion?: string;
  icono?: string;
  url: string;
}

export interface Option {
  idModulo: number,
  idOpcion: number,
  nombre: string,
  descripcion: string,
  url: string,
  icono: string
}

export interface Action {
  idAccion: number,
  nombre: string,
  descripcion: string
}

export interface DataLogin {
  usuario: Usuario;
  perfiles?: Profile[];
  modulos?: Module[];
  isProcessValid?: boolean;
}

export interface LoginResponse {
  success: boolean;
  message?: string;
  errorCode?: number;
  data: {
    datos: DataLogin;
  };
}

export interface PasswordUpdateResponse {
  resultado: number;
  mensaje: string;
}


export interface SesionActiva {
  usuario?: string;
}