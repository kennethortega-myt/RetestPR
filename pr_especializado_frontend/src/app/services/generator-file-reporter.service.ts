import { Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';
import saveAs from 'file-saver';

import { environment } from '../../environments/environment';
import { RequestsService } from './common/request.service';
import { catchErrorHandler$ } from './common/request-error-handler';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { getCurrentDateTime } from '../helpers/datetime-helper.common';

// INTERFACES

export interface IGenerateReportParams {
  idEleccion: number;
  idAmbitoGeografico?: number;
  tipoFiltro: string;
  ubigeoNivel01?: string; // Departamento
  ubigeoNivel02?: string; // Provincia
  idUbigeo?: string; // Distrito
  descripcionUbigeoNivel1?: string;
  descripcionUbigeoNivel2?: string;
  descripcionUbigeoNivel3?: string;
  codigoLocalVotacion?: number;
  codigoUsuario?: string;
  tipoReporte?: number;
}

export interface IFileResponse {
  file?: Blob;
  success: boolean;
}

export interface ICreateReportFileData {}

export interface ICreateReportFileResponse extends GenericResponse {
  data: ICreateReportFileData;
}

export type DocumentType = 'pdf' | 'excel';
export const DOCUMENTS_TYPES: { [key in DocumentType]: string } = {
  pdf: 'application/pdf',
  excel: 'application/vnd.ms-excel',
};
export const DOCUMENTS_EXTENSION: { [key in DocumentType]: string } = {
  pdf: 'pdf',
  excel: 'xlsx',
};

// SERVICE

@Injectable({
  providedIn: 'root',
})
export class GeneratorFileReporterService {
  private readonly baseUrl = environment.apiUrl;
  private readonly urls = {
    generateReport: this.baseUrl + '/reportes/generar',
    generateReportObservadas: this.baseUrl + '/reportes/generarObservadas',
  };

  constructor(private readonly request: RequestsService) {}

  public getReporterFile$(
    params: IGenerateReportParams,
    documentType?: DocumentType
  ): Observable<IFileResponse> {
    const mediaType = DOCUMENTS_TYPES[documentType ?? 'pdf'];
    const extension = DOCUMENTS_EXTENSION[documentType ?? 'pdf'];
    return this.request.postBlob(this.urls.generateReport, params).pipe(
      map((newResponse) => {
        const { body } = newResponse;
        if (body) {
          let blob = new Blob([body], { type: mediaType });
          saveAs(blob, `${'reporte_'}${getCurrentDateTime()}.${extension}`);
          return { success: true };
        }
        return { success: false };
      }),
      catchError(catchErrorHandler$)
    );
  }

  public createReporterFile$(
    params: IGenerateReportParams
  ): Observable<FrontendResponse<ICreateReportFileData>> {
    return this.request
      .post<ICreateReportFileResponse>(this.urls.generateReport, params)
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<ICreateReportFileData>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public createReporterObservadasFile$(
    params: IGenerateReportParams
  ): Observable<FrontendResponse<ICreateReportFileData>> {
    return this.request
      .post<ICreateReportFileResponse>(
        this.urls.generateReportObservadas,
        params
      )
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<ICreateReportFileData>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
