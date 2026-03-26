import { Archivo } from '../../../components/modal-visor-pdf/modal-visor-pdf.component';
import { Base, Paginado } from '../base.model';

export class ActaObservada extends Base {
  declare data?: Data;
}
export class Data extends Paginado {
  content?: [ContentActaObservada];
}
export class ContentActaObservada {
  codigoEstadoActa?: string;
  codigoMesa?: string;
  descripcionAmbitoGeografico?: string;
  descripcionEleccion?: string;
  descripcionEstadoActa?: string;
  estadoDescripcionActaResolucion?: string;
  descripcionMesa?: string;
  estadoActa?: string;
  estadoComputo?: string;
  id?: number;
  idAmbitoGeografico?: number;
  idEleccion?: number;
  idMesa?: number;
  nombreArchivoActa?: string;
  nombreLocalVotacion?: string;
  nombresArchivoResoluciones?: [string];
  totalElectoresHabiles?: number;
  totalVotosEmitidos?: number;
  totalVotosValidos?: number;
  ubigeoNivel01?: string;
  ubigeoNivel02?: string;
  ubigeoNivel03?: string;
  activo: boolean = false;
  archivos?: Archivo[];
  archivosActa?: Archivo[];
  archivosResolucion?: Archivo[];
}
