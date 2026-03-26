import { Injectable } from "@angular/core";
import { Observable, catchError, forkJoin, map } from "rxjs";
import { ParticipacionCiudadanaService } from "./participacion-ciudadana.service";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { UBIGEO_LEVELS } from "../../helpers/constantes";
import { getGeograpScopeByScope } from "../../helpers/election-type.config";
import {
  DepartmentParams,
  ProvinceParams,
  DistrictParams,
  District,
  InternationalUbigeo,
  Department,
  Province,
} from "../../interfaces/elections.interfaces";
import { ElectionScope, IHotMapUbigeoItem } from "../../interfaces/hot-map.interfaces";
import { MapaDeCalorParams, MapaDeCalorData } from "../../interfaces/participacion-ciudadana.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";
import { MULTIPLIER_PERCENTAGE_VALID_VOTES, MULTIPLIER_HOT_MAP, MULTIPLIER_HOT_MAP_REVOCA } from "../../settings/map.settings";
import { BaseUbigeoService } from "../common/base-ubigeo.service";
import { encryptStorageEleccion } from "../../settings/encrypt-storage.settings";
import { isRevocatoria } from "../../helpers/storage-helpers/encrypt-storage.helper";
import { PoliticalOrganizationItem } from "../../interfaces/presidenciales.interfaces";

const ELECTION_ID_FOR_UBIGEOS = 10;

@Injectable({
  providedIn: "root",
})
export class HotMapService {
  public electionId?: number;
  public electionIdForUbigeos?: number;
  public isParticipaciónCiudadana = false;

  constructor(
    private participacionCiudadanaService: ParticipacionCiudadanaService,
    private baseUbigeoService: BaseUbigeoService
  ) {
    this.electionIdForUbigeos = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO")).idEleccionPrincipal ?? ELECTION_ID_FOR_UBIGEOS;
  }

  // PRIVATE METHODS

  private getDepartmentParams(scope: ElectionScope): DepartmentParams {
    return {
      idEleccion: this.electionIdForUbigeos,
      idAmbitoGeografico: getGeograpScopeByScope(scope),
    };
  }

  private getProvinceParams(ubigeoNivel01: string, scope: ElectionScope): ProvinceParams {
    return {
      idEleccion: this.electionIdForUbigeos,
      idAmbitoGeografico: getGeograpScopeByScope(scope),
      idUbigeoDepartamento: ubigeoNivel01,
    };
  }

  private getDistrictParams(ubigeoNivel02: string, scope: ElectionScope): DistrictParams {
    return {
      idEleccion: this.electionIdForUbigeos,
      idAmbitoGeografico: getGeograpScopeByScope(scope),
      idUbigeoProvincia: ubigeoNivel02,
    };
  }

  private getHotParamsForDepartments(scope: ElectionScope, codigoAgrupacionPolitica?: string): MapaDeCalorParams {
    const params: MapaDeCalorParams = {
      idAmbitoGeografico: getGeograpScopeByScope(scope),
      idEleccion: this.electionId!,
      tipoFiltro: UBIGEO_LEVELS.ALL_LABEL,
    };
    if (codigoAgrupacionPolitica) {
      params.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }
    return params;
  }

  private getHotParamsForProvinces(
    ubigeoNivel01: string,
    scope: ElectionScope,
    codigoAgrupacionPolitica?: string
  ): MapaDeCalorParams {
    const params: MapaDeCalorParams = {
      idAmbitoGeografico: getGeograpScopeByScope(scope),
      idEleccion: this.electionId!,
      tipoFiltro: UBIGEO_LEVELS.LEVEL_01,
      ubigeoNivel01: ubigeoNivel01,
    };
    if (codigoAgrupacionPolitica) {
      params.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }
    return params;
  }

