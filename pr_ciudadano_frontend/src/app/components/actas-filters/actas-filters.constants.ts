import { ACTA_CODIGO_ESTADO_DESC } from "../../helpers/constantes";
import { MainOptionFilter, ActaOptionFilter } from "./actas-filters.interface";

export const MAIN_OPTIONS_ACTAS_FILTER: MainOptionFilter[] = [
  { value: null, text: "TODOS" },
  { value: false, text: ACTA_CODIGO_ESTADO_DESC.PARA_ENVIO_JEE },
  { value: true, text: ACTA_CODIGO_ESTADO_DESC.CONTABILIZADA },
];

export const DEFAULT_MAIN_OPTION = MAIN_OPTIONS_ACTAS_FILTER[0].value;

export const ACTAS_OPTIONS = {
  observadas: { value: "H", text: "Actas observadas" },
  observadas_sin_datos: { value: "S", text: "Sin datos" },
  observadas_incompleta: { value: "C", text: "Incompleta" },
  observadas_error_material: { value: "E, M, V", text: "Error aritmético" },
  observadas_ilegibilidad: { value: "L, G, P", text: "Ilegibilidad" },
  observadas_sin_firmas: { value: "F", text: "Sin firmas" },
  impugnadas: { value: "I", text: "Acta con votos impugnados" },
  nulidad: { value: "N", text: "Acta con solicitud de nulidad" },
  extraviadas: { value: "X", text: "Acta extraviada" },
  siniestradas: { value: "Y", text: "Acta siniestrada" },  
  observadas_dos_o_mas: { value: "2MAS, H, S, C, E, M, V, L, G, P, F, I, N, X, Y", text: "Dos o más observaciones" },
  observadas_otras: { value: "", text: "Otras observaciones" },
};

export const ACTAS_OPTIONS_V: ActaOptionFilter[] = [
  {  id: "observadas", value: "H", text: "Actas observadas", children: [
    { id: "observadas_sin_datos", value: "S", text: "Sin datos" },
    { id: "observadas_incompleta", value: "C", text: "Incompleta" },
    { id: "observadas_error_material", value: "E, M, V", text: "Error aritmético" },
    { id: "observadas_ilegibilidad", value: "L, G, P", text: "Ilegibilidad" },
    { id: "observadas_sin_firmas", value: "F", text: "Sin firmas" },
  ]},
  {  id: "impugnadas", value: "I", text: "Acta con votos impugnados" },
  {  id: "nulidad", value: "N", text: "Acta con solicitud de nulidad" },
  {  id: "extraviadas", value: "X", text: "Acta extraviada" },
  {  id: "siniestradas", value: "Y", text: "Acta siniestrada" },  
  {  id: "observadas_dos_o_mas", value: "2MAS, H, S, C, E, M, V, L, G, P, F, I, N, X, Y", text: "Dos o más observaciones" },
];
