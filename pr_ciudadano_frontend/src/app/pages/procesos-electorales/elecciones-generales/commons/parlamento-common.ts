import { ElementRef } from "@angular/core";

import { scrollToMainSectionBase } from "../../../../helpers/ubigeo-level.common";
import { RegionValue, FilterByLocationParams, GenericFilterParams } from "../../../../interfaces/filtro-settings";
import { Resumen } from "../../../../interfaces/resumen-bean";
import { ILoadGeneralSummaryComponent } from "../../../../interfaces/resumen-general.interfaces";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";

export class ParlamentoCommon implements ILoadGeneralSummaryComponent {
  public resumen: Resumen = {} as Resumen;
  public regionValue: RegionValue = "TODOS";
  public selectedFilterParams: FilterByLocationParams = {} as FilterByLocationParams;
  public activeTab: string = "tab1";

  constructor(public resumenGeneralService: ResumenGeneralService, public elementRef: ElementRef) {}

  public openTab(tabName: string) {
    this.activeTab = tabName;
    this.regionValue = "TODOS";
    this.resumenGeneralService.loadGeneralSummary(this);
  }

  public scrollToMainSection(): void {
    scrollToMainSectionBase(this);
  }

  public regionChanged(event: RegionValue): void {
    this.resumenGeneralService.regionChanged(event, this);
  }

  public filterDistrictElectionChart($event: FilterByLocationParams): void {
    this.resumenGeneralService.filterDistrictElectionChart($event, this);
  }

  public loadGeneralSummaryByGenericFilters($event: GenericFilterParams): void {
    this.resumenGeneralService.loadGeneralSummaryByGenericFilters($event, this);
  }
}
