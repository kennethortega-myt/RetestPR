import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DataLoginStore } from '../states/data-login.store';
import { GenericResponse } from './common/response.common';

@Injectable({
  providedIn: 'root'
})
export class UsuarioApiService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = environment.apiUrl;
  private readonly dataLoginStore = inject(DataLoginStore);

  cerrarSesion(): void {
    this.postCloseSession().subscribe({
      next: () => this.clearDataAndNavigateToLogin(),
      error: () => this.clearDataAndNavigateToLogin()
    });
  }

  private postCloseSession(): Observable<GenericResponse> {
    return this.http.post<GenericResponse>(`${this.baseUrl}/cerrar-sesion`, {});
  }

  private clearDataAndNavigateToLogin(): void {
    this.dataLoginStore.clearDataLogin();
    this.router.navigate(['/']);
  }
}
