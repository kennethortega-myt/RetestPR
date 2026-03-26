import { Component, Inject, inject, OnInit } from "@angular/core";
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheet } from "@angular/material/bottom-sheet";
import { FormBuilder } from "@angular/forms";

import {
  getBreadcrumbForSelectedFilters,
  getElectoralDistrictForm,
  getElectoralRevocatoriaForm,
  getRegionString,
  getUbigeoForm,
} from "../../helpers/filtro-ubigeos-helpers/form-ubigeos.helper";
import { LocalVotacion } from "../../interfaces/acta-bean";
import {
  Department,
  Province,
  District,
  InternationalUbigeo,
  Region,
  IFiltroUbigeo,
  IBaseFiltroUbigeo,
} from "../../interfaces/elections.interfaces";
import {
  FilterFunctionality,
  GenericFilterParams,
  getOptimizedObject,
  REGION_EXTRAJERO,
  REGION_PERU,
  REGION_TODOS,
  SelectedFilters,
} from "../../interfaces/filtro-settings";
import { ElectionsService } from "../../services/elecciones-generales/elections.service";
import { getGenericFilterType, getGenericGeographicalScope } from "../../helpers/filters-helper.common";
import { IDataForPopup } from "../../interfaces/popup-filter-ubigeos.interface";
import { getGeograpScopeByRegion } from "../../helpers/election-type.config";
import { isRevocatoria } from "../../helpers/storage-helpers/encrypt-storage.helper";
import { getEncryptStorageEleccionValue } from "../../helpers/encrypt-storage-eleccion";
import { PROCESOS_ELECTORALES_EXISTENTES } from "../../settings/procesos-electorales.settings";
import { GEOGRAPHIC_SCOPE } from "../../helpers/constantes";
import { getUbigeoLevel01FromLevel02, getUbigeoLevel02FromLevel03 } from "../../helpers/ubigeo-level.common";

@Component({
  selector: "app-popup-filtro-ubigeo",
  templateUrl: "./popup-filtro-ubigeo.component.html",
  standalone: false,
})
export class PopupFiltroUbigeoComponent implements IFiltroUbigeo, OnInit {

  public VerlugarKey = 'popup-filtro.verLugar';
  public SeleccionaUbicacionKey = 'popup-filtro.seleccionaUbicacion';
  public FiltrarKey = 'popup-filtro.filtrar';
  public AmbitoKey = 'popup-filtro.ambito';
  public RegionKey = 'popup-filtro.region';
  public ProvinciaKey = 'popup-filtro.provincia';
  public DistritoKey = 'popup-filtro.distrito';
  public LocalvotacionKey  = 'popup-filtro.localVotacion';
  public ContinenteKey  = 'popup-filtro.continente';
  public PaisKey  = 'popup-filtro.pais';
  public CiudadKey  = 'popup-filtro.ciudad';
  public PeruKey  = 'popup-filtro.peru';

  // FORMULARIOS
  private formBuilder = inject(FormBuilder);
  public ubigeoForm = getUbigeoForm(this.formBuilder);
  public electoralDistrictForm = getElectoralDistrictForm(this.formBuilder);
  public revocatoriaForm = getElectoralRevocatoriaForm(this.formBuilder);
  public electoralRevocatoriaForm = getElectoralRevocatoriaForm(this.formBuilder);

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
  public cleanButtonIsDisabled = false;
  public showLocales: boolean = true;

  // TODO: This values should be updated
  public electionId = 10;
  public showSelectLocationUbigeo = false;
  public filterFunctionality: FilterFunctionality = "peru_intern_and_all_join";

  // ATRIBUTOS PRIVADOS
  public selectedUbigeoFormValues: SelectedFilters = {} as SelectedFilters;
  public appliedUbigeoFormValues: SelectedFilters = {} as SelectedFilters;
  private breadcrumbString = "";

  // TIPO DE PROCESO ELECTORAL
  public tipoDeProcesoElectoralAMostrar = getEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR");
  public tiposDeProcesosElectorales = PROCESOS_ELECTORALES_EXISTENTES;

