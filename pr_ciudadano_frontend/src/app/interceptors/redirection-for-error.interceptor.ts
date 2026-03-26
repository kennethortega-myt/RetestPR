import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, Observable, throwError } from "rxjs";
import { errorRedirectionRoutes } from "../settings/error.routing.settings";
import { PATHS } from "../settings/app.routes.settings";

export function redirectionForErrorInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const router = inject(Router);
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error("redirectionForErrorInterceptor: validating error status code");
      const errorRedirectionPath: string = errorRedirectionRoutes[error.status] ?? PATHS.error_inesperado;
        router.navigate([errorRedirectionPath]);  
      return throwError(() => error);
    })
  );
}
