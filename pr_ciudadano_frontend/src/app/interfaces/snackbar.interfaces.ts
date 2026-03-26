export interface SnackbarData {
  message: string;
}

export type SnackbarMessageType = "default" | "thereIsNoInformationForReport";

export const SNACKBAR_MESSAGES: { [key in SnackbarMessageType]: string } = {
  default: "La descarga es una vez por ubicación.",
  thereIsNoInformationForReport: "No se tiene información para generar el reporte.",
};
