import { Component, ElementRef, EventEmitter, Input, Output, ViewChild, HostListener, OnDestroy } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { take, debounceTime, distinctUntilChanged, Subject, takeUntil } from "rxjs";
import { IChartBarInfo } from "../../interfaces/chart-bar-info.interface";
import { TIME_TO_LOADING, UBIGEO_LEVELS } from "../../helpers/constantes";
import { getGeograpScopeByRegion } from "../../helpers/election-type.config";
import { getGenericFilterType, getGenericGeographicalScope } from "../../helpers/filters-helper.common";
import {
  makeFormattedInformationForBars,
  makeScaleValues,
  makeScaleValuesFromGenericArray,
} from "../../helpers/handler-chart-data.common";
import { commonUpdateMapFromFilter } from "../../helpers/map.update-map-from-filter";
import { getFilterTypeForBackend } from "../../helpers/ubigeo-level.common";
import {
  RegionValue,
  FilterByLocationParams,
  GenericFilterParams,
  REGION_PERU,
  REGION_EXTRAJERO,
  getOptimizedObject,
} from "../../interfaces/filtro-settings";
import { MesasDetail, MesasDetailParams } from "../../interfaces/mesas-de-votacion.interfaces";
import { ParlGeographicalLocationNameParams } from "../../interfaces/parlamento.interfaces";
import {
  GeographicalLocationItem,
  GeographicalLocationNameItem,
  GeographicalLocationParams,
} from "../../interfaces/presidenciales.interfaces";
import { Resumen } from "../../interfaces/resumen-bean";
import { MesasDeVotacionService } from "../../services/elecciones-generales/mesas-de-votacion.service";
import { ParlamentoService } from "../../services/elecciones-generales/parlamento.service";
import { GenericFilterUbigeoComponent } from "../generic-filter-ubigeo/generic-filter-ubigeo.component";
import { MainHotMapComponent } from "../main-hot-map/main-hot-map.component";
import { ModalDetailVotes } from "../../interfaces/modal-detail-votes.interface";
import { ModalDetailVotesComponent } from "../modal-detail-votes/modal-detail-votes.component";
import { MatDialog } from "@angular/material/dialog";
import { ModalDetailVotesService } from "../../services/common/modal-detail-votes.service";
import { DELAY_SEARCH } from "../../constants/search-incremental.constants";
import { getPoliticImageFromAssets, mapWithPoliticImage } from "../../helpers/get-images.helper";

@Component({
  selector: "app-tab-ubicacion-geografica-parlamento",
  templateUrl: "./tab-ubicacion-geografica-parlamento.component.html",
  styleUrls: ["tab-ubicacion-geografica-parlamento.component.scss"],
  standalone: false,
})
export class TabUbicacionGeograficaParlamentoComponent implements OnDestroy {

  public OrganizacionesPoliticasKey = 'tab-ubicacion.organizacionesPoliticas';
  public BuscarKey = 'tab-ubicacion.buscar';
  public DescripcionKey = 'tab-ubicacion.votosEmitidos';
  public LimpiarKey = 'tab-ubicacion.limpiar';
  public VotosEmitidosKey = 'tab-ubicacion.votosEmitidos';
  public VotosValidadosKey = 'tab-ubicacion.votosValidados';
  public TotalCandidatosKey = 'tab-ubicacion.totalCandidatos';
  public VotosEmitidosSPKey = 'tab-ubicacion.votosEmitidosSp';
  public VotosValidadosSPKey = 'tab-ubicacion.votosValidadosSp';
  public VotosTotalesKey = 'tab-ubicacion.votosTotalesp';
  public CantidadVotosKey = 'tab-ubicacion.votosValidadosSp';
  public cantidadVotos = 'tab-ubicacion.cantidadVotos';

  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;

