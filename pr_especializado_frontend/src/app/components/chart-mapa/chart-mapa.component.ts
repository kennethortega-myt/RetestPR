import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';

import { EnumAmbito, EnumIdAmbito, EnumIdDistrito, EnumIdNivelUbigeo, EnumTipoFiltro } from '../../helpers/enums';
import { getDegradatedColorFromPercentage } from '../../helpers/funciones';
import { MapaCalor } from '../../interfaces/chart-mapa.model';
import {
  MULTIPLIER_HOT_MAP,
  INITIAL_COLOR_PERCENTAGE_0,
  FINAL_COLOR_PERCENTAGE_100,
} from '../../helpers/constantes';
import { BUTTON_MAP } from './chart-mapa.constants';
import { LoadingService } from '../loading/loading.service';
import { ALL_COUNTRIES_IDS, EUROPA_ID, EUROPA_TURQUIA_ID, PERU_LKT } from '../../constants/map.constant';

interface BtnConfig {
  showMundoBtn: boolean;
  indexMundo: number | null;
  showPeruBtn: boolean;
  indexPeru: number | null;
  showInterBtn: boolean;
  indexInter: number | null;
}

interface AmbitoConfig {
  idAmbito: number;
  config: BtnConfig;
}

@Component({
  selector: 'app-chart-mapa',
  templateUrl: './chart-mapa.component.html',
})

export class ChartMapaComponent {

  private readonly loadingService = inject(LoadingService);

  @Input({ required: true }) id: string = 'chartdivMapa';
  @Input() ambitoSeleccionado: string = EnumAmbito.NACIONAL;
  @Input() etiqueta: string = 'Porcentaje de actas contabilizadas';

  @Output() bottonHome: EventEmitter<number> = new EventEmitter<number>();
  @Output() seleccionPoligonoEvent: EventEmitter<any> = new EventEmitter<any>();

  idAmbitoGeograficoSeleccionado: number = EnumIdAmbito.NACIONAL;

  private readonly defaultButtonConfigs : Record<number, AmbitoConfig> = {
    [EnumIdAmbito.TODOS]: {
      idAmbito: EnumIdAmbito.TODOS,
      config: {
        showMundoBtn: false,
        indexMundo: null,
        showPeruBtn: true,
        indexPeru: 0,
        showInterBtn: true,
        indexInter: 1,
      },
    },
    [EnumIdAmbito.NACIONAL]: {
      idAmbito: EnumIdAmbito.NACIONAL,
      config: {
        showMundoBtn: true,
        indexMundo: 1,
        showPeruBtn: false,
        indexPeru: null,
        showInterBtn: true,
        indexInter: 0,
      },
    },
    [EnumIdAmbito.EXTRANJERO]: {
      idAmbito: EnumIdAmbito.EXTRANJERO,
      config: {
        showMundoBtn: true,
        indexMundo: 1,
        showPeruBtn: true,
        indexPeru: 0,
        showInterBtn: false,
        indexInter: null,
      },
    },
  };

  constructor(){}

