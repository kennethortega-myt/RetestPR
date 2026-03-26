import { DetalleVotoCandidatoPresidencialBean } from "./detalle-voto-candidato-presidencial-bean";

export interface DetalleDataPresidencialBean {
  ESTADO: string;
  VOTOS_CANDIDATO: Array<DetalleVotoCandidatoPresidencialBean>;
  VOTOS_BLANCO: number;
  VOTOS_NULOS: number;
  VOTOS_VALIDOS: number;
}