    if (!this.esPantallaChica) {
      this.mostrarMapa2 = true;
    } else {
      this.mostrarMapa2 = false;
    }
  }

  toggleMapa() {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  @ViewChild(MainHotMapComponent) mainHotMapComponent: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent)
  mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;

  @Output() scrollToTopEvent = new EventEmitter();
  @Output() regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();

  @Input() resumen: Resumen;

  public geographicalLocationItems: GeographicalLocationItem[] = [];
  public geographicalLocationNameItems: GeographicalLocationNameItem[] = [];
  public geographicalLocationNameItemsTemp: GeographicalLocationNameItem[] = [];
  public geographicalLocationNameItemsComplete: GeographicalLocationNameItem[] = [];

  public emptyVotes: GeographicalLocationNameItem = null;
  public nullVotes: GeographicalLocationNameItem = null;
  public totalVotes: GeographicalLocationNameItem = null;
  public showSpecialCount = true;

  public geographicalLocationNameFirstRequestIsLoaded = false;

  public myFormBusCandidato: FormGroup = this.fb.group({
    nomCandidato: ["", Validators.required],
  });

  private regionValue: RegionValue = "TODOS";

  private filters: FilterByLocationParams = {} as FilterByLocationParams;

  // BAR CHART
  public scalesForDistrict: number[] = [];
  public dataForDistrict: IChartBarInfo[] = [];

  // SUMMARY BY NAME
  public maxValueForScaleName: number[] = [];
  public mesasDetail: MesasDetail = {} as MesasDetail;

  @Input() electionId: number;
  @Input() parlamentoService: ParlamentoService;

  private destroy$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly mesasDeVotacionService: MesasDeVotacionService,
    private elementRef: ElementRef,
    private readonly modalDetailVotesService: ModalDetailVotesService,
    private readonly dialog: MatDialog
  ) {}

  private showLoading() {
    // Loading functionality removed
    setTimeout(() => {
      // Loading functionality removed
    }, TIME_TO_LOADING);
  }

  // LIFE CYCLE

  ngOnInit(): void {
    this.showLoading();
    this.loadInitialParticipantsByGeographicalLocationName();
    this.loadMesasDetailByTheWorld();

    // Configurar búsqueda incremental con debounce de 500ms
    this.myFormBusCandidato.get('nomCandidato')?.valueChanges
      .pipe(
        debounceTime(DELAY_SEARCH),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe((searchTerm: string) => {
        this.performSearch(searchTerm);
      });
  }

  ngAfterViewInit(): void {
    if (this.mainHotMapComponent) {
      this.mainHotMapComponent.loadInitialUbigeoWorld();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public scrollToTopSection(): void {
    //this.scrollToTopEvent.emit();
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  // FILTRAR GRAFICO DE BARRAS Y RESUMEN DE CANDIDATOS POR UBIGEOS

  public filterParticipantsByGeographicalLocation(params: FilterByLocationParams) {
    this.filters = params;
    this.filterByLocationParamsEvent.emit(params);

    let paramsBase = {
      idEleccion: this.electionId,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      tipoFiltro: getFilterTypeForBackend(params),
      ubigeoNivel1: params.departmentUbigeoId,
      ubigeoNivel2: params.provinceUbigeoId,
      ubigeoNivel3: params.districtUbigeoId,
    };

    const currentParams2 = paramsBase as ParlGeographicalLocationNameParams;
    this.loadParticipantsByGeographicalLocationName(currentParams2);
    this.loadMesasDetailByUbigeo();
    this.updateMapFromFilter();
  }

  public regionChanged($event: RegionValue) {
    this.cleanInformation($event);
    this.regionChangedEvent.emit($event);
  }

  public cleanInformation($event: RegionValue) {
    this.filters = {} as FilterByLocationParams;
    this.regionValue = $event;
    if ($event == REGION_PERU) {
      this.mainHotMapComponent.loadInitialUbigeoPeru();
      this.loadInitialUbigeoBase();
    } else if ($event == REGION_EXTRAJERO) {
      this.mainHotMapComponent.loadInitialUbigeoInternational();
      this.loadInitialUbigeoBase();
    } else {
      this.loadInitialUbigeoBaseWorld();
    }
  }

  private loadInitialUbigeoBase() {
    this.loadInitialParticipantsByGeographicalLocationNameByRegion();
    this.loadMesasDetailByUbigeo();
  }

  private loadInitialUbigeoBaseWorld() {
    this.mainHotMapComponent.loadInitialUbigeoWorld();
    this.loadInitialParticipantsByGeographicalLocationName();
    this.loadMesasDetailByTheWorld();
  }

  public applyFiltersEvent(params: GenericFilterParams) {
    const customParamsMesasDetail = {
      ...params,
      ambitoGeografico: params.idAmbitoGeografico,
    };
    delete customParamsMesasDetail.idAmbitoGeografico;
    this.loadMesasDetail(customParamsMesasDetail);

    const customParamsGeographical = { ...params, idEleccion: this.electionId };
    this.loadParticipantsByGeographicalLocationName(customParamsGeographical as ParlGeographicalLocationNameParams);
    this.updateFiltersFromNewEvent.emit(params);

    if (params.idAmbitoGeografico) {
      this.regionValue = params.idAmbitoGeografico == 1 ? "PERÚ" : "EXTRANJERO";
    } else {
      this.regionValue = "TODOS";
    }
    this.filters = {
      region: params.idAmbitoGeografico,
      departmentUbigeoId: params.ubigeoNivel1,
      provinceUbigeoId: params.ubigeoNivel2,
      districtUbigeoId: params.ubigeoNivel3,
    };
    this.filters = getOptimizedObject<FilterByLocationParams>(this.filters);
    this.updateMapFromFilter();
  }

  private updateMapFromFilter() {
    commonUpdateMapFromFilter(this.filters, this.mainHotMapComponent);
  }

  private loadInitialParticipantsByGeographicalLocationName() {
    const currentParams = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ELECTION,
    } as ParlGeographicalLocationNameParams;
    this.loadParticipantsByGeographicalLocationName(currentParams);
  }

  private loadInitialParticipantsByGeographicalLocationNameByRegion() {
    const currentParams = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
    } as ParlGeographicalLocationNameParams;
    this.loadParticipantsByGeographicalLocationName(currentParams);
  }

  private getFilterType(): string {
    const filterType = getGenericFilterType("peru_intern_and_all_join", this.regionValue, {
      departmentUbigeoId: this.filters.departmentUbigeoId,
      provinceUbigeoId: this.filters.provinceUbigeoId,
      districtUbigeoId: this.filters.districtUbigeoId,
    });
    return filterType;
  }

  private getGeographicalScopeNew(): number | null {
    return getGenericGeographicalScope("peru_intern_and_all_join", this.regionValue);
  }

  private updateFiltersExternalEvent($event: FilterByLocationParams) {
    const newParams = getOptimizedObject({
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScopeNew(),
      ubigeoNivel1: $event.departmentUbigeoId,
      ubigeoNivel2: $event.provinceUbigeoId,
      ubigeoNivel3: $event.districtUbigeoId,
    } as GenericFilterParams);
    this.updateFiltersFromNewEvent.emit(newParams);

    // IMPORTANT: update filter breadcrumb and responsive state to reflect map navigation
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal(newParams);
  }

  /**
   * This method is called from map
   */
  public ubigeoParamsChangedFromMap($event: FilterByLocationParams): void {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId, regionString } = $event;

    if (regionString) {
      this.regionValue = regionString;
    }
    if (regionString == "EXTRANJERO") {
      this.mainFiltroUbigeoComponent.setUbigeoParamsExtrangero($event);
    } else {
      this.mainFiltroUbigeoComponent.setUbigeoParams($event);
    }

    const levels = [
      {
        id: districtUbigeoId,
        level: UBIGEO_LEVELS.LEVEL_03,
        key: "districtUbigeoId",
      },
      {
        id: provinceUbigeoId,
        level: UBIGEO_LEVELS.LEVEL_02,
        key: "provinceUbigeoId",
      },
      {
        id: departmentUbigeoId,
        level: UBIGEO_LEVELS.LEVEL_01,
        key: "departmentUbigeoId",
      },
    ];

    for (const { id, level, key } of levels) {
      if (id) {
        // For department-level clicks, fully reset lower-level ubigeos (no stale province/district).
        // For province/district-level clicks, spread to keep parent levels.
        if (key === 'departmentUbigeoId') {
          this.filters = { departmentUbigeoId: id } as FilterByLocationParams;
        } else {
          this.filters = { ...this.filters, [key]: id } as FilterByLocationParams;
        }

        const currentParams: GeographicalLocationParams & ParlGeographicalLocationNameParams = {
          idEleccion: this.electionId,
          idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
          tipoFiltro: level,
          ubigeoNivel1: this.filters.departmentUbigeoId || "",
          ubigeoNivel2: this.filters.provinceUbigeoId || "",
          ubigeoNivel3: this.filters.districtUbigeoId || "",
        };

        this.loadParticipantsByGeographicalLocationName(currentParams);
        this.loadMesasDetailByUbigeo();
        this.updateFiltersExternalEvent($event);
        break;
      }
    }
  }

  // RESUMEN DE TOTALES

  public get filteredDataForDistrict(): IChartBarInfo[] {
    return this.dataForDistrict.filter((_, index) => {
      return index < 100;
    });
  }

  // MAPA

  public changeRegionFromMap($event: RegionValue) {
    this.regionChanged($event);
    this.mainFiltroUbigeoComponent.ubigeoForm.controls.region.setValue($event, { emitEvent: false });
    this.mainFiltroUbigeoComponent.regionChanged();

    // IMPORTANT: update filter breadcrumb and responsive state to reflect region change from map
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal({
      idAmbitoGeografico: this.getGeographicalScopeNew()
    });
  }

  // METHODS GENERICS FOR THIS COMPONENT

  private performSearch(searchTerm: string): void {
    const enteredName = (searchTerm ?? "").toLowerCase();
    
    this.geographicalLocationNameItems = mapWithPoliticImage(
      this.geographicalLocationNameItemsTemp.filter((x) =>
        x.nombreAgrupacionPolitica.toLowerCase().includes(enteredName)
      )
    );

    this.showSpecialCount = !enteredName;
  }

  public submitFormBusCandidato(): void {
    // Este método ahora se maneja automáticamente por la búsqueda incremental
    // Se mantiene por compatibilidad hacia atrás pero ya no es necesario
    const searchTerm = this.myFormBusCandidato.get("nomCandidato").value;
    this.performSearch(searchTerm);
  }

  public resetListaCandidatos() {    
    this.geographicalLocationNameItems = mapWithPoliticImage(this.geographicalLocationNameItemsComplete);
    this.myFormBusCandidato.get("nomCandidato").setValue("");
    this.showSpecialCount = true;
  }

  public scrollToBottomSection(): void {
    const element = this.elementRef.nativeElement.querySelector(`#bottom-section`);
    if (element) {
      element.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  }

  // PRIVATE METHODS

  private loadMesasDetailByTheWorld() {
    this.mesasDeVotacionService
      .getMesasDetail$({
        tipoFiltro: UBIGEO_LEVELS.ELECTION,
      })
      .pipe(
      takeUntil(this.destroy$),take(1))
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

  private loadMesasDetailByUbigeo() {
    this.mesasDeVotacionService
      .getMesasDetail$({
        ambitoGeografico: getGeograpScopeByRegion(this.regionValue),
        tipoFiltro: getFilterTypeForBackend(this.filters),
        ubigeoNivel1: this.filters.departmentUbigeoId,
        ubigeoNivel2: this.filters.provinceUbigeoId,
        ubigeoNivel3: this.filters.districtUbigeoId,
      })
      .pipe(
      takeUntil(this.destroy$),take(1))
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

  private loadMesasDetail(params: MesasDetailParams) {
    this.mesasDeVotacionService
      .getMesasDetail$(params)
      .pipe(
      takeUntil(this.destroy$), take(1))
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }


  private loadParticipantsByGeographicalLocationName(params: ParlGeographicalLocationNameParams) {
    let nombreAgrupacion = this.myFormBusCandidato.get("nomCandidato").value;
    this.dataForDistrict = [];
    this.parlamentoService
      .getParticipantsByGeographicalLocationName$(params)
      .pipe(
        takeUntil(this.destroy$),
        take(1))
      .subscribe((response) => {
        this.geographicalLocationNameFirstRequestIsLoaded = true;
        if (response.success) {
          this.geographicalLocationNameItems = mapWithPoliticImage(this.getSortedGeographicalLocationNames(response.data.list));
          this.geographicalLocationNameItemsTemp = [...this.geographicalLocationNameItems];
          if(nombreAgrupacion && nombreAgrupacion.trim() !== ''){
            const filtro = nombreAgrupacion.trim().toLowerCase();
            this.geographicalLocationNameItems = mapWithPoliticImage(
              this.geographicalLocationNameItems.filter(
                (org) =>
                  org.nombreAgrupacionPolitica &&
                  org.nombreAgrupacionPolitica.toLowerCase().includes(filtro)
              )
            );
          }else{
            this.geographicalLocationNameItems = mapWithPoliticImage([...this.geographicalLocationNameItemsTemp]);
          }
          
          this.dataForDistrict = makeFormattedInformationForBars(this.getNumberOfPoliticalOrganizations(response.data.listForScales));
          this.scalesForDistrict = makeScaleValues(response.data.listForScales, 10);
          this.geographicalLocationNameItemsComplete = this.geographicalLocationNameItems;
          this.emptyVotes = response.data.emptyVotes;
          this.nullVotes = response.data.nullVotes;
          this.totalVotes = response.data.totals;
          this.maxValueForScaleName = makeScaleValuesFromGenericArray(
            response.data.listForScales.map((item) => item.totalVotosValidos),
            5
          ).reverse();
        } else {
          console.log("getParticipantsByGeographicalLocation error");
        }
      });
  }

  private getSortedGeographicalLocationNames(list: any[]): any[] {
    return list.sort(this.customOrderBy);
  }

  private customOrderBy(a: GeographicalLocationNameItem, b: GeographicalLocationNameItem): number {
    return b.totalVotosValidos - a.totalVotosValidos;
  }

  public getPercentageOfEmptyAndNullVotes(totalVotes: number): string {
    if (this.maxValueForScaleName.length == 0) {
      return "0%";
    }

    const maxValue = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    const agregado = maxValue * 0.05;
    const votoCalculado = maxValue + agregado;
    const percentage = (totalVotes / votoCalculado) * 100;
    const currentPercentage = percentage > 100 ? 100 : percentage;
    return (maxValue != 0 ? currentPercentage : 0) + "%";
  }

  public getPosicionRegla(valor: number, index: number): string {
    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    let retirado = (valorMaximo * 0.05);
    let votoCalculado = valorMaximo + retirado;
    let valorRetorno = (valor * 100) / votoCalculado;

    if(this.maxValueForScaleName.length - 1 == index) {
      return '100%';
    }

    return valorRetorno + '%';
  }

  public getReglaValorIteracion(valor: number, index: number): number {
    if(this.maxValueForScaleName.length - 1 == index) {
      return valor + (this.maxValueForScaleName[this.maxValueForScaleName.length - 1] == 300 ? 0 : 1);
    }

    return valor;
  }

  detailVote(value: GeographicalLocationNameItem): void {
    if(this.esPantallaChica) {
      const data: Partial<ModalDetailVotes> = {
        // Partido Politico
        politicalPartyImage: getPoliticImageFromAssets(value.codigoAgrupacionPolitica.toString().padStart(8, '0')),
        politicalPartyName: value.nombreAgrupacionPolitica,
        // Candidato
        candidateImageShow: false,
        candidateNameShow: false,
        // Votos
        votesNumber: value.totalVotosValidos,
        // Votos Emitidos
        votesEmittedPercentage: value.porcentajeVotosEmitidos,
        // Votos Validos
        votesValidPercentage: value.porcentajeVotosValidos,
      };
      this.openModalDetailVotes(data);
    }
  }

  detailTypeVotes(value: GeographicalLocationNameItem, title: string): void {
    if(!this.esPantallaChica) {
      return;
    }
    const data: Partial<ModalDetailVotes> = {
      // Partido Politico
      politicalPartyShow: false,
      // Candidato
      candidateImageShow: false,
      candidateName: title,
      // Votos
      votesNumber: value.totalVotosValidos,
      // Votos Emitidos
      votesEmittedPercentage: value.porcentajeVotosEmitidos,
      // Votos Validos
      votesValidPercentage: value.porcentajeVotosValidos,
    };
    this.openModalDetailVotes(data);
  }

  private openModalDetailVotes(data: Partial<ModalDetailVotes>): void {
    this.modalDetailVotesService.setData(data);
    this.dialog.open(ModalDetailVotesComponent, {
      width: '400px',
      maxWidth: '80vw',
      panelClass: 'popup-votos-detalle',
    });
  }

  getNumberOfPoliticalOrganizations(geographicalLocationNameItems: GeographicalLocationNameItem[]) {
    return geographicalLocationNameItems.filter(org =>
      org.codigoAgrupacionPolitica !== "80" &&
      org.codigoAgrupacionPolitica !== "81" &&
      org.totalCandidatos > 0
    );
  }
}
