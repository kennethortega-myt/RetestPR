import { Base } from './base.model';

export class ResumenTotal extends Base {
  contabilizadas?: number;
  enviadasJee?: number;
  pendientesJee?: number;

  actasContabilizadas?: number; //Porcentual
  actasEnviadasJee?: number; //Porcentual
  actasPendientesJee?: number; //Porcentual
  contabilizadasPorcentaje?: number;
  enviadasJeePorcentaje?: number;
  pendientesJeePorcentaje?: number;

  participacionCiudadana?: number;
  participacionCiudadanaTotal?: number;

  fechaActualizacion?: string;

  electoresAsistentes?: number;
  electoresAusentes?: number;
  totalActas?: number;
  totalAsistentes?: number;
  totalAusentes?: number;
  totalElectoresHabiles?: number;
  totalVotosEmitidos?: number;
  idEleccion?: number;
}
