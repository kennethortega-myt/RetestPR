import { DatePipe } from "@angular/common";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from '@angular/core';
import { Observable, Subject, catchError, throwError } from "rxjs";
import { environment } from '../../../environments/environment';
import { IProcesoElectoralResponse } from '../../interfaces/proceso-electoral.interface';

@Injectable({
  providedIn: 'root'
})
export class ElectionsApiService {
  private readonly urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly httpClient: HttpClient, private readonly datePipe: DatePipe) {
    this.urlServidor = environment.apiUrlLocal;
  }

  obtenerEleccionAll(): Observable<any> {
    let headers: HttpHeaders = new HttpHeaders();
    return this.httpClient.get(this.urlServidor + `proceso/17/elecciones`,
      { headers: headers }
    )
      .pipe(
        catchError(() => {
          return throwError(() => 'api departamentos no funciona');
        })
      );
  }

  listarEleccionarPorIdProcesoElectoral(idProcesoElectoral: number): Observable<any> {
    let headers: HttpHeaders = new HttpHeaders();
    return this.httpClient.get(this.urlServidor + `proceso/${idProcesoElectoral}/elecciones`,
      { headers: headers }
    )
      .pipe(
        catchError(() => {
          return throwError(() => 'api departamentos no funciona');
        })
      );
  }
  obtenerProcesoElectoralActivo(): Observable<IProcesoElectoralResponse> {
    let headers: HttpHeaders = new HttpHeaders();
    return this.httpClient.get<IProcesoElectoralResponse>(this.urlServidor + `proceso/proceso-electoral-activo`,
      { headers: headers }
    )
      .pipe(
        catchError(() => {
          return throwError(() => 'api departamentos no funciona');
        })
      );
  }
}
