import { Injectable } from "@angular/core";
import { Observable, map, catchError, take } from "rxjs";
import { RequestsService } from "../common/requests.service";
import { FrontendResponse } from "../../interfaces/response.common";
import { environment } from "../../../environments/environment";
import { Resumen } from "../../interfaces/resumen-bean";
import { GEOGRAPHIC_SCOPE, GEOGRAPHIC_SCOPE_EXTRANJERA } from "../../helpers/constantes";
import { getFilterTypeForBackend } from "../../helpers/ubigeo-level.common";
import {
  RegionValue,
  REGION_PERU,
  REGION_EXTRAJERO,
  FilterByLocationParams,
  GenericFilterParams,
} from "../../interfaces/filtro-settings";
import {
  TotalsParams,
  Totals,
  TotalsResponse,
  BarsChartParams,
  BarChartInfo,
  BarsChartInformationResponse,
  ILoadGeneralSummaryComponent,
} from "../../interfaces/resumen-general.interfaces";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { ResumenGeneralApiService } from "./resumen-general-api.service";
import { ResumenGeneral, MapaCalor, AgrupacionPolitica } from "../../interfaces/resumen-general-bean";

const specificURLs = {
  totals: environment.apiUrlLocal + "resumen-general/totales",
  barsChart: environment.apiUrlLocal + "resumen-general/participantes",
};

@Injectable({
  providedIn: "root",
})
export class ResumenGeneralService {
  constructor(private readonly request: RequestsService, private resumenGeneralApiService: ResumenGeneralApiService) {}

  public getTotals$(params: TotalsParams): Observable<FrontendResponse<Totals>> {
    return this.request.post<TotalsResponse>(specificURLs.totals, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body?.data,
        } as FrontendResponse<Totals>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getBarsChartInformation$(params: BarsChartParams): Observable<FrontendResponse<BarChartInfo[]>> {
    return this.request.post<BarsChartInformationResponse>(specificURLs.barsChart, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body?.data,
        } as FrontendResponse<BarChartInfo[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getFormattedSummary(object: Totals): Resumen {
    return {
      actasContabilizadas: object?.actasContabilizadas,
      contabilizadas: object?.contabilizadas,
      actasEnviadasJee: object?.actasEnviadasJee,
      enviadasJee: object?.enviadasJee,
      actasPendientes: object?.actasPendientesJee,
      pendientesJee: object?.pendientesJee,
      fechaActualizacion: object?.fechaActualizacion,
      idUbigeoDepartamento: object?.idUbigeoDepartamento,
      idUbigeoProvincia: object?.idUbigeoProvincia,
      idUbigeoDistrito: object?.idUbigeoDistrito,
      participacionCiudadana: object?.participacionCiudadana,
      totalActas: object?.totalActas,
      actasPendientesJee: object?.actasPendientesJee,
      porcentajeVotosEmitidos: object?.porcentajeVotosEmitidos,
      porcentajeVotosValidos: object?.porcentajeVotosValidos,
      totalVotosEmitidos: object?.totalVotosEmitidos,
      totalVotosValidos: object?.totalVotosValidos,
    } as Resumen;
  }

  public regionChanged($event: RegionValue, $scope: ILoadGeneralSummaryComponent) {
    $scope.regionValue = $event;
    if ($event == REGION_PERU) {
      this.loadGeneralSummaryByRegion($scope);
    } else if ($event == REGION_EXTRAJERO) {
      this.loadGeneralSummaryByRegion($scope);
    } else {
      this.loadGeneralSummary($scope);
    }
  }

  public filterDistrictElectionChart(params: FilterByLocationParams, $scope: ILoadGeneralSummaryComponent) {
    $scope.selectedFilterParams = params;
    this.loadGeneralSummaryByRegionByUbigeo(params, $scope);
  }

  public loadGeneralSummary($scope: ILoadGeneralSummaryComponent) {
    this.getTotals$({
      idEleccion: ($scope.electionID || $scope.electionId) ?? 0,
      tipoFiltro: "eleccion",
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          $scope.resumen = this.getFormattedSummary(response.data!);
        } else {
          console.log("getGeneralSummaryTotals error");
        }
      });
  }

  // TODO: REPLACE ANY
  private getGeographicalScope($scope: any): number {
    return $scope.regionValue == "PERÚ" ? GEOGRAPHIC_SCOPE : GEOGRAPHIC_SCOPE_EXTRANJERA;
  }

  // TODO: REPLACE ANY
  public loadGeneralSummaryByRegion($scope: any) {
    this.getTotals$({
      idAmbitoGeografico: this.getGeographicalScope($scope),
      idEleccion: $scope.electionID || $scope.electionId,
      tipoFiltro: getFilterTypeForBackend(),
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          $scope.resumen = this.getFormattedSummary(response.data!);
        } else {
          console.log("loadPresidentialElectionInfo error");
        }
      });
  }

  // TODO: REPLACE ANY
  public loadGeneralSummaryByRegionByUbigeo(params: FilterByLocationParams, $scope: any) {
    this.getTotals$({
      idAmbitoGeografico: this.getGeographicalScope($scope),
      idEleccion: $scope.electionID || $scope.electionId,
      tipoFiltro: getFilterTypeForBackend(params),
      idUbigeoDepartamento: $scope.selectedFilterParams.departmentUbigeoId,
      idUbigeoDistrito: $scope.selectedFilterParams.districtUbigeoId,
      idUbigeoProvincia: $scope.selectedFilterParams.provinceUbigeoId,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          $scope.resumen = this.getFormattedSummary(response.data!);
        } else {
          console.log("loadPresidentialElectionInfo error");
        }
      });
  }

