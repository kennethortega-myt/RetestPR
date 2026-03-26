import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { IBase } from "../../interfaces/response/base-response.interface";
import {
  ActaResumenGeneralTotalRequest,
  ActaResumentGeneralResquest,
} from "../../interfaces/request/acta-resumen-general-request.interface";
import { ResumenGeneral } from "../../interfaces/resumen-general-bean";

import { HttpHeaders } from "@angular/common/http";
import { transformBodyToParams } from "../../helpers/transformBodyParams";
import { RequestsService } from "../common/requests.service";
import { ActaResumenGeneralTotalesResponse } from "../../interfaces/response/acta-resumen-general-response.interface";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class ActaResumenGeneralService {
  private readonly pathBase: string = environment.apiUrlLocal + "resumen-general";
  private readonly requestsService: RequestsService = inject(RequestsService);

  public obtenerResumenGeneral(request: ActaResumentGeneralResquest): Observable<IBase<ResumenGeneral>> {
    let headers: HttpHeaders = new HttpHeaders();
    let httpParams = transformBodyToParams(request as any);
    let urlEndpoint: string = `${this.pathBase}/elecciones`;

    return this.requestsService
      .getTake<IBase<ResumenGeneral>>(urlEndpoint, headers, httpParams)
      .pipe(map((x) => x.body));
  }
  public obtenerResumenGeneralRevocatoria(request: ActaResumentGeneralResquest): Observable<IBase<ResumenGeneral>> {
    let headers: HttpHeaders = new HttpHeaders();
    let httpParams = transformBodyToParams(request as any);
    let urlEndpoint: string = `${this.pathBase}/revocatorias`;

    return this.requestsService
      .getTake<IBase<ResumenGeneral>>(urlEndpoint, headers, httpParams)
      .pipe(map((x) => x.body));
  }

  public obtenerTotales(request: ActaResumenGeneralTotalRequest): Observable<IBase<ActaResumenGeneralTotalesResponse>> {
    let headers: HttpHeaders = new HttpHeaders();
    let httpParams = transformBodyToParams(request as any);
    let urlEndpoint: string = `${this.pathBase}/totales`;

    return this.requestsService
      .getTake<IBase<ActaResumenGeneralTotalesResponse>>(urlEndpoint, headers, httpParams)
      .pipe(map((x) => x.body));
  }
}
