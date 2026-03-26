import { environment } from "../../environments/environment";
import { ModulesForPDFReportType } from "../interfaces/reporte.interfaces";

export const specificURLs = {
  reportePresidencialUbicacionGeografica: environment.apiUrlLocal + "reportes/eleccion-presidencial",
  reportePresidencialOrganizacionPolitica: environment.apiUrlLocal + "reportes/eleccion-presidencial-organizacion",
  reportePresidencialResumenGeneral: environment.apiUrlLocal + "reportes/eleccion-presidencial-resumen-general",

  reporteParlamentoAndinoResultadoUbicacionGeografica:
    environment.apiUrlLocal + "reportes/eleccion-parlamento-andino-geografica",
  reporteParlamentoAndinoResultadoCandidato: environment.apiUrlLocal + "reportes/eleccion-parlamento-andino-candidato",
  reporteParlamentoAndinoResultadoOrganizacion:
    environment.apiUrlLocal + "reportes/eleccion-parlamento-andino-organizacion",
  reporteParlamentoAndinoResumenGeneral:
    environment.apiUrlLocal + "reportes/eleccion-parlamento-andino-resumen-general",

  reporteSenadores33ResultadoUbicacionGeografica:
    environment.apiUrlLocal + "reportes/eleccion-senadores-unico-geografica",
  reporteSenadores33ResultadoCandidato: environment.apiUrlLocal + "reportes/eleccion-senadores-unico-candidato",
  reporteSenadores33ResultadoOrganizacion: environment.apiUrlLocal + "reportes/eleccion-senadores-unico-organizacion",
  reporteSenadores33ResumenGeneral: environment.apiUrlLocal + "reportes/eleccion-senadores-unico-resumen-general",

  reporteSenadores27ResumenGeneral: environment.apiUrlLocal + "reportes/eleccion-senadores-multiple-resumen-general",

  reporteDiputadosResumenGeneral: environment.apiUrlLocal + "reportes/eleccion-diputados-resumen-general",

  reporteParticipacionCiudadana: environment.apiUrlLocal + "reportes/participacion-ciudadana",
};

export const URL_FOR_MODULE_PDF_REPORT: { [key in ModulesForPDFReportType]: string } = {
  Presidencial_Ubicacion_Geografica: specificURLs.reportePresidencialUbicacionGeografica,
  Presidencial_Organizacion_Politica: specificURLs.reportePresidencialOrganizacionPolitica,
  Presidencial_Resumen_General: specificURLs.reportePresidencialResumenGeneral,

  Parlamento_Ubicacion_Geografica: specificURLs.reporteParlamentoAndinoResultadoUbicacionGeografica,
  Parlamento_Candidato: specificURLs.reporteParlamentoAndinoResultadoCandidato,
  Parlamento_Organizacion: specificURLs.reporteParlamentoAndinoResultadoOrganizacion,
  Parlamento_Resumen_General: specificURLs.reporteParlamentoAndinoResumenGeneral,

  Senadores_33_Ubicacion_Geografica: specificURLs.reporteSenadores33ResultadoUbicacionGeografica,
  Senadores_33_Candidato: specificURLs.reporteSenadores33ResultadoCandidato,
  Senadores_33_Organizacion: specificURLs.reporteSenadores33ResultadoOrganizacion,
  Senadores_33_Resumen_General: specificURLs.reporteSenadores33ResumenGeneral,

  Diputados_Resumen_General: specificURLs.reporteDiputadosResumenGeneral,
  Senadores27_Resumen_General: specificURLs.reporteSenadores27ResumenGeneral,
  Participacion_Ciudadana: specificURLs.reporteParticipacionCiudadana,
};

export const NAME_FOR_MODULE_PDF_REPORT: { [key in ModulesForPDFReportType]: string } = {
  Presidencial_Ubicacion_Geografica: "Reporte_Presidencial_UbicacionGeografica_",
  Presidencial_Organizacion_Politica: "Reporte_Presidencial_OrganizacionPolitica_",
  Presidencial_Resumen_General: "Reporte_Presidencial_ResumenGeneral_",

  Parlamento_Ubicacion_Geografica: "Reporte_ParlamentoAndino_UbicacionGeografica_",
  Parlamento_Candidato: "Reporte_ParlamentoAndino_Candidato_",
  Parlamento_Organizacion: "Reporte_ParlamentoAndino_OrganizacionPolitica_",
  Parlamento_Resumen_General: "Reporte_ParlamentoAndino_ResumenGeneral_",

  Senadores_33_Ubicacion_Geografica: "Reporte_Senadores33_UbicacionGeografica_",
  Senadores_33_Candidato: "Reporte_Senadores33_Candidato_",
  Senadores_33_Organizacion: "Reporte_Senadores33_OrganizacionPolitica_",
  Senadores_33_Resumen_General: "Reporte_Senadores33_ResumenGeneral_",

  Diputados_Resumen_General: "Reporte_Congresales_ResumenGeneral_",
  Senadores27_Resumen_General: "Reporte_Senadores27_ResumenGeneral_",
  Participacion_Ciudadana: "Reporte_ParticipacionCiudadana_",
};

export const ELECTION_NAMES_FOR_REPORT: { [key in ModulesForPDFReportType]: string } = {
  Presidencial_Ubicacion_Geografica: "Elecciones Presidenciales / Resultado por Ubicación Geográfica",
  Presidencial_Organizacion_Politica: "Elecciones Presidenciales / Resultado por Organización Política",
  Presidencial_Resumen_General: "Resumen General / Presidenciales",

  Parlamento_Ubicacion_Geografica: "Parlamento Andino / Resultado por Ubicación Geográfica",
  Parlamento_Candidato: "Parlamento Andino / Resultado por Candidato",
  Parlamento_Organizacion: "Parlamento Andino / Realizar búsquedas",
  Parlamento_Resumen_General: "Resumen General / Parlamento Andino",

  Senadores_33_Ubicacion_Geografica: "Senadores por distrito electoral único / Resultado por Ubicación Geográfica",
  Senadores_33_Candidato: "Senadores por distrito electoral único / Resultado por Candidato",
  Senadores_33_Organizacion: "Senadores por distrito electoral único / Realizar búsquedas",
  Senadores_33_Resumen_General: "Resumen General / Senadores por distrito electoral único",

  Diputados_Resumen_General: "Resumen General / Congresales",
  Senadores27_Resumen_General: "Resumen General / Senadores por distrito electoral múltiple",
  Participacion_Ciudadana: "Participación Ciudadana",
};
