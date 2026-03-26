import { Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';

import { RequestsService } from './common/request.service';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { environment } from '../../environments/environment';
import { jwtDecode } from 'jwt-decode'; // Importamos la librería
import { StorageService } from './storage.service';
import { catchErrorHandler$ } from './common/request-error-handler';
import { IReportConfigData } from '../interfaces/detalle-reportes.interfaces';
import { IConfigRequestParams } from '../interfaces/configurar-reportes.interfaces';

/**
 * 1.1 Interfaz para normalizar respuestas del Backend
 * Colocar en el archivo de interfaces o al inicio del servicio.
 */
export interface IBackendResponseWrapper<T> {
  response?: T;  // Algunos microservicios usan este campo
  data?: T;      // Otros usan este
  success?: boolean;
  exito?: boolean;
  message?: string;
  mensaje?: string;
}

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

export interface IJWTDecodedPayload {
  sub: string;
  usr: string;
  per: string;
  iss: string;
  iat: number;
  exp: number;
  codigoOp: string | number;
  usrname: string;
}

@Injectable({
  providedIn: 'root',
})
export class SettingReportService {
  private readonly baseUrl = environment.apiUrl;

  private readonly urls = {
    reportConfigurationUrl: this.baseUrl + '/reporte-automatico',
  };

  constructor(
    private readonly request: RequestsService,
    private readonly mainStorage: StorageService
  ) { }

  public getUserFromToken(): IJWTDecodedPayload | null {
    const jwt = this.mainStorage.getLocalStorageValue('token');
    if (jwt) {
      return jwtDecode<IJWTDecodedPayload>(jwt);
    } else {
      return null;
    }
  }

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
      .put<ISetReportConfigResponse>(this.urls.reportConfigurationUrl + '/' + params.id, params)
      .pipe(
        map((response) => {
          return {
            success: true
          } as FrontendResponse<any>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  /**
   * 1.2 Método para Generación Manual/Acciones
   * Implementa el mapeo para unificar 'exito'/'success' y 'mensaje'/'message'.
   */
  public generarReporteManual$(id: string): Observable<FrontendResponse<any>> {
    return this.request
      .post<IBackendResponseWrapper<any>>(`${this.urls.reportConfigurationUrl}/generar-reporte/${id}`, {})
      .pipe(
        map((response) => {
          const body = response.body!;
          const actualData = body.response || body;
          return {
            success: actualData.success || actualData.exito || false,
            data: {
              ...actualData,
              mensaje: actualData.mensaje || actualData.message
            }
          } as FrontendResponse<any>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
