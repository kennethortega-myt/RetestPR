import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../environments/environment';
import { IGenericInterface } from '../interfaces/general.interface';

@Injectable({
  providedIn: 'root',
})
export class PadronProgresoApiService {
  private readonly baseUrl: string = `${environment.apiUrl}/padron-progreso`;

  constructor(private readonly httpClient: HttpClient) {}

  finalizo() {
    return this.httpClient.get<IGenericInterface<boolean>>(
      `${this.baseUrl}/finalizo`
    );
  }
}
