import { Component, OnDestroy, OnInit, HostListener, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { Subject, Subscription, take } from "rxjs";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatTabChangeEvent } from "@angular/material/tabs";

import { CentEducativoBean } from "../../../../interfaces/cent-educativo-bean";
import { DistritoBean } from "../../../../interfaces/distrito";
import { ItemBean } from "../../../../interfaces/item-bean";
import { ProvinciaBean } from "../../../../interfaces/provincia-bean";
import { Resumen } from "../../../../interfaces/resumen-bean";
import { encryptStorageEleccion } from "../../../../settings/encrypt-storage.settings";
import { Ubigeo } from "../../../../interfaces/ubigeo-bean";
import { EleccionesService } from "../../../../services/elecciones-generales/elecciones.service";
import { ResumenGeneralService } from "../../../../services/elecciones-generales/resumen-general.service";
import { URL_PATHS_TO_REDIRECT } from "../../../../settings/app.routes.settings";
import { getCurrentElectionDescriptionTitleBy } from "../../../../helpers/encrypt-storage-eleccion";
import { MenuElectionIconKeys } from "../../../../settings/icon-keys.settings";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";

const currentIconKey: MenuElectionIconKeys = "actas";

@Component({
  selector: "app-actas",
  templateUrl: "./actas.component.html",
  standalone: false,
})
export class ActasComponent implements OnInit, OnDestroy, AfterViewInit {

  tiposActas: boolean = false;
  actasDetalleLista: boolean = false;
  DetalleActas: boolean = false;

  destroy$: Subject<boolean> = new Subject<boolean>();
  procesoElectoral: any;
  idEleccion: any;
  idAmbito: any = 1;
  ingresoTabUno = false;
  ingresoTabDos = false;
  ingresoTabTres = false;
  ingresoTabCuatro = false;
  idProceso = 0;
  resumen: Resumen;
  elecciones: any[];
  totalPagina = 0;

  public electionDescriptionTitle = getCurrentElectionDescriptionTitleBy(currentIconKey);

  public isResponsive = false;
  private readonly breakpointSubscription: Subscription;

  scrollPosition = 0;
  tabWidth = 300;
  canScrollLeft = false;
  canScrollRight = true;
  isMobile = false;

  activeTab: string = "tab1";

