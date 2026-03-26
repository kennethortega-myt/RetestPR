import { EnumIdEleccion } from './enums';

export function getIconImageForElectionId(electionId?: number): string {
  const iconConfigs = [
    {
      electionId: EnumIdEleccion.ID_ELECCION_PRESIDENCIAL,
      iconUrl: 'assets/icon-acta-pre.svg',
    },
    {
      electionId: EnumIdEleccion.ID_ELECCION_DIPUTADOS,
      iconUrl: 'assets/icon-congresal.svg',
    },
    {
      electionId: EnumIdEleccion.ID_ELECCION_PARLAMENTO_ANDINO,
      iconUrl: 'assets/icon-parlamento-andino.svg',
    },
    {
      electionId: EnumIdEleccion.ID_ELECCION_SENADORES_MULTIPLE,
      iconUrl: 'assets/img/icons/senador_DEM.svg',
    },
    {
      electionId: EnumIdEleccion.ID_ELECCION_SENADORES_UNICO,
      iconUrl: 'assets/img/icons/senador_DEU.svg',
    },
  ];
  const foundIconConfig = iconConfigs.find(
    (config) => config.electionId == electionId
  );
  return foundIconConfig?.iconUrl ?? '';
}
