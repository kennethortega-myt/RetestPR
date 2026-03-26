import { LineaTiempo } from './linea-tiempo.model';
import { CandidatoModeloUno } from '../../../components/tabla-escrutinio-modelo-uno/tabla-escrutinio-modelo-uno.model';
import { Base } from '../base.model';
import { Archivo } from '../../../components/modal-visor-pdf/modal-visor-pdf.component';

export class DetalleActaObservada extends Base {
  declare data?: Acta;
}
export class Acta {
  archivos?: [Archivo];
  centroPoblado?: string;
  codigoEstadoActa?: string;
  codigoMesa?: string;
  descripcionEstadoActa?: string;
  estadoActaResolucion?: string;
  descripcionSubEstadoActa?: string;
  descripcionMesa?: string;
  estadoDescripcionActaResolucion?: string;
  estadoActa?: string;
  estadoComputo?: string;
  id?: number;
  idEleccion?: number;
  idMesa?: number;
  nombreLocalVotacion?: string;
  porcentajeParticipacionCiudadana?: number;
  totalAsistentes?: number;
  totalElectoresHabiles?: number;
  totalVotosEmitidos?: number;
  totalVotosValidos?: number;
  ubigeoNivel01?: string;
  ubigeoNivel02?: string;
  ubigeoNivel03?: string;
  detalle?: [Detalle];
  lineaTiempo?: [LineaTiempo];  
  codigoSolucionTecnologica?: number;
  descripcionSolucionTecnologica?: string;
  idAmbitoGeografico?: number;
}

export class Detalle {
  ccodigo?: string;
  descripcion?: string;
  estado?: number;
  grafico?: number;
  nagrupacionPolitica?: number;
  nporcentajeVotosEmitidos?: number;
  nporcentajeVotosValidos?: number;
  nposicion?: number;
  nvotos?: number;
  totalCandidatos?: number;
  candidato?: [CandidatoModeloUno];
}
