import { Base } from './base.model';

export class Reporte extends Base {
  ambitoGeografico?: string;
  estado?: string;
  fechaConsulta?: string;
  localVotacion?: string;
  tipoEleccion?: string;
  tipoReporte?: string;
  ubigeoNivel1?: string;
  ubigeoNivel2?: string;
  ubigeoNivel3?: string;
  urlReporte?: string;
}
