import { ChangeDetectorRef, Component, ElementRef, inject, OnDestroy, OnInit } from "@angular/core";
import {
  getCurrentElectionDescriptionTitleBy,
  getCurrentElectionId,
} from "../../../../helpers/encrypt-storage-eleccion";
import { RequestsService } from "../../../../services/common/requests.service";
import { ParlamentoService, Senadores33ApiUrls } from "../../../../services/elecciones-generales/parlamento.service";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { ParlamentoCommon } from "../commons/parlamento-common";
import { setDistritoUnicoUrlInSessionStorage } from "../../../../helpers/session-storage-helper";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { Subscription } from "rxjs";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";

const currentIconKey: MenuElectionIconKeys = "senadores_unico";

@Component({
  selector: "app-senadores-distrito-nacional-unico",
  templateUrl: "../parlamento-andino/parlamento-andino.component.html",
  standalone: false,
})
export class SenadoresDistritoNacionalUnicoComponent extends ParlamentoCommon implements OnInit, OnDestroy {
  public iconoClass = "icon-ico-senador_deu";
  public electionID = getCurrentElectionId(currentIconKey);
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  private readonly requestService = inject(RequestsService);
  public readonly parlamentoService = new ParlamentoService(this.requestService, Senadores33ApiUrls);

  public isResponsive = false;
  private breakpointSubscription: Subscription;

  public showNombreOP = false;

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
    setDistritoUnicoUrlInSessionStorage();
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
      this.activeTab = tabId;
      this.cdr.detectChanges();
      this.openTab(tabId);
    }
  }
}
