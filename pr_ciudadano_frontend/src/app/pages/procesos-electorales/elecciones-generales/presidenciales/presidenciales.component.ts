import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit } from "@angular/core";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";

import {
  getCurrentElectionDescriptionTitleBy,
  getCurrentElectionId,
} from "../../../../helpers/encrypt-storage-eleccion";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { ParlamentoCommon } from "../commons/parlamento-common";
import { setPresidencialesUrlInSessionStorage } from "../../../../helpers/session-storage-helper";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { Subscription } from "rxjs";

const currentIconKey: MenuElectionIconKeys = "presidenciales";

@Component({
  selector: "app-presidenciales",
  templateUrl: "./presidenciales.component.html",
  standalone: false,
})
export class PresidencialesComponent extends ParlamentoCommon implements OnInit, OnDestroy, AfterViewInit  {
  public electionID = getCurrentElectionId(currentIconKey);
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  public isResponsive = false;
  private breakpointSubscription: Subscription;

  constructor(
    public override resumenGeneralService: ResumenGeneralService,
    public override elementRef: ElementRef,
    private readonly breakpointObserver: BreakpointObserver
  ) {
    super(resumenGeneralService, elementRef);
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnInit(): void {
    setPresidencialesUrlInSessionStorage();
    this.resumenGeneralService.loadGeneralSummary(this);
  }

  ngAfterViewInit() {
    const tabIndex = history.state?.sectionIndex;    
      
    if (tabIndex !== undefined) {
      history.replaceState({ ...history.state, sectionIndex: undefined }, '');
      
      const tabId = `tab${tabIndex}`;
      this.activeTab = tabId;
      this.openTab(tabId);
    }
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }
}
