import { GenericResponse } from "./response.common";

export interface EleccionResponse extends GenericResponse {
  data: [Eleccion];
}

export interface Eleccion {
  id: number;
  nombre: string;
}

export interface ActaResponse extends GenericResponse {
  data: Acta;
}

export interface Acta {
  pagina: number;
  totalPaginas: number;
  totalRegistros: number;
  pendiente: number;
  observada: number;
  contabilizada: number;
  content: [Content];
  imgActaTipo: number;
  imgResolucionTipo: number;
}

export interface Content {
  id: number;
  idMesa: number;
  codigoMesa: string;
  estadoActa: string;
  codigoEstadoActa: string;
  descripcionEstadoActa: string;
  idEleccion: number;
  esSeleccionado?: boolean;
  descripcionMesa?: string;
}

export interface MesaResponse extends GenericResponse {
  data: Mesa;
}

export interface Mesa {
  centroPoblado: string;
  codigoLocalVotacion: string;
  codigoEstadoActa: string;
  codigoMesa: string;
  detalle: [Detalle];
  estadoActa: string;
  id: number;
  idAmbitoGeografico: number;
  idEleccion: number;
  idMesa: number;
  idUbigeo: number;
  idUbigeoEleccion: number;
  lineaTiempo: [LineaTiempo];
  nombreLocalVotacion: string;
  numeroCopia: string;
  porcentajeParticipacionCiudadana: number;
  totalAsistentes: number;
  totalElectoresHabiles: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  ubigeoNivel01: string;
  ubigeoNivel02: string;
  ubigeoNivel03: string;
  esSeleccionado?: boolean;
  descripcionMesa: string;
  descripcionEstadoActa: string;
  estadoActaResolucion: string;
  estadoDescripcionActaResolucion: string;
  descripcionSubEstadoActa: string;
  imgActaTipo: number;
  imgResolucionTipo: number;
  archivos: [Archivo];
  codigoSolucionTecnologica?: number;
  descripcionSolucionTecnologica?: string;
}

export interface LineaTiempo {
  cdescripcion?: string;
  cestado?: string;
  dfecha?: number;
  norden?: number;
  codigoEstadoActa: string;
  descripcionEstadoActa: string;
  fechaRegistro: number;
  descripcionEstadoActaResolucion: string;
}

export interface Detalle {
  nagrupacionPolitica: number;
  cdescripcion?: string;
  descripcion: string;
  estado: number;
  ccodigo: string;
  ccandidato?: [Candidato];
  candidato: [Candidato];
  ngrafico: number;
  nporcentajeVotosEmitidos: number;
  nporcentajeVotosValidos: number;
  nposicion: number;
  nvotos: number;
  totalCandidatos?: number;
  seleccionado?: boolean;
  urlAgrupacionImage?: string;
}

export interface Candidato {
  capellidoMaterno: string;
  capellidoPaterno: string;
  ccargo: string;
  cdocumentoIdentidad: string;
  cnombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombres: string;
  lista: number;
  nombreCompleto: string;
  votos: number;
  seleccionado?: boolean;
}

export interface LocalVotacionResponse extends GenericResponse {
  data: [LocalVotacion];
}

export interface LocalVotacion {
  codigoLocalVotacion: string;
  nombreLocalVotacion: string;
}

export interface MesaActaResponse extends GenericResponse {
  data: [Mesa];
}

export interface Archivo {
  descripcion: string;
  id: string;
  nombre: string;
  tipo: number;
  activo?: boolean;
}

export interface CandidatoResponse extends GenericResponse {
  data: [Candidato];
}
