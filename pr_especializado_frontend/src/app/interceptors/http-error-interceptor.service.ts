import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { CODE_ERROR_403 } from '../helpers/constantes';
import { Router } from '@angular/router';
import { DataLoginStore } from '../states/data-login.store';

@Injectable()
export class HttpErrorInterceptorService implements HttpInterceptor {
  constructor(
    private readonly dataLoginStore: DataLoginStore,
    private readonly router: Router) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if(error.status == CODE_ERROR_403){
            this.dataLoginStore.clearDataLogin();
            this.router.navigateByUrl('/');
        }
        return throwError(() => error);
      })
    );
  }
}