  async cargaChartMapa(
    id: string,
    idAmbitoSeleccionado: number,
    tipoFiltro: string,
    idDistritoElectoral: number,
    mapaCalor: MapaCalor[],
    ubigeo: string = '000000'
  ) {
    const rutaMapa: string = this.obtenerRutaMapa(idAmbitoSeleccionado, ubigeo, tipoFiltro);

    if (rutaMapa === '') {
      this.loadingService.hide()
      return;
    }

    this.destroyChart(id);
    this.addLicense();

    let root = am5.Root.new(id);

    if (root._logo != undefined) root._logo.dispose();

    root.setThemes([am5themes_Animated.new(root)]);

    let chart = root.container.children.push(
      am5map.MapChart.new(root, {
        panX: 'translateX',
        panY: 'translateY',
        rotationX: -13,
        projection: am5map.geoMercator(),
        homeZoomLevel: 0.8,
        maxZoomLevel: 2.7,
        minZoomLevel: 0.8,
        zoomStep: 1.5,
        wheelY: "zoom",
        wheelX: "none",
        pinchZoom: true,
      })
    );

    root.events.once("frameended", () => {
      chart.seriesContainer.set("y", -20);
      chart.seriesContainer._markDirtyKey("y");
    });

    let polygonSeries = chart.series.push(am5map.MapPolygonSeries.new(root, {}));

    // --- Determinar contexto de mapa ---
    const esEleccionDistrital = idDistritoElectoral !== EnumIdDistrito.NO_SELECCIONADO;
    const esMapaTodos = idAmbitoSeleccionado === EnumIdAmbito.TODOS ||
      (esEleccionDistrital && idDistritoElectoral === EnumIdDistrito.TODOS);

    const esMapaExtranjero = (
      idAmbitoSeleccionado === EnumIdAmbito.EXTRANJERO ||
      (esEleccionDistrital && idDistritoElectoral === EnumIdDistrito.EXTRANJERO)
    );

    const nivelUbigeoActual = this.obtenerNivelUbigeo(ubigeo);
    const esUbigeoZero = nivelUbigeoActual === 0;
    // Para EXTRANJERO: nivel 2 (país) y nivel 3 (estado) deben comportarse igual → sin bloqueos
    const esUbigeoDosOMas = nivelUbigeoActual >= 2;

    // Vista TODOS no-distrital: mapa mundial unificado sin bloqueos ni interacción
    const esTodos = tipoFiltro === EnumTipoFiltro.TOTAL;

    // Vista distrital EXTRANJERO: mismo color uniforme para todos los polígonos, sin interacción
    const esExtranjeroDistrital = esEleccionDistrital && idDistritoElectoral === EnumIdDistrito.EXTRANJERO;

    // --- Definir países/lugares bloqueados ---
    let bloquedCountries: string[] = [];
    if (!esTodos) {
      bloquedCountries = esMapaExtranjero ? ALL_COUNTRIES_IDS : PERU_LKT;
      if (ubigeo === EUROPA_ID) {
        bloquedCountries.push(EUROPA_TURQUIA_ID);
      }
      // Si es elección distrital y no es mapa todos → lista vacía
      // Para EXTRANJERO nivel 2+ (país o estado) → sin bloqueos
      if ((esEleccionDistrital && !esMapaTodos && !esMapaExtranjero) || (esMapaExtranjero && esUbigeoDosOMas)) {
        bloquedCountries = [];
      }
    }

    this.cargarDatosMapaCalor(
      rutaMapa,
      chart,
      mapaCalor,
      polygonSeries,
      {
        tipoFiltro,
        ubigeo,
        idAmbitoSeleccionado,
        idDistritoElectoral
      },
      bloquedCountries
    );

    if (esMapaExtranjero && esUbigeoZero) {
      let peruSeries = chart.series.push(am5map.MapPolygonSeries.new(root, {}));
      peruSeries.mapPolygons.template.setAll({
        stroke: am5.color(0xc3c3c3),
        strokeWidth:  0.5
      });
      am5.net.load('assets/mapas/amcharts5/geodata/json/peruLow.json', chart).then((m) => {
        const _geodata = am5.JSONParser.parse(m.response ?? '');
        peruSeries.set("geoJSON", _geodata);
        peruSeries.set("fill", am5.color(0xc3c3c3));
      });
    }

    this.generateMapForCountryBlocked(rutaMapa, chart, root, bloquedCountries);

    if ((esTodos || esExtranjeroDistrital) && mapaCalor.length > 0) {
      // TODOS / distrital-EXTRANJERO: color uniforme sin tooltip ni hover ni interacción
      polygonSeries.mapPolygons.template.setAll(this.generatePolygonSettingsTodos());
    } else if (!esMapaTodos || mapaCalor.length > 0 || (esEleccionDistrital && idDistritoElectoral !== EnumIdDistrito.EXTRANJERO)) {
      polygonSeries.mapPolygons.template.setAll(this.generatePolygonSettings());
      polygonSeries.mapPolygons.template.states.create('hover', {
        fill: am5.color('#2A71B9'),
      });
    }

    this.crearLeyenda(root, chart, this.etiqueta);
    this.cargarBotones(root, chart, idAmbitoSeleccionado, ubigeo, tipoFiltro, idDistritoElectoral);
    polygonSeries.mapPolygons.template.set('fill', am5.color('#6b95bf'));

    let mapHideCalled = false;
    const hideOnce = () => {
      if (mapHideCalled) return;
      mapHideCalled = true;
      setTimeout(() => this.loadingService.hide(), 525);
    };

    polygonSeries.events.on("datavalidated", () => {
      chart.goHome(0);
      hideOnce();
    });

    const listener = chart.root.events.on("frameended", () => {
      hideOnce();
      listener.dispose();
    });
    polygonSeries.appear();
    chart.appear();
  }

