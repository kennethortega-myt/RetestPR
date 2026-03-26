import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnDestroy,
  Output,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime, distinctUntilChanged, Subject, take, takeUntil } from 'rxjs';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { MainHotMapComponent } from '../../../../../components/main-hot-map/main-hot-map.component';
import { ModalDetailVotesComponent } from '../../../../../components/modal-detail-votes/modal-detail-votes.component';
import { TIME_TO_LOADING, UBIGEO_LEVELS } from '../../../../../helpers/constantes';
import { getGeograpScopeByRegion } from '../../../../../helpers/election-type.config';
import { getGenericFilterType, getGenericGeographicalScope } from '../../../../../helpers/filters-helper.common';
import {
  makeFormattedInformationForBars,
  makeScaleValues,
  makeScaleValuesFromGenericArray
} from '../../../../../helpers/handler-chart-data.common';
import { commonUpdateMapFromFilter } from '../../../../../helpers/map.update-map-from-filter';
import { getFilterTypeForBackend } from '../../../../../helpers/ubigeo-level.common';
import { IChartBarInfo } from '../../../../../interfaces/chart-bar-info.interface';
import { DetalleDataPresidencialBean } from '../../../../../interfaces/detalle-data-presidencial-bean';
import {
  FilterByLocationParams,
  GenericFilterParams,
  getOptimizedObject,
  REGION_EXTRAJERO,
  REGION_PERU,
  RegionValue
} from '../../../../../interfaces/filtro-settings';
import { MesasDetail, MesasDetailParams } from '../../../../../interfaces/mesas-de-votacion.interfaces';
import { ModalDetailVotes } from '../../../../../interfaces/modal-detail-votes.interface';
import {
  CommonPresidentialParams,
  GeographicalLocationItem,
  GeographicalLocationNameInfo,
  GeographicalLocationNameItem,
  GeographicalLocationNameParams,
  GeographicalLocationParams
} from '../../../../../interfaces/presidenciales.interfaces';
import { FrontendResponse } from '../../../../../interfaces/response.common';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { ModalDetailVotesService } from '../../../../../services/common/modal-detail-votes.service';
import { MesasDeVotacionService } from '../../../../../services/elecciones-generales/mesas-de-votacion.service';
import { PresidencialesService } from '../../../../../services/elecciones-generales/presidenciales.service';
import { DELAY_SEARCH } from '../../../../../constants/search-incremental.constants';
import { mapWithPoliticImage } from '../../../../../helpers/get-images.helper';

