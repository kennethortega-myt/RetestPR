import { Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';

import { RequestsService } from './common/request.service';
import { environment } from '../../environments/environment';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { catchErrorHandler$ } from './common/request-error-handler';

export interface IPoliticGroupDescriptionParams {
  codigoOP: string | number;
}

export interface IPoliticGroupDescriptionData {
  id: number;
  descripcion: string;
}

export interface IPoliticGroupDescriptionResponse extends GenericResponse {
  data: IPoliticGroupDescriptionData;
}

@Injectable({
  providedIn: 'root',
})
export class PotiticGroupsService {
  private readonly baseUrl = environment.apiUrl;
  private readonly urls = {
    agrupacion_politica: this.baseUrl + '/agrupacion-politica/codigo',
  };

  constructor(private readonly request: RequestsService) {}

  public getPoliticGroupDescription$(
    params?: IPoliticGroupDescriptionParams
  ): Observable<FrontendResponse<IPoliticGroupDescriptionData>> {
    return this.request
      .post<IPoliticGroupDescriptionResponse>(this.urls.agrupacion_politica, {})
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IPoliticGroupDescriptionData>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
