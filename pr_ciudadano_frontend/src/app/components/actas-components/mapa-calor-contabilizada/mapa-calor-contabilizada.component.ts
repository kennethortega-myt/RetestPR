import { Component, EventEmitter, Input, Output } from "@angular/core";
import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import { FeatureCollection, Geometry } from "geojson";

import { loadSinglePolygonSeriesFromMapAndChart } from "../../../components/main-hot-map/loadPolygonSeriesFromMapAndChart";
import { getDegradatedColorFromPercentage } from "../../../components/main-hot-map/map-auxiliary-elements";
import { EnumAmbito } from "../../../helpers/estado-enum";
import { cargaInicialMapa } from "../../../helpers/map.carga-inicial-mapa";
import { cargarBotones } from "../../../helpers/map.cargar-botones";
import { IPolygonSerieData } from "../../../interfaces/hot-map.interfaces";
import { MapaCalor } from "../../../interfaces/resumen-general-bean";
import { MULTIPLIER_HOT_MAP, MULTIPLIER_HOT_MAP_REVOCA } from "../../../settings/map.settings";
import { isRevocatoria } from "../../../helpers/storage-helpers/encrypt-storage.helper";

const INITIAL_COLOR_PERCENTAGE_0 = "#DFE5EB";
const FINAL_COLOR_PERCENTAGE_100 = "#295789";

@Component({
  selector: "app-mapa-calor-contabilizada",
  templateUrl: "./mapa-calor-contabilizada.component.html",
  standalone: false,
})
export class MapaCalorContabilizadaComponent {
  @Input() idMapa: string = "chartdiv";
  @Input() mapaCalor: [MapaCalor];
  @Input() etiqueta: string = "Porcentaje de actas contabilizadas";
  @Input() AmbitoSeleccionado: string;
  @Output() bottonHome: EventEmitter<any> = new EventEmitter<any>();

  @Output() cambioUbigeoEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() distritoSeleccionadoEvent: EventEmitter<any> = new EventEmitter<any>();

  public AmbitoEnum = EnumAmbito;

  cargaInicialMapa(
    mapaCalor: MapaCalor[],
    nombreDiv: string,
    nacional: boolean = true,
    tipoNivelAmbito: number = 0,
    ubigeo: string = ""
  ) {
    this.destroyChart(nombreDiv);
    this.addLicense();
    const { root, chart, polygonSeries } = cargaInicialMapa(
      {
        datos: {
          id: nombreDiv,
          mapaCalor: mapaCalor,
          esEstranjero: !nacional,
        },
      },
      () => {}
    );
    this.AmbitoSeleccionado = nacional ? this.AmbitoEnum.NACIONAL : this.AmbitoEnum.EXTRANJERO;
    if (nacional) {
      this.cargarDataMapaNacional(chart, polygonSeries, root, mapaCalor, tipoNivelAmbito, ubigeo);
    } else if (!nacional) {
      this.cargarDataMapaExtranjero(chart, polygonSeries, root, mapaCalor, tipoNivelAmbito, ubigeo);
    }
  }

  private addLicense() {
    if (am5.registry.licenses.length > 0) {
      am5.addLicense("AM5C357384425");
    }
  }

  private cargarDataMapaNacional(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: MapaCalor[],
    tipoNivelAmbito: number = 0,
    ubigeo: string = ""
  ) {
    console.log("CAMBIANDO MAPA NACIONAL");
    let thisTmp: any = this;
    chart.set("projection", am5map.geoMercator());
    let map = `./assets/lib/amcharts5/geodata/json/peruLow.json`;
    if (tipoNivelAmbito == 1) {
      map = `./assets/lib/amcharts5/geodata/json/departamentos/${ubigeo}.json`;
    } else if (tipoNivelAmbito == 2) {
      map = `./assets/lib/amcharts5/geodata/json/provincias/${ubigeo}.json`;
    } else if (tipoNivelAmbito == 3) {
      let dUbigeo = ubigeo.slice(0, -2) + "00";
      map = `./assets/lib/amcharts5/geodata/json/provincias/${dUbigeo}.json`;
      this.distritoSeleccionadoEvent.emit(true);
    }

    am5.net.load(map, chart).then((result) => {
      const geodata = this.processGeodata(result, tipoNivelAmbito, ubigeo);
      const data = this.processData(geodata, mapaCalor, tipoNivelAmbito);
      this.setupPolygonSeries(polygonSeries, geodata, data, tipoNivelAmbito, root);
    });

    this.cargarBotones(root, chart, tipoNivelAmbito);
  }

