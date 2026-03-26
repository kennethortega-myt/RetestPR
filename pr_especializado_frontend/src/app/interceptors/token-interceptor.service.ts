import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, first, Observable, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth-service.service';
import { ERROR_SESSION, SESSION_KEY } from '../helpers/constantes';
import { DialogService } from '../services/dialog.service';

@Injectable({
  providedIn: 'root',
})
export class TokenInterceptorService implements HttpInterceptor {
  private isRefreshing = false;

  constructor(private readonly router: Router, 
    private readonly authService: AuthService, private readonly dialogService:DialogService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
      
    const token: string = localStorage.getItem('token')!;
    if (!req.url.includes('/assets/i18n') && !req.url.includes('apigoogle') ) {
      const sessionId = this.obtenerIdSesion();
      req = req.clone({
            setHeaders: {
                IdSession: sessionId
            }
          });
    }

    let request = req;
    if (token && !req.url.includes('api/auth/refreshtoken')) {
      const cleanToken = token.replaceAll(/['"]+/g, '');
      request = req.clone({
        setHeaders: {
          Authorization: `Bearer ${cleanToken}`,
        },
      });
    }
    return next.handle(request).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401 && !req.url.includes('api/auth')) {
          if (!this.isRefreshing) {
            this.isRefreshing = true;

            return this.authService.refreshToken().pipe(
              switchMap((res: any) => {
                this.isRefreshing = false;

                // Guardar nuevos tokens
                localStorage.setItem('token', JSON.stringify(res.token));
                localStorage.setItem('refreshToken', JSON.stringify(res.refreshToken));

                // Reintentar petición original con el nuevo token
                const newReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${res.token}`,
                  },
                });
                return next.handle(newReq);
              }),
              catchError((refreshErr) => {
                this.isRefreshing = false;
                // Refresh token failed: clear storage and redirect to login
                localStorage.clear();
                sessionStorage.clear();
                this.router.navigateByUrl('/');
                return throwError(() => refreshErr);
              })
            );
          }
        }
        return throwError(() => err);
      })
    );
  }

  private obtenerIdSesion(): string {
    let idSesion = sessionStorage.getItem(SESSION_KEY);
    if (!idSesion) {
       this.dialogService.show('error', ERROR_SESSION, 'Error')
          .pipe(first())
          .subscribe({
            next: () => this.router.navigateByUrl('/'),
                error: () => this.router.navigateByUrl('/')
          });        
       throw new Error(ERROR_SESSION); 
    }   
    return idSesion;
 }
}
