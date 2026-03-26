import { from, take } from "rxjs";
import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import { MapaCalor } from "../interfaces/resumen-general-bean";
import { settingCustomData } from "./map.auxiliari-functions";
import { IMapaCongresal } from "../interfaces/mapa-calor-congresal.interfaces";
import { FeatureCollection, Geometry } from "geojson";
import { COUNTRY_PERU_IDS } from "../components/main-hot-map/maps.constants";
import { DISTRITO_ELECTORAL_EXTRENGERA_ID } from "./constantes";

export const INITIAL_COLOR_PERCENTAGE_0 = "#DFE5EB";
export const FINAL_COLOR_PERCENTAGE_100 = "#295789";

export function loadPolygonsWithoutActions(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root,
  callback: () => void = null
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      let geodata = am5.JSONParser.parse(result.response) as {
        type: string;
        features: any[];
      };
      let currentGeodata = JSON.parse(JSON.stringify(geodata)) as {
        type: string;
        features: any[];
      };

      currentGeodata.features = currentGeodata.features.filter((feature) => COUNTRY_PERU_IDS.includes(feature.id));

      const polygonSeries_Base = chart.series.push(
        am5map.MapPolygonSeries.new(rootMap, {
          geoJSON: currentGeodata as GeoJSON.GeoJSON,
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
    });
}

export function loadPolygonsWithActionsInMap(
  map: string,
  chart: am5map.MapChart,
  mapaCalor: MapaCalor[],
  polygonSeries: am5map.MapPolygonSeries,
  thisRoot: IMapaCongresal,
  rootMap: am5.Root
) {
  let { esEstranjero, oneFilter, idDistritoElectoral } = thisRoot.datos;
  const isExtranjero = esEstranjero || idDistritoElectoral === DISTRITO_ELECTORAL_EXTRENGERA_ID;
  let COUNTRYS_BLOCKED = isExtranjero ? COUNTRY_PERU_IDS : COUNTRY_PERU_IDS;
  let clickCountries = !oneFilter;

  am5.net.load(map, chart).then(function (result) {
    let geodata: FeatureCollection<Geometry, any> = am5.JSONParser.parse(result.response);

    geodata.features = geodata.features.filter((feature) => {
      const featureId = feature.id || feature.properties?.id;
      return !COUNTRYS_BLOCKED.includes(featureId);
    });

    if(!isExtranjero && idDistritoElectoral == 22) {
      geodata.features = geodata.features.filter((feature) => {
        const featureId = feature.id || feature.properties?.id;
        return !COUNTRYS_BLOCKED.includes(featureId);
      });
    }

    if(!isExtranjero && idDistritoElectoral != DISTRITO_ELECTORAL_EXTRENGERA_ID) {
      geodata.features = geodata.features.filter((feature) => {
        return feature.id == idDistritoElectoral;
      });
    }

    geodata.features = geodata.features.map((feature) => {
      feature.properties.name = (feature.properties.name as string).toUpperCase();
      return feature;
    });

    if (geodata.features.length === 0) {
      console.warn("No se encontraron features para el mapa. idDistritoElectoral:", idDistritoElectoral, "esEstranjero:", esEstranjero, "isExtranjero:", isExtranjero);
      return;
    }

    let data = [];
    if (mapaCalor != undefined) {
      for (let i = 0; i < geodata.features.length; i++) {
        if (COUNTRYS_BLOCKED.includes(geodata.features[i].id + "")) {
          data.push({
            id: geodata.features[i].id,
            value: 0,
            polygonSettings: {
              fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
            },
          });
        } else {
          let tmp = !isExtranjero
            ? mapaCalor.find((x) => x.distritoElectoral == geodata.features[i].id)
            : mapaCalor.find((x) => x.ubigeoNivel02 == geodata.features[i].id) ||
              mapaCalor.find((x) => x.ubigeoNivel01 == geodata.features[i].id);
          settingCustomData(tmp, data, geodata, i);
        }
      }
    }

    // button.set("cursorOverStyle", "pointer");

    if (clickCountries) {
      polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
        document.body.style.cursor = "pointer";
      });

      polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
        document.body.style.cursor = "default";
      });

      polygonSeries.mapPolygons.template.events.on("click", function (ev) {
        const dataContext = ev.target.dataItem.dataContext as any;
        if (!isNaN(dataContext.id)) {
          thisRoot.eventClickMapa.emit(dataContext.id);
          document.body.style.cursor = "default";
        }
      });
    }

    // if(!(esEstranjero && oneFilter)){
    let tooltip = am5.Tooltip.new(rootMap, {
      autoTextColor: false,
    });

    tooltip.get("background").setAll({
      fill: am5.color(0x003874),
    });
    polygonSeries.mapPolygons.template.setAll({
      tooltip: tooltip,
    });
    // }

    polygonSeries.set("geoJSON", geodata);
    polygonSeries.data.setAll(data);
  });
}

export function removeBlockPoligons(
  map: string,
  chart: am5map.MapChart,
  rootMap: am5.Root
) {
  from(am5.net.load(map, chart))
    .pipe(take(1))
    .subscribe((result) => {
      chart.series.removeValue(chart.series.getIndex(1));
    });
}

export function updatePolygonsWithActionsInMap(
  map: string,
  chart: am5map.MapChart,
  mapaCalor: MapaCalor[],
  polygonSeries: am5map.MapPolygonSeries,
  thisRoot: IMapaCongresal,
  rootMap: am5.Root
) {
  let { esEstranjero, oneFilter, idDistritoElectoral } = thisRoot.datos;
  const isExtranjero = esEstranjero || idDistritoElectoral === DISTRITO_ELECTORAL_EXTRENGERA_ID;
  let COUNTRYS_BLOCKED = isExtranjero ? COUNTRY_PERU_IDS : COUNTRY_PERU_IDS;
  let clickCountries = !oneFilter;

  am5.net.load(map, chart).then(function (result) {
    let geodata: FeatureCollection<Geometry, any> = am5.JSONParser.parse(result.response);

    if(!isExtranjero && idDistritoElectoral == 22) {
      geodata.features = geodata.features.filter((feature) => {
        const featureId = feature.id || feature.properties?.id;
        return !COUNTRYS_BLOCKED.includes(featureId);
      });
    }

    if(!isExtranjero && idDistritoElectoral != DISTRITO_ELECTORAL_EXTRENGERA_ID) {
      geodata.features = geodata.features.filter((feature) => {
        return feature.id == idDistritoElectoral;
      });
    }

    geodata.features = geodata.features.map((feature) => {
      feature.properties.name = (feature.properties.name as string).toUpperCase();
      return feature;
    });

    if (geodata.features.length === 0) {
      console.warn("No se encontraron features para el mapa. idDistritoElectoral:", idDistritoElectoral, "esEstranjero:", esEstranjero, "isExtranjero:", isExtranjero);
      return;
    }

    let data = [];
    if (mapaCalor != undefined) {
      for (let i = 0; i < geodata.features.length; i++) {
        if (COUNTRYS_BLOCKED.includes(geodata.features[i].id + "")) {
          data.push({
            id: geodata.features[i].id,
            value: 0,
            polygonSettings: {
              fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
            },
          });
        } else {
          let tmp = !isExtranjero
            ? mapaCalor.find((x) => x.distritoElectoral == geodata.features[i].id)
            : mapaCalor.find((x) => x.ubigeoNivel02 == geodata.features[i].id) ||
              mapaCalor.find((x) => x.ubigeoNivel01 == geodata.features[i].id);
          settingCustomData(tmp, data, geodata, i);
        }
      }
    }

    // Recorre todos los polígonos y oculta su tooltip
    // polygonSeries.mapPolygons.each((polygon) => {
    //   let tooltip = polygon.get("tooltip");
    //   if (tooltip) {
    //     tooltip.hide();
    //   }
    // });
    // chart.series.each((series) => {
    //   if (series.get("tooltip")) {
    //     series.get("tooltip").hide();
    //   }
    // });

    polygonSeries.mapPolygons.template.events.off("pointerover");
    polygonSeries.mapPolygons.template.events.off("pointerout");
    polygonSeries.mapPolygons.template.events.off("click");
    // polygonSeries.mapPolygons.template.events.dispose();

    if (clickCountries) {
      polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
        document.body.style.cursor = "pointer";
      });


      polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
        document.body.style.cursor = "default";
      });


      polygonSeries.mapPolygons.template.events.on("click", function (ev) {
        const dataContext = ev.target.dataItem?.dataContext as any;
        if (!isNaN(dataContext?.id)) {
          thisRoot.eventClickMapa.emit(dataContext.id);
          document.body.style.cursor = "default";
        }
      });
    }

    polygonSeries.mapPolygons.template.setAll({
      tooltip: null,
    });
    // polygonSeries.mapPolygons.template.set("tooltip", null);
    // polygonSeries.tooltip?.hide(); // Oculta el tooltip si aún está visible

    setTimeout(() => {
      let tooltip = am5.Tooltip.new(rootMap, {
        autoTextColor: false,
      });

      tooltip.get("background").setAll({
        fill: am5.color(0x003874),
      });

      // polygonSeries.mapPolygons.template.set("tooltip", tooltip);

      polygonSeries.mapPolygons.template.setAll({
        tooltip: tooltip,
      });
    });

    polygonSeries.data.clear();

    polygonSeries.set('geoJSON', null);
    polygonSeries.set('geoJSON', geodata);

    // Establecer los nuevos datos
    polygonSeries.data.setAll(data);

    // polygonSeries.appear(1000);  // Redibuja la serie con animación
    // chart.appear(1000);          // Redibuja el gráfico
  });
}