  private cargarValorPorDefecto(data: IPolygonSerieData[], geodata: any, i: number) {
    data.push({
      id: geodata.features[i].id,
      value: 0,
      polygonSettings: {
        fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
      },
    });
  }

  private obtenerDatoMapaCalor(tipoNivelAmbito: number, tmp: any, mapaCalor: MapaCalor[], geodata: any, i: number) {
    if (tipoNivelAmbito == 0) {
      tmp = mapaCalor.find((x) => x.ubigeoNivel01.toString().padStart(6, "0") == geodata.features[i].id);
    } else if (tipoNivelAmbito == 1) {
      tmp = mapaCalor.find((x) => x.ubigeoNivel02.toString().padStart(6, "0") == geodata.features[i].properties.ID);
    } else if (tipoNivelAmbito == 2) {
      tmp = mapaCalor.find((x) => x.ubigeoNivel03.toString().padStart(6, "0") == geodata.features[i].properties.ID);
    } else if (tipoNivelAmbito == 3) {
      tmp = mapaCalor.find((x) => x.ubigeoNivel03.toString().padStart(6, "0") == geodata.features[i].properties.ID);
    }
    return tmp;
  }

  private obtenerIdMapa(tipoNivelAmbito: number, id: string, geodata: any, i: number, tmp: any): any {
    if (tipoNivelAmbito == 0) {
      id = geodata.features[i].id;
    } else if (tipoNivelAmbito == 1) {
      id = tmp.ubigeoNivel02;
    } else if (tipoNivelAmbito == 2) {
      id = tmp.ubigeoNivel03;
    } else if (tipoNivelAmbito == 3) {
      id = tmp.ubigeoNivel03;
    }
    return id;
  }

