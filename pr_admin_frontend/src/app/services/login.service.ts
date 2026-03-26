import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';
import { IGenericInterface } from '../interfaces/general.interface';
import { ILoginRequest, ILoginResponse } from '../interfaces/login.interface';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  login(data: ILoginRequest) {
    return this.http.post<IGenericInterface<ILoginResponse>>(
      `${this.baseUrl}/login`,
      data
    );
  }
}
