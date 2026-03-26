import { Injectable } from "@angular/core";
import { MesaApiService } from "./mesa-api.service";
import { Observable } from "rxjs";
import { Mesa } from "../../interfaces/acta-bean";
import { FrontendResponse } from "../../interfaces/response.common";

@Injectable({
  providedIn: "root",
})
export class MesaService {
  constructor(private mesaApiService: MesaApiService) {}
  obtenerTotales(
    ambitoGeografico: number,
    tipoFiltro: string,
    ubigeoNivel1: number,
    ubigeoNivel2: number,
    ubigeoNivel3: number,
    distritoElectoral: number
  ): Observable<FrontendResponse<Mesa>> {
    return this.mesaApiService.obtenerTotales(
      ambitoGeografico,
      tipoFiltro,
      ubigeoNivel1,
      ubigeoNivel2,
      ubigeoNivel3,
      distritoElectoral
    );
  }
}
