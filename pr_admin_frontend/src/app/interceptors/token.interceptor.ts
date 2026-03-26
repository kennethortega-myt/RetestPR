import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from '../services/auth-service.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private readonly router: Router, public readonly auth: AuthService) {}

  public intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (this.auth.getCToken() != null && this.auth.getCToken() !== '') {
      req = this.getAuthHeader(req);
    } else {
      this.router.navigate(['/login']);
    }

    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
    }

    return next.handle(req).pipe(
      map((event: HttpEvent<any>) => {
        return event;
      }),
      catchError((error: any) => {
        return this.catchError(error, req, next);
      })
    );
  }

  catchError(
    error: any,
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<any> {
    return throwError(() => error);
  }

  private getAuthHeader(req: HttpRequest<any>): HttpRequest<any> {
    const token = this.auth.getCToken();
    const cleanToken = token.replace(/['"]+/g, '');
    const headers = req.headers
      .set('Authorization', `Bearer ${cleanToken}`)
      .set('X-XSS-Protection', '1; mode=block')
      .set('X-Content-Type-Options', 'nosniff')
      .set('X-Frame-Options', 'SAMEORIGIN')
      .set('Strict-Transport-Security', 'max-age=3600000; includeSubDomains')
      .set('sce', 'sce');
    return req.clone({ headers: headers });
  }
}
