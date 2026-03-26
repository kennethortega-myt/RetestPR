import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { MainHotMapComponent } from '../../../../../components/main-hot-map/main-hot-map.component';
import { FilterByLocationParams, GenericFilterParams, REGION_PERU, RegionValue } from '../../../../../interfaces/filtro-settings';
import { PresidencialesService } from '../../../../../services/elecciones-generales/presidenciales.service';
import { OrganizacionPoliticaPresidencialesCommon } from '../presidenciales-helpers/OrganizacionPoliticaPresidencialesCommon';
import { formatNumberWithApostrophe } from '../../../../../utils/funciones';

@Component({
  selector: 'app-tab-organizacion-politica-presidenciales',
  templateUrl: './tab-organizacion-politica-presidenciales.component.html',
  styleUrls: ['./tab-organizacion-politica-presidenciales.component.scss'],
  standalone: false
})
export class TabOrganizacionPoliticaPresidencialesComponent
  extends OrganizacionPoliticaPresidencialesCommon
  implements OnInit
{
  @Input() override electionID: number;
  @Output() override regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() override filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() override updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();
  @ViewChild(MainHotMapComponent, { static: false }) override mainHotMapComponent: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent) override mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;

  regionFilter: RegionValue = REGION_PERU;

  constructor(
    public override presidencialesService: PresidencialesService,
  ) {
    super(presidencialesService);
  }

  ngOnInit(): void {
    this.initialLoad('pres');
  }

  public get calculateNumberOfPages(): number {
    let numberOfCandidates = this.policitalOrganizationItems.length;
    return Math.ceil(numberOfCandidates / this.numberByPage);
  }

  public getLocaleString(numberValue: number | null): string {
      return formatNumberWithApostrophe(numberValue);
  }  
}
