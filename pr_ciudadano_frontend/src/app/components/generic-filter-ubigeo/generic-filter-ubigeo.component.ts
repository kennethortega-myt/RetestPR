import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { Subscription } from 'rxjs';
import { getGenericFilterType, getGenericGeographicalScope } from '../../helpers/filters-helper.common';
import {
  getBreadcrumbForSelectedFilters,
  getElectoralDistrictForm,
  getElectoralRevocatoriaForm,
  getIDataForBreadcrumb,
  getUbigeoForm
} from '../../helpers/filtro-ubigeos-helpers/form-ubigeos.helper';
import {
  Department,
  District,
  IBaseFiltroUbigeo,
  IElectionType,
  IFiltroUbigeo,
  InternationalUbigeo,
  LocalVotacion,
  Province,
  Region
} from '../../interfaces/elections.interfaces';
import {
  FilterByLocationParams,
  FilterFunctionality,
  FilterParamsWithLists,
  GenericFilterParams,
  getOptimizedObject,
  getOptimizedObjectRevoca,
  REGION_EXTRAJERO,
  REGION_PERU,
  REGION_PERU_METROPOLITAN_LIMA,
  REGION_TODOS,
  RegionValue,
  SelectedFilters
} from '../../interfaces/filtro-settings';
import { IDataForPopup } from '../../interfaces/popup-filter-ubigeos.interface';
import { PopupFilterUbigeosService } from '../../services/common/popup-filter-ubigeos.service';
import { ElectionsService } from '../../services/elecciones-generales/elections.service';
import { PopupFiltroUbigeoComponent } from '../popup-filtro-ubigeo/popup-filtro-ubigeo.component';
import { GEOGRAPHIC_SCOPE, UBIGEO_LEVELS } from '../../helpers/constantes';
import { GENERAL_BREAKPOINTS } from '../../helpers/responsive-dimentions.helper';
import { isRevocatoria } from '../../helpers/storage-helpers/encrypt-storage.helper';
import { getUbigeoLevel01FromLevel02, getUbigeoLevel02FromLevel03 } from '../../helpers/ubigeo-level.common';
import { BaseUbigeoService } from '../../services/common/base-ubigeo.service';

@Component({
  selector: 'app-generic-filter-ubigeo',
  templateUrl: './generic-filter-ubigeo.component.html',
  styleUrls: ['./generic-filter-ubigeo.component.scss'],
  standalone: false
})
export class GenericFilterUbigeoComponent implements IFiltroUbigeo, OnInit, OnDestroy {
  @Input() electionId: number = 10;
  @Input() ubigeoInitialValue: number = 0;
  @Input() showSelectLocationUbigeo: boolean = false;
  @Input() filterFunctionality: FilterFunctionality = 'peru_intern_and_all_join';
  @Input() showCleanButton: boolean = true;
  @Input() showLocales: boolean = true;
  @Input() isEven: boolean = false;
  @Input() electionType!: IElectionType;
  @Input() region: RegionValue;
  @Input() skipAutoLoadUbigeoLists = false;
  @Output() applyFiltersEvent = new EventEmitter<GenericFilterParams>();
  public Region = 'generic-filter-ubigeo.Region';
  public Provincia = 'generic-filter-ubigeo.Provincia';
  public Distrito = 'generic-filter-ubigeo.Distrito';
  public LocalVotacion = 'generic-filter-ubigeo.LocalVotacion';
  public Continente = 'generic-filter-ubigeo.Continente';
  public Pais = 'generic-filter-ubigeo.Pais';
  public Estado = 'generic-filter-ubigeo.Ciudad';
  public FiltrarMayusc = 'generic-filter-ubigeo.FiltrarMayusc';
  public Limpiar = 'generic-filter-ubigeo.Limpiar';
  public FiltrarMinusc = 'generic-filter-ubigeo.FiltrarMinusc';
  public Peru = 'generic-filter-ubigeo.Peru';
  // FORMULARIOS
  private formBuilder = inject(FormBuilder);
  public ubigeoForm = getUbigeoForm(this.formBuilder);
  public electoralDistrictForm = getElectoralDistrictForm(this.formBuilder);
  public electoralRevocatoriaForm = getElectoralRevocatoriaForm(this.formBuilder);
  // ATRIBUTOS PRIVADOS
  public selectedUbigeoFormValues: SelectedFilters = {
    region:
      this.filterFunctionality == 'peru_and_international' || this.filterFunctionality == 'only_peru'
        ? REGION_PERU
        : REGION_TODOS
  } as SelectedFilters;

