import { inject, Injectable } from "@angular/core";
import { RequestsService } from "../common/requests.service";
import { ActaPorAmbitoDetalleResponse } from "../../interfaces/response/acta-por-ambito-response.interface";
import { IBase } from "../../interfaces/response/base-response.interface";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class ActaService {
  private readonly pathBase: string = environment.apiUrlLocal + "actas";
  private readonly requestsService: RequestsService = inject(RequestsService);

  public obtenerActasRevocatoriaDetalle<T>(idActa: number): Observable<IBase<ActaPorAmbitoDetalleResponse>> {
    let urlEndpoint: string = `${this.pathBase}/${idActa}`;
    return this.requestsService.getTake<IBase<ActaPorAmbitoDetalleResponse>>(urlEndpoint).pipe(map((x) => x.body));
  }
}
