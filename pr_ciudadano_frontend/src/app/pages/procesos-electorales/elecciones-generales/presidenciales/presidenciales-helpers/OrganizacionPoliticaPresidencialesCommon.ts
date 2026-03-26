import { EventEmitter } from "@angular/core";
import { GenericFilterUbigeoComponent } from "../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component";
import { MainHotMapComponent } from "../../../../../components/main-hot-map/main-hot-map.component";
import { TooltipType } from "../../../../../components/main-hot-map/map-auxiliary-elements";
import {
  RegionValue,
  FilterByLocationParams,
  GenericFilterParams,
  getOptimizedObject,
} from "../../../../../interfaces/filtro-settings";
import {
  PoliticalOrganizationItem,
  PoliticalOrganizationParams,
} from "../../../../../interfaces/presidenciales.interfaces";
import { PresidencialesService } from "../../../../../services/elecciones-generales/presidenciales.service";
import { take } from "rxjs";
import { GEOGRAPHIC_SCOPE } from "../../../../../helpers/constantes";
import { getFilterTypeForBackend } from "../../../../../helpers/ubigeo-level.common";
import { getGeograpScopeByRegion } from "../../../../../helpers/election-type.config";
import { mapWithPoliticImage } from "../../../../../helpers/get-images.helper";

export class OrganizacionPoliticaPresidencialesCommon {
  // this attributes will be ViewChild
  mainHotMapComponent: MainHotMapComponent;
  mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;

  // this attributes will be Output
  regionChangedEvent = new EventEmitter<RegionValue>();
  filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();

  // this attribute will be Input
  electionID: number;

  // normal attributes
  public policitalOrganizationItems: PoliticalOrganizationItem[] = [];
  public policitalOrganizationItemsPaginated: PoliticalOrganizationItem[] = [];
  public selectedCandidato: PoliticalOrganizationItem = {} as PoliticalOrganizationItem;
  public tooltipType: TooltipType = "tooltip_01";
  public showOnpePersonToInform = true;

  public selectedFilterParams: FilterByLocationParams = {} as FilterByLocationParams;
  public regionValue: RegionValue = "PERÚ";

  public numberByPage = 15;

  public showMap = false;
  public isFinishFirstLoading = false;

  constructor(public readonly presidencialesService: PresidencialesService) {}

  public cleanInformation($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilterParams = {} as FilterByLocationParams;

    if (this.regionValue == "PERÚ") {
      this.loadUbigeoMapForPeru();
    } else {
      this.loadUbigeoMapForInternational();
    }
  }

  public selectThisCandidate(candidato: PoliticalOrganizationItem): void {
    this.regionValue = "PERÚ";
    this.selectedFilterParams = {} as FilterByLocationParams;
    // Loading functionality removed
    this.showMap = false;
    this.showOnpePersonToInform = false;

    setTimeout(() => {
      // Loading functionality removed
      this.selectedCandidato = candidato;
      this.showMap = true;
      this.regionChangedEvent.emit(this.regionValue);
      if (this.regionValue == "PERÚ") {
        this.loadUbigeoMapForPeru(this.selectedCandidato);
      } else {
        this.updateMapFromFilter(this.selectedCandidato);
      }
    }, 200);
  }

  public regionChanged($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilterParams = {} as FilterByLocationParams;

    if (this.regionValue == "PERÚ") {
      this.loadUbigeoMapForPeru();
    } else {
      this.loadUbigeoMapForInternational();
    }

    this.regionChangedEvent.emit($event);
  }

  public changeRegionFromMap($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilterParams = {} as FilterByLocationParams;
    this.mainFiltroUbigeoComponent.ubigeoForm.controls.region.setValue($event, { emitEvent: false });
    this.mainFiltroUbigeoComponent.regionChanged();
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal({
      idAmbitoGeografico: getGeograpScopeByRegion($event)
    });
    this.regionChangedEvent.emit($event);
  }

  public filterParticipantsByGeographicalLocation(params: FilterByLocationParams) {
    this.selectedFilterParams = getOptimizedObject(params);

    this.filterByLocationParamsEvent.emit(params);
    this.updateMapFromFilter();
  }

  // TODO: AQUÍ SE LLAMA PARA LA ACTUALIZACIÓN
  public applyFiltersEvent(params: GenericFilterParams) {
    this.updateFiltersFromNewEvent.emit(params);

    if (params.idAmbitoGeografico) {
      this.regionValue = params.idAmbitoGeografico == 1 ? "PERÚ" : "EXTRANJERO";
    } else {
      this.regionValue = "TODOS";
    }
    this.selectedFilterParams = {
      region: params.idAmbitoGeografico,
      departmentUbigeoId: params.ubigeoNivel1,
      provinceUbigeoId: params.ubigeoNivel2,
      districtUbigeoId: params.ubigeoNivel3,
    };
    this.selectedFilterParams = getOptimizedObject<FilterByLocationParams>(this.selectedFilterParams);
    this.updateMapFromFilter(this.selectedCandidato);
  }

  public loadPage($event: number) {
    this.policitalOrganizationItemsPaginated = this.getPolicitalOrganizationItemsPaginated(
      this.policitalOrganizationItems,
      $event
    );
  }

  public initialLoad(value?: string) {    
    let paramsToSend = {... this.defaultParams}

    if(value === "pres"){
      delete paramsToSend.idAmbitoGeografico;
      paramsToSend.tipoFiltro = "eleccion";
    }

    this.loadParticipantsByPoliticalOrganization(paramsToSend);
  }

