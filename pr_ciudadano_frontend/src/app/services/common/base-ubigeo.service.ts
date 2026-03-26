import { Injectable } from "@angular/core";
import { RequestsService } from "./requests.service";
import { Observable, map, catchError, take } from "rxjs";
import { LocalVotacion } from "../../interfaces/acta-bean";
import {
  DepartmentParams,
  Department,
  DepartmentsResponse,
  ProvinceParams,
  Province,
  ProvincesResponse,
  DistrictParams,
  District,
  DistrictsResponse,
  LocalParams,
  LocalsResponse,
  Region,
  RegionesResponse,
  InternationalParams,
  InternationalUbigeoResponse,
  InternationalUbigeo,
  IBaseFiltroUbigeo,
} from "../../interfaces/elections.interfaces";
import { FrontendResponse } from "../../interfaces/response.common";
import { catchErrorHandler$ } from "./catchErrorHandler";
import { environment } from "../../../environments/environment";
import { GEOGRAPHIC_SCOPE, GEOGRAPHIC_SCOPE_EXTRANJERA } from "../../helpers/constantes";

const specificURLs = {
  departments: environment.apiUrlLocal + "ubigeos/departamentos",
  provinces: environment.apiUrlLocal + "ubigeos/provincias",
  districts: environment.apiUrlLocal + "ubigeos/distritos",
  locals: environment.apiUrlLocal + "ubigeos/locales",
  depProvDist: environment.apiUrlLocal + "ubigeos/dep-prov-distritos",
  regiones: environment.apiUrlLocal + "distrito-electoral/distritos",
  summaryTotals: environment.apiUrlLocal + "resumen-general/totales",
  summaryCandidates: environment.apiUrlLocal + "resumen-general/participantes",
  electionList: (id: number): string => {
    return environment.apiUrlLocal + `proceso/${id}/elecciones`;
  },
};

@Injectable({
  providedIn: "root",
})
export class BaseUbigeoService {
  constructor(private readonly request: RequestsService) {}

  public loadDepartments(scope: IBaseFiltroUbigeo) {
    this.getDepartments$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listDepartamento = response.data;
        } else {
          console.error("loadDepartments error");
        }
      });
  }

  public loadProvinces(scope: IBaseFiltroUbigeo, departmentId: string) {
    this.getProvinces$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
      idUbigeoDepartamento: departmentId,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listProvincia = response.data;
        } else {
          console.error("loadProvinces error");
        }
      });
  }

  public loadDistricts(scope: IBaseFiltroUbigeo, provinceId: string) {
    this.getDistricts$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
      idUbigeoProvincia: provinceId,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listDistrito = response.data;
        } else {
          console.error("loadDistricts error");
        }
      });
  }

  public loadContinents(scope: IBaseFiltroUbigeo) {
    this.getInternationalContinetals$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE_EXTRANJERA,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listContinentals = response.data;
        } else {
          console.error("loadContinentals error");
        }
      });
  }

  public loadCountries(scope: IBaseFiltroUbigeo, continentId: string) {
    this.getInternationalCountries$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE_EXTRANJERA,
      idUbigeo: continentId,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listCountries = response.data;
        } else {
          console.error("loadContinentals error");
        }
      });
  }

  public loadStates(scope: IBaseFiltroUbigeo, countryId: string) {
    this.getInternationalStates$({
      idEleccion: scope.electionId,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE_EXTRANJERA,
      idUbigeo: countryId,
    })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.listStates = response.data;
        } else {
          console.error("loadContinentals error");
        }
      });
  }

  public loadRegiones(scope: IBaseFiltroUbigeo, params: DepartmentParams) {
    this.getDepProvDist$(params)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          scope.regiones = response.data.map((elem) => {
            return {
              codigo: Number(elem.ubigeo),
              nombre: elem.nombre,
              ubigeo: elem.ubigeo,
            } as Region;
          });
        } else {
          console.error("loadRegiones error");
        }
      });
  }

  // BASIC REQUEST TO GET UBIGEOS

  public getDepProvDist$(params: DepartmentParams): Observable<FrontendResponse<Department[]>> {
    return this.request.post<DepartmentsResponse>(specificURLs.depProvDist, params).pipe(
      map((response) => {
        const customResponse = {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Department[]>;
        return customResponse;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getDepartments$(params: DepartmentParams): Observable<FrontendResponse<[Department]>> {
    return this.request.post<DepartmentsResponse>(specificURLs.departments, params).pipe(
      map((response) => {
        const customResponse = {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[Department]>;
        return customResponse;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getProvinces$(params: ProvinceParams): Observable<FrontendResponse<[Province]>> {
    return this.request.post<ProvincesResponse>(specificURLs.provinces, params).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[Province]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getDistricts$(params: DistrictParams): Observable<FrontendResponse<[District]>> {
    return this.request.post<DistrictsResponse>(specificURLs.districts, params).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[District]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getLocals$(params: LocalParams): Observable<FrontendResponse<[LocalVotacion]>> {
    return this.request.post<LocalsResponse>(specificURLs.locals, params).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[LocalVotacion]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getRegiones$(): Observable<FrontendResponse<[Region]>> {
    return this.request.post<RegionesResponse>(specificURLs.regiones, {}).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<[Region]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getInternationalContinetals$(params: InternationalParams): Observable<FrontendResponse<Department[]>> {
    return this.request.post<InternationalUbigeoResponse>(specificURLs.departments, params).pipe(
      map((response) => {
        return {
          success: response.body.success,
          data: response.body.data,
        } as FrontendResponse<Department[]>;
      }),
      catchError(catchErrorHandler$)
    );
  }

  public getInternationalCountries$(params: InternationalParams): Observable<FrontendResponse<Province[]>> {
    return this.request
      .post<InternationalUbigeoResponse>(specificURLs.provinces, {
        idEleccion: params.idEleccion,
        idAmbitoGeografico: params.idAmbitoGeografico,
        idUbigeoDepartamento: params.idUbigeo,
      })
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<Province[]>;
        }),
        catchError(catchErrorHandler$)
      );
  }

  public getInternationalStates$(params: InternationalParams): Observable<FrontendResponse<InternationalUbigeo[]>> {
    return this.request
      .post<InternationalUbigeoResponse>(specificURLs.districts, {
        idEleccion: params.idEleccion,
        idAmbitoGeografico: params.idAmbitoGeografico,
        idUbigeoProvincia: params.idUbigeo,
      })
      .pipe(
        map((response) => {
          return {
            success: response.body.success,
            data: response.body.data,
          } as FrontendResponse<InternationalUbigeo[]>;
        }),
        catchError(catchErrorHandler$)
      );
  }
}
