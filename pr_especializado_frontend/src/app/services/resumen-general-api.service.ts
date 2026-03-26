import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, firstValueFrom, map, Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { obtenerDatos } from '../helpers/funciones';
import { IConfigEleccionesItem } from '../interfaces/configurar-reportes.interfaces';
import { ListarEleccionesResumenGeneralInput } from '../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { ObtenerMapaCalorResumenGeneralInput } from '../interfaces/input/resumen-general/obtener-mapa-calor-resumen-general-input';
import { ObtenerTotalesResumenGeneralInput } from '../interfaces/input/resumen-general/obtener-totales-resumen-general-input';
import { ObtenerTotalesResumenGeneralObservadasInput } from '../interfaces/input/resumen-general/obtener-totales-resumen-general-observadas-input';
import { Base } from '../interfaces/output/base.model';
import { MapaCalor } from '../interfaces/output/mapa-calor.model';
import { ResumenTotal } from '../interfaces/output/resumen-total.model';
import { TipoEleccion } from '../interfaces/output/tipo-eleccion.model';
import { catchErrorHandler$ } from './common/request-error-handler';
import { RequestsService } from './common/request.service';
import { GenericResponse, FrontendResponse } from './common/response.common';
import { getEncryptStorageEleccionValue, setEncryptStorageEleccionValue } from '../helpers/encrypt-storage-eleccion';

export interface IProcesoElectoralActivoData {
  acronimo: string;
  fechaConvocatoria: number;
  id: number;
  nombre: string;
  activoFechaProceso: boolean;
}

export interface IProcesoElectoralActivoResponse extends GenericResponse {
  data: IProcesoElectoralActivoData;
}

export interface EleccionItem {
  value: number;
  text: string;
}


@Injectable({
  providedIn: 'root',
})
export class ResumenGeneralApiService {
  private readonly baseUrl = environment.apiUrl;
  private readonly path = environment.pathResumenGeneral;
  private readonly pathActa = environment.pathActa;

  constructor(private readonly http: HttpClient, private readonly request: RequestsService) {}

  listarEleccionesGenerico(
    endpoint: string,
    data: ListarEleccionesResumenGeneralInput,
    ordenar: boolean = true
  ): Observable<FrontendResponse<IConfigEleccionesItem[]>> {
    return this.request
      .get<Base>(`${this.baseUrl}${endpoint}/${data.idProceso}`)
      .pipe(
        map((response) => {
          let elecciones = response.body?.data!.map((e: any) => {
            return { value: Number(e.codigo), text: e.nombre.toUpperCase() };
          });

          if (ordenar) {
            const ordenDeseado = [10, 14, 15, 13, 12];
            elecciones = elecciones.sort(
              (a: EleccionItem, b: EleccionItem) =>
                ordenDeseado.indexOf(a.value) - ordenDeseado.indexOf(b.value)
            );
          }

          return {
            success: response.body?.success,
            data: elecciones,
          } as FrontendResponse<IConfigEleccionesItem[]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  listarElecciones(
    data: ListarEleccionesResumenGeneralInput
  ): Observable<TipoEleccion> {
    return this.listarEleccionesGenerico('/mae-eleccion', data);
  }


  listarEleccionesParaConfigurarReporte(
    data: ListarEleccionesResumenGeneralInput
  ): Observable<TipoEleccion> {
    return this.listarEleccionesGenerico('/mae-eleccion/for-create-report', data);
  }

  listarEleccionesParaConfigurarReporteActas(
    data: ListarEleccionesResumenGeneralInput
  ): Observable<TipoEleccion> {
    return this.listarEleccionesGenerico('/mae-eleccion/for-create-report-actas', data);
  }

  async getIdEleccionPrincipal(): Promise<number | null> {
    const stored = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');
    if (stored) {
      return JSON.parse(stored);
    }

    try {
      const response = await firstValueFrom(this.getProcesoElectoralActivo$());
      const id = response?.data?.id;
      if (id) {
        setEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL', id);
        return Number(id);
      }
    } catch (e) {
      console.error('Error al obtener ID de elección principal:', e);
    }

    return null;
  }

  public getProcesoElectoralActivo$(): Observable<
    FrontendResponse<IProcesoElectoralActivoData>
  > {
    return this.request
      .get<IProcesoElectoralActivoResponse>(
        this.baseUrl + '/proceso/proceso-electoral-activo'
      )
      .pipe(
        map((response) => {
          return {
            success: response.body!.success,
            data: response.body!.data,
          } as FrontendResponse<IProcesoElectoralActivoData>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  obtenerResumenTotales(
    data: ObtenerTotalesResumenGeneralInput
  ): Observable<ResumenTotal> {
    return this.http
      .post<Base>(this.baseUrl + this.path + '/totales', data)
      .pipe(
        map((value: Base) => {
          return obtenerDatos(value)!;
        })
      );
  }

  obtenerResumenTotalesObservadas(
    data: ObtenerTotalesResumenGeneralObservadasInput
  ): Observable<ResumenTotal> {
    return this.http
      .post<Base>(this.baseUrl + this.pathActa + '/resumen-observadas', data)
      .pipe(
        map((value: Base) => {
          return obtenerDatos(value)!;
        })
      );
  }

  obtenerMapaCalor(
    data: ObtenerMapaCalorResumenGeneralInput
  ): Observable<MapaCalor> {
    return this.http
      .post<Base>(this.baseUrl + this.path + '/mapa-calor', data)
      .pipe(
        map((value: Base) => {
          return obtenerDatos(value)!;
        })
      );
  }

  obtenerMapaCalorObservada(
    data: ObtenerMapaCalorResumenGeneralInput
  ): Observable<MapaCalor> {
    return this.http
      .post<Base>(this.baseUrl + this.path + '/mapa-calor-observadas', data)
      .pipe(
        map((value: Base) => {
          return obtenerDatos(value)!;
        })
      );
  }
}