  constructor(
    private readonly _bottomSheet: MatBottomSheet,
    public readonly electionsService: ElectionsService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IDataForPopup) {
    this.electionId = data.electionId;
    this.showSelectLocationUbigeo = data.showSelectLocationUbigeo;
    this.filterFunctionality = data.filterFunctionality;
    this.selectedUbigeoFormValues = {
      region: this.filterFunctionality == "peru_and_international" ? REGION_PERU : REGION_TODOS,
    } as SelectedFilters;
    this.appliedUbigeoFormValues = this.selectedUbigeoFormValues;
  }

  ngOnInit(): void {
    this.settingInitialValues();
    // // push data on listRegiones, regiones and others
    this.electionsService.settingsFilterFunctionality(this);
    // // push data on listRegiones, regiones and others
    this.applyFilterValidations();
  }

  cerrarmenu(): void {
    this._bottomSheet.dismiss();
  }

  private settingInitialValues() {
    this.showLocales = this.data.showLocales;    
    if (this.filterFunctionality == "only_regiones"){
    const { responsiveUbigeoParams } = this.data;
      const { electoralDistrictId } = responsiveUbigeoParams;
      this.electoralDistrictForm.controls.region.setValue(electoralDistrictId);
    } else if (isRevocatoria()) {
      this.settingInitialValuesFromOpenModalForRevocatoria();
    } else {
      this.settingInitialValuesFromOpenModal();
    }
    this.syncSelectedAndAppliedFromForms();
    this.applyFilterValidations();
  }

  private syncSelectedAndAppliedFromForms(): void {
    // Caso only_regiones
    if (this.filterFunctionality === "only_regiones") {
      const electoralDistrictId = this.electoralDistrictForm.controls.region.value ?? null;
      this.selectedUbigeoFormValues = { electoralDistrictId };
      this.appliedUbigeoFormValues = { electoralDistrictId };
      return;
    }

    // Caso revocatoria (actualmente no se tiene habilitado el tema de las revocatorias pero por si se llega a usar en algún momento)
    if (isRevocatoria()) {
      const ubigeoNivel3 = this.revocatoriaForm.controls.region.value ?? null;
      const location = this.revocatoriaForm.controls.location.value ?? null;

      this.selectedUbigeoFormValues = {
        region: REGION_PERU,
        regionString: REGION_PERU,
        departmentUbigeoId: ubigeoNivel3 ? getUbigeoLevel01FromLevel02(ubigeoNivel3) : null,
        provinceUbigeoId: ubigeoNivel3 ? getUbigeoLevel02FromLevel03(ubigeoNivel3) : null,
        districtUbigeoId: ubigeoNivel3 ? String(ubigeoNivel3) : null,
        electoralDistrictId: location ? Number(location) : null,
      };

      this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
      return;
    }

    // Caso común PERÚ - EXTRANJERO - TODOS
    const regionValue = this.ubigeoForm.controls.region.value;

    if (regionValue === REGION_PERU) {
      this.selectedUbigeoFormValues = {
        region: regionValue,
        departmentUbigeoId: this.ubigeoForm.controls.department.value,
        provinceUbigeoId: this.ubigeoForm.controls.province.value,
        districtUbigeoId: this.ubigeoForm.controls.district.value,
      };
    } else if (regionValue === REGION_EXTRAJERO) {
      this.selectedUbigeoFormValues = {
        region: regionValue,
        departmentUbigeoId: this.ubigeoForm.controls.continent.value,
        provinceUbigeoId: this.ubigeoForm.controls.country.value,
        districtUbigeoId: this.ubigeoForm.controls.state.value,
      };
    } else {
      this.selectedUbigeoFormValues = { region: REGION_TODOS };
    }
    this.appliedUbigeoFormValues = { ...this.selectedUbigeoFormValues };
  }

