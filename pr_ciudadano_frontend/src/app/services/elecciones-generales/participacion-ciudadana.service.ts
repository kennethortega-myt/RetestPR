import { Injectable } from "@angular/core";
import {
  DepartmentParams,
  DepartmentsDataDetail,
  DepartmentsResponse,
  MapaDeCalorData,
  MapaDeCalorParams,
  MapaDeCalorResponse,
  ParticipacionCiudadano,
  TotalesDataDetail,
  TotalesParams,
  TotalesResponse,
  UbigeosData,
  UbigeosDetail,
  UbigeosParams,
  UbigeosResponse,
  UbigeosTotalResponse,
} from "../../interfaces/participacion-ciudadana.interfaces";
import { Observable, catchError, map } from "rxjs";
import { FrontendResponse } from "../../interfaces/response.common";

const specificURLs = {
  departments: environment.apiUrlLocal + "participacion-ciudadana/departamentos",
  totales: environment.apiUrlLocal + "participacion-ciudadana/totales",
  ubigeos: environment.apiUrlLocal + "participacion-ciudadana/ubigeos",
  ubigeosTotales: environment.apiUrlLocal + "participacion-ciudadana/ubigeos-total",
  mapaCalor: environment.apiUrlLocal + "resumen-general/mapa-calor",
  mapaCalorParticCiudadana: environment.apiUrlLocal + "participacion-ciudadana/mapa-calor",
};
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";
import { ParticipacionCiudadanaApiService } from "./participacion-ciudadana-api.service";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class ParticipacionCiudadanaService {
  constructor(
    private readonly request: RequestsService,
    private readonly participacionCiudadanaApiService: ParticipacionCiudadanaApiService
  ) {}

  public getDepartments$(params: DepartmentParams): Observable<FrontendResponse<DepartmentsDataDetail[]>> {
    return this.request.post<DepartmentsResponse>(specificURLs.departments, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<DepartmentsDataDetail[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getTotales$(params: TotalesParams): Observable<FrontendResponse<TotalesDataDetail>> {
    return this.request.post<TotalesResponse>(specificURLs.totales, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<TotalesDataDetail>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getUbigeos$(params: UbigeosParams, pagina?: number): Observable<FrontendResponse<UbigeosData>> {
    let url = "";
    if (pagina) {
      const queryParams = `?pagina=${pagina}`;
      url = specificURLs.ubigeos + queryParams;
    } else {
      url = specificURLs.ubigeos;
    }
    return this.request.post<UbigeosResponse>(url, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<UbigeosData>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getTotalUbigeos$(params: UbigeosParams): Observable<FrontendResponse<UbigeosDetail[]>> {
    return this.request.post<UbigeosTotalResponse>(specificURLs.ubigeosTotales, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<UbigeosDetail[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getMapaDeCalor$(params: MapaDeCalorParams): Observable<FrontendResponse<MapaDeCalorData[]>> {
    return this.getCommonMapaDeCalor$(params, specificURLs.mapaCalor);
  }

  public getMapaDeCalorParticCiudadana$(params: MapaDeCalorParams): Observable<FrontendResponse<MapaDeCalorData[]>> {
    return this.getCommonMapaDeCalor$(params, specificURLs.mapaCalorParticCiudadana);
  }

  private getCommonMapaDeCalor$(params: MapaDeCalorParams, url: string) {
    return this.request.post<MapaDeCalorResponse>(url, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<MapaDeCalorData[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarParticipacionCiudadana(tipoFiltro: string): Observable<FrontendResponse<[ParticipacionCiudadano]>> {
    return this.participacionCiudadanaApiService.listarParticipacionCiudadana(tipoFiltro);
  }
}
