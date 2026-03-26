import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { Subscription, take } from "rxjs";
import { HTMLStatus, UBIGEO_LEVELS } from "../../helpers/constantes";
import { getGeograpScopeByRegion } from "../../helpers/election-type.config";
import { getGenericGeographicalScope, getGenericFilterType } from "../../helpers/filters-helper.common";
import { makeScaleValuesFromGenericArray } from "../../helpers/handler-chart-data.common";
import { getFilterTypeForBackend } from "../../helpers/ubigeo-level.common";
import {
  RegionValue,
  FilterByLocationParams,
  GenericFilterParams,
  getOptimizedObject,
} from "../../interfaces/filtro-settings";
import {
  ResultOfParticipantsItem,
  PoliticOrganizationsForSelectItem,
  ResultOfParticipantsParams,
  DatosOP,
} from "../../interfaces/presidenciales.interfaces";
import { ParlamentoService } from "../../services/elecciones-generales/parlamento.service";
import { FrontendResponse } from "../../interfaces/response.common";

interface IFormTipoDeBusqueda {
  organization: FormControl;
  candidateName: FormControl;
}

@Component({
  selector: "app-tab-realizar-busqueda",
  templateUrl: "./tab-realizar-busqueda.component.html",
  styleUrls: ["./tab-realizar-busqueda.component.scss"],
  host: { "pr-module": "parlamento" },
  standalone: false,
})
export class TabRealizarBusquedaComponent implements OnInit, OnDestroy {
  
  public resultOfParticipants: ResultOfParticipantsItem[] = [];
  public compactListPorCandidato: ResultOfParticipantsItem[] = [];
  public resultOfParticipantsComplete: ResultOfParticipantsItem[] = [];
  public maxValueForScaleName: number[] = [];
  public showInitialMessage = true;
  datosOP?: DatosOP;

  public formTipoDeBusqueda = this.fb.group<IFormTipoDeBusqueda>({
    organization: this.fb.control<number | null>(null, Validators.required),
    candidateName: this.fb.control<string>("", Validators.required),
  });

  @Input() electionId: number;
  @Input() parlamentoService: ParlamentoService;
  @Input() organizationsForSelect: PoliticOrganizationsForSelectItem[] = [];
  @Input() showNombreOP?: boolean = true;

  @Output() regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();

  private selectedFilters: FilterByLocationParams = {} as FilterByLocationParams;
  private regionValue: RegionValue = "TODOS";

  public allContentStatus: HTMLStatus = "";
  public dataFromBackendStatue: HTMLStatus = "";
  public showListOfResult: HTMLStatus = "";
  public thereIsMatchWithName: HTMLStatus = "";
  public enableButtonBuscar = false;
  public enableButtonLimpiar = false;

  private subscriptions: Subscription[] = [];

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadPoliticalOrganizationForSelect();

    const sub1 = this.candidateNameCtrl.valueChanges.subscribe((name) => {
      this.changeCandidateName();
    });
    this.subscriptions.push(sub1);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
    this.subscriptions = [];
  }

  private get organizationCtrl(): FormControl<number | null> {
    return this.formTipoDeBusqueda.controls.organization as FormControl<number | null>;
  }

  private get candidateNameCtrl(): FormControl<string> {
    return this.formTipoDeBusqueda.controls.candidateName as FormControl<string>;
  }

  public filterDistrictElectionChart(params: FilterByLocationParams) {
    this.filterByLocationParamsEvent.emit(params);
    this.selectedFilters = params;

    this.loadListOfResultForUbigeo();
  }

  public cleanInformation($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilters = {} as FilterByLocationParams;
  }

  public regionChanged($event: RegionValue) {
    this.regionValue = $event;
    this.selectedFilters = {} as FilterByLocationParams;

    if (this.regionValue == "PERÚ") {
      this.loadListOfResultForRegion();
    } else if (this.regionValue == "EXTRANJERO") {
      this.loadListOfResultForRegion();
    } else {
      this.loadListOfResultForTheWorld();
    }
    this.regionChangedEvent.emit($event);
  }

  public organizationChanged(even?: any) {
    if (even) {
      this.enableButtonBuscar = true;
      this.showInitialMessage = false;
      this.dataFromBackendStatue = '';
      this.regionValue = 'TODOS';
      this.showResultFromSelectAndName();
    } else {
      this.showInitialMessage = true;
      this.dataFromBackendStatue = '';
    }
  }

  public showResultFromSelectAndName() {
    this.enableButtonBuscar = false;
    const organization = this.organizationCtrl.value;
    this.formTipoDeBusqueda.controls.candidateName.setValue("");
    if (!isNaN(organization)) {
      this.loadInformationForPoliticalOrganization();
    } else {      
      this.showInitialMessage = isNaN(organization);
    }
  }

  public changeCandidateName() {
    this.thereIsMatchWithName = "";

    const enteredName = this.candidateNameCtrl.value;
    if (enteredName) {
      this.enableButtonLimpiar = true;
      this.resultOfParticipants = this.resultOfParticipantsComplete.filter((x) =>
        x.nombreCandidato.toLowerCase().includes(enteredName.toLowerCase())
      );
      this.thereIsMatchWithName = this.resultOfParticipants.length > 0 ? "show" : "hide";
    } else {
      this.thereIsMatchWithName = "show";
      this.resultOfParticipants = this.resultOfParticipantsComplete;
    }
  }

  public cleanCandidateFilter() {
    this.formTipoDeBusqueda.controls.candidateName.setValue("");
    if (this.regionValue == "TODOS") {
      this.loadListOfResultForTheWorld();
    } else {
      this.loadListOfResultForUbigeo();
    }
  }

  public applyFiltersEvent(params: GenericFilterParams) {
    this.updateFiltersFromNewEvent.emit(params);

    if (params.idAmbitoGeografico) {
      this.regionValue = params.idAmbitoGeografico == 1 ? "PERÚ" : "EXTRANJERO";
    } else {
      this.regionValue = "TODOS";
    }
    this.selectedFilters = {
      region: params.idAmbitoGeografico,
      departmentUbigeoId: params.ubigeoNivel1,
      provinceUbigeoId: params.ubigeoNivel2,
      districtUbigeoId: params.ubigeoNivel3,
    };
    this.selectedFilters = getOptimizedObject<FilterByLocationParams>(this.selectedFilters);

    this.loadListOfResultComplete();
  }

  private getGeographicalScopeNew(): number | null {
    return getGenericGeographicalScope("peru_intern_and_all_join", this.regionValue);
  }

  private getFilterType(): string {
    const filterType = getGenericFilterType("peru_intern_and_all_join", this.regionValue, {
      departmentUbigeoId: this.selectedFilters.departmentUbigeoId,
      provinceUbigeoId: this.selectedFilters.provinceUbigeoId,
      districtUbigeoId: this.selectedFilters.districtUbigeoId,
    });
    return filterType;
  }

  private loadListOfResultComplete() {
    const params = {
      idEleccion: this.electionId,
      idAmbitoGeografico: this.getGeographicalScopeNew(),
      tipoFiltro: this.getFilterType(),
      ubigeoNivel1: this.selectedFilters.departmentUbigeoId,
      ubigeoNivel2: this.selectedFilters.provinceUbigeoId,
      ubigeoNivel3: this.selectedFilters.districtUbigeoId,
      idAgrupacionPolitica: this.organizationCtrl.value,
    } as ResultOfParticipantsParams;
    if (this.formTipoDeBusqueda.controls.candidateName.value) {
      params.nombreCandidato = this.formTipoDeBusqueda.controls.candidateName.value;
    }
    this.loadListOfResult(params);
  }

  // REQUEST A BACKEND


  private loadListOfResultForTheWorld() {
    const params = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ELECTION,
      idAgrupacionPolitica: this.organizationCtrl.value,
    } as ResultOfParticipantsParams;
    if (this.formTipoDeBusqueda.controls.candidateName.value) {
      params.nombreCandidato = this.formTipoDeBusqueda.controls.candidateName.value;
    }
    this.loadListOfResult(params);
  }

  private loadListOfResultForRegion() {
    const params = {
      idEleccion: this.electionId,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      tipoFiltro: getFilterTypeForBackend(),
      idAgrupacionPolitica: this.organizationCtrl.value,
    } as ResultOfParticipantsParams;
    if (this.formTipoDeBusqueda.controls.candidateName.value) {
      params.nombreCandidato = this.formTipoDeBusqueda.controls.candidateName.value;
    }
    this.loadListOfResult(params);
  }

  private loadListOfResultForUbigeo() {
    const params = {
      idEleccion: this.electionId,
      idAmbitoGeografico: getGeograpScopeByRegion(this.regionValue),
      tipoFiltro: getFilterTypeForBackend(this.selectedFilters),
      ubigeoNivel1: this.selectedFilters.departmentUbigeoId,
      ubigeoNivel2: this.selectedFilters.provinceUbigeoId,
      ubigeoNivel3: this.selectedFilters.districtUbigeoId,
      idAgrupacionPolitica: this.organizationCtrl.value,
    } as ResultOfParticipantsParams;
    if (this.formTipoDeBusqueda.controls.candidateName.value) {
      params.nombreCandidato = this.formTipoDeBusqueda.controls.candidateName.value;
    }
    this.loadListOfResult(params);
  }

  private procesarRespuesta(response: FrontendResponse<ResultOfParticipantsItem[]>, 
      esParaOrganizacion: boolean = false) {
    if (response.success) {
      this.resultOfParticipants = response.data;
      this.resultOfParticipantsComplete = response.data;
      
      const responseOP = {
        totalVotosPorOP: response.totalVotosPorOP,
        porcentajeVotoValido: response.porcentajeVotoValido,  
        porcentajeVotoEmitido: response.porcentajeVotoEmitido
      };
      this.datosOP = responseOP;

      if (esParaOrganizacion) {
        this.compactListPorCandidato = response.data.slice(0,1);
        this.dataFromBackendStatue = "show";
        this.showListOfResult = this.resultOfParticipantsComplete.length == 0 ? "hide" : "show";
      } else {
        this.showListOfResult = "show";
      }

      const valuesArr = response.data.map((ele) => ele.totalVotosValidos);
      this.maxValueForScaleName = makeScaleValuesFromGenericArray(valuesArr, 5);
    } else {
      if (esParaOrganizacion) {
        this.dataFromBackendStatue = "hide";
      } else {
        this.showListOfResult = "hide";
      }
      console.log("error loadListOfResult");
    }
  }

  private loadListOfResult(params: ResultOfParticipantsParams) {
    this.showListOfResult = "";
    this.datosOP = null;
    this.parlamentoService
      .getResultOfParticipants$(params)
      .pipe(take(1))
      .subscribe((response) => {
        this.procesarRespuesta(response, false);
      });
  }

  private loadInformationForPoliticalOrganization() {
    const params = {
      idEleccion: this.electionId,
      tipoFiltro: UBIGEO_LEVELS.ELECTION,
      idAgrupacionPolitica: this.organizationCtrl.value,
    } as ResultOfParticipantsParams;
    
    this.showListOfResult = "";
    this.dataFromBackendStatue = "";
    this.datosOP = null;
    
    this.parlamentoService
      .getResultOfParticipants$(params)
      .pipe(take(1))
      .subscribe((response) => {
        this.procesarRespuesta(response, true);
      });
  }

  private loadPoliticalOrganizationForSelect() {
    this.loadOrganizationsAfterValidate();
  }

  private loadOrganizationsAfterValidate() {
    // Loading functionality removed
    this.allContentStatus = "";
      this.parlamentoService
      .getPoliticOrganizationsForSelect$()
        .pipe(take(1))
        .subscribe((response) => {
          // Loading functionality removed
           if (response.success) {
            this.allContentStatus = "show";
            this.organizationsForSelect = response.data;
          } else {
            this.allContentStatus = "hide";
            console.log("error loadPoliticalOrganizationForSelect");
          }
        });
  }
}