  public appliedUbigeoFormValues: SelectedFilters = {
    region:
      this.filterFunctionality == 'peru_and_international' || this.filterFunctionality == 'only_peru'
        ? REGION_PERU
        : REGION_TODOS
  } as SelectedFilters;
  // ATRIBUTOS PÚBLICOS
  public listRegiones: string[] = [];
  public listDepartamento: Department[] = [];
  public listProvincia: Province[] = [];
  public listDistrito: District[] = [];
  public listLocales: LocalVotacion[] = [];
  public listContinentals: InternationalUbigeo[] = [];
  public listCountries: InternationalUbigeo[] = [];
  public listStates: InternationalUbigeo[] = [];
  public regiones: Region[] = [];
  public showUbigeoPeru = false;
  public showUbigeoExtranjero = false;
  public filterButtonIsDisabled = false;
  public filterButton2IsDisabled = false;
  public cleanButtonIsDisabled = true;
  public breadcrumbString = 'Todos';
  public subscriptions: Subscription[] = [];
  public isResponsive = false;
  public originalInformationInstance = {} as IBaseFiltroUbigeo;
  public popupInformationInstance = {} as IBaseFiltroUbigeo;
  public responsiveUbigeoParams = {} as GenericFilterParams;

  // Estado inicial para determinar cuándo mostrar el botón limpiar
  public initialRegionValue: RegionValue = null;
  public initialElectoralDistrictId: any = null;
  public initialElectoralDistrictIdSet = false;

  constructor(
    private readonly _bottomSheet: MatBottomSheet,
    public readonly electionsService: ElectionsService,
    private readonly popupFilterUbigeosService: PopupFilterUbigeosService,
    private readonly breakpointObserver: BreakpointObserver,
    private readonly baseUbigeoService: BaseUbigeoService 
  ) {
    // START - ALL THIS CODE IS ONLY FOR RESPONSIVE
  }

  ngOnInit(): void {
    const breakpointSubs$ = this.breakpointObserver.observe(GENERAL_BREAKPOINTS).subscribe((result) => {
      // controla el coomponente segun layout mobile o desktop
      this.isResponsive = result.matches;
    });

    const subs01$ = this.popupFilterUbigeosService.applyFilters$.subscribe((filterParams) => {
      //emitir filtrado desde popup
      this.electoralRevocatoriaForm.controls.region.setValue(filterParams.ubigeoNivel3, { emitEvent: false });
      let electoralDistrictId = filterParams?.electoralDistrictId != null ? String(filterParams.electoralDistrictId) : null;
      this.electoralRevocatoriaForm.controls.location.setValue(electoralDistrictId, { emitEvent: false });

      this.applyFiltersEvent.emit(filterParams);
    });

    const subs02$ = this.popupFilterUbigeosService.applyBreadcrumb$.subscribe((breadcrumbString) => {
      // actualiza la etiqueta del label de los inputs
      this.breadcrumbString = breadcrumbString;
    });

    const subs03$ = this.popupFilterUbigeosService.applyFiltersInformation$.subscribe((ubigeosLists) => {
      // la data se mantiene persistente luego de un filtro
      this.popupInformationInstance = {
        ...this.popupInformationInstance,
        listDepartamento: ubigeosLists.listDepartamento,
        listProvincia: ubigeosLists.listProvincia,
        listDistrito: ubigeosLists.listDistrito,
        listContinentals: ubigeosLists.listContinentals,
        listCountries: ubigeosLists.listCountries,
        listStates: ubigeosLists.listStates,
        listLocales: ubigeosLists.listLocales,
        regiones: ubigeosLists.regiones
      };
    });

    const sub4$ = this.popupFilterUbigeosService.applyUbigeos$.subscribe((ubigeos) => {
      this.responsiveUbigeoParams = ubigeos;
    });

    this.subscriptions.push(breakpointSubs$, subs01$, subs02$, subs03$, sub4$);
    // END - ALL THIS CODE IS ONLY FOR RESPONSIVE
    // THIS CONFIG IS FOR RESPONSIVE
    this.popupInformationInstance.electionId = this.electionId;

    this.originalInformationInstance = { ...this.popupInformationInstance };

    // THIS CONFIG IS FOR ALL
    this.electionsService.settingsFilterFunctionality(this);
    this.applyFilterValidations();

    this.electoralRevocatoriaForm.get('location').valueChanges.subscribe({
      next: (value) => {
        this.selectedUbigeoFormValues.revocatoriaDistrictId = this.electoralDistrictForm.controls.region.value;
        if (value) {
          this.selectedUbigeoFormValues.electoralDistrictId = Number(value);
          this.validateIfHasChangedFilterValues();
          // this.filterButton2IsDisabled = false;
        }
        if (value === null) {
          if (this.selectedUbigeoFormValues.electoralDistrictId) {
            delete this.selectedUbigeoFormValues.electoralDistrictId;
            this.filterButton2IsDisabled = false;
          }
        }
      }
    });

    // Auto-seleccionar la región si se proporciona ambitInit
    if (this.region) {
      this.setRegion(this.region);
    }
    this.breadcrumbString = this.getRegionInitialValue();
    // Capturar el valor inicial de región para saber cuándo mostrar el botón limpiar
    this.initialRegionValue = this.getRegionInitialValue();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((elem) => elem.unsubscribe());
    this.cleanFormValues();
    this.cleanAllUbigeoLists();
  }

  // PUBLIC METHODS: para llamar desde fuera del componente

