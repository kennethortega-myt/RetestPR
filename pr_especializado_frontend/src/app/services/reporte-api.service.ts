import { Injectable } from '@angular/core';
import { catchError, EMPTY, map, Observable } from 'rxjs';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { environment } from '../../environments/environment';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { RequestsService } from './common/request.service';
import { catchErrorHandler$ } from './common/request-error-handler';
import { ReporteListarInput } from '../interfaces/input/reporte/reporte-listar-input';
import { reporterUrlToDownload } from '../helpers/constantes';
import { BlobConUrl } from '../components/modal-visor-pdf/modal-visor-pdf.component';

export type ReportDescriptionType = 'Terminado' | 'En Proceso';

export interface IReportListData {
  ambitoGeografico: string;
  estado: number;
  estadoDescripcion: ReportDescriptionType;
  fechaConsulta: number;
  porcentajeActasContabilizadas: string;
  idArchivo: string;
  localVotacion: string;
  tipoEleccion: string;
  tipoReporte: string;
  ubigeoNivel1: string;
  ubigeoNivel2: string;
  ubigeoNivel3: string;
}
export interface IReportListDataContent {
  content: IReportListData[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
}
export interface IReportListResponse extends GenericResponse {
  data: IReportListDataContent;
}

export interface IReporteFileDTO {
  archivo: any; // Base64
  nombreArchivo: string;
}

@Injectable({
  providedIn: 'root',
})
export class ReporteApiService {
  private readonly baseUrl = environment.apiUrl;
  private readonly path = environment.pathActa;
  private readonly urls = {
    reportList: this.baseUrl + '/reportes/listar',
    reportListAutomaticos: this.baseUrl + '/reportes/automatico',
    generarReporteCandidato: this.baseUrl + '/reportes/reporteCandidato'
  };

  constructor(private readonly http: HttpClient, private readonly request: RequestsService) {}

  listarReportes$(
    params: ReporteListarInput,
    pagina: number = 0,
    tamanio: number = 10
  ): Observable<FrontendResponse<IReportListDataContent>> {
    return this.request
      .post<IReportListResponse>(
        this.urls.reportList, params, undefined, { pagina, tamanio, }
      )
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IReportListDataContent>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarReportesAutomaticos$(
    params: ReporteListarInput,
    pagina: number = 0,
    tamanio: number = 10
  ): Observable<FrontendResponse<IReportListDataContent>> {
    return this.request
      .post<IReportListResponse>(
        this.urls.reportListAutomaticos, params, undefined, { pagina, tamanio }
      )
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IReportListDataContent>;
        }),
        catchError(catchErrorHandler$)
      );
  } 

  descargarZip(repositorio: string): Observable<{ url: string; fileName: string } | null> {
    const url = reporterUrlToDownload + repositorio;
    return this.descargarReporte(url, "application/zip", 'reporte.zip');
  } 

  descargarReporteCandidato(id: number): Observable<{ url: string; fileName: string } | null> {
    const url = `${this.urls.generarReporteCandidato}?id=${id}`;
    return this.descargarReporte(url, "text/csv", 'reporte.csv');
  }

  descargarReporte(url: string, tipoArchivo: string, defaultArchivo: string): 
    Observable<{ url: string; fileName: string } | null> {
    
    return this.http.get(url, {
      headers: { Accept: tipoArchivo },
      responseType: "blob",
      observe: "response"
    }).pipe(
      map(response => {
        if (!response.body || response.body.size === 0) {
          return null; 
        }        
        const fileName = response.headers.get('Content-Disposition')
          ?.match(/filename="(.+)"/)?.[1] || defaultArchivo;
        
        const blob = new Blob([response.body!], { type: tipoArchivo });
        
        return {
          url: URL.createObjectURL(blob),
          fileName
        };
      })
    );
  }

}
