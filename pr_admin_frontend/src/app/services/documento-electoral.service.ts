import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';
import {
  IDatosGeneralRequest,
  IGenericInterface,
  IDatosGeneralResponse,
} from '../interfaces/general.interface';

@Injectable({
  providedIn: 'root',
})
export class DocumentoElectoralService {
  readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  save(data: IDatosGeneralRequest) {
    return this.http.post<IGenericInterface<IDatosGeneralResponse>>(
      `${this.baseUrl}/documento`,
      data
    );
  }

  delete(id: number) {
    return this.http.delete<IGenericInterface<IDatosGeneralResponse>>(
      `${this.baseUrl}/documento/${id}`
    );
  }

  listAllCatalogos() {
    return this.http.get<IGenericInterface<any>>(
      `${this.baseUrl}/documento/catalogos`
    );
  }
}
