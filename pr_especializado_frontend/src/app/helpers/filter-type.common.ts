export const UBIGEO_LEVELS = {
  LEVEL_01: 'ubigeo_nivel_01',
  LEVEL_02: 'ubigeo_nivel_02',
  LEVEL_03: 'ubigeo_nivel_03',
  AMBITO_GEOGRAFICO: 'ambito_geografico',
  DISTRITO_ELECTORAL: 'distrito_electoral',
  ELECTION: 'eleccion',
  TOTAL: 'total',
};

export interface IUbigeosForFilterType {
  ambito?: number;
  ubigeo01?: string | number;
  ubigeo02?: string | number;
  ubigeo03?: string | number;
  local?: string | number;
}

export function getGenericFilterType(ubigeos: IUbigeosForFilterType): string {
  if (ubigeos.ubigeo03) {
    return UBIGEO_LEVELS.LEVEL_03;
  }
  if (ubigeos.ubigeo02) {
    return UBIGEO_LEVELS.LEVEL_02;
  }
  if (ubigeos.ubigeo01) {
    return UBIGEO_LEVELS.LEVEL_01;
  }
  if (ubigeos.ambito) {
    return UBIGEO_LEVELS.AMBITO_GEOGRAFICO;
  }
  return UBIGEO_LEVELS.ELECTION;
}
