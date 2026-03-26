import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from "@angular/core";

import { GenericFilterUbigeoComponent } from "../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component";
import { MainHotMapComponent } from "../../../../../components/main-hot-map/main-hot-map.component";
import { RegionValue, FilterByLocationParams, GenericFilterParams } from "../../../../../interfaces/filtro-settings";
import { PopFilterOpComponent } from "../../../../../components/pop-filter-op/pop-filter-op.component";
import { MatBottomSheet } from "@angular/material/bottom-sheet"; // Nuevo
import { PresidencialesService } from "../../../../../services/elecciones-generales/presidenciales.service";
import { OrganizacionPoliticaPresidencialesCommon } from "../presidenciales-helpers/OrganizacionPoliticaPresidencialesCommon";

@Component({
  selector: "app-tab-organizacion-politica-presidenciales-res",
  templateUrl: "./tab-organizacion-politica-presidenciales-res.component.html",
  standalone: false,
})
export class TabOrganizacionPoliticaPresidencialesResComponent
  extends OrganizacionPoliticaPresidencialesCommon
  implements OnInit
{
  @ViewChild(MainHotMapComponent, { static: false })
  override mainHotMapComponent: MainHotMapComponent;
  @ViewChild(GenericFilterUbigeoComponent)
  override mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;

  @Output() override regionChangedEvent = new EventEmitter<RegionValue>();
  @Output() override filterByLocationParamsEvent = new EventEmitter<FilterByLocationParams>();
  @Output() override updateFiltersFromNewEvent = new EventEmitter<GenericFilterParams>();

  @Input() override electionID: number;

  candidatoSeleccionado: any;

  constructor(
    public override presidencialesService: PresidencialesService,
    private readonly _bottomSheet: MatBottomSheet
  ) {
    super(presidencialesService);
  }

  openBottomSheet(): void {
    const bottomSheetRef = this._bottomSheet.open(PopFilterOpComponent, {
      panelClass: "modal-fullscreen",
      disableClose: false,
      data: {
        electionID: this.electionID
      },
      height: "100vh",
    });

    bottomSheetRef.afterDismissed().subscribe((candidato) => {
      if (candidato) {
        this.candidatoSeleccionado = candidato;
        this.showMap = true;
        this.selectThisCandidate(candidato);
      }
    });
  }

  ngOnInit(): void {
    // Loading functionality removed
  }

  public get calculateNumberOfPages(): number {
    let numberOfCandidates = this.policitalOrganizationItems.length;
    return Math.ceil(numberOfCandidates / this.numberByPage);
  }
}
