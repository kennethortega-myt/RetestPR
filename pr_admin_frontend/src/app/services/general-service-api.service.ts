import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, Subject, takeUntil } from 'rxjs';

import { environment } from '../../environments/environment';
import { Constantes } from '../helpers/constantes';
import { AmbitoBean } from '../interfaces/ambitoBean';
import { CentEducativoBean } from '../interfaces/cent-educativo-bean';
import { CentroComputoBean } from '../interfaces/centroComputoBean';
import { DetCatalogoEstructuraBean } from '../interfaces/DetCatalogoEstructuraBean';
import { DistritoBean } from '../interfaces/distrito';
import { FiltroUbigeoDepartamentoBean } from '../interfaces/FiltroUbigeoDepartamentoBean';
import { FiltroUbigeoDistritoBean } from '../interfaces/filtroUbigeoDistritoBean';
import { FiltroUbigeoProvinciaBean } from '../interfaces/filtroUbigeoProvinciaBean';
import { IGenericInterface } from '../interfaces/general.interface';
import { GenericResponseBean } from '../interfaces/genericResponseBean';
import { ItemBean } from '../interfaces/item-bean';
import { LocalVotacionBean } from '../interfaces/localVotacionBean';
import { MaeEleccionBean } from '../interfaces/maeEleccionBean';
import { MaeProcesoElectoralBean } from '../interfaces/maeProcesoElectoralBean';
import { MesaBean } from '../interfaces/mesaBean';
import { ProcesoAmbitoBean } from '../interfaces/procesoAmbitoBean';
import { ProvinciaBean } from '../interfaces/provincia-bean';
import { UbigeoDepartamentoBean } from '../interfaces/UbigeoDepartamentoBean';
import { UbigeoDistritoBean } from '../interfaces/ubigeoDistritoBean';
import { UbigeoProvinciaBean } from '../interfaces/ubigeoProvinciaBean';

