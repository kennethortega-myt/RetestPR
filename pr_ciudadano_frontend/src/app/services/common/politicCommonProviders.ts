import { Provider } from "@angular/core";
import { ParlamentoApiUrls, ParlamentoService, Senadores33ApiUrls } from "../elecciones-generales/parlamento.service";
import { RequestsService } from "./requests.service";

export interface ICommonUrls {
  geographicalLocation: string;
  politicalOrganization: string;
  geographicalLocationName: string;
  getPoliticalOrganizationForSelect: string;
  getResultOfParticipants: string;
  participantsByCandidates: string;
}

export class CommonServiceProvider {
  static getParlamentoServiceProvider() {
    return {
      provide: ParlamentoService,
      deps: [RequestsService],
      useFactory: (dep: RequestsService) => {
        return new ParlamentoService(dep, ParlamentoApiUrls);
      },
    } as Provider;
  }

  static getSenadores33ServiceProvider() {
    return {
      provide: ParlamentoService,
      deps: [RequestsService],
      useFactory: (dep: RequestsService) => {
        return new ParlamentoService(dep, Senadores33ApiUrls);
      },
    } as Provider;
  }
}
