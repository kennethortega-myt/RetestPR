import { BreakpointObserver } from '@angular/cdk/layout';
import { NgFor } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, computed, OnInit, ViewChild } from '@angular/core';
import { MatDrawer, MatSidenavModule } from '@angular/material/sidenav';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../components/components.module';
import { LoadingService } from '../../components/loading/loading.service';
import { Usuario } from '../../interfaces/login';
import { DrawerService } from '../../services/drawer.service';
import { DataLoginStore } from '../../states/data-login.store';
import { IDrawerMenuItem } from './drawer-menu-options';
import { ResumenGeneralApiService } from '../../services/resumen-general-api.service';
import { CommonService } from '../../services/common/common.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  imports: [ComponentsModule, MatSidenavModule, RouterModule, NgFor, TranslateModule],
  standalone: true
})
export class MainComponent implements OnInit, AfterViewInit {
  @ViewChild('drawer') drawer!: MatDrawer;
  private visibleSubMenus: { [key: string]: boolean } = {};
  readonly usuarioLogin: Usuario;
  readonly drawerMenuList = computed<IDrawerMenuItem[]>(() => {
    const isProcessValid = this.dataLoginStore.isProcessValid();
    if (!isProcessValid) {
      return [];
    }
    const modulos = this.dataLoginStore.modulos();
    const rutas = modulos
      .filter((m) => m.nombre.toLowerCase() !== 'principal')
      .map((m) => ({
        routeName: m.nombre,
        routerLink: `/${m.url}`,
        icon: m.icono || 'icono_no_data.svg'
      }));
    return rutas;
  });
  isMobile: boolean = false;
  isLoading: boolean = false;

  constructor(
    private readonly drawerService: DrawerService,
    private readonly breakpointObserver: BreakpointObserver,
    private readonly router: Router,
    private readonly dataLoginStore: DataLoginStore,
    private readonly loadingService: LoadingService,
    private readonly resumenGeneralApiService: ResumenGeneralApiService,
    private readonly commonService: CommonService,
    private readonly cd: ChangeDetectorRef
  ) {
    this.usuarioLogin = this.dataLoginStore.usuario() ?? {};
  }

  ngOnInit() {
    this.loadProcesoActivo();
    if (this.router.url === '/home' && this.drawerMenuList().length > 0) {
      this.router.navigate([this.drawerMenuList()[0].routerLink]);
    }
    this.loadingService.getAccionLoading().subscribe((isLoading) => {
      Promise.resolve().then(() => {
        this.isLoading = isLoading;
        this.cd.markForCheck();
      });
    });
  }

  private loadProcesoActivo(): void {
    this.resumenGeneralApiService
      .getProcesoElectoralActivo$()
      .pipe()
      .subscribe(({ success, data }) => {
        let activoFechaProceso: boolean = false;

        if (success && data?.activoFechaProceso) {
          activoFechaProceso = data.activoFechaProceso;
        }
        this.commonService.updateIsImageRealDirectory(activoFechaProceso);
      });
  }


  getIconPath(routeName: string): string {
    const iconMap: { [key: string]: string } = {
      Menu: 'icono_no_data',
      Inicio: 'icon-icon_home',
      'Consultas avanzadas': 'icon-icon_consultas',
      'Actas enviadas y devueltas del JEE': 'icon-icon_enviado-recibido',
      'Actas enviadas y recibidas del JEE': 'icon-icon_enviado-recibido',
      'Mis reportes': 'icon-icon_reportes',
      'Reportes automáticos': 'icon-icon_reporte-automaticos',
      'Configuración de reportes automáticos': 'icon-icon_configuracion-reportes',
      'Lista de configuración de reportes': 'icon-icon_lista-configuracion-reportes',
      'Descarga de actas': 'icon-icon_actas',
      'Configuración de descarga de actas': 'icon-icon_configuracion-boletines',
      'Lista de configuración de descarga de actas': 'icon-icon_lista-configuracion-boletines'
    };

    return iconMap[routeName] || 'icono_no_data';
  }

  getTranslationKey(routeName: string): string {
    const translationMap: { [key: string]: string } = {
      'Menu': 'menu.menu',
      'Inicio': 'menu.inicio',
      'Consultas avanzadas': 'menu.consultasAvanzadas',
      'Actas enviadas y devueltas del JEE': 'menu.actasEnviadasJEE',
      'Actas enviadas y recibidas del JEE': 'menu.actasEnviadasJEE',
      'Mis reportes': 'menu.misReportes',
      'Reportes automáticos': 'menu.reportesAutomaticos',
      'Configuración de reportes automáticos': 'menu.configuracionReportes',
      'Lista de configuración de reportes': 'menu.listaConfiguracion',
      'Descarga de actas': 'menu.descargaActas',
      'Configuración de descarga de actas': 'menu.configuracionDescargaActas',
      'Lista de configuración de descarga de actas': 'menu.listaConfiguracionActas'
    };

    return translationMap[routeName] || routeName;
  }

  toggleSubMenu(menu: string): void {
    for (const key in this.visibleSubMenus) {
      if (this.visibleSubMenus.hasOwnProperty(key) && key !== menu) {
        this.visibleSubMenus[key] = false;
      }
    }
    this.visibleSubMenus[menu] = !this.visibleSubMenus[menu];
  }

  isSubMenuVisible(menu: string): boolean {
    return this.visibleSubMenus[menu];
  }

  ngAfterViewInit() {
    this.drawerService.drawerState$.subscribe((isOpen) => {
      if (isOpen) {
        this.drawer.open();
      } else {
        this.drawer.close();
      }
    });

    this.breakpointObserver.observe(['(max-width: 1024px)']).subscribe((result) => {
      this.isMobile = result.matches;
      if (result.matches) {
        this.drawer.close();
      }
    });
  }

  closeDrawer() {
    this.drawerService.closeDrawer();
  }
}
