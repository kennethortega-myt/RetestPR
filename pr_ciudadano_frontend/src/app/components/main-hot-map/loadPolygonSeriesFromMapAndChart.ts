import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import { from, take } from "rxjs";

import { getDegradatedColorFromPercentage, HeatLegendTextType, TooltipType } from "./map-auxiliary-elements";
import { FINAL_COLOR_PERCENTAGE_100, INITIAL_COLOR_PERCENTAGE_0 } from "../../helpers/map.config-congresales";
import { IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import { IFeatureElement } from "./map.interfaces";
import { getCustomHTMLTooltip } from "./settingPoligonOfSection";
import { COUNTRY_PERU_IDS } from "./maps.constants";

export function loadPolygonSeriesFromMapAndChartForDistrict(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  ubigeo: string,
  selectedHotMapDistrict: IHotMapUbigeoItem,
  heatLegendTextType: HeatLegendTextType,
  tooltipType: TooltipType,
  clickMap?: boolean
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response) as IFeatureElement;
      let currentGeodata = JSON.parse(JSON.stringify(geodata)) as IFeatureElement;
      currentGeodata.features = [currentGeodata.features.find((feature) => feature.properties.ID == ubigeo)];

      settingPolygonSeriesBase(chart, rootMap, selectedHotMapDistrict, heatLegendTextType, currentGeodata, tooltipType, clickMap);
    });
}

export function loadPolygonSeriesFromMapAndChartForCountry(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  ubigeo: string,
  selectedHotMapDistrict: IHotMapUbigeoItem,
  heatLegendTextType: HeatLegendTextType,
  tooltipType: TooltipType,
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response) as IFeatureElement;
      let currentGeodata = JSON.parse(JSON.stringify(geodata)) as IFeatureElement;
      const foundFeature = currentGeodata.features.find((feature) => feature.id == Number(ubigeo));
      currentGeodata.features = [foundFeature];

      const hotMapItem =
        ubigeo === '944200' && foundFeature?.properties?.name
          ? { ...selectedHotMapDistrict, ubigeoName: foundFeature.properties.name }
          : selectedHotMapDistrict;

      settingPolygonSeriesBase(chart, rootMap, hotMapItem, heatLegendTextType, currentGeodata, tooltipType);
    });
}

function settingPolygonSeriesBase(
  chart: am5map.MapChart,
  rootMap: am5.Root,
  selectedHotMapDistrict: IHotMapUbigeoItem,
  heatLegendTextType: HeatLegendTextType,
  currentGeodata: IFeatureElement,
  tooltipType: TooltipType = "default",
  clickMap?: boolean
) {
  const percentage =
    heatLegendTextType == "type_1" ? selectedHotMapDistrict?.percentage : selectedHotMapDistrict?.percentageValidVotes;
  
  // Validación de valores inválidos: null, undefined, NaN, o negativos se tratan como 0
  const isValidPercentage = 
    percentage != null && 
    !isNaN(percentage) && 
    isFinite(percentage) && 
    percentage >= 0;

  let currentPercentage: number;
  if (!isValidPercentage) {
    // Si el valor es inválido, usar 0 para mostrar el color inicial
    currentPercentage = 0;
  } else if (percentage > 100) {
    // Si es mayor a 100, limitar a 100
    currentPercentage = 100;
  } else {
    // Valor válido entre 0 y 100
    currentPercentage = percentage;
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
  polygonSeries_Base.mapPolygons.template.setAll({
    tooltipHTML: getCustomHTMLTooltip(selectedHotMapDistrict, tooltipType),
    toggleKey: "active",
    interactive: true,
    strokeWidth: 0.5,
    cursorOverStyle: clickMap ? "pointer" : "",
  });
  polygonSeries_Base.mapPolygons.template.states.create("hover", {
    fill: am5.color("#2A71B9"),
  });
}

export function loadPolygonSeriesFromMapAndChart(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  callback: () => void = null
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response);
      loadSinglePolygonSeriesFromMapAndChart(geodata, chart, rootMap, callback);
    });
}

export function loadSinglePolygonSeriesFromMapAndChart(
  geodata: { type: string; features: any[] },
  chart: am5map.MapChart,
  rootMap: am5.Root,
  callback: () => void = null
) {
  const polygonSeries_Base = chart.series.push(
    am5map.MapPolygonSeries.new(rootMap, {
      geoJSON: geodata as GeoJSON.GeoJSON,
      fill: am5.color(0xc3c3c3),
      stroke: am5.color(0xffffff),
    })
  );
  polygonSeries_Base.mapPolygons.template.setAll({
    strokeWidth: 0.5,
  });
  if (callback) {
    callback();
  }
}

export function loadPolygonSeriesFromMapAndChartLockPeru(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  callback: () => void = null
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response);
      
      let currentGeodata = JSON.parse(JSON.stringify(geodata)) as {
        type: string;
        features: any[];
      };
      currentGeodata.features = currentGeodata.features.filter((feature) => COUNTRY_PERU_IDS.includes(feature.id));

      loadSinglePolygonSeriesFromMapAndChart(currentGeodata, chart, rootMap, callback);
    });
}
