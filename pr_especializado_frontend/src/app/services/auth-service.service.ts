import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { catchError, map, Observable } from 'rxjs';

import { RequestsService } from './common/request.service';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { catchErrorHandler$ } from './common/request-error-handler';
import { environment } from '../../environments/environment';
import { AccesoRequest, Login, LoginResponse, PasswordUpdateResponse, Restore, SesionActiva } from '../interfaces/login';

export interface IRecoveryPasswordParams {
  userOrEmail: string;
}

export interface IRecoveryPasswordData { }

export interface IRecoveryPasswordResponse extends GenericResponse {
  data: IRecoveryPasswordData;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = environment.apiUrl;
  private readonly authUrl = `${environment.apiUrl}/api/auth/`;

  constructor(private readonly http: HttpClient, private readonly request: RequestsService) { }

  public notifyRecoveryPassword$(
    params: IRecoveryPasswordParams
  ): Observable<FrontendResponse<IRecoveryPasswordData>> {
    const currentUrl = `${this.baseUrl}/recuperar-contrasena`;
    return this.request
      .post<IRecoveryPasswordResponse>(currentUrl, params)
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IRecoveryPasswordData>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  passwordRestore(restore: Restore) {
    const body = restore;
    return this.http.post<any>(
      `${this.authUrl}restablecer-contrasenia`,
      body
    );
  }

  getToken(login: Login): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authUrl + 'login', login);
  }

  refreshToken(): Observable<any> {
    const refreshToken = this.getCTokenRefresh();
    return this.http.post<any>(`${this.authUrl}refreshtoken`, {
      refreshToken: refreshToken,
    });
  }

  public getCTokenRefresh(): string {
    const token = localStorage.getItem('refreshToken');
    if (token == null || token === undefined) {
      return '';
    }
    try {
      return JSON.parse(token);
    } catch (e) {
      console.error('Error al parsear token: ', e);
      return '';
    }
  }

  public getCToken(): string {
    const token = localStorage.getItem('token');
    if (token == null || token === undefined) {
      return '';
    }
    try {
      return token;
    } catch (e) {
      console.error('Error al parsear token: ', e);
      return '';
    }
  }

  logout() {
    return this.http.post(this.baseUrl + 'logout', '');
  }

  public isAuthenticated(): boolean {
    const token = this.getCToken();
    const helper = new JwtHelperService();
    const isTokenExpired = helper.isTokenExpired(token);
    return !isTokenExpired;
  }

  public isAuthenticatedRefresh(): boolean {
    const tokenRefresh = this.getCTokenRefresh();
    const helper = new JwtHelperService();
    const isTokenExpired = helper.isTokenExpired(tokenRefresh);
    return !isTokenExpired;
  }

  passwordUpdate(nuevaclave: string, nuevaclave2: string, tokenSasa: string): Observable<PasswordUpdateResponse> {
    const body = {
      clave: nuevaclave,
      clave2: nuevaclave2,
      tokenSasa: tokenSasa,
    };
    return this.http.post<PasswordUpdateResponse>(
      `${this.authUrl}actualizar-contrasenia`,
      body
    );
  }

  getAccesos(data: AccesoRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authUrl + 'cargar-accesos', data);
  }

  cerrarSessionActiva(data: SesionActiva): Observable<GenericResponse> {
    return this.http.post<LoginResponse>(this.authUrl + 'cerrar-sesion-activa', data);
  }
}
