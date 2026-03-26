import { GenericResponse } from "./response.common";

export interface PadronResponse extends GenericResponse {
  data: Padron;
}
export interface Padron {
  dni: string;
  mesa: string;
}
