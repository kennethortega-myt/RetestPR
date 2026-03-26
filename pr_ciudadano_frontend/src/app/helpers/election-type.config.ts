import { RegionValue } from "../interfaces/filtro-settings";
import { ElectionScope } from "../interfaces/hot-map.interfaces";
import { GEOGRAPHIC_SCOPE, GEOGRAPHIC_SCOPE_EXTRANJERA, MAIN_ELECTION_IDS } from "./constantes";

export function isSpecialElectionType(electionId: number): boolean {
  return electionId == MAIN_ELECTION_IDS.diputados || electionId == MAIN_ELECTION_IDS.senadores_27;
}

export function getGeograpScopeByRegion(regionValue: RegionValue): number {
  return regionValue == "PERÚ" ? GEOGRAPHIC_SCOPE : GEOGRAPHIC_SCOPE_EXTRANJERA;
}

export function getGeograpScopeByScope(scope: ElectionScope): number {
  return scope == "peru" ? GEOGRAPHIC_SCOPE : GEOGRAPHIC_SCOPE_EXTRANJERA;
}
