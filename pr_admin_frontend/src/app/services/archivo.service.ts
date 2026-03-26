import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ArchivoApiService } from './archivo-api.service';
import { GenericResponseBean } from '../interfaces/genericResponseBean';

@Injectable({
  providedIn: 'root',
})
export class ArchivoService {
  constructor(private readonly archivoApiService: ArchivoApiService) {}

  validarServicioFileserver(): Observable<GenericResponseBean<any>> {
    return this.archivoApiService.validarServicioFileserver();
  }
}
