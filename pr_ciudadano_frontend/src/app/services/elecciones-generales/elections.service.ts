import { Injectable } from "@angular/core";
import { catchError, forkJoin, map, Observable, take } from "rxjs";
import { environment } from "../../../environments/environment";
import { encryptStorageEleccion } from "../../settings/encrypt-storage.settings";

import { RequestsService } from "../common/requests.service";
import { FrontendResponse } from "../../interfaces/response.common";
import {
  Candidate,
  CandidatesResponse,
  ElectoralTable,
  ElectoralTablesResponse,
  PoliticalOrganization,
  PoliticalOrganizationsResponse,
  AllInformationAboutElection,
  SummaryByCandidatesResponse,
  SummaryByCandidateParams,
  GeneralSummaryTotalsParams,
  GeneralSummaryTotalsResponse,
  GeneralSummaryTotals,
  IElectionType,
  IElectionTypesResponse,
  SummaryByCandidate2,
  IFiltroUbigeo,
  SumaryRevocatoria,
  SummaryRevocatoriaByCandidatesResponse,
  Region,
} from "../../interfaces/elections.interfaces";
import { Resumen } from "../../interfaces/resumen-bean";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { DISTRITO_ELECTORAL_LIMA_ID } from "../../helpers/constantes";
import {
  REGION_PERU,
  REGION_NEW_VALUES,
  REGION_VALUES,
  FilterByLocationParams,
} from "../../interfaces/filtro-settings";
import { BaseUbigeoService } from "../common/base-ubigeo.service";
import { isRevocatoria } from "../../helpers/storage-helpers/encrypt-storage.helper";

const specificURLs = {
  summaryTotals: environment.apiUrlLocal + "resumen-general/totales",
  summaryCandidates: environment.apiUrlLocal + "resumen-general/participantes",
  electionList: (id: number): string => {
    return environment.apiUrlLocal + `proceso/${id}/elecciones`;
  },
};

@Injectable({
  providedIn: "root",
})
export class ElectionsService {
  private electionId?: number;
  constructor(private readonly request: RequestsService, private readonly baseUbigeoService: BaseUbigeoService) {}

