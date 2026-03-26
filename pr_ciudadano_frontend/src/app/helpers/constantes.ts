import { IActaCodigoEstado, IEleccion } from "../interfaces/actas.interfaces";

export class Constantes {
  public static readonly CO_GENERAL_OBTENER_DEPARTAMENTO = "departamento";
  public static readonly CO_GENERAL_OBTENER_PROVINCIA = "provincia";
  public static readonly CO_GENERAL_OBTENER_CENT_EDUCATIVO = "centro_educativo";
}

export const ELECTION_ID = "04";
export const GEOGRAPHIC_SCOPE = 1;
export const GEOGRAPHIC_SCOPE_EXTRANJERA = 2;
export const UBIGEO_LEVELS = {
  LEVEL_01: "ubigeo_nivel_01",
  LEVEL_02: "ubigeo_nivel_02",
  LEVEL_03: "ubigeo_nivel_03",
  ALL_LABEL: "ambito_geografico",
  DISTRITO_ELECTORAL: "distrito_electoral",
  ELECTION: "eleccion",
  TOTAL: "total",
};

// Values for Election Types

export type ElectionType =
  | "SUMMARY"
  | "PRESIDENTIAL_ELECTION"
  | "PROVINCE_ELECTION"
  | "DISTRICT_ELECTION"
  | "PROCEEDINGS"
  | "CITIZEN_PARTICIPATION";

export const ELECTION_IDS: {
  [K in ElectionType]: string;
} = {
  SUMMARY: "SUMMARY",
  PRESIDENTIAL_ELECTION: "06", // this value depends on backend
  PROVINCE_ELECTION: "05", // this value depends on backend
  DISTRICT_ELECTION: "04", // this value depends on backend
  PROCEEDINGS: "PROCEEDINGS",
  CITIZEN_PARTICIPATION: "CITIZEN_PARTICIPATION",
};

export const MAP_ZOOM: { [key: string]: number } = {
  INCREASE_ZOOM_FACTOR: 1.5,
  MAX_ZOOM_LEVEL: 3
}

export const CODIGO_TIPO_ELECCION = {
  PRESIDENCY: "10",
  ANDEAN_PARLIAMENT: "12",
  DEPUTIES: "13",
  SENATORS_DEM: "14",
  SENATORS_DEU: "15"
}

export const ACTA_CODIGO_ESTADO: IActaCodigoEstado = {
  PENDIENTE: "P",
  CONTABILIZADA: "C",
  OBSERVADA: "H",
  PARA_ENVIO_JEE: "E",
};

export const ACTA_CODIGO_ESTADO_DESC: IActaCodigoEstado = {
  PENDIENTE: "Pendiente",
  CONTABILIZADA: "Contabilizada",
  OBSERVADA: "Observada",
  PARA_ENVIO_JEE: "Para envío al JEE",
};

export const ACTA_CODIGO_ESTADO_RESOLUCION = {
  EXTRAVIADA: "X",
  SINIESTRADA: "Y",
};

export const TYPE_FOR_PDF = {
  ID_ACTA_ESCRUTINIO: 1,
  ID_ACTA_INSTALACION_Y_SUFRAGIO: 2,
  ID_ACTA_INSTALACION: 3,
  ID_ACTA_SUFRAGIO: 4,
  ID_RESOLUTION: 5,
};

export const ACTA_LINEAT_CODIGO_ESTADO = {
  DIGITALIZACION: "T",
  DIGITACION: "D",
  CONTABILIZADA: "C",
  OBSERVADA: "H",
  PARA_ENVIO_JEE: "E",
  RECEPCION_RESOLUCION: "J",
  PROCESADA_RESOLUCION: "L",
  MESA_NO_INSTALADA: "N",
  EXTRAVIADA: "O",
  RESOLUCIÓN_DE_ONPE: "R",
  SINIESTRADA: "S",
  RESOLUCION_DEL_JEE: "RJ",
};

export const CANTIDAD_LIMITE_CANDIDATOS = {
  PARLAMENTO: 56,
  DIPUTADOS: 56,
  SENADORES_DEM: 56
}

export const ACTA_LINEAT_CODIGO_ESTADO_DESC = {
  DIGITALIZACION: "Digitalización",
  DIGITACION: "Digitación",
  CONTABILIZADA: "Contabilizada",
  OBSERVADA: "Observada",
  PARA_ENVIO_JEE: "Para envío al JEE",
  RECEPCION_RESOLUCION: "Recibida del JEE",
  PROCESADA_RESOLUCION: "Contabilizada",
};

