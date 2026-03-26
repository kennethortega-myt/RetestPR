import { GenericResponse } from "../../interfaces/response.common";
import {
  getNewMaxValueForY,
  getMaxValueFromArrayBase,
} from "../../helpers/handler-chart-data.common";

export interface ChartBarInformacionResponse extends GenericResponse {
  data: IChartBarInformacionRest[];
}
export interface IChartBarInformacionRest {
  totalVotosValidos: number;
  codigo: string;
  nombreAgrupacionPolitica: string;
  logo: string;
  percentage_for_chart?: number;
  simbolo: string;
}

export interface IChartBarInformacion {
  number_of_valid_votes: number;
  name_of_candidate: string;
  codigoAgrupacionPolitica?: string;
  nombreAgrupacionPolitica: string;
  url_candidate_image: string;
  percentage_for_chart?: number;
  urlAgrupacionImage: string;
}

export function formateoInformacionBarras(
  summaries: IChartBarInformacionRest[]
): IChartBarInformacion[] {
  let maxValueOfVotes = getMaxValueFromArrayBase(summaries);
  let newMaxValueForY = getNewMaxValueForY(maxValueOfVotes);
  let summariesForChart = summaries.map((summary) => {
    return {
      number_of_valid_votes: summary.totalVotosValidos,
      url_candidate_image: "assets/img/candidatos/000003.jpg",
      urlAgrupacionImage: "assets/img/partidos/000001.jpg",
      percentage_for_chart:
        newMaxValueForY == 0
          ? 0
          : (100 * summary.totalVotosValidos) / newMaxValueForY,
      nombreAgrupacionPolitica: summary.nombreAgrupacionPolitica,
    } as IChartBarInformacion;
  });
  return summariesForChart.sort((a, b) => {
    return b.percentage_for_chart - a.percentage_for_chart;
  });
}

export interface IResumenPorCandidato {
  nombreAgrupacionPolitica: string;
  idFotoAgrupacionPolitica: string;
  nombreCandidato: string;
  idFotoCandidato: string;
  totalVotosValidos: number;
}
