import { Inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { ParlGeographicalLocationNameParams } from '../../interfaces/parlamento.interfaces';
import {
  PoliticOrganizationsForSelectItem,
  GeographicalLocationParams,
  GeographicalLocationItem,
  PoliticalOrganizationParams,
  PoliticalOrganizationItem,
  GeographicalLocationNameInfo,
  ResultOfParticipantsParams,
  ResultOfParticipantsItem
} from '../../interfaces/presidenciales.interfaces';
import { FrontendResponse } from '../../interfaces/response.common';
import {
  getParticipantsByCandidates$,
  getParticipantsByGeographicalLocation$,
  getParticipantsByPoliticalOrganization$,
  getParticipantsByGeographicalLocationName$,
  getResultOfParticipants$
} from '../common/getParticipants';
import { getPoliticOrganizationsForSelect$ } from '../common/getPoliticOrganizationsForSelect';
import { ICommonUrls } from '../common/politicCommonProviders';
import { RequestsService } from '../common/requests.service';
import { Observable } from 'rxjs';

export const ParlamentoApiUrls = {
  geographicalLocation: environment.apiUrlLocal + 'parlamento-andino/participantes-ubicacion-geografica',
  politicalOrganization: environment.apiUrlLocal + 'parlamento-andino/participantes-organizacion-politica',
  geographicalLocationName: environment.apiUrlLocal + 'parlamento-andino/participantes-ubicacion-geografica-nombre',
  getPoliticalOrganizationForSelect: environment.apiUrlLocal + 'parlamento-andino/organizacion-politica',
  getResultOfParticipants: environment.apiUrlLocal + 'parlamento-andino/participantes-realizar-busqueda',
  participantsByCandidates: environment.apiUrlLocal + 'parlamento-andino/participantes-por-candidato'
};

const modulePath = 'senadores-distrito-unico/';
export const Senadores33ApiUrls = {
  geographicalLocation: environment.apiUrlLocal + modulePath + 'participantes-ubicacion-geografica',
  politicalOrganization: environment.apiUrlLocal + modulePath + 'participantes-organizacion-politica',
  geographicalLocationName: environment.apiUrlLocal + modulePath + 'participantes-ubicacion-geografica-nombre',
  getPoliticalOrganizationForSelect: environment.apiUrlLocal + modulePath + 'organizacion-politica',
  getResultOfParticipants: environment.apiUrlLocal + modulePath + 'participantes-realizar-busqueda',
  participantsByCandidates: environment.apiUrlLocal + modulePath + 'participantes-por-candidato'
};

@Injectable({
  providedIn: 'root'
})
export class ParlamentoService {
  constructor(private readonly request: RequestsService, @Inject('specificURLs') private specificURLs: ICommonUrls) {}

  public getPoliticOrganizationsForSelect$(): Observable<FrontendResponse<PoliticOrganizationsForSelectItem[]>> {
    return getPoliticOrganizationsForSelect$(this.request, this.specificURLs.getPoliticalOrganizationForSelect);
  }

  public getParticipantsByCandidates$(
    params: GeographicalLocationParams
  ): Observable<FrontendResponse<GeographicalLocationItem[]>> {
    return getParticipantsByCandidates$(this.request, this.specificURLs.participantsByCandidates, params);
  }

  public getParticipantsByGeographicalLocation$(
    params: GeographicalLocationParams
  ): Observable<FrontendResponse<GeographicalLocationItem[]>> {
    return getParticipantsByGeographicalLocation$(this.request, this.specificURLs.geographicalLocation, params);
  }

  public getParticipantsByPoliticalOrganization$(
    params: PoliticalOrganizationParams
  ): Observable<FrontendResponse<PoliticalOrganizationItem[]>> {
    return getParticipantsByPoliticalOrganization$(this.request, this.specificURLs.politicalOrganization, params);
  }

  public getParticipantsByGeographicalLocationName$(
    params: ParlGeographicalLocationNameParams
  ): Observable<FrontendResponse<GeographicalLocationNameInfo>> {
    return getParticipantsByGeographicalLocationName$(this.request, this.specificURLs.geographicalLocationName, params);
  }

  public getResultOfParticipants$(
    params: ResultOfParticipantsParams
  ): Observable<FrontendResponse<ResultOfParticipantsItem[]>> {
    return getResultOfParticipants$(this.request, this.specificURLs.getResultOfParticipants, params);
  }
}
