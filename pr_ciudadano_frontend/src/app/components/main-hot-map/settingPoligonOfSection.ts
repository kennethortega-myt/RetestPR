import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import { FINAL_COLOR_PERCENTAGE_100, INITIAL_COLOR_PERCENTAGE_0 } from "../../helpers/map.config-congresales";
import { IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import {
  getDefaultHTMLTooltip,
  getDegradatedColorFromPercentage,
  getHTMLTooltip,
  HeatLegendTextType,
  TooltipType,
} from "./map-auxiliary-elements";

export function settingPoligonOfSection(
  hotMapUbigeo: IHotMapUbigeoItem,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  currentGeodata: any,
  callback: (event: am5.ISpritePointerEvent) => void,
  heatLegendTextType: HeatLegendTextType,
  tooltipType: TooltipType, 
  clickMap?: boolean
) {
  const { percentage, percentageValidVotes } = hotMapUbigeo;

  const heatLegendIsType01 = heatLegendTextType == "type_1";
  const percentageToTransform = heatLegendIsType01 ? percentage : percentageValidVotes;
  // Validación de valores inválidos: null, undefined, NaN, o negativos se tratan como 0
  const isValidPercentage = 
    percentageToTransform != null && 
    !isNaN(percentageToTransform) && 
    isFinite(percentageToTransform) && 
    percentageToTransform >= 0;

  let currentPercentage: number;
  if (!isValidPercentage) {
    // Si el valor es inválido, usar 0 para mostrar el color inicial
    currentPercentage = 0;
  } else if (percentageToTransform > 100) {
    // Si es mayor a 100, limitar a 100
    currentPercentage = 100;
  } else {
    // Valor válido entre 0 y 100
    currentPercentage = percentageToTransform;
  }

  const currentColor = getDegradatedColorFromPercentage(currentPercentage, {
    init: INITIAL_COLOR_PERCENTAGE_0,
    end: FINAL_COLOR_PERCENTAGE_100,
  });

  const polygonSeries_Base = chart.series.push(
    am5map.MapPolygonSeries.new(rootMap, {
      geoJSON: currentGeodata as GeoJSON.GeoJSON,
      fill: am5.color(currentColor),
      stroke: am5.color(0xffffff),
    })
  );
  polygonSeries_Base.show();
  polygonSeries_Base.mapPolygons.template.setAll({
    tooltipHTML: getCustomHTMLTooltip(hotMapUbigeo, tooltipType),
    toggleKey: "active",
    interactive: true,
    strokeWidth: 0.5,
    cursorOverStyle: clickMap ? "pointer" : "",
  });
  polygonSeries_Base.mapPolygons.template.states.create("hover", {
    fill: am5.color("#2A71B9"),
  });
  polygonSeries_Base.mapPolygons.template.events.on("click", clickMap ? callback : () => {}
  );
}

export function getCustomHTMLTooltip(hotMapUbigeo: IHotMapUbigeoItem, tooltipType: TooltipType): string {
  if (tooltipType == "tooltip_01") {
    return getHTMLTooltip(hotMapUbigeo);
  }
  return getDefaultHTMLTooltip(hotMapUbigeo.ubigeoName);
}