  private cargarBotones(root: am5.Root, chart: am5map.MapChart, tipoNivelAmbito: number) {
    const { zoomControl } = cargarBotones(root, chart);

    const iconStringWorld =
      "M7.629,15.258a7.428,7.428,0,0,1-2.975-.6A7.617,7.617,0,0,1,.6,10.6,7.428,7.428,0,0,1,0,7.629,7.428,7.428,0,0,1,.6,4.654,7.617,7.617,0,0,1,4.654.6,7.428,7.428,0,0,1,7.629,0,7.428,7.428,0,0,1,10.6.6a7.617,7.617,0,0,1,4.053,4.053,7.428,7.428,0,0,1,.6,2.975,7.428,7.428,0,0,1-.6,2.975A7.617,7.617,0,0,1,10.6,14.657a7.428,7.428,0,0,1-2.975.6m-.82-1.163V12.531a1.471,1.471,0,0,1-1.125-.5,1.66,1.66,0,0,1-.458-1.163v-.839l-3.91-3.91a5.363,5.363,0,0,0-.134.753,7.341,7.341,0,0,0-.038.753,6.384,6.384,0,0,0,1.612,4.33,6.15,6.15,0,0,0,4.053,2.136m5.607-2.06a6.306,6.306,0,0,0,.734-.973,6.791,6.791,0,0,0,.534-1.078,6.107,6.107,0,0,0,.324-1.154,6.875,6.875,0,0,0,.1-1.2,6.539,6.539,0,0,0-4.063-6.084v.343a1.66,1.66,0,0,1-.458,1.163,1.471,1.471,0,0,1-1.125.5H6.809V5.207a.659.659,0,0,1-.257.534.9.9,0,0,1-.582.21H4.387V7.629H9.308a.669.669,0,0,1,.534.248.859.859,0,0,1,.21.572v2.422h.82a1.545,1.545,0,0,1,.973.324,1.615,1.615,0,0,1,.572.839";
    const iconStringPeru =
      "M18.1,23.9c-1-.4-1.4-1.5-2.4-1.9-1.9-.9-4-1.7-5.1-3.6,0-.1-.1-.3,0-.4.2-.6-.6-1.2-.8-1.8-.8-1-1.4-2.1-1.9-3.3-1-1.6-1.6-3.9-3.5-4.6-.2,0-.5-.1-.5-.5.5-.3.5-.4,0-.8,0,0,0-.2,0-.2.1-.4-.6-.9,0-1.3,1.1-1.5,1.8-.6.9-.2,0,.2,0,.4,0,.6,1.3-.6,1.5,1.5,2.1.3.5-.4.4-1.1.6-1.6,0,0,.2.1.3,0,.6-1.2,2.3-.9,3-2.1.4-.3.4-.9.7-1.2.2-.3-.2-.8-.4-1.1.7-.2,1.4.2,1.7.9.2.6,1,.5,1.1,1.1,0,0,.1.2.2.2.7.2-.3,1.1,1.3.8.5,0,.7-.5,1.2-.2.3.1.7-.1,1,0,.2.2.8.3.7.6-.1.3-1.2,1.6-.3,1.2.2.2.9,1,0,.6-.4-.3-.7.1-1,.2-.9,0-2.2.5-2.5,1.4,0,.4-.4.6-.2.9,0,.2,0,.3-.1.4,0,0,0,0,0,0-.6.1-.5.8-.8,1.2.3.3.3.7.6,1.1.3.4,1,.8.4,1.4.6,0,1.2,0,1.3.7.7.2,1.5,0,1.9-.6.4-.1,0,1.8.2,1.7.4.6,1-.4,1.4.2.4,1,1.5,1.9.7,2.8-.2.4-.2.9,0,1.3-.1.4-.9.8-.5,1.2-.6,2.6,1.6.5-.4,2.6-.3.1,0,.3,0,.6-.7.4,0,1-1,1";

    let iconSeleccionado = "";

    if (this.AmbitoEnum.NACIONAL == this.AmbitoSeleccionado) {
      iconSeleccionado = iconStringWorld;
      if (tipoNivelAmbito > 0) {
        iconSeleccionado = iconStringPeru;
      }
    } else if (this.AmbitoEnum.EXTRANJERO == this.AmbitoSeleccionado) {
      iconSeleccionado = iconStringPeru;
      if (tipoNivelAmbito > 0) {
        iconSeleccionado = iconStringWorld;
      }
    }

    const homeIcondButton = zoomControl.children.moveValue(
      am5.Button.new(root, {
        paddingTop: iconSeleccionado != iconStringPeru ? 10 : 8,
        paddingBottom: iconSeleccionado != iconStringPeru ? 10 : 8,
        // fill: am5.color(0xffffff),
        // color: am5.color(0xffffff),
        width: 36,
        height: 36,
        icon: am5.Graphics.new(root, {
          svgPath: iconSeleccionado,
          fill: am5.color(0x003874),
          scale: iconSeleccionado != iconStringPeru ? 1.125 : 0.825,
        }),
      }),
      0
    );

    homeIcondButton.get("background").setAll({
      fill: am5.color(0xffffff),
      fillOpacity: 0.8,
      stroke: am5.color(0x003874),
    });
    homeIcondButton
      .get("background")
      .states.create("hover", {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1,
      });
    homeIcondButton
      .get('background')
      .states.create('down', {})
      .setAll({
        fill: am5.color('#6DB2E2'),
        fillOpacity: 1
      });

    homeIcondButton.set("cursorOverStyle", "pointer");
    homeIcondButton.events.on("click", () => {
      if (this.AmbitoEnum.NACIONAL == this.AmbitoSeleccionado) {
        if (tipoNivelAmbito > 0) {
          this.bottonHome.emit(1);
        } else {
          this.bottonHome.emit(2);
        }
      } else if (this.AmbitoEnum.EXTRANJERO == this.AmbitoSeleccionado) {
        if (tipoNivelAmbito > 0) {
          this.bottonHome.emit(2);
        } else {
          this.bottonHome.emit(1);
        }
      }
      document.body.style.cursor = "default";
    });
  }

