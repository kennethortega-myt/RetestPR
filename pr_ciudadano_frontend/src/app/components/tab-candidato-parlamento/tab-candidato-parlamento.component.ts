import { Component, EventEmitter, HostListener, Input, Output, ViewChild } from '@angular/core';
import { take } from 'rxjs';
import { CANTIDAD_LIMITE_CANDIDATOS, UBIGEO_LEVELS } from '../../helpers/constantes';
import { getGeograpScopeByRegion } from '../../helpers/election-type.config';
import { makeFormattedInformationForBars2, makeScaleValues } from '../../helpers/handler-chart-data.common';
import { commonUpdateMapFromFilter } from '../../helpers/map.update-map-from-filter';
import { getFilterTypeForBackend } from '../../helpers/ubigeo-level.common';
import { IChartBarInfo } from '../../interfaces/chart-bar-info.interface';
import {
  FilterByLocationParams,
  GenericFilterParams,
  REGION_EXTRAJERO,
  REGION_PERU,
  RegionValue,
  getOptimizedObject
} from '../../interfaces/filtro-settings';
import {
  GeographicalLocationItem,
  GeographicalLocationNameItem,
  GeographicalLocationParams
} from '../../interfaces/presidenciales.interfaces';
import { ParlamentoService } from '../../services/elecciones-generales/parlamento.service';
import { GenericFilterUbigeoComponent } from '../generic-filter-ubigeo/generic-filter-ubigeo.component';
import { MainHotMapComponent } from '../main-hot-map/main-hot-map.component';

@Component({
  selector: 'app-tab-candidato-parlamento',
  templateUrl: './tab-candidato-parlamento.component.html',
  styleUrls: ['./tab-candidato-parlamento.component.scss'],
  standalone: false
})
export class TabCandidatoParlamentoComponent {
  @Output() regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();
  @Input() electionId: number;
  @Input() parlamentoService: ParlamentoService;
  @ViewChild(MainHotMapComponent) mainHotMapComponent: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  GraficoKey = 'tab-candidato.grafico';
  geographicalLocationItems: GeographicalLocationItem[] = [];
  geographicalLocationNameItems: GeographicalLocationNameItem[] = [];
  scalesForDistrict: number[] = [];
  dataForDistrict: IChartBarInfo[] = [];
  showChartAndFilters = false;
  showOnlyChart = false;
  isWorldRequest = true;
  showGraficBar = false;
  regionValue: RegionValue = 'TODOS';
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  private filters: FilterByLocationParams = {} as FilterByLocationParams;

  constructor() {}

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;

