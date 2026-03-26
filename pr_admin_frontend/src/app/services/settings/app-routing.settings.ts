export type RoutePathKeys =
  | 'configuracion_reportes'
  | 'lista_configuraciones'

export const ROUTE_PATHS: { [key in RoutePathKeys]: string } = {
  configuracion_reportes: 'configuracion-de-reportes',
  lista_configuraciones: 'lista-de-configuraciones'
};
