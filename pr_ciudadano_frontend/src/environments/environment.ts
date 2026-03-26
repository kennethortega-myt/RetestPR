import { BICAMERALIDAD } from '../app/settings/procesos-electorales.settings';

const local = 'http://localhost:8099/presentacion-backend/';
const dev = 'https://resultadoelectoraldesarrollo.dev.onpe.gob.pe/presentacion-backend/';
const prod = 'https://pr.deployedpe.com/api/';
const tunel = 'https://mm9v25rz-8096.brs.devtunnels.ms/presentacion-backend/';

export const environment = {
  production: false,
  apiUrlLocal: dev,
  procesoConfig: BICAMERALIDAD
};