  private getHotParamsForDistricts(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    scope: ElectionScope,
    codigoAgrupacionPolitica?: string
  ): MapaDeCalorParams {
    const params: MapaDeCalorParams = {
      idAmbitoGeografico: getGeograpScopeByScope(scope),
      idEleccion: this.electionId!,
      tipoFiltro: UBIGEO_LEVELS.LEVEL_02,
      ubigeoNivel01: ubigeoNivel01,
      ubigeoNivel02: ubigeoNivel02,
    };
    if (codigoAgrupacionPolitica) {
      params.codigoAgrupacionPolitica = codigoAgrupacionPolitica;
    }
    return params;
  }

  private getIHotMapUbigeoItem(
    matchedElement: District | InternationalUbigeo,
    item: MapaDeCalorData,
    candidato?: PoliticalOrganizationItem
  ): IHotMapUbigeoItem {    
    const totalVotosCandidato = candidato?.totalVotosValidos ?? 0;

    const porcentajeVotosValidos = item.participante && totalVotosCandidato > 0
      ? ((item.participante.totalVotosValidos / totalVotosCandidato) * 100) * MULTIPLIER_PERCENTAGE_VALID_VOTES
      : 0;

    const MULTIPLIER = isRevocatoria() ? MULTIPLIER_HOT_MAP_REVOCA : MULTIPLIER_HOT_MAP

    const newItem = {
      ubigeo: matchedElement.ubigeo,
      ubigeoName: matchedElement.nombre,
      candidateName: item.participante ? item.participante.nombreCandidato : "",
      percentage: item.porcentajeActasContabilizadas * MULTIPLIER,
      validVotes: item.participante ? item.participante.totalVotosValidos : 0,
      percentageValidVotes: item.porcentajeAsistentes
        ? item.porcentajeAsistentes * MULTIPLIER
        : porcentajeVotosValidos,
    } as IHotMapUbigeoItem;

    return newItem;
  }

  // PUBLIC METHODS

  public getJoinedHotMapWorld$(codigoAgrupacionPolitica?: string): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const params = {
      idEleccion: this.electionId!,
      tipoFiltro: UBIGEO_LEVELS.TOTAL,
    } as MapaDeCalorParams;

