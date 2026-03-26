import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { FechaResponse } from "../interfaces/fecha";

@Injectable({
  providedIn: "root",
})
export class FechaApiService {
  private readonly baseUrl = `${environment.apiUrl}/fecha`;
  private readonly http: HttpClient = inject(HttpClient);

  listarFecha(): Observable<FechaResponse> {
    return this.http.get<FechaResponse>(`${this.baseUrl}/listarFecha`, {});
  }

}