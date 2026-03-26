import { Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';

import { RequestsService } from './common/request.service';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { environment } from '../../environments/environment';
import { catchErrorHandler$ } from './common/request-error-handler';

export interface IValidarProcesoResponse extends GenericResponse {
  data: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class MaeImportarService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly request: RequestsService) { }

  /**
   * Valida si el proceso de importación en 'mae_importar' fue exitoso.
   * Retorna true si todos los procesos fueron exitosos, false en caso contrario.
   */
  public validarProcesoImportar$(): Observable<FrontendResponse<boolean>> {
    const url = `${this.baseUrl}/fecha/validarprocesoimportar`;
    return this.request.get<IValidarProcesoResponse>(url).pipe(
      map((response) => {
        return {
          success: response.body!.success,
          data: !!response.body!.data,
        } as FrontendResponse<boolean>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}