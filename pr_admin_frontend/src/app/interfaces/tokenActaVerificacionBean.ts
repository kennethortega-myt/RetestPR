import { ActaRandomClaimsBean } from './actaRandomClaimsBean';

export interface TokenActaVerificacionBean {
  actaRandom: ActaRandomClaimsBean;
  exp: string;
  iat: string;
}
