export type ReportExtentionType = "pdf" | "csv";

export interface IDescargarPdfCommonParams {
  tipoReporte?: number;
  idAgrupacionPolitica?: number;
  idAmbitoGeografico?: number;
  idDistritoElectoral?: number;
  idEleccion?: number;
  nombreEleccion?: string;
  nombreProceso?: string;
  tipoFiltro?: string;
}

export interface IListarReportesAutomaticosParams {
  pagina: number;
  tamanio: number;
}

export interface IListarReportesAutomaticosBody {
  usuarioConsulta: string;
  tipoEleccion: number;
}

export interface IPDFReportParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
  idDistritoElectoral?: number;
  idOrgPolitica?: number;
  descripcionOrgPolitica?: string;
  nombreProceso: string;
  nombreEleccion?: string;
  tipoReporte?: number;
}

export interface IPDFReportParamsForPartCiud {
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel01?: string;
  ubigeoNivel02?: string;
  ubigeoNivel03?: string;
  idDistritoElectoral?: number;
  idOrgPolitica?: number;
  descripcionOrgPolitica?: string;
  nombreProceso: string;
  nombreEleccion?: string;
  tipoReporte?: number;
}

export type ReportErrorType = "no_firma_digital";

export interface IFileResponse {
  file?: Blob;
  success: boolean;
  reportErrorType?: ReportErrorType;
}

export type ModulesForPDFReportType =
  | "Senadores27_Resumen_General"
  | "Diputados_Resumen_General"
  | "Presidencial_Ubicacion_Geografica"
  | "Presidencial_Organizacion_Politica"
  | "Presidencial_Resumen_General"
  | "Parlamento_Ubicacion_Geografica"
  | "Parlamento_Candidato"
  | "Parlamento_Organizacion"
  | "Parlamento_Resumen_General"
  | "Senadores_33_Ubicacion_Geografica"
  | "Senadores_33_Candidato"
  | "Senadores_33_Organizacion"
  | "Senadores_33_Resumen_General"
  | "Participacion_Ciudadana";
