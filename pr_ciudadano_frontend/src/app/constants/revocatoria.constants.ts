/**
 * Este tipo debe ser el mismo valor que en backend
 */
export type AutoridadType = "Alcalde" | "Regidor";

export const TIPOS_DE_AUTORIDADES: AutoridadType[] = ["Alcalde", "Regidor"];

export type TiposDeAutoridadesTexts = { singular: string; plural: string };

export const AUTORIDAD_DEFAULT_TEXT: TiposDeAutoridadesTexts = {
  plural: "",
  singular: "",
};

export const TIPOS_AUTORIDADES_PARA_REVOCATORIA: { [key in AutoridadType]: TiposDeAutoridadesTexts } = {
  Alcalde: { plural: "Alcaldes distritales", singular: "Alcalde distrital" },
  Regidor: { plural: "Regidores distritales", singular: "Regidor distrital" },
};
