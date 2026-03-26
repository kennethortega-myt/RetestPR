import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } from "@angular/core";
import { Subject, Subscription, take, takeUntil } from "rxjs";
import { ID_AMBITO_GEOGRAFICO, ID_INICIAL_UBIGEO, TIPO_FILTRO } from "../../../../helpers/constantes";
import {
  getCurrentElectionDescriptionTitleBy,
  getCurrentElectionId,
} from "../../../../helpers/encrypt-storage-eleccion";
import { Resumen } from "../../../../interfaces/resumen-bean";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { BehaviorResumenService } from "../../../../services/elecciones-generales/behavior-resumen.service";

const currentIconKey: MenuElectionIconKeys = "senadores_multiple";

@Component({
  selector: "app-senadores-distrito-electoral-multiple",
  templateUrl: "./senadores-distrito-electoral-multiple.component.html",
  standalone: false,
})
export class SenadoresDistritoElectoralMultipleComponent implements OnInit,OnDestroy, AfterViewInit {
  resumen: Resumen;
  activeTab: string = "tab1";

  public idEleccion = getCurrentElectionId(currentIconKey);
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  public isResponsive = false;
  private breakpointSubscription: Subscription;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly breakpointObserver: BreakpointObserver,
    private cdr: ChangeDetectorRef,
    private readonly behaviorResumenService: BehaviorResumenService) {
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.obtenerResumen();
    this.behaviorResumenService
      .getActualizarResumen()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe({
        next: (result) => {
          if (result) {
            this.obtenerResumenFromTabSection(result);
          }
        },
      });
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

  obtenerResumenFromTabSection(idDistritoELectoral: number) {
    let idUbigeoDepartamento: number = 0;
    let idUbigeoProvincia: number = 0;
    let idUbigeoDistrito: number = 0;
    this.resumenGeneralService
      .obtenerResumenGeneral(
        ID_AMBITO_GEOGRAFICO.ID_SIN_AMBITO_GEOGRAFICO,
        this.idEleccion,
        TIPO_FILTRO.DISTRITO_ELECTORAL,
        idDistritoELectoral,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito
      )
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.resumen = resp.data;
        },
        error: (e) => {
          console.error(e);
        }
      });
  }

  obtenerResumen() {
    let idUbigeoDepartamento: number = 0;
    let idUbigeoProvincia: number = 0;
    let idUbigeoDistrito: number = 0;
    this.resumenGeneralService
      .obtenerResumenGeneral(
        ID_AMBITO_GEOGRAFICO.ID_SIN_AMBITO_GEOGRAFICO,
        this.idEleccion,
        TIPO_FILTRO.DISTRITO_ELECTORAL,
        ID_INICIAL_UBIGEO.ID_DISTRITO_LIMA,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito
      )
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.resumen = resp.data;
        },
        error: (err) => {
          console.error("Error al cargar datos", err);
        }
      });
  }

  openTab(tabName: string): void {
    this.activeTab = tabName;
  }
}
