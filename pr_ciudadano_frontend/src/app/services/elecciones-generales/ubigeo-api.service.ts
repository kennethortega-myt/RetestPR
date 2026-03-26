import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map, throwError } from "rxjs";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { transformBodyToParams } from "../../helpers/transformBodyParams";
import { LocalVotacionResponse } from "../../interfaces/acta-bean";
import { FrontendResponse } from "../../interfaces/response.common";
import {
  UbigeoBean,
  RegionResponse,
  DistritoElectoral,
  DistritoElectoralResponse,
  Region,
  LocalVotacion,
} from "../../interfaces/ubigeo-bean";
import { catchErrorHandler$ } from "../common/catchErrorHandler";

@Injectable({
  providedIn: "root",
})
export class UbigeoApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();
  private pointBase = "ubigeos";

  constructor(private httpClient: HttpClient) {
    this.urlServidor = environment.apiUrlLocal;
  }

  listarDepartamentos(idEleccion: number, idAmbito: number): Observable<UbigeoBean> {
    let headers: HttpHeaders = new HttpHeaders();
    let request = {
      idEleccion: idEleccion,
      idAmbitoGeografico: idAmbito,
    };
    let httpParams = transformBodyToParams(request);
    return this.httpClient
      .get<UbigeoBean>(this.urlServidor + this.pointBase + `/departamentos`, { headers: headers, params: httpParams })
      .pipe(
        catchError((error) => {
          return throwError(() => "api departamentos no funciona");
        })
      );
  }

  listarProvincias(idEleccion: number, idAmbito: number, idUbigeoDepartamento: string): Observable<UbigeoBean> {
    let headers: HttpHeaders = new HttpHeaders();
    let request = {
      idEleccion: idEleccion,
      idAmbitoGeografico: idAmbito,
      idUbigeoDepartamento: idUbigeoDepartamento,
    };
    let httpParams = transformBodyToParams(request);
    return this.httpClient
      .get<UbigeoBean>(this.urlServidor + this.pointBase + `/provincias`, { headers: headers, params: httpParams })
      .pipe(
        catchError((error) => {
          return throwError(() => "api departamentos no funciona");
        })
      );
  }

  listarDistritos(idEleccion: number, idAmbito: number, provincia: string): Observable<UbigeoBean> {
    let headers: HttpHeaders = new HttpHeaders();
    let request = {
      idEleccion: idEleccion,
      idAmbitoGeografico: idAmbito,
      idUbigeoProvincia: provincia,
    };
    let httpParams = transformBodyToParams(request);
    return this.httpClient
      .get<UbigeoBean>(this.urlServidor + this.pointBase + `/distritos`, { headers: headers, params: httpParams })
      .pipe(
        catchError((error) => {
          return throwError(() => "api departamentos no funciona");
        })
      );
  }

  listarLocales(idEleccion: number, idUbigeo: number): Observable<FrontendResponse<[LocalVotacion]>> {
    let request = {
      idUbigeo: idUbigeo,
    };
    let httpParams = transformBodyToParams(request);
    return this.httpClient.get<LocalVotacionResponse>(this.urlServidor + `ubigeos/locales`, { params: request }).pipe(
      map((response) => {
        return {
          success: response.success,
          data: response.data,
        } as FrontendResponse<[LocalVotacion]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarRegiones(): Observable<FrontendResponse<Region[]>> {
    return this.httpClient.get<RegionResponse>(this.urlServidor + `ubigeos/regiones`).pipe(
      map((response) => {
        const { success, data } = response;
        return {
          success,
          data,
        } as FrontendResponse<Region[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  listarDistritoElectoral(): Observable<FrontendResponse<[DistritoElectoral]>> {
    let request = null;

    return this.httpClient.get<DistritoElectoralResponse>(this.urlServidor + `distrito-electoral/distritos`).pipe(
      map((response) => {
        return {
          success: response.success,
          data: response.data,
        } as FrontendResponse<[DistritoElectoral]>;
      }),
      catchError(catchErrorHandler$)
    );
  }
}
