import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { SafeHtml } from "@angular/platform-browser";
import { ELECTION_IDS, ListaMenu } from "../../helpers/constantes";
import { URL_PATHS_TO_REDIRECT } from "../../settings/app.routes.settings";
import { MenuService } from "../../services/common/menu.service";
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
  selector: "app-menu-movil",
  templateUrl: "./menu-movil.component.html",
  standalone: false,
})
export class MenuMovilComponent implements OnInit {

  desplegado = false;
  desplegadoinfo = false;

  togglePanel(): void {
    this.desplegado = !this.desplegado;
  }
  
  toggleInfo(): void {
    this.desplegadoinfo = !this.desplegadoinfo;
  }

  closeInfoMenu(): void {
    this.desplegadoinfo = false;
  }

  public informacionProcesoKey = 'menu.informacionProceso';

  classToggled = false;
  rutaBase = URL_PATHS_TO_REDIRECT.resumen;
  CURRENT_ELECTION_IDS = ELECTION_IDS;

  public listaEleccionesMenu: ListaMenu[] = [];
  public eleccionSeleccionada: ListaMenu = {};
  public eleccionPadreSeleccionada: ListaMenu = {};
  public eleccionHijaSeleccionada: ListaMenu = {};

  constructor(
    private dialog: MatDialog,
    private menuService: MenuService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.listaEleccionesMenu = this.menuService.listaEleccionesMenu;
    this.eleccionSeleccionada = this.menuService.eleccionSeleccionada;
    this.eleccionPadreSeleccionada = this.menuService.eleccionPadreSeleccionada;
    this.eleccionHijaSeleccionada = this.menuService.eleccionHijaSeleccionada;
  }

  toggleField() {
    this.classToggled = !this.classToggled;
  }

  formatNombre(nombre: string): SafeHtml {
    return this.menuService.formatNombre(nombre);
  }

  activarItem(electionId: number): string {
    return this.menuService.activarItem(electionId);
  }

  redirectToPadre(electionId: number, eleccionPadreSeleccionada: any): void {
    this.menuService.redirectToPadre(electionId, eleccionPadreSeleccionada);
    this.toggleField();
    this.dialog.closeAll();
  }

  redirectToHija(electionId: number, eleccionHijaSeleccionada: any): void {
    this.menuService.redirectToHija(electionId, eleccionHijaSeleccionada);
    this.toggleField();
    this.dialog.closeAll();
  }
}
