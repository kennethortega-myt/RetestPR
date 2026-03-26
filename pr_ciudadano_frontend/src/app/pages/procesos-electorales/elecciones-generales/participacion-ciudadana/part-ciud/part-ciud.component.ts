import * as am5 from '@amcharts/amcharts5';
import * as am5percent from '@amcharts/amcharts5/percent';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import { Component, ElementRef, HostListener, inject, Input, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { take } from 'rxjs/operators';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { MainHotMapComponent } from '../../../../../components/main-hot-map/main-hot-map.component';
import { UBIGEO_DESCRIPTION_TEXTS } from '../../../../../constants/participacion-ciudadana.constants';
import { UBIGEO_LEVELS } from '../../../../../helpers/constantes';
import { getGeograpScopeByRegion } from '../../../../../helpers/election-type.config';
import { getCurrentElectionDescriptionTitleBy } from '../../../../../helpers/encrypt-storage-eleccion';
import { commonUpdateMapFromFilter } from '../../../../../helpers/map.update-map-from-filter';
import {
  getUbigeoDescriptionForNivel01,
  getUbigeoDescriptionForNivel02,
  getUbigeoDescriptionForNivel03
} from '../../../../../helpers/participacion-ubigeo-description.helper';
import { getFilterTypeForBackend, getFilterTypeForBackend2 } from '../../../../../helpers/ubigeo-level.common';
import { IElectionType } from '../../../../../interfaces/elections.interfaces';
import {
  FilterByLocationParams,
  FilterParamsWithLists,
  GenericFilterParams,
  getOptimizedObject,
  REGION_EXTRAJERO,
  REGION_PERU,
  REGION_TODOS,
  RegionValue
} from '../../../../../interfaces/filtro-settings';
import {
  CommonParams,
  TotalesDataDetail,
  UbigeosDetail
} from '../../../../../interfaces/participacion-ciudadana.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { TotalsParams } from '../../../../../interfaces/resumen-general.interfaces';
import { Ubigeo } from '../../../../../interfaces/ubigeo-bean';
import { BaseUbigeoService } from '../../../../../services/common/base-ubigeo.service';
import { ParticipacionCiudadanaService } from '../../../../../services/elecciones-generales/participacion-ciudadana.service';
import { ResumenGeneralService } from '../../../../../services/elecciones-generales/resumen-general.service';
import { encryptStorageEleccion } from '../../../../../settings/encrypt-storage.settings';
import { MenuElectionIconKeys } from '../../../../../settings/icon-keys.settings';
import { formatNumberWithApostrophe } from '../../../../../utils/funciones';

const DEFAULT_TOTALS_DETAIL = {
  totalElectoresHabiles: 0,
  totalAsistentes: 0,
  totalAusentes: 0
} as TotalesDataDetail;

const currentIconKey: MenuElectionIconKeys = 'participacion_ciudadana';

@Component({
  selector: 'app-part-ciud',
  standalone: false,
  templateUrl: './part-ciud.component.html'
})
export class PartCiudComponent {
  @ViewChild(MainHotMapComponent, { static: false }) mainHotMapComponent?: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent) mainFiltroUbigeoComponent?: GenericFilterUbigeoComponent;
  mostrarMapa = false;
  esPantallaChica = window.innerWidth < 960;

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;

    if (!this.esPantallaChica) {
      this.mostrarMapa = true;
    } else {
      this.mostrarMapa = false;
    }
  }

  toggleMapa() {
    if (this.esPantallaChica) {
      this.mostrarMapa = !this.mostrarMapa;
    }
  }

  private formBuilder = inject(FormBuilder);
  public myFormUbigeo = this._myFormUbigeo;
  public sortedForm = this._sortedForm;

  public initialUbigeo?: FilterByLocationParams;
  @Input() electionType?: IElectionType;

  public pagination: {
    arrayForPagination?: any[];
    paginaActual?: any;
  } = {
    arrayForPagination: [],
    paginaActual: 0
  };
  public ubigeosData: UbigeosDetail[] = [];
  public initialUbigeosData: UbigeosDetail[] = [];
  private totalUbigeosData: UbigeosDetail[] = []; // Lista completa de ubigeos para paginar
  public totalesDataDetail: TotalesDataDetail = DEFAULT_TOTALS_DETAIL;

  private selectedFilterParams: FilterByLocationParams = {} as FilterByLocationParams;

  public departamentos: Ubigeo[] = [];
  public provincias: Ubigeo[] = [];
  public distritos: Ubigeo[] = [];

  public continentes: Ubigeo[] = [];
  public paises: Ubigeo[] = [];

  public resumen: Resumen = {} as Resumen;
  public primerIngreso = true;

  public root: am5.Root | null = null;

  public ubigeoTitleName = '';

  public regionValue: RegionValue = REGION_TODOS;
  public numberOfElemForPage = 12;
  regionTodos = REGION_TODOS;

  public electionId: number;
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  constructor(
    public dialog: MatDialog,
    private readonly participacionCiudadanaService: ParticipacionCiudadanaService,
    private readonly resumenGeneral: ResumenGeneralService,
    private readonly elementRef: ElementRef,
    private readonly baseUbigeoService: BaseUbigeoService
  ) {
    this.electionId = JSON.parse(encryptStorageEleccion.getItem('ID_DE_ELECCION_PRINCIPAL') ?? '{}') ?? 10;
    this.loadGeneralSummaryByTheWorld();
  }

  private get _myFormUbigeo() {
    return this.formBuilder.group({
      region: ['0', Validators.required],
      departamento: ['0', Validators.required],
      provincia: ['0', Validators.required],
      distrito: ['0', Validators.required],
      cent_educativo: ['0', Validators.required]
    });
  }

  private get _sortedForm() {
    return this.formBuilder.group({
      orderBy: ['alfab', Validators.required]
    });
  }

  ngOnInit() {  
    this.cargarTotalesByTheWorld();
    this.cargarUbigeoDetailsByTheWorld();
  }

  ngAfterViewInit(): void {
    if (this.mainHotMapComponent) {
      this.mainHotMapComponent!.loadInitialUbigeoWorld();
    }
  }

  public scrollToBottomSection(): void {
    const element = this.elementRef.nativeElement.querySelector(`#bottom-section`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  public cleanInformation($event: RegionValue) {
    this.selectedFilterParams = {};
    this.regionValue = $event;
    this.ubigeosData = [];
    this.initialUbigeosData = [];
    this.totalesDataDetail = DEFAULT_TOTALS_DETAIL;
    this.pagination = {
      arrayForPagination: [],
      paginaActual: 0
    };
    this.cargarDepartamentosUbigeosByRegion();
    this.loadGeneralSummaryByRegion();

    if ($event == REGION_PERU) {
      this.cargarTotalesByRegion();
      this.mainHotMapComponent!.loadInitialUbigeoPeru();
    } else if ($event == REGION_EXTRAJERO) {
      this.cargarTotalesByRegion();
      this.mainHotMapComponent!.loadInitialUbigeoInternational();
    } else {
      this.mainHotMapComponent!.loadInitialUbigeoWorld();
      this.cargarTotalesByTheWorld();
    }
    this.initialUbigeo = undefined;
    this.ubigeoTitleName = 'regiones del Perú';

    this.updateUbigeoTitleName();
  }

  public applyFiltersEvent(params: FilterParamsWithLists) {

    if (this.initialUbigeo) {
      this.initialUbigeo.districtUbigeoId = null;
      this.initialUbigeo.provinceUbigeoId = null;
    }
    
    this.departamentos = params.listDepartamento;
    this.provincias = params.listProvincia;
    this.distritos = params.listDistrito;

    this.continentes = params.listContinentals;
    this.paises = params.listCountries;
    this.resetOrderBy();

    if (params.idAmbitoGeografico) {
      this.regionValue = params.idAmbitoGeografico == 1 ? REGION_PERU : REGION_EXTRAJERO;
    } else {
      this.regionValue = REGION_TODOS;
    }

    this.loadGeneralSummaryByUbigeo(params);

    this.selectedFilterParams = {
      region: params.idAmbitoGeografico,
      departmentUbigeoId: params.ubigeoNivel1,
      provinceUbigeoId: params.ubigeoNivel2,
      districtUbigeoId: params.ubigeoNivel3
    };
    this.selectedFilterParams = getOptimizedObject<FilterByLocationParams>(this.selectedFilterParams);

    if (this.regionValue == REGION_PERU) {
      this.updateDetallesDeUbigeos();
    } else if (this.regionValue == REGION_EXTRAJERO) {
      this.updateDetallesDeUbigeosExtrangero();
    } else {
      this.cargarUbigeoDetailsByTheWorld();
      this.cargarTotalesByTheWorld();
    }

    this.updateMapFromFilter();
    this.updateUbigeoTitleName();
  }

  private updateDetallesDeUbigeos() {
    let currentParams = {
      idAmbitoGeografico: this.getGeographicalScope(),
      tipoFiltro: getFilterTypeForBackend(this.selectedFilterParams),
      ubigeoNivel01: Number(this.selectedFilterParams.departmentUbigeoId) || null,
      ubigeoNivel02: Number(this.selectedFilterParams.provinceUbigeoId) || null,
      ubigeoNivel03: Number(this.selectedFilterParams.districtUbigeoId) || null
    } as CommonParams;
    currentParams = getOptimizedObject<CommonParams>(currentParams);

    this.cargarTotalesByUbigeo(currentParams);

    if (this.selectedFilterParams.districtUbigeoId) {
      this.initialUbigeo = {
        ...this.initialUbigeo,
        departmentUbigeoId: this.selectedFilterParams.departmentUbigeoId,
        provinceUbigeoId: this.selectedFilterParams.provinceUbigeoId,
        districtUbigeoId: this.selectedFilterParams.districtUbigeoId
      } as FilterByLocationParams;
      currentParams.tipoFiltro = UBIGEO_LEVELS.LEVEL_02;
      currentParams.ubigeoNivel03 = undefined;
      this.cargarUbigeosOnlyForOneDistrict(currentParams, Number(this.selectedFilterParams.districtUbigeoId));
      return;
    }
    if (this.selectedFilterParams.provinceUbigeoId) {
      this.initialUbigeo = {
        ...this.initialUbigeo,
        departmentUbigeoId: this.selectedFilterParams.departmentUbigeoId,
        provinceUbigeoId: this.selectedFilterParams.provinceUbigeoId
      } as FilterByLocationParams;
      this.cargarDistritosByUbigeos(this.selectedFilterParams.provinceUbigeoId, currentParams);
      return;
    }
    if (this.selectedFilterParams.departmentUbigeoId) {
      this.initialUbigeo = {
        departmentUbigeoId: this.selectedFilterParams.departmentUbigeoId
      } as FilterByLocationParams;
      this.cargarProvinciasByUbigeos(this.selectedFilterParams.departmentUbigeoId, currentParams);
      return;
    }

    this.cargarDepartamentosUbigeosByRegion();

    this.initialUbigeo = undefined;
    this.updateUbigeoTitleName();
  }

  private updateDetallesDeUbigeosExtrangero() {
    let currentParams = {
      idAmbitoGeografico: this.getGeographicalScope(),
      tipoFiltro: getFilterTypeForBackend(this.selectedFilterParams),
      ubigeoNivel01: Number(this.selectedFilterParams.departmentUbigeoId) || null,
      ubigeoNivel02: Number(this.selectedFilterParams.provinceUbigeoId) || null,
      ubigeoNivel03: Number(this.selectedFilterParams.districtUbigeoId) || null
    } as CommonParams;
    currentParams = getOptimizedObject<CommonParams>(currentParams);

    this.cargarTotalesByUbigeo(currentParams);

    if (this.selectedFilterParams.provinceUbigeoId) {
      this.initialUbigeo = {
        ...this.initialUbigeo,
        departmentUbigeoId: this.selectedFilterParams.departmentUbigeoId,
        provinceUbigeoId: this.selectedFilterParams.provinceUbigeoId
      } as FilterByLocationParams;
      if (this.mainFiltroUbigeoComponent) {
        this.baseUbigeoService.loadStates(this.mainFiltroUbigeoComponent, this.selectedFilterParams.provinceUbigeoId);
      }
      this.cargarUbigeosOnlyForOneCountry(
        this.getParamsForUbigeo01(),
        Number(this.selectedFilterParams.provinceUbigeoId)
      );
      return;
    }
    if (this.selectedFilterParams.departmentUbigeoId) {
      this.initialUbigeo = {
        departmentUbigeoId: this.selectedFilterParams.departmentUbigeoId
      } as FilterByLocationParams;
      this.cargarPaisesByUbigeos(this.selectedFilterParams.departmentUbigeoId, currentParams);
      return;
    }

    this.cargarContinentesUbigeosByRegion();

    this.initialUbigeo = undefined;
    this.updateUbigeoTitleName();
  }

  private updateMapFromFilter() {
    commonUpdateMapFromFilter(this.selectedFilterParams, this.mainHotMapComponent!);
  }

  public getStringUbigeo(ubigeo: number): string {
    return ubigeo.toString().length == 5 ? '0' + ubigeo.toString() : ubigeo.toString();
  }

  private resetOrderBy() {
    this.sortedForm.controls['orderBy'].setValue('alfab');
  }

  private updateUbigeoTitleName() {
    if (this.regionValue == REGION_TODOS) {
      this.ubigeoTitleName = UBIGEO_DESCRIPTION_TEXTS.descripcion_ambos_ambitos;
    } else if (this.initialUbigeo) {
      if (this.regionValue == REGION_PERU) {
        this.validateInitialUbigeo();
      } else {
        this.validateInitialUbigeoExtrangero();
      }
    } else {
      this.ubigeoTitleName =
        this.regionValue == REGION_PERU
          ? UBIGEO_DESCRIPTION_TEXTS.descripcion_peru
          : UBIGEO_DESCRIPTION_TEXTS.descripcion_internacional;
    }
  }

  private validateInitialUbigeo(): void {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = this.initialUbigeo!;
    if (districtUbigeoId) {
      this.validateDistritoId(districtUbigeoId);
      return null;
    }
    if (provinceUbigeoId) {
      this.validateProvinceId(provinceUbigeoId);
      return null;
    }
    if (departmentUbigeoId) {
      this.validateDepartmentId(departmentUbigeoId);
      return null;
    }
  }

  private validateInitialUbigeoExtrangero(): void {
    const { departmentUbigeoId, provinceUbigeoId } = this.initialUbigeo!;
    if (provinceUbigeoId) {
      this.validateCountryId(provinceUbigeoId);
      return null;
    }
    if (departmentUbigeoId) {
      this.validateContinenteId(departmentUbigeoId);
      return null;
    }
  }

  private validateDistritoId(districtUbigeoId: string) {
    const selectedDistrict = this.distritos.find((prov) => Number(prov.ubigeo) == Number(districtUbigeoId));
    const districtName = selectedDistrict?.nombre?.toLowerCase();
    this.ubigeoTitleName = getUbigeoDescriptionForNivel03(districtName);
  }

  private validateProvinceId(provinceUbigeoId: string) {
    const selectedProvince = this.provincias.find((prov) => Number(prov.ubigeo) == Number(provinceUbigeoId));
    const provinceName = selectedProvince?.nombre?.toLowerCase();
    this.ubigeoTitleName = getUbigeoDescriptionForNivel02(this.regionValue, provinceName);
  }

  private validateDepartmentId(departmentUbigeoId: string): void {
    const selectedDepartment = this.departamentos.find((dep) => Number(dep.ubigeo) === Number(departmentUbigeoId));
    const departmentName = selectedDepartment?.nombre?.toLowerCase();
    this.ubigeoTitleName = getUbigeoDescriptionForNivel01(this.regionValue, departmentName);
  }

  private validateContinenteId(departmentUbigeoId: string): void {
    const selectedContinent = this.continentes.find((dep) => Number(dep.ubigeo) === Number(departmentUbigeoId));
    const continentName = selectedContinent?.nombre?.toLowerCase();
    this.ubigeoTitleName = getUbigeoDescriptionForNivel01(this.regionValue, continentName);
  }

  private validateCountryId(provinceUbigeoId: string) {
    const selectedCountry = this.paises.find((prov) => Number(prov.ubigeo) == Number(provinceUbigeoId));
    const countryName = selectedCountry?.nombre?.toLowerCase();
    this.ubigeoTitleName = getUbigeoDescriptionForNivel02(this.regionValue, countryName);
  }

  private getGeographicalScope(): number | undefined {
    if (this.regionValue == REGION_TODOS) {
      return undefined;
    }
    return getGeograpScopeByRegion(this.regionValue);
  }

  /**
   * This method is called only from map
   */
  public regionChanged($event: RegionValue) {
    this.selectedFilterParams = {};
    this.initialUbigeo = undefined;
    this.regionValue = $event;

    if ($event == REGION_PERU) {
      this.loadGeneralSummaryByRegion();
      this.mainHotMapComponent!.loadInitialUbigeoPeru();
      this.cargarDepartamentosUbigeosByRegion();
      this.cargarTotalesByRegion();
    } else if ($event == REGION_EXTRAJERO) {
      this.loadGeneralSummaryByRegion();
      this.mainHotMapComponent!.loadInitialUbigeoInternational();
      this.cargarContinentesUbigeosByRegion();
      this.cargarTotalesByRegion();
    } else {
      this.loadGeneralSummaryByTheWorld();
      this.mainHotMapComponent!.loadInitialUbigeoWorld();
      this.cargarUbigeoDetailsByTheWorld();
      this.cargarTotalesByTheWorld();
    }

    this.updateUbigeoTitleName();
  }

  public changeRegionFromMap($event: RegionValue) {
    this.resetOrderBy();
    this.selectedFilterParams = {};
    this.initialUbigeo = undefined;
    this.mainFiltroUbigeoComponent!.syncRegionFromExternal($event);
    this.regionChanged($event);
    this.mainFiltroUbigeoComponent!.updateBreadcrumbStringFromExternal({
      idAmbitoGeografico: this.getGeographicalScope()
    });
  }

  /**
   * This method is called from map
   */
  public ubigeoParamsChangedFromMap($event: FilterByLocationParams) {
    this.resetOrderBy();
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = $event;
    if (this.regionValue == REGION_EXTRAJERO) {
      this.mainFiltroUbigeoComponent!.setUbigeoParamsExtrangero($event);
    } else {
      this.mainFiltroUbigeoComponent!.setUbigeoParams($event);
    }
    this.mainFiltroUbigeoComponent!.updateBreadcrumbStringFromExternal(getOptimizedObject({
      idAmbitoGeografico: this.getGeographicalScope(),
      ubigeoNivel1: $event.departmentUbigeoId,
      ubigeoNivel2: $event.provinceUbigeoId,
      ubigeoNivel3: $event.districtUbigeoId,
    } as GenericFilterParams));

    this.selectedFilterParams = $event;
    this.loadGeneralSummaryByUbigeo(this.getGenericFilterParams($event));

    if (districtUbigeoId && this.regionValue == REGION_PERU) {
      this.initialUbigeo = {
        ...this.initialUbigeo,
        districtUbigeoId: districtUbigeoId,
        provinceUbigeoId: provinceUbigeoId,
        departmentUbigeoId: departmentUbigeoId
      } as FilterByLocationParams;

      this.cargarTotalesByUbigeo(this.getParamsForUbigeo03());
      this.cargarUbigeosOnlyForOneDistrict(this.getParamsForUbigeo02(), Number(districtUbigeoId));
      return;
    }

    if (provinceUbigeoId) {
      this.initialUbigeo = {
        ...this.initialUbigeo,
        provinceUbigeoId: provinceUbigeoId,
        departmentUbigeoId: departmentUbigeoId
      } as FilterByLocationParams;

      this.cargarTotalesByUbigeo(this.getParamsForUbigeo02());
      if (this.regionValue == REGION_PERU) {
        this.cargarDistritosByUbigeos(provinceUbigeoId, this.getParamsForUbigeo02());
      } else {
        if (this.mainFiltroUbigeoComponent) {
          this.baseUbigeoService.loadStates(this.mainFiltroUbigeoComponent, provinceUbigeoId);
        }
        this.cargarUbigeosOnlyForOneCountry(this.getParamsForUbigeo01(), Number(provinceUbigeoId));
      }
      return;
    }

    if (departmentUbigeoId) {
      this.initialUbigeo = {
        departmentUbigeoId: departmentUbigeoId
      } as FilterByLocationParams;

      this.cargarTotalesByUbigeo(this.getParamsForUbigeo01());
      if (this.regionValue == REGION_PERU) {
        this.cargarProvinciasByUbigeos(departmentUbigeoId, this.getParamsForUbigeo01());
      } else {
        this.cargarPaisesByUbigeos(departmentUbigeoId, this.getParamsForUbigeo01());
      }
    }
  }

  private resetLevel02() {
    this.provincias = [];
    this.distritos = [];
    this.paises = [];
  }

  private resetLevel03() {
    this.distritos = [];
  }

  private getParamsForUbigeo01() {
    return {
      idAmbitoGeografico: this.getGeographicalScope(),
      tipoFiltro: UBIGEO_LEVELS.LEVEL_01,
      ubigeoNivel01: Number(this.initialUbigeo!.departmentUbigeoId),
      ubigeoNivel02: 0,
      ubigeoNivel03: 0
    } as CommonParams;
  }

  private getParamsForUbigeo02() {
    return {
      idAmbitoGeografico: this.getGeographicalScope(),
      tipoFiltro: UBIGEO_LEVELS.LEVEL_02,
      ubigeoNivel01: Number(this.initialUbigeo!.departmentUbigeoId),
      ubigeoNivel02: Number(this.initialUbigeo!.provinceUbigeoId),
      ubigeoNivel03: 0
    } as CommonParams;
  }

  private getParamsForUbigeo03() {
    return {
      idAmbitoGeografico: this.getGeographicalScope(),
      tipoFiltro: UBIGEO_LEVELS.LEVEL_03,
      ubigeoNivel01: Number(this.initialUbigeo!.departmentUbigeoId),
      ubigeoNivel02: Number(this.initialUbigeo!.provinceUbigeoId),
      ubigeoNivel03: Number(this.initialUbigeo!.districtUbigeoId)
    } as CommonParams;
  }

  /**
   * START - ACTION FROM DETAIL SECTION: This method si called from detail section
   */
  public selectCurrentUbigeoDetail(item: UbigeosDetail) {
    const { ubigeoNivel01, ubigeoNivel02, ubigeoNivel03, ambitoGeografico } = item;
    if (ambitoGeografico == 1) {
      if (Number(this.initialUbigeo?.districtUbigeoId) === item.ubigeoNivel03) {
        return;
      }
      this.resetOrderBy();

      // Only call loadGeneralSummaryByUbigeo when there are actual ubigeo levels.
      // For the region-level fall-through, regionChanged() already calls loadGeneralSummaryByRegion.
      if (ubigeoNivel01 || ubigeoNivel02 || ubigeoNivel03) {
        setTimeout(() => {
          const params: GenericFilterParams = {
            ubigeoNivel1: ubigeoNivel01 ? String(ubigeoNivel01) : undefined,
            ubigeoNivel2: ubigeoNivel02 ? String(ubigeoNivel02) : undefined,
            ubigeoNivel3: ubigeoNivel03 ? String(ubigeoNivel03) : undefined
          };
          this.loadGeneralSummaryByUbigeo(params);
        }, 100);
      }
      this.selectCurrentUbigeoDetailForPeru(item);
    } else {
      if (Number(this.initialUbigeo?.provinceUbigeoId) === item.ubigeoNivel02) {
        return;
      }
      this.resetOrderBy();

      // Only call loadGeneralSummaryByUbigeo when there are actual ubigeo levels.
      // For the region-level fall-through, regionChanged() already calls loadGeneralSummaryByRegion.
      if (ubigeoNivel01 || ubigeoNivel02) {
        setTimeout(() => {
          const params: GenericFilterParams = {
            ubigeoNivel1: ubigeoNivel01 ? String(ubigeoNivel01) : undefined,
            ubigeoNivel2: ubigeoNivel02 ? String(ubigeoNivel02) : undefined,
            ubigeoNivel3: ubigeoNivel03 ? String(ubigeoNivel03) : undefined
          };
          this.loadGeneralSummaryByUbigeo(params);
        }, 100);
      }

      this.selectCurrentUbigeoDetailForInternational(item);
    }
  }

  private getGenericFilterParams(oldParams: FilterByLocationParams) {
    const params: GenericFilterParams = {
      ubigeoNivel1: oldParams.departmentUbigeoId,
      ubigeoNivel2: oldParams.provinceUbigeoId,
      ubigeoNivel3: oldParams.districtUbigeoId
    };
    return params;
  }

  private selectCurrentUbigeoDetailForPeru(item: UbigeosDetail) {
    const { ubigeoNivel01, ubigeoNivel02, ubigeoNivel03 } = item;
    
    if (ubigeoNivel03) {
      const ubigeo = this.getStringUbigeo(ubigeoNivel03);
      this.initialUbigeo = {
        ...this.initialUbigeo,
        districtUbigeoId: ubigeo
      } as FilterByLocationParams;
      this.selectedFilterParams = this.initialUbigeo;

      this.cargarTotalesByUbigeo(this.getParamsForUbigeo03());

      this.mainHotMapComponent!.loadUbigeoDistrict(
        this.getValidUbigeo(ubigeoNivel01),
        this.getValidUbigeo(ubigeoNivel02),
        this.getValidUbigeo(ubigeoNivel03)
      );
      this.cargarUbigeosOnlyForOneDistrict(this.getParamsForUbigeo02(), ubigeoNivel03);
      this.updateUbigeoPeruInFiltersComponent(item);
      return;
    }

    if (ubigeoNivel02) {
      const ubigeo = this.getStringUbigeo(ubigeoNivel02);

      this.initialUbigeo = {
        ...this.initialUbigeo,
        provinceUbigeoId: ubigeo
      } as FilterByLocationParams;
      this.selectedFilterParams = this.initialUbigeo;

      if (this.regionValue == REGION_PERU) {
        this.cargarDistritosByUbigeos(ubigeo, this.getParamsForUbigeo02());
      }
      this.cargarTotalesByUbigeo(this.getParamsForUbigeo02());

      this.mainHotMapComponent!.loadUbigeoProvince(
        this.getValidUbigeo(ubigeoNivel01),
        this.getValidUbigeo(ubigeoNivel02)
      );
      this.updateUbigeoPeruInFiltersComponent(item);
      return;
    }

    if (ubigeoNivel01) {
      const ubigeo = this.getStringUbigeo(ubigeoNivel01);

      this.initialUbigeo = {
        departmentUbigeoId: ubigeo
      } as FilterByLocationParams;
      this.selectedFilterParams = this.initialUbigeo;

      this.cargarProvinciasByUbigeos(ubigeo, this.getParamsForUbigeo01());
      this.cargarTotalesByUbigeo(this.getParamsForUbigeo01());

      this.mainHotMapComponent!.loadUbigeoDepartamento(this.getValidUbigeo(ubigeoNivel01));
      this.updateUbigeoPeruInFiltersComponent(item);
      return;
    }

    this.mainFiltroUbigeoComponent!.syncRegionFromExternal(REGION_PERU);
    this.regionChanged(REGION_PERU);
    this.syncResponsiveBreadcrumbFromSelectedFilterParams();
  }

  private selectCurrentUbigeoDetailForInternational(item: UbigeosDetail) {
    const { ubigeoNivel01, ubigeoNivel02 } = item;

    if (ubigeoNivel02) {
      const ubigeo = this.getStringUbigeo(ubigeoNivel02);

      this.initialUbigeo = {
        ...this.initialUbigeo,
        provinceUbigeoId: ubigeo
      } as FilterByLocationParams;
      this.selectedFilterParams = this.initialUbigeo;

      if (this.mainFiltroUbigeoComponent) {
        this.mainFiltroUbigeoComponent.listContinentals = this.continentes as any;
        this.baseUbigeoService.loadCountries(this.mainFiltroUbigeoComponent, String(this.initialUbigeo.departmentUbigeoId));
        this.baseUbigeoService.loadStates(this.mainFiltroUbigeoComponent, ubigeo);
      }
      this.cargarTotalesByUbigeo(this.getParamsForUbigeo02());

      this.mainHotMapComponent!.loadUbigeoCountry(
        this.getValidUbigeo(ubigeoNivel01),
        this.getValidUbigeo(ubigeoNivel02)
      );
      this.cargarUbigeosOnlyForOneCountry(this.getParamsForUbigeo01(), ubigeoNivel02);
      this.updateUbigeoInternationalInFiltersComponent(item);
      return;
    }

    if (ubigeoNivel01) {
      const ubigeo = this.getStringUbigeo(ubigeoNivel01);

      this.initialUbigeo = {
        departmentUbigeoId: ubigeo
      } as FilterByLocationParams;
      this.selectedFilterParams = this.initialUbigeo;

      if (this.mainFiltroUbigeoComponent) {
        this.mainFiltroUbigeoComponent.listContinentals = this.continentes as any;
        this.baseUbigeoService.loadCountries(this.mainFiltroUbigeoComponent, ubigeo);
      }
      this.cargarPaisesByUbigeos(ubigeo, this.getParamsForUbigeo01());
      this.cargarTotalesByUbigeo(this.getParamsForUbigeo01());

      this.mainHotMapComponent!.loadUbigeoContinent(this.getValidUbigeo(ubigeoNivel01));
      this.updateUbigeoInternationalInFiltersComponent(item);
      return;
    }

    this.mainFiltroUbigeoComponent!.syncRegionFromExternal(REGION_EXTRAJERO);
    this.regionChanged(REGION_EXTRAJERO);
    this.syncResponsiveBreadcrumbFromSelectedFilterParams();
  }

  private updateUbigeoPeruInFiltersComponent(item: UbigeosDetail) {
    const { ubigeoNivel01, ubigeoNivel02, ubigeoNivel03 } = item;
    this.mainFiltroUbigeoComponent!.setUbigeoParams({
      departmentUbigeoId: this.getValidUbigeo(ubigeoNivel01),
      provinceUbigeoId: this.getValidUbigeo(ubigeoNivel02),
      districtUbigeoId: this.getValidUbigeo(ubigeoNivel03)
    });
    this.syncResponsiveBreadcrumbFromSelectedFilterParams();
  }

  private updateUbigeoInternationalInFiltersComponent(item: UbigeosDetail) {
    const { ubigeoNivel01, ubigeoNivel02, ubigeoNivel03 } = item;
    this.mainFiltroUbigeoComponent!.setUbigeoParamsExtrangero({
      departmentUbigeoId: this.getValidUbigeo(ubigeoNivel01),
      provinceUbigeoId: this.getValidUbigeo(ubigeoNivel02),
      districtUbigeoId: this.getValidUbigeo(ubigeoNivel03)
    });
    this.syncResponsiveBreadcrumbFromSelectedFilterParams();
  }

  private syncResponsiveBreadcrumbFromSelectedFilterParams(): void {
    this.mainFiltroUbigeoComponent!.updateBreadcrumbStringFromExternal(
      getOptimizedObject({
        idAmbitoGeografico: this.getGeographicalScope(),
        ubigeoNivel1: this.selectedFilterParams?.departmentUbigeoId,
        ubigeoNivel2: this.selectedFilterParams?.provinceUbigeoId,
        ubigeoNivel3: this.selectedFilterParams?.districtUbigeoId,
      } as GenericFilterParams)
    );
  }
  /**
   * END - ACTION FROM DETAIL SECTION: This method si called from detail section
   */

  private getValidUbigeo(ubigeo?: number): string {
    if (ubigeo && ubigeo != 0 && ubigeo != undefined) {
      const ubigeoStr = ubigeo.toString();
      return ubigeoStr.length == 5 ? '0' + ubigeoStr : ubigeoStr;
    }
    return '';
  }

  cargarChartPie(id: string) {
    am5.addLicense('AM5M357387632');
    if (!this.root) {
      this.root = am5.Root.new(id);
    }
    if (this.root._logo != undefined) {
      this.root._logo.dispose();
    }
    this.root.setThemes([am5themes_Animated.new(this.root)]);

    let chart = this.root.container.children.push(
      am5percent.PieChart.new(this.root, {
        radius: am5.percent(98),
        layout: this.root.horizontalLayout
      })
    );

    chart.root.dom.style.height = '150px';
    chart.root.dom.style.width = '150px';

    let series = chart.series.push(
      am5percent.PieSeries.new(this.root, {
        name: 'Series',
        categoryField: 'country',
        valueField: 'sales',
        alignLabels: false
      })
    );
    /*** QUITANDO LINEAS al REDEDOR DEL CIRCULO  ** */
    series.ticks.template.setAll({
      forceHidden: true
    });
    /*** FIN QUITANDO LINEAS al REDEDOR DEL CIRCULO  ** */

    const data = [
      {
        country: 'Contabilizada',
        sales: this.totalesDataDetail.porcentajeAsistentes,
        sliceSettings: {
          fill: am5.color(0x003874),
          stroke: am5.color(0x000000),
        }
      },
      {
        country: 'Enviadas al JEE',
        sales: this.totalesDataDetail.porcentajeAusentes,
        sliceSettings: {
          fill: am5.color(0x6db2e2),
          stroke: am5.color(0x000000),
        }
      },
      {
        country: 'Pendientes',
        sales: 100 - (this.totalesDataDetail.porcentajeAusentes + this.totalesDataDetail.porcentajeAsistentes),
        sliceSettings: {
          fill: am5.color(0xececec),
          stroke: am5.color(0x000000),
        }
      }
    ];

    series.slices.template.setAll({
      templateField: 'sliceSettings'
    });
    series.data.setAll(data);

    series.slices.template.set('tooltipText', '');
    series.slices.template.set('toggleKey', 'none');
    series.labels.template.setAll({
      fontSize: 10,
      width: 700,
      height: 700
    });

    series.appear();
    chart.appear();
  }

  // PRIVATE METHODS: RESUMEN GENERAL DE TOTALES

  private loadGeneralSummaryByTheWorld() {
    this.showLoading();
    this.resumenGeneral
      .getTotals$({
        idEleccion: this.electionId,
        tipoFiltro: UBIGEO_LEVELS.ELECTION
      })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.resumen = this.resumenGeneral.getFormattedSummary(response.data!);
        } else {
          console.error('getGeneralSummaryTotals error');
        }
        this.hideLoading();
      });
  }

  private loadGeneralSummaryByRegion() {
    this.showLoading();
    this.resumenGeneral
      .getTotals$({
        idAmbitoGeografico: this.getGeographicalScope(),
        idEleccion: this.electionId,
        tipoFiltro: getFilterTypeForBackend()
      })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.resumen = this.resumenGeneral.getFormattedSummary(response.data!);
        } else {
          console.error('loadPresidentialElectionInfo error');
        }
        this.hideLoading();
      });
  }

  private loadGeneralSummaryByUbigeo(params: GenericFilterParams) {
    let currentParams: TotalsParams = {
      idAmbitoGeografico: this.getGeographicalScope(),
      idEleccion: this.electionId,
      tipoFiltro: this.getGeographicalScope() == null ? UBIGEO_LEVELS.ELECTION : getFilterTypeForBackend2(params),
      idUbigeoDepartamento: params.ubigeoNivel1,
      idUbigeoProvincia: params.ubigeoNivel2,
      idUbigeoDistrito: params.ubigeoNivel3
    };

    currentParams = getOptimizedObject(currentParams);
    this.showLoading();
    this.resumenGeneral
      .getTotals$(currentParams)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.resumen = this.resumenGeneral.getFormattedSummary(response.data!);
        } else {
          console.error('loadPresidentialElectionInfo error');
        }
        this.hideLoading();
      });
  }

  private cargarTotalesByTheWorld() {
    this.showLoading();
    this.participacionCiudadanaService
      .getTotales$({ tipoFiltro: UBIGEO_LEVELS.TOTAL })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.totalesDataDetail = response.data!;
          this.cargarChartPie('divChartPie');
        }
        this.hideLoading();
      });
  }

  private cargarTotalesByRegion() {
    this.showLoading();
    this.participacionCiudadanaService
      .getTotales$({
        tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
        idAmbitoGeografico: this.getGeographicalScope()
      })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.totalesDataDetail = response.data!;
          this.cargarChartPie('divChartPie');
        }
        this.hideLoading();
      });
  }

  private cargarTotalesByUbigeo(params?: CommonParams) {
    if (params) {
      this.showLoading();
      this.participacionCiudadanaService
        .getTotales$(params as TotalsParams)
        .pipe(take(1))
        .subscribe((response) => {
          if (response.success) {
            this.totalesDataDetail = response.data!;
            this.cargarChartPie('divChartPie');
          }
          this.hideLoading();
        });
    }
  }

  /**
   * Esto carga una lista de ubigeos páginado en la parte final de la pantalla
   * @param params
   */
  private cargarUbigeos(params?: CommonParams) {
    if (params) {
      this.showLoading();
      this.updateUbigeoTitleName();
      this.participacionCiudadanaService
        .getTotalUbigeos$(params as TotalsParams)
        .pipe(take(1))
        .subscribe((response) => {
          if (response.success) {
            this.ubigeosData = response.data!;
            this.pagination = {
              arrayForPagination: this.getArrayForPagination(response.data!),
              paginaActual: 0
            };
            this.ubigeosData = this.getUbigeosWithName(this.ubigeosData);
            this.totalUbigeosData = this.ubigeosData; // Guardamos la lista completa de ubigeos

            this.ubigeosData = this.getListOfUbigeosToShow(this.getSortedValuesByAlfabeticamente(this.ubigeosData));
          }
          this.hideLoading();
        });
    }
  }

  /**
   * Esto carga una lista de ubigeos páginado en la parte final de la pantalla
   * @param params
   */
  private cargarUbigeosExtrangero(params?: CommonParams) {
    if (params) {
      this.showLoading();
      this.updateUbigeoTitleName();
      this.participacionCiudadanaService
        .getTotalUbigeos$(params as TotalsParams)
        .pipe(take(1))
        .subscribe((response) => {
          if (response.success) {
            this.ubigeosData = response.data!;
            this.pagination = {
              arrayForPagination: this.getArrayForPagination(response.data!),
              paginaActual: 0
            };
            this.ubigeosData = this.getUbigeosWithNameExtrangero(this.ubigeosData);
            this.totalUbigeosData = this.ubigeosData; // Guardamos la lista completa de ubigeos

            this.ubigeosData = this.getListOfUbigeosToShow(this.getSortedValuesByAlfabeticamente(this.ubigeosData));
          }
          this.hideLoading();
        });
    }
  }

  private cargarUbigeosOnlyForOneDistrict(params?: CommonParams, ubigeoDistrict?: number) {
    if (params) {
      this.showLoading();
      delete params.ubigeoNivel03;
      this.cargarDistritosAndCallAction(String(params.ubigeoNivel02), () => {
        this.participacionCiudadanaService
          .getTotalUbigeos$(params as TotalsParams)
          .pipe(take(1))
          .subscribe((response) => {
            if (response.success) {
              this.ubigeosData = response.data!;
              this.pagination = {
                arrayForPagination: [''],
                paginaActual: 0
              };
              this.ubigeosData = this.getUbigeosWithName(this.ubigeosData);
              this.ubigeosData = this.ubigeosData.filter((elem) => elem.ubigeoNivel03 == ubigeoDistrict);
              this.totalUbigeosData = this.ubigeosData;
              this.updateUbigeoTitleName();
            }
            this.hideLoading();
          });
      });
    }
  }

  private cargarUbigeosOnlyForOneCountry(params?: CommonParams, ubigeoCountry?: number) {
    if (params) {
      this.showLoading();
      this.cargarPaisesAndCallAction(String(params.ubigeoNivel01), () => {
        this.participacionCiudadanaService
          .getTotalUbigeos$(params as TotalsParams)
          .pipe(take(1))
          .subscribe((response) => {
            if (response.success) {
              this.ubigeosData = response.data!;
              this.pagination = {
                arrayForPagination: [''],
                paginaActual: 0
              };
              this.ubigeosData = this.getUbigeosWithNameExtrangero(this.ubigeosData);
              this.ubigeosData = this.ubigeosData.filter((elem) => elem.ubigeoNivel02 == ubigeoCountry);
              this.totalUbigeosData = this.ubigeosData;
              this.updateUbigeoTitleName();
            }
            this.hideLoading();
          });
      });
    }
  }

  /**
   * Aquí se hace la carga de las demás páginas del detalle de los ubigeos
   * @param page
   */
  public cargarUbigeosFromPage(page: number) {
    this.ubigeosData = this.totalUbigeosData.filter((_, index) => {
      return this.numberOfElemForPage * page <= index && index < this.numberOfElemForPage * (page + 1);
    });
    this.pagination = {
      arrayForPagination: this.getArrayForPagination(this.totalUbigeosData),
      paginaActual: page
    };
  }

  private cargarUbigeoDetailsByTheWorld(): void {
    this.showLoading();
    this.updateUbigeoTitleName();
    this.participacionCiudadanaService
      .getTotalUbigeos$({ tipoFiltro: UBIGEO_LEVELS.TOTAL })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.ubigeosData = response.data!;
          this.pagination = {
            arrayForPagination: this.getArrayForPagination(response.data!),
            paginaActual: 0
          };
          this.ubigeosData = this.ubigeosData.map((elem) => {
            const name = elem.ambitoGeografico == 2 ? 'EXTRANJERO' : 'PERÚ';
            return { ...elem, name: name };
          });
          this.totalUbigeosData = this.ubigeosData; // Guardamos la lista completa de ubigeos

          this.ubigeosData = this.getListOfUbigeosToShow(this.getSortedValuesByAlfabeticamente(this.ubigeosData));
        }
        this.hideLoading();
      });
  }

  public getListOfUbigeosToShow(data: UbigeosDetail[]): UbigeosDetail[] {
    return data.filter((_, index) => {
      return index < this.numberOfElemForPage;
    });
  }

  public sortUbigeosDetails(event: Event): void {
    const type = (event.target as HTMLSelectElement).value;

    switch (type) {
      case 'menor':
        this.totalUbigeosData.sort((a, b) => a.porcentajeAsistentes! - b.porcentajeAsistentes!);
        break;
      case 'mayor':
        this.totalUbigeosData.sort((a, b) => b.porcentajeAsistentes! - a.porcentajeAsistentes!);
        break;
      default:
        this.totalUbigeosData = this.getSortedValuesByAlfabeticamente(this.totalUbigeosData);
        break;
    }

    this.ubigeosData = this.getListOfUbigeosToShow(this.totalUbigeosData);
    this.pagination = {
      arrayForPagination: this.getArrayForPagination(this.totalUbigeosData),
      paginaActual: 0
    };
  }

  private getSortedValuesByAlfabeticamente(data: UbigeosDetail[]): UbigeosDetail[] {
    return data.sort((a, b) => a.name!.localeCompare(b.name!, 'es', { sensitivity: 'base' }));
  }

  private getArrayForPagination(data: UbigeosDetail[]) {
    const arrayForPagination = Math.ceil(data.length / this.numberOfElemForPage);
    return new Array(arrayForPagination).fill('');
  }

  private getUbigeosWithName(data: UbigeosDetail[]) {
    return data.map((elem) => {
      const { ubigeoNivel01, ubigeoNivel02, ubigeoNivel03 } = elem;
      if (ubigeoNivel03) {
        const current = this.distritos.find((m) => Number(m.ubigeo) == ubigeoNivel03);
        return { ...elem, name: current!.nombre };
      }
      if (ubigeoNivel02) {
        const current = this.provincias.find((m) => Number(m.ubigeo) == ubigeoNivel02);
        return { ...elem, name: current!.nombre };
      }
      if (ubigeoNivel01) {
        const current = this.departamentos.find((m) => Number(m.ubigeo) == ubigeoNivel01);
        return { ...elem, name: current!.nombre };
      }
      return elem;
    });
  }

  private getUbigeosWithNameExtrangero(data: UbigeosDetail[]) {
    return data.map((elem) => {
      const { ubigeoNivel01, ubigeoNivel02 } = elem;
      if (ubigeoNivel02) {
        const current = this.paises.find((m) => Number(m.ubigeo) == ubigeoNivel02);
        return { ...elem, name: current!.nombre };
      }
      if (ubigeoNivel01) {
        const current = this.continentes.find((m) => Number(m.ubigeo) == ubigeoNivel01);
        return { ...elem, name: current!.nombre };
      }
      return elem;
    });
  }



  private cargarDepartamentosUbigeosByRegion(): void {
    this.baseUbigeoService
      .getDepartments$({
        idAmbitoGeografico: this.getGeographicalScope() ?? 0,
        idEleccion: this.electionId
      })
      .pipe(take(1))
      .subscribe((resp) => {
        this.departamentos = resp.data!;
        if (this.mainFiltroUbigeoComponent) {
          this.mainFiltroUbigeoComponent.listDepartamento = resp.data as any;
        }
        this.resetLevel02();
        const currentParams = {
          idAmbitoGeografico: this.getGeographicalScope(),
          tipoFiltro: UBIGEO_LEVELS.ALL_LABEL
        } as CommonParams;
        this.cargarUbigeos(currentParams);
      });
  }

  private cargarProvinciasByUbigeos(departamento: string, params: CommonParams): void {
    this.cargarDepartamentosAndCallAction(() => {
      this.baseUbigeoService
        .getProvinces$({
          idAmbitoGeografico: this.getGeographicalScope() ?? 0,
          idEleccion: this.electionId,
          idUbigeoDepartamento: departamento
        })
        .pipe(take(1))
        .subscribe((resp) => {
          this.provincias = resp.data!;
          if (this.mainFiltroUbigeoComponent) {
            this.mainFiltroUbigeoComponent.listProvincia = resp.data as any;
          }
          this.resetLevel03();
          this.cargarUbigeos(params);
        });
    });
  }

  private cargarDistritosByUbigeos(provincia: string, params: CommonParams): void {
    this.cargarProvinciasAndCallAction(String(params.ubigeoNivel01), () => {
      this.cargarDistritosAndCallAction(provincia, () => {
        this.cargarUbigeos(params);
      });
    });
  }

  /**
   * this method only load department list
   */
  private cargarDepartamentosAndCallAction(action: () => void): void {
    if (this.departamentos.length > 0) {
      action();
    } else {
      this.baseUbigeoService
        .getDepartments$({
          idAmbitoGeografico: this.getGeographicalScope() ?? 0,
          idEleccion: this.electionId
        })
        .pipe(take(1))
        .subscribe((resp) => {
          this.departamentos = resp.data!;
          if (this.mainFiltroUbigeoComponent) {
            this.mainFiltroUbigeoComponent.listDepartamento = resp.data as any;
          }
          this.resetLevel02();
          action();
        });
    }
  }

  /**
   * this method only load province list
   */
  private cargarProvinciasAndCallAction(departamento: string, action: () => void) {
    if (this.provincias.length > 0) {
      action();
    } else {
      this.baseUbigeoService
        .getProvinces$({
          idAmbitoGeografico: this.getGeographicalScope() ?? 0,
          idEleccion: this.electionId,
          idUbigeoDepartamento: departamento
        })
        .pipe(take(1))
        .subscribe((resp) => {
          this.provincias = resp.data!;
          if (this.mainFiltroUbigeoComponent) {
            this.mainFiltroUbigeoComponent.listProvincia = resp.data as any;
          }
          this.resetLevel03();
          action();
        });
    }
  }

  /**
   * this method only load district list
   */
  private cargarDistritosAndCallAction(provincia: string, action: () => void) {
    if (this.distritos && this.distritos.length > 0) {
      action();
    } else {
      this.baseUbigeoService
        .getDistricts$({
          idAmbitoGeografico: this.getGeographicalScope() ?? 0,
          idEleccion: this.electionId,
          idUbigeoProvincia: provincia
        })
        .pipe(take(1))
        .subscribe((resp) => {
          this.distritos = resp.data!;
          if (this.mainFiltroUbigeoComponent) {
            this.mainFiltroUbigeoComponent.listDistrito = resp.data as any;
          }
          action();
        });
    }
  }

  /**
   * this method only load district list
   */
  private cargarContinentsAndCallAction(action: () => void) {
    if (this.continentes.length > 0) {
      if (this.mainFiltroUbigeoComponent) {
        this.mainFiltroUbigeoComponent.listContinentals = this.continentes as any;
      }
      action();
    } else {
      this.baseUbigeoService
        .getInternationalContinetals$({
          idAmbitoGeografico: this.getGeographicalScope() ?? 0,
          idEleccion: this.electionId
        })
        .pipe(take(1))
        .subscribe((resp) => {
          this.continentes = resp.data!;
          if (this.mainFiltroUbigeoComponent) {
            this.mainFiltroUbigeoComponent.listContinentals = resp.data as any;
          }
          this.resetLevel02();
          action();
        });
    }
  }

  private cargarContinentesUbigeosByRegion(): void {
    this.baseUbigeoService
      .getInternationalContinetals$({
        idAmbitoGeografico: this.getGeographicalScope() ?? 0,
        idEleccion: this.electionId
      })
      .pipe(take(1))
      .subscribe((resp) => {
        this.continentes = resp.data!;
        if (this.mainFiltroUbigeoComponent) {
          this.mainFiltroUbigeoComponent.listContinentals = resp.data as any;
        }
        this.resetLevel02();
        const currentParams = {
          idAmbitoGeografico: this.getGeographicalScope(),
          tipoFiltro: UBIGEO_LEVELS.ALL_LABEL
        } as CommonParams;
        this.cargarUbigeosExtrangero(currentParams);
      });
  }

  private cargarPaisesByUbigeos(continente: string, params: CommonParams): void {
    this.cargarContinentsAndCallAction(() => {
      this.cargarPaisesAndCallAction(continente, () => {
        this.cargarUbigeosExtrangero(params);
      });
    });
  }

  private cargarPaisesAndCallAction(continente: string, action: () => void) {
    this.baseUbigeoService
      .getInternationalCountries$({
        idAmbitoGeografico: this.getGeographicalScope() ?? 0,
        idEleccion: this.electionId,
        idUbigeo: continente
      })
      .pipe(take(1))
      .subscribe((resp) => {
        this.paises = resp.data!;
        if (this.mainFiltroUbigeoComponent) {
          this.mainFiltroUbigeoComponent.listCountries = resp.data as any;
        }
        this.resetLevel03();
        this.updateUbigeoTitleName();
        action();
      });
  }


  public getLocaleString(numberValue: number | null): string {
    return formatNumberWithApostrophe(numberValue);
  }  

  private showLoading() {
    // Loading functionality removed
  }

  private hideLoading() {
    setTimeout(() => {
      // Loading functionality removed
    }, 500);
  }

  cleanFiltersEvent(){
    this.changeRegionFromMap(REGION_TODOS);
  }
}
