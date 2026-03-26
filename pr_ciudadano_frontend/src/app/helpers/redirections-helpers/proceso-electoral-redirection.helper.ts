import {
  PROCESOS_ELECTORALES_EXISTENTES,
  PROCESO_ELECTORAL_DEFAULT_PATH_FOR_REDIRECTIONS,
} from "../../settings/procesos-electorales.settings";
import { getEncryptStorageEleccionValue } from "../encrypt-storage-eleccion";

export function getProcesoElectoralUrl(): string {
  const tipoProcesoElectoral = getEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR") as string;
  let urlToRedirect = "";
  for (const key in PROCESOS_ELECTORALES_EXISTENTES) {
    const element = PROCESOS_ELECTORALES_EXISTENTES[key];
    if (element == tipoProcesoElectoral) {
      urlToRedirect = PROCESO_ELECTORAL_DEFAULT_PATH_FOR_REDIRECTIONS[element];
      break;
    }
  }
  return urlToRedirect;
}
