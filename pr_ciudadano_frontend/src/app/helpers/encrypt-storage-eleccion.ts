import { IMenuElections } from "../interfaces/elections";
import { encryptStorageEleccion } from "../settings/encrypt-storage.settings";
import { MenuElectionIconKeys, MENU_ELECTION_ICONS_KEYS } from "../settings/icon-keys.settings";

export type encryptStorageEleccionKeyType =
  | "FECHA_DE_PROCESO"
  | "OTHER_KEY_YOU_NEED"
  | "ELECCIONES"
  | "PROCESO_ELECTORAL_ACTIVO"
  | "TIPO_DE_PROCESO_ELECTORAL_A_CARGAR"
  | "ID_DE_ELECCION_PRINCIPAL"
  | "ACTIVO_FECHA_PROCESO";

export const MENU_ELECTIONS_KEY: encryptStorageEleccionKeyType = "ELECCIONES";

export function getEncryptStorageEleccionValue(key: encryptStorageEleccionKeyType) {
  return encryptStorageEleccion.getItem(key);
}

export function setEncryptStorageEleccionValue(key: encryptStorageEleccionKeyType, value: string | number | object) {
  encryptStorageEleccion.setItem(key, value);
}

export function getMenuElections(): IMenuElections[] {
  const elections = encryptStorageEleccion.getItem(MENU_ELECTIONS_KEY);
  return elections ? (JSON.parse(elections) as IMenuElections[]) : [];
}

export function getCurrentElectionId(iconKey: MenuElectionIconKeys): number {
  const currentElection = getElectionByIconKey(iconKey);
  if (currentElection) {
    return currentElection.idEleccion;
  } else {
    return 10;
  }
}

export function getCurrentElectionDescriptionTitleBy(iconKey: MenuElectionIconKeys): string {
  const currentElection = getElectionByIconKey(iconKey);
  if (currentElection) {
    return currentElection.descripcion;
  } else {
    return "";
  }
}

export function getElectionByIconKey(iconKey: MenuElectionIconKeys): IMenuElections {
  const elections = getMenuElections();
  const iconValue = MENU_ELECTION_ICONS_KEYS[iconKey];
  const currentElection = elections.find((election) => election.icono == iconValue) ?? ({} as IMenuElections);
  return currentElection;
}

export function getElectionByIdElection(idEleccion: number): IMenuElections {
  const elections = getMenuElections();
  const currentElection = elections.find((election) => election.idEleccion == idEleccion) ?? ({} as IMenuElections);
  return currentElection;
}

// Versiones asíncronas para manejar mejor el timing
async function getMenuElectionsAsync(): Promise<IMenuElections[]> {
  const elections = await encryptStorageEleccion.getItem(MENU_ELECTIONS_KEY);
  return elections ? (JSON.parse(elections) as IMenuElections[]) : [];
}

export async function getElectionByIconKeyAsync(iconKey: MenuElectionIconKeys): Promise<IMenuElections> {
  const elections = await getMenuElectionsAsync();
  const currentElection = elections.find((election) => election.icono == iconKey) ?? ({} as IMenuElections);
  return currentElection;
}