  constructor(
    private readonly router: Router,
    public dialog: MatDialog,
    private readonly fb: FormBuilder,
    private readonly eleccionesService: EleccionesService,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly breakpointObserver: BreakpointObserver,    
    private cdr: ChangeDetectorRef) {
    if (!encryptStorageEleccion.getItem("ELECCION_SELECCIONADA")) {
      this.router.navigate([URL_PATHS_TO_REDIRECT.resumen]);
      return;
    }

    this.procesoElectoral = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO"));
    this.idEleccion = JSON.parse(encryptStorageEleccion.getItem("ID_DE_ELECCION_PRINCIPAL"));
    this.idProceso = this.procesoElectoral.id;
    this.listCentEducativo = [];
    
    this.obtenerResumen();
    this.listarElecciones(this.procesoElectoral);

    sessionStorage.removeItem("verMesa");
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnInit() {
    this.tiposActas = true;
    this.actasDetalleLista = false;
    this.DetalleActas = false;

    sessionStorage.removeItem("verMesa");
    
    // Inicializar funcionalidad móvil
    this.checkIfMobile();
    if (this.isMobile) {
      this.updateArrowsState(this.activeTab);
    }
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

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkIfMobile();
    if (!this.isMobile) {
      this.scrollPosition = 0;
    } else {
      this.updateArrowsState(this.activeTab);
    }
  }

  private checkIfMobile() {
    this.isMobile = window.innerWidth <= 768;
  }

  openTab(tabName: string) {
    this.activeTab = tabName;
    
    if (this.isMobile) {
      this.updateArrowsState(tabName);
    }
  }

  scrollTabs(direction: 'left' | 'right') {
    if (!this.isMobile) return;
    
    if (direction === 'left' && this.canScrollLeft) {
      this.scrollPosition = Math.min(0, this.scrollPosition + this.tabWidth);
      
      if (this.activeTab === 'tab2') {
        this.openTab('tab1');
      } else if (this.activeTab === 'tab3') {
        this.openTab('tab2');
      } else if (this.activeTab === 'tab4') {
        this.openTab('tab3');
      }
    } else if (direction === 'right' && this.canScrollRight) {
      this.scrollPosition = Math.max(-250, this.scrollPosition - this.tabWidth);
      
      if (this.activeTab === 'tab1') {
        this.openTab('tab2');
      } else if (this.activeTab === 'tab2') {
        this.openTab('tab3');
      } else if (this.activeTab === 'tab3') {
        this.openTab('tab4');
      }
    }
  }

  private updateArrowsState(activeTab: string) {
    if (!this.isMobile) return;
    
    this.canScrollLeft = this.scrollPosition < 0 || activeTab !== 'tab1';
    this.canScrollRight = this.scrollPosition > -250 || activeTab !== 'tab5';
    
    switch(activeTab) {
      case 'tab1':
        this.scrollPosition = 0;
        this.canScrollLeft = false;
        this.canScrollRight = true;
        break;
      case 'tab2':
        this.scrollPosition = -130;
        this.canScrollLeft = true;
        this.canScrollRight = true;
        break;
      case 'tab3':
        this.scrollPosition = -230;
        this.canScrollLeft = true;
        this.canScrollRight = true;
        break;
      case 'tab4':
        this.scrollPosition = -300;
        this.canScrollLeft = true;
        this.canScrollRight = true;
        break;
    }
  }

  getTransformStyle(): string {
    return this.isMobile ? `translateX(${this.scrollPosition}px)` : 'translateX(0px)';
  }

  listarElecciones(procesoElectoral): void {
    this.eleccionesService
      .listarEleccionesPorIdProcesoElectoral(procesoElectoral.id)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.elecciones = result.data.filter((x) => (x.idEleccion != 0 && x.padre == 0) || x.hijos);
        },
      });
  }

  VerActasdDetalle() {
    this.tiposActas = false;
    this.actasDetalleLista = true;
  }

  VerDetalles() {
    this.DetalleActas = true;
  }

  listDepartamento: Array<ItemBean>;
  listProvincia: Array<ProvinciaBean>;
  listDistrito: Array<DistritoBean>;
  listCentEducativo: Array<CentEducativoBean>;
  ubigeo = "000000";
  seleccionLocal = false;
  departamentos: Ubigeo[];
  provincias: Ubigeo[];
  distritos: Ubigeo[];

  public myFormUbigeo: FormGroup = this.fb.group({
    region: ["1", Validators.required],
    departamento: ["0", Validators.required],
    provincia: ["0", Validators.required],
    distrito: ["0", Validators.required],
    cent_educativo: ["0", Validators.required],
  });

  public myFormActa: FormGroup = this.fb.group({
    acta: ["", Validators.required],
  });

  listaResultado = new Array();

  ActasDetalle() {
    this.router.navigate(["/main/actas/detalle"]);
  }

  verMesas() {
    this.ubigeo =
      this.myFormUbigeo.get("departamento").value +
      this.myFormUbigeo.get("provincia").value +
      this.myFormUbigeo.get("distrito").value;
    if (this.seleccionLocal) {
      sessionStorage.setItem("verMesa", "true");
      this.router.navigate(["/main/actas/detalle"]);
    }
  }

  onRegionChanged(): void {
    this.myFormUbigeo.get("region").valueChanges.subscribe((region) => {
      if (region === "0") {
        this.desabilitarUbigeo();
      } else if (region === "1") {
        this.habilitarUbigeo();
      } else {
        this.desabilitarUbigeo();
        this.seleccionLocal = true;
      }
    });
  }
  habilitarUbigeo(): void {
    this.myFormUbigeo.get("departamento").enable({ emitEvent: false });
    this.myFormUbigeo.get("provincia").enable({ emitEvent: false });
    this.myFormUbigeo.get("distrito").enable({ emitEvent: false });
    this.myFormUbigeo.get("cent_educativo").enable({ emitEvent: false });
  }
  desabilitarUbigeo() {
    this.myFormUbigeo.get("departamento").setValue("0", { emitEvent: false });
    this.myFormUbigeo.get("provincia").setValue("0");
    this.myFormUbigeo.get("distrito").setValue("0");
    this.myFormUbigeo.get("cent_educativo").setValue("0", { emitEvent: false });

    this.myFormUbigeo.get("departamento").disable({ emitEvent: false });
    this.myFormUbigeo.get("provincia").disable({ emitEvent: false });
    this.myFormUbigeo.get("distrito").disable({ emitEvent: false });
    this.myFormUbigeo.get("cent_educativo").disable({ emitEvent: false });
  }

  onLocalVotacionChanged(): void {
    this.myFormUbigeo.get("cent_educativo").valueChanges.subscribe((cent_educativo) => {
      if (
        this.myFormUbigeo.get("cent_educativo").value !== undefined &&
        this.myFormUbigeo.get("cent_educativo").value !== "0"
      )
        this.seleccionLocal = true;
      else this.seleccionLocal = false;
    });
  }

  Mesas() {
    this.router.navigate(["/main/actas/mesa"]);
  }

  onTabChange(event: MatTabChangeEvent) {
    this.ingresoTabUno = false;
    this.ingresoTabDos = false;
    this.ingresoTabTres = false;
    this.ingresoTabCuatro = false;
    if (event.index == 0) {
      this.ingresoTabUno = true;
    } else if (event.index == 1) {
      this.ingresoTabDos = true;
    } else if (event.index == 2) {
      this.ingresoTabTres = true;
    } else if (event.index == 3) {
      this.ingresoTabCuatro = true;
    }
  }

  obtenerResumen() {
    // Loading functionality removed
    let idAmbitoGeografico = 0;
    let idEleccion = this.idEleccion;
    let tipoFiltro = "eleccion";
    this.resumenGeneralService
      .obtenerResumenGeneral(idAmbitoGeografico, idEleccion, tipoFiltro)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.resumen = resp.data;
          this.procesoElectoral = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO"));
          this.idProceso = this.procesoElectoral.id;
          // Loading functionality removed
        },
        error: (err) => {/* Loading functionality removed */},
      });
  }
}