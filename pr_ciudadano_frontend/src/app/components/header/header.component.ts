import { Component, inject, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatBottomSheet } from "@angular/material/bottom-sheet";
import { SafeHtml } from "@angular/platform-browser";
import { ELECTION_IDS, ListaMenu } from "../../helpers/constantes";
import { URL_PATHS_TO_REDIRECT } from "../../settings/app.routes.settings";
import { MenuService } from "../../services/common/menu.service";
import { SnackbarService } from "../../services/elecciones-generales/snackbar.service";
import { MenuMovilComponent } from "../menu-movil/menu-movil.component";
import { PopCompartirComponent } from "../pop-compartir/pop-compartir.component";
import { AccesibilidadComponent } from "../accesibilidad/accesibilidad.component";
import { TranslateService } from '@ngx-translate/core';
import { isRevocatoria } from "../../helpers/storage-helpers/encrypt-storage.helper";
import { Router } from '@angular/router';
import { INFORMACION } from "../../constants/constantes";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrl: "./header.component.scss",
  standalone: false,
})
export class HeaderComponent implements OnInit {
  
  currentLang: string = 'es';
  informacionExpanded = false;

  private snackBar = inject(SnackbarService);

  public infoProcess = "header.info-process";
  public copiarTexto = "header.copiar-texto";

  public isRevocatoria = isRevocatoria();

  classToggled = false;
  rutaBase = URL_PATHS_TO_REDIRECT.resumen;
  CURRENT_ELECTION_IDS = ELECTION_IDS;

  public listaEleccionesMenu: ListaMenu[] = [];
  public eleccionSeleccionada: ListaMenu = {};
  public eleccionPadreSeleccionada: ListaMenu = {};
  public eleccionHijaSeleccionada: ListaMenu = {};

  public languages = [
    { code: 'es', name: 'languages.es', flag: 'assets/flags/es.svg' },
    { code: 'en', name: 'languages.en', flag: 'assets/flags/en.svg' },
    { code: 'qu', name: 'languages.qu', flag: 'assets/flags/qu.svg' },
    { code: 'ay', name: 'languages.ay', flag: 'assets/flags/ay.svg' },
  ];

  constructor(
    private readonly _bottomSheet: MatBottomSheet,
    private readonly dialog: MatDialog,
    private readonly menuService: MenuService,
    private readonly translate: TranslateService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.currentLang = this.translate.currentLang || localStorage.getItem('selectedLang') || 'es';
    this.translate.use(this.currentLang);

    this.listaEleccionesMenu = this.menuService.listaEleccionesMenu;
    this.eleccionSeleccionada = this.menuService.eleccionSeleccionada;
    this.eleccionPadreSeleccionada = this.menuService.eleccionPadreSeleccionada;
    this.eleccionHijaSeleccionada = this.menuService.eleccionHijaSeleccionada;
  }


  getTranslatedLangName(code: string): string {
    const lang = this.languages.find(l => l.code === code);
    return lang ? this.translate.instant(lang.name) : code;
  }

  changeLanguage(lang: string) {
    this.translate.use(lang);
    localStorage.setItem('selectedLang', lang);
    this.currentLang = lang;
  }

  toggleField() {
    this.classToggled = !this.classToggled;
  }

  openBottomSheet(): void {
    this._bottomSheet.open(AccesibilidadComponent, {
      panelClass: "barra-accesibilidad",
    });
  }

  formatNombre(nombre: string): SafeHtml {
    return this.menuService.formatNombre(nombre);
  }

    private readonly infoRoutes = [INFORMACION.FAQ, INFORMACION.SITEMAP];

    public readonly ROUTE_FAQ = URL_PATHS_TO_REDIRECT.faq;
    public readonly ROUTE_SITEMAP = URL_PATHS_TO_REDIRECT.sitemap;

    activarItem(election: ListaMenu): string {
      if (this.infoRoutes.some(route => this.router.url.includes(route))) {
        return '';
      }
      return this.menuService.activarItem(election.id);
    }

  isInformacionActive(): boolean {
    return this.infoRoutes.some(route => this.router.url.includes(route));
  }

  redirectToPadre(electionId: number, eleccionPadreSeleccionada: ListaMenu): void {
    this.menuService.redirectToPadre(electionId, eleccionPadreSeleccionada);
  }

  redirectToHija(electionId: number, eleccionHijaSeleccionada: ListaMenu): void {
    this.menuService.redirectToHija(electionId, eleccionHijaSeleccionada);
  }

  openDialogPopCompartir() {
    const dialogRef = this.dialog.open(PopCompartirComponent, {
      width: "50%",
      maxWidth: "800px",
      maxHeight: "5000px",
      panelClass: "popup-centrado",
    });
    dialogRef.afterClosed().subscribe((result) => { });
  }

  copiarEnlaceURL() {
    const completeCurrentURL = window.location.href;
    navigator.clipboard.writeText(completeCurrentURL).then(() => {
      this.snackBar.showSnackbarWithSuccessMessage("La URL se copió correctamente!");
    });
  }

  openMenuMovil(enterAnimationDuration: string, exitAnimationDuration: string): void {
    this.dialog.open(MenuMovilComponent, {
      panelClass: "menu-movil",
      width: "100%",
      maxWidth: "100%",
      minWidth: "100%",
      enterAnimationDuration,
      exitAnimationDuration,
    });
  }

  changeWithClick(eleccion: ListaMenu, event: Event): void {
    event.preventDefault();
    this.informacionExpanded = false;
    this.listaEleccionesMenu.forEach(e => {
      if (e !== eleccion) {
        e.expanded = false;
      }
    });
    eleccion.expanded = !eleccion.expanded;
    if (!eleccion.expanded) {
      (event.currentTarget as HTMLElement).blur();
    }
  }

  toggleInformacion(event: Event): void {
    event.preventDefault();
    this.listaEleccionesMenu.forEach(e => {
      e.expanded = false;
    });
    this.informacionExpanded = !this.informacionExpanded;
    if (!this.informacionExpanded) {
      (event.currentTarget as HTMLElement).blur();
    }
  }

  selectHija(hijo: ListaMenu, eleccion: ListaMenu): void {
    this.redirectToHija(hijo.id, hijo);
    eleccion.expanded = false;
  }

  collapseInformacion(): void {
    this.informacionExpanded = false;
  }

  onMenuFocusOut(event: FocusEvent, eleccion: ListaMenu): void {
    const relatedTarget = event.relatedTarget as HTMLElement | null;
    const currentTarget = event.currentTarget as HTMLElement;
    if (!relatedTarget || !currentTarget.contains(relatedTarget)) {
      eleccion.expanded = false;
    }
  }

  onInfoFocusOut(event: FocusEvent): void {
    const relatedTarget = event.relatedTarget as HTMLElement | null;
    const currentTarget = event.currentTarget as HTMLElement;
    if (!relatedTarget || !currentTarget.contains(relatedTarget)) {
      this.informacionExpanded = false;
    }
  }

}
