import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map } from "rxjs";
import { environment } from "../../../environments/environment";
import { FrontendResponse } from "../../interfaces/response.common";
import { Resumen, ResumenResponse } from "../../interfaces/resumen-bean";
import {
  ResumenGeneral,
  ResumenGeneralResponse,
  MapaCalor,
  MapaCalorResponse,
  AgrupacionPolitica,
  AgrupacionPoliticaResponse,
} from "../../interfaces/resumen-general-bean";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";

@Injectable({
  providedIn: "root",
})
export class ResumenGeneralApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarElecciones(
    activo: number,
    idProceso: number,
    idNivel01: number,
    idNivel02: number,
    idDistrito: number,
    tipoFiltro: string,
    idAmbitoGeografico: number
  ): Observable<FrontendResponse<[ResumenGeneral]>> {
    let data = {
      activo: activo,
      idProceso: idProceso,
      ubigeoNivel01: idNivel01,
      ubigeoNivel02: idNivel02,
      ubigeoNivel03: idDistrito,
      tipoFiltro: tipoFiltro,
      idAmbitoGeografico: idAmbitoGeografico,
    };
    return this.request.post<ResumenGeneralResponse>(this.urlServidor + `resumen-general/elecciones`, data).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[ResumenGeneral]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  obtenerResumenGeneral(
    idAmbitoGeografico: number,
    idEleccion: number,
    tipoFiltro: string,
    idDistritoElectoral?: number,
    idUbigeoDepartamento?: number,
    idUbigeoProvincia?: number,
    idUbigeoDistrito?: number,
  ): Observable<FrontendResponse<Resumen>> {
    let data = {
      idAmbitoGeografico: idAmbitoGeografico,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      idDistritoElectoral: idDistritoElectoral,
      idUbigeoDepartamento: idUbigeoDepartamento,
      idUbigeoProvincia: idUbigeoProvincia,
      idUbigeoDistrito: idUbigeoDistrito,
    };
    return this.request.post<ResumenResponse>(this.urlServidor + `resumen-general/totales`, data).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Resumen>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarMapaCalor(
    codigoAgrupacionPolitica: string,
    idAmbitoGeografico: number,
    idEleccion: number,
    tipoFiltro: string = null,
    ubigeoNivel01?: number,
    ubigeoNivel02?: number,
    ubigeoNivel03?: number
  ): Observable<FrontendResponse<[MapaCalor]>> {
    let request = {
      codigoAgrupacionPolitica: codigoAgrupacionPolitica,
      idAmbitoGeografico: idAmbitoGeografico,
      idEleccion: idEleccion,
      ubigeoNivel01: ubigeoNivel01,
      ubigeoNivel02: ubigeoNivel02,
      ubigeoNivel03: ubigeoNivel03,
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
  listarParticipantes(
    idAmbitoGeografico: number = 1,
    idEleccion?: number,
    tipoFiltro: string = null,
    idUbigeoDepartamento?: number,
    idUbigeoProvincia?: number,
    idUbigeoDistrito?: number
  ): Observable<FrontendResponse<[AgrupacionPolitica]>> {
    let request = {
      idAmbitoGeografico: idAmbitoGeografico,
      idEleccion: idEleccion,
      idUbigeoDepartamento: idUbigeoDepartamento,
      idUbigeoProvincia: idUbigeoProvincia,
      idUbigeoDistrito: idUbigeoDistrito,
      tipoFiltro: tipoFiltro,
    };

    return this.request
      .post<AgrupacionPoliticaResponse>(this.urlServidor + `resumen-general/participantes`, request)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<[AgrupacionPolitica]>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