export const ID_AMBITO_GEOGRAFICO = {
  ID_SIN_AMBITO_GEOGRAFICO: 0,
  ID_NACIONAL: 1,
  ID_EXTRANJERO: 2,
};

export const ID_INICIAL_UBIGEO = {
  ID_DISTRITO_ELECTORAL_0: 0,
  ID_DISTRITO_LIMA: 15,
};

export const TIPO_FILTRO = {
  UBIGEO_NIVEL_01: "ubigeo_nivel_01",
  UBIGEO_NIVEL_02: "ubigeo_nivel_02",
  UBIGEO_NIVEL_03: "ubigeo_nivel_03",
  ELECCION: "eleccion",
  AMBITO_GEOGRAFICO: "ambito_geografico",
  DISTRITO_ELECTORAL: "distrito_electoral",
};

// CONFIGURACIÓN PRINCIPAL PARA GENERACIÓN DE REPORTES EN RESUMEN GENERAL

export const ID_ELECCION: IEleccion = {
  ID_ELECCION_MUNICIPAL: 4,
  ID_ELECCION_GENERAL: 5,
  ID_ELECCION_PRESIDENCIAL: 10,
  ID_ELECCION_DIPUTADOS: 13,
  ID_ELECCION_PARLAMENTO_ANDINO: 12,
  ID_ELECCION_SENADOR_27: 14,
  ID_ELECCION_SENADOR_33: 15,
  ID_ELECCION_REVOCATORIA: 7,
};

export type ElectionIDName =
  | "presidenciales"
  | "diputados"
  | "parlamento_andino"
  | "senadores_27"
  | "senadores_33"
  | "revocatoria";

export const MAIN_ELECTION_IDS: Record<ElectionIDName, number> = {
  diputados: ID_ELECCION.ID_ELECCION_DIPUTADOS,
  parlamento_andino: ID_ELECCION.ID_ELECCION_PARLAMENTO_ANDINO,
  presidenciales: ID_ELECCION.ID_ELECCION_PRESIDENCIAL,
  senadores_27: ID_ELECCION.ID_ELECCION_SENADOR_27,
  senadores_33: ID_ELECCION.ID_ELECCION_SENADOR_33,
  revocatoria: ID_ELECCION.ID_ELECCION_REVOCATORIA,
};

export interface IConfigElectionId {
  electionId: number;
  name: ElectionIDName;
}

export const CONFIG_ELECTION_IDS: IConfigElectionId[] = [
  { electionId: MAIN_ELECTION_IDS.presidenciales, name: "presidenciales" },
  { electionId: MAIN_ELECTION_IDS.diputados, name: "diputados" },
  {
    electionId: MAIN_ELECTION_IDS.parlamento_andino,
    name: "parlamento_andino",
  },
  { electionId: MAIN_ELECTION_IDS.senadores_27, name: "senadores_27" },
  { electionId: MAIN_ELECTION_IDS.senadores_33, name: "senadores_33" },
];

export interface IConfigNameId {
  electionId: number;
  name: string;
}

export const CONFIG_NAMES_FOR_ACTAS: IConfigNameId[] = [
  { electionId: MAIN_ELECTION_IDS.presidenciales, name: "presidenciales" },
  { electionId: MAIN_ELECTION_IDS.diputados, name: "diputados" },
  {
    electionId: MAIN_ELECTION_IDS.parlamento_andino,
    name: "parlamento andino",
  },
  {
    electionId: MAIN_ELECTION_IDS.senadores_27,
    name: "senadores elección múltiple",
  },
  {
    electionId: MAIN_ELECTION_IDS.senadores_33,
    name: "senadores elección única",
  },
];

export type HTMLStatus = "show" | "hide" | "";

export const TIME_TO_LOADING = 1200;

export const DISTRITO_ELECTORAL_EXTRENGERA_ID = 27;

export const DISTRITO_ELECTORAL_LIMA_ID = 15;

export const CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO = 2;

export const ESTADO_ACTA= "N";

export const MENSAJE_REPORTE = {
  SIN_FIRMA_DIGITAL: "En estos momentos no se puede generar el reporte, por favor inténtalo nuevamente más tarde",
};

export interface ListaMenu {
  id?: number;
  nombre?: string;
  padre?: number;
  hijos?: boolean;
  icono?: string;
  orden?: number;
  idEleccion?: number;
  url?: string;
  esPrincipal?: boolean;
  seleccionado?: boolean;
  listaHijos?: ListaMenu[];
  expanded?: boolean;
}

export interface ListaMenuResponse {
  success: boolean;
  message: string;
  data: Array<ListaMenu>;
}
