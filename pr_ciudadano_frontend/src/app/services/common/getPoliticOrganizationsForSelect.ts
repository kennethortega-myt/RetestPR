import { Observable, map, catchError } from "rxjs";
import { RequestsService } from "./requests.service";
import { catchErrorHandler$ } from "./catchErrorHandler";
import {
  PoliticOrganizationsForSelectItem,
  PoliticOrganizationsForSelectResponse,
} from "../../interfaces/presidenciales.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";

export function getPoliticOrganizationsForSelect$(
  request: RequestsService,
  apiUrl: string
): Observable<FrontendResponse<PoliticOrganizationsForSelectItem[]>> {
  return request.get<PoliticOrganizationsForSelectResponse>(apiUrl).pipe(
    map((response) => {
      return {
        success: true,
        data: response.body.data,
      } as FrontendResponse<PoliticOrganizationsForSelectItem[]>;
    }),
    catchError(catchErrorHandler$)
  );
}