  public loadGeneralSummaryByGenericFilters(params: GenericFilterParams, $scope: ILoadGeneralSummaryComponent) {
    this.getTotals$({
      idAmbitoGeografico: params.idAmbitoGeografico,
      idEleccion: ($scope.electionID || $scope.electionId) ?? 0,
      tipoFiltro: params.tipoFiltro ?? "",
      idUbigeoDepartamento: params.ubigeoNivel1,
      idUbigeoDistrito: params.ubigeoNivel3,
      idUbigeoProvincia: params.ubigeoNivel2,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          $scope.resumen = this.getFormattedSummary(response.data!);
        } else {
          console.log("loadPresidentialElectionInfo error");
        }
      });
  }

  listarElecciones(
    activo: number,
    idProceso: number,
    idNivel01: number,
    idNivel02: number,
    idDistrito: number,
    tipoFiltro: string,
    idAmbitoGeografico: number
  ): Observable<FrontendResponse<[ResumenGeneral]>> {
    return this.resumenGeneralApiService.listarElecciones(
      activo,
      idProceso,
      idNivel01,
      idNivel02,
      idDistrito,
      tipoFiltro,
      idAmbitoGeografico
    );
  }

  obtenerResumenGeneral(
    idAmbitoGeografico: number,
    idEleccion: number,
    tipoFiltro: string,
    idDistritoElectoral?: number,
    idUbigeoDepartamento?: number,
    idUbigeoProvincia?: number,
    idUbigeoDistrito?: number,
  ): Observable<FrontendResponse<Resumen>> {
    return this.resumenGeneralApiService.obtenerResumenGeneral(
      idAmbitoGeografico,
      idEleccion,
      tipoFiltro,
      idDistritoElectoral,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito,
    );
  }

  listarMapaCalor(
    codigoAgrupacionPolitica: string,
    idAmbitoGeografico: number,
    idEleccion: number,
    tipoFiltro: string = null,
    ubigeoNivel01?: number,
    ubigeoNivel02?: number,
    ubigeoNivel03?: number
  ): Observable<FrontendResponse<[MapaCalor]>> {
    return this.resumenGeneralApiService.listarMapaCalor(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      idEleccion,
      tipoFiltro,
      ubigeoNivel01,
      ubigeoNivel02,
      ubigeoNivel03
    );
  }
  listarParticipantes(
    idAmbitoGeografico: number,
    idEleccion: number,
    tipoFiltro: string = null,
    idUbigeoDepartamento?: number,
    idUbigeoProvincia?: number,
    idUbigeoDistrito?: number
  ): Observable<FrontendResponse<[AgrupacionPolitica]>> {
    return this.resumenGeneralApiService.listarParticipantes(
      idAmbitoGeografico,
      idEleccion,
      tipoFiltro,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito
    );
  }
}
