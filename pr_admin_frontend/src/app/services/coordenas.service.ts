import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  IRelativeCoordinatesInterface,
  IareaCoordinates,
} from '../interfaces/IRelativeCoordinates.interface';

@Injectable({
  providedIn: 'root',
})
export class CoordenasService {
  apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  verificarImagen(
    data: IRelativeCoordinatesInterface
  ): Observable<Array<IareaCoordinates>> {
    const headers = {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
    };
    return this.http.post<Array<IareaCoordinates>>(
      `${this.apiUrl}/externalApi/relative-coordena`,
      data,
      { headers }
    );
  }
}
