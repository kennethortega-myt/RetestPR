import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Constantes } from '../helpers/constantes';
import { GenericResponseBean } from '../interfaces/genericResponseBean';
import { AuthService } from './auth-service.service';

@Injectable({
  providedIn: 'root',
})
export class ReportesServiceApi {
  private readonly urlServidor: string;

  constructor(private readonly httpClient: HttpClient, public auth: AuthService) {
    this.urlServidor = environment.apiUrl;
  }

  validarServicioDB(): Observable<GenericResponseBean<any>> {
    return this.httpClient.post<GenericResponseBean<any>>(
      `${this.urlServidor}${Constantes.CB_MAEIMPORTAR_CONTROLLER_GET_VALIDA_SERVICIO_DB}`,
      null
    );
  }

  validarServicioFirma(): Observable<GenericResponseBean<any>> {
    return this.httpClient.post<GenericResponseBean<any>>(
      `${this.urlServidor}${Constantes.CB_REPORTES_CONTROLLER_GET_VALIDA_SERVICIO_FIRMA}`,
      null
    );
  }

  validarServicioRabbitmq(): Observable<GenericResponseBean<any>> {
    return this.httpClient.post<GenericResponseBean<any>>(
      `${this.urlServidor}${Constantes.CB_REPORTES_CONTROLLER_GET_VALIDA_SERVICIO_AMQP}`,
      null
    );
  }
}
