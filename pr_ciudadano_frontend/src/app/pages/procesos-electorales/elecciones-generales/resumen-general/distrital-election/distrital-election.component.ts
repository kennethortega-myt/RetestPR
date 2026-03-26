import { Component, EventEmitter, inject, Input, OnInit, Output } from "@angular/core";
import { Router } from "@angular/router";
import { take } from "rxjs";
import { IChartBarInfo } from "../../../../../interfaces/chart-bar-info.interface";
import {
  MAIN_ELECTION_IDS,
  UBIGEO_LEVELS,
  GEOGRAPHIC_SCOPE_EXTRANJERA,
  GEOGRAPHIC_SCOPE,
  ID_INICIAL_UBIGEO,
} from "../../../../../helpers/constantes";
import { isSpecialElectionType, getGeograpScopeByRegion } from "../../../../../helpers/election-type.config";
import { makeFormattedInformationForBars, makeScaleValues } from "../../../../../helpers/handler-chart-data.common";
import { getFilterTypeForBackend } from "../../../../../helpers/ubigeo-level.common";
import { IElectionType, SummaryByCandidateParams } from "../../../../../interfaces/elections.interfaces";
import {
  FilterByLocationParams,
  RegionValue,
  FilterFunctionality,
  REGION_PERU,
  REGION_EXTRAJERO,
  GenericFilterParams,
  getOptimizedObject,
} from "../../../../../interfaces/filtro-settings";
import { Resumen } from "../../../../../interfaces/resumen-bean";
import { TotalsParams } from "../../../../../interfaces/resumen-general.interfaces";
import { ElectionsService } from "../../../../../services/elecciones-generales/elections.service";
import { ReportManagerService } from "../../../../../services/elecciones-generales/report-manager.service";
import { ResumenGeneralService } from "../../../../../services/elecciones-generales/resumen-general.service";
import { RESUMEN_GENERAL_TITLE } from "../../../../../helpers/resumen-general.helper";
import { getElectionByIconKeyAsync } from "../../../../../helpers/encrypt-storage-eleccion";

const DISTRITO_ELECTORAL_EXTRENGERA_ID = 27;

@Component({
  selector: "app-distrital-election",
  templateUrl: "./distrital-election.component.html",
  standalone: false,
})
export class DistritalElectionComponent implements OnInit {
  //Attributes

  public resumen = {} as Resumen;
  public dataForDistrict: IChartBarInfo[] = [];
  public scalesForDistrict: number[] = [];
  public initialUbigeo: FilterByLocationParams;
  public TITLE = RESUMEN_GENERAL_TITLE;
  public electionTitle: string = '';

  private readonly electionsService = inject(ElectionsService);
  private regionValue: RegionValue = "TODOS";

  private selectedFilterParams: FilterByLocationParams = {} as FilterByLocationParams;

  @Input() index: number;
  @Input() showNextButton: boolean;
  @Input() showPrevButton: boolean;
  @Input() electionType: IElectionType;
  @Input() isEven : boolean;

  @Output() nextChartAction = new EventEmitter<number>();
  @Output() prevChartAction = new EventEmitter<number>();

  // Constructor

  constructor(
    private readonly router: Router,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly reportManagerService: ReportManagerService
  ) {
    sessionStorage.setItem("favorito", "0");
  }

  ngOnInit(): void {
    if (!this.isSpecialElectionType()) {
      this.loadTotalsInfoByTheWorld();
      this.loadSummaryByTheWorld();
    } else {
      this.loadTotalsForElectoralDistrict();
      this.loadSummaryForElectoralDistrict();
    }
    
    // Cargar el título asíncronamente
    this.loadElectionTitleAsync();
  }

  private async loadElectionTitleAsync(): Promise<void> {
    if (!this.electionType?.icono) {
      this.electionTitle = '';
      return;
    }
    try {
      // Intentar primero con el objeto TITLE estático
      const staticTitle = this.TITLE[this.electionType.icono];
      if (staticTitle && staticTitle !== undefined) {
        this.electionTitle = staticTitle;
        return;
      }
      // Si no está disponible, usar la función asíncrona
      const election = await getElectionByIconKeyAsync(this.electionType.icono as any);
      
      if (election && election.descripcion) {
        this.electionTitle = election.descripcion;
      } else {
        this.electionTitle = this.electionType.icono.toUpperCase();
      }
    } catch (error) {
      this.electionTitle = this.electionType.icono.toUpperCase();
    }
  }

  // Public methods

  public get filteredDataForDistrict(): IChartBarInfo[] {
    return this.dataForDistrict.filter((_) => _);
  }

  public get filterFunctionality(): FilterFunctionality {
    return this.isSpecialElectionType() ? "only_regiones" : "peru_intern_and_all_join";
  }

