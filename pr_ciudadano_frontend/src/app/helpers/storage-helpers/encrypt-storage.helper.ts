import { PROCESOS_ELECTORALES_EXISTENTES } from "../../settings/procesos-electorales.settings";
import { getEncryptStorageEleccionValue } from "../encrypt-storage-eleccion";

export function isRevocatoria(): boolean {
  const tipoDeProcesoElectoral = getEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR");
  return tipoDeProcesoElectoral == PROCESOS_ELECTORALES_EXISTENTES["proceso_de_revocatoria"];
}

export function isEleccionesGenerales(): boolean {
  const tipoDeProcesoElectoral = getEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR");
  return tipoDeProcesoElectoral == PROCESOS_ELECTORALES_EXISTENTES["elecciones_generales_o_bicameralidad"];
}

export function shouldLoadForInitial(): boolean {
  const tipoDeProcesoElectoral = getEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR");
  const isBicameralidad =
    tipoDeProcesoElectoral == PROCESOS_ELECTORALES_EXISTENTES["elecciones_generales_o_bicameralidad"];
  const otherElectoralProcess = tipoDeProcesoElectoral == PROCESOS_ELECTORALES_EXISTENTES["other_electoral_process"];
  const validations = [isBicameralidad, otherElectoralProcess];
  return validations.some((e) => e);
}
