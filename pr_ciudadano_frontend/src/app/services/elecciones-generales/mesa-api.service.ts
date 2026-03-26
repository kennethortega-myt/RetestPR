import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map } from "rxjs";
import { environment } from "../../../environments/environment";
import { Mesa, MesaResponse } from "../../interfaces/acta-bean";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";

@Injectable({
  providedIn: "root",
})
export class MesaApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly request: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  obtenerTotales(
    ambitoGeografico: number,
    tipoFiltro: string,
    ubigeoNivel1: number,
    ubigeoNivel2: number,
    ubigeoNivel3: number,
    distritoElectoral: number
  ): Observable<FrontendResponse<Mesa>> {
    let data = {
      ambitoGeografico: ambitoGeografico,
      distritoElectoral: distritoElectoral,
      tipoFiltro: tipoFiltro,
      ubigeoNivel1: ubigeoNivel1,
      ubigeoNivel2: ubigeoNivel2,
      ubigeoNivel3: ubigeoNivel3,
    };
    return this.request.post<MesaResponse>(this.urlServidor + `mesa/totales`, data).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Mesa>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
