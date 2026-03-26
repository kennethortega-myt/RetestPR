import { encryptStorageEleccion } from "../settings/encrypt-storage.settings";

export type encryptStorageEleccionKeyType =
  "PROCESO_ELECTORAL_ACTIVO"
  | "ID_ELECCION_PRINCIPAL";

export function getEncryptStorageEleccionValue(key: encryptStorageEleccionKeyType) {
  return encryptStorageEleccion.getItem(key);
}

export function setEncryptStorageEleccionValue(key: encryptStorageEleccionKeyType, value: string | number | object) {
  encryptStorageEleccion.setItem(key, value);
}
