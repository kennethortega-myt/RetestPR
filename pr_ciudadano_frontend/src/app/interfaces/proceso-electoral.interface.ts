import { GenericResponse } from "./response.common";

export interface IProcesoElectoralData {
  acronimo: string;
  fechaProceso: number; // en formato timestamp
  id: number;
  idEleccionPrincipal?: number;
  nombre: string;
  tipoProcesoElectoral?: string;
  activoFechaProceso: boolean;
}

export interface IProcesoElectoralResponse extends GenericResponse {
  data?: IProcesoElectoralData;
}
