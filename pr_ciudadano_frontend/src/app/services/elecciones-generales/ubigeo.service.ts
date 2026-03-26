import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { FrontendResponse } from "../../interfaces/response.common";
import { UbigeoBean, DistritoElectoral, LocalVotacion, Region } from "../../interfaces/ubigeo-bean";
import { UbigeoApiService } from "./ubigeo-api.service";

@Injectable({
  providedIn: "root",
})
export class UbigeoService {
  constructor(private ubigeoApiService: UbigeoApiService) {}

  listarDepartamentos(idEleccion: number, idAmbito: number): Observable<UbigeoBean> {
    return this.ubigeoApiService.listarDepartamentos(idEleccion, idAmbito);
  }

  listarProvincias(idEleccion: number, idAmbito: number, idUbigeoDepartamento: string): Observable<UbigeoBean> {
    return this.ubigeoApiService.listarProvincias(idEleccion, idAmbito, idUbigeoDepartamento);
  }

  listarDistritos(idEleccion: number, idAmbito: number, provincia: string): Observable<UbigeoBean> {
    return this.ubigeoApiService.listarDistritos(idEleccion, idAmbito, provincia);
  }
  listarLocales(idEleccion: number, idUbigeo: number): Observable<FrontendResponse<[LocalVotacion]>> {
    return this.ubigeoApiService.listarLocales(idEleccion, idUbigeo);
  }
  listarRegiones(): Observable<FrontendResponse<Region[]>> {
    return this.ubigeoApiService.listarRegiones();
  }
  listarDistritoElectorales(): Observable<FrontendResponse<[DistritoElectoral]>> {
    return this.ubigeoApiService.listarDistritoElectoral();
  }
}
