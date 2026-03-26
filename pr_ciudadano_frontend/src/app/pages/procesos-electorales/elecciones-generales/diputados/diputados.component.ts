import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Subject, takeUntil, take, Subscription } from "rxjs";

import { ID_AMBITO_GEOGRAFICO, TIPO_FILTRO, ID_INICIAL_UBIGEO } from "../../../../helpers/constantes";
import {
  getCurrentElectionDescriptionTitleBy,
  getCurrentElectionId,
} from "../../../../helpers/encrypt-storage-eleccion";
import { Participante } from "../../../../interfaces/eleccion-congresal-bean";
import { Region } from "../../../../interfaces/elections.interfaces";
import { Resumen } from "../../../../interfaces/resumen-bean";
import { BehaviorResumenService } from "../../../../services/elecciones-generales/behavior-resumen.service";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { RealizarBusquedaDiputadoComponent } from "./realizar-busqueda-diputado/realizar-busqueda-diputado.component";
import { ResultadoPorCandidatoComponent } from "./resultado-por-candidato/resultado-por-candidato.component";
import { ResultadoPorUbicacionGeograficaComponent } from "./resultado-por-ubicacion-geografica/resultado-por-ubicacion-geografica.component";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";

const currentIconKey: MenuElectionIconKeys = "diputados";

@Component({
  selector: "app-diputados",
  templateUrl: "./diputados.component.html",
  standalone: false,
})
export class DiputadosComponent implements OnInit, OnDestroy, AfterViewInit {
  destroy$: Subject<boolean> = new Subject<boolean>();

  listRegion: Region[] = [];
  listParticipante: Participante[] = [];
  listCandidato: Participante[] = [];
  listOrganizacionPolitica: Participante[] = [];
  totalCandidatos = 98;
  idAmbito: any;
  resumen: Resumen;
  escala: number[];

  public idEleccion = getCurrentElectionId(currentIconKey);
  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  idDistritoElectoralResultadoPorUbicacionGeografica: number;
  idDistritoElectoralResultadoPorCandidato: number;
  idDistritoElectoralRealizarBusqueda: number;

  tempBlob: any;
  pdfSrc: any;
  @ViewChild(ResultadoPorUbicacionGeograficaComponent) child01: ResultadoPorUbicacionGeograficaComponent;
  @ViewChild(RealizarBusquedaDiputadoComponent) child02: RealizarBusquedaDiputadoComponent;
  @ViewChild(ResultadoPorCandidatoComponent) child03: ResultadoPorCandidatoComponent;

  private readonly ngUnsubscribe = new Subject<void>();
  private primeraVezCargaPagina: boolean = true;

  public isResponsive = false;
  private readonly breakpointSubscription: Subscription;

  constructor(
    public dialog: MatDialog,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly el: ElementRef,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly breakpointObserver: BreakpointObserver,
    private cdr: ChangeDetectorRef) {
    this.listRegion = [];
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnInit() {
    if (this.primeraVezCargaPagina) {
      this.obtenerResumen();
      this.primeraVezCargaPagina = false;
    }
    this.behaviorResumenService
      .getActualizarResumen()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe({
        next: (result) => {
          if (result) {
            this.obtenerResumenAsyncrono(result);
          }
        },
      });
  }

  // funcionalidad para los tabs nativos
  activeTab: string = "tab1";

  ngAfterViewInit() {
    const tabIndex = history.state?.sectionIndex;    
      
    if (tabIndex !== undefined) {
      history.replaceState({ ...history.state, sectionIndex: undefined }, '');
      const tabId = `tab${tabIndex}`;
      this.openTab(tabId);
    }
  } 

  openTab(tabName: string) {
    this.activeTab = tabName;
    setTimeout(() => {
      if (tabName === "tab1" && this.child01) {
        this.child01.cargarResumen();
      }
      if (tabName === "tab2" && this.child02) {
        this.child02.cargarResumen();
      }
      if (tabName === "tab3" && this.child03) {
        this.child03.cargarResumen();
      }
    }, 100);
    this.cdr.detectChanges();
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.behaviorResumenService.setActualizarResumen(0);
    this.breakpointSubscription.unsubscribe();
  }

  obtenerResumenAsyncrono(idDistritoELectoral: number) {
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
        },
        complete: () => console.info("complete"),
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
        error: (e) => {
          console.error(e);
        },
        complete: () => console.info("complete"),
      });
  }

  scrollToElement(elementId: string): void {
    const element = this.el.nativeElement.querySelector(`#${elementId}`);
    if (element) {
      element.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  }

  cambioDistritoElectoral01(dato: number): void {
    this.idDistritoElectoralResultadoPorUbicacionGeografica = dato;
  }
  cambioDistritoElectoral02(dato: number): void {
    this.idDistritoElectoralResultadoPorCandidato = dato;
  }
  cambioDistritoElectoral03(dato: number): void {
    this.idDistritoElectoralResultadoPorCandidato = dato;
  }
}