@Injectable({
  providedIn: 'root',
})
export class GeneralServiceApi {
  private readonly urlServidor: string;
  private readonly urlServidorNacion: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly httpClient: HttpClient) {
    this.urlServidor = '';
    this.urlServidorNacion = environment.apiUrl;
  }

  obtenerProcesoNacion(): Observable<Array<ItemBean>> {
    return this.httpClient.get<Array<ItemBean>>(
      this.urlServidorNacion +
        Constantes.CB_GENERAL_CONTROLLER_OBTENER_PROCESOS,
      {}
    );
  }

  obtenerProcesos(): Observable<
    GenericResponseBean<Array<MaeProcesoElectoralBean>>
  > {
    return this.httpClient.get<
      GenericResponseBean<Array<MaeProcesoElectoralBean>>
    >(this.urlServidor + Constantes.CB_PROCESO_CONTROLLER_LIST_PROCESOS);
  }

  obtenerElecciones(
    idProceso: string
  ): Observable<GenericResponseBean<Array<MaeEleccionBean>>> {
    return this.httpClient.get<GenericResponseBean<Array<MaeEleccionBean>>>(
      this.urlServidor +
        'proceso/' +
        idProceso +
        Constantes.CB_PROCESO_CONTROLLER_LIST_ELECCIONES
    );
  }

  obtenerEleccionNacion(idProceso: string): Observable<Array<ItemBean>> {
    return this.httpClient.get<Array<ItemBean>>(
      this.urlServidorNacion +
        Constantes.CB_GENERAL_CONTROLLER_OBTENER_ELECCIONES +
        '?idProceso=' +
        idProceso,
      {}
    );
  }

  obtenerDetCatalogoEstructura(
    cMaestro: string,
    cColumna: string
  ): Observable<IGenericInterface<DetCatalogoEstructuraBean[]>> {
    return this.httpClient.get<IGenericInterface<DetCatalogoEstructuraBean[]>>(
      this.urlServidor +
        Constantes.CB_GENERAL_CONTROLLER_DET_CATALOGO_ESTRUCTURA +
        '?c_maestro=' +
        cMaestro +
        '&c_columna=' +
        cColumna,
      {}
    );
  }

  getDepartamento(
    filtroUbigeoDepartamentoBean: FiltroUbigeoDepartamentoBean
  ): Observable<GenericResponseBean<Array<UbigeoDepartamentoBean>>> {
    return this.httpClient.post<
      GenericResponseBean<Array<UbigeoDepartamentoBean>>
    >(
      this.urlServidor + Constantes.CB_UBIGEO_CONTROLLER_GET_DEPARTAMENTO,
      filtroUbigeoDepartamentoBean
    );
  }

  getProvincia(
    filtroUbigeoProvinciaBean: FiltroUbigeoProvinciaBean
  ): Observable<GenericResponseBean<Array<UbigeoProvinciaBean>>> {
    return this.httpClient.post<
      GenericResponseBean<Array<UbigeoProvinciaBean>>
    >(
      this.urlServidor + Constantes.CB_UBIGEO_CONTROLLER_GET_PROVINCIA,
      filtroUbigeoProvinciaBean
    );
  }

  getDistrito(
    filtroUbigeoDistritoBean: FiltroUbigeoDistritoBean
  ): Observable<GenericResponseBean<Array<UbigeoDistritoBean>>> {
    return this.httpClient.post<GenericResponseBean<Array<UbigeoDistritoBean>>>(
      this.urlServidor + Constantes.CB_UBIGEO_CONTROLLER_GET_DISTRITO,
      filtroUbigeoDistritoBean
    );
  }



  getListAmbitos(): Observable<GenericResponseBean<Array<AmbitoBean>>> {
    return this.httpClient.get<GenericResponseBean<Array<AmbitoBean>>>(
      this.urlServidor + Constantes.CB_AMBITO_CONTROLLER_LIST_AMBITOS
    );
  }

  getTipoAmbitoPorProceso(
    idProceso: string
  ): Observable<GenericResponseBean<ProcesoAmbitoBean>> {
    return this.httpClient.get<GenericResponseBean<ProcesoAmbitoBean>>(
      this.urlServidor +
        'proceso/' +
        idProceso +
        Constantes.CB_PROCESO_CONTROLLER_TIPO_AMBITO_POR_PROCESO
    );
  }

  getCentrosComputo(): Observable<
    GenericResponseBean<Array<CentroComputoBean>>
  > {
    return this.httpClient.get<GenericResponseBean<Array<CentroComputoBean>>>(
      this.urlServidor +
        Constantes.CB_CENTRO_COMPUTO_CONTROLLER_LIST_CENTROS_COMPUTO
    );
  }

  obtenerDepartamento(): Observable<Array<ItemBean>> {
    return this.httpClient.get<Array<ItemBean>>(
      'assets/data/departamentos.json'
    );
  }

  obtenerProvincia(idDepartamento: string): Observable<Array<ProvinciaBean>> {
    let listaProvincia = new Array<ProvinciaBean>();
    let parametro = '';
    if (+idDepartamento === 0) {
      parametro = '';
    } else {
      parametro = '' + idDepartamento;
    }

    this.obtenerProvinciaAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe((response) => {
        response.forEach((d) => {
          if (d.ID_DEPARTAMENTO == parametro) {
            listaProvincia.push(d);
          }
        });
      });

    return of(listaProvincia);
  }

  obtenerProvinciaAll(): Observable<Array<ProvinciaBean>> {
    return this.httpClient.get<Array<ProvinciaBean>>(
      'assets/data/provincias.json'
    );
  }

  obtenerDistrito(
    idDepartamento: string,
    idProvincia: string
  ): Observable<Array<DistritoBean>> {
    let listaDistrito = new Array<DistritoBean>();
    let parametro = '';
    this.obtenerDistritoAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe((response) => {
        response.forEach((d) => {
          if (
            d.ID_DEPARTAMENTO == idDepartamento &&
            d.ID_PROVINCIA == idProvincia
          ) {
            listaDistrito.push(d);
          }
        });
      });
    return of(listaDistrito);
  }

  obtenerLocalVotacionPorUbigeo(
    ubigeo: string
  ): Observable<GenericResponseBean<Array<LocalVotacionBean>>> {
    return this.httpClient.post<GenericResponseBean<Array<LocalVotacionBean>>>(
      this.urlServidor + 'ubigeos/locales-votacion',
      { idUbigeo: ubigeo }
    );
  }

  obtenerMesaPorLocalVotacion(
    idLocalVotacion: number
  ): Observable<GenericResponseBean<Array<MesaBean>>> {
    return this.httpClient.post<GenericResponseBean<Array<MesaBean>>>(
      this.urlServidor + 'ubigeos/mesas',
      { idLocalVotacion: idLocalVotacion }
    );
  }

  obtenerDistritoAll(): Observable<Array<DistritoBean>> {
    return this.httpClient.get<Array<DistritoBean>>(
      'assets/data/distrito.json'
    );
  }

  obtenerLocalVotacion(): Observable<Array<CentEducativoBean>> {
    return this.httpClient.get<Array<CentEducativoBean>>(
      'assets/data/centros.json'
    );
  }
}
