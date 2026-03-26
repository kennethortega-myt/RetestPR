import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, switchMap } from 'rxjs';

import { RequestsService } from './common/request.service';
import { catchErrorHandler$ } from './common/request-error-handler';
import { FrontendResponse, GenericResponse } from './common/response.common';
import { environment } from '../../environments/environment';
import { ActaObservadaInput } from '../interfaces/input/acta/acta-observada-input';
import { ActaObservada } from '../interfaces/output/acta-observada/acta-observada.model';
import { DetalleActaObservada } from '../interfaces/output/acta-observada/detalle-acta-observada.model';
import { Archivo, BlobConUrl } from '../components/modal-visor-pdf/modal-visor-pdf.component';
import { CandidatoEscrutinio } from '../components/tabla-escrutinio-modelo-dos/tabla-escrutinio-modelo-dos.model';

// INTERFACES

export interface IActasParams {
  codigoLocalVotacion?: number;
  idAmbitoGeografico?: number;
  idEleccion?: number;
  idUbigeo?: number;
  codigoEstadoActa?: string | null;
  ubigeoNivel01?: number;
  ubigeoNivel02?: number;
}

export interface IActaItem {
  id: number;
  idEleccion: number;
  descripcionEleccion: string;
  codigoMesa: string;
  descripcionMesa: string;
  idAmbitoGeografico: number;
  descripcionAmbitoGeografico: string;
  ubigeoNivel01: string;
  ubigeoNivel02: string;
  ubigeoNivel03: string;
  nombreLocalVotacion: string;
  idMesa: number;
  totalElectoresHabiles: number;
  totalVotosEmitidos: number;
  totalVotosValidos: number;
  estadoActa: string;
  estadoComputo: string;
  codigoEstadoActa: string;
  descripcionEstadoActa: string;
  archivos: Archivo[];
  archivosActa?: Archivo[];
  archivosResolucion?: Archivo[];
}

export interface IActasResponseData {
  paginaActual: number;
  totalRegistros: number;
  totalPaginas: number;
  content: IActaItem[];
}

export interface IActasResponse extends GenericResponse {
  data: IActasResponseData;
}

export type ActasQueryParamsType = 'pagina' | 'tamanio';

// SERVICIO

@Injectable({
  providedIn: 'root',
})
export class ActaApiService {

  constructor(
    private readonly request: RequestsService,
    private readonly http: HttpClient
  ) { }

  private get urls() {
    return {
      actas: environment.apiUrl + environment.pathActa + '/',
    };
  }

  public getActas$(
    params: IActasParams,
    queryParams?: { [key in ActasQueryParamsType]: string | number }
  ): Observable<FrontendResponse<IActasResponseData>> {
    let actasRequest: Observable<HttpResponse<IActasResponse>>;
    if (queryParams) {
      actasRequest = this.request.post<IActasResponse>(
        this.urls.actas,
        params,
        undefined,
        queryParams
      );
    } else {
      actasRequest = this.request.post<IActasResponse>(this.urls.actas, params);
    }

    return actasRequest.pipe(
      map((response) => {
        return {
          success: response.body!.success,
          data: response.body!.data,
        } as FrontendResponse<IActasResponseData>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarActasObservadasPaginada(
    data: ActaObservadaInput,
    pagina: number,
    tamanio: number
  ): Observable<ActaObservada> {
    return this.http.post<ActaObservada>(
      this.urls.actas + `observadas?pagina=${pagina}&tamanio=${tamanio}`,
      data
    );
  }

  obtenerDetalleActa(id: any): Observable<DetalleActaObservada> {
    return this.http.get<DetalleActaObservada>(
      this.urls.actas + `${id}`
    );
  }

  descargarPdf(idActa: string): Observable<Blob> {
    return this.getPdf(this.urls.actas + `file?id=${idActa}`);
  }

  descargarImagen(idActa: string): Observable<Blob> {
    return this.getImage(this.urls.actas + `file?id=${idActa}`);
  }

  obtenerCandidatosPorIdAgrupacion(idActa: number, idAgrupacion: number) {
    return this.http.get<CandidatoEscrutinio>(
      this.urls.actas + `${idActa}/agrupacion/${idAgrupacion}/candidatos`
    );
  }

  private getPdf(url: string): Observable<Blob> {
    return this.http
      .get(url, {
        headers: {
          Accept: 'application/pdf',
        },
        responseType: 'blob',
      })
      .pipe(switchMap((response) => this.readFile(response)));
  }

  public getImage(url: string): Observable<Blob> {
    return this.http.get(url, { responseType: "blob" }).pipe(switchMap((response) => this.readFile(response)));
  }

  private readFile(blob: Blob): Observable<Blob> {
    return new Observable((observer) => {
      const fixedBlob = blob.type
        ? blob
        : new Blob([blob], { type: 'application/pdf' });

      const blobUrl = URL.createObjectURL(fixedBlob);

      const blobConUrl = fixedBlob as BlobConUrl;
      blobConUrl.url = blobUrl;

      observer.next(blobConUrl);
      observer.complete();
    });
  }

}
