import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map } from "rxjs";

import { environment } from "../../../environments/environment";
import { Padron, PadronResponse } from "../../interfaces/padron-bean";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";

@Injectable({
  providedIn: "root",
})
export class PadronApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly httpClient: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  buscarPorDni(dni: string): Observable<FrontendResponse<Padron>> {
    return this.httpClient.get<PadronResponse>(this.urlServidor + `padron/mesa/${dni}`).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Padron>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
