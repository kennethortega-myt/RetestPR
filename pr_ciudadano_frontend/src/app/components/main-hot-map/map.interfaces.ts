import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import { IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import { HeatLegendTextType, TooltipType } from "./map-auxiliary-elements";

export interface IMapProvinceFeature {
  geometry: {
    coordinates: any[];
    type: string;
  };
  properties: {
    DEPARTAMEN: string;
    DISTRITO: string;
    ID: string;
    PROVINCIA: string;
    name: string;
  };
  type: string;
}
export interface IMapProvinceResponse {
  features: IMapProvinceFeature[];
  name: string;
  type: string;
}

export type FeatureElementType = "type01" | "type02" | "type03";

export type FeatureElementFormater = (currentGeodata: IFeatureElement, hotMapUbigeo: IHotMapUbigeoItem) => any[]; // it's a function

export type IFeatureElementGet = {
  [key in FeatureElementType]: FeatureElementFormater;
};
export interface IFeatureElement {
  type: string;
  features: any[];
}

export interface CommonLoadHeatSubMapPoligonsParams {
  map: string;
  chart: am5map.MapChart;
  rootMap: am5.Root;
  hotMapsUbigeos: IHotMapUbigeoItem[];
  callback: (event: am5.ISpritePointerEvent) => void;
  featureElementType: FeatureElementType;
  heatLegendTextType: HeatLegendTextType;
  tooltipType: TooltipType;
  clickMap?: boolean
}

export interface LoadEachHotUbigeoParams {
  chart: am5map.MapChart;
  rootMap: am5.Root;
  hotMapsUbigeos: IHotMapUbigeoItem[];
  callback: (event: am5.ISpritePointerEvent) => void;
  featureElementType: FeatureElementType;
  geodata: IFeatureElement;
  heatLegendTextType: HeatLegendTextType;
  tooltipType: TooltipType;
  clickMap?: boolean
}
