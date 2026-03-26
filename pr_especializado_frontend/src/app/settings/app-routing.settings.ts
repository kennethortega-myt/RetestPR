export type RoutePathKeys =
  | 'home'
  | 'consulta'
  | 'actas_observadas'
  | 'mis_reportes'
  | 'reportes_automaticos'
  | 'recuperar_contrasena'
  | 'configuracion_reportes'
  | 'boletines'
  | 'boletinesdetalle'
  | 'lista_configuraciones'
  | 'configuracion_reportes_actas'
  | 'lista_configuraciones_actas';

export const ROUTE_PATHS: { [key in RoutePathKeys]: string } = {
  home: 'home',
  actas_observadas: 'consulta',
  consulta: 'actas-observadas',
  mis_reportes: 'mis-reportes',
  reportes_automaticos: 'reportes-automaticos',
  recuperar_contrasena: 'recuperar-contrasena',
  configuracion_reportes: 'configuracion-de-reportes',
  boletines: 'boletines',
  boletinesdetalle: 'boletines/detalle',
  lista_configuraciones: 'lista-de-configuraciones',
  configuracion_reportes_actas: 'configuracion-de-actas',
  lista_configuraciones_actas: 'lista-de-configuraciones-actas'



};
