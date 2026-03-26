import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { GenericResponse } from './common/response.common';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Constantes } from '../helpers/constantes';
import { RefreshTokenResponse } from '../interfaces/RefreshTokenResponse';
import { openDialogMensaje } from '../utils/funciones';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import moment from 'moment';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient,
      public dialog: MatDialog,
      public router: Router) {}

  closeSession(): Observable<GenericResponse> {
    return this.http.post<GenericResponse>(
      `${this.baseUrl}/cerrar-sesion`,{});
  }
  
  refreshToken(refreshToken:string): Observable<RefreshTokenResponse> {
    return this.http.post<RefreshTokenResponse>(`${this.baseUrl}/api/auth/refreshtoken`, { refreshToken } );
  }

  
  cerrarSesion() {
   this.closeSession().subscribe({
      next: (response: GenericResponse) => {
        if(response.success){
          localStorage.clear();
          sessionStorage.clear();
          this.router.navigate(['/inicio']);
        }
        else{
          openDialogMensaje(this.dialog,response.message ?? 'Error al cerrar sesión', 2);
        }
      },
      error: (error: HttpErrorResponse) => {        
       openDialogMensaje(this.dialog,error.message, 2).afterClosed().subscribe(() => {
        this.router.navigate(['/inicio']);
       });
      }
    }); 
  }
  
  getDuration() {
    const helper = new JwtHelperService();
    const token = localStorage.getItem(Constantes.TOKEN);
    const refreshToken = localStorage.getItem(Constantes.REFRESH_TOKEN);
    
    const decodedToken = helper.decodeToken(token + "");
    const decodedRefreshToken = helper.decodeToken(refreshToken + "");
    
    if (decodedToken != null && decodedRefreshToken != null) {
    
    const tokenExpirado = helper.isTokenExpired(token);
    const refreshTokenExpirado = helper.isTokenExpired(refreshToken);
    
      if (tokenExpirado || refreshTokenExpirado) {
        this.router.navigate(['/inicio']);
        return 0;
      } else {
        const fechaExpiracion = moment(decodedToken.exp * 1000);
        const fechaActual = moment();
        const tiempo = fechaExpiracion.diff(fechaActual, 'seconds') - 10;
        return tiempo;
      }
    } else {
      this.cerrarSesion();
      return 0;
    }
  }
}