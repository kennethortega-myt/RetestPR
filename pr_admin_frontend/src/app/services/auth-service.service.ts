import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../environments/environment';
import { Login, Restore } from '../interfaces/login';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = environment.apiUrl;

  private url = '';
  constructor(private readonly http: HttpClient) {}

  getToken(login: Login) {
    return this.http.post(this.baseUrl + '/api/auth/login', login);
  }

  public getCTokenRefresh(): string {
    if (localStorage.getItem('token') == null) {
      return '';
    }
    return JSON.parse(localStorage.getItem('token') ?? '');
  }

  public getCToken(): string {
    if (localStorage.getItem('token') == null) {
      return '';
    }
    return localStorage.getItem('token') ?? '';
  }

  logout() {
    this.url = environment.apiUrl + 'logout';
    return this.http.post(this.url, '');
  }

  passwordRestore(restore: Restore) {
    const body = restore;
    return this.http.post<any>(
      `${this.baseUrl}/api/auth/restablecer-contrasenia`,
      body
    );
  }

  passwordUpdate(nuevaclave: string, nuevaclave2:string, tokenSasa: string){
    const body = {
      clave: nuevaclave,
      clave2: nuevaclave2,
      tokenSasa: tokenSasa,
    };
    return this.http.post<any>(
      `${this.baseUrl}/api/auth/actualizar-contrasenia`,
      body
    );
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

  getAccesos(data: {
    idUsuario?: number;
    idPerfil?: number;
  }) {
    return this.http.post(`${this.baseUrl}/api/auth/cargar-accesos`, data);
  }
}
