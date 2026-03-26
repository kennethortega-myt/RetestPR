import { Injectable } from "@angular/core";
import { Observable, map, catchError } from "rxjs";

import { RequestsService } from "../common/requests.service";
import { environment } from "../../../environments/environment";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import {
  AutoridadesRevocatoriaInformationResponse,
  AutoridadesRevocatoriaInformationResponseData,
  categoryTypeForParticipant,
  InformationDistritalPorCargo,
  InformationDistritalPorCargoResponse,
  InformationDistritalPorCargoResponseData,
  RevocatoriaResumenResponse,
  RevocatoriaResumenResponseData,
} from "../../interfaces/revocatoria.interfaces";

const urls = {
  revocatoria_distrital: environment.apiUrlLocal + "revocatoria-distrital/participantes/",
  resumen: environment.apiUrlLocal + "revocatoria-distrital/resumen",
  participante: environment.apiUrlLocal + "revocatoria-distrital/participante",
};

@Injectable({
  providedIn: "root",
})
export class RevocatoriaDistritalService {
  constructor(private readonly request: RequestsService) {}

  public getParticipantes$(
    category: categoryTypeForParticipant
  ): Observable<FrontendResponse<AutoridadesRevocatoriaInformationResponseData[]>> {
    return this.request.get<AutoridadesRevocatoriaInformationResponse>(urls.revocatoria_distrital + category).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<AutoridadesRevocatoriaInformationResponseData[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getResumen$(): Observable<FrontendResponse<RevocatoriaResumenResponseData>> {
    return this.request.get<RevocatoriaResumenResponse>(urls.resumen).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<RevocatoriaResumenResponseData>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getInformacionDistritalPorCargo$(
    params: InformationDistritalPorCargo
  ): Observable<FrontendResponse<InformationDistritalPorCargoResponseData[]>> {
    return this.request.post<InformationDistritalPorCargoResponse>(urls.participante, params).pipe(
      map((response) => {
        return {
          success: response.body?.success ?? false,
          data: response.body?.data,
        } as FrontendResponse<InformationDistritalPorCargoResponseData[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
