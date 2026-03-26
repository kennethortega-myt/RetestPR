/**
 * Values in this constant should be the same that backend return in endpoint /elecciones
 */
export const PATHS = {
  main_routing_page: "main",
  // RUTAS PARA PROCESO DE BICAMERALIDAD
  resumen_general: "resumen",
  presidenciales: "presidenciales",
  diputados: "diputados",
  senadores_27: "senadores-distrito-electoral-multiple",
  senadores_33: "senadores-distrito-nacional-unico",
  parlamento_andino: "parlamento-andino",
  participacion_ciudadana: "participacion-ciudadana",
  actas: "actas",
  faq: "faq", // Preguntas frecuentes
  sitemap: "sitemap", // Sitemap
  reportes_automaticos: "reportes-automaticos",
  // RUTAS PARA PAGINAS DE ERRORES
  pagina_no_encontrada: "pagina-no-encontrada",
  tiempo_de_espera_agotado: "tiempo-de-espera-agotado",
  error_en_servidor: "error-en-el-servidor",
  servicio_no_disponible: "servicio-no-disponible",
  error_inesperado: "error-inesperado",
};

export const DEFAULT_PATHS_TO_REDIRECT = {
  // RUTAS PARA PROCESO DE NICAMERALIDAD
  elecciones_generales_o_bicameralidad: `/${PATHS.main_routing_page}/${PATHS.resumen_general}`,
};

export const URL_PATHS_TO_REDIRECT = {
  // RUTAS PARA PROCESO DE NICAMERALIDAD
  resumen: `/${PATHS.main_routing_page}/${PATHS.resumen_general}`, // "/main/resumen"
  presidenciales: `/${PATHS.main_routing_page}/${PATHS.presidenciales}`, // "/main/presidenciales"
  diputados: `/${PATHS.main_routing_page}/${PATHS.diputados}`, // "/main/diputados"
  parlamento_andino: `/${PATHS.main_routing_page}/${PATHS.parlamento_andino}`, // "/main/parlamento-andino"
  distrito_electoral_multiple: `/${PATHS.main_routing_page}/${PATHS.senadores_27}`, // "/main/senadores-distrito-electoral-multiple"
  distrito_electoral_unico: `/${PATHS.main_routing_page}/${PATHS.senadores_33}`, // "/main/senadores-distrito-nacional-unico"
  participacion_ciudadana: `/${PATHS.main_routing_page}/${PATHS.participacion_ciudadana}`, // "/main/participacion-ciudadana"
  actas: `/${PATHS.main_routing_page}/${PATHS.actas}`, // "/main/actas"
  faq: `/${PATHS.main_routing_page}/${PATHS.faq}`, // "main/faq"
  reportes_automaticos: `/${PATHS.main_routing_page}/${PATHS.reportes_automaticos}`, // "main/reportes-automaticos"
  sitemap: `/${PATHS.main_routing_page}/${PATHS.sitemap}`, // "main/sitemap"


  pagina_no_encontrada: `/${PATHS.pagina_no_encontrada}`,
  tiempo_de_espera_agotado: `/${PATHS.tiempo_de_espera_agotado}`,
  error_en_servidor: `/${PATHS.error_en_servidor}`,
  servicio_no_disponible: `/${PATHS.servicio_no_disponible}`,
  error_inesperado: `/${PATHS.error_inesperado}`,
};

export const VALID_URLS_FOR_NAVIGATION = [
  // Elecciones generales o bicameralidad
  URL_PATHS_TO_REDIRECT.resumen,
  URL_PATHS_TO_REDIRECT.presidenciales,
  URL_PATHS_TO_REDIRECT.diputados,
  URL_PATHS_TO_REDIRECT.distrito_electoral_multiple,
  URL_PATHS_TO_REDIRECT.distrito_electoral_unico,
  URL_PATHS_TO_REDIRECT.parlamento_andino,
  URL_PATHS_TO_REDIRECT.participacion_ciudadana,
  URL_PATHS_TO_REDIRECT.actas,
  URL_PATHS_TO_REDIRECT.reportes_automaticos,
  URL_PATHS_TO_REDIRECT.faq,
  URL_PATHS_TO_REDIRECT.sitemap,
  // otros
  URL_PATHS_TO_REDIRECT.pagina_no_encontrada,
  URL_PATHS_TO_REDIRECT.tiempo_de_espera_agotado,
  URL_PATHS_TO_REDIRECT.error_en_servidor,
  URL_PATHS_TO_REDIRECT.servicio_no_disponible,
  URL_PATHS_TO_REDIRECT.error_inesperado,
];
