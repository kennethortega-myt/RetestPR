import { inject, Injectable } from '@angular/core';
import { catchError, firstValueFrom, map, Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ListarEleccionesResumenGeneralInput } from '../interfaces/input/resumen-general/listar-elecciones-resumen-general-input';
import { Base } from '../interfaces/output/base.model';
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
  private readonly request = inject(RequestsService);
  private readonly baseUrl = environment.apiUrl;
  private readonly ORDEN_ELECCIONES = [10, 14, 15, 13, 12];

  constructor() {}

  listarElecciones(
    data: ListarEleccionesResumenGeneralInput,
    endpoint: string = '/mae-eleccion',
    ordenar: boolean = true
  ): Observable<FrontendResponse<TipoEleccion[]>> {
    return this.request
      .get<Base>(`${this.baseUrl}${endpoint}/${data.idProceso}`)
      .pipe(
        map((response) =>
          this.mapEleccionesResponse(response.body, ordenar)
        ),
        catchError(catchErrorHandler$)
      );
  }

  listarEleccionesParaConfigurarReporte(
    data: ListarEleccionesResumenGeneralInput
  ) {
    return this.listarElecciones(
      data,
      '/mae-eleccion/for-create-report'
    );
  }

  listarEleccionesParaConfigurarReporteActas(
    data: ListarEleccionesResumenGeneralInput
  ) {
    return this.listarElecciones(
      data,
      '/mae-eleccion/for-create-report-actas'
    );
  }

  async getIdEleccionPrincipal(): Promise<number | null> {
    const stored = getEncryptStorageEleccionValue('ID_ELECCION_PRINCIPAL');
    if (stored) {
      return Number(JSON.parse(stored));
    }

    try {
      const response = await firstValueFrom(
        this.getProcesoElectoralActivo$()
      );
      const id = response?.data?.id;

      if (id) {
        setEncryptStorageEleccionValue(
          'ID_ELECCION_PRINCIPAL',
          id
        );
        return id;
      }
    } catch (error) {
      console.error('Error al obtener ID de elección principal:', error);
    }

    return null;
  }

  getProcesoElectoralActivo$(): Observable<
    FrontendResponse<IProcesoElectoralActivoData>
  > {
    return this.request
      .get<IProcesoElectoralActivoResponse>(
        `${this.baseUrl}/proceso/proceso-electoral-activo`
      )
      .pipe(
        map(({ body }) => ({
          success: body!.success,
          data: body!.data,
        })),
        catchError(catchErrorHandler$)
      );
  }

  private mapEleccionesResponse(
    body: Base | null,
    ordenar: boolean
  ): FrontendResponse<TipoEleccion[]> {
    let elecciones: TipoEleccion[] =
      body?.data?.map((e: { codigo: string; nombre: string }) => ({
        ...e,
        value: Number(e.codigo),
        text: e.nombre.toUpperCase(),
      })) ?? [];

    if (ordenar) {
      elecciones = this.ordenarElecciones(elecciones);
    }

    return {
      success: body?.success ?? false,
      data: elecciones,
    };
  }

  private ordenarElecciones(
    elecciones: TipoEleccion[]
  ): TipoEleccion[] {
    return [...elecciones].sort((a, b) => {
      const ia = this.ORDEN_ELECCIONES.indexOf(a.value);
      const ib = this.ORDEN_ELECCIONES.indexOf(b.value);
      return (ia === -1 ? 999 : ia) - (ib === -1 ? 999 : ib);
    });
  }
}
