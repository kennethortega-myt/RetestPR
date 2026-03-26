import { Injectable } from "@angular/core";
import { Subject, Observable, map, catchError } from "rxjs";

import { environment } from "../../../environments/environment";
import {
  Eleccion,
  EleccionResponse,
  Acta,
  ActaResponse,
  Mesa,
  MesaActaResponse,
  MesaResponse,
  Candidato,
  CandidatoResponse,
} from "../../interfaces/acta-bean";
import { LocalVotacion } from "../../interfaces/elections.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";
import { MapaCalor, MapaCalorResponse } from "../../interfaces/resumen-general-bean";
import { LocalVotacionResponse } from "../../interfaces/ubigeo-bean";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";
import { IGetActasObserbadasParams } from "../../interfaces/acta-api.interface";

@Injectable({
  providedIn: "root",
})
export class ActaApiService {
  private readonly urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarElecciones(idEleccion: number): Observable<FrontendResponse<[Eleccion]>> {
    return this.request
      .post<EleccionResponse>(this.urlServidor + `actas/elecciones?procesoId=${idEleccion}`, null)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<[Eleccion]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarActas(
    codigoLocalVotacion: number,
    idAmbitoGeografico: number,
    idUbigeo: number,
    page: number,
    size: number
  ): Observable<FrontendResponse<Acta>> {
    let request = {
      codigoLocalVotacion: codigoLocalVotacion,
      idAmbitoGeografico: idAmbitoGeografico,
      idUbigeo: idUbigeo,
    };

    return this.request.post<ActaResponse>(this.urlServidor + `actas?pagina=${page}&tamanio=${size}`, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Acta>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  buscarMesa(codigoMesa: string): Observable<FrontendResponse<[Mesa]>> {
    let request = {
      codigoMesa: codigoMesa,
    };

    return this.request.post<MesaActaResponse>(this.urlServidor + `actas/buscar/mesa`, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[Mesa]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  buscarMesaPorId(idMesa: number): Observable<FrontendResponse<Mesa>> {
    return this.request.get<MesaResponse>(this.urlServidor + `actas/${idMesa}`).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Mesa>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarLocales(idEleccion: number, idUbigeo: number): Observable<FrontendResponse<[LocalVotacion]>> {
    let request = {
      idEleccion: idEleccion,
      idUbigeo: idUbigeo,
    };

    return this.request.post<LocalVotacionResponse>(this.urlServidor + `actas/locales`, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[LocalVotacion]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarMapaCalor(
    codigoAgrupacionPolitica: number,
    idAmbitoGeografico: number,
    idEleccion: number,
    idUbigeoDepartamento: number,
    idUbigeoProvincia: number,
    idUbigeoDistrito: number,
    tipoFiltro: string = "eleccion"
  ): Observable<FrontendResponse<[MapaCalor]>> {
    let request = {
      codigoAgrupacionPolitica: codigoAgrupacionPolitica,
      idAmbitoGeografico: idAmbitoGeografico,
      idEleccion: idEleccion,
      ubigeoNivel01: idUbigeoDepartamento,
      ubigeoNivel02: idUbigeoProvincia,
      ubigeoNivel03: idUbigeoDistrito,
      tipoFiltro: tipoFiltro,
    };

    return this.request.post<MapaCalorResponse>(this.urlServidor + `resumen-general/mapa-calor`, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[MapaCalor]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarActasObservadas(
    params: IGetActasObserbadasParams,
    page: number,
    size: number
  ): Observable<FrontendResponse<Acta>> {
    return this.request
      .post<ActaResponse>(this.urlServidor + `actas/observadas?pagina=${page}&tamanio=${size}`, params)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<Acta>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarMapaCalorObservadas(
    codigoAgrupacionPolitica: number,
    idAmbitoGeografico: number,
    idEleccion: number,
    idUbigeoDepartamento: number,
    idUbigeoProvincia: number,
    idUbigeoDistrito: number,
    tipoFiltro: string = "eleccion"
  ): Observable<FrontendResponse<[MapaCalor]>> {
    let request = {
      codigoAgrupacionPolitica: codigoAgrupacionPolitica,
      idAmbitoGeografico: idAmbitoGeografico,
      idEleccion: idEleccion,
      ubigeoNivel01: idUbigeoDepartamento,
      ubigeoNivel02: idUbigeoProvincia,
      ubigeoNivel03: idUbigeoDistrito,
      tipoFiltro: tipoFiltro,
      tipoActa: "H",
    };

    return this.request.post<MapaCalorResponse>(this.urlServidor + `resumen-general/mapa-calor`, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[MapaCalor]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarCandidatosPorAgrupacion(idActa: number, idAgrupacion: number): Observable<FrontendResponse<[Candidato]>> {
    return this.request
      .get<CandidatoResponse>(this.urlServidor + `actas/${idActa}/agrupacion/${idAgrupacion}/candidatos`)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<[Candidato]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  descargarImagen(idActa: string): Observable<Blob> {
    return this.request.getImage(this.urlServidor + `actas/file?id=${idActa}`);
  }

  descargarPdf(idActa: string): Observable<Blob> {
    return this.request.getPdf(this.urlServidor + `actas/file?id=${idActa}`);
  }
}
