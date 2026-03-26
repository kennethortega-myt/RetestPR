import { HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export function catchErrorHandler$(
  errorResponse: HttpErrorResponse
): Observable<{
  success: boolean;
}> {
  return of({
    success: false,
    message: errorResponse.error?.message ?? 'Ocurrió un error!',
  });
}