  public setRegion(region: RegionValue) {
    const current = this.ubigeoForm.controls.region.value;
    if (current === region) {
      return;
    }

    this.ubigeoForm.controls.region.setValue(region);
    this.appliedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value
    };
    this.regionChanged();
  }

  public syncRegionFromExternal(region: RegionValue): void {
    this.ubigeoForm.controls.region.setValue(region, { emitEvent: false });
    this.showUbigeoPeru = region === REGION_PERU;
    this.showUbigeoExtranjero = region === REGION_EXTRAJERO;
    
    this.listDepartamento = [];
    this.listProvincia = [];
    this.listDistrito = [];
    this.listLocales = [];
    this.listContinentals = [];
    this.listCountries = [];
    this.listStates = [];
    this.ubigeoForm.controls.department.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.province.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.district.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.location.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.continent.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.country.setValue('', { emitEvent: false });
    this.ubigeoForm.controls.state.setValue('', { emitEvent: false });

    this.selectedUbigeoFormValues = { region };
    this.appliedUbigeoFormValues = { region };
    this.applyFilterValidations();
  }

  public setElectoralRegion(regionId: number) {
    // Capturar el valor inicial del distrito electoral en la primera llamada
    if (!this.initialElectoralDistrictIdSet) {
      this.initialElectoralDistrictId = Number(regionId);
      this.initialElectoralDistrictIdSet = true;
    }
    this.electoralDistrictForm.controls.region.setValue(Number(regionId));
    this.applyFilterValidations();
  }

  public setElectoralRegionRevocatoria(regionId: string) {
    this.electoralRevocatoriaForm.controls.region.setValue(String(regionId));
    this.cleanButtonIsDisabled = false;
    this.applyFilterValidations();
  }

  public setUbigeoInitialParams(params: FilterByLocationParams) {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = params;

    if (departmentUbigeoId) {
      this.ubigeoForm.controls.department.setValue(departmentUbigeoId);
      this.appliedUbigeoFormValues = {
        region: this.ubigeoForm.controls.region.value,
        departmentUbigeoId: this.ubigeoForm.controls.department.value
      };
      this.departmentChanged();
    }
    if (provinceUbigeoId) {
      this.ubigeoForm.controls.province.setValue(provinceUbigeoId);
      this.appliedUbigeoFormValues = {
        region: this.ubigeoForm.controls.region.value,
        departmentUbigeoId: this.ubigeoForm.controls.department.value,
        provinceUbigeoId: this.ubigeoForm.controls.province.value
      };
      this.provinceChanged();
    }
    if (districtUbigeoId) {
      this.ubigeoForm.controls.district.setValue(districtUbigeoId);
      this.appliedUbigeoFormValues = {
        region: this.ubigeoForm.controls.region.value,
        departmentUbigeoId: this.ubigeoForm.controls.department.value,
        provinceUbigeoId: this.ubigeoForm.controls.province.value,
        districtUbigeoId: this.ubigeoForm.controls.district.value
      };
      this.districtChanged();
    }
  }

  public setUbigeoParams(params: FilterByLocationParams) {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = params;
    if (districtUbigeoId) {
      this.ubigeoForm.controls.district.setValue(districtUbigeoId);
      this.applyDistrictState();
      return;
    }
    if (provinceUbigeoId) {
      this.ubigeoForm.controls.province.setValue(provinceUbigeoId);
      this.applyProvinceState();
      return;
    }
    if (departmentUbigeoId) {
      this.ubigeoForm.controls.department.setValue(departmentUbigeoId);
      this.applyDepartmentState();
    }
  }

  public setUbigeoParamsExtrangero(params: FilterByLocationParams) {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId, regionString } = params;
    if (regionString && !departmentUbigeoId && !provinceUbigeoId && !districtUbigeoId) {
      this.ubigeoForm.controls.region.setValue(regionString);
      this.electionsService.loadContinentals(this);
      this.showUbigeoExtranjero = true;
    }
    if (departmentUbigeoId && !provinceUbigeoId && !districtUbigeoId) {
      this.ubigeoForm.controls.continent.setValue(departmentUbigeoId);
      this.applyContinentState();
    }
    if (provinceUbigeoId && !districtUbigeoId) {
      this.ubigeoForm.controls.country.setValue(String(provinceUbigeoId));
      this.applyCountryState();
    }
    if (districtUbigeoId) {
      this.ubigeoForm.controls.state.setValue(String(districtUbigeoId));
      this.applyForeignStateState();
    }
  }

  private applyDepartmentState() {
    this.listLocales = [];
    this.listDistrito = [];
    this.listProvincia = [];
    this.ubigeoForm.controls.province.setValue('');
    this.ubigeoForm.controls.district.setValue('');
    this.ubigeoForm.controls.location.setValue('');
    if (!this.skipAutoLoadUbigeoLists) {
      this.electionsService.loadProvinces(this);
    }
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  private applyProvinceState() {
    this.listLocales = [];
    this.listDistrito = [];
    this.ubigeoForm.controls.district.setValue('');
    this.ubigeoForm.controls.location.setValue('');
    if (!this.skipAutoLoadUbigeoLists) {
      this.electionsService.loadDistricts(this);
    }
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value,
      provinceUbigeoId: this.ubigeoForm.controls.province.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  private applyDistrictState() {
    this.listLocales = [];
    this.ubigeoForm.controls.location.setValue('');
    if (this.showSelectLocationUbigeo) {
      this.electionsService.loadLocals(this);
    }
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value,
      provinceUbigeoId: this.ubigeoForm.controls.province.value,
      districtUbigeoId: this.ubigeoForm.controls.district.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  private applyContinentState() {
    this.listCountries = [];
    this.listStates = [];
    this.ubigeoForm.controls.country.setValue('');
    this.ubigeoForm.controls.state.setValue('');
    if (!this.skipAutoLoadUbigeoLists) {
      this.electionsService.loadCountries(this);
    }
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  private applyCountryState() {
    this.listStates = [];
    this.ubigeoForm.controls.state.setValue('');
    if (!this.skipAutoLoadUbigeoLists) {
      this.electionsService.loadStates(this);
    }
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value,
      provinceUbigeoId: this.ubigeoForm.controls.country.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  private applyForeignStateState() {
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value,
      provinceUbigeoId: this.ubigeoForm.controls.country.value,
      districtUbigeoId: this.ubigeoForm.controls.state.value
    };
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
    this.applyFilterValidations();
  }

  // PUBLIC METHODS: para ser llamados desde el html de este componente

  public regionChanged($event?: Event) {
    const regionValue = this.ubigeoForm.controls.region.value;
    this.showUbigeoPeru = regionValue == REGION_PERU;
    this.showUbigeoExtranjero = regionValue == REGION_EXTRAJERO;

    this.cleanAllUbigeoLists();
    this.cleanFormValues();

    if (!this.skipAutoLoadUbigeoLists) {
      if (this.showUbigeoPeru) {
        this.electionsService.loadDepartments(this);
      } else if (this.showUbigeoExtranjero) {
        this.electionsService.loadContinentals(this);
      }
    }

    this.selectedUbigeoFormValues = {
      region: regionValue
    };
    this.applyFilterValidations();
    this.applyUbigeoFilters();
  }

  public departmentChanged($event?: Event) {
    this.applyDepartmentState();
    this.applyUbigeoFilters();
  }

  public provinceChanged($event?: Event) {
    this.applyProvinceState();
    this.applyUbigeoFilters();
  }

  public districtChanged($event?: Event) {
    this.applyDistrictState();
    this.applyUbigeoFilters();
  }

  public continentChanged($event?: Event) {
    this.applyContinentState();
    this.applyUbigeoFilters();
  }

  public countryChanged($event?: Event) {
    this.applyCountryState();
    this.applyUbigeoFilters();
  }

  public stateChanged($event?: Event) {
    this.applyForeignStateState();
    this.applyUbigeoFilters();
  }

  clearFilterNew(): void {
    this.cleanButtonIsDisabled = true;

    // Caso only_regiones: restaurar el distrito electoral inicial
    if (this.filterFunctionality === 'only_regiones' && this.initialElectoralDistrictId !== null) {
      this.electoralDistrictForm.controls.region.setValue(this.initialElectoralDistrictId);
      this.applyUbigeoFiltersEvent();
      return;
    }

    const regionInit: RegionValue = this.getRegionInitialValue();
    this.electoralRevocatoriaForm.controls.region.setValue(regionInit, { emitEvent: false });
    this.electoralRevocatoriaForm.controls.location.setValue('', { emitEvent: true });
    
    this.ubigeoForm.controls.region.setValue(regionInit);

    this.appliedUbigeoFormValues = {};
    this.regionChanged();
    this.applyUbigeoFiltersEvent();
    // Cambios para responsive
    if (
      isRevocatoria() ||
      this.filterFunctionality === 'peru_intern_and_all_join' ||
      this.filterFunctionality === 'peru_and_international'
    ) {
      this.breadcrumbString = this.getRegionInitialValue();
      this.popupInformationInstance = { ...this.originalInformationInstance };
      this.responsiveUbigeoParams = {};
    }
  }

  private getRegionInitialValue(): RegionValue {
    if (this.region) {
      return this.region;
    }
    switch (this.filterFunctionality) {
      case 'peru_and_international':
      case 'only_peru':
        this.region = REGION_PERU;
        this.ubigeoForm.controls.region.setValue(REGION_PERU);
        return REGION_PERU;
      case 'only_regiones':
        return REGION_PERU_METROPOLITAN_LIMA;
      default:
        return REGION_TODOS;
    }
  }

  /**
   * Método para aplicar filtros de ubigeo
   */
  public applyUbigeoFilters() {
    const regionValue = this.ubigeoForm.controls.region.value;
    this.filterButtonIsDisabled = true;

    if (regionValue == REGION_PERU) {
      const filters = {
        region: regionValue,
        departmentUbigeoId: this.ubigeoForm.controls.department.value,
        provinceUbigeoId: this.ubigeoForm.controls.province.value,
        districtUbigeoId: this.ubigeoForm.controls.district.value
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    } else if (regionValue == REGION_EXTRAJERO) {
      const filters = {
        region: regionValue,
        departmentUbigeoId: this.ubigeoForm.controls.continent.value,
        provinceUbigeoId: this.ubigeoForm.controls.country.value,
        districtUbigeoId: this.ubigeoForm.controls.state.value
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    } else {
      const filters = {
        region: regionValue
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    }
    this.applyUbigeoFiltersEvent();
  }

  /**
   * Método para aplicar filtros de distito electoral
   */

  public applyElectoralDistrictFilters() {
    this.electionsService.applyElectoralDistrictFilters(this, (_) => {
      this.applyUbigeoFiltersEvent();
    });
  }

  public applyElectoralDistrictFiltersForRevocatoria(defaultUbigeo: string) {
    this.electionsService.applyElectoralDistrictFilters(this, (_) => {
      this.settingBreadcrumbForRevocatoriaInResponsive(defaultUbigeo);
    });
  }

  public settingBreadcrumbForRevocatoriaInResponsive(defaultUbigeo: string) {
    const { regiones } = this;
    if (regiones && regiones.length > 0) {
      const selectedDefaultUbigeo = this.regiones.find((region) => Number(region.ubigeo) == Number(defaultUbigeo));
      if (selectedDefaultUbigeo) {
        const name = selectedDefaultUbigeo.nombre;
        this.breadcrumbString = name;
      }
    }
  }

  /**
   * Método que crea el formato correcto para mandar ejecutar los filtros
   */

  public applyUbigeoFiltersEvent() {
    const currentParams = this.buildCurrentParams();
    const listsParams = this.buildListsParams();

    const paramsOutput = {
      ...currentParams,
      ...listsParams
    };

    this.validateIfHasChangedFilterValues();
    this.applyFiltersEvent.emit(paramsOutput);
  }

  private buildCurrentParams(): GenericFilterParams {
    let currentParams: GenericFilterParams;

    if (isRevocatoria() && this.filterFunctionality == 'peru_and_regiones') {
      currentParams = this.buildRevocatoriaParams();

    } else if (this.filterFunctionality == 'peru_intern_and_all_join') {
      currentParams = this.buildPeruInternAllJoinParams();

    } else if (
      this.filterFunctionality == 'only_regiones' ||
      this.filterFunctionality == 'peru_and_international'
    ) {
      currentParams = this.buildOnlyRegionesOrInternationalParams();
    }

    return currentParams;
  }

  private buildRevocatoriaParams(): GenericFilterParams {
    const ubigeoLevel3 = this.electoralRevocatoriaForm.controls.region.value;
    const location = this.electoralRevocatoriaForm.controls.location.value;

    const params = {
      tipoFiltro: ubigeoLevel3 ? UBIGEO_LEVELS.LEVEL_03 : null,
      idAmbitoGeografico: ubigeoLevel3 ? GEOGRAPHIC_SCOPE : null,
      ubigeoNivel1: ubigeoLevel3 ? getUbigeoLevel01FromLevel02(ubigeoLevel3) : null,
      ubigeoNivel2: ubigeoLevel3 ? getUbigeoLevel02FromLevel03(ubigeoLevel3) : null,
      ubigeoNivel3: ubigeoLevel3 ? String(ubigeoLevel3) : null,
      electoralDistrictId: location ? Number(location) : null
    } as GenericFilterParams;

    return getOptimizedObject<GenericFilterParams>(params);
  }


  private buildPeruInternAllJoinParams(): GenericFilterParams {
    const params = {
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScope(),
      ubigeoNivel1: this.appliedUbigeoFormValues.departmentUbigeoId,
      ubigeoNivel2: this.appliedUbigeoFormValues.provinceUbigeoId,
      ubigeoNivel3: this.appliedUbigeoFormValues.districtUbigeoId
    } as GenericFilterParams;

    return getOptimizedObject<GenericFilterParams>(params);
  }

  private buildOnlyRegionesOrInternationalParams(): GenericFilterParams {
    const params = {
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScope(),
      ubigeoNivel1: this.appliedUbigeoFormValues.departmentUbigeoId,
      ubigeoNivel2: this.appliedUbigeoFormValues.provinceUbigeoId,
      ubigeoNivel3: this.appliedUbigeoFormValues.districtUbigeoId,
      electoralDistrictId: this.electoralDistrictForm.controls.region.value
    } as GenericFilterParams;

    return getOptimizedObject<GenericFilterParams>(params);
  }

  private buildListsParams(): FilterParamsWithLists {
    const lists = {
      listRegiones: this.listRegiones,
      listDepartamento: this.listDepartamento,
      listProvincia: this.listProvincia,
      listDistrito: this.listDistrito,
      listContinentals: this.listContinentals,
      listCountries: this.listCountries
    } as FilterParamsWithLists;

    return getOptimizedObject<FilterParamsWithLists>(lists);
  }

  public applyFilterValidations() {
    this.validateIfHasChangedFilterValues();
    this.validateIfThereIsFilterValues();
  }

  private getFilterType(): string {
    return getGenericFilterType(
      this.filterFunctionality,
      this.ubigeoForm.controls.region.value,
      this.appliedUbigeoFormValues
    );
  }

  private getGeographicalScope(): number | null {
    return getGenericGeographicalScope(
      this.filterFunctionality,
      this.ubigeoForm.controls.region.value,
      Number(this.electoralDistrictForm.controls.region.value)
      // Number(this.electoralPeruDistrictForm.controls.region.value)
    );
  }

  /**
   * Validación para habilitar o deshabilitar el botón FILTRAR
   */
  private validateIfHasChangedFilterValues() {
    setTimeout(() => {
      let appliedUbigeoFormValues = getOptimizedObject<SelectedFilters>(this.appliedUbigeoFormValues);
      let selectedUbigeoFormValues = getOptimizedObject<SelectedFilters>(this.selectedUbigeoFormValues);

      let filtersCompare = [];
      if (isRevocatoria()) {
        appliedUbigeoFormValues = getOptimizedObjectRevoca<SelectedFilters>(this.appliedUbigeoFormValues);
        selectedUbigeoFormValues = getOptimizedObjectRevoca<SelectedFilters>(this.selectedUbigeoFormValues);

        filtersCompare = [
          appliedUbigeoFormValues.region == selectedUbigeoFormValues.region,
          appliedUbigeoFormValues.electoralDistrictId == selectedUbigeoFormValues.electoralDistrictId
        ];
      } else {
        filtersCompare = [
          appliedUbigeoFormValues.region == selectedUbigeoFormValues.region,
          appliedUbigeoFormValues.departmentUbigeoId == selectedUbigeoFormValues.departmentUbigeoId,
          appliedUbigeoFormValues.provinceUbigeoId == selectedUbigeoFormValues.provinceUbigeoId,
          appliedUbigeoFormValues.districtUbigeoId == selectedUbigeoFormValues.districtUbigeoId,
          appliedUbigeoFormValues.electoralDistrictId == selectedUbigeoFormValues.electoralDistrictId
        ];
      }

      this.filterButtonIsDisabled = filtersCompare.every((e) => e);
      this.filterButton2IsDisabled = filtersCompare.every((e) => e);
    }, 100);
  }

  public isOnInitialValue(): boolean {
    // Para only_regiones: comparar el distrito electoral actual con el inicial
    if (this.filterFunctionality === 'only_regiones') {
      const currentDistrictId = Number(this.electoralDistrictForm.controls.region.value);
      return currentDistrictId === this.initialElectoralDistrictId;
    }

    const currentRegion = this.ubigeoForm.controls.region.value;

    // Si la región cambió respecto al valor inicial → no está en estado inicial
    if (currentRegion !== this.initialRegionValue) {
      return false;
    }

    // Misma región inicial: verificar si hay sub-ubigeos seleccionados
    if (currentRegion === REGION_PERU || currentRegion === REGION_EXTRAJERO) {
      const selected = getOptimizedObject<SelectedFilters>(this.selectedUbigeoFormValues);
      return !selected.departmentUbigeoId && !selected.provinceUbigeoId && !selected.districtUbigeoId;
    }

    // Región TODOS sin sub-filtro → en estado inicial
    return true;
  }

  private validateIfThereIsFilterValues() {
    setTimeout(() => {
      if (this.filterFunctionality === 'only_regiones') {
        // Mostrar solo si el distrito actual es distinto al inicial
        const currentDistrictId = Number(this.electoralDistrictForm.controls.region.value);
        this.cleanButtonIsDisabled = currentDistrictId === this.initialElectoralDistrictId;
        return;
      }

      if (isRevocatoria()) {
        const { region, location } = this.electoralRevocatoriaForm.controls;
        this.cleanButtonIsDisabled = [!region.value, !location.value].every((e) => e);
        return;
      }

      const currentRegion = this.ubigeoForm.controls.region.value;

      // Si la región cambió respecto al valor inicial → habilitar limpiar
      if (currentRegion !== this.initialRegionValue) {
        this.cleanButtonIsDisabled = false;
        return;
      }

      // Misma región inicial: verificar si hay sub-ubigeos seleccionados
      if (currentRegion === REGION_PERU || currentRegion === REGION_EXTRAJERO) {
        const selected = getOptimizedObject<SelectedFilters>(this.selectedUbigeoFormValues);
        this.cleanButtonIsDisabled = [
          !selected.departmentUbigeoId,
          !selected.provinceUbigeoId,
          !selected.districtUbigeoId
        ].every((e) => e);
      } else {
        // currentRegion === initialRegionValue === TODOS → sin cambios
        this.cleanButtonIsDisabled = true;
      }
    }, 100);
  }

  // PRIVATE METHODS

  private cleanAllUbigeoLists() {
    this.listLocales = [];
    this.listDepartamento = [];
    this.listProvincia = [];
    this.listDistrito = [];
    this.listContinentals = [];
    this.listCountries = [];
    this.listStates = [];
  }

  private cleanFormValues() {
    this.ubigeoForm.controls.department.setValue('');
    this.ubigeoForm.controls.province.setValue('');
    this.ubigeoForm.controls.district.setValue('');
    this.ubigeoForm.controls.location.setValue('');
    this.ubigeoForm.controls.continent.setValue('');
    this.ubigeoForm.controls.country.setValue('');
    this.ubigeoForm.controls.state.setValue('');
  }

  public getSelectedUbigeoName(
    list: Array<{ ubigeo: string; nombre: string }>,
    value: string,
    fallback: string
  ): string {
    if (!value) {
      return fallback;
    }
    return list.find((item) => String(item.ubigeo) === String(value))?.nombre ?? fallback;
  }

  public electoralDistrictChanged() {
    const { electoralDistrictId } = this.appliedUbigeoFormValues;
    this.filterButton2IsDisabled = electoralDistrictId == Number(this.electoralDistrictForm.controls.region.value);
    this.applyElectoralDistrictFilters();
  }

  public electoralRevocatoriaChanged() {
    this.listLocales = [];
    this.electoralRevocatoriaForm.controls.location.setValue('', { emitEvent: true });
    let canClean = !this.showCleanButton && this.electoralRevocatoriaForm.controls.region.value == '';
    let defaultVal = Number(this.electoralRevocatoriaForm.controls.region.value) == 0;

    const { revocatoriaDistrictId } = this.appliedUbigeoFormValues;
    const { controls } = this.electoralRevocatoriaForm;

    this.filterButton2IsDisabled = revocatoriaDistrictId == Number(controls.region.value) || defaultVal || canClean;

    this.electionsService.loadLocalsRevocatoria(this);
  }

  public isAllUbigeoSelected() {
    return this.breadcrumbString.toLowerCase() === 'todos';
  }

  public openBottomSheet(): void {
    let data = {
      targetId: 'contenido',
      electionId: this.electionId,
      showLocales: this.showLocales,
      filterFunctionality: this.filterFunctionality,
      showSelectLocationUbigeo: this.showSelectLocationUbigeo,
      popupInformationInstance: this.popupInformationInstance,
      responsiveUbigeoParams: this.getResponsiveUbigeoParams(),
      service: this.popupFilterUbigeosService,
      breadcrumbString: this.breadcrumbString
    };

    const bottomSheetRef = this._bottomSheet.open<PopupFiltroUbigeoComponent, IDataForPopup>(
      PopupFiltroUbigeoComponent,
      {
        panelClass: 'menu-movil',
        data: data
      }
    );

    // Handle the result from the modal
    bottomSheetRef.afterDismissed().subscribe((result) => {
      if (result) {
        const paramsWithLists = {
          ...result.filterParams,
          listDepartamento: result.popupInformationInstance?.listDepartamento ?? [],
          listProvincia: result.popupInformationInstance?.listProvincia ?? [],
          listDistrito: result.popupInformationInstance?.listDistrito ?? [],
          listContinentals: result.popupInformationInstance?.listContinentals ?? [],
          listCountries: result.popupInformationInstance?.listCountries ?? []
        };
        this.applyFiltersEvent.emit(paramsWithLists);
        this.breadcrumbString = result.breadcrumbString;
        this.popupInformationInstance = {
          ...this.popupInformationInstance,
          listDepartamento: result.popupInformationInstance.listDepartamento,
          listProvincia: result.popupInformationInstance.listProvincia,
          listDistrito: result.popupInformationInstance.listDistrito,
          listContinentals: result.popupInformationInstance.listContinentals,
          listCountries: result.popupInformationInstance.listCountries,
          listStates: result.popupInformationInstance.listStates,
          listLocales: result.popupInformationInstance.listLocales,
          regiones: result.popupInformationInstance.regiones
        };
        this.responsiveUbigeoParams = result.selectedUbigeos;

        // Sync form state so isOnInitialValue() / cleanButtonIsDisabled work correctly after popup
        const selectedUbigeos = result.selectedUbigeos;
        const scope = selectedUbigeos?.idAmbitoGeografico;
        const regionValue: RegionValue = scope === 1 ? REGION_PERU : scope === 2 ? REGION_EXTRAJERO : REGION_TODOS;
        this.ubigeoForm.controls.region.setValue(regionValue, { emitEvent: false });
        this.selectedUbigeoFormValues = {
          region: regionValue,
          departmentUbigeoId: selectedUbigeos?.ubigeoNivel1,
          provinceUbigeoId: selectedUbigeos?.ubigeoNivel2,
          districtUbigeoId: selectedUbigeos?.ubigeoNivel3,
        };
        this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
        this.applyFilterValidations();
      }
    });
  }

  private getResponsiveUbigeoParams(): GenericFilterParams {
    if (this.filterFunctionality == 'only_regiones') {
      return {
        electoralDistrictId: Number(this.responsiveUbigeoParams.electoralDistrictId)
      };
    } else if (isRevocatoria()) {
      return {
        ubigeoNivel3: this.responsiveUbigeoParams.ubigeoNivel3 ?? this.electoralRevocatoriaForm.controls.region.value,
        electoralDistrictId: Number(this.electoralRevocatoriaForm.controls.location.value)
      };
    } else {
      return this.responsiveUbigeoParams;
    }
  }

  /**
   * THIS METHOD IS ONLY FOR RESPONSIVE
   * This method is only to be used in map event
   * @param params
   */
  public updateBreadcrumbStringFromExternal(params: GenericFilterParams) {
    this.responsiveUbigeoParams = params;

    if (!this.isResponsive) return;

    // Seed popupInformationInstance with the component's own already-loaded lists so name
    // lookups work even when the bottom sheet has never been opened yet.
    this.syncListsToPopupInformationInstance();

    if (this.filterFunctionality == 'only_regiones') {
      this.handleOnlyRegiones(params);
    } else if (params.idAmbitoGeografico == 1) {
      this.handleNationalScope(params);
    } else if (params.idAmbitoGeografico == 2) {
      this.handleInternationalScope(params);
    } else {
      this.setBreadcrumb(params);
    }
  }
  
  private syncListsToPopupInformationInstance() {
    if (this.listDepartamento?.length && !this.popupInformationInstance.listDepartamento?.length) {
      this.popupInformationInstance.listDepartamento = this.listDepartamento;
    }
    if (this.listProvincia?.length && !this.popupInformationInstance.listProvincia?.length) {
      this.popupInformationInstance.listProvincia = this.listProvincia;
    }
    if (this.listDistrito?.length && !this.popupInformationInstance.listDistrito?.length) {
      this.popupInformationInstance.listDistrito = this.listDistrito;
    }
    if (this.listContinentals?.length && !this.popupInformationInstance.listContinentals?.length) {
      this.popupInformationInstance.listContinentals = this.listContinentals;
    }
    if (this.listCountries?.length && !this.popupInformationInstance.listCountries?.length) {
      this.popupInformationInstance.listCountries = this.listCountries;
    }
    if (this.listStates?.length && !this.popupInformationInstance.listStates?.length) {
      this.popupInformationInstance.listStates = this.listStates;
    }
  }

  private setBreadcrumb(params: GenericFilterParams, includeParams: boolean = false) {
    const dataForBreadCrumb = getIDataForBreadcrumb(
      params,
      this.popupInformationInstance
    );

    this.breadcrumbString = includeParams
      ? getBreadcrumbForSelectedFilters(dataForBreadCrumb, params)
      : getBreadcrumbForSelectedFilters(dataForBreadCrumb);
  }

  private handleOnlyRegiones(params: GenericFilterParams) {
    this.setBreadcrumb(params, true);
  }

  private handleNationalScope(params: GenericFilterParams) {
    if (params.ubigeoNivel3) {
      this.setBreadcrumb(params);
      return;
    }

    if (params.ubigeoNivel2) {
      this.baseUbigeoService.loadDistricts(
        this.popupInformationInstance,
        params.ubigeoNivel2
      );
      this.setBreadcrumb(params);
      return;
    }

    if (params.ubigeoNivel1) {
      this.baseUbigeoService.loadProvinces(
        this.popupInformationInstance,
        params.ubigeoNivel1
      );
      this.setBreadcrumb(params);
      return;
    }

    if (!this.popupInformationInstance.listDepartamento?.length) {
      this.baseUbigeoService.loadDepartments(this.popupInformationInstance);
    }
    this.setBreadcrumb(params);
  }

  private handleInternationalScope(params: GenericFilterParams) {
    if (params.ubigeoNivel2) {
      this.baseUbigeoService.loadStates(
        this.popupInformationInstance,
        params.ubigeoNivel2
      );
      this.setBreadcrumb(params);
      return;
    }

    if (params.ubigeoNivel1) {
      this.baseUbigeoService.loadCountries(
        this.popupInformationInstance,
        params.ubigeoNivel1
      );
      this.setBreadcrumb(params);
      return;
    }

    if (!this.popupInformationInstance.listContinentals?.length) {
      this.baseUbigeoService.loadContinents(this.popupInformationInstance);
    }
    this.setBreadcrumb(params);
  }

  public getRegionForOnlyRegions(): string | undefined {
    const regionValue = this.electoralDistrictForm.controls.region.value;
    return this.regiones.find((r) => r.codigo === regionValue)?.nombre;
  }

  public updateBreadcrumbStringRegion() {
    this.popupInformationInstance.regiones = this.regiones;
    const params = {
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScope(),
      electoralDistrictId: this.electoralDistrictForm.controls.region.value ?? null
    } as GenericFilterParams;
    this.updateBreadcrumbStringFromExternal(params);
  }
}
