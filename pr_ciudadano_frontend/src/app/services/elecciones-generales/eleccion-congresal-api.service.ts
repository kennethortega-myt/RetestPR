import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map } from "rxjs";
import { environment } from "../../../environments/environment";
import {
  Participante,
  ParticipanteResponse,
  ParticipantePorCandidato,
  ParticipantePorCandidatoResponse,
  Agrupacion,
  AgrupacionResponse,
  Candidato,
  CandidatoResponse,
} from "../../interfaces/eleccion-congresal-bean";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";

@Injectable({
  providedIn: "root",
})
export class EleccionCongresalApiService {
  private readonly urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarParticipantes(
    nombreApellidoPartido: string,
    idEleccion: number,
    tipoFiltro: string,
    ubigeoNivel1: number
  ): Observable<FrontendResponse<[Participante]>> {
    const url = this.urlServidor + `eleccion-diputado/participantes-ubicacion-geografica`;
    return this.listarParticipantesOrganizaciones(nombreApellidoPartido, idEleccion, tipoFiltro, ubigeoNivel1, url);
  }

  listarOrganizaciones(
    nombreApellidoPartido: string,
    idEleccion: number,
    tipoFiltro: string,
    ubigeoNivel1: number
  ): Observable<FrontendResponse<[Participante]>> {
    const url = this.urlServidor + `eleccion-diputado/participantes-ubicacion-geografica-nombre`;
    return this.listarParticipantesOrganizaciones(nombreApellidoPartido, idEleccion, tipoFiltro, ubigeoNivel1, url);
  }

  private listarParticipantesOrganizaciones(
    nombreApellidoPartido: string,
    idEleccion: number,
    tipoFiltro: string,
    ubigeoNivel1: number,
    url: string
  ): Observable<FrontendResponse<[Participante]>> {
    let request = {
      nombreApellidoPartido: nombreApellidoPartido,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      idDistritoElectoral: ubigeoNivel1,
    };
    return this.request.post<ParticipanteResponse>(url, request).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[Participante]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarParticipantesPorCandidato(
    idEleccion: number,
    nombreApellidoPartido: string,
    tipoFiltro: string,
    ubigeoNivel1: number,
    pagina: number,
    tamanio: number
  ): Observable<FrontendResponse<ParticipantePorCandidato[]>> {
    let request = {
      nombreApellidoPartido: nombreApellidoPartido,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      idDistritoElectoral: ubigeoNivel1,
    };
    return this.request
      .post<ParticipantePorCandidatoResponse>(
        this.urlServidor + `eleccion-diputado/participantes-por-candidato?pagina=${pagina}&tamanio=${tamanio}`,
        request
      )
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<ParticipantePorCandidato[]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarOrganizacionesPorBusqueda(
    idDistritoElectoral: number,
    idEleccion: number,
    nombreApellidoPartido: string,
    tipoFiltro: string
  ): Observable<FrontendResponse<[Agrupacion]>> {
    let request = {
      idDistritoElectoral: idDistritoElectoral,
      idEleccion: idEleccion,
      nombreApellidoPartido: nombreApellidoPartido,
      tipoFiltro: tipoFiltro,
    };
    return this.request
      .post<AgrupacionResponse>(this.urlServidor + `eleccion-diputado/organizacion-politica`, request)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<[Agrupacion]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarCandidatosPorAgrupacionPolitica(
    idAgrupacionPolitica: number,
    idDistritoElectoral: number,
    idEleccion: number,
    nombreApellido: string,
    tipoFiltro: string
  ): Observable<FrontendResponse<[Candidato]>> {
    let request = {
      idAgrupacionPolitica: idAgrupacionPolitica,
      idDistritoElectoral: idDistritoElectoral,
      idEleccion: idEleccion,
      nombreApellido: nombreApellido,
      tipoFiltro: tipoFiltro,
    };
    return this.request
      .post<CandidatoResponse>(this.urlServidor + `eleccion-diputado/participantes-por-candidato-nombre`, request)
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            totalVotosPorOP: response.body.totalVotosPorOP,
            porcentajeVotoEmitido: response.body.porcentajeVotoEmitido,
            porcentajeVotoValido: response.body.porcentajeVotoValido,
            data: response.body.data,
          } as FrontendResponse<[Candidato]>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
