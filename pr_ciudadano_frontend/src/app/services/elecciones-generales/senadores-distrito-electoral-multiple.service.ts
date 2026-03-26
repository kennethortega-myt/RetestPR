import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { SenadoresDistritoElectoralMultipleApiService } from "./senadores-distrito-electoral-multiple-api.service";
import { RequestCandidatoBase } from "../../interfaces/candidato";
import { Candidato } from "../../interfaces/eleccion-congresal-bean";
import { FrontendResponse } from "../../interfaces/response.common";

@Injectable({
  providedIn: "root",
})
export class SenadoresDistritoElectoralMultipleService {
  constructor(private senadoresDistritoElectoralMultipleApiService: SenadoresDistritoElectoralMultipleApiService) {}

  listarParticipantesCandidato(request: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.senadoresDistritoElectoralMultipleApiService.listarParticipantesCandidato(request);
  }

  listarParticipantesCandidatoOrganizacion(request: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.senadoresDistritoElectoralMultipleApiService.listarParticipantesCandidatoOrganizacion(request);
  }

  listarParticipantesPorUbicacionGeografica(request: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.senadoresDistritoElectoralMultipleApiService.listarParticipantesPorUbicacionGeografica(request);
  }

  listarOrganizacionPolitica(request: RequestCandidatoBase): Observable<FrontendResponse<[Candidato]>> {
    return this.senadoresDistritoElectoralMultipleApiService.listarOrganizacionPolitica(request);
  }
}
