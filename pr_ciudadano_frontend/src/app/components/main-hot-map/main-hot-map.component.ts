import { AfterViewInit, Component, ElementRef, EventEmitter, Injector, Input, OnDestroy, OnInit, Output, SimpleChanges, OnChanges, ViewChild } from "@angular/core";
import { from, take } from "rxjs";
import { HotMapService } from "../../services/elecciones-generales/hot-map.service";

import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { MapsUrl } from "./maps.url";
import {
  HEAT_LEGEND_TEXTS,
  HeatLegendTextType,
  TooltipType,
  getDegradatedColorFromPercentage,
  loadMapCustomExtrangeroButton,
  loadMapCustomHeatLegend,
  loadMapCustomHomeButton,
  loadMapCustomPeruButton,
  loadMapCustomWorldButton,
  setCustomButtonsForZoom,
} from "./map-auxiliary-elements";
import { IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import {
  RegionValue,
  REGION_EXTRAJERO,
  REGION_PERU,
  getOptimizedObject,
  REGION_TODOS,
  FilterByLocationParams,
} from "../../interfaces/filtro-settings";
import {
  loadPolygonSeriesFromMapAndChart,
  loadPolygonSeriesFromMapAndChartLockPeru,
  loadPolygonSeriesFromMapAndChartForCountry,
  loadPolygonSeriesFromMapAndChartForDistrict,
  loadSinglePolygonSeriesFromMapAndChart,
} from "./loadPolygonSeriesFromMapAndChart";
import { commonLoadHeatSubMapPoligons } from "./commonLoadHeatSubMapPoligonsForContinent";
import { AppInjector } from "../../helpers/app-injector";
import { LEGEND_HEIGHT } from "../../settings/map.settings";
import { PoliticalOrganizationItem } from "../../interfaces/presidenciales.interfaces";
import { MAP_ZOOM } from "../../helpers/constantes";

// Imports para soporte de mapas congresales/contabilizadas/revocatorias
import {
  cargaInicialMapa,
  cargarDataMapaNacional,
  actualizarDataMapa,
} from "../../helpers/map.carga-inicial-mapa";
import { cargarBotones } from "../../helpers/map.cargar-botones";
import { destroyChart } from "../../helpers/map.destroy-chart";
import { loadPolygonsWithoutActions } from "../../helpers/map.config-congresales";
import { COUNTRY_PERU_IDS } from "./maps.constants";
import { MapaCalor } from "../../interfaces/resumen-general-bean";

const INITIAL_COLOR_PERCENTAGE_0 = "#DFE5EB";
const FINAL_COLOR_PERCENTAGE_100 = "#295789";

const DELAY_TO_LOAD_MAPS = 100;
const BUTTON_SEQUENCE_SCROLL_LOCK_MS = 1600;

@Component({
  selector: "app-main-hot-map",
  templateUrl: "./main-hot-map.component.html",
  styleUrls: ["./main-hot-map.component.scss"],
  standalone: false,
})
export class MainHotMapComponent implements OnInit, AfterViewInit, OnDestroy, OnChanges {
  public rootMap: am5.Root = null;

  public selectedUbigeoParams: FilterByLocationParams;

  public selectedCountryId: string;

  public hotMapWorldData: IHotMapUbigeoItem[] = [];

  public hotMapDepartments: IHotMapUbigeoItem[] = [];
  public hotMapProvinces: IHotMapUbigeoItem[] = [];
  public hotMapDistricts: IHotMapUbigeoItem[] = [];

  public hotMapContinents: IHotMapUbigeoItem[] = [];
  public hotMapCountries: IHotMapUbigeoItem[] = [];
  public hotMapStates: IHotMapUbigeoItem[] = [];
  @ViewChild('mapContentRef', { static: true }) mapContentRef!: ElementRef<HTMLDivElement>;

  @Input() electionId: number;
  @Input() isParticipaciónCiudadana = false;
  @Input() clickMap = true;
  @Input({ required: false }) changeZoomLevelsToThree?: boolean = false

  private codigoAgrupacionPolitica: string;
  private selectedCandidate?: PoliticalOrganizationItem;

  private updateSelectedCandidate(candidato?: PoliticalOrganizationItem, codigoAgrupacionPolitica?: string): void {
    if (candidato) {
      this.selectedCandidate = candidato;
      return;
    }

    if (!codigoAgrupacionPolitica) {
      this.selectedCandidate = undefined;
    }
  }

  @Input() tooltipType: TooltipType = "default";
  @Input() heatLegendTextType: HeatLegendTextType = "type_1";
  @Input() showIconTodos = true;
  @Input() showIconExtranjero = true;

  // ============================================================================
  // NUEVAS CONFIGURACIONES PARA UNIFICAR COMPONENTES
  // ============================================================================
  
  /**
   * Mostrar estado de carga mientras el mapa se inicializa
   * Útil para mejorar la UX cuando el mapa tarda en cargar
   * @default false
   */
  @Input() showLoadingState = false;

  /**
   * Texto personalizado para el estado de carga
   * @default "Cargando mapa..."
   */
  @Input() loadingText = "Cargando mapa...";

  /**
   * Optimizar re-renders cuando solo cambian los datos pero no la estructura del mapa
   * Útil para MapaCalorCongresalComponent y MapaCalorSenadoresDistritoElectoralMultipleComponent
   * @default false
   */
  @Input() optimizeDataUpdates = false;

  /**
   * Etiqueta personalizada para la leyenda del mapa
   * Útil para MapaCalorContabilizadaComponent
   * @default undefined (usa etiqueta por defecto según heatLegendTextType)
   */
  @Input() customLegendLabel?: string;

  /**
   * Ocultar botones de navegación de región (Perú/Extranjero/Todos)
   * Útil para ActasSharedMapaCalorComponent que solo trabaja con Perú
   * @default false
   */
  @Input() hideRegionButtons = false;

  /**
   * Altura del contenedor del mapa en píxeles
   * @default 460
   */
  @Input() mapHeight = 460;

  /**
   * ID personalizado para el contenedor del mapa
   * Útil cuando hay múltiples mapas en la misma vista
   * @default "chartdiv"
   */
  @Input() mapContainerId = "chartdiv";

  /**
   * Deshabilitar la destrucción automática del mapa en ngOnDestroy
   * Útil para casos donde el ciclo de vida del mapa se gestiona externamente
   * @default false
   */
  @Input() disableAutoDestroy = false;

  // ============================================================================
  // OUTPUTS EXISTENTES
  // ============================================================================

  @Output() regionChanged = new EventEmitter<RegionValue>();
  @Output() ubigeoParamsChanged = new EventEmitter<FilterByLocationParams>();

  // ============================================================================
  // NUEVOS OUTPUTS PARA COMPATIBILIDAD
  // ============================================================================

  /**
   * Evento emitido cuando se hace click en el botón "Home" del mapa
   * Usado en MapaCalorContabilizadaComponent
   */
  @Output() bottonHome = new EventEmitter<any>();

  /**
   * Evento emitido cuando cambia el ubigeo seleccionado
   * Usado en MapaCalorContabilizadaComponent y ActasSharedMapaCalorComponent
   */
  @Output() cambioUbigeoEvent = new EventEmitter<string>();

  /**
   * Evento emitido cuando se selecciona un distrito
   * Usado en MapaCalorContabilizadaComponent
   */
  @Output() distritoSeleccionadoEvent = new EventEmitter<boolean>();

  // ============================================================================
  // TIPO DE MAPA (UNIFICACIÓN)
  // ============================================================================

  /**
   * Tipo de mapa a renderizar
   * - 'presidenciales': Mapa estándar para elecciones presidenciales (ubigeo hierarchy)
   * - 'congresales': Mapa de distritos electorales para diputados/senadores (27 distritos)
   * - 'contabilizadas': Mapa de porcentaje de actas contabilizadas
   * - 'revocatorias': Mapa para procesos de revocatoria con niveles dinámicos
   * @default 'presidenciales'
   */
  @Input() mapType: 'presidenciales' | 'congresales' | 'contabilizadas' | 'revocatorias' = 'presidenciales';

  /**
   * Datos del mapa de calor para tipos congresales/contabilizadas/revocatorias
   * Compatible con MapaCalor[] de los componentes legacy
   */
  @Input() mapaCalor?: any[];

  /**
   * ID del distrito electoral seleccionado
   * Usado en mapType='congresales'
   */
  @Input() idDistritoElectoral?: number;

  /**
   * Indica si es un mapa de extranjero
   * Usado en mapType='congresales'
   */
  @Input() esEstranjero?: boolean = false;

  /**
   * Habilita optimización de filtro único
   * Usado en mapType='congresales'
   */
  @Input() oneFilter?: boolean = false;

  /**
   * Nivel de ubigeo para mapas de revocatorias
   * 1: Departamento, 2: Provincia, 3: Distrito
   */
  @Input() nivelUbigeo?: number;

  /**
   * Código de ubigeo para zoom en mapas de revocatorias
   */
  @Input() codigoUbigeo?: string;

  /**
   * Datos adicionales de mapa de calor para revocatorias
   */
  @Input() mapaDeCalorData?: any[];

  /**
   * Lista de ubigeos participantes para revocatorias
   */
  @Input() ubigeosParticipantes?: any[];

  /**
   * Etiqueta custom para leyenda (usado en contabilizadas)
   */
  @Input() etiquetaLeyenda?: string = 'Porcentaje de actas contabilizadas';

  /**
   * Mostrar botón de mundo en mapas congresales
   */
  @Input() mostrarBotonMundo?: boolean = true;

  // ============================================================================
  // PROPIEDADES INTERNAS PARA GESTIÓN DE MAPAS
  // ============================================================================

  // Propiedades para manejo interno de mapas congresales/senadores
  private chart: am5map.MapChart;
  private polygonSeries: am5map.MapPolygonSeries;
  private root: am5.Root;
  private changeTipoMapa: boolean = false;

  /**
   * Estado de carga del mapa
   */
  public isLoading = true;

  /**
   * Indica si el mapa ya fue inicializado
   */
  private isInitialized = false;
  private cleanupMapInteractionGuards: Array<() => void> = [];
  private scrollLockTimer: ReturnType<typeof setTimeout> | null = null;
  private releaseScrollLock: (() => void) | null = null;

  constructor(private readonly hotMapService: HotMapService, private injector: Injector) {
    this.codigoAgrupacionPolitica = null;
    AppInjector.setInjector(this.injector); // Guardamos el inyector para uso global
  }

  ngOnInit(): void {
    this.hotMapService.isParticipaciónCiudadana = this.isParticipaciónCiudadana;
    
    // Iniciar con estado de carga si está habilitado
    if (this.showLoadingState) {
      this.isLoading = true;
    }
  }

  ngAfterViewInit(): void {
    this.setupMapInteractionGuards();
  }

  /**
   * Detecta cambios en los @Input y carga el mapa correspondiente
   */
  ngOnChanges(changes: SimpleChanges): void {
    // Manejar cambios específicos según el tipo de mapa
    if (this.mapType === 'congresales' || this.mapType === 'contabilizadas') {
      const mapaCalorChange = changes['mapaCalor'];
      const esExtranjerChange = changes['esEstranjero'];
      const nivelUbigeoChange = changes['nivelUbigeo'];
      const codigoUbigeoChange = changes['codigoUbigeo'];
      
      // Cargar mapa si hay cambios en cualquiera de estos inputs
      const shouldReload = mapaCalorChange || esExtranjerChange || nivelUbigeoChange || codigoUbigeoChange;
      
      if (shouldReload) {
        this.changeTipoMapa =
          !mapaCalorChange?.firstChange &&
          esExtranjerChange?.previousValue !== esExtranjerChange?.currentValue;
        
        // Solo cargar si hay datos o si el mapa aún no existe o si cambió el nivel/ubigeo
        if (mapaCalorChange?.currentValue || mapaCalorChange?.firstChange || !this.chart || 
            nivelUbigeoChange || codigoUbigeoChange || esExtranjerChange) {
          setTimeout(() => {
            this.loadMapByType();
          }, 300);
        }
      }
    } else if (this.mapType === 'revocatorias') {
      const nivelChange = changes['nivelUbigeo'];
      const mapaCalorChange = changes['mapaCalor'];
      
      if (nivelChange || (mapaCalorChange && mapaCalorChange.currentValue)) {
        setTimeout(() => {
          this.loadMapByType();
        }, 300);
      }
    }
  }

  /**
   * this method is called from external component
   */
  public loadInitialUbigeoPeru(codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.hotMapService.electionId = this.electionId; // this value is mandatory
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    setTimeout(() => {
      this.loadJoinedHotMapDepartments(codigoAgrupacionPolitica, candidato);
      this.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }, DELAY_TO_LOAD_MAPS);
  }

  public loadInitialUbigeoInternational(codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.hotMapService.electionId = this.electionId; // this value is mandatory
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    setTimeout(() => {
      this.loadJoinedHotMapContinents(codigoAgrupacionPolitica, candidato);
      this.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }, DELAY_TO_LOAD_MAPS);
  }

  public loadInitialUbigeoWorld(codigoAgrupacionPolitica?: string) {
    this.hotMapService.electionId = this.electionId; // this value is mandatory
    setTimeout(() => {
      this.loadJoinedHotMapWorld();
      this.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }, DELAY_TO_LOAD_MAPS);
  }

  /**
   * this method is called from external component
   */
  public loadUbigeoDepartamento(ubigeoNivel01: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = { departmentUbigeoId: ubigeoNivel01 };
    this.loadJoinedHotMapProvinces(ubigeoNivel01, codigoAgrupacionPolitica, candidato);
  }

  /**
   * this method is called from external component
   */
  public loadUbigeoProvince(ubigeoNivel01: string, ubigeoNivel02: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = {
      departmentUbigeoId: ubigeoNivel01,
      provinceUbigeoId: ubigeoNivel02,
    };
    this.loadJoinedHotMapDistricts(ubigeoNivel01, ubigeoNivel02, codigoAgrupacionPolitica, candidato);
  }

  /**
   * this method is called from external component
   */
  public loadUbigeoDistrict(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    ubigeoNivel03: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = {
      departmentUbigeoId: ubigeoNivel01,
      provinceUbigeoId: ubigeoNivel02,
      districtUbigeoId: ubigeoNivel03,
    };
    this.loadOnlyDistrictFromUbigeoFilters(ubigeoNivel01, ubigeoNivel02, ubigeoNivel03, candidato);
  }

  /**
   * this method is called from external component
   */
  public loadUbigeoContinent(ubigeoNivel01: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = {
      regionString: "EXTRANJERO",
      departmentUbigeoId: ubigeoNivel01,
    };
    this.loadJoinedHotMapCountries(ubigeoNivel01, codigoAgrupacionPolitica, candidato);
  }

  public loadUbigeoCountry(ubigeoNivel01: string, ubigeoNivel02: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = {
      regionString: "EXTRANJERO",
      departmentUbigeoId: ubigeoNivel01,
      provinceUbigeoId: ubigeoNivel02,
    };
    this.loadOnlyCountryFromUbigeoFilters(ubigeoNivel01, ubigeoNivel02, codigoAgrupacionPolitica, candidato);
  }

  public loadUbigeoState(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    ubigeoNivel03: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ) {
    this.updateSelectedCandidate(candidato, codigoAgrupacionPolitica);
    this.selectedUbigeoParams = {
      regionString: "EXTRANJERO",
      departmentUbigeoId: ubigeoNivel01,
      provinceUbigeoId: ubigeoNivel02,
      districtUbigeoId: ubigeoNivel03,
    };
    this.loadOnlyCountryFromUbigeoFilters(ubigeoNivel01, ubigeoNivel02, codigoAgrupacionPolitica, candidato);
  }

  /**
   * CARGA ACTUAL DE MAPA
   * @param codigoAgrupacionPolitica
   */
  private loadJoinedHotMapWorld(codigoAgrupacionPolitica?: string): void {
    this.hotMapService
      .getJoinedHotMapWorld$(codigoAgrupacionPolitica)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapWorldData = response.data;
          this.loadWorldMap();
        } else {
          console.error("loadJoinedHotMapWorld error");
        }
      });
  }

  private loadJoinedHotMapDepartments(codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem): void {
    this.hotMapService
      .getJoinedHotMapDepartments$(codigoAgrupacionPolitica, candidato)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapDepartments = response.data;
          this.loadMap();
        } else {
          console.error("loadJoinedHotMapDepartments error");
        }
      });
  }

  private loadJoinedHotMapProvinces(ubigeoNivel01: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem): void {
    this.hotMapService
      .getJoinedHotMapProvinces$(ubigeoNivel01, codigoAgrupacionPolitica, candidato)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapProvinces = response.data;
          this.loadDepartmentMap(ubigeoNivel01);
        } else {
          console.error("loadJoinedHotMapProvinces error");
        }
      });
  }

  private loadJoinedHotMapDistricts(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): void {
    this.hotMapService
      .getJoinedHotMapDistricts$(ubigeoNivel01, ubigeoNivel02, codigoAgrupacionPolitica, candidato)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapDistricts = response.data;
          this.loadProvinceMap(ubigeoNivel02);
        } else {
          console.error("loadJoinedHotMapDistricts error");
        }
      });
  }

  /**
   * This method depends on this.hotMapDistricts in a way mandatory
   */
  private loadOnlyDistrictFromUbigeoFilters(ubigeoNivel01: string, ubigeoNivel02: string, ubigeoNivel03: string, candidato?: PoliticalOrganizationItem) {
    if (this.hotMapDistricts.length == 0) {
      this.hotMapService
        .getJoinedHotMapDistricts$(ubigeoNivel01, ubigeoNivel02, null, candidato)
        .pipe(take(1))
        .subscribe((response) => {
          if (response.success) {
            this.hotMapDistricts = response.data;
            const selectedHotMapDistrict = this.hotMapDistricts.find((x) => x.ubigeo == ubigeoNivel03);
            this.loadDistritoMap(ubigeoNivel03, ubigeoNivel02, selectedHotMapDistrict);
          } else {
            console.error("loadJoinedHotMapDistricts error");
          }
        });
    } else {
      const selectedHotMapDistrict = this.hotMapDistricts.find((x) => x.ubigeo == ubigeoNivel03);
      if (selectedHotMapDistrict) {
        this.loadDistritoMap(ubigeoNivel03, ubigeoNivel02, selectedHotMapDistrict);
      } else {
        this.hotMapDistricts = [];
        this.loadOnlyDistrictFromUbigeoFilters(ubigeoNivel01, ubigeoNivel02, ubigeoNivel03, candidato);
      }
    }
  }

  private loadJoinedHotMapContinents(codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem): void {
    this.hotMapService
      .getJoinedHotMapContinents$(codigoAgrupacionPolitica, candidato)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapContinents = response.data;
          this.loadInternationalMap();
        } else {
          console.error("loadJoinedHotMapContinents error");
        }
      });
  }

  private loadJoinedHotMapCountries(ubigeoNivel01: string, codigoAgrupacionPolitica?: string, candidato?: PoliticalOrganizationItem): void {
    this.hotMapService
      .getJoinedHotMapCountries$(ubigeoNivel01, codigoAgrupacionPolitica, candidato)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.hotMapCountries = response.data;
          this.loadContinentMap(ubigeoNivel01);
        } else {
          console.error("loadJoinedHotMapCountries error");
        }
      });
  }

  private loadOnlyCountryFromUbigeoFilters(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ) {
    if (this.hotMapCountries.length == 0) {
      this.hotMapService
        .getJoinedHotMapCountries$(ubigeoNivel01, codigoAgrupacionPolitica, candidato)
        .pipe(take(1))
        .subscribe((response) => {
          if (response.success) {
            this.hotMapCountries = response.data;
            const selectedHotMapCountry = this.hotMapCountries.find((x) => x.ubigeo == ubigeoNivel02);
            this.loadCountryMap(ubigeoNivel02, ubigeoNivel01, selectedHotMapCountry);
          } else {
            console.error("loadJoinedHotMapCountries error");
          }
        });
    } else {
      const selectedHotMapCountry = this.hotMapCountries.find((x) => x.ubigeo == ubigeoNivel02);
      if (selectedHotMapCountry) {
        this.loadCountryMap(ubigeoNivel02, ubigeoNivel01, selectedHotMapCountry);
      } else {
        this.hotMapCountries = [];
        this.loadOnlyCountryFromUbigeoFilters(ubigeoNivel01, ubigeoNivel02, codigoAgrupacionPolitica, candidato);
      }
    }
  }

  private addLicense() {
    if (am5.registry.licenses.length > 0) {
      am5.addLicense("AM5C357384425");
    }
  }

  private getRootMap(): am5.Root | null {
    // Guard: the DOM element must exist before amcharts can create a Root.
    const el = document.getElementById(this.mapContainerId);
    if (!el) {
      console.warn(`[MainHotMap] DOM element #${this.mapContainerId} not found – skipping map initialization`);
      return null;
    }

    if (this.rootMap) {
      return this.rootMap;
    }

    const staleRoots = am5.registry.rootElements.filter(
      (r: am5.Root) => r?.dom?.id === this.mapContainerId
    );
    staleRoots.forEach((r: am5.Root) => r.dispose());

    this.rootMap = am5.Root.new(this.mapContainerId, {
      tooltipContainerBounds: {
        top: 100,
        left: 50,
        right: 50,
        bottom: 50,
      },
    });
    if (this.rootMap._logo) {
      this.rootMap._logo.dispose();
    }
    this.rootMap.setThemes([am5themes_Animated.new(this.rootMap)]);
    return this.rootMap;
  }

  private getChartMap(rootMap: am5.Root): am5map.MapChart {
    const zoomControl = this.getZoomControl(rootMap);
    zoomControl.minusButton
      .get("background")
      .states.create("hover", {})
      .setAll({
        fill: am5.color("#6DB2E2"),
        fillOpacity: 0.7,
      });

    let chart = rootMap.container.children.push(
      am5map.MapChart.new(rootMap, {
        wheelY: "none",
        wheelX: "none",
        rotationX: -13,
        paddingBottom: LEGEND_HEIGHT,
        zoomControl: zoomControl,
      })
    );

    setCustomButtonsForZoom(rootMap, chart);

    return chart;
  }

  private getZoomControl(rootMap: am5.Root): am5map.ZoomControl {
    const zoomControl = am5map.ZoomControl.new(rootMap, {
      active: true,
    });

    zoomControl.minusButton
      .get("background")
      .states.create("hover", {})
      .setAll({
        fill: am5.color("#6DB2E2"),
        fillOpacity: 0.7,
      });

    return zoomControl;
  }

  private setCustomHeatLegend(rootMap: am5.Root, chart: am5map.MapChart) {
    loadMapCustomHeatLegend(
      rootMap,
      chart,
      { init: INITIAL_COLOR_PERCENTAGE_0, end: FINAL_COLOR_PERCENTAGE_100 },
      HEAT_LEGEND_TEXTS[this.heatLegendTextType]
    );
  }

  private emitClickButton(string){
    this.lockScrollPositionTemporarily(BUTTON_SEQUENCE_SCROLL_LOCK_MS);
    this.regionChanged.emit(string as RegionValue);
    document.body.style.cursor = "default";
  }

  private preserveScrollPosition(): void {
    const x = window.scrollX;
    const y = window.scrollY;
    requestAnimationFrame(() => window.scrollTo({ left: x, top: y, behavior: 'auto' }));
    setTimeout(() => window.scrollTo({ left: x, top: y, behavior: 'auto' }), 0);
  }

  private lockScrollPositionTemporarily(durationMs: number = 700): void {
    this.releaseScrollLock?.();
    if (this.scrollLockTimer) {
      clearTimeout(this.scrollLockTimer);
      this.scrollLockTimer = null;
    }

    const x = window.scrollX;
    const y = window.scrollY;
    const restore = () => window.scrollTo({ left: x, top: y, behavior: 'auto' });
    const onScroll = () => restore();

    window.addEventListener('scroll', onScroll, { passive: true });
    this.releaseScrollLock = () => {
      window.removeEventListener('scroll', onScroll);
      this.releaseScrollLock = null;
    };

    restore();
    requestAnimationFrame(restore);
    setTimeout(restore, 0);
    setTimeout(restore, 120);
    setTimeout(restore, 300);
    setTimeout(restore, 700);
    setTimeout(restore, 1100);

    this.scrollLockTimer = setTimeout(() => {
      this.releaseScrollLock?.();
      this.scrollLockTimer = null;
    }, durationMs);
  }

  private setupMapInteractionGuards(): void {
    const el = this.mapContentRef?.nativeElement;
    if (!el) {
      return;
    }

    const stopPropagation = (event: Event) => {
      event.stopPropagation();
    };

    const preventScroll = (event: Event) => {
      event.preventDefault();
      event.stopPropagation();
    };

    el.addEventListener('click', stopPropagation);
    this.cleanupMapInteractionGuards.push(() => el.removeEventListener('click', stopPropagation));

    el.addEventListener('mousedown', stopPropagation);
    this.cleanupMapInteractionGuards.push(() => el.removeEventListener('mousedown', stopPropagation));

    el.addEventListener('wheel', preventScroll as EventListener, { passive: false });
    this.cleanupMapInteractionGuards.push(() => el.removeEventListener('wheel', preventScroll as EventListener));

    el.addEventListener('touchmove', preventScroll as EventListener, { passive: false });
    this.cleanupMapInteractionGuards.push(() => el.removeEventListener('touchmove', preventScroll as EventListener));
  }


  // CARGAR MAPA DEL PERÚ
  private loadMap() {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);
    this.applyZoomConfig(chart);

    const mapURL = MapsUrl.peru;

    this.setCustomHeatLegend(rootMap, chart);

    if (this.showIconTodos && !this.hideRegionButtons) {
      loadMapCustomHomeButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_TODOS);
        },
        2,
        this.isParticipaciónCiudadana
      );
    }

    if (this.showIconExtranjero && !this.hideRegionButtons) {
      loadMapCustomExtrangeroButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_EXTRAJERO);
        },
        1
      );
    }

    loadPolygonSeriesFromMapAndChart(mapURL, chart, rootMap, () => {
      commonLoadHeatSubMapPoligons({
        map: mapURL,
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: this.hotMapDepartments,
        callback: this.departmentPolygonClicked.bind(this),
        featureElementType: "type03",
        heatLegendTextType: this.heatLegendTextType,
        tooltipType: this.tooltipType,
        clickMap: this.clickMap
      });
      
      // Marcar el mapa como listo
      this.markMapAsReady();
    });
  }

  // CARGAR MAPA DEL MUNDO
  private loadWorldMap() {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);

    this.applyZoomConfig(chart);

    const mapURL = MapsUrl.allContinentsUltra;

    this.setCustomHeatLegend(rootMap, chart);

    if (!this.hideRegionButtons) {
      loadMapCustomPeruButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_PERU);
        },
        2
      );
      loadMapCustomExtrangeroButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_EXTRAJERO);
        },
        1
      );
    }

    let color = "0xc3c3c3";
    if (this.hotMapWorldData.length > 0) {
      color = getDegradatedColorFromPercentage(this.hotMapWorldData[0].percentage, {
        init: INITIAL_COLOR_PERCENTAGE_0,
        end: FINAL_COLOR_PERCENTAGE_100,
      });
    }

    from(am5.net.load(mapURL, chart))
      .pipe(take(1))
      .subscribe((result) => {
        const polygonSeries_Base = chart.series.push(
          am5map.MapPolygonSeries.new(rootMap, {
            geoJSON: am5.JSONParser.parse(result.response),
            fill: am5.color(color),
            stroke: am5.color(0xffffff),
          })
        );
        polygonSeries_Base.mapPolygons.template.setAll({
          strokeWidth: 0.5,
        });
        
        // Marcar el mapa como listo
        this.markMapAsReady();
      });
  }

  // CARGAR MAPA INTERNACIONAL
  private loadInternationalMap() {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);

    this.applyZoomConfig(chart);

    const mapURL = MapsUrl.allContinentsUltra;
    const mapURL1 = MapsUrl.allContinentsUltra1;

    this.setCustomHeatLegend(rootMap, chart);

    if (this.showIconTodos && !this.hideRegionButtons) {
      loadMapCustomHomeButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_TODOS);
        },
        2,
        this.isParticipaciónCiudadana
      );
    }


    if (this.showIconExtranjero && !this.hideRegionButtons) {
      loadMapCustomPeruButton(
        rootMap,
        chart,
        () => {
          this.emitClickButton(REGION_PERU);
        },
        1
      );
    }

    loadPolygonSeriesFromMapAndChart(mapURL, chart, rootMap, () => {
      commonLoadHeatSubMapPoligons({
        map: mapURL,
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: this.hotMapContinents,
        callback: this.continentPolygonClicked.bind(this),
        featureElementType: "type03",
        heatLegendTextType: this.heatLegendTextType,
        tooltipType: this.tooltipType,
        clickMap: this.clickMap
      });

      loadPolygonSeriesFromMapAndChartLockPeru(mapURL1, chart, rootMap);
      
      // Marcar el mapa como listo
      this.markMapAsReady();
    });
  }

  // CARGAR 1 DEPARTAMENTO
  private loadDepartmentMap(ubigeo: string) {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);

    this.applyZoomConfig(chart);

    const map = `./assets/lib/amcharts5/geodata/json/departamentos/${ubigeo}.json`;

    this.setCustomHeatLegend(rootMap, chart);
    loadMapCustomPeruButton(rootMap, chart, () => {
      this.emitClickButton(REGION_PERU);
    });

    loadPolygonSeriesFromMapAndChart(map, chart, rootMap, () => {
      commonLoadHeatSubMapPoligons({
        map: map,
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: this.hotMapProvinces,
        callback: this.provincePolygonClicked.bind(this),
        featureElementType: "type02",
        heatLegendTextType: this.heatLegendTextType,
        tooltipType: this.tooltipType,
        clickMap: this.clickMap
      });
    });
  }

  // CARGAR 1 PROVINCIA
  private loadProvinceMap(ubigeo: string) {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);

    this.applyZoomConfig(chart);

    const map = `./assets/lib/amcharts5/geodata/json/provincias/${ubigeo}.json`;

    this.setCustomHeatLegend(rootMap, chart);
    loadMapCustomPeruButton(rootMap, chart, () => {
      this.emitClickButton(REGION_PERU);
    });

    loadPolygonSeriesFromMapAndChart(map, chart, rootMap, () => {
      commonLoadHeatSubMapPoligons({
        map: map,
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: this.hotMapDistricts,
        callback: this.districtPolygonClicked.bind(this),
        featureElementType: "type02",
        heatLegendTextType: this.heatLegendTextType,
        tooltipType: this.tooltipType,
        clickMap: this.clickMap
      });
    });
  }

  private loadDistritoMap(ubigeoDistrito: string, ubigeoProvincia: string, selectedHotMapDistrict: IHotMapUbigeoItem) {
    setTimeout(() => {
      this.addLicense();
      this.destroyMap();

      const rootMap = this.getRootMap();
      if (!rootMap) return;
      const chart = this.getChartMap(rootMap);

      this.applyZoomConfig(chart);

      const map = `./assets/lib/amcharts5/geodata/json/provincias/${ubigeoProvincia}.json`;

      this.setCustomHeatLegend(rootMap, chart);
      loadMapCustomPeruButton(rootMap, chart, () => {
        this.emitClickButton(REGION_PERU);
      });

      loadPolygonSeriesFromMapAndChartForDistrict(
        map,
        chart,
        rootMap,
        ubigeoDistrito,
        selectedHotMapDistrict,
        this.heatLegendTextType,
        this.tooltipType,
        this.clickMap
      );
    }, 200);
  }

  // CARGAR 1 CONTINENTE
  private loadContinentMap(ubigeo: string) {
    this.addLicense();
    this.destroyMap();

    const rootMap = this.getRootMap();
    if (!rootMap) return;
    const chart = this.getChartMap(rootMap);

    this.applyZoomConfig(chart);

    chart.set("rotationX", 0);
    const map = `./assets/lib/amcharts5/geodata/json/continentes/${ubigeo}.json`;

    this.setCustomHeatLegend(rootMap, chart);
    loadMapCustomWorldButton(rootMap, chart, () => {
      this.emitClickButton(REGION_EXTRAJERO);
    });
    
    loadPolygonSeriesFromMapAndChart(map, chart, rootMap, () => {
      commonLoadHeatSubMapPoligons({
        map: map,
        chart: chart,
        rootMap: rootMap,
        hotMapsUbigeos: this.hotMapCountries,
        callback: this.countryPolygonClicked.bind(this),
        featureElementType: "type01",
        heatLegendTextType: this.heatLegendTextType,
        tooltipType: this.tooltipType,
        clickMap: this.clickMap
      });
    });
  }

  private loadCountryMap(ubigeoCountry: string, ubigeoContinente: string, selectedHotMapCountry: IHotMapUbigeoItem) {
    setTimeout(() => {
      this.addLicense();
      this.destroyMap();

      const rootMap = this.getRootMap();
      if (!rootMap) return;
      const chart = this.getChartMap(rootMap);

      this.applyZoomConfig(chart);

      chart.set("rotationX", 0);
      const map = `./assets/lib/amcharts5/geodata/json/continentes/${ubigeoContinente}.json`;

      this.setCustomHeatLegend(rootMap, chart);
      loadMapCustomExtrangeroButton(rootMap, chart, () => {
        this.emitClickButton(REGION_EXTRAJERO);
      });

      loadPolygonSeriesFromMapAndChartForCountry(
        map,
        chart,
        rootMap,
        ubigeoCountry,
        selectedHotMapCountry,
        this.heatLegendTextType,
        this.tooltipType,
      );
    }, 500);
  }

  // EVENTOS DE CLICK

  private departmentPolygonClicked(event: am5.ISpritePointerEvent) {
    this.lockScrollPositionTemporarily();
    const currentContext = event.target.dataItem.dataContext as any;
    const currentClicked = this.hotMapDepartments.filter((x) => x.ubigeo == currentContext.id);
    if (currentClicked.length > 0) {
      this.selectedUbigeoParams = {
        departmentUbigeoId: currentContext.id,
      } as FilterByLocationParams;

      if (this.codigoAgrupacionPolitica) {
        this.loadJoinedHotMapProvinces(currentContext.id, this.codigoAgrupacionPolitica, this.selectedCandidate);
      } else {
        this.loadJoinedHotMapProvinces(currentContext.id, undefined, this.selectedCandidate);
      }

      this.ubigeoParamsChanged.emit(this.selectedUbigeoParams);
    }
    document.body.style.cursor = "default";
  }

  private provincePolygonClicked(event: am5.ISpritePointerEvent) {
    this.lockScrollPositionTemporarily();
    const currentContext = event.target.dataItem.dataContext as any;
    const currentClicked = this.hotMapProvinces.filter((x) => x.ubigeo == currentContext.ID);
    if (currentClicked.length > 0) {
      this.selectedUbigeoParams = {
        ...this.selectedUbigeoParams,
        provinceUbigeoId: currentContext.ID,
      } as FilterByLocationParams;

      if (this.codigoAgrupacionPolitica) {
        this.loadJoinedHotMapDistricts(
          this.selectedUbigeoParams.departmentUbigeoId,
          currentContext.ID,
          this.codigoAgrupacionPolitica,
          this.selectedCandidate
        );
      } else {
        this.loadJoinedHotMapDistricts(
          this.selectedUbigeoParams.departmentUbigeoId,
          currentContext.ID,
          undefined,
          this.selectedCandidate
        );
      }

      this.selectedUbigeoParams = getOptimizedObject(this.selectedUbigeoParams);
      this.ubigeoParamsChanged.emit(this.selectedUbigeoParams);
    }
    document.body.style.cursor = "default";
  }

  private districtPolygonClicked(event: am5.ISpritePointerEvent) {
    this.lockScrollPositionTemporarily();
    const currentContext = event.target.dataItem.dataContext as any;

    if (this.selectedUbigeoParams.districtUbigeoId == currentContext.ID) {
      return;
    }
    const currentClicked = this.hotMapDistricts.filter((x) => x.ubigeo == currentContext.ID);
    if (currentClicked.length > 0) {
      this.selectedUbigeoParams = {
        ...this.selectedUbigeoParams,
        districtUbigeoId: currentContext.ID,
      } as FilterByLocationParams;

      const selectedHotMapDistrict = this.hotMapDistricts.find((x) => x.ubigeo == currentContext.ID);
      this.loadDistritoMap(currentContext.ID, this.selectedUbigeoParams.provinceUbigeoId, selectedHotMapDistrict);

      this.selectedUbigeoParams = getOptimizedObject(this.selectedUbigeoParams);
      this.ubigeoParamsChanged.emit(this.selectedUbigeoParams);
    }
    document.body.style.cursor = "default";
  }

  private continentPolygonClicked(event: am5.ISpritePointerEvent) {
    this.lockScrollPositionTemporarily();
    const currentContext = event.target.dataItem.dataContext as any;
    const currentClicked = this.hotMapContinents.filter((x) => x.ubigeo == currentContext.id);

    if (currentClicked.length > 0) {
      this.selectedUbigeoParams = {
        regionString: "EXTRANJERO",
        departmentUbigeoId: currentContext.id,
      } as FilterByLocationParams;
    }

    if (this.codigoAgrupacionPolitica) {
      this.loadJoinedHotMapCountries(currentContext.id, this.codigoAgrupacionPolitica, this.selectedCandidate);
    } else {
      this.loadJoinedHotMapCountries(currentContext.id, undefined, this.selectedCandidate);
    }

    this.selectedUbigeoParams = getOptimizedObject(this.selectedUbigeoParams);
    this.ubigeoParamsChanged.emit(this.selectedUbigeoParams);
    document.body.style.cursor = "default";
  }

  private countryPolygonClicked(event: am5.ISpritePointerEvent) {
    this.lockScrollPositionTemporarily();
    const currentContext = event.target.dataItem.dataContext as any;

    if (this.selectedCountryId == currentContext.id) {
      return;
    }

    const currentClicked = this.hotMapCountries.filter((x) => x.ubigeo == currentContext.id);
    this.selectedCountryId = currentContext.id;

    if (currentClicked.length > 0) {
      this.selectedUbigeoParams = {
        ...this.selectedUbigeoParams,
        provinceUbigeoId: currentContext.id,
      } as FilterByLocationParams;

      const selectedHotMapCountry = this.hotMapCountries.find((x) => x.ubigeo == currentContext.id);

      this.loadCountryMap(
        currentContext.id,
        this.selectedUbigeoParams.departmentUbigeoId, // el ubigeo en este caso es del continente
        selectedHotMapCountry
      );

      this.selectedUbigeoParams = getOptimizedObject(this.selectedUbigeoParams);
      this.ubigeoParamsChanged.emit(this.selectedUbigeoParams);
    }
    document.body.style.cursor = "default";
  }

  ngOnDestroy(): void {
    this.cleanupMapInteractionGuards.forEach((cleanup) => cleanup());
    this.cleanupMapInteractionGuards = [];
    this.releaseScrollLock?.();
    if (this.scrollLockTimer) {
      clearTimeout(this.scrollLockTimer);
      this.scrollLockTimer = null;
    }
    if (!this.disableAutoDestroy) {
      this.destroyMap();
    }
  }

  // ============================================================================
  // MÉTODOS ESPECÍFICOS PARA CADA TIPO DE MAPA
  // ============================================================================

  /**
   * Carga el mapa según el tipo configurado
   */
  private loadMapByType(): void {
    if (this.showLoadingState) {
      this.isLoading = true;
    }
    switch (this.mapType) {
      case 'congresales':
        this.loadMapCongresales();
        break;
      case 'contabilizadas':
        this.loadMapContabilizadas();
        break;
      case 'revocatorias':
        this.loadMapRevocatorias();
        break;
      case 'presidenciales':
      default:
        // Ya está implementado en loadInitialUbigeoPeru
        break;
    }

    this.applyZoomConfig(this.chart);
  }

  /**
   * Carga mapa de distritos electorales para congresales/senadores
   */
  private loadMapCongresales(): void {
    const mapContainerId = this.mapContainerId || 'chartdiv';
    
    // Verificar si necesitamos crear un nuevo mapa o actualizar el existente
    const shouldCreateNewMap = !this.oneFilter || this.changeTipoMapa || !this.chart || !this.polygonSeries;
    
    if (shouldCreateNewMap) {
      this.destroyChartHelper(mapContainerId);
    }

    if (am5.registry.licenses.length > 0) {
      am5.addLicense("AM5C357384425");
    }

    const eventClickMapaEmitter = new EventEmitter<number>();
    eventClickMapaEmitter.subscribe((id: number) => {
      this.ubigeoParamsChanged.emit({ electoralDistrictId: id } as any);
    });

    const datos = {
      id: mapContainerId,
      mapaCalor: this.mapaCalor,
      esEstranjero: this.esEstranjero,
      oneFilter: this.oneFilter,
      idDistritoElectoral: this.idDistritoElectoral,
      mostrarBotonMundo: this.mostrarBotonMundo,
    };

    if (shouldCreateNewMap) {
      // Crear nuevo mapa
      const result = cargaInicialMapa({ datos, eventClickMapa: eventClickMapaEmitter }, (chart, polygonSeries, root, mapaCalor) => {
        cargarDataMapaNacional(chart, polygonSeries, root, mapaCalor, { datos, eventClickMapa: eventClickMapaEmitter });
        this.chart = chart;
        this.polygonSeries = polygonSeries;
        this.root = root;
        this.setLoadingState(false);
      });
    } else {
      // Actualizar mapa existente
      actualizarDataMapa(this.chart, this.polygonSeries, this.root, this.mapaCalor, { datos, eventClickMapa: eventClickMapaEmitter });
      this.setLoadingState(false);
    }
  }

  private applyZoomConfig(chart: am5map.MapChart): void {
    if(this.changeZoomLevelsToThree && chart){
        chart.set("maxZoomLevel", MAP_ZOOM["MAX_ZOOM_LEVEL"]);
        chart.set("zoomStep", MAP_ZOOM["INCREASE_ZOOM_FACTOR"]);
        chart.set("zoomLevel", 1);
    }
  }

  /**
   * Carga mapa de porcentaje de actas contabilizadas
   * Usa la misma lógica que MapaCalorContabilizadaComponent original
   */
  private loadMapContabilizadas(): void {
    const mapContainerId = this.mapContainerId || 'chartdiv';
  
    cancelAnimationFrame(window['_lastRaf']);
      window['_lastRaf'] = requestAnimationFrame(() => {
        am5.registry.rootElements
          .filter(r => r?.dom?.id === mapContainerId)
          .forEach(r => r.dispose());
        
        this.destroyChartHelper(mapContainerId);

        const tipoNivelAmbito = this.nivelUbigeo || 0;
        const ubigeo = this.codigoUbigeo || '';
        const nacional = !this.esEstranjero;

        if (am5.registry.licenses.length > 0) {
          am5.addLicense("AM5C357384425");
        }

      const mapaCalorData = this.mapaCalor && Array.isArray(this.mapaCalor) && this.mapaCalor.length > 0 
        ? this.mapaCalor 
        : [];

      const eventClickMapaEmitter = new EventEmitter<any>();
      eventClickMapaEmitter.subscribe((ubigeoId: any) => {
        this.cambioUbigeoEvent.emit(ubigeoId);
        this.ubigeoParamsChanged.emit({ departmentUbigeoId: ubigeoId } as any);
      });

      const datos = {
        id: mapContainerId,
        mapaCalor: mapaCalorData,
        esEstranjero: this.esEstranjero,
      };

      requestAnimationFrame(() => {
        cargaInicialMapa(
          { datos, eventClickMapa: eventClickMapaEmitter },
          (chart, polygonSeries, root, mapaCalor) => {
            if (nacional) {
              this.cargarDataMapaNacionalContabilizadas(chart, polygonSeries, root, mapaCalor, tipoNivelAmbito, ubigeo, eventClickMapaEmitter);
            } else {
              this.cargarDataMapaExtranjeroContabilizadas(chart, polygonSeries, root, mapaCalor, eventClickMapaEmitter);
            }
            this.chart = chart;
            this.applyZoomConfig(chart)
            this.polygonSeries = polygonSeries;
            this.rootMap = root;
            this.setLoadingState(false);
          }
        );
      });
    });
  }

  /**
   * Carga datos del mapa nacional de contabilizadas
   * Replicando exactamente MapaCalorContabilizadaComponent.cargarDataMapaNacional
   */
  private cargarDataMapaNacionalContabilizadas(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: MapaCalor[],
    tipoNivelAmbito: number = 0,
    ubigeo: string = '',
    eventClickMapaEmitter: EventEmitter<any>
  ): void {
    chart.set("projection", am5map.geoMercator());

    // Determinar qué mapa cargar según el nivel
    let map = `./assets/lib/amcharts5/geodata/json/peruLow.json`;
    if (tipoNivelAmbito === 1) {
      map = `./assets/lib/amcharts5/geodata/json/departamentos/${ubigeo}.json`;
    } else if (tipoNivelAmbito === 2) {
      map = `./assets/lib/amcharts5/geodata/json/provincias/${ubigeo}.json`;
    } else if (tipoNivelAmbito === 3) {
      const dUbigeo = ubigeo.slice(0, -2) + "00";
      map = `./assets/lib/amcharts5/geodata/json/provincias/${dUbigeo}.json`;
      this.distritoSeleccionadoEvent.emit(true);
    }

    am5.net.load(map).then((result) => {
      if (chart.isDisposed() || root.isDisposed()) {
        return;
      }
      let geodata = am5.JSONParser.parse(result.response);

      // Si es nivel 3 (distrito), filtrar solo el distrito específico
      if (tipoNivelAmbito === 3 && ubigeo) {
        const district = geodata.features.find((element: any) => element.properties.ID === ubigeo);
        if (district) {
          geodata.features = [district];
        }
      }
      
      // Si es nivel 0 (mapa nacional), excluir áreas bloqueadas (Lago Titicaca)
      if (tipoNivelAmbito === 0) {
        geodata.features = geodata.features.filter((feature: any) => !COUNTRY_PERU_IDS.includes(feature.id));
      }

      // Formatear nombres a mayúsculas
      geodata.features = geodata.features.map((feature: any) => {
        feature.properties.name = (feature.properties.name as string).toUpperCase();
        return feature;
      });

      const data: any[] = [];

      // Aplicar datos de mapa de calor
      if (mapaCalor && mapaCalor.length > 0) {
        for (let i = 0; i < geodata.features.length; i++) {
          const feature = geodata.features[i];
          let featureId = feature.id || feature.properties.ID;
          
          // Buscar datos según el nivel (igual que en el componente original)
          let dataPoint: MapaCalor | undefined;
          
          if (tipoNivelAmbito === 0) {
            dataPoint = mapaCalor.find((x) => x.ubigeoNivel01?.toString().padStart(6, "0") === featureId?.toString().padStart(6, "0"));
          } else if (tipoNivelAmbito === 1) {
            dataPoint = mapaCalor.find((x) => x.ubigeoNivel02?.toString().padStart(6, "0") === feature.properties.ID?.toString().padStart(6, "0"));
            featureId = feature.properties.ID;
          } else if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
            dataPoint = mapaCalor.find((x) => x.ubigeoNivel03?.toString().padStart(6, "0") === feature.properties.ID?.toString().padStart(6, "0"));
            featureId = feature.properties.ID;
          }

          if (dataPoint) {
            const percentage = dataPoint.porcentajeActasContabilizadas || 0;
            const currentColor = getDegradatedColorFromPercentage(percentage, {
              init: INITIAL_COLOR_PERCENTAGE_0,
              end: FINAL_COLOR_PERCENTAGE_100
            });

            // Determinar el ID según el nivel
            let id: any = '';
            if (tipoNivelAmbito === 0) {
              id = featureId;
            } else if (tipoNivelAmbito === 1) {
              id = dataPoint.ubigeoNivel02;
            } else if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
              id = dataPoint.ubigeoNivel03;
            }

            feature.id = id?.toString().padStart(6, '0');
            
            data.push({
              id: feature.id,
              value: 0,
              polygonSettings: {
                fill: am5.color(currentColor),
              },
            });
          } else {
            // Sin datos - color por defecto
            data.push({
              id: featureId?.toString().padStart(6, '0'),
              value: 0,
              polygonSettings: {
                fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
              },
            });
          }
        }
      }

      // Configurar eventos hover
      polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
        document.body.style.cursor = "pointer";
      });
      
      polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
        document.body.style.cursor = "default";
      });

      // Eventos de click
      polygonSeries.mapPolygons.template.events.on("click", (ev: any) => {
        if (tipoNivelAmbito === 3) return; // No navegar desde distrito
        
        const id = ev.target.dataItem?.dataContext?.ID || ev.target.dataItem?.get?.("id");
        if (id && eventClickMapaEmitter) {
          eventClickMapaEmitter.emit(String(id));
        }
        document.body.style.cursor = "default";
      });

      // Configurar tooltip
      const tooltip = am5.Tooltip.new(root, {
        autoTextColor: false,
      });

      tooltip.get("background").setAll({
        fill: am5.color(0x003874),
      });

      polygonSeries.mapPolygons.template.setAll({
        tooltip: tooltip,
        tooltipText: "[fontFamily: NotoSans-regular][fontSize: 14px]{name}",
      });

      polygonSeries.set("geoJSON", geodata);
      polygonSeries.data.setAll(data);
      
      // Si es mapa nacional (nivel 0), cargar áreas bloqueadas como Lago Titicaca
      if (tipoNivelAmbito === 0) {
        loadPolygonsWithoutActions(map, chart, root);
      }
    });

    // Cambiar fondo a blanco usando DOM manipulation
    setTimeout(() => {
      const mapContainer = document.getElementById(root.dom.id);
      if (mapContainer) {
        mapContainer.style.backgroundColor = '#ffffff';
      }
    }, 100);

    // Cargar botones al final
    this.cargarBotonesContabilizadas(root, chart, tipoNivelAmbito);
  }

  /**
   * Carga datos del mapa extranjero de contabilizadas
   * Nivel 0: Continentes totales
   * Nivel 1: Países de un continente
   * Nivel 2: País específico
   */
  private cargarDataMapaExtranjeroContabilizadas(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: MapaCalor[],
    eventClickMapaEmitter: EventEmitter<any>
  ): void {
    const tipoNivelAmbito = this.nivelUbigeo || 0;
    const ubigeo = this.codigoUbigeo || '';
    
    chart.set("projection", am5map.geoMercator());
    
    // Remover la rotación cuando se selecciona un continente o país específico
    if (tipoNivelAmbito >= 1) {
      chart.set("rotationX", 0);
    }
    
    // Determinar qué mapa cargar según el nivel
    let map = `./assets/lib/amcharts5/geodata/json/continental_total.json`;
    if (tipoNivelAmbito === 1) {
      map = `./assets/lib/amcharts5/geodata/json/continentes/${ubigeo}.json`;
    } else if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
      const pUbigeo = ubigeo.toString().slice(0, -4) + "0000";
      map = `./assets/lib/amcharts5/geodata/json/continentes/${pUbigeo}.json`;
    }

    am5.net.load(map, chart).then((result) => {
      // Reemplazos específicos para ciertos países
      const reemplazos: Record<string, string> = {
        "920100": "AW",      // antillas holandesas -> aruba
        "921200": "920511"   // el salvador
      };
      
      let ubigeoAjustado = reemplazos[ubigeo] ?? ubigeo;
      if (tipoNivelAmbito === 3) ubigeoAjustado = ubigeo.toString().slice(0, -2) + "00";
      let geodata = am5.JSONParser.parse(result.response);

      // Si es nivel 2 (país específico), filtrar solo ese país
      if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
        const country = geodata.features.find((element: any) => element.id == ubigeoAjustado);
        geodata.features = country ? [country] : [];
      }

      // Formatear nombres a mayúsculas
      geodata.features.forEach((el: any) => (el.properties.name = el.properties.name.toUpperCase()));

      const data: any[] = [];

      // Aplicar datos según el nivel
      if (mapaCalor && mapaCalor.length > 0) {
        for (let i = 0; i < geodata.features.length; i++) {
          const feature = geodata.features[i];
          let dataPoint: MapaCalor | undefined;
          
          if (tipoNivelAmbito === 0) {
            // Continentes - usar ubigeoNivel01
            dataPoint = mapaCalor.find((x) => x.ubigeoNivel01?.toString().padStart(6, "0") === feature.id?.toString().padStart(6, "0"));
          } else if (tipoNivelAmbito === 1) {
            // Países de un continente - usar ubigeoNivel02
            dataPoint = mapaCalor.find((x) => Number(x.ubigeoNivel02) === feature.id);
          } else if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
            // País específico - usar ubigeoNivel03
            dataPoint = mapaCalor.find((x) => x.ubigeoNivel03?.toString().padStart(6, "0") === feature.id?.toString().padStart(6, "0"));
          }

          if (dataPoint) {
            const percentage = dataPoint.porcentajeActasContabilizadas || 0;
            const currentColor = getDegradatedColorFromPercentage(percentage, {
              init: INITIAL_COLOR_PERCENTAGE_0,
              end: FINAL_COLOR_PERCENTAGE_100
            });

            let id: any = '';
            if (tipoNivelAmbito === 0) {
              id = feature.id;
            } else if (tipoNivelAmbito === 1) {
              id = dataPoint.ubigeoNivel02;
            } else if (tipoNivelAmbito === 2 || tipoNivelAmbito === 3) {
              id = dataPoint.ubigeoNivel03;
            }

            data.push({
              id: id?.toString().padStart(6, '0'),
              value: 0,
              polygonSettings: {
                fill: am5.color(currentColor),
              },
            });
          } else {
            data.push({
              id: feature.id?.toString().padStart(6, '0'),
              value: 0,
              polygonSettings: {
                fill: am5.color(INITIAL_COLOR_PERCENTAGE_0),
              },
            });
          }
        }
      }

      // Configurar interactividad y efectos visuales
      polygonSeries.mapPolygons.template.setAll({
        toggleKey: "active",
        interactive: true,
      });

      // Estado hover (color azul al pasar el mouse)
      polygonSeries.mapPolygons.template.states.create("hover", {
        fill: am5.color("#2A71B9"),
      });

      // Eventos hover - solo cambiar cursor si NO es el último nivel (país)
      if (tipoNivelAmbito < 2) {
        polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
          document.body.style.cursor = "pointer";
        });
        
        polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
          document.body.style.cursor = "default";
        });
      }

      // Eventos click - solo permitir navegación si NO es el último nivel (país)
      if (tipoNivelAmbito < 2) {
        polygonSeries.mapPolygons.template.events.on("click", (ev: any) => {
          const currentId = ev.target.dataItem?.get?.("id");
          const clickedId = currentId === undefined ? ev.target.dataItem?.dataContext?.ID : currentId;
          if (clickedId && eventClickMapaEmitter) {
            eventClickMapaEmitter.emit(String(clickedId));
          }
          document.body.style.cursor = "default";
        });
      }

      // Tooltip
      const tooltip = am5.Tooltip.new(root, {
        autoTextColor: false,
      });

      tooltip.get("background").setAll({
        fill: am5.color(0x003874),
      });

      polygonSeries.mapPolygons.template.setAll({
        tooltip: tooltip,
        tooltipText: "[fontFamily: NotoSans-regular][fontSize: 14px]{name}",
      });

      // Si es nivel 1, separar países con datos de países sin datos
      if (tipoNivelAmbito === 1) {
        const currentGeodata = { type: "FeatureCollection", features: [] as any[] };
        const currentGeodataDisabled = { type: "FeatureCollection", features: [] as any[] };
        const idxEnabled: number[] = [];

        mapaCalor.forEach((map) => {
          geodata.features.forEach((feature: any, index: number) => {
            if (feature.id == Number(map.ubigeoNivel02)) {
              currentGeodata.features.push(feature);
              idxEnabled.push(index);
            }
          });
        });

        geodata.features.forEach((feature: any, index: number) => {
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

      // Agregar polígono de bloqueo del mapa de Perú (solo en vista de continentes)
      if (tipoNivelAmbito === 0) {
        loadPolygonSeriesFromMapAndChartLockPeru(MapsUrl.allContinentsUltra1, chart, root);
      }
    });

    // Cambiar fondo a blanco usando DOM manipulation
    setTimeout(() => {
      const mapContainer = document.getElementById(root.dom.id);
      if (mapContainer) {
        mapContainer.style.backgroundColor = '#ffffff';
      }
    }, 100);

    // Cargar botones
    this.cargarBotonesContabilizadas(root, chart, tipoNivelAmbito);
  }

  /**
   * Carga los botones de navegación para mapas de contabilizadas
   */
  private cargarBotonesContabilizadas(root: am5.Root, chart: am5map.MapChart, tipoNivelAmbito: number = 0): void {
    const { zoomControl } = cargarBotones(root, chart);

    const iconStringWorld = "M7.629,15.258a7.428,7.428,0,0,1-2.975-.6A7.617,7.617,0,0,1,.6,10.6,7.428,7.428,0,0,1,0,7.629,7.428,7.428,0,0,1,.6,4.654,7.617,7.617,0,0,1,4.654.6,7.428,7.428,0,0,1,7.629,0,7.428,7.428,0,0,1,10.6.6a7.617,7.617,0,0,1,4.053,4.053,7.428,7.428,0,0,1,.6,2.975,7.428,7.428,0,0,1-.6,2.975A7.617,7.617,0,0,1,10.6,14.657a7.428,7.428,0,0,1-2.975.6m-.82-1.163V12.531a1.471,1.471,0,0,1-1.125-.5,1.66,1.66,0,0,1-.458-1.163v-.839l-3.91-3.91a5.363,5.363,0,0,0-.134.753,7.341,7.341,0,0,0-.038.753,6.384,6.384,0,0,0,1.612,4.33,6.15,6.15,0,0,0,4.053,2.136m5.607-2.06a6.306,6.306,0,0,0,.734-.973,6.791,6.791,0,0,0,.534-1.078,6.107,6.107,0,0,0,.324-1.154,6.875,6.875,0,0,0,.1-1.2,6.539,6.539,0,0,0-4.063-6.084v.343a1.66,1.66,0,0,1-.458,1.163,1.471,1.471,0,0,1-1.125.5H6.809V5.207a.659.659,0,0,1-.257.534.9.9,0,0,1-.582.21H4.387V7.629H9.308a.669.669,0,0,1,.534.248.859.859,0,0,1,.21.572v2.422h.82a1.545,1.545,0,0,1,.973.324,1.615,1.615,0,0,1,.572.839";
    
    const iconStringPeru = "M18.1,23.9c-1-.4-1.4-1.5-2.4-1.9-1.9-.9-4-1.7-5.1-3.6,0-.1-.1-.3,0-.4.2-.6-.6-1.2-.8-1.8-.8-1-1.4-2.1-1.9-3.3-1-1.6-1.6-3.9-3.5-4.6-.2,0-.5-.1-.5-.5.5-.3.5-.4,0-.8,0,0,0-.2,0-.2.1-.4-.6-.9,0-1.3,1.1-1.5,1.8-.6.9-.2,0,.2,0,.4,0,.6,1.3-.6,1.5,1.5,2.1.3.5-.4.4-1.1.6-1.6,0,0,.2.1.3,0,.6-1.2,2.3-.9,3-2.1.4-.3.4-.9.7-1.2.2-.3-.2-.8-.4-1.1.7-.2,1.4.2,1.7.9.2.6,1,.5,1.1,1.1,0,0,.1.2.2.2.7.2-.3,1.1,1.3.8.5,0,.7-.5,1.2-.2.3.1.7-.1,1,0,.2.2.8.3.7.6-.1.3-1.2,1.6-.3,1.2.2.2.9,1,0,.6-.4-.3-.7.1-1,.2-.9,0-2.2.5-2.5,1.4,0,.4-.4.6-.2.9,0,.2,0,.3-.1.4,0,0,0,0,0,0-.6.1-.5.8-.8,1.2.3.3.3.7.6,1.1.3.4,1,.8.4,1.4.6,0,1.2,0,1.3.7.7.2,1.5,0,1.9-.6.4-.1,0,1.8.2,1.7.4.6,1-.4,1.4.2.4,1,1.5,1.9.7,2.8-.2.4-.2.9,0,1.3-.1.4-.9.8-.5,1.2-.6,2.6,1.6.5-.4,2.6-.3.1,0,.3,0,.6-.7.4,0,1-1,1";

    // Lógica para determinar qué botón mostrar:
    // - Si está en nivel 0 (nacional) y NO es extranjero -> mostrar botón mundo (extranjero)
    // - Si está en nivel 0 (continentes) y ES extranjero -> mostrar botón Perú
    // - Si está en nivel >= 1 (departamento/provincia) -> SIEMPRE mostrar botón para regresar a nivel superior
    //   - Si es nacional (Perú), mostrar botón Perú para regresar a mapa nacional
    //   - Si es extranjero, mostrar botón Perú para regresar a continentes
    let iconSeleccionado: string;
    
    if (tipoNivelAmbito === 0) {
      // Nivel nacional/continentes - mostrar botón opuesto
      iconSeleccionado = !this.esEstranjero ? iconStringWorld : iconStringPeru;
    } else {
      // Nivel departamento/provincia - siempre mostrar botón Perú para regresar
      iconSeleccionado = iconStringPeru;
    }

    const homeIconButton = zoomControl.children.moveValue(
      am5.Button.new(root, {
        paddingTop: iconSeleccionado === iconStringPeru ? 8 : 10,
        paddingBottom: iconSeleccionado === iconStringPeru ? 8 : 10,
        width: 36,
        height: 36,
        icon: am5.Graphics.new(root, {
          svgPath: iconSeleccionado,
          fill: am5.color(0x003874),
          scale: iconSeleccionado === iconStringPeru ? 0.825 : 1.125,
        }),
      }),
      0
    );

    homeIconButton.get("background").setAll({
      fill: am5.color(0xffffff),
      fillOpacity: 0.8,
      stroke: am5.color(0x003874),
    });

    homeIconButton.get("background").states.create("hover", {}).setAll({
      fill: am5.color(0xbcd1e6),
      fillOpacity: 1,
    });

    homeIconButton.get("background").states.create("down", {}).setAll({
      fill: am5.color(0x6DB2E2),
      fillOpacity: 1,
    });

    homeIconButton.set("cursorOverStyle", "pointer");
    homeIconButton.events.on("click", () => {
      this.lockScrollPositionTemporarily(BUTTON_SEQUENCE_SCROLL_LOCK_MS);

      // Lógica de navegación:
      // - Si está en nivel 0 (nacional/continentes) y es Perú -> ir a extranjero (ámbito 2)
      // - Si está en nivel 0 (continentes) y es extranjero -> ir a Perú (ámbito 1)
      // - Si está en nivel >= 1 (departamento/provincia) -> SIEMPRE regresar a Perú nacional (ámbito 1)
      let newAmbito: number;
      
      if (tipoNivelAmbito === 0) {
        // Nivel nacional/continentes - cambiar entre Perú y extranjero
        newAmbito = !this.esEstranjero ? 2 : 1;
      } else {
        // Nivel departamento/provincia - siempre regresar a Perú nacional
        newAmbito = 1;
      }
      
      this.bottonHome.emit(newAmbito);
      document.body.style.cursor = "default";
    });
  }

  /**
   * Carga mapa para revocatorias con niveles de ubigeo dinámicos
   * Implementación basada en ActasSharedMapaCalorComponent
   */
  private loadMapRevocatorias(): void {
    const mapContainerId = this.mapContainerId || 'chartdiv';
    
    this.destroyChartHelper(mapContainerId);
    if (am5.registry.licenses.length > 0) {
      am5.addLicense("AM5C357384425");
    }

    // Crear root y chart
    const root = am5.Root.new(mapContainerId);
    root.setThemes([am5themes_Animated.new(root)]);
    
    if (root._logo) {
      root._logo.dispose();
    }

    const chart = root.container.children.push(
      am5map.MapChart.new(root, {
        wheelY: "none",
        wheelX: "none",
        paddingBottom: LEGEND_HEIGHT,
      })
    );

    // Determinar ruta del mapa según nivel
    let rutaMapa = this.obtenerRutaMapaRevocatorias();

    // Cargar JSON del mapa (esto requeriría ActasService, por ahora usamos fetch)
    fetch(rutaMapa)
      .then(response => response.json())
      .then((geodata: any) => {
        // Filtrar distrito si es necesario
        if (this.nivelUbigeo === 3 && this.codigoUbigeo) {
          const feature = geodata.features.find((f: any) => f.properties.ID === this.codigoUbigeo);
          geodata.features = [feature];
        }

        // Crear polígonos
        const polygonSeries = this.crearPoligonosBase(chart, root, geodata);
        
        if (this.mapaCalor) {
          this.crearPoligonosActivos(geodata, polygonSeries, chart, root);
        }

        chart.appear(1000, 100);
        this.setLoadingState(false);
      })
      .catch(error => {
        console.error("Error loading map for revocatorias:", error);
        this.setLoadingState(false);
      });
  }

  /**
   * Obtiene la ruta del mapa según el nivel de ubigeo para revocatorias
   */
  private obtenerRutaMapaRevocatorias(): string {
    const baseUrl = './assets/lib/amcharts5/geodata/json';
    
    switch (this.nivelUbigeo) {
      case 1: // Perú completo
        return `${baseUrl}/peruLow.json`;
      case 2: // Departamento
        return `${baseUrl}/departamentos/${this.codigoUbigeo}.json`;
      case 3: // Provincia
        const dpto = this.codigoUbigeo?.slice(0, -2) + '00';
        return `${baseUrl}/provincias/${dpto}.json`;
      default:
        return `${baseUrl}/peruLow.json`;
    }
  }

  /**
   * Crea los polígonos base para mapas de revocatorias
   */
  private crearPoligonosBase(chart: am5map.MapChart, root: am5.Root, geodata: any): am5map.MapPolygonSeries {
    const polygonSeries = chart.series.push(
      am5map.MapPolygonSeries.new(root, {
        geoJSON: geodata,
      })
    );

    polygonSeries.mapPolygons.template.setAll({
      tooltipText: "[fontFamily: NotoSans-regular][fontSize: 14px]{name}",
      fill: am5.color(0xc3c3c3),
      stroke: am5.color(0xffffff),
      strokeWidth: 1,
    });

    return polygonSeries;
  }

  /**
   * Crea polígonos activos con datos de mapa de calor
   */
  private crearPoligonosActivos(geodata: any, polygonSeries: am5map.MapPolygonSeries, chart: am5map.MapChart, root: am5.Root): void {
    // Implementación simplificada - requiere mapear mapaDeCalorData
    // con los polígonos de geodata
    
    polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {
      document.body.style.cursor = "pointer";
    });

    polygonSeries.mapPolygons.template.events.on("pointerout", function (ev) {
      document.body.style.cursor = "default";
    });

    polygonSeries.mapPolygons.template.events.on("click", (ev) => {
      const ubigeo = (ev.target.dataItem.dataContext as any).ID;
      this.cambioUbigeoEvent.emit(ubigeo);
    });
  }

  /**
   * Helper para destruir chart específico
   */
  private destroyChartHelper(divId: string): void {
    destroyChart(divId);
  }

  // ============================================================================
  // FIN DE MÉTODOS ESPECÍFICOS
  // ============================================================================

  private destroyMap(): void {
    if (this.rootMap) {
      this.rootMap.dispose();
      this.rootMap = undefined;
    }
    // Dispose any orphaned roots on the same container that might have been
    // created outside this component instance (e.g. after a route change).
    try {
      const orphans = am5.registry.rootElements.filter(
        (r: am5.Root) => r?.dom?.id === this.mapContainerId
      );
      orphans.forEach((r: am5.Root) => r.dispose());
    } catch (_) { /* silent – registry may already be clean */ }
  }

  /**
   * Actualiza el estado de carga del mapa
   * @param loading - true si está cargando, false cuando termina
   */
  private setLoadingState(loading: boolean): void {
    if (this.showLoadingState) {
      this.isLoading = loading;
    }
  }

  /**
   * Marca el mapa como completamente inicializado y cargado
   */
  private markMapAsReady(): void {
    this.isInitialized = true;
    this.setLoadingState(false);
  }
}
