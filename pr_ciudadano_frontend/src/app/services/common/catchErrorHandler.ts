import { HttpErrorResponse } from "@angular/common/http";
import { Observable, of } from "rxjs";

export function catchErrorHandler$(_: HttpErrorResponse): Observable<{
  success: boolean;
}> {
  return of({ success: false });
}