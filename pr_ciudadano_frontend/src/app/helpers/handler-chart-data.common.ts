import dayjs from "dayjs";

import { IChartBarInfo } from "../interfaces/chart-bar-info.interface";
import { getEncryptStorageEleccionValue } from "./encrypt-storage-eleccion";
import { BarChartItem } from "../interfaces/common.interfaces";
import { SumaryRevocatoria } from "../interfaces/elections.interfaces";
import { getCandidateImageFromAssets, getPoliticImageFromAssets } from "./get-images.helper";

export function separarPorGrupo(
  lista: IChartBarInfo[],
  tamanioGrupo: number
): { list: IChartBarInfo[]; totalPaginasGrafica: number } {
  let listaResult: IChartBarInfo[] = [];
  let grupo: number = 0;
  let conta: number = 1;
  for (const element of lista) {
    if (conta > tamanioGrupo) {
      grupo = grupo + 1;
      conta = 1;
    }
    element.group = grupo;
    listaResult.push(element);
    conta = conta + 1;
  }
  return { list: listaResult, totalPaginasGrafica: grupo };
}

export function getIsEnabledRealDirectoryForImages(): boolean {
  const activoFechaProceso = getEncryptStorageEleccionValue("ACTIVO_FECHA_PROCESO") === "true" ? true : false;
  return activoFechaProceso;
}

export function makeFormattedInformationForBarsGroup(
  summaries: SumaryRevocatoria[],
  codigosPermitidos: string[]
): IChartBarInfo[] {
  let maxValueOfVotes = getMaxValueFromArrayBaseGroup(summaries);

  let sumaries = [...summaries]
    .map((summarie) => {
      summarie.candidato = codigosPermitidos.map((codigo) => {
        // con este mapeo solo tendremos 1 opción para cada codigo
        return summarie.candidato.find((cand) => cand.codigoOpcionVoto == codigo);
      });

      let max_percentage_for_chart = 0;
      let items: IChartBarInfo[] = summarie.candidato.map((candidato) => {
        max_percentage_for_chart = Math.max(max_percentage_for_chart, candidato.porcentajeVotosValidos);
        return {
          number_of_valid_votes: candidato.totalVotos,
          name_of_candidate: candidato.descripcionOpcionVoto,
          name_of_politic_group: candidato.descripcionOpcionVoto,
          url_candidate_image: "",
          urlAgrupacionImage: "",

          no_have_urlAgrupacionImage: true,
          code_of_politic_group: candidato.codigoOpcionVoto.toString(),
          percentage_for_chart: (100 * candidato.totalVotos) / maxValueOfVotes,
          percentage_of_valid_votes: candidato.porcentajeVotosValidos,
          percentage_valid_votes: candidato.porcentajeVotosValidos,
          number_of_candidate: candidato.posicionOpcionVoto.toString(),
        };
      });

      items.sort((a, b) => {
        return Number(a.number_of_candidate) - Number(b.number_of_candidate);
      });

      return {
        number_of_valid_votes: 0,
        name_of_candidate: summarie.cargo,
        name_of_politic_group: summarie.nombreAgrupacionPolitica,
        url_candidate_image: "",
        urlAgrupacionImage: "",

        no_have_urlAgrupacionImage: true,
        code_of_politic_group: summarie.codigoAgrupacionPolitica.toString(),
        percentage_for_chart: max_percentage_for_chart,
        items: items,
      };
    })
    .filter((group) => group.items.length > 0); // Filtrar grupos sin items

  return sumaries
  // return sumaries.sort((a, b) => {
  //   return b.percentage_for_chart - a.percentage_for_chart;
  // });
}

export function makeFormattedInformationForBars(summaries: BarChartItem[], path_folder?: string): IChartBarInfo[] {
  path_folder = path_folder || "candidatos";
  let maxValueOfVotes = getMaxValueFromArray(summaries);

  let summariesForChart = summaries.map((summary) => {
    const { dniCandidato, codigoAgrupacionPolitica } = summary;
    const urlCandidateImage = getCandidateImageFromAssets(dniCandidato, path_folder);    
    const urlAgrupacionImage = getPoliticImageFromAssets(codigoAgrupacionPolitica);

    return {
      name_of_candidate: summary.nombreCandidato,
      number_of_valid_votes: summary.totalVotosValidos,
      url_candidate_image: urlCandidateImage,
      urlAgrupacionImage: urlAgrupacionImage,
      percentage_for_chart: (100 * summary.totalVotosValidos) / maxValueOfVotes,
      name_of_politic_group: summary.nombreAgrupacionPolitica,
      percentage_valid_votes: summary.porcentajeVotosValidos,
      lista: summary.lista,
    } as IChartBarInfo;
  });
  return summariesForChart.sort((a, b) => {
    return b.percentage_for_chart - a.percentage_for_chart;
  });
}