    return this.getMapaDeCalorObs$(params).pipe(
      map((hotMapResponse) => {
        const MULTIPLIER =  isRevocatoria() ? MULTIPLIER_HOT_MAP_REVOCA : MULTIPLIER_HOT_MAP
        const items = hotMapResponse.data!!.map((item, index) => {
          return {
            ubigeo: "",
            ubigeoName: "",
            candidateName: "",
            percentage: item.porcentajeActasContabilizadas * MULTIPLIER,
            validVotes: 0,
            percentageValidVotes: 0,
          } as IHotMapUbigeoItem;
        });
        return {
          success: true,
          data: items,
        } as FrontendResponse<[IHotMapUbigeoItem]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getJoinedHotMapDepartments$(
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const departments$ = this.baseUbigeoService.getDepartments$(this.getDepartmentParams("peru"));
    return this.getFirstLevelInformation$(departments$, "peru", codigoAgrupacionPolitica, candidato);
  }

  public getJoinedHotMapContinents$(
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const departments$ = this.baseUbigeoService.getInternationalContinetals$(this.getDepartmentParams("international"));

    return this.getFirstLevelInformation$(departments$, "international", codigoAgrupacionPolitica, candidato);
  }

  private getFirstLevelInformation$(
    firstLevelUbigeo$: Observable<FrontendResponse<Department[]>>,
    scope: ElectionScope,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ) {
    const params = this.getHotParamsForDepartments(scope, codigoAgrupacionPolitica);
    const hotMap$ = this.getMapaDeCalorObs$(params);

    return forkJoin([firstLevelUbigeo$, hotMap$]).pipe(
      map(([departmentsResponse, hotMapResponse]) => {
        const items = hotMapResponse.data!.map((item, index) => {
          const matchedDepartment = departmentsResponse.data!.find((e) => {
            return Number(e.ubigeo) == Number(item.ubigeoNivel01);
          });
          return this.getIHotMapUbigeoItem(matchedDepartment!, item, candidato);
        });
        return {
          success: true,
          data: items,
        } as FrontendResponse<[IHotMapUbigeoItem]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getJoinedHotMapProvinces$(
    ubigeoNivel01: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const provinces$ = this.baseUbigeoService.getProvinces$(this.getProvinceParams(ubigeoNivel01, "peru"));
    return this.getSecondLevelInformation$(provinces$, ubigeoNivel01, "peru", codigoAgrupacionPolitica, candidato);
  }

  public getJoinedHotMapCountries$(
    ubigeoNivel01: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const provinces$ = this.baseUbigeoService.getProvinces$(this.getProvinceParams(ubigeoNivel01, "international"));
    return this.getSecondLevelInformation$(provinces$, ubigeoNivel01, "international", codigoAgrupacionPolitica, candidato);
  }

  private getSecondLevelInformation$(
    secondLevelUbigeo$: Observable<FrontendResponse<Province[]>>,
    ubigeoNivel01: string,
    scope: ElectionScope,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ) {
    const params = this.getHotParamsForProvinces(ubigeoNivel01, scope, codigoAgrupacionPolitica);
    const hotMap$ = this.getMapaDeCalorObs$(params);

    return forkJoin([secondLevelUbigeo$, hotMap$]).pipe(
      map(([provincesResponse, hotMapResponse]) => {
        const items = hotMapResponse.data!.map((item, index) => {
          const matchedProvince = provincesResponse.data!.find((e) => {
            return Number(e.ubigeo) == Number(item.ubigeoNivel02);
          });
          return this.getIHotMapUbigeoItem(matchedProvince!, item, candidato);
        });
        return {
          success: true,
          data: items,
        } as FrontendResponse<[IHotMapUbigeoItem]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getJoinedHotMapDistricts$(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const districts$ = this.baseUbigeoService.getDistricts$(this.getDistrictParams(ubigeoNivel02, "peru"));
    return this.getThirdLevelInformation$(districts$, ubigeoNivel01, ubigeoNivel02, "peru", codigoAgrupacionPolitica, candidato);
  }

  public getJoinedHotMapStates$(
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    codigoAgrupacionPolitica?: string
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const districts$ = this.baseUbigeoService.getDistricts$(this.getDistrictParams(ubigeoNivel02, "international"));
    return this.getThirdLevelInformation$(
      districts$,
      ubigeoNivel01,
      ubigeoNivel02,
      "international",
      codigoAgrupacionPolitica
    );
  }

  private getThirdLevelInformation$(
    thirdLevelUbigeo$: Observable<FrontendResponse<Province[]>>,
    ubigeoNivel01: string,
    ubigeoNivel02: string,
    scope: ElectionScope,
    codigoAgrupacionPolitica?: string,
    candidato?: PoliticalOrganizationItem
  ): Observable<FrontendResponse<[IHotMapUbigeoItem]>> {
    const params = this.getHotParamsForDistricts(ubigeoNivel01, ubigeoNivel02, scope, codigoAgrupacionPolitica);
    const hotMap$ = this.getMapaDeCalorObs$(params);

    return forkJoin([thirdLevelUbigeo$, hotMap$]).pipe(
      map(([districtsResponse, hotMapResponse]) => {
        const items = hotMapResponse.data!.map((item, index) => {
          const matchedDistrict = districtsResponse.data!.find((e) => {
            return Number(e.ubigeo) == Number(item.ubigeoNivel03);
          });
          return this.getIHotMapUbigeoItem(matchedDistrict!, item, candidato);
        });
        return {
          success: true,
          data: items,
        } as FrontendResponse<[IHotMapUbigeoItem]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  // HELPERS

  public getMapaDeCalorObs$(params: MapaDeCalorParams) {
    if (this.isParticipaciónCiudadana) {
      return this.participacionCiudadanaService.getMapaDeCalorParticCiudadana$(params);
    } else {
      return this.participacionCiudadanaService.getMapaDeCalor$(params);
    }
  }
}
