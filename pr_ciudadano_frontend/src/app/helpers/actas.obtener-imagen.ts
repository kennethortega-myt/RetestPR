import { ACTA_LINEAT_CODIGO_ESTADO } from "./constantes";

export function obtenerImagenForActa(codigoEstadoActa: string): string {
  const ACTA_ICONS = {
    [ACTA_LINEAT_CODIGO_ESTADO.DIGITALIZACION]: "assets/img/icons/estado-actas/ico_digitalizacion.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.DIGITACION]: "assets/img/icons/estado-actas/ico_digitacion.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.CONTABILIZADA]: "assets/img/icons/estado-actas/ico_acta_contabilizada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.OBSERVADA]: "assets/img/icons/estado-actas/ico_observada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.PARA_ENVIO_JEE]: "assets/img/icons/estado-actas/ico_jee.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.RECEPCION_RESOLUCION]: "assets/img/icons/estado-actas/ico_recepcion.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.PROCESADA_RESOLUCION]: "assets/img/icons/estado-actas/ico_acta_contabilizada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.MESA_NO_INSTALADA]: "assets/img/icons/estado-actas/ico_noencontrada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.EXTRAVIADA]: "assets/img/icons/estado-actas/ico_noencontrada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.RESOLUCIÓN_DE_ONPE]: "assets/img/icons/estado-actas/ico_resolucion.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.SINIESTRADA]: "assets/img/icons/estado-actas/ico_siniestrada.svg",
    [ACTA_LINEAT_CODIGO_ESTADO.RESOLUCION_DEL_JEE]: "assets/img/icons/estado-actas/ico_jee.svg",
  };
  return ACTA_ICONS[codigoEstadoActa] ?? "";
}
