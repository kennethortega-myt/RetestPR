/**
 * BAR CHART DATA
 * This interface should be used to implement in services
 * that getting data for bar chars
 */
export interface BarChartItem {
  nombreCandidato: string; // mandatory
  totalVotosValidos: number; // mandatory
  nombreAgrupacionPolitica: string; // mandatory
  codigoAgrupacionPolitica?: string;
  dniCandidato?: string;
  porcentajeVotosValidos?: number;
  porcentajeVotosEmitidos?: number;
  totalCandidatos?: number;
  lista?: number;
}