  private generateMapForCountryBlocked(routMap: string, chart: am5map.MapChart, root: am5.Root, countries: string[]): void {
    const series = chart.series.push(am5map.MapPolygonSeries.new(root, {}));
    series.mapPolygons.template.setAll({ stroke: am5.color('#FFFFFF'), strokeWidth: 0.5 });

    am5.net.load(routMap, chart).then((m) => {
      const _geodata = am5.JSONParser.parse(m.response ?? '');

      const newFeatures = _geodata.features.filter((feature: any) => {
        const id = feature.properties.id;
        const match = countries.includes(id);
        return match;
      });

      _geodata.features = newFeatures
      series.set("geoJSON", _geodata);
      series.set("fill", am5.color(0xc3c3c3));
    });
  }

  cargarDatosMapaCalor(
    rutaMapa: string,
    chart: am5map.MapChart,
    mapaCalor: MapaCalor[],
    polygonSeries: am5map.MapPolygonSeries,
    filtroParams : {
      tipoFiltro: string,
      ubigeo: string,
      idAmbitoSeleccionado: number,
      idDistritoElectoral?: number
    },
    bloquearPaises: string[] = []
  ): void {
    const { tipoFiltro, ubigeo, idAmbitoSeleccionado, idDistritoElectoral } = filtroParams;
    const nivelUbigeo = this.obtenerNivelUbigeo(ubigeo);

    am5.net.load(rutaMapa, chart).then((result) => {
      const geodata = am5.JSONParser.parse(result.response ?? '');

      if (bloquearPaises?.length) {
        geodata.features = geodata.features.filter(
          (f: any) => !bloquearPaises.includes(f.properties.id)
        );
      }

      const data: any[] = [];

      // TODOS no-distrital: un único resultado total → pintar todos los polígonos
      // con el mismo color, sin tooltips ni click.
      if (tipoFiltro === EnumTipoFiltro.TOTAL) {
        if (mapaCalor && mapaCalor.length > 0) {
          const totalResult = mapaCalor[0];
          const porcentaje = Math.min(
            totalResult.porcentajeActasContabilizadas * MULTIPLIER_HOT_MAP,
            100
          );
          const color = getDegradatedColorFromPercentage(porcentaje, {
            init: INITIAL_COLOR_PERCENTAGE_0,
            end: FINAL_COLOR_PERCENTAGE_100,
          });
          geodata.features.forEach((feature: any) => {
            data.push({ id: feature.id, value: 0, polygonSettings: { fill: am5.color(color) } });
          });
        }
        polygonSeries.data.setAll(data);
        polygonSeries.set('geoJSON', geodata);
        return;
      }

      // Distrital EXTRANJERO: un único color para todos los polígonos (sin interacción; Peru bloqueado queda en capa aparte)
      if (tipoFiltro === 'distrito_electoral' && idDistritoElectoral === EnumIdDistrito.EXTRANJERO) {
        const totalResult = mapaCalor?.find(x => x.distritoElectoral === EnumIdDistrito.EXTRANJERO);
        if (totalResult) {
          const porcentaje = Math.min(
            totalResult.porcentajeActasContabilizadas * MULTIPLIER_HOT_MAP,
            100
          );
          const color = getDegradatedColorFromPercentage(porcentaje, {
            init: INITIAL_COLOR_PERCENTAGE_0,
            end: FINAL_COLOR_PERCENTAGE_100,
          });
          geodata.features.forEach((feature: any) => {
            data.push({ id: feature.id, value: 0, polygonSettings: { fill: am5.color(color) } });
          });
        }
        polygonSeries.data.setAll(data);
        polygonSeries.set('geoJSON', geodata);
        return;
      }

      // Solo salir temprano si realmente no hay datos de calor que pintar.
      if (idAmbitoSeleccionado === EnumIdAmbito.TODOS && (!mapaCalor || mapaCalor.length === 0)) {
        polygonSeries.set('geoJSON', geodata);
        polygonSeries.data.setAll(data);
        this.loadingService.hide();
        return;
      }

      if (!mapaCalor || mapaCalor.length === 0) {
        polygonSeries.set('geoJSON', geodata);
        polygonSeries.set('fill', am5.color(0xdfe5eb));
        polygonSeries.set('stroke', am5.color(0xffffff));
        return;
      }

      // filtros existentes (sin tocar)
      if (nivelUbigeo === 2 && idAmbitoSeleccionado === EnumIdAmbito.EXTRANJERO) {
        geodata.features = geodata.features.filter((el: any) => String(el.properties.id) === String(ubigeo));
      }

      // Para EXTRANJERO, nivel 3 = estado dentro de un país → mostrar solo el país (mismo filtro que nivel 2)
      if (nivelUbigeo === 3 && idAmbitoSeleccionado === EnumIdAmbito.EXTRANJERO) {
        const ubigeoNivel02 = ubigeo.slice(0, -2) + '00'; // '910601' → '910600'
        geodata.features = geodata.features.filter((el: any) => String(el.properties.id) === String(ubigeoNivel02));
      }

      if (nivelUbigeo === 3 && idAmbitoSeleccionado === EnumIdAmbito.NACIONAL) {
        geodata.features = geodata.features.filter((el: any) => el.properties.ID === ubigeo);
      }

      if (
        idDistritoElectoral !== EnumIdDistrito.NO_SELECCIONADO &&
        idDistritoElectoral !== EnumIdDistrito.TODOS &&
        idAmbitoSeleccionado === EnumIdAmbito.NACIONAL
      ) {
        geodata.features = geodata.features.filter((el: any) => String(el.id) === String(idDistritoElectoral));
      }

      geodata.features.forEach((feature: any) => {
        const tmp = this.resolverMapaCalor(
          feature,
          mapaCalor,
          nivelUbigeo,
          ubigeo,
          idAmbitoSeleccionado,
          idDistritoElectoral
        );

        data.push(
          this.crearDataMapa(feature, tmp, nivelUbigeo, idDistritoElectoral, idAmbitoSeleccionado)
        );
      });

      polygonSeries.mapPolygons.template.events.on('click', (ev: any) => {
        if (
          idDistritoElectoral !== EnumIdDistrito.TODOS &&
          idDistritoElectoral !== EnumIdDistrito.NO_SELECCIONADO
        ) return;

        if (
          (idAmbitoSeleccionado === EnumIdAmbito.EXTRANJERO && nivelUbigeo === 2) ||
          (idAmbitoSeleccionado === EnumIdAmbito.NACIONAL && nivelUbigeo === 3)
        ) return;

        const id =
          ev.target.dataItem?.get('id') ??
          ev.target.dataItem?.dataContext?.ID ??
          ev.target.dataItem?.dataContext?.id;

        if (!id) return;

        const idFinal = typeof id === 'number' ? id.toString() : id;

        // Códigos ISO de 2 chars (ej: 'ZW', 'ZM') no tienen ubigeo numérico ni ruta de mapa asociada
        if (idFinal.length <= 2 && isNaN(Number(idFinal))) return;

        this.seleccionPoligonoEvent.emit(
          tipoFiltro === 'distrito_electoral'
            ? { tipoFiltro, idDistritoElectoral: idFinal }
            : { tipoFiltro, idUbigeo: idFinal }
        );
      });

      if (nivelUbigeo > 0) {
        chart.set('rotationX', 0);
      }

      polygonSeries.data.setAll(data);
      polygonSeries.set('geoJSON', geodata);
    });
  }