  public ubigeoParamsChangedFromMapWithSpecialCase($event: FilterByLocationParams) {
    const { regionString } = $event;
    this.selectedFilterParams = getOptimizedObject($event);

    this.filterByLocationParamsEvent.emit($event);

    if (regionString) {
      this.regionValue = regionString;
    }
    if (regionString == "EXTRANJERO") {
      this.mainFiltroUbigeoComponent.setUbigeoParamsExtrangero($event);
    } else {
      this.mainFiltroUbigeoComponent.setUbigeoParams($event);
    }
  }

  // PRIVATE METHODS

  private get defaultParams(): PoliticalOrganizationParams {
    return {
      idEleccion: this.electionID,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
      tipoFiltro: getFilterTypeForBackend(),
      ubigeoNivel1: "",
      ubigeoNivel2: "",
      ubigeoNivel3: "",
    } as PoliticalOrganizationParams;
  }

  private loadUbigeoMapForInternational(candidato?: PoliticalOrganizationItem) {
    setTimeout(() => {
      const codigoAgrupacionPolitica = this.selectedCandidato?.codigoAgrupacionPolitica?.toString();
      this.mainHotMapComponent.loadInitialUbigeoInternational(codigoAgrupacionPolitica, candidato);
    }, 200);
  }

  private loadUbigeoMapForPeru(candidato?: PoliticalOrganizationItem) {
    setTimeout(() => {
      this.tooltipType = "tooltip_01";
      const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = this.selectedFilterParams;
      const codigoAgrupacionPolitica = this.selectedCandidato.codigoAgrupacionPolitica.toString();
      if (districtUbigeoId) {
        this.mainHotMapComponent.loadUbigeoDistrict(
          departmentUbigeoId,
          provinceUbigeoId,
          districtUbigeoId,
          codigoAgrupacionPolitica,
          candidato
        );
        return;
      }
      if (provinceUbigeoId) {
        this.mainHotMapComponent.loadUbigeoProvince(departmentUbigeoId, provinceUbigeoId, codigoAgrupacionPolitica, candidato);
        return;
      }
      if (departmentUbigeoId) {
        this.mainHotMapComponent.loadUbigeoDepartamento(departmentUbigeoId, codigoAgrupacionPolitica, candidato);
        return;
      }
      this.mainHotMapComponent.loadInitialUbigeoPeru(codigoAgrupacionPolitica, candidato);
    }, 200);
  }

  private updateMapFromFilter(candidato?: PoliticalOrganizationItem) {
    const { region, departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = this.selectedFilterParams;
    const codigoAgrupacionPolitica = this.selectedCandidato.codigoAgrupacionPolitica.toString();
    if (districtUbigeoId) {
      if (region == 1) {
        this.mainHotMapComponent.loadUbigeoDistrict(
          departmentUbigeoId,
          provinceUbigeoId,
          districtUbigeoId,
          codigoAgrupacionPolitica,
          candidato
        );
      } else if (region == 2) {
        this.mainHotMapComponent.loadUbigeoState(
          departmentUbigeoId,
          provinceUbigeoId,
          districtUbigeoId,
          codigoAgrupacionPolitica,
          candidato
        );
      }
      return;
    }
    if (provinceUbigeoId) {
      if (region == 1) {
        this.mainHotMapComponent.loadUbigeoProvince(departmentUbigeoId, provinceUbigeoId, codigoAgrupacionPolitica, candidato);
      } else if (region == 2) {
        this.mainHotMapComponent.loadUbigeoCountry(departmentUbigeoId, provinceUbigeoId, codigoAgrupacionPolitica, candidato);
      }
      return;
    }
    if (departmentUbigeoId) {
      if (region == 1) {
        this.mainHotMapComponent.loadUbigeoDepartamento(departmentUbigeoId, codigoAgrupacionPolitica, candidato);
      } else if (region == 2) {
        this.mainHotMapComponent.loadUbigeoContinent(departmentUbigeoId, codigoAgrupacionPolitica, candidato);
      }
      return;
    }

    if (region == 1) {
      this.mainHotMapComponent?.loadInitialUbigeoPeru(codigoAgrupacionPolitica, candidato);
      return;
    } else if (region == 2) {
      this.mainHotMapComponent.loadInitialUbigeoInternational(codigoAgrupacionPolitica, candidato);
      return;
    }

    this.mainHotMapComponent.loadInitialUbigeoInternational(codigoAgrupacionPolitica);
  }

  private loadParticipantsByPoliticalOrganization(params: PoliticalOrganizationParams) {
    this.presidencialesService
      .getParticipantsByPoliticalOrganization$(params)
      .pipe(take(1))
      .subscribe((response) => {
        this.isFinishFirstLoading = true;
        // Loading functionality removed
        if (response.success) {
          this.policitalOrganizationItems = response.data;
          this.loadPage(1);
        } else {
          console.log('getParticipantsByGeographicalLocation error');
        }
      });
  }

  private getPolicitalOrganizationItemsPaginated(
    policitalOrganizationItems: PoliticalOrganizationItem[],
    $event: number
  ) {  
    this.policitalOrganizationItemsPaginated = mapWithPoliticImage(
      policitalOrganizationItems.filter((_, index) => {
        return index < $event * this.numberByPage && index >= ($event - 1) * this.numberByPage;
      })
    );
    return this.policitalOrganizationItemsPaginated;
  }
}
