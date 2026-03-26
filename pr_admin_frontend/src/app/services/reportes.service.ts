import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ReportesServiceApi } from './reportes-service-api.service';
import { GenericResponseBean } from '../interfaces/genericResponseBean';

@Injectable({
  providedIn: 'root',
})
export class ReportesService {
  constructor(private readonly reportesServiceApi: ReportesServiceApi) {}

  validarServicioDB(): Observable<GenericResponseBean<any>> {
    return this.reportesServiceApi.validarServicioDB();
  }

  validarServicioFirma(): Observable<GenericResponseBean<any>> {
    return this.reportesServiceApi.validarServicioFirma();
  }

  validarServicioRabbitmq(): Observable<GenericResponseBean<any>> {
    return this.reportesServiceApi.validarServicioRabbitmq();
  }
}
