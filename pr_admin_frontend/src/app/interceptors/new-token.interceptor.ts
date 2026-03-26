import {
  HttpEvent,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth-service.service';
import { map, catchError } from 'rxjs';
import { Constantes } from '../helpers/constantes';

export const newTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);  
  const sessionId = obtenerIdSesion();
  req = req.clone({
          setHeaders: {
              IdSession: sessionId,
          }
        });      

  if (auth.getCToken() != null && auth.getCToken() !== '') {
    req = getAuthHeader(req, auth);
  } else {
    router.navigate(['/login']);
  }

  if (!auth.isAuthenticated()) {
    router.navigate(['/login']);
  }

  return next(req).pipe(
    map((event: HttpEvent<any>) => {
      return event;
    }),
    catchError((error: any) => {
      throw error;
    })
  );
};

function getAuthHeader(
  req: HttpRequest<any>,
  auth: AuthService
): HttpRequest<any> {
  const token = auth.getCToken();
  const sessionId = obtenerIdSesion();
  const cleanToken = token.replace(/['"]+/g, '');
  const headers = req.headers
    .set('Authorization', `Bearer ${cleanToken}`)
    .set('X-XSS-Protection', '1; mode=block')
    .set('X-Content-Type-Options', 'nosniff')
    .set('X-Frame-Options', 'SAMEORIGIN')
    .set('Strict-Transport-Security', 'max-age=3600000; includeSubDomains')
    .set('sce', 'sce')
    .set('IdSession', sessionId);
  return req.clone({ headers: headers });
}

 function obtenerIdSesion(): string {
    let idSesion = sessionStorage.getItem(Constantes.SESSION_KEY);
    if (!idSesion) {
      throw new Error(Constantes.ERROR_SESSION);
    }    
    return idSesion;
 }