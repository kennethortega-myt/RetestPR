export enum EnumAmbito {
  NACIONAL = 'NACIONAL',
  EXTRANJERO = 'EXTRANJERO',
}

export enum EnumIdAmbito {
  TODOS = 0,
  NACIONAL = 1,
  EXTRANJERO = 2,
}

export enum EnumIdDistrito {
  NO_SELECCIONADO = -1,
  TODOS = 30,
  EXTRANJERO = 27,
}

export enum EnumTipoFiltro {
  ELECCION = 'eleccion',
  UBIGEO_NIVEL_01 = 'ubigeo_nivel_01',
  UBIGEO_NIVEL_02 = 'ubigeo_nivel_02',
  UBIGEO_NIVEL_03 = 'ubigeo_nivel_03',
  AMBITO_GEOGRAFICO = 'ambito_geografico',
  DISTRITO_ELECTORAL = 'distrito_electoral',
}

export enum EnumIdNivelUbigeo {
  SIN_NIVEL_UBIGEO = 0,
  NIVEL_UBIGEO_01 = 1,
  NIVEL_UBIGEO_02 = 2,
  NIVEL_UBIGEO_03 = 3,
}

export enum EnumProceso {
  ID_GETBD = 'getBd',
  ID_GETVISTAELECCION = 'getVistaEleccion',
  ID_GETVISTACTA = 'getVistaActa',
  ID_GETVISTAPARTICIPACION = 'getVistaParticipacionCiudadana',
  ID_GETVISTAMESA = 'getVistaMesa',
  ID_GETVISTATOTALCPAP = 'getVistaTotalCandidatosPorAgrupacionPolitica',
}

export enum EnumIdEleccion {
  ID_ELECCION_PRESIDENCIAL = 10,
  ID_ELECCION_CONGRESAL = 11,
  ID_ELECCION_PARLAMENTO_ANDINO = 12,
  ID_ELECCION_SENADORES_MULTIPLE = 14,
  ID_ELECCION_SENADORES_UNICO = 15,
  ID_ELECCION_DIPUTADOS = 13,
}

export enum EnumIdEleccionDistritoElectoral {
  ID_ELECCION_SENADORES_MULTIPLE = 14,
  ID_ELECCION_DIPUTADOS = 13,
}

export enum EnumCodigoEstadoActa {
  CODIGO_ESTADO_ACTA_CONTABILIZADA = 'C',
  CODIGO_ESTADO_ACTA_PARA_ENVIO_JEE = 'E',
}

export enum EnumCodigoEstadoActaLineaTiempo {
  CODIGO_ESTADO_ACTA_DIGITALIZACION = 'T',
  CODIGO_ESTADO_ACTA_DIGITACION = 'D',
  CODIGO_ESTADO_ACTA_PARA_ENVIO_JEE = 'E',
  CODIGO_ESTADO_ACTA_OBSERVADA = 'H',
  CODIGO_ESTADO_ACTA_RECIBIDA_DEL_JEE = 'J',
  CODIGO_ESTADO_ACTA_CONTABILIZADA = 'C', // L
  CODIGO_ESTADO_ACTA_DEVUELTA_POR_EL_JEE = 'F',

  CODIGO_ESTADO_ACTA_RECEPCION_RESOLUCION = "J",
  CODIGO_ESTADO_ACTA_PROCESADA_RESOLUCION = "L",
  CODIGO_ESTADO_ACTA_MESA_NO_INSTALADA = "N",
  CODIGO_ESTADO_ACTA_EXTRAVIADA = "O",
  CODIGO_ESTADO_ACTA_RESOLUCIÓN_DE_ONPE = "R",
  CODIGO_ESTADO_ACTA_SINIESTRADA = "S",
  CODIGO_ESTADO_ACTA_RESOLUCION_DEL_JEE = "RJ",
}

export const TYPE_FOR_PDF = {
  ID_ACTA_ESCRUTINIO: 1,
  ID_ACTA_INSTALACION_Y_SUFRAGIO: 2,
  ID_ACTA_INSTALACION: 3,
  ID_ACTA_SUFRAGIO: 4,
  ID_RESOLUTION: 5,
};

export const ELECCION_DEFAULT = 0;
export const AMBITO_DEFAULT = 0;