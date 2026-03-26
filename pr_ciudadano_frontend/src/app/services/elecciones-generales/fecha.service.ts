import { Injectable } from "@angular/core";
import { Subject, Observable, map, catchError } from "rxjs";
import { environment } from "../../../environments/environment";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";
import { Fecha, FechaResponse } from "../../interfaces/fecha";

@Injectable({
  providedIn: "root",
})
export class FechaApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }
  
  listarFecha(): Observable<FrontendResponse<Fecha>> {
    return this.request
        .post<FechaResponse>(this.urlServidor + `fecha/listarFecha`, null)
        .pipe(
         map((response) => {
            return {
            success: response.body.success,
            data: response.body.data,
            } as FrontendResponse<Fecha>;
        }),
      catchError(catchErrorHandler$)
    );
  }

}