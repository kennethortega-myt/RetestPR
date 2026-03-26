import { MAIN_ELECTION_IDS } from "./constantes";
import { getElectionByIconKey } from "./encrypt-storage-eleccion";

export const ACTAS_RESUMEN_GENERAL_TITLE = {
  [MAIN_ELECTION_IDS.presidenciales]: getElectionByIconKey("presidenciales")?.nombre,
  [MAIN_ELECTION_IDS.diputados]: getElectionByIconKey("diputados")?.nombre,
  [MAIN_ELECTION_IDS.senadores_27]: getElectionByIconKey("senadores_multiple")?.nombre,
  [MAIN_ELECTION_IDS.senadores_33]: getElectionByIconKey("senadores_unico")?.nombre,
  [MAIN_ELECTION_IDS.parlamento_andino]: getElectionByIconKey("parlamento_andino")?.nombre,
  [MAIN_ELECTION_IDS.revocatoria]: getElectionByIconKey("revocatoria")?.nombre,
};