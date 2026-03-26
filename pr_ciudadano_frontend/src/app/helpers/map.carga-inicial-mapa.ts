import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

import {
  loadPolygonsWithActionsInMap,
  loadPolygonsWithoutActions,
  removeBlockPoligons,
  updatePolygonsWithActionsInMap,
} from "./map.config-congresales";
import { cargarBotones } from "./map.cargar-botones";
import {
  loadMapCustomPeruButton,
  loadMapCustomExtrangeroButton,
} from "../components/main-hot-map/map-auxiliary-elements";
import { IMapaCongresal } from "../interfaces/mapa-calor-congresal.interfaces";
import { MapaCalor } from "../interfaces/resumen-general-bean";
import { LEGEND_HEIGHT } from "../settings/map.settings";

export function actualizarDataMapa(
  chart: am5map.MapChart,
  polygonSeries: am5map.MapPolygonSeries,
  root: am5.Root,
  mapaCalor: MapaCalor[],
  thisRoot: IMapaCongresal
) {
  let { esEstranjero, idDistritoElectoral } = thisRoot.datos;

  const map = !esEstranjero
    ? `./assets/lib/amcharts5/geodata/json/peruLow-distrito-electoral.json`
    : `./assets/lib/amcharts5/geodata/json/continental_total.json`;

  const blockedMap = esEstranjero
    ? `./assets/lib/amcharts5/geodata/json/mundo.json`
    : map;

  updatePolygonsWithActionsInMap(map, chart, mapaCalor, polygonSeries, thisRoot, root);

  if(!esEstranjero && idDistritoElectoral != 22) {
    removeBlockPoligons(map, chart, root);
  } else {
    loadPolygonsWithoutActions(blockedMap, chart, root);
  }
}

export function cargarDataMapaNacional(
  chart: am5map.MapChart,
  polygonSeries: am5map.MapPolygonSeries,
  root: am5.Root,
  mapaCalor: MapaCalor[],
  thisRoot: IMapaCongresal
) {
  let { esEstranjero, idDistritoElectoral, mostrarBotonMundo } = thisRoot.datos;
  chart.set("projection", am5map.geoMercator());

  const map = !esEstranjero
    ? `./assets/lib/amcharts5/geodata/json/peruLow-distrito-electoral.json`
    : `./assets/lib/amcharts5/geodata/json/continental_total.json`;

  const blockedMap = esEstranjero
    ? `./assets/lib/amcharts5/geodata/json/mundo.json`
    : map;
      
  loadPolygonsWithActionsInMap(map, chart, mapaCalor, polygonSeries, thisRoot, root);

  if(!esEstranjero && idDistritoElectoral != 22) {
    removeBlockPoligons(map, chart, root);
  } else {
    loadPolygonsWithoutActions(blockedMap, chart, root);
  }

  cargarBotones(root, chart);

  // Si mostrarBotonMundo es undefined, se considera true por defecto para mantener compatibilidad
  const shouldShowButton = mostrarBotonMundo !== false;

  if (esEstranjero) {
    loadMapCustomPeruButton(root, chart, () => {
      thisRoot.eventClickMapa.emit(15);
      document.body.style.cursor = "default";
    }, 1, shouldShowButton);
  } else {
    loadMapCustomExtrangeroButton(root, chart, () => {
      thisRoot.eventClickMapa.emit(27);
      document.body.style.cursor = "default";
    }, 1, shouldShowButton);
  }
}

export function cargaInicialMapa(
  mapaCongresal: IMapaCongresal,
  callback: (
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: MapaCalor[]
  ) => void
) {
  let { id } = mapaCongresal.datos;
  const root = am5.Root.new(id);
  if (root._logo != undefined) {
    root._logo.dispose();
  }

  root.setThemes([am5themes_Animated.new(root)]);

  const chart = root.container.children.push(
    am5map.MapChart.new(root, {
      wheelY: "none",
      wheelX: "none",
      rotationX: -13,
      paddingBottom: LEGEND_HEIGHT,
    })
  );

  const polygonSeries = chart.series.push(am5map.MapPolygonSeries.new(root, {}));

  settingPolygonsColorAndHeatLegend(chart, polygonSeries, root, mapaCongresal);
  callback(chart, polygonSeries, root, mapaCongresal.datos.mapaCalor);

  return { root: root, chart: chart, polygonSeries: polygonSeries };
}

// export function cargarInicialMap

export function settingPolygonsColorAndHeatLegend(
  chart: am5map.MapChart,
  polygonSeries: am5map.MapPolygonSeries,
  root: am5.Root,
  thisRoot: IMapaCongresal
) {
  let { esEstranjero, oneFilter } = thisRoot.datos;
  const chartHeight = chart.height();

  if (!(esEstranjero && oneFilter)) {
    polygonSeries.mapPolygons.template.setAll({
      tooltipText: "[fontFamily: NotoSans-regular][fontSize: 14px]{name}",
      interactive: true,
      templateField: "polygonSettings",
      stroke: am5.color(0xffffff),
      strokeWidth: 0.5,
      fill: am5.color(0xdfe5eb),
    });

    polygonSeries.mapPolygons.template.states.create("hover", {
      fill: am5.color("#2A71B9"),
    });
  } else {
    polygonSeries.mapPolygons.template.setAll({
      templateField: "polygonSettings",
      stroke: am5.color(0xffffff),
      strokeWidth: 0.5,
      fill: am5.color(0xdfe5eb),
    });
  }

  let heatLegend = chart.children.push(
    am5.HeatLegend.new(root, {
      orientation: "horizontal",
      startColor: am5.color(0xdfe5eb),
      endColor: am5.color(0x295789),
      startText: "0 %",
      endText: "100 %",
      width: am5.percent(50),
      x: am5.percent(5),
      y: chartHeight - LEGEND_HEIGHT,
    })
  );

  heatLegend.startLabel.setAll({
    fontSize: 12,
    fill: heatLegend.get("endColor"),
  });

  heatLegend.endLabel.setAll({
    fontSize: 12,
    fill: heatLegend.get("endColor"),
  });

  heatLegend.children.push(
    am5.Label.new(root, {
      text: "Porcentaje de actas contabilizadas",
      fill: am5.color(0x295789),
      fontSize: 12,
      paddingLeft: 0,
      paddingTop: 0,
    })
  );

  polygonSeries.appear();
  chart.appear();
}
