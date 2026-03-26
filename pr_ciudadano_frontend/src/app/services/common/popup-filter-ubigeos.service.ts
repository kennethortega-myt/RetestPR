import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";

import { GenericFilterParams } from "../../interfaces/filtro-settings";
import { IBaseFiltroUbigeo } from "../../interfaces/elections.interfaces";

@Injectable({
  providedIn: "root",
})
export class PopupFilterUbigeosService {
  private applyFiltersSubject = new Subject<GenericFilterParams>();
  private applyBreadcrumbSubject = new Subject<string>();
  private applyFiltersInformationSubject = new Subject<IBaseFiltroUbigeo>();
  private applyUbigeosSubject = new Subject<GenericFilterParams>();

  constructor() {}

  // FILTERS

  public get applyFilters$(): Observable<GenericFilterParams> {
    return this.applyFiltersSubject.asObservable();
  }

  public setApplyFilters(params: GenericFilterParams) {
    this.applyFiltersSubject.next(params);
  }

  // BREADCRUMBS

  public get applyBreadcrumb$(): Observable<string> {
    return this.applyBreadcrumbSubject.asObservable();
  }

  public setApplyBreadcrumb(value: string) {
    this.applyBreadcrumbSubject.next(value);
  }

  // UBIGEOS LIST

  public get applyFiltersInformation$(): Observable<IBaseFiltroUbigeo> {
    return this.applyFiltersInformationSubject.asObservable();
  }

  public setApplyFiltersInformation(value: IBaseFiltroUbigeo) {
    this.applyFiltersInformationSubject.next(value);
  }

  // UBIGEOS CONFIG

  public get applyUbigeos$(): Observable<GenericFilterParams> {
    return this.applyUbigeosSubject.asObservable();
  }

  public setApplyUbigeos(value: GenericFilterParams) {
    this.applyUbigeosSubject.next(value);
  }
}
