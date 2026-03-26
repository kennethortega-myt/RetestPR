import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';
import { IGenericInterface } from '../interfaces/general.interface';
import { IMaeImportar } from '../interfaces/importar.interface';

@Injectable({
  providedIn: 'root',
})
export class MaeimportarApiService {
  private readonly baseUrl: string = `${environment.apiUrl}/maeimportar`;

  constructor(private readonly httpClient: HttpClient) {}

  listar() {
    return this.httpClient.post<IGenericInterface<IMaeImportar[]>>(
      `${this.baseUrl}`,
      null
    );
  }
}