@Component({
  selector: 'app-tab-ubicacion-geografica-presidenciales',
  templateUrl: './tab-ubicacion-geografica-presidenciales.component.html',
  styleUrls: ['./tab-ubicacion-geografica-presidenciales.component.scss'],
  standalone: false
})
export class TabUbicacionGeograficaPresidencialesComponent implements AfterViewInit, OnDestroy {
  readonly dialog = inject(MatDialog);
  private formBuilder = inject(FormBuilder);
  @Input() electionID: number;
  @Input() resumen: Resumen;
  @Output() scrollToTopEvent = new EventEmitter();
  @Output() regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();
  @ViewChild(MainHotMapComponent) mainHotMapComponent: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent)
  mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  dataPresidencial: DetalleDataPresidencialBean;
  geographicalLocationItems: GeographicalLocationItem[] = [];
  geographicalLocationNameItems: GeographicalLocationNameItem[] = [];
  geographicalLocationNameItemsComplete: GeographicalLocationNameItem[] = [];
  emptyVotes: GeographicalLocationNameItem = null;
  nullVotes: GeographicalLocationNameItem = null;
  totalVotes: GeographicalLocationNameItem = null;
  showSpecialCount = true;
  scalesForDistrict: number[] = [];
  dataForDistrict: IChartBarInfo[] = [];
  maxValueForScaleName: number[] = [];
  mesasDetail: MesasDetail = {} as MesasDetail;
  geographicalLocationNameFirstRequestIsLoaded = false;
  myFormBusCandidato: FormGroup = this.formBuilder.group({
    nomCandidato: ['', Validators.required]
  });
  private filters: FilterByLocationParams = {} as FilterByLocationParams;
  private regionValue: RegionValue = 'TODOS';
  private destroy$ = new Subject<void>();

  constructor(
    private readonly presidencialesService: PresidencialesService,
    private readonly mesasDeVotacionService: MesasDeVotacionService,
    private elementRef: ElementRef,
    private readonly modalDetailVotesService: ModalDetailVotesService
  ) {}

  ngOnInit(): void {
    this.showLoading();
    this.loadInitialParticipantsByGeographicalLocationName();
    this.loadMesasDetailByTheWorld();
    this.myFormBusCandidato
      .get('nomCandidato')
      ?.valueChanges.pipe(debounceTime(DELAY_SEARCH), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((searchTerm: string) => {
        this.performSearch(searchTerm);
      });
  }

  ngAfterViewInit(): void {
    if (this.mainHotMapComponent) {
      this.mainHotMapComponent.loadInitialUbigeoWorld();
    }
  }

  @HostListener('window:resize', ['$event']) onResize(event: any): void {
    this.esPantallaChica = event.target.innerWidth < 960;
    if (!this.esPantallaChica) {
      this.mostrarMapa2 = true;
    } else {
      this.mostrarMapa2 = false;
    }
  }

  toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  detalleVotos(candidate: GeographicalLocationNameItem): void {
    const data: Partial<ModalDetailVotes> = {
      // Partido Politico
      politicalPartyImage: candidate.urlAgrupacionImage,
      politicalPartyName: candidate.nombreAgrupacionPolitica,
      // Candidato
      candidateImage: candidate.urlCandidateImage,
      candidateName: candidate.nombreCandidato,
      // Votos
      votesNumber: candidate.totalVotosValidos,
      // Votos Emitidos
      votesEmittedPercentage: candidate.porcentajeVotosEmitidos,
      // Votos Validos
      votesValidPercentage: candidate.porcentajeVotosValidos
    };
    this.modalDetailVotesService.setData(data);

    if (this.esPantallaChica) {
      this.dialog.open(ModalDetailVotesComponent, {
        width: '400px',
        maxWidth: '80vw',
        panelClass: 'popup-votos-detalle'
      });
    }
  }

  detailTypeVotes(vote: GeographicalLocationNameItem, title: string): void {
    const data: Partial<ModalDetailVotes> = {
      // Partido Politico
      politicalPartyShow: false,
      // Candidato
      candidateImageShow: false,
      candidateName: title,
      // Votos
      votesNumber: vote.totalVotosValidos,
      // Votos Emitidos
      votesEmittedPercentage: vote.porcentajeVotosEmitidos,
      // Votos Validos
      votesValidPercentage: vote.porcentajeVotosValidos
    };
    this.modalDetailVotesService.setData(data);

    if (this.esPantallaChica) {
      this.dialog.open(ModalDetailVotesComponent, {
        width: '400px',
        maxWidth: '80vw',
        panelClass: 'popup-votos-detalle'
      });
    }
  }

  private showLoading() {
    // Loading functionality removed
    setTimeout(() => {
      // Loading functionality removed
    }, TIME_TO_LOADING);
  }

  private performSearch(searchTerm: string): void {
    const enteredName = (searchTerm ?? '').toLowerCase();

    this.geographicalLocationNameItems = mapWithPoliticImage(
      this.geographicalLocationNameItemsComplete.filter(x =>
        (x.nombreAgrupacionPolitica ?? '').toLowerCase().includes(enteredName) ||
        (x.nombreCandidato ?? '').toLowerCase().includes(enteredName)
      )
    );

    this.showSpecialCount = !enteredName;
  }

  submitFormBusCandidato(): void {
    // Este método ahora se maneja automáticamente por la búsqueda incremental
    // Se mantiene por compatibilidad hacia atrás pero ya no es necesario
    const searchTerm = this.myFormBusCandidato.get('nomCandidato').value;
    this.performSearch(searchTerm);
  }

  resetListaCandidatos(): void {
    this.geographicalLocationNameItems = mapWithPoliticImage(
      this.geographicalLocationNameItemsComplete
    );

    this.myFormBusCandidato.get('nomCandidato')?.setValue('');
    this.showSpecialCount = true;
  }

  private loadInitialParticipantsByGeographicalLocationName() {
    const currentParams = {
      idEleccion: this.electionID,
      tipoFiltro: UBIGEO_LEVELS.ELECTION
    } as GeographicalLocationNameParams;
    this.loadParticipantsByGeographicalLocationName(currentParams);
  }

  private loadInitialParticipantsByGeographicalLocationNameByRegion() {
    const currentParams = {
      idEleccion: this.electionID,
      tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue)
    } as GeographicalLocationNameParams;
    this.loadParticipantsByGeographicalLocationName(currentParams);
  }

  private updateMapFromFilter() {
    commonUpdateMapFromFilter(this.filters, this.mainHotMapComponent);
  }

  regionChanged($event: RegionValue) {
    this.filters = {} as FilterByLocationParams;
    this.regionValue = $event;
    if ($event == REGION_PERU) {
      this.loadMesasDetailByUbigeo();
      this.mainHotMapComponent.loadInitialUbigeoPeru();
      this.loadInitialParticipantsByGeographicalLocationNameByRegion();
    } else if ($event == REGION_EXTRAJERO) {
      this.loadMesasDetailByUbigeo();
      this.mainHotMapComponent.loadInitialUbigeoInternational();
      this.loadInitialParticipantsByGeographicalLocationNameByRegion();
    } else {
      this.loadMesasDetailByTheWorld();
      this.mainHotMapComponent.loadInitialUbigeoWorld();
      this.loadInitialParticipantsByGeographicalLocationName();
    }

    this.regionChangedEvent.emit($event);
  }

  scrollToTopSection(): void {
    //this.scrollToTopEvent.emit();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  scrollToBottomSection(): void {
    const element = this.elementRef.nativeElement.querySelector(`#bottom-section`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  private getFilterType(): string {
    const filterType = getGenericFilterType('peru_intern_and_all_join', this.regionValue, {
      departmentUbigeoId: this.filters.departmentUbigeoId,
      provinceUbigeoId: this.filters.provinceUbigeoId,
      districtUbigeoId: this.filters.districtUbigeoId
    });
    return filterType;
  }

  private getGeographicalScopeNew(): number | null {
    return getGenericGeographicalScope('peru_intern_and_all_join', this.regionValue);
  }

  private updateFiltersExternalEvent($event: FilterByLocationParams) {
    const newParams = getOptimizedObject({
      tipoFiltro: this.getFilterType(),
      idAmbitoGeografico: this.getGeographicalScopeNew(),
      ubigeoNivel1: $event.departmentUbigeoId,
      ubigeoNivel2: $event.provinceUbigeoId,
      ubigeoNivel3: $event.districtUbigeoId
    } as GenericFilterParams);
    this.updateFiltersFromNewEvent.emit(newParams);

    // IMPORTANT: THIS CODE SHOULD BE ADDED IN ALL VIEWS TO INTEGRATE BREADCRUMB
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal(newParams);
  }

  /**
   * This method is called from map
   */
  ubigeoParamsChangedFromMap($event: FilterByLocationParams) {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId, regionString } = $event;

    if (regionString) {
      this.regionValue = regionString;
    }
    if (regionString == 'EXTRANJERO') {
      this.mainFiltroUbigeoComponent.setUbigeoParamsExtrangero($event);
    } else {
      this.mainFiltroUbigeoComponent.setUbigeoParams($event);
    }

    if (districtUbigeoId) {
      this.filters = {
        ...this.filters,
        districtUbigeoId: districtUbigeoId
      } as FilterByLocationParams;

      const currentParams = {
        idEleccion: this.electionID,
        idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
        tipoFiltro: UBIGEO_LEVELS.LEVEL_03,
        ubigeoNivel1: this.filters.departmentUbigeoId,
        ubigeoNivel2: this.filters.provinceUbigeoId,
        ubigeoNivel3: this.filters.districtUbigeoId
      } as CommonPresidentialParams;
      this.loadParticipantsByGeographicalLocationName(currentParams as GeographicalLocationNameParams);
      this.loadMesasDetailByUbigeo();

      this.updateFiltersExternalEvent($event);
      return;
    }

    if (provinceUbigeoId) {
      this.filters = {
        ...this.filters,
        provinceUbigeoId: provinceUbigeoId
      } as FilterByLocationParams;

      const currentParams = {
        idEleccion: this.electionID,
        idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
        tipoFiltro: UBIGEO_LEVELS.LEVEL_02,
        ubigeoNivel1: this.filters.departmentUbigeoId,
        ubigeoNivel2: this.filters.provinceUbigeoId,
        ubigeoNivel3: ''
      } as CommonPresidentialParams;
      this.loadParticipantsByGeographicalLocationName(currentParams as GeographicalLocationNameParams);
      this.loadMesasDetailByUbigeo();

      this.updateFiltersExternalEvent($event);
      return;
    }

    if (departmentUbigeoId) {
      this.filters = {
        departmentUbigeoId: departmentUbigeoId
      } as FilterByLocationParams;

      const currentParams = {
        idEleccion: this.electionID,
        idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
        tipoFiltro: UBIGEO_LEVELS.LEVEL_01,
        ubigeoNivel1: this.filters.departmentUbigeoId,
        ubigeoNivel2: '',
        ubigeoNivel3: ''
      } as CommonPresidentialParams;
      this.loadParticipantsByGeographicalLocationName(currentParams as GeographicalLocationNameParams);
      this.loadMesasDetailByUbigeo();

      this.updateFiltersExternalEvent($event);
    }
  }

  /**
   * This method is called only from map
   */
  changeRegionFromMap($event: RegionValue) {
    this.regionChanged($event);

    if (this.mainFiltroUbigeoComponent) {
      this.mainFiltroUbigeoComponent.ubigeoForm.controls.region.setValue($event, { emitEvent: false });
      this.mainFiltroUbigeoComponent.regionChanged();
    }

    // IMPORTANT: THIS CODE SHOULD BE ADDED IN ALL VIEWS TO INTEGRATE BREADCRUMB
    this.mainFiltroUbigeoComponent.updateBreadcrumbStringFromExternal({
      idAmbitoGeografico: this.getGeographicalScopeNew()
    });
  }

  applyFiltersEvent(params: GenericFilterParams) {
    const customParamsMesasDetail = {
      ...params,
      ambitoGeografico: params.idAmbitoGeografico
    };
    delete customParamsMesasDetail.idAmbitoGeografico;
    this.loadMesasDetail(customParamsMesasDetail);

    const customParamsGeographical = { ...params, idEleccion: this.electionID };
    this.loadParticipantsByGeographicalLocationName(customParamsGeographical as GeographicalLocationNameParams);
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
  }

  // MARK: REQUEST TO BACKEND

  private loadMesasDetailByTheWorld() {
    this.mesasDeVotacionService
      .getMesasDetail$({
        tipoFiltro: UBIGEO_LEVELS.ELECTION
      })
      .pipe(
        takeUntil(this.destroy$),
        take(1)
      )
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
        ubigeoNivel3: this.filters.districtUbigeoId
      })
      .pipe(
        takeUntil(this.destroy$),
        take(1)
      )
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

  // GENERIC REQUEST METHODS

  private loadMesasDetail(params: MesasDetailParams) {
    this.mesasDeVotacionService
      .getMesasDetail$(params)
      .pipe(
        takeUntil(this.destroy$),
        take(1)
      )
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

  private loadParticipantsByGeographicalLocationName(params: GeographicalLocationNameParams): void {
    this.resetVotes();
    this.dataForDistrict = [];

    this.presidencialesService
      .getParticipantsByGeographicalLocationName$(params)
      .pipe(
        takeUntil(this.destroy$),
        take(1)
      )
      .subscribe({
        next: (response) => this.handleResponse(response),
        error: () => console.log('getParticipantsByGeographicalLocation error')
      });
  }

  private resetVotes(): void {
    this.emptyVotes = {} as GeographicalLocationNameItem;
    this.nullVotes = {} as GeographicalLocationNameItem;
    this.totalVotes = {} as GeographicalLocationNameItem;
  }

  getNumberOfPoliticalOrganizations(geographicalLocationNameItems: GeographicalLocationNameItem[]) {
    return geographicalLocationNameItems.filter(org =>
      org.codigoAgrupacionPolitica !== "80" &&
      org.codigoAgrupacionPolitica !== "81" );
  }  

  private handleResponse(
    response: FrontendResponse<GeographicalLocationNameInfo>
  ): void {
    this.geographicalLocationNameFirstRequestIsLoaded = true;

    if (!response.success) return;

    const mappedItems = mapWithPoliticImage(
      response.data.list.sort(this.customOrderBy)
    );

    this.geographicalLocationNameItems = mappedItems;
    this.geographicalLocationNameItemsComplete = mappedItems;
    this.dataForDistrict = makeFormattedInformationForBars(this.getNumberOfPoliticalOrganizations(response.data.listForScales));
    this.scalesForDistrict = makeScaleValues(response.data.listForScales, 10);
    this.emptyVotes = response.data.emptyVotes;
    this.nullVotes = response.data.nullVotes;
    this.totalVotes = response.data.totals;

    this.maxValueForScaleName = makeScaleValuesFromGenericArray(
      response.data.listForScales.map(item => item.totalVotosValidos),
      5
    ).reverse();
  }

  private customOrderBy(a: GeographicalLocationNameItem, b: GeographicalLocationNameItem): number {
    return b.totalVotosValidos - a.totalVotosValidos;
  }

  calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }

    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    let agregado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }

  getPosicionRegla(valor: number, index: number): string {
    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    let retirado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + retirado;
    let valorRetorno = (valor * 100) / votoCalculado;

    if (this.maxValueForScaleName.length - 1 == index) {
      return '100%';
    }

    return valorRetorno + '%';
  }

  getReglaValorIteracion(valor: number, index: number): number {
    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    if (this.maxValueForScaleName.length - 1 == index) {
      return valor + (valorMaximo == 0 ? 0 : 1);
    }

    return valor;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
