import { Injectable } from "@angular/core";
import { PadronApiService } from "./padron-api.service";
import { Observable } from "rxjs";

import { Padron } from "../../interfaces/padron-bean";
import { FrontendResponse } from "../../interfaces/response.common";

@Injectable({
  providedIn: "root",
})
export class PadronService {
  constructor(private padronApiService: PadronApiService) {}
  buscarPorDni(dni: string): Observable<FrontendResponse<Padron>> {
    return this.padronApiService.buscarPorDni(dni);
  }
}
