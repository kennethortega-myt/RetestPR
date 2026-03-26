import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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
import { GeneralServiceApi } from './general-service-api.service';

@Injectable({
  providedIn: 'root',
})
export class GeneralService {
  constructor(private readonly generalServiceApi: GeneralServiceApi) {}

  obtenerProcesos(): Observable<
    GenericResponseBean<Array<MaeProcesoElectoralBean>>
  > {
    return this.generalServiceApi.obtenerProcesos();
  }

  obtenerElecciones(
    idProceso: string
  ): Observable<GenericResponseBean<Array<MaeEleccionBean>>> {
    return this.generalServiceApi.obtenerElecciones(idProceso);
  }

  obtenerUsuarioSession() {
    return 'jcisnerosp';
  }

  obtenerDetCatalogoEstructura(
    cMaestro: string,
    cColumna: string
  ): Observable<IGenericInterface<DetCatalogoEstructuraBean[]>> {
    return this.generalServiceApi.obtenerDetCatalogoEstructura(
      cMaestro,
      cColumna
    );
  }

  getDepartamento(
    filtroUbigeoDepartamentoBean: FiltroUbigeoDepartamentoBean
  ): Observable<GenericResponseBean<Array<UbigeoDepartamentoBean>>> {
    return this.generalServiceApi.getDepartamento(filtroUbigeoDepartamentoBean);
  }
  getProvincia(
    filtroUbigeoProvinciaBean: FiltroUbigeoProvinciaBean
  ): Observable<GenericResponseBean<Array<UbigeoProvinciaBean>>> {
    return this.generalServiceApi.getProvincia(filtroUbigeoProvinciaBean);
  }
  getDistrito(
    filtroUbigeoDistritoBean: FiltroUbigeoDistritoBean
  ): Observable<GenericResponseBean<Array<UbigeoDistritoBean>>> {
    return this.generalServiceApi.getDistrito(filtroUbigeoDistritoBean);
  }

  getEleccionesPorProceso(
    idProceso: string
  ): Observable<GenericResponseBean<Array<MaeEleccionBean>>> {
    return this.generalServiceApi.getEleccionesPorProceso(idProceso);
  }

  getListAmbitos(): Observable<GenericResponseBean<Array<AmbitoBean>>> {
    return this.generalServiceApi.getListAmbitos();
  }

  getTipoAmbitoPorProceso(
    idProceso: string
  ): Observable<GenericResponseBean<ProcesoAmbitoBean>> {
    return this.generalServiceApi.getTipoAmbitoPorProceso(idProceso);
  }

  getCentrosComputo(): Observable<
    GenericResponseBean<Array<CentroComputoBean>>
  > {
    return this.generalServiceApi.getCentrosComputo();
  }

  obtenerDepartamento(): Observable<Array<ItemBean>> {
    return this.generalServiceApi.obtenerDepartamento();
  }

  obtenerProvincia(idDepartamento: string): Observable<Array<ProvinciaBean>> {
    return this.generalServiceApi.obtenerProvincia(idDepartamento);
  }

  obtenerDistrito(
    idDepartamento: string,
    idProvincia: string
  ): Observable<Array<DistritoBean>> {
    return this.generalServiceApi.obtenerDistrito(idDepartamento, idProvincia);
  }

  obtenerLocalVotacionPorUbigeo(
    ubigeo: string
  ): Observable<GenericResponseBean<Array<LocalVotacionBean>>> {
    return this.generalServiceApi.obtenerLocalVotacionPorUbigeo(ubigeo);
  }

  obtenerMesaPorLocalVotacion(
    idLocalVotacion: number
  ): Observable<GenericResponseBean<Array<MesaBean>>> {
    return this.generalServiceApi.obtenerMesaPorLocalVotacion(idLocalVotacion);
  }

  obtenerLocalVotacion(): Observable<Array<CentEducativoBean>> {
    return this.generalServiceApi.obtenerLocalVotacion();
  }
}
