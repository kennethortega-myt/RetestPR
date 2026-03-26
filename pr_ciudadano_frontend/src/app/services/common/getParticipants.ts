import { Observable, map, catchError } from "rxjs";
import { catchErrorHandler$ } from "./catchErrorHandler";
import { ParlGeographicalLocationNameParams } from "../../interfaces/parlamento.interfaces";
import {
  GeographicalLocationParams,
  GeographicalLocationItem,
  GeographicalLocationResponse,
  PoliticalOrganizationParams,
  PoliticalOrganizationItem,
  PoliticalOrganizationResponse,
  GeographicalLocationNameInfo,
  GeographicalLocationNameResponse,
  GeographicalLocationNameItem,
  ResultOfParticipantsParams,
  ResultOfParticipantsItem,
  ResultOfParticipantsResponse,
} from "../../interfaces/presidenciales.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";
import { RequestsService } from "./requests.service";

export function getParticipantsByCandidates$(
  request: RequestsService,
  apiUrl: string,
  params: GeographicalLocationParams
): Observable<FrontendResponse<GeographicalLocationItem[]>> {
  return request.post<GeographicalLocationResponse>(apiUrl, params).pipe(
    map((response) => {
      return {
        success: true,
        data: response.body.data,
      } as FrontendResponse<GeographicalLocationItem[]>;
    }),
    catchError(catchErrorHandler$)
  );
}

export function getParticipantsByGeographicalLocation$(
  request: RequestsService,
  apiUrl: string,
  params: GeographicalLocationParams
): Observable<FrontendResponse<GeographicalLocationItem[]>> {
  return getParticipantsByCandidates$(request, apiUrl, params);
}

export function getParticipantsByPoliticalOrganization$(
  request: RequestsService,
  apiUrl: string,
  params: PoliticalOrganizationParams
): Observable<FrontendResponse<PoliticalOrganizationItem[]>> {
  return request.post<PoliticalOrganizationResponse>(apiUrl, params).pipe(
    map((response) => {
      return {
        success: true,
        data: response.body.data,
      } as FrontendResponse<PoliticalOrganizationItem[]>;
    }),
    catchError(catchErrorHandler$)
  );
}

export function getParticipantsByGeographicalLocationName$(
  request: RequestsService,
  apiUrl: string,
  params: ParlGeographicalLocationNameParams
): Observable<FrontendResponse<GeographicalLocationNameInfo>> {
  return request.post<GeographicalLocationNameResponse>(apiUrl, params).pipe(
    map((response) => {
      const emptyVotesCode = "80";
      const nullVotesCode = "81";
      const list = response.body.data.filter(
        (el) => el.codigoAgrupacionPolitica != emptyVotesCode && el.codigoAgrupacionPolitica != nullVotesCode
      );
      const emptyVotes = response.body.data.find((el) => el.codigoAgrupacionPolitica == emptyVotesCode);
      const nullVotes = response.body.data.find((el) => el.codigoAgrupacionPolitica == nullVotesCode);
      const totals = {
        porcentajeVotosValidos: response.body.data.reduce((prev, curr) => prev + curr.porcentajeVotosValidos, 0),
        porcentajeVotosEmitidos: response.body.data.reduce((prev, curr) => prev + curr.porcentajeVotosEmitidos, 0),
        totalVotosValidos: response.body.data.reduce((prev, curr) => prev + curr.totalVotosValidos, 0),
      } as GeographicalLocationNameItem;
      return {
        success: true,
        data: {
          list,
          emptyVotes,
          nullVotes,
          totals,
          listForScales: response.body.data,
        },
      } as FrontendResponse<GeographicalLocationNameInfo>;
    }),
    catchError(catchErrorHandler$)
  );
}

export function getResultOfParticipants$(
  request: RequestsService,
  apiUrl: string,
  params: ResultOfParticipantsParams
): Observable<FrontendResponse<ResultOfParticipantsItem[]>> {
  return request.post<ResultOfParticipantsResponse>(apiUrl, params).pipe(
    map((response) => {
      return {
        success: true,
        totalVotosPorOP: response.body.totalVotosPorOP,
        porcentajeVotoEmitido: response.body.porcentajeVotoEmitido,
        porcentajeVotoValido: response.body.porcentajeVotoValido,
        data: response.body.data,
      } as FrontendResponse<ResultOfParticipantsItem[]>;
    }),
    catchError(catchErrorHandler$)
  );
}