  /**
   * THIS METHOD IS EXCLUSIVE FOR RESPONSIVE MODAL
   */
  private settingInitialValuesFromOpenModal() {
    const { popupInformationInstance, responsiveUbigeoParams, breadcrumbString } = this.data;
    const keys = Object.keys(responsiveUbigeoParams ?? {});
    if (keys.length) {
      this.listDepartamento = popupInformationInstance.listDepartamento ?? [];
      this.listProvincia = popupInformationInstance.listProvincia ?? [];
      this.listDistrito = popupInformationInstance.listDistrito ?? [];
      this.listContinentals = popupInformationInstance.listContinentals ?? [];
      this.listCountries = popupInformationInstance.listCountries ?? [];
      this.listStates = popupInformationInstance.listStates ?? [];
      this.regiones = popupInformationInstance.regiones ?? [];

      const { idAmbitoGeografico, ubigeoNivel1, ubigeoNivel2, ubigeoNivel3 } = responsiveUbigeoParams;

      const regionValue = getRegionString(idAmbitoGeografico);
      this.ubigeoForm.controls.region.setValue(regionValue);
      this.showUbigeoPeru = regionValue == REGION_PERU;
      this.showUbigeoExtranjero = regionValue == REGION_EXTRAJERO;

      this.ubigeoForm.controls.department.setValue(ubigeoNivel1 ? String(ubigeoNivel1) : "");
      this.ubigeoForm.controls.province.setValue(ubigeoNivel2 ? String(ubigeoNivel2) : "");
      this.ubigeoForm.controls.district.setValue(ubigeoNivel3 ? String(ubigeoNivel3) : "");

      this.ubigeoForm.controls.continent.setValue(ubigeoNivel1 ? String(ubigeoNivel1) : "");
      this.ubigeoForm.controls.country.setValue(ubigeoNivel2 ? String(ubigeoNivel2) : "");
      this.ubigeoForm.controls.state.setValue(ubigeoNivel3 ? String(ubigeoNivel3) : "");
    } else if (breadcrumbString && breadcrumbString.toUpperCase() === 'PERÚ') {
      // Si el breadcrumb es "Perú" pero no hay parámetros de ubigeo, pre-seleccionar Perú
      this.ubigeoForm.controls.region.setValue(REGION_PERU);
      this.showUbigeoPeru = true;
      this.showUbigeoExtranjero = false;
      
      // Cargar departamentos si no están cargados
      if (!popupInformationInstance.listDepartamento || popupInformationInstance.listDepartamento.length === 0) {
        this.electionsService.loadDepartments(this);
      } else {
        this.listDepartamento = popupInformationInstance.listDepartamento;
      }
    }
  }

  /**
   * THIS METHOD IS EXCLUSIVE FOR RESPONSIVE MODAL
   */
  private settingInitialValuesFromOpenModalForRevocatoria() {
    this.loadRegiones();
    const { popupInformationInstance, responsiveUbigeoParams } = this.data;
    const keys = Object.keys(responsiveUbigeoParams ?? {});
    if (keys.length) {
      this.listLocales = popupInformationInstance.listLocales ?? [];
      this.regiones = popupInformationInstance.regiones ?? [];

      const { ubigeoNivel3, electoralDistrictId } = responsiveUbigeoParams;
      
      this.revocatoriaForm.controls.region.setValue(ubigeoNivel3);
      this.revocatoriaForm.controls.location.setValue(String(electoralDistrictId));
    }
  }

  // METHODS TO LOAD UBIGEOS IN SELECT LIST

