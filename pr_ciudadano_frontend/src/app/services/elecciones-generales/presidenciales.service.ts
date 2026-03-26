import { Injectable } from "@angular/core";
import { Observable, map, catchError } from "rxjs";

import { RequestsService } from "../common/requests.service";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { environment } from "../../../environments/environment";
import {
  GeographicalLocationParams,
  GeographicalLocationItem,
  GeographicalLocationResponse,
  PoliticalOrganizationParams,
  PoliticalOrganizationItem,
  PoliticalOrganizationResponse,
  GeographicalLocationNameParams,
  GeographicalLocationNameInfo,
  GeographicalLocationNameResponse,
  GeographicalLocationNameItem,
} from "../../interfaces/presidenciales.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";
import { mapWithPoliticImage } from "../../helpers/get-images.helper";

const specificURLs = {
  geographicalLocation: environment.apiUrlLocal + "eleccion-presidencial/participantes-ubicacion-geografica",
  politicalOrganization: environment.apiUrlLocal + "eleccion-presidencial/participantes-organizacion-politica",
  geographicalLocationName: environment.apiUrlLocal + "eleccion-presidencial/participantes-ubicacion-geografica-nombre",
};

@Injectable({
  providedIn: "root",
})
export class PresidencialesService {
  constructor(private readonly request: RequestsService) {}

  public getParticipantsByGeographicalLocation$(
    params: GeographicalLocationParams
  ): Observable<FrontendResponse<GeographicalLocationItem[]>> {
    return this.request.post<GeographicalLocationResponse>(specificURLs.geographicalLocation, params).pipe(
      map((response) => {
        return {
          success: true,
          data: response.body.data,
        } as FrontendResponse<GeographicalLocationItem[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getParticipantsByPoliticalOrganization$(
    params: PoliticalOrganizationParams
  ): Observable<FrontendResponse<PoliticalOrganizationItem[]>> {    
    return this.request.post<PoliticalOrganizationResponse>(specificURLs.politicalOrganization, params).pipe(
      map((response) => {
        const data = mapWithPoliticImage(response.body.data);
        return {
          success: true,
          data,
        } as FrontendResponse<PoliticalOrganizationItem[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getParticipantsByGeographicalLocationName$(
    params: GeographicalLocationNameParams
  ): Observable<FrontendResponse<GeographicalLocationNameInfo>> {
    return this.request.post<GeographicalLocationNameResponse>(specificURLs.geographicalLocationName, params).pipe(
      map((response) => {
        const emptyVotesCode = "80";
        const nullVotesCode = "81";        
        const list = mapWithPoliticImage(
          response.body.data.filter((el) => el.codigoAgrupacionPolitica != emptyVotesCode && el.codigoAgrupacionPolitica != nullVotesCode)
        );
        const emptyVotes = response.body.data.find((el) => el.codigoAgrupacionPolitica == emptyVotesCode);
        const nullVotes = response.body.data.find((el) => el.codigoAgrupacionPolitica == nullVotesCode);
        const totals = {
          porcentajeVotosValidos: response.body.data.reduce((prev, curr) => {
            if (curr.codigoAgrupacionPolitica == emptyVotesCode || curr.codigoAgrupacionPolitica == nullVotesCode) {
              return prev;
            }
            return prev + curr.porcentajeVotosValidos;
          }, 0),
          porcentajeVotosEmitidos: response.body.data.reduce((prev, curr) => prev + curr.porcentajeVotosEmitidos, 0),
          totalVotosValidos: response.body.data.reduce((prev, curr) => prev + curr.totalVotosValidos, 0),
        } as GeographicalLocationNameItem;
        return {
          success: true,
          data: {
            list,
            emptyVotes: emptyVotes,
            nullVotes: nullVotes,
            totals: totals,
            listForScales: response.body.data,
          },
        } as FrontendResponse<GeographicalLocationNameInfo>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
