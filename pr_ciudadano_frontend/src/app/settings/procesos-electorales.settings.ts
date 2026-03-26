import { DEFAULT_PATHS_TO_REDIRECT } from "./app.routes.settings";

/**
 * Este tipo debe estar configurado con los mismos valores que en backend
 */
export type ElectoralProcessType = "bicameralidad" | "other_electoral_process";

export const BICAMERALIDAD: ElectoralProcessType = "bicameralidad";

export const PROCESOS_ELECTORALES_EXISTENTES: { [key in string]: ElectoralProcessType } = {
  elecciones_generales_o_bicameralidad: BICAMERALIDAD,
  other_electoral_process: "other_electoral_process",
};

export const PROCESO_ELECTORAL_DEFAULT_PATH_FOR_REDIRECTIONS: { [key in ElectoralProcessType]: string } = {
  // RUTAS PARA PROCESO DE NICAMERALIDAD
  bicameralidad: DEFAULT_PATHS_TO_REDIRECT.elecciones_generales_o_bicameralidad,
  other_electoral_process: "",
};
