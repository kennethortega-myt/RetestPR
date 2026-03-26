import { inject } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpInterceptorFn} from '@angular/common/http';
import { finalize, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoadingService } from '../services/loading.service';

export const loaderInterceptor: HttpInterceptorFn = (req, next) => {

    const loaderService = inject(LoadingService);
  
    loaderService.startRequest();
  
    return next(req).pipe(
      finalize(() => {
        loaderService.endRequest();
      }),
      catchError(err => {
  
        if (err instanceof HttpErrorResponse && err.error instanceof Blob && err.error.type === "application/json") {
  
          return new Observable<HttpEvent<any>>((observer) => {
            const reader = new FileReader();
  
            reader.onload = () => {
              try {
                const parsedError = JSON.parse(reader.result as string);
                const errorResponse = new HttpErrorResponse({
                  error: parsedError,
                  headers: err.headers,
                  status: err.status,
                  statusText: err.statusText,
                  url: err.url ?? undefined,
                });
  
                observer.error(errorResponse);
              } catch (e) {
                observer.error(err);
              }
            };
  
            reader.onerror = () => observer.error(err);
  
            reader.readAsText(err.error);
          });
        }
        return throwError(() => err);
      })
    );
  }