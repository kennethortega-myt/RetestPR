import { GenericResponse } from "../services/common/response.common";

export interface Fecha {
  id: number;
  fechaProceso: string;
  servicioFirma: string;
  cDescripcion: Date;
}

export interface FechaResponse extends GenericResponse {
  data: Fecha;
}
