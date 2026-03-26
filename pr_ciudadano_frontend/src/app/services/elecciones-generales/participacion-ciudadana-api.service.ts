import { Injectable } from "@angular/core";
import { RequestsService } from "../common/requests.service";
import { Observable, Subject, catchError, map } from "rxjs";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { environment } from "../../../environments/environment";
import {
  ParticipacionCiudadano,
  ParticipacionCiudadanaResponse,
} from "../../interfaces/participacion-ciudadana.interfaces";

@Injectable({
  providedIn: "root",
})
export class ParticipacionCiudadanaApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarParticipacionCiudadana(tipoFiltro: string): Observable<FrontendResponse<[ParticipacionCiudadano]>> {
    let request = {
      tipoFiltro: tipoFiltro,
    };
    return this.request
      .post<ParticipacionCiudadanaResponse>(
        this.urlServidor + `participacion-ciudadana/departamentos?tipoFiltro=${tipoFiltro}`,
        request
      )
      .pipe(
        map((response) => {
          return {
            success: response.body?.success ?? false,
            data: response.body?.data,
          } as FrontendResponse<[ParticipacionCiudadano]>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
