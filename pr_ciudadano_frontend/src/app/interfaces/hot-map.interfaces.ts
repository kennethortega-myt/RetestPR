import * as am5 from "@amcharts/amcharts5";

export interface IHotMapUbigeoItem {
  ubigeo: string;
  ubigeoName: string;
  candidateName: string;
  percentage: number;
  validVotes: number;
  percentageValidVotes: number;
}

export type ElectionScope = "peru" | "international";

export interface IPolygonSerieData {
  id: string;
  value: number;
  polygonSettings: {
    fill: am5.Color;
  };
}