  private cargarDataMapaExtranjero(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: MapaCalor[],
    tipoNivelAmbito: number = 0,
    ubigeo: string = ""
  ) { 
    let thisTmp: any = this;
    chart.set("projection", am5map.geoMercator());
    let map = `./assets/lib/amcharts5/geodata/json/continental_total.json`;
    if (tipoNivelAmbito == 1) {
      map = `./assets/lib/amcharts5/geodata/json/continentes/${ubigeo}.json`;
    } else if (tipoNivelAmbito == 2) {
      let pUbigeo = ubigeo.toString().slice(0, -4) + "0000";
      map = `./assets/lib/amcharts5/geodata/json/continentes/${pUbigeo}.json`;
    }

    am5.net.load(map, chart).then(function (result) {
      const reemplazos: Record<string, string> = {
        "920100": "AW",      // antillas holandesas -> aruba
        "921200": "920511"   // el salvador
      };

      ubigeo = reemplazos[ubigeo] ?? ubigeo;

      let geodata: FeatureCollection<Geometry, any> = am5.JSONParser.parse(result.response);
      
      if (tipoNivelAmbito == 2) {
        const country = geodata.features.find((element) => element.id == ubigeo);
        geodata.features = country ? [country] : [];
      }

      geodata.features.forEach((el) => (el.properties.name = el.properties.name.toUpperCase()));

      let data: IPolygonSerieData[] = [];
      thisTmp.cargarDatosExtranjeros(mapaCalor, geodata, tipoNivelAmbito, thisTmp, data);

      let tooltip = am5.Tooltip.new(root, {
        autoTextColor: false,
      });

      tooltip.get("background").setAll({
        fill: am5.color(0x003874),
      });

      polygonSeries.mapPolygons.template.setAll({
        tooltip: tooltip,
      });

      polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
        document.body.style.cursor = "pointer";
      });
      
      polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
        document.body.style.cursor = "default";
      });

      polygonSeries.mapPolygons.template.events.on("click", (ev) => {
        const currentId = (ev.target.dataItem as any).get("id");
        const clickedCountry = currentId == undefined ? (ev.target.dataItem.dataContext as any).ID : currentId;
        thisTmp.cambioUbigeoEvent.emit(clickedCountry);
        document.body.style.cursor = "default";
      });

      if (tipoNivelAmbito == 1) {
        let currentGeodata = { type: "FeatureCollection", features: [] } as {
          type: string;
          features: any[];
        };
        let currentGeodataDisabled = {
          type: "FeatureCollection",
          features: [],
        } as { type: string; features: any[] };
        let idxEnabled: number[] = [];
        mapaCalor.forEach((map) => {
          geodata.features.forEach((feature, index) => {
            if (feature.id == Number(map.ubigeoNivel02)) {
              currentGeodata.features.push(feature);
              idxEnabled.push(index);
            }
          });
        });
        geodata.features.forEach((feature, index) => {
          if (!idxEnabled.includes(index)) {
            currentGeodataDisabled.features.push(feature);
          }
        });
        loadSinglePolygonSeriesFromMapAndChart(currentGeodataDisabled, chart, root, () => {
          polygonSeries.set("geoJSON", currentGeodata as any);
        });
      } else {
        polygonSeries.set("geoJSON", geodata);
      }

      polygonSeries.data.setAll(data);
    });

    this.cargarBotones(root, chart, tipoNivelAmbito);
  }

  private cargarDatosExtranjeros(
    mapaCalor: MapaCalor[],
    geodata: any,
    tipoNivelAmbito: number,
    thisTmp: any,
    data: any[]
  ) {
    if (mapaCalor != undefined) {
      for (let i = 0; i < geodata.features.length; i++) {
        let tmp = undefined;
        if (tipoNivelAmbito == 0) {
          tmp = mapaCalor.find((x) => x.ubigeoNivel01.toString().padStart(6, "0") == geodata.features[i].id);
        } else if (tipoNivelAmbito == 1) {
          tmp = mapaCalor.find((x) => x.ubigeoNivel02.toString().padStart(6, "0") == geodata.features[i].id);
        } else if (tipoNivelAmbito == 2) {
          tmp = mapaCalor.find((x) => x.ubigeoNivel02.toString().padStart(6, "0") == geodata.features[i].id);
        }

        thisTmp.cargarDatosMapa(tmp, tipoNivelAmbito, geodata, i, data);
      }
    }
  }

  private cargarDatosMapa(tmp: any, tipoNivelAmbito: number, geodata: any, i: number, data: IPolygonSerieData[]) {
    if (tmp) {
      const MULTIPLIER =  isRevocatoria() ? MULTIPLIER_HOT_MAP_REVOCA : MULTIPLIER_HOT_MAP;
      const porcentajeCalculado = Number(tmp.porcentajeActasContabilizadas) * MULTIPLIER;
      const currentPercentage = porcentajeCalculado > 100.0 ? 100.0 : porcentajeCalculado;
      const currentColor = getDegradatedColorFromPercentage(currentPercentage, {
        init: INITIAL_COLOR_PERCENTAGE_0,
        end: FINAL_COLOR_PERCENTAGE_100,
      });

      let id = "";
      if (tipoNivelAmbito == 0) {
        id = geodata.features[i].id;
      } else if (tipoNivelAmbito == 1) {
        id = tmp.ubigeoNivel02;
      } else if (tipoNivelAmbito == 2) {
        id = tmp.ubigeoNivel02;
      }
      geodata.features[i].id = id;
      data.push({
        id: id,
        value: 0,
        polygonSettings: {
          fill: am5.color(currentColor),
        },
      });
    } else {
      this.cargarValorPorDefecto(data, geodata, i);
    }
  }

  private processGeodata(result: any, tipoNivelAmbito: number, ubigeo: string): FeatureCollection<Geometry, any> {
    let geodata: FeatureCollection<Geometry, any> = am5.JSONParser.parse(result.response);

    if (tipoNivelAmbito == 3) {
      const district = geodata.features.find((element) => element.properties.ID == ubigeo);
      geodata.features = [district];
    }

    geodata.features = geodata.features.map((feature) => {
      feature.properties.name = (feature.properties.name as string).toUpperCase();
      return feature;
    });

    return geodata;
  }

  private processData(geodata: FeatureCollection<Geometry, any>, mapaCalor: MapaCalor[], tipoNivelAmbito: number): any[] {
    let data = [];
    if (mapaCalor != undefined) {
      for (let i = 0; i < geodata.features.length; i++) {
        let tmp = this.obtenerDatoMapaCalor(tipoNivelAmbito, undefined, mapaCalor, geodata, i);

        if (tmp) {
          const MULTIPLIER = isRevocatoria() ? MULTIPLIER_HOT_MAP_REVOCA : MULTIPLIER_HOT_MAP;
          const porcentajeCalculado = Number(tmp.porcentajeActasContabilizadas) * MULTIPLIER;
          const currentPercentage = porcentajeCalculado > 100.0 ? 100.0 : porcentajeCalculado;
          const currentColor = getDegradatedColorFromPercentage(currentPercentage, {
            init: INITIAL_COLOR_PERCENTAGE_0,
            end: FINAL_COLOR_PERCENTAGE_100,
          });

          let id = this.obtenerIdMapa(tipoNivelAmbito, "", geodata, i, tmp);
          geodata.features[i].id = id;
          geodata.features.forEach((el) => (el.properties.name = el.properties.name.toUpperCase()));
          data.push({
            id: id.toString().padStart(6, "0"),
            value: 0,
            polygonSettings: {
              fill: am5.color(currentColor),
            },
          });
        } else {
          this.cargarValorPorDefecto(data, geodata, i);
        }
      }
    }
    return data;
  }

  private setupPolygonSeries(polygonSeries: am5map.MapPolygonSeries, geodata: FeatureCollection<Geometry, any>, data: any[], tipoNivelAmbito: number, root: am5.Root) {
    polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
      document.body.style.cursor = "pointer";
    });
    
    polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
      document.body.style.cursor = "default";
    });

    polygonSeries.mapPolygons.template.events.on("click", (ev) => {
      if(tipoNivelAmbito == 3) { return; }

      const id =
        ev.target.dataItem.get == undefined
          ? (ev.target.dataItem.dataContext as any).ID
          : (ev.target.dataItem as any).get("id");
      this.cambioUbigeoEvent.emit(id);
      document.body.style.cursor = "default";
    });

    let tooltip = am5.Tooltip.new(root, {
      autoTextColor: false,
    });

    tooltip.get("background").setAll({
      fill: am5.color(0x003874),
    });

    polygonSeries.mapPolygons.template.setAll({
      tooltip: tooltip,
    });

    polygonSeries.set("geoJSON", geodata);
    polygonSeries.data.setAll(data);
  }

  private destroyChart(nombreDiv: string) {
    am5.array.each(am5.registry.rootElements, function (root) {
      if (root != undefined) {
        if (root.dom != undefined) {
          if (root.dom.id == nombreDiv) {
            root.dispose();
          }
        }
      }
    });
  }
}