  public filterDistrictElectionChart(params: FilterByLocationParams) {
    this.selectedFilterParams = params;

    if (params.electoralDistrictId) {
      this.loadSummaryForElectoralDistrict();
      this.loadTotalsForElectoralDistrict();
    } else {
      this.loadTotalsInfoByUbigeo();
      this.loadSummaryByUbigeo();
    }
  }

  public resetInfo($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilterParams = {} as FilterByLocationParams;

    // actualizar
    if ($event == "PERÚ" || $event == "EXTRANJERO") {
      this.loadTotalsInfoByRegion();
      this.loadSummaryByRegion();
    } else {
      this.loadTotalsInfoByTheWorld();
      this.loadSummaryByTheWorld();
    }
  }

  public scrollToNextElement(): void {
    this.nextChartAction.emit(this.index + 1);
  }

  public scrollToPrevElement(): void {
    // Selecciona todas las secciones dinámicas
    const sections = Array.from(document.querySelectorAll("section.seccion-pantalla"));

    // Verifica que existan secciones y que la index sea válida
    if (sections.length > 0 && this.index > 0) {
      // Caso específico: si estamos en la sección 2 y necesitamos ir a la primera sección
      if (this.index === 1) {
        window.scrollTo({
          top: 0, // Ir al inicio de la página
          behavior: "smooth", // Desplazamiento suave
        });
        return; // Detenemos aquí para que no se ejecute más lógica
      }

      // En otros casos, desplázate a la sección anterior
      const targetSection = sections[this.index - 1] as HTMLElement; // Sección anterior
      const elementPosition = targetSection.offsetTop; // Obtén la posición absoluta de la sección

      window.scrollTo({
        top: elementPosition, // Desplázate a la sección anterior
        behavior: "smooth", // Desplazamiento suave
      });
    } else {
      console.warn("No hay secciones previas para desplazarse.");
    }

    // Emite el evento original en todos los casos
    this.prevChartAction.emit(this.index - 1);
  }

  public verDetalle(): void {
    this.router.navigate([this.electionType.url]);
  }

  public regionChanged($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilterParams = {} as FilterByLocationParams;

    if ($event == REGION_PERU || $event == REGION_EXTRAJERO) {
      this.loadTotalsInfoByRegion();
      this.loadSummaryByRegion();
    } else {
      this.loadTotalsInfoByTheWorld();
      this.loadSummaryByTheWorld();
    }
  }