  public regionChanged($event?: Event) {
    const regionValue = this.ubigeoForm.controls.region.value;
    this.showUbigeoPeru = regionValue == REGION_PERU;
    this.showUbigeoExtranjero = regionValue == REGION_EXTRAJERO;

    this.cleanAllUbigeoLists();
    this.cleanFormValues();

    if (this.showUbigeoPeru) {
      this.electionsService.loadDepartments(this);
    } else if (this.showUbigeoExtranjero) {
      this.electionsService.loadContinentals(this);
    }

    this.selectedUbigeoFormValues = {
      region: regionValue,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public departmentChanged($event?: Event) {
    this.listProvincia = [];
    this.listDistrito = [];
    this.listLocales = [];
    this.ubigeoForm.controls.province.setValue("");
    this.ubigeoForm.controls.district.setValue("");
    this.ubigeoForm.controls.location.setValue("");

    if (this.ubigeoForm.controls.department.value) {
      this.electionsService.loadProvinces(this);
    }

    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public provinceChanged($event?: Event) {
    this.listDistrito = [];
    this.listLocales = [];
    this.ubigeoForm.controls.district.setValue("");
    this.ubigeoForm.controls.location.setValue("");

    if (this.ubigeoForm.controls.province.value) {
      this.electionsService.loadDistricts(this);
    }

    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value,
      provinceUbigeoId: this.ubigeoForm.controls.province.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public districtChanged($event?: Event) {
    this.listLocales = [];
    this.ubigeoForm.controls.location.setValue("");

    if (this.showSelectLocationUbigeo && this.ubigeoForm.controls.district.value) {
      this.electionsService.loadLocals(this);
    }

    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.department.value,
      provinceUbigeoId: this.ubigeoForm.controls.province.value,
      districtUbigeoId: this.ubigeoForm.controls.district.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public continentChanged($event?: Event) {
    this.listCountries = [];
    this.listStates = [];
    this.ubigeoForm.controls.country.setValue("");
    this.ubigeoForm.controls.state.setValue("");

    if (this.ubigeoForm.controls.continent.value) {
      this.electionsService.loadCountries(this);
    }

    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public countryChanged($event?: Event) {
    this.listStates = [];
    this.ubigeoForm.controls.state.setValue("");

    if (this.ubigeoForm.controls.country.value) {
      this.electionsService.loadStates(this);
    }

    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value,
      provinceUbigeoId: this.ubigeoForm.controls.country.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  public stateChanged($event?: Event) {
    this.selectedUbigeoFormValues = {
      region: this.ubigeoForm.controls.region.value,
      departmentUbigeoId: this.ubigeoForm.controls.continent.value,
      provinceUbigeoId: this.ubigeoForm.controls.country.value,
      districtUbigeoId: this.ubigeoForm.controls.state.value,
    };
    this.applyFilterValidations();
    this.makeBreadCrumbForSelectedFilters();
  }

  private loadRegiones() {
    this.electionsService.loadRegiones(this);
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
        districtUbigeoId: this.ubigeoForm.controls.district.value,
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    } else if (regionValue == "EXTRANJERO") {
      const filters = {
        region: regionValue,
        departmentUbigeoId: this.ubigeoForm.controls.continent.value,
        provinceUbigeoId: this.ubigeoForm.controls.country.value,
        districtUbigeoId: this.ubigeoForm.controls.state.value,
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    } else {
      const filters = {
        region: regionValue,
      } as SelectedFilters;
      const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
      this.appliedUbigeoFormValues = optimizedFilters;
    }
    
    this.applyUbigeoFiltersEvent();
  }

  public applyUbigeoFiltersForRegion() {
    this.filterButtonIsDisabled = true;
    this.makeBreadCrumbForSelectedFilters();
    const regionValue = this.electoralDistrictForm.controls.region.value;
    const filters = {
      electoralDistrictId: regionValue,
    } as SelectedFilters;
    const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
    this.appliedUbigeoFormValues = optimizedFilters;

    this.applyUbigeoFiltersEvent();
  }

  public applyUbigeoFiltersForRevocatoria() {
    this.filterButtonIsDisabled = true;
    this.makeBreadCrumbForSelectedFilters();
    const ubigeoLevel3 = this.revocatoriaForm.controls.region.value;
    const location = this.revocatoriaForm.controls.location.value;
    const filters = {
      region: REGION_PERU,
      regionString: REGION_PERU,
      departmentUbigeoId: ubigeoLevel3 ? getUbigeoLevel01FromLevel02(ubigeoLevel3) : null,
      provinceUbigeoId: ubigeoLevel3 ? getUbigeoLevel02FromLevel03(ubigeoLevel3) : null,
      districtUbigeoId: ubigeoLevel3 ? String(ubigeoLevel3) : null,
      electoralDistrictId: location ? Number(location) : null,
    } as SelectedFilters;
    const optimizedFilters = getOptimizedObject<SelectedFilters>(filters);
    this.appliedUbigeoFormValues = optimizedFilters;

    this.applyUbigeoFiltersEvent();
  }

  // PRIVATE METHODS

  /**
   * Método que crea el formato correcto para mandar ejecutar los filtros
   */
  private applyUbigeoFiltersEvent() {
    const params = {
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScope(),
      ubigeoNivel1: this.appliedUbigeoFormValues.departmentUbigeoId,
      ubigeoNivel2: this.appliedUbigeoFormValues.provinceUbigeoId,
      ubigeoNivel3: this.appliedUbigeoFormValues.districtUbigeoId,
      electoralDistrictId: this.appliedUbigeoFormValues.electoralDistrictId ?? null,
    } as GenericFilterParams;
    const currentParams = getOptimizedObject<GenericFilterParams>(params);

    const popupInformationInstance = {
      listDepartamento: this.listDepartamento,
      listProvincia: this.listProvincia,
      listDistrito: this.listDistrito,
      listContinentals: this.listContinentals,
      listCountries: this.listCountries,
      listStates: this.listStates,
      regiones: this.regiones,
      listLocales: this.listLocales,
    } as IBaseFiltroUbigeo;

    const selectedUbigeos = this.getSelectedUbigeos();
    const optSelectedUbigeos = getOptimizedObject<GenericFilterParams>(selectedUbigeos);

    // Return data instead of using global service
    const result = {
      filterParams: currentParams,
      breadcrumbString: this.breadcrumbString,
      popupInformationInstance: popupInformationInstance,
      selectedUbigeos: optSelectedUbigeos
    };

    setTimeout(() => {
      this._bottomSheet.dismiss(result);
    }, 10);
  }

  private getSelectedUbigeos(): GenericFilterParams {
    const location = this.revocatoriaForm.controls.location.value;
    const regionValue = this.electoralDistrictForm.controls.region.value;

    if (this.filterFunctionality == "only_regiones"){
      return {
        electoralDistrictId: regionValue ? Number(regionValue) : null,
      } as GenericFilterParams;
    }

    if (isRevocatoria()) {
      return {
        ubigeoNivel3: this.revocatoriaForm.controls.region.value,
        electoralDistrictId: location ? Number(location) : null,
      } as GenericFilterParams;
    } else {
      return {
        idAmbitoGeografico:
          this.ubigeoForm.controls.region.value == REGION_TODOS
            ? undefined
            : getGeograpScopeByRegion(this.ubigeoForm.controls.region.value),
        ubigeoNivel1: this.selectedUbigeoFormValues.departmentUbigeoId,
        ubigeoNivel2: this.selectedUbigeoFormValues.provinceUbigeoId,
        ubigeoNivel3: this.selectedUbigeoFormValues.districtUbigeoId,
      } as GenericFilterParams;
    }
  }

  private getFilterType(): string {
    return getGenericFilterType(
      this.filterFunctionality,
      this.ubigeoForm.controls.region.value,
      this.appliedUbigeoFormValues
    );
  }

  private getGeographicalScope(): number | null {
    if (isRevocatoria()) {
      return GEOGRAPHIC_SCOPE;
    } else {
      return getGenericGeographicalScope(
        this.filterFunctionality,
        this.ubigeoForm.controls.region.value,
        Number(this.electoralDistrictForm.controls.region.value)
      );
    }
  }

  private applyFilterValidations() {
    this.validateIfHasChangedFilterValues();
    this.validateIfThereIsFilterValues();
  }

  /**
   * Validación para habilitar o deshabilitar el botón FILTRAR
   */
  private validateIfHasChangedFilterValues() {
    setTimeout(() => {
      const appliedUbigeoFormValues = getOptimizedObject<SelectedFilters>(this.appliedUbigeoFormValues);
      const selectedUbigeoFormValues = getOptimizedObject<SelectedFilters>(this.selectedUbigeoFormValues);
      this.filterButtonIsDisabled = [
        appliedUbigeoFormValues.region == selectedUbigeoFormValues.region,
        appliedUbigeoFormValues.departmentUbigeoId == selectedUbigeoFormValues.departmentUbigeoId,
        appliedUbigeoFormValues.provinceUbigeoId == selectedUbigeoFormValues.provinceUbigeoId,
        appliedUbigeoFormValues.districtUbigeoId == selectedUbigeoFormValues.districtUbigeoId,
      ].every((e) => e);
    }, 100);
  }

  /**
   * Validación para habilitar o deshabilitar el botón LIMPIAR
   */
  private validateIfThereIsFilterValues() {
    setTimeout(() => {
      const { region } = this.ubigeoForm.controls;
      if (region.value == REGION_PERU || region.value == REGION_EXTRAJERO) {
        const selectedUbigeoFormValues = getOptimizedObject<SelectedFilters>(this.selectedUbigeoFormValues);
        this.cleanButtonIsDisabled = [
          !selectedUbigeoFormValues.departmentUbigeoId,
          !selectedUbigeoFormValues.provinceUbigeoId,
          !selectedUbigeoFormValues.districtUbigeoId,
        ].every((e) => e);
      } else {
        this.cleanButtonIsDisabled = true;
      }
    }, 100);
  }

  private cleanAllUbigeoLists() {
    this.listDepartamento = [];
    this.listProvincia = [];
    this.listDistrito = [];
    this.listLocales = [];
    this.listContinentals = [];
    this.listCountries = [];
    this.listStates = [];
  }

  private cleanFormValues() {
    this.ubigeoForm.controls.department.setValue("");
    this.ubigeoForm.controls.province.setValue("");
    this.ubigeoForm.controls.district.setValue("");
    this.ubigeoForm.controls.location.setValue("");
    this.ubigeoForm.controls.continent.setValue("");
    this.ubigeoForm.controls.country.setValue("");
    this.ubigeoForm.controls.state.setValue("");
  }

  makeBreadCrumbForSelectedFilters() {
    if (this.filterFunctionality == "only_regiones"){
      this.settingBreadcrumbForRegion();
    } else if (isRevocatoria()) {
      this.settingBreadcrumbForRevocatoria();
    } else {
      this.breadcrumbString = getBreadcrumbForSelectedFilters({
        regionValue: this.ubigeoForm.controls.region.value,
        selectedUbigeoFormValues: this.selectedUbigeoFormValues,
        listContinentals: this.listContinentals,
        listCountries: this.listCountries,
        listStates: this.listStates,
        listDepartamento: this.listDepartamento,
        listProvincia: this.listProvincia,
        listDistrito: this.listDistrito,
      });
    }
  }

  private settingBreadcrumbForRegion() {
    const regionValue = this.electoralDistrictForm.controls.region.value;
    const { regiones } = this;
    if (regiones && regiones.length > 0) {
      const selectedDefaultUbigeo = this.regiones.find((region) => Number(region.codigo) == Number(regionValue));
      if (selectedDefaultUbigeo) {
        this.breadcrumbString = selectedDefaultUbigeo.nombre
      }
    }
  }

  private settingBreadcrumbForRevocatoria() {
    const ubigeoNivel3 = this.revocatoriaForm.controls.region.value;
    const { regiones } = this;
    if (regiones && regiones.length > 0) {
      const selectedDefaultUbigeo = this.regiones.find((region) => Number(region.ubigeo) == Number(ubigeoNivel3));
      if (selectedDefaultUbigeo) {
        this.breadcrumbString = selectedDefaultUbigeo.nombre
      }
    }
  }

  public electoralDistrictChanged() {
    const { electoralDistrictId } = this.appliedUbigeoFormValues;
    this.filterButtonIsDisabled = electoralDistrictId == Number(this.electoralDistrictForm.controls.region.value);
  }

  electoralRevocatoriaChanged() {
    this.revocatoriaForm.controls.location.setValue("", { emitEvent: true });
    this.listLocales = [];
    this.electoralRevocatoriaForm = this.revocatoriaForm;
    this.electionsService.loadLocalsRevocatoria(this);
  }

  valueChange(ev) {
    if (ev === null){
      this.revocatoriaForm.controls.location.setValue("", { emitEvent: true });
      this.electoralRevocatoriaForm.controls.location.setValue("", { emitEvent: true });
    }
  }
}