    if (!this.esPantallaChica) {
      this.mostrarMapa2 = true;
    } else {
      this.mostrarMapa2 = false;
    }
  }

  get cantidadLimiteCandidatos(){
    return CANTIDAD_LIMITE_CANDIDATOS.PARLAMENTO;
  }

  ngOnInit(): void {
    this.loadInitialParticipantsByGeographicalLocation();
  }

  toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  filterParticipantsByGeographicalLocation(params: FilterByLocationParams) {
    this.filters = params;
    this.filterByLocationParamsEvent.emit(params);

    const currentParams1 = {
      idEleccion: this.electionId,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      tipoFiltro: getFilterTypeForBackend(params),
      ubigeoNivel1: params.departmentUbigeoId,
      ubigeoNivel2: params.provinceUbigeoId,
      ubigeoNivel3: params.districtUbigeoId
    } as GeographicalLocationParams;
    this.loadParticipantsByGeographicalLocation(currentParams1);

    this.updateMapFromFilter();
  }

  regionChanged($event: RegionValue) {
    this.filters = {} as FilterByLocationParams;
    this.regionValue = $event;
    if ($event == REGION_PERU) {
      this.mainHotMapComponent.loadInitialUbigeoPeru();
      this.loadInitialParticipantsByGeographicalLocationByRegion();
    } else if ($event == REGION_EXTRAJERO) {
      this.mainHotMapComponent.loadInitialUbigeoInternational();
      this.loadInitialParticipantsByGeographicalLocationByRegion();
    } else {
      this.mainHotMapComponent.loadInitialUbigeoWorld();
      this.loadInitialParticipantsByGeographicalLocation();
    }

    this.regionChangedEvent.emit($event);
  }

  cleanInformation($event: RegionValue) {
    this.regionValue = $event;
    this.filters = {} as FilterByLocationParams;

    // actualizar
    if ($event == 'PERÚ') {
      this.loadInitialParticipantsByGeographicalLocationByRegion();
      this.mainHotMapComponent.loadInitialUbigeoPeru();
    } else if ($event == 'EXTRANJERO') {
      this.loadInitialParticipantsByGeographicalLocationByRegion();
      this.mainHotMapComponent.loadInitialUbigeoInternational();
    } else {
      this.loadInitialParticipantsByGeographicalLocation();
      this.mainHotMapComponent.loadInitialUbigeoWorld();
    }
  }

  get filteredDataForDistrict(): IChartBarInfo[] {
    return this.dataForDistrict.filter((_) => _);
  }

  changeRegionFromMap($event: RegionValue) {
    this.regionChanged($event);
    this.mainFiltroUbigeoComponent.setRegion($event);
    // Sync the responsive breadcrumb when the region changes from the map
    let idAmbitoGeografico: number | null = null;
    if ($event === 'PERÚ') { idAmbitoGeografico = 1; }
    else if ($event === 'EXTRANJERO') { idAmbitoGeografico = 2; }
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal(
      getOptimizedObject({ idAmbitoGeografico } as GenericFilterParams)
    );
  }

  ubigeoParamsChangedFromMap($event: FilterByLocationParams) {
    this.filterByLocationParamsEvent.emit($event);
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId, regionString } = $event;

    if (regionString) {
      this.regionValue = regionString;
    }

    const setUbigeoParamsMethod = regionString === 'EXTRANJERO' ? 'setUbigeoParamsExtrangero' : 'setUbigeoParams';

    this.mainFiltroUbigeoComponent[setUbigeoParamsMethod]($event);

    const levels = [
      { id: districtUbigeoId, level: UBIGEO_LEVELS.LEVEL_03, key: 'districtUbigeoId' },
      { id: provinceUbigeoId, level: UBIGEO_LEVELS.LEVEL_02, key: 'provinceUbigeoId' },
      { id: departmentUbigeoId, level: UBIGEO_LEVELS.LEVEL_01, key: 'departmentUbigeoId' }
    ];

    for (const { id, level, key } of levels) {
      if (id) {
        this.filters = { ...this.filters, [key]: id } as FilterByLocationParams;

        const currentParams: GeographicalLocationParams = {
          idEleccion: this.electionId,
          idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
          tipoFiltro: level,
          ubigeoNivel1: this.filters.departmentUbigeoId || '',
          ubigeoNivel2: this.filters.provinceUbigeoId || '',
          ubigeoNivel3: this.filters.districtUbigeoId || ''
        };

        this.loadParticipantsByGeographicalLocation(currentParams);

        // Update the responsive breadcrumb after a map navigation event
        const breadcrumbParams = getOptimizedObject({
          tipoFiltro: level,
          idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
          ubigeoNivel1: this.filters.departmentUbigeoId,
          ubigeoNivel2: this.filters.provinceUbigeoId,
          ubigeoNivel3: this.filters.districtUbigeoId,
        } as GenericFilterParams);
        this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal(breadcrumbParams);
        this.updateFiltersFromNewEvent.emit(breadcrumbParams);

        return;
      }
    }
  }

  applyFiltersEvent(params: GenericFilterParams) {
    const customParamsGeographical = { ...params, idEleccion: this.electionId };
    this.loadParticipantsByGeographicalLocation(customParamsGeographical as GeographicalLocationParams);
    this.updateFiltersFromNewEvent.emit(params);

    if (params.idAmbitoGeografico) {
      this.regionValue = params.idAmbitoGeografico == 1 ? 'PERÚ' : 'EXTRANJERO';
    } else {
      this.regionValue = 'TODOS';
    }
    this.filters = {
      region: params.idAmbitoGeografico,
      departmentUbigeoId: params.ubigeoNivel1,
      provinceUbigeoId: params.ubigeoNivel2,
      districtUbigeoId: params.ubigeoNivel3
    };
    this.filters = getOptimizedObject<FilterByLocationParams>(this.filters);
    this.updateMapFromFilter();
    this.updateFiltersFromNewEvent.emit(params);
  }

  private showMap(): void {
    setTimeout(() => {
      if (this.regionValue === 'TODOS') {
        this.loadInitialWorldMap();
      } else if (this.filters.departmentUbigeoId) {
        this.updateMapFromFilter();
      } else {
        this.loadInitialMapByRegion();
      }
    }, 100);
  }

  private loadInitialWorldMap(): void {
    this.mainHotMapComponent.loadInitialUbigeoWorld();
  }

  private loadInitialMapByRegion(): void {
    if (this.regionValue === 'EXTRANJERO') {
      this.mainHotMapComponent.loadInitialUbigeoInternational();
    } else {
      this.mainHotMapComponent.loadInitialUbigeoPeru();
    }
  }

  private updateMapFromFilter() {
    commonUpdateMapFromFilter(this.filters, this.mainHotMapComponent);
  }

  private loadInitialParticipantsByGeographicalLocation() {
    // Loading functionality removed
    this.isWorldRequest = true;
    const currentParams = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ELECTION
    } as GeographicalLocationParams;
    this.loadParticipantsByGeographicalLocation(currentParams);
  }

  private loadInitialParticipantsByGeographicalLocationByRegion() {
    const currentParams = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue)
    } as GeographicalLocationParams;
    this.loadParticipantsByGeographicalLocation(currentParams);
  }

  private loadParticipantsByGeographicalLocation(params: GeographicalLocationParams) {
    this.showGraficBar = false;
    this.parlamentoService
      .getParticipantsByCandidates$(params)
      .pipe(take(1))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.showGraficBar = true;
            this.isWorldRequest = false;
            this.showChartAndFilters = true;
            this.showOnlyChart = true;
            this.dataForDistrict = makeFormattedInformationForBars2(response.data);
            this.scalesForDistrict = makeScaleValues(response.data, 10);

            this.showMap();
          } else {
            if (this.isWorldRequest) {
              this.showChartAndFilters = false;
              this.showOnlyChart = true;
            } else {
              this.showOnlyChart = false;
              this.showChartAndFilters = true;
            }
          }
        },
        error: (error) => {
          console.error('Error fetching participants by geographical location:', error);
        },
        complete: () => {
          // Loading functionality removed
        }
      });
  }
}
