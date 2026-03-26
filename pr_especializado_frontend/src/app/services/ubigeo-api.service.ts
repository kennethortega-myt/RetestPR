import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../environments/environment';
import { LocalVotacionAdapter } from '../interfaces/adapter/local-votacion.adapter';
import { UbigeoAdapter } from '../interfaces/adapter/ubigeo.adapter';
import { UbigeoLocalVotacionInput } from '../interfaces/input/filtro-ubigeo/ubigeo-local-votacion-input';
import { UbigeoNivel01Input } from '../interfaces/input/filtro-ubigeo/ubigeo-nivel-01-input';
import { UbigeoNivel02Input } from '../interfaces/input/filtro-ubigeo/ubigeo-nivel-02-input';
import { UbigeoNivel03Input } from '../interfaces/input/filtro-ubigeo/ubigeo-nivel-03-input';
import { Base } from '../interfaces/output/base.model';
import { LocalVotacion } from '../interfaces/output/local-votacion.model';
import { Ubigeo } from '../interfaces/output/ubigeo.model';

@Injectable({
  providedIn: 'root',
})
export class UbigeoApiService {
  private readonly baseUrl = environment.apiUrl;
  private readonly path = environment.pathUbigeo;
  private readonly pathDistrito = environment.pathDistritoElectoral;

  constructor(
    private readonly http: HttpClient,
    private readonly ubigeoAdapter: UbigeoAdapter,
    private readonly localVotacionAdapter: LocalVotacionAdapter
  ) { }

  // 🔑 Método genérico para reducir repetición
  private mapResponse<T>(
    adapter: (item: any) => T
  ): (source: Base) => { data: T[] } & Base {
    return (value: Base) => ({
      ...value,
      data: value.data.map(adapter),
    });
  }

  listarNivel01(data: UbigeoNivel01Input): Observable<Ubigeo> {
    return this.http
      .post<Base>(`${this.baseUrl}${this.path}/departamentos`, data)
      .pipe(map(this.mapResponse(this.ubigeoAdapter.adapt.bind(this.ubigeoAdapter))));
  }

  listarNivel02(data: UbigeoNivel02Input): Observable<Ubigeo> {
    return this.http
      .post<Base>(`${this.baseUrl}${this.path}/provincias`, data)
      .pipe(map(this.mapResponse(this.ubigeoAdapter.adapt.bind(this.ubigeoAdapter))));
  }

  listarNivel03(data: UbigeoNivel03Input): Observable<Ubigeo> {
    return this.http
      .post<Base>(`${this.baseUrl}${this.path}/distritos`, data)
      .pipe(map(this.mapResponse(this.ubigeoAdapter.adapt.bind(this.ubigeoAdapter))));
  }

  listarLocalesVotaciones(
    data: UbigeoLocalVotacionInput
  ): Observable<LocalVotacion> {
    return this.http
      .post<Base>(`${this.baseUrl}${this.path}/locales`, data)
      .pipe(map(this.mapResponse(this.localVotacionAdapter.adapt.bind(this.localVotacionAdapter))));
  }

  listarDistritosElectorales(): Observable<Ubigeo> {
    return this.http
      .get<Base>(`${this.baseUrl}${this.pathDistrito}/distritos`)
      .pipe(map(this.mapResponse(this.ubigeoAdapter.adapt.bind(this.ubigeoAdapter))));
  }

  listarDepartamentos(): Observable<string[]> {
    return this.http.get<Base>(`${this.baseUrl}${this.path}/departamentos/all`)
      .pipe(map(response => response.data as string[]));
  }
}
