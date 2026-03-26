import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EleccionesCandidatosRequest } from '../interfaces/eleccionesCandidatosRequest';

export interface EleccionResponseDto {
  success: boolean;
  message?: string;
}

@Injectable({ providedIn: 'root' })
export class EleccionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  eleccionGenerar(params: EleccionesCandidatosRequest): Observable<EleccionResponseDto> {
    return this.http.post<EleccionResponseDto>(
      `${this.baseUrl}/eleccion/generar`,
      params
    );
  }
}