  public applyFiltersEvent(params: GenericFilterParams) {
    const params1 = getOptimizedObject<TotalsParams>({
      idEleccion: this.electionType.idEleccion,
      tipoFiltro: params.tipoFiltro,
      idAmbitoGeografico: params.idAmbitoGeografico,
      idDistritoElectoral: params.electoralDistrictId,
      idUbigeoDepartamento: params.ubigeoNivel1,
      idUbigeoProvincia: params.ubigeoNivel2,
      idUbigeoDistrito: params.ubigeoNivel3,
    } as TotalsParams);
    this.loadTotalsInfoForSummary(params1);

    const params2 = getOptimizedObject<SummaryByCandidateParams>({
      idEleccion: this.electionType.idEleccion,
      tipoFiltro: params.tipoFiltro,
      idAmbitoGeografico: params.idAmbitoGeografico,
      idDistritoElectoral: params.electoralDistrictId,
      idUbigeoDepartamento: params.ubigeoNivel1,
      idUbigeoProvincia: params.ubigeoNivel2,
      idUbigeoDistrito: params.ubigeoNivel3,
    } as SummaryByCandidateParams);
    this.loadSummary(params2);

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
      electoralDistrictId: params.electoralDistrictId,
    };
    this.selectedFilterParams = getOptimizedObject<FilterByLocationParams>(this.selectedFilterParams);
  }

  public highLightTheMostVoted(): boolean {
    const conditions = [
      this.electionType?.idEleccion != MAIN_ELECTION_IDS.diputados,
      this.electionType?.idEleccion != MAIN_ELECTION_IDS.parlamento_andino,
      this.electionType?.idEleccion != MAIN_ELECTION_IDS.senadores_27,
      this.electionType?.idEleccion != MAIN_ELECTION_IDS.senadores_33,
    ];
    return conditions.every((cond) => cond);
  }

  // private methods

  private isSpecialElectionType(): boolean {
    return isSpecialElectionType(this.electionType?.idEleccion);
  }

  // REQUEST TO BACKEND: SUMARY

  private loadSummaryByTheWorld() {
    const currentParams = {
      idEleccion: this.electionType?.idEleccion,
      tipoFiltro: UBIGEO_LEVELS.ELECTION,
    } as SummaryByCandidateParams;
    this.loadSummary(currentParams);
  }

  private loadSummaryByRegion() {
    const currentParams = {
      idEleccion: this.electionType?.idEleccion,
      tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
    } as SummaryByCandidateParams;
    this.loadSummary(currentParams);
  }

  private loadSummaryByUbigeo() {
    const currentParams = {
      idEleccion: this.electionType?.idEleccion,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      idUbigeoDepartamento: this.selectedFilterParams.departmentUbigeoId,
      idUbigeoDistrito: this.selectedFilterParams.districtUbigeoId,
      idUbigeoProvincia: this.selectedFilterParams.provinceUbigeoId,
      tipoFiltro: getFilterTypeForBackend(this.selectedFilterParams),
    } as SummaryByCandidateParams;
    this.loadSummary(currentParams);
  }

  private loadSummaryForElectoralDistrict() {
    const id = Number(this.selectedFilterParams.electoralDistrictId);
    const currentParams = {
      idAmbitoGeografico: id == DISTRITO_ELECTORAL_EXTRENGERA_ID ? GEOGRAPHIC_SCOPE_EXTRANJERA : GEOGRAPHIC_SCOPE,
      idEleccion: this.electionType?.idEleccion,
      tipoFiltro: UBIGEO_LEVELS.DISTRITO_ELECTORAL,
      idDistritoElectoral: Number(this.selectedFilterParams.electoralDistrictId) || ID_INICIAL_UBIGEO.ID_DISTRITO_LIMA,
    } as SummaryByCandidateParams;
    this.loadSummary(currentParams);
  }

  private loadSummary(params: SummaryByCandidateParams) {
    this.dataForDistrict = [];
    this.electionsService
      .getSummaryByCandidates$(params)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.dataForDistrict = makeFormattedInformationForBars(response.data);
          this.scalesForDistrict = makeScaleValues(response.data, 10);
        } else {
          console.error("loadSummaryByCandidatesForDistrict");
        }
      });
  }

  // REQUEST TO BACKEND: TOTAL SUMMARY

  private loadTotalsInfoByTheWorld() {
    const totalParams = {
      idEleccion: this.electionType.idEleccion,
      tipoFiltro: UBIGEO_LEVELS.ELECTION,
    } as TotalsParams;
    this.loadTotalsInfoForSummary(totalParams);
  }

  private loadTotalsInfoByRegion() {
    const totalParams = {
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      idEleccion: this.electionType.idEleccion,
      tipoFiltro: getFilterTypeForBackend(),
    } as TotalsParams;
    this.loadTotalsInfoForSummary(totalParams);
  }

  private loadTotalsInfoByUbigeo() {
    const totalParams = {
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      idEleccion: this.electionType.idEleccion,
      tipoFiltro: getFilterTypeForBackend(this.selectedFilterParams),
      idUbigeoDepartamento: this.selectedFilterParams.departmentUbigeoId,
      idUbigeoDistrito: this.selectedFilterParams.districtUbigeoId,
      idUbigeoProvincia: this.selectedFilterParams.provinceUbigeoId,
    } as TotalsParams;
    this.loadTotalsInfoForSummary(totalParams);
  }

  private loadTotalsForElectoralDistrict() {
    const id = Number(this.selectedFilterParams.electoralDistrictId);
    const totalParams = {
      idAmbitoGeografico: id == DISTRITO_ELECTORAL_EXTRENGERA_ID ? GEOGRAPHIC_SCOPE_EXTRANJERA : GEOGRAPHIC_SCOPE,
      idEleccion: this.electionType?.idEleccion,
      tipoFiltro: UBIGEO_LEVELS.DISTRITO_ELECTORAL,
      idDistritoElectoral: Number(this.selectedFilterParams.electoralDistrictId) || ID_INICIAL_UBIGEO.ID_DISTRITO_LIMA,
    } as TotalsParams;
    this.loadTotalsInfoForSummary(totalParams);
  }

  private loadTotalsInfoForSummary(params: TotalsParams) {
    this.resumenGeneralService
      .getTotals$(params)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.resumen = this.resumenGeneralService.getFormattedSummary(response.data);
        } else {
          console.error("loadPresidentialElectionInfo error");
        }
      });
  }

  public generateReportPdf() {
    this.reportManagerService.downloadReportePDFResumenGeneral({
      electionId: this.electionType.idEleccion,
      filters: this.selectedFilterParams,
      regionValue: this.regionValue,
      tipoReporte: 1,
    });
  }

  public noGenerateReportPDF() {
    this.reportManagerService.presentSnackbarForNoDownloadReport();
  }

  public generateReportCsv() {
    this.reportManagerService.downloadReportePDFResumenGeneral({
      electionId: this.electionType.idEleccion,
      filters: this.selectedFilterParams,
      regionValue: this.regionValue,
      tipoReporte: 2,
    });
  }

  public noGenerateReportCsv() {
    this.reportManagerService.presentSnackbarForNoDownloadReport();
  }
}
