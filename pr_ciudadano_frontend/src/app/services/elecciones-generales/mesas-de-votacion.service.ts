import { Injectable } from "@angular/core";
import { RequestsService } from "../common/requests.service";
import { Observable, map, catchError } from "rxjs";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { environment } from "../../../environments/environment";
import { MesasDetailParams, MesasDetail, MesasDetailResponse } from "../../interfaces/mesas-de-votacion.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";

const specificURLs = {
  mesaTotales: environment.apiUrlLocal + "mesa/totales",
};

@Injectable({
  providedIn: "root",
})
export class MesasDeVotacionService {
  constructor(private readonly request: RequestsService) {}

  public getMesasDetail$(params: MesasDetailParams): Observable<FrontendResponse<MesasDetail>> {
    return this.request.post<MesasDetailResponse>(specificURLs.mesaTotales, params).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<MesasDetail>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
