export type MenuElectionIconKeys =
  | "presidenciales"
  | "diputados"
  | "senadores_multiple"
  | "senadores_unico"
  | "parlamento_andino"
  | "participacion_ciudadana"
  | "actas"
  | "revocatoria"
  | "revocatoria_distrital";

/**
 * Estos keys se copiaron del endpoint de elecciones para poder identificar cuales son los ids correctos de cada elección
 */
export const MENU_ELECTION_ICONS_KEYS: {
  [key in MenuElectionIconKeys]: string;
} = {
  presidenciales: "icon-ico_e_presidencial",
  diputados: "icon-ico_e_diputados",
  senadores_multiple: "icon-ico-senado_27",
  senadores_unico: "icon-ico-senado_33",
  parlamento_andino: "icon-ico_e_parlamento",
  participacion_ciudadana: "icon-ico-participacion2",
  actas: "icon-ico_menu_actas",
  revocatoria: "icon-ico-revocatoria",
  revocatoria_distrital: "icon-ico-revocatoria-distrital",
};
