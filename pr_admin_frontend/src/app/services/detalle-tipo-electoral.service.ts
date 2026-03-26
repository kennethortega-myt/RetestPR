import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';
import { IDetalleTipoDocumentoElectoral } from '../interfaces/detalleTipoDocumentoElectoral.interface';
import { IGenericInterface } from '../interfaces/general.interface';

@Injectable({
  providedIn: 'root',
})
export class DetalleTipoElectoralService {
  readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  obtenerDetalleTipoElectoral(tipoEleccion: number, metodo: number) {
    let params = new HttpParams();
    if (metodo !== 0) {
      params = params.append('metodo', metodo);
    }
    return this.http.get<
      IGenericInterface<Array<IDetalleTipoDocumentoElectoral>>
    >(`${this.baseUrl}/detalleTipoEleccionDocumentoElectoral/${tipoEleccion}`, {
      params: params,
    });
  }

  guardar(data: Array<IDetalleTipoDocumentoElectoral>) {
    return this.http.post<IGenericInterface<any>>(
      `${this.baseUrl}/detalleTipoEleccionDocumentoElectoral`,
      data
    );
  }

  guardarArchivo(id: number, doc: File) {
    const form = new FormData();
    form.append('file', doc, doc.name);
    form.append('idDetalle', '' + id);
    return this.http.post<IGenericInterface<any>>(
      `${this.baseUrl}/detalleTipoEleccionDocumentoElectoral/archivo`,
      form
    );
  }

  listAllCatalogos() {
    return this.http.get<IGenericInterface<any>>(
      `${this.baseUrl}/detalleTipoEleccionDocumentoElectoral/catalogos`
    );
  }
}
