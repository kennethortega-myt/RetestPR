import { MENU_ELECTION_ICONS_KEYS } from "../settings/icon-keys.settings";
import { getElectionByIconKey } from "./encrypt-storage-eleccion";

export const RESUMEN_GENERAL_TITLE = {
  [MENU_ELECTION_ICONS_KEYS.presidenciales]: getElectionByIconKey("presidenciales").descripcion,
  [MENU_ELECTION_ICONS_KEYS.diputados]: getElectionByIconKey("diputados").descripcion,
  [MENU_ELECTION_ICONS_KEYS.senadores_multiple]: getElectionByIconKey("senadores_multiple").descripcion,
  [MENU_ELECTION_ICONS_KEYS.senadores_unico]: getElectionByIconKey("senadores_unico").descripcion,
  [MENU_ELECTION_ICONS_KEYS.parlamento_andino]: getElectionByIconKey("parlamento_andino").descripcion,
  [MENU_ELECTION_ICONS_KEYS.revocatoria]: getElectionByIconKey("revocatoria").descripcion,
  [MENU_ELECTION_ICONS_KEYS.revocatoria_distrital]: getElectionByIconKey("revocatoria_distrital").descripcion,
};
