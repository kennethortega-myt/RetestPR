import { MENU_ELECTION_ICONS_KEYS } from "../settings/icon-keys.settings";
import { formatPascalCaseInText } from "./basic-helpers/string.helper";
import { getElectionByIconKey } from "./encrypt-storage-eleccion";

export const SHARE_ELECTION_TITLE = {
  [MENU_ELECTION_ICONS_KEYS.presidenciales]: formatPascalCaseInText(getElectionByIconKey("presidenciales")?.nombre),
  [MENU_ELECTION_ICONS_KEYS.diputados]: formatPascalCaseInText(getElectionByIconKey("diputados")?.nombre),
  [MENU_ELECTION_ICONS_KEYS.senadores_multiple]: formatPascalCaseInText(
    getElectionByIconKey("senadores_multiple")?.nombre
  ),
  [MENU_ELECTION_ICONS_KEYS.senadores_unico]: formatPascalCaseInText(getElectionByIconKey("senadores_unico")?.nombre),
  [MENU_ELECTION_ICONS_KEYS.parlamento_andino]: formatPascalCaseInText(
    getElectionByIconKey("parlamento_andino")?.nombre
  ),
  [MENU_ELECTION_ICONS_KEYS.revocatoria]: getElectionByIconKey("revocatoria")?.nombre,
};