  private resolverMapaCalor(
    feature: any,
    mapaCalor: MapaCalor[],
    nivelUbigeo: number,
    ubigeo: string,
    ambito: number,
    idDistrito?: number
  ): MapaCalor | undefined {

    if (idDistrito !== EnumIdDistrito.NO_SELECCIONADO) {
      return mapaCalor.find(x => String(x.distritoElectoral) === String(feature.id));
    }

    if (nivelUbigeo === EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO) {
      return mapaCalor.find(
        x => x.ubigeoNivel01?.toString().padStart(6, '0') === String(feature.id).padStart(6, '0')
      );
    }

    if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_01) {
      return mapaCalor.find(
        x => x.ubigeoNivel02?.toString().padStart(6, '0') === String(feature.properties.ID).padStart(6, '0')
      );
    }

    if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_02) {
      return ambito === EnumIdAmbito.EXTRANJERO
        ? mapaCalor.find(x => Number(x.ubigeoNivel02) === Number(ubigeo))
        : mapaCalor.find(
            x => x.ubigeoNivel03?.toString().padStart(6, '0') === feature.properties.ID
          );
    }

    if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_03) {
      // EXTRANJERO: país es el máximo nivel → match por ubigeoNivel02 del país
      if (ambito === EnumIdAmbito.EXTRANJERO) {
        const ubigeoNivel02 = Number(ubigeo.slice(0, -2) + '00'); // '910601' → 910600
        return mapaCalor.find(x => Number(x.ubigeoNivel02) === ubigeoNivel02);
      }
      return mapaCalor.find(x => Number(x.ubigeoNivel03) === Number(ubigeo));
    }

    return undefined;
  }

  private crearDataMapa(
    feature: any,
    tmp: MapaCalor | undefined,
    nivelUbigeo: number,
    idDistrito?: number,
    idAmbito?: number
  ) {

    if (!tmp) {
      feature.id = String(feature.id); // normalizar number → string
      return {
        id: feature.id,
        value: 0,
        polygonSettings: {
          fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
        }
      };
    }

    const porcentaje = Math.min(
      tmp.porcentajeActasContabilizadas * MULTIPLIER_HOT_MAP,
      100
    );

    const color = getDegradatedColorFromPercentage(porcentaje, {
      init: INITIAL_COLOR_PERCENTAGE_0,
      end: FINAL_COLOR_PERCENTAGE_100,
    });

    let id = feature.id;

    if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_01) {
      // Normalizar a string padded para que feature.id y data.id sean iguales
      id = tmp.ubigeoNivel02?.toString().padStart(6, '0') ?? String(feature.id);
    } else if (
      nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_02 ||
      nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_03
    ) {
      // Para EXTRANJERO a nivel 2+ el id es el código del país (feature.id),
      // no ubigeoNivel03 que correspondería a distritos del caso NACIONAL.
      if (idAmbito !== EnumIdAmbito.EXTRANJERO) {
        id = tmp.ubigeoNivel03?.toString().padStart(6, '0') ?? String(feature.id);
      }
    }

    // Sincronizar feature.id al mismo valor string que usará data.id
    feature.id = String(id);

    return {
      id: idDistrito === EnumIdDistrito.NO_SELECCIONADO
        ? String(id).padStart(6, '0')
        : id,
      value: 0,
      polygonSettings: {
        fill: am5.color(color),
      }
    };
  }

  private generatePolygonSettings(): Partial<am5map.IMapPolygonSettings> {
    const tooltipHTML = '<p style="font-size: 12px; fontFamily: NotoSans-regular; color: white; overflow: hidden; text-transform: uppercase;">{name}</p>';

    return {
      tooltipHTML,
      interactive: true,
      templateField: 'polygonSettings',
      toggleKey: "active",
      strokeWidth: 0.5,
      cursorOverStyle: 'pointer',
    };
  }

  // Modo TODOS: colores de calor pero sin tooltip, sin hover, sin click
  private generatePolygonSettingsTodos(): Partial<am5map.IMapPolygonSettings> {
    return {
      interactive: false,
      templateField: 'polygonSettings',
      strokeWidth: 0.5,
    };
  }

  private obtenerRutaMapa(ambitoGeografico: number, ubigeo: string, tipoFiltro: string): string {
    let ruta: string = '';

    const nivelUbigeo: number = this.obtenerNivelUbigeo(ubigeo);
    const rutaBase = 'assets/mapas/amcharts5/geodata/json/';

    if (ambitoGeografico === EnumIdAmbito.NACIONAL) {
      if(tipoFiltro === 'distrito_electoral') {
        ruta = `${rutaBase}peruLow-distrito-electoral.json`;
      } else if (nivelUbigeo === EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO) {
        ruta = `${rutaBase}peruLow.json`;
      } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_01) {
        //Departamento - Continente
        ruta = `${rutaBase}departamentos/${ubigeo}.json`;
      } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_02) {
        //Provincia - País
        ruta = `${rutaBase}provincias/${ubigeo}.json`;
      } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_03) {
        //Distrito
        ruta = `${rutaBase}provincias/${ubigeo.slice(0, -2) + "00"}.json`;
      }
    } else if (nivelUbigeo === EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO) {
      ruta = `${rutaBase}continental_total.json`;
    } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_01) {
      //Departamento - Continente
      ruta = `${rutaBase}continentes/${ubigeo}.json`;
    } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_02) {
      //Provincia - País
      ruta = `${rutaBase}continentes/${ubigeo.slice(0, -4) + "0000"}.json`;
    } else if (nivelUbigeo === EnumIdNivelUbigeo.NIVEL_UBIGEO_03) {
      // EXTRANJERO: país es el máximo nivel → mismo mapa del continente
      ruta = `${rutaBase}continentes/${ubigeo.slice(0, -4) + "0000"}.json`;
    }

    return ruta;
  }

  private obtenerNivelUbigeo(ubigeo: string): number {
    if (ubigeo === null) {
      return EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO;
    }
    let retorno: number = 0;
    const parte_departamento = ubigeo.substring(2, 6);
    const parte_provincia = ubigeo.substring(4, 6);
    if (ubigeo === '000000') {
      return retorno;
    } else if (parte_departamento === '0000') {
      retorno = EnumIdNivelUbigeo.NIVEL_UBIGEO_01;
    } else if (parte_provincia === '00') {
      retorno = EnumIdNivelUbigeo.NIVEL_UBIGEO_02;
    } else if (parte_provincia !== '00') {
      retorno = EnumIdNivelUbigeo.NIVEL_UBIGEO_03;
    }
    return retorno;
  }

  private crearLeyenda(
    root: am5.Root,
    chart: am5map.MapChart,
    etiqueta: string
  ): am5.HeatLegend {
    const heatLegend = chart.children.push(
      am5.HeatLegend.new(root, this.crearEstiloLeyenda())
    );
    heatLegend.startLabel.setAll(this.crearLabelLeyendaSettings(heatLegend));

    heatLegend.endLabel.setAll(this.crearLabelLeyendaSettings(heatLegend));

    heatLegend.children.push(this.crearLabelLeyenda(root, etiqueta));
    return heatLegend;
  }

  private crearLabelLeyendaSettings(
    heatLegend: am5.HeatLegend
  ): Partial<am5.ILabelSettings> {
    return {
      fontSize: 12,
      fill: heatLegend.get('endColor'),
    };
  }

  private crearLabelLeyenda(root: am5.Root, etiqueta: string): am5.Label {
    return am5.Label.new(root, this.crearEstiloLabelLeyenda(etiqueta));
  }

  private crearEstiloLabelLeyenda(etiqueta: string): am5.ILabelSettings {
    return {
      text: etiqueta,
      fontSize: 12,
      paddingLeft: 0,
      paddingTop: 0,
      fill: am5.color(0x295789),
    };
  }

  private crearEstiloLeyenda(): am5.IHeatLegendSettings {
    return {
      orientation: 'horizontal',
      startColor: am5.color(0xdfe5eb),
      endColor: am5.color(0x295789),
      startText: '0 %',
      endText: '100 %',
      width: am5.percent(60),
      x: am5.percent(5),
      y: am5.percent(86)
    };
  }

  private cargarBotones(
    root: am5.Root,
    chart: am5map.MapChart,
    idAmbitoSeleccionado: number,
    ubigeo: string,
    tipoFiltro: string,
    idDistritoElectoral: number
  ) {
    const zoomControl = chart.set(
      'zoomControl',
      am5map.ZoomControl.new(root, {})
    );

    this.settingAllCustomButtons(
      zoomControl,
      root,
      idAmbitoSeleccionado,
      ubigeo,
      tipoFiltro,
      idDistritoElectoral
    );

    zoomControl.minusButton.setAll({
      background: am5.RoundedRectangle._new(
        root,
        this.obtenerModeloRectangulo()
      ),
      icon: am5.Graphics.new(root, this.obtenerIconoMinimizar()),
      cursorOverStyle: 'pointer',
    });
    zoomControl.plusButton.setAll({
      background: am5.RoundedRectangle._new(
        root,
        this.obtenerModeloRectangulo()
      ),
      icon: am5.Graphics.new(root, this.obtenerIconoMaximizar()),
      cursorOverStyle: 'pointer',
    });
    zoomControl.plusButton
      .get('background')!
      .states.create('hover', {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1,
      });
    zoomControl.minusButton
      .get('background')!
      .states.create('hover', {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1,
      });

    chart.set('zoomControl', zoomControl);
  }

  private settingAllCustomButtons(
    zoomControl: am5map.ZoomControl,
    root: am5.Root,
    idAmbitoSeleccionado: number,
    ubigeo: string,
    tipoFiltro: string,
    idDistritoElectoral: number
  ) {
    const iconStringPeru = BUTTON_MAP.peru;
    const iconStringInternacional = BUTTON_MAP.international;
    const iconStringMundo = BUTTON_MAP.world;

    const selectedButtonConfig = this.getButtonCongif(
      idAmbitoSeleccionado,
      ubigeo,
      idDistritoElectoral
    );

    if (selectedButtonConfig?.config.showMundoBtn && tipoFiltro !== 'distrito_electoral') {
      this.settingCustomButton(
        zoomControl,
        root,
        iconStringMundo,
        () => {
          this.idAmbitoGeograficoSeleccionado = EnumIdAmbito.TODOS;
          this.bottonHome.emit(EnumIdAmbito.TODOS);
        },
        selectedButtonConfig?.config.indexMundo ?? 0
      );
    }

    if (selectedButtonConfig?.config.showPeruBtn ) {
      this.settingCustomButton(
        zoomControl,
        root,
        iconStringPeru,
        () => {

          if(idDistritoElectoral !== EnumIdDistrito.NO_SELECCIONADO &&
            idDistritoElectoral !== EnumIdDistrito.TODOS &&
            idAmbitoSeleccionado !== EnumIdDistrito.EXTRANJERO
          ) {
            this.seleccionPoligonoEvent.emit({
              tipoFiltro: tipoFiltro,
              idDistritoElectoral: EnumIdDistrito.TODOS
            });
          } else {
            this.idAmbitoGeograficoSeleccionado = EnumIdAmbito.NACIONAL;
            this.bottonHome.emit(EnumIdAmbito.NACIONAL);
          }
        },
        selectedButtonConfig?.config.indexPeru ?? 0
      );
    }

    if (selectedButtonConfig?.config.showInterBtn) {
      this.settingCustomButton(
        zoomControl,
        root,
        iconStringInternacional,
        () => {
          if(idDistritoElectoral === EnumIdDistrito.TODOS) {
            this.seleccionPoligonoEvent.emit({
              tipoFiltro: tipoFiltro,
              idDistritoElectoral: EnumIdDistrito.EXTRANJERO
            });
          } else {
            this.idAmbitoGeograficoSeleccionado = EnumIdAmbito.EXTRANJERO;
            this.bottonHome.emit(EnumIdAmbito.EXTRANJERO);
          }
        },
        selectedButtonConfig?.config.indexInter ?? 0
      );
    }
  }

  private getButtonCongif(
    idAmbitoSeleccionado: number,
    ubigeo: string,
    idDistritoElectoral: number
  ) {
    const nivelUbigeo = this.obtenerNivelUbigeo(ubigeo);

    // --- 1) CASO: Ubigeo con nivel válido ---
    if (nivelUbigeo !== EnumIdNivelUbigeo.SIN_NIVEL_UBIGEO) {
      return idAmbitoSeleccionado === EnumIdAmbito.NACIONAL
        ? this.buildConfig(EnumIdAmbito.NACIONAL)
        : this.buildConfig(EnumIdAmbito.EXTRANJERO);
    }

    // --- 2) CASO: Distrito Electoral seleccionado (no TODOS) ---
    if (
      idDistritoElectoral !== EnumIdDistrito.NO_SELECCIONADO &&
      idDistritoElectoral !== EnumIdDistrito.TODOS &&
      idAmbitoSeleccionado !== EnumIdAmbito.EXTRANJERO
    ) {
      return this.buildConfig(EnumIdAmbito.NACIONAL);
    }

    // --- 3) CASO: Distrito Electoral seleccionado (EXTRANJERO) ---
    if (idDistritoElectoral === EnumIdDistrito.TODOS) {
      return this.buildConfig(EnumIdAmbito.EXTRANJERO);
    }

    // --- 4) CONFIGS GENÉRICAS SEGÚN ÁMBITO (por defecto) ---
    const selectedButtonConfig = this.defaultButtonConfigs[idAmbitoSeleccionado];
    return selectedButtonConfig ?? null;
  }

  private buildConfig(idAmbito: number): AmbitoConfig {
    const configs : Record<number, BtnConfig> = {
      [EnumIdAmbito.NACIONAL]: {
        showMundoBtn: false,
        indexMundo: null,
        showPeruBtn: true,
        indexPeru: 0,
        showInterBtn: false,
        indexInter: null,
      },
      [EnumIdAmbito.EXTRANJERO]: {
        showMundoBtn: false,
        indexMundo: null,
        showPeruBtn: false,
        indexPeru: null,
        showInterBtn: true,
        indexInter: 0,
      },
    };

    return {
      idAmbito,
      config: configs[idAmbito],
    };
  }

  // solo se necesita usar este método para consigurar un nuevo botón y asignarle correctamente su posición
  private settingCustomButton(
    zoomControl: am5map.ZoomControl,
    root: am5.Root,
    iconString: string,
    callback: () => void,
    index: number = 0
  ) {
    const worldButton = zoomControl.children.moveValue(
      am5.Button.new(root, {
        background: am5.RoundedRectangle._new(
          root,
          this.obtenerModeloRectangulo()
        ),
        width: 36,
        height: 36,
        icon: am5.Graphics.new(root, this.getGraphicsSettingsIcon(iconString)),
        cursorOverStyle: 'pointer',
      }),
      index
    );
    worldButton
      .get('background')!
      .states.create('hover', {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1,
      });
    worldButton.events.off('click');
    worldButton.events.on('click', callback);
  }

  private getGraphicsSettingsIcon(
    iconSeleccionado: string
  ): am5.IGraphicsSettings {
    return {
      svgPath: iconSeleccionado,
      fill: am5.color(0x003874),
      scale: 1,
      centerY: am5.p50,
      y: am5.percent(50),
      centerX: am5.p50,
      x: am5.percent(50),
    };
  }

  private obtenerIconoMaximizar(): am5.IGraphicsSettings {
    return {
      fill: am5.color(0x003874),
      scale: 0.13,
      centerY: am5.percent(50),
      y: am5.percent(50),
      centerX: am5.percent(50),
      x: am5.percent(50),
      svgPath: BUTTON_MAP.zoomIn,
    };
  }

  private obtenerIconoMinimizar(): am5.IGraphicsSettings {
    return {
      svgPath: BUTTON_MAP.zoomOut,
      scale: 0.13,
      centerY: am5.p50,
      y: am5.percent(50),
      centerX: am5.p50,
      x: am5.percent(50),
      fill: am5.color(0x003874),
    };
  }

  private obtenerModeloRectangulo(): am5.IRoundedRectangleSettings {
    return {
      fill: am5.color(0xffffff),
      cornerRadiusBL: 8,
      cornerRadiusBR: 8,
      cornerRadiusTL: 8,
      cornerRadiusTR: 8,
      stroke: am5.color(0x003874),
    };
  }

  private addLicense() {
    if (am5.registry.licenses.length > 0) {
      am5.addLicense('AM5C357384425');
    }
  }

  destroyChart(nombreDiv: string) {
    document.body.style.cursor = 'default';
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
