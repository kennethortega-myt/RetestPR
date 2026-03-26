import { HttpStatusCode } from "@angular/common/http";
import { URL_PATHS_TO_REDIRECT } from "./app.routes.settings";

export type errorHttpType =
  | HttpStatusCode.NotFound
  | HttpStatusCode.RequestTimeout
  | HttpStatusCode.InternalServerError
  | HttpStatusCode.ServiceUnavailable;

export const errorRedirectionRoutes: { [key in errorHttpType]: string } = {
  [HttpStatusCode.NotFound]: URL_PATHS_TO_REDIRECT.pagina_no_encontrada,
  [HttpStatusCode.RequestTimeout]: URL_PATHS_TO_REDIRECT.tiempo_de_espera_agotado,
  [HttpStatusCode.InternalServerError]: URL_PATHS_TO_REDIRECT.error_en_servidor,
  [HttpStatusCode.ServiceUnavailable]: URL_PATHS_TO_REDIRECT.servicio_no_disponible,
};
