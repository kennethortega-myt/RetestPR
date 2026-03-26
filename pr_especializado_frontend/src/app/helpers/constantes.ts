import { environment } from '../../environments/environment';
import { ActaOptionFilter, MainOptionFilter } from '../interfaces/input/acta/acta-filter.interface';

export const MULTIPLIER_HOT_MAP = 1;
export const MULTIPLIER_PERCENTAGE_VALID_VOTES = 10;
export const INITIAL_COLOR_PERCENTAGE_0 = '#DFE5EB';
export const FINAL_COLOR_PERCENTAGE_100 = '#295789';
export const MENSAJE_ORIGINAL = 'MensajeOriginal';
export const PAGINA_INICIAL = 0;
export const TAMANIO_PAGINA = 10;
export const TAMANIO_PAGINA_ACTAS_OBS = 10;
export const TAMANIO_PAGINA_ACTAS_OBS_MOVIL = 5;
export const REPORTE_GENERALES = 1;
export const REPORTE_OBSERVADAS = 2;
export const VALOR_TODOS = 0;

export const LISTA_TIPO_REPORTE = [{
  text: 'TipoReporte.ActasContabilizadas',
  value: REPORTE_GENERALES
}, {
  text: 'TipoReporte.ActasContabilizadasJEE',
  value: REPORTE_OBSERVADAS
}];
export const VALOR_PONDERADO = 1.05;
export const LENGTH_ESCALAS = 4;// [0,1,2,3,4]
export const MENSAJE_BUSQUEDA_POR_FECHAS = 'Selecciona el rango de fechas para poder mostrar la información que buscas';
export const MENSAJE_NO_DATA_PARA_FECHAS = 'No hay información para el rango de fechas seleccionado';
export const MENSAJE_NO_DATA_EXISTS = 'No se encontraron registros con el filtro seleccionado';
export const environmentApíUrl = environment.apiUrl;
export const imageUrlToDownload = environment.apiUrl + '/actas/file?id=';
export const reporterUrlToDownload = environment.apiUrl + '/reportes/file?id=';
export const MENSAJE_ACTAS_OBSERVADAS_SIN_RESULTADOS = 'De acuerdo a la actualización del {FECHA_HORA}, no se cuenta con actas observadas en el criterio de búsqueda seleccionado.';
export const MENSAJE_NO_DATA_CONSULTA='';
export const MENSAJE_NO_DATA_REPORTES='';
export const ACTA_CODIGO_ESTADO_DESC: any = {
  PENDIENTE: "Pendiente",
  CONTABILIZADA: "Contabilizada",
  OBSERVADA: "Observada",
  PARA_ENVIO_JEE: "Para envío al JEE",
};

export const MAIN_OPTIONS_ACTAS_FILTER: MainOptionFilter[] = [
  { value: 0, text: "ActasEnviadasJEE.Todos" },
  { value: 2, text: "ActasEnviadasJEE.ParaJEE" },
  { value: 1, text: "ActasEnviadasJEE.Contabilizadas" },
];

export const ACTAS_OPTIONS_V: ActaOptionFilter[] = [
  {  id: "observadas", value: "H", text: "ActasEnviadasJEE.ActasObservadas", children: [
    { id: "observadas_sin_datos", value: "S", text: "ActasEnviadasJEE.SinDatos" },
    { id: "observadas_incompleta", value: "C", text: "ActasEnviadasJEE.Incompleta" },
    { id: "observadas_error_material", value: "E, M, V", text: "ActasEnviadasJEE.ErrorMaterial" },
    { id: "observadas_ilegibilidad", value: "L, G, P", text: "ActasEnviadasJEE.Ilegibilidad" },
    { id: "observadas_sin_firmas", value: "F", text: "ActasEnviadasJEE.SinFirmas" },
  ]},
  {  id: "impugnadas", value: "I", text: "ActasEnviadasJEE.ActaConVotosImpugnados" },
  {  id: "nulidad", value: "N", text: "ActasEnviadasJEE.ActaConSolicitudNulidad" },
  {  id: "extraviadas", value: "X", text: "ActasEnviadasJEE.ActaExtraviada" },
  {  id: "siniestradas", value: "Y", text: "ActasEnviadasJEE.ActaSiniestrada" },  
  {  id: "observadas_dos_o_mas", value: "2MAS, H, S, C, E, M, V, L, G, P, F, I, N, X, Y", text: "ActasEnviadasJEE.DosOMasObservaciones" },
];

export const TIME_BETWWEN_MODULES = 225;
export const ID_ELECCION_PRINCIPAL = 'ID_ELECCION_PRINCIPAL'

export const SESSION_KEY = 'session_id';
export const ERROR_SESSION = 'No se encontró el identificador de sesión. Por favor, recargue la página.';
export const REPORTE_NO_DISPONIBLE = 'El archivo solicitado no se encuentra disponible en este momento.';
export const ARCHIVO_NO_DISPONIBLE = 'Archivo no disponible';
export const CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO = 2;

export const FIELD_LIMITS  = {
  USUARIO_MAX_LENGTH_FAKE: 51,
  USUARIO_MAX_LENGTH: 50,
  USUARIO_MIN_LENGTH: 3,
  CLAVE_MAX_LENGTH_FAKE: 31,
  CLAVE_MAX_LENGTH:30,
  CLAVE_MIN_LENGTH:8
}

export const ERROR_SESSION_UNICA = 12;
export const CODE_ERROR_403 = 403;

export const AMBITO = {
    1: 'PERÚ',
    2: 'EXTRANJERO'
}