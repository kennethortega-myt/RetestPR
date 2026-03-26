import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Constantes } from '../helpers/constantes';
import { GenericResponseBean } from '../interfaces/genericResponseBean';

@Injectable({
  providedIn: 'root',
})
export class ArchivoApiService {
  private readonly urlServidor: string;

  constructor(private readonly httpClient: HttpClient) {
    this.urlServidor = `${environment.apiUrl}/`;
  }

  validarServicioFileserver(): Observable<GenericResponseBean<any>> {
    return this.httpClient.post<GenericResponseBean<any>>(
      `${this.urlServidor}${Constantes.CB_ARCHIVO_CONTROLLER_GET_VALIDA_SERVICIO_FILESERVER}`,
      null
    );
  }
}
