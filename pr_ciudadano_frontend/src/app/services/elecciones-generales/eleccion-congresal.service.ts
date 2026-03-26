import { Injectable } from "@angular/core";
import { EleccionCongresalApiService } from "./eleccion-congresal-api.service";
import { Observable } from "rxjs";
import {
  Participante,
  ParticipantePorCandidato,
  Agrupacion,
  Candidato,
} from "../../interfaces/eleccion-congresal-bean";
import { FrontendResponse } from "../../interfaces/response.common";

@Injectable({
  providedIn: "root",
})
export class EleccionCongresalService {
  constructor(private readonly eleccionCongresalApiService: EleccionCongresalApiService) {}

  listarParticipantes(
    nombreApellidoPartido: string,
    idEleccion: number,
    tipoFiltro: string,
    ubigeoNivel1: number
  ): Observable<FrontendResponse<[Participante]>> {
    return this.eleccionCongresalApiService.listarParticipantes(
      nombreApellidoPartido,
      idEleccion,
      tipoFiltro,
      ubigeoNivel1
    );
  }

  listarOrganizaciones(
    nombreApellidoPartido: string,
    idEleccion: number,
    tipoFiltro: string,
    ubigeoNivel1: number
  ): Observable<FrontendResponse<[Participante]>> {
    return this.eleccionCongresalApiService.listarOrganizaciones(
      nombreApellidoPartido,
      idEleccion,
      tipoFiltro,
      ubigeoNivel1
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
    return this.eleccionCongresalApiService.listarParticipantesPorCandidato(
      idEleccion,
      nombreApellidoPartido,
      tipoFiltro,
      ubigeoNivel1,
      pagina,
      tamanio
    );
  }

  listarOrganizacionesPorBusqueda(
    idDistritoElectoral: number,
    idEleccion: number,
    nombreApellidoPartido: string,
    tipoFiltro: string
  ): Observable<FrontendResponse<[Agrupacion]>> {
    return this.eleccionCongresalApiService.listarOrganizacionesPorBusqueda(
      idDistritoElectoral,
      idEleccion,
      nombreApellidoPartido,
      tipoFiltro
    );
  }
  listarCandidatosPorAgrupacionPolitica(
    idAgrupacionPolitica: number,
    idDistritoElectoral: number,
    idEleccion: number,
    nombreApellido: string,
    tipoFiltro: string
  ): Observable<FrontendResponse<[Candidato]>> {
    return this.eleccionCongresalApiService.listarCandidatosPorAgrupacionPolitica(
      idAgrupacionPolitica,
      idDistritoElectoral,
      idEleccion,
      nombreApellido,
      tipoFiltro
    );
  }
}