export function makeFormattedInformationForBars2(summaries: BarChartItem[]): IChartBarInfo[] {
  return makeFormattedInformationForBars(summaries, "candidatos");
}

export function getImageCode(code: number): string {
  return code < 10 ? "00000" + code : "0000" + code;
}

export function getStringDNIfromNumberDNI(dni: number): string {
  const currentDNI = dni.toString();
  if (currentDNI.length == 7) {
    return "0" + currentDNI;
  } else {
    return currentDNI;
  }
}

export function makeScaleValuesForBarsGroup(summaries: SumaryRevocatoria[], numberOfScales: number): number[] {
  let maxValueOfVotes = getMaxValueFromArrayBaseGroup(summaries);
  const newMaxValueForY = maxValueOfVotes > 0 ? Math.round(maxValueOfVotes) : 100;
  const ratio = newMaxValueForY / numberOfScales;
  let arr = new Array(numberOfScales).fill("");
  let scales = arr.map((_, index) => {
    return Math.round((numberOfScales - index) * ratio);
  });
  scales.push(0);
  return scales;
}

export function makeScaleValues(summaries: BarChartItem[], numberOfScales: number): number[] {
  const maxValueOfVotes = getMaxValueFromArray(summaries);
  if(numberOfScales > maxValueOfVotes && maxValueOfVotes > 0) {
    numberOfScales = maxValueOfVotes;
  }

  const newMaxValueForY = maxValueOfVotes > 0 ? Math.round(maxValueOfVotes) : 100;
  const ratio = newMaxValueForY / numberOfScales;
  let arr = new Array(numberOfScales).fill("");
  let scales = arr.map((_, index) => {
    return Math.round((numberOfScales - index) * ratio);
  });
  scales.push(0);
  return scales;
}

export function getNewMaxValueForY(maxValue: number): number {
  let numberOfDigits = getNumberOfDigits(maxValue);
  let tenElevation = getTenElevation(numberOfDigits - 1);
  let newMaxValueForY = Math.ceil(maxValue / tenElevation) * tenElevation;
  return newMaxValueForY;
}

export function getMaxValueFromArrayBaseByPercent(summaries: any[]): number {
  let maxValue = 0;
  summaries.forEach((summary) => {
    if (summary.percentage_for_chart > maxValue) {
      maxValue = summary.percentage_for_chart;
    }
  });
  return maxValue;
}

export function getMaxValueFromArrayBaseGroup(summaries: SumaryRevocatoria[]): number {
  let maxValue = 0;
  summaries.forEach((summary) => {
    summary.candidato.forEach((candidato) => {
      if (
        candidato.totalVotos > maxValue &&
        (Number(candidato.codigoOpcionVoto) == 1 || Number(candidato.codigoOpcionVoto) == 2)
      ) {
        maxValue = candidato.totalVotos;
      }
    });
  });
  return maxValue;
}

export function getMaxValueFromArrayBase(summaries: any[]): number {
  let maxValue = 0;
  summaries.forEach((summary) => {
    if (summary.totalVotosValidos > maxValue) {
      maxValue = summary.totalVotosValidos;
    }
  });
  return maxValue;
}

export function getMaxValueFromArray(summaries: BarChartItem[]): number {
  return getMaxValueFromArrayBase(summaries);
}

function getTenElevation(exponent: number): number {
  let arr = new Array(exponent).fill("");
  let tenElevation = 1;
  arr.forEach(() => {
    tenElevation = tenElevation * 10;
  });
  return tenElevation;
}

function getNumberOfDigits(value: number): number {
  let valueString = value.toString();
  return valueString.length;
}

export function makeScaleValuesFromGenericArray(summaries: number[], numberOfScales: number): number[] {
  const maxValueOfVotes = getNewMaxValueFromArray(summaries);
  const newMaxValueForY = maxValueOfVotes > 0 ? Math.round(maxValueOfVotes * 1.0) : 300; // no se aumenta nada
  const ratio = newMaxValueForY / (numberOfScales - 1);
  let arr = new Array(numberOfScales - 1).fill("");
  let scales = arr.map((_, index) => {
    return Math.round((numberOfScales - index - 1) * ratio);
  });
  scales.push(0);
  return scales;
}

export function getNewMaxValueFromArray(arr: number[]): number {
  let maxValue = 0;
  arr.forEach((el) => {
    if (el > maxValue) {
      maxValue = el;
    }
  });
  return maxValue;
}
