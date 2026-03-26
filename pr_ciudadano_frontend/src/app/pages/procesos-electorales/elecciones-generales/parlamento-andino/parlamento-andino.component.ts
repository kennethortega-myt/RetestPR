import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, inject, OnDestroy, OnInit } from "@angular/core";
import { BreakpointObserver, Breakpoints, LayoutModule } from "@angular/cdk/layout";
import {
  getCurrentElectionDescriptionTitleBy,
  getCurrentElectionId,
} from "../../../../helpers/encrypt-storage-eleccion";
import { RequestsService } from "../../../../services/common/requests.service";
import { ParlamentoService, ParlamentoApiUrls } from "../../../../services/elecciones-generales/parlamento.service";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { ParlamentoCommon } from "../commons/parlamento-common";
import { setParlamentoAndinoUrlInSessionStorage } from "../../../../helpers/session-storage-helper";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { Subscription } from "rxjs";

const currentIconKey: MenuElectionIconKeys = "parlamento_andino";

@Component({
  selector: "app-parlamento-andino",
  templateUrl: "./parlamento-andino.component.html",
  standalone: false,
})
export class ParlamentoAndinoComponent extends ParlamentoCommon implements OnInit, OnDestroy, AfterViewInit {
  public iconoClass = "icon-ico_e_parlamento";
  public electionID = getCurrentElectionId(currentIconKey);
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  private readonly requestService = inject(RequestsService);
  public readonly parlamentoService = new ParlamentoService(this.requestService, ParlamentoApiUrls);

  public isResponsive = false;
  private breakpointSubscription: Subscription;

  public showNombreOP = true;

  constructor(
    public override resumenGeneralService: ResumenGeneralService,
    public override elementRef: ElementRef,
    private readonly breakpointObserver: BreakpointObserver,
    private cdr: ChangeDetectorRef) {      
    super(resumenGeneralService, elementRef);
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnInit(): void {
    setParlamentoAndinoUrlInSessionStorage();
    this.resumenGeneralService.loadGeneralSummary(this);
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }

  ngAfterViewInit() {
    const tabIndex = history.state?.sectionIndex;    
      
    if (tabIndex !== undefined) {
      history.replaceState({ ...history.state, sectionIndex: undefined }, '');
      const tabId = `tab${tabIndex}`;
      this.openTab(tabId);
      this.cdr.detectChanges();
    }
  }   
}