  public getGeneralSummaryTotals$(
    params: GeneralSummaryTotalsParams
  ): Observable<FrontendResponse<GeneralSummaryTotals>> {
    return this.request.post<GeneralSummaryTotalsResponse>(specificURLs.summaryTotals, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<GeneralSummaryTotals>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getFormattedSummary(object: GeneralSummaryTotals): Resumen {
    return {
      actasContabilizadas: object.actasContabilizadas,
      actasEnviadasJee: object.actasEnviadasJee,
      actasPendientes: object.actasPendientesJee,
      fechaActualizacion: object.fechaActualizacion,
      idUbigeoDepartamento: object.idUbigeoDepartamento,
      idUbigeoProvincia: object.idUbigeoProvincia,
      idUbigeoDistrito: object.idUbigeoDistrito,
      participacionCiudadana: object.participacionCiudadana,
      totalActas: object.totalActas,
      actasPendientesJee: object.actasPendientesJee,
    } as Resumen;
  }

  public getPoliticalOrganizations$(): Observable<FrontendResponse<PoliticalOrganization[]>> {
    return this.request.get<PoliticalOrganizationsResponse>("assets/data/organizacionPolitica.json").pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<PoliticalOrganization[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getCandidates$(): Observable<FrontendResponse<Candidate[]>> {
    return this.request.get<CandidatesResponse>("assets/data/candidatos.json").pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<Candidate[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getElectoralTables$(): Observable<FrontendResponse<ElectoralTable[]>> {
    return this.request.get<ElectoralTablesResponse>("assets/data/mesas.json").pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<ElectoralTable[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getAllInformationAboutElections$(): Observable<FrontendResponse<AllInformationAboutElection>> {
    return forkJoin([this.getPoliticalOrganizations$(), this.getCandidates$(), this.getElectoralTables$()]).pipe(
      map(([politicalOrganizationsResponse, candidatesResponse, electoralTablesResponse]) => {
        return {
          success: true,
          data: {
            organizations: politicalOrganizationsResponse.data,
            candidates: candidatesResponse.data,
            electoralTables: electoralTablesResponse.data,
          },
        } as FrontendResponse<AllInformationAboutElection>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getSummaryByCandidates$(
    params: SummaryByCandidateParams
  ): Observable<FrontendResponse<SummaryByCandidate2[]>> {
    return this.request.post<SummaryByCandidatesResponse>(specificURLs.summaryCandidates, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<SummaryByCandidate2[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getSummaryRevocatoriaByCandidates$(
    params: SummaryByCandidateParams
  ): Observable<FrontendResponse<SumaryRevocatoria[]>> {
    return this.request.post<SummaryRevocatoriaByCandidatesResponse>(specificURLs.summaryCandidates, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<SumaryRevocatoria[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getElectionTypes$(): Observable<FrontendResponse<IElectionType[]>> {
    this.electionId = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO")).id ?? 10;
    return this.request.get<IElectionTypesResponse>(specificURLs.electionList(this.electionId)).pipe(
      map((response) => {
        let data = response.body.data.map((electionType) => {
          return {
            ...electionType,
            idEleccion: electionType.idEleccion ? Number(electionType.idEleccion) : 0,
          } as IElectionType;
        });
        return {
          success: true,
          data: data,
        } as FrontendResponse<IElectionType[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getElectionTypesForGeneralSummary$(): Observable<FrontendResponse<IElectionType[]>> {
    return this.getElectionTypes$().pipe(
      map((response) => {
        return {
          success: true,
          data: response.data.filter((e) => e.idEleccion !== 0),
        } as FrontendResponse<IElectionType[]>;
      })
    );
  }

  // GenericFilterUbigeoComponent & MainFiltroUbigeoComponent
  public settingsFilterFunctionality(scope: IFiltroUbigeo) {
    if (scope.filterFunctionality == "only_peru") {
      scope.listRegiones = [REGION_PERU];
      scope.updateInitialUbigeoFormValues && scope.updateInitialUbigeoFormValues();
    } else if (scope.filterFunctionality == "only_regiones") {

      this.loadElectoralDistricts(scope);

    } else if (scope.filterFunctionality == "peru_and_regiones") {
      this.loadElectoralDistricts(scope);
    } else if (scope.filterFunctionality == "peru_intern_and_all_join") {
      scope.listRegiones = REGION_NEW_VALUES;
      scope.updateInitialUbigeoForAllTheWorld && scope.updateInitialUbigeoForAllTheWorld();
    } else if (scope.filterFunctionality == "peru_and_international") {
      scope.listRegiones = REGION_VALUES;
      scope.setRegion && scope.setRegion(REGION_PERU);

      scope.updateInitialUbigeoFormValues && scope.updateInitialUbigeoFormValues();
    }
  }

  public loadLocals(scope: IFiltroUbigeo, districtUbigeo?: string) {
    let districtId = districtUbigeo ?? scope.ubigeoForm.controls.district.value;
    this.baseUbigeoService
      .getLocals$({ idUbigeo: Number(districtId) })
      .pipe(take(1))
      .subscribe((response: any) => {
        if (response.success) {
          scope.listLocales = response.data;
        } else {
          console.error("loadLocals error");
        }
      });
  }

  public loadDistricts(scope: IFiltroUbigeo, provinceUbigeo?: string) {
    let provinceId = provinceUbigeo ?? scope.ubigeoForm.controls.province.value;
    this.baseUbigeoService.loadDistricts(scope, provinceId);
  }

  public loadProvinces(scope: IFiltroUbigeo, departmentUbigeo?: string) {
    let departmentId = departmentUbigeo ?? scope.ubigeoForm.controls.department.value;
    this.baseUbigeoService.loadProvinces(scope, departmentId);
  }

  public loadDepartments(scope: IFiltroUbigeo) {
    this.baseUbigeoService.loadDepartments(scope);
  }

  public loadStates(scope: IFiltroUbigeo, ubigeo?: string) {
    const country = ubigeo ?? scope.ubigeoForm.controls.country.value;
    this.baseUbigeoService.loadStates(scope, country);
  }

  public loadCountries(scope: IFiltroUbigeo, ubigeo?: string) {
    const continent = ubigeo ?? scope.ubigeoForm.controls.continent.value;
    this.baseUbigeoService.loadCountries(scope, continent);
  }

  public loadContinentals(scope: IFiltroUbigeo) {
    this.baseUbigeoService.loadContinents(scope);
  }

  // FOR NOW, THIS METHOD IS ONLY FOR REVOCATORIA
  public loadRegiones(scope: IFiltroUbigeo) {
    this.baseUbigeoService.loadRegiones(scope, { idEleccion: 7, idAmbitoGeografico: 1 });
  }

  /**
   * este es un caso particular para elecciones diputados y distrito electoral multiple
   */
  public loadElectoralDistricts(scope: IFiltroUbigeo) {
    if (isRevocatoria()) {
      this.loadElectoralDistrictsForRevocatoria(scope);
    } else {
      this.loadElectoralDistrictsForEleccionesGenerales(scope);
    }
  }

  private loadElectoralDistrictsForRevocatoria(scope: IFiltroUbigeo) {
    this.baseUbigeoService
      .getDepProvDist$({ idEleccion: 7, idAmbitoGeografico: 1 })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.regiones = response.data.map((elem) => {
            return {
              codigo: Number(elem.ubigeo),
              nombre: elem.nombre,
              ubigeo: elem.ubigeo,
            } as Region;
          });
          if (scope.ubigeoInitialValue != 0) {
            scope.electoralRevocatoriaForm?.controls.region.setValue(String(scope.ubigeoInitialValue).padStart(6, "0"), {
              emitEvent: false,
            });
          } else {
            const regionId = scope.electoralRevocatoriaForm.controls.region.value;
            let value = "";
            if (!scope.showCleanButton && !regionId) {
              const initialRegion = scope.regiones[0];
              value = initialRegion.ubigeo;
            }
            scope.electoralRevocatoriaForm.controls.region.setValue(value, { emitEvent: false });
          }
          if(scope.applyElectoralDistrictFiltersForRevocatoria){
            scope.applyElectoralDistrictFiltersForRevocatoria(scope.electoralRevocatoriaForm.controls.region.value);
          }
        } else {
          console.error("loadElectoralDistricts getDepProvDist error");
        }
      });
  }

  public loadLocalsRevocatoria(scope: IFiltroUbigeo, districtUbigeo?: string) {
    let regionId = districtUbigeo ?? scope.electoralRevocatoriaForm.controls.region.value;
    this.baseUbigeoService
      .getLocals$({ idUbigeo: Number(regionId) })
      .pipe(take(1))
      .subscribe((response: any) => {
        if (response.success) {
          scope.listLocales = response.data;
        } else {
          console.error("loadLocals error");
        }
      });
  }

  private loadElectoralDistrictsForEleccionesGenerales(scope: IFiltroUbigeo) {
    this.baseUbigeoService
      .getRegiones$()
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.regiones = response.data;
          const initialRegion = response.data.find((ele) => ele.codigo == DISTRITO_ELECTORAL_LIMA_ID);
          
          if (scope.filterFunctionality == "only_regiones"){
            const regionValue = scope.electoralDistrictForm.controls.region.value;
            let value = regionValue || initialRegion.codigo;
            scope.electoralDistrictForm.controls.region.setValue(value);
          } else {
            scope.electoralDistrictForm.controls.region.setValue(initialRegion.codigo);
            scope.applyElectoralDistrictFilters();
          }
        } else {
          console.error("loadElectoralDistricts getRegiones error");
        }
      });
  }

  public applyElectoralDistrictFilters(scope: IFiltroUbigeo, func: (filters: FilterByLocationParams) => void) {
    // scope.filterButton2IsDisabled = true;
    const filters = {
      electoralDistrictId: Number(scope.electoralDistrictForm.controls.region.value),
      revocatoriaDistrictId: Number(scope.electoralRevocatoriaForm.controls.region.value),
    } as FilterByLocationParams;
    scope.appliedUbigeoFormValues = filters;
    func?.(filters);
  }
}
