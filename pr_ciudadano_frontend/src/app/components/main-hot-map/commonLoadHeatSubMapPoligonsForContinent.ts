import * as am5 from "@amcharts/amcharts5";

import { from, take } from "rxjs";
import { IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import { settingPoligonOfSection } from "./settingPoligonOfSection";
import {
  IFeatureElement,
  IFeatureElementGet,
  CommonLoadHeatSubMapPoligonsParams,
  LoadEachHotUbigeoParams,
} from "./map.interfaces";

export function commonLoadHeatSubMapPoligons(commonLoadHeatSubMapPoligonsParams: CommonLoadHeatSubMapPoligonsParams) {
  const { map, chart, rootMap, hotMapsUbigeos, callback, featureElementType, heatLegendTextType, tooltipType, clickMap } =
    commonLoadHeatSubMapPoligonsParams;
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response!) as IFeatureElement;

      loadEachHotUbigeo({
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: hotMapsUbigeos,
        callback: callback,
        featureElementType: featureElementType,
        geodata: geodata,
        heatLegendTextType: heatLegendTextType,
        tooltipType: tooltipType,
        clickMap: clickMap
      });
    });
}

// LOCAL HELPER METHODS

/**
 * Este método recorre cada ubigeo del array y carga en el mapa el color que le corresponde
 */
function loadEachHotUbigeo(loadEachHotUbigeoParams: LoadEachHotUbigeoParams) {
  const { chart, rootMap, hotMapsUbigeos, callback, featureElementType, geodata, heatLegendTextType, tooltipType, clickMap } =
    loadEachHotUbigeoParams;
  hotMapsUbigeos.forEach((hotMapUbigeo) => {
    let currentGeodata = JSON.parse(JSON.stringify(geodata)) as IFeatureElement;
    const currentFormater = featureElementGets[featureElementType];
    currentGeodata.features = currentFormater(currentGeodata, hotMapUbigeo);

    if (currentGeodata.features.length && !currentGeodata.features[0]) {
      return;
    }

    settingPoligonOfSection(hotMapUbigeo, chart, rootMap, currentGeodata, callback, heatLegendTextType, tooltipType, clickMap);
  });
}

function getFeatureElementTypes01(currentGeodata: IFeatureElement, hotMapUbigeo: IHotMapUbigeoItem): any[] {
  return [
    currentGeodata.features.find((feature) => {
      return feature.id == Number(hotMapUbigeo.ubigeo);
    }),
  ];
}
function getFeatureElementTypes02(currentGeodata: IFeatureElement, hotMapUbigeo: IHotMapUbigeoItem): any[] {
  return [
    currentGeodata.features.find((feature) => {
      return feature.properties.ID == hotMapUbigeo.ubigeo;
    }),
  ];
}
function getFeatureElementTypes03(currentGeodata: IFeatureElement, hotMapUbigeo: IHotMapUbigeoItem): any[] {
  return [
    currentGeodata.features.find((feature) => {
      return feature.id == hotMapUbigeo.ubigeo;
    }),
  ];
}
const featureElementGets: IFeatureElementGet = {
  type01: getFeatureElementTypes01,
  type02: getFeatureElementTypes02,
  type03: getFeatureElementTypes03,
};
