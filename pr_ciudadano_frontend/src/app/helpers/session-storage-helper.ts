import { URL_PATHS_TO_REDIRECT } from "../settings/app.routes.settings";
import { getSessionStorageBy, setSessionStorageBy } from "../settings/session-storage.settings";

export function setResumenGeneralUrlInSessionStorage() {
  setSessionStorageBy("url", URL_PATHS_TO_REDIRECT.resumen);
}

export function setParlamentoAndinoUrlInSessionStorage() {
  setSessionStorageBy("url", URL_PATHS_TO_REDIRECT.parlamento_andino);
}

export function setPresidencialesUrlInSessionStorage() {
  setSessionStorageBy("url", URL_PATHS_TO_REDIRECT.presidenciales);
}

export function setDistritoUnicoUrlInSessionStorage() {
  setSessionStorageBy("url", URL_PATHS_TO_REDIRECT.distrito_electoral_unico);
}

export function getCurrentUrlFromSessionStorage(): string {
  return getSessionStorageBy("url") ?? "";
}
