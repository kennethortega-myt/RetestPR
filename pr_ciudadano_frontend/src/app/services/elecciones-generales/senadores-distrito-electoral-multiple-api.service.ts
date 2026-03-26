import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map } from "rxjs";

import { environment } from "../../../environments/environment";
import { RequestCandidatoBase, CandidatoReponse } from "../../interfaces/candidato";
import { Candidato } from "../../interfaces/eleccion-congresal-bean";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";

@Injectable({
  providedIn: "root",
})
export class SenadoresDistritoElectoralMultipleApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarParticipantesCandidato(requestBase: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.listarBase(requestBase, "participantes-candidato");
  }

  listarParticipantesCandidatoOrganizacion(
    requestBase: RequestCandidatoBase
  ): Observable<FrontendResponse<[Candidato]>> {
    return this.listarBase(requestBase, "participantes-candidato-organizacion");
  }

  listarParticipantesPorUbicacionGeografica(
    requestBase: RequestCandidatoBase
  ): Observable<FrontendResponse<[Candidato]>> {
    return this.listarBase(requestBase, "participantes-ubicacion-geografica");
  }

  listarOrganizacionPolitica(requestBase: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.listarBase(requestBase, "organizacion-politica");
  }

  listarBase(requestBase: RequestCandidatoBase, requestString: string): Observable<FrontendResponse<[Candidato]>> {
    return this.request
      .post<CandidatoReponse>(this.urlServidor + `senadores-distrital-multiple/` + requestString, requestBase)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            totalVotosPorOP: response.body.totalVotosPorOP,
            porcentajeVotoValido: response.body.porcentajeVotoValido,
            porcentajeVotoEmitido: response.body.porcentajeVotoEmitido,
            data: response.body.data,
          } as FrontendResponse<[Candidato]>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
