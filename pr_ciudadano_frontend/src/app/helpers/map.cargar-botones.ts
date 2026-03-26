import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";

export function cargarBotones(root: am5.Root, chart: am5map.MapChart) {
  let arr: string[];
  const chartHeight = chart.height();
  const zoomControl = chart.set(
    "zoomControl",
    am5map.ZoomControl.new(root, {
      y: chartHeight,
    })
  );
  // Guardar zoomControl en el Root
  // root.set("zoomControl", zoomControl);
  // root.data.set("zoomControl", zoomControl);
  let buttons = cargarBotonesFromZoom(root, chart, zoomControl);
  return { zoomControl: zoomControl, buttons: buttons };
}

export function cargarBotonesFromZoom(root: am5.Root, chart: am5map.MapChart, zoomControl: am5map.ZoomControl) {
  const { minusButton, plusButton } = zoomControl;

  zoomControl.children["_container"].plusButton.set(
    "icon",
    am5.Picture.new(root, {
      width: 15,
      height: 15,
      layer: 40,
      src: "assets/img/icons/ico-mas.svg",
    })
  );

  zoomControl.children["_container"].minusButton.set(
    "icon",
    am5.Picture.new(root, {
      width: 15,
      height: 15,
      layer: 40,
      src: "assets/img/icons/ico-menos.svg",
    })
  );

  plusButton.set("cursorOverStyle", "pointer");
  minusButton.set("cursorOverStyle", "pointer");

  chart.set("zoomControl", zoomControl);

  zoomControl.minusButton.get("background").setAll({
    fill: am5.color(0xffffff),
    fillOpacity: 0.8,
    stroke: am5.color(0x003874),
  });
  zoomControl.plusButton.get("background").setAll({
    fill: am5.color(0xffffff),
    fillOpacity: 0.8,
    stroke: am5.color(0x003874),
  });

  zoomControl.plusButton
    .get("background")
    .states.create("hover", {})
    .setAll({
      fill: am5.color("#6DB2E2"),
      fillOpacity: 0.8,
    });
  zoomControl.minusButton
    .get("background")
    .states.create("hover", {})
    .setAll({
      fill: am5.color("#6DB2E2"),
      fillOpacity: 0.8,
    });

  zoomControl.plusButton
    .get("background")
    .states.create("down", {}) // Estado "down" en amCharts es para "pressed"
    .setAll({
      fill: am5.color("#6DB2E2"), // Azul más oscuro
      fillOpacity: 1,
    });

  zoomControl.minusButton
    .get("background")
    .states.create("down", {})
    .setAll({
      fill: am5.color("#6DB2E2"),
      fillOpacity: 1,
    });

  zoomControl.plusButton
    .get("background")
    .states.create("disabled", {})
    .setAll({
      fill: am5.color("#ffffff"), // Gris claro
      fillOpacity: 0.5,
      stroke: am5.color("#003874"), // Borde gris
    });

  zoomControl.minusButton
    .get("background")
    .states.create("disabled", {})
    .setAll({
      fill: am5.color("#ffffff"),
      fillOpacity: 0.5,
      stroke: am5.color("#003874"),
    });

  return { plusButton: plusButton, minusButton: minusButton };
}
