import { Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';

import { RequestsService } from './common/request.service';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { environment } from '../../environments/environment';
import { catchErrorHandler$ } from './common/request-error-handler';
import { IReportConfigData } from '../interfaces/detalle-reportes.interfaces';
import { IConfigRequestParams } from '../interfaces/configurar-reportes.interfaces';
import { jwtDecode } from 'jwt-decode'; // Importamos la librería
import { StorageService } from './storage.service';

/**
 * Param mapear el objeto que devuelve backend
 */
export interface IGetAllReportsConfigResponse extends GenericResponse {
  data: IReportConfigData[];
}
export interface IGetReportConfigResponse extends GenericResponse {
  data: IReportConfigData;
}
export interface ISetReportConfigResponse extends GenericResponse {
  data: any;
}

/**
 * Interface para manejar la respuesta anidada del backend
 */
export interface IBackendResponseWrapper<T> {
  response?: T;
  success?: boolean;
  message?: string;
  data?: T;
  exito?: boolean;
  mensaje?: string;
}

export interface IGenerarReporteResponse extends GenericResponse {
  success: boolean;
  mensaje: string;
  fechaEjecucion: string;
  detalles: string[];
}

export interface IJWTDecodedPayload {
  sub: string;
  usr: string;
  per: string;
  iss: string;
  iat: number;
  exp: number;
  codigoOp: string | number;
  usrname: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class SettingReportService {

  private baseUrl = environment.apiUrl;

  private urls = {
    reportConfigurationUrl: this.baseUrl + '/reporte-automatico',
  };

  public getUserFromToken(): IJWTDecodedPayload | null {
    const jwt = this.mainStorage.getLocalStorageValue('token');
    if (jwt) {
      return jwtDecode<IJWTDecodedPayload>(jwt);
    } else {
      return null;
    }
  }

  constructor(
    private request: RequestsService,
    private mainStorage: StorageService
  ) { }

  public getAllReportsConfigurations$(): Observable<
    FrontendResponse<IReportConfigData[]>
  > {
    return this.request
      .get<IGetAllReportsConfigResponse>(this.urls.reportConfigurationUrl)
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IReportConfigData[]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public getReportConfigurationById$(
    id: string
  ): Observable<FrontendResponse<IReportConfigData>> {
    return this.request
      .get<IGetReportConfigResponse>(this.urls.reportConfigurationUrl)
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data
          } as FrontendResponse<IReportConfigData>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public saveReportConfiguration$(
    params: IConfigRequestParams
  ): Observable<FrontendResponse<any>> {

    params.usuario = this.getUserFromToken()?.usr

    return this.request
      .post<ISetReportConfigResponse>(this.urls.reportConfigurationUrl, params)
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data
          } as FrontendResponse<any>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public updateReportConfiguration$(
    params: IConfigRequestParams
  ): Observable<FrontendResponse<any>> {

    params.usuario = this.getUserFromToken()?.usr

    return this.request
      .post<ISetReportConfigResponse>(this.urls.reportConfigurationUrl + '/' + params.id, params)
      .pipe(
        map((response) => {
          return {
            success: true
          } as FrontendResponse<any>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public generarReporteManual$(id: string): Observable<FrontendResponse<IGenerarReporteResponse>> {
    return this.request
      .post<IBackendResponseWrapper<IGenerarReporteResponse>>(`${this.urls.reportConfigurationUrl}/generar-reporte/${id}`, {})
      .pipe(
        map((response) => {
          const body = response.body!;
          const actualData = (body.response || body) as any;
          return {
            success: actualData.success || actualData.exito || false,
            data: {
              ...actualData,
              mensaje: actualData.mensaje || actualData.message
            }
          } as FrontendResponse<IGenerarReporteResponse>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
