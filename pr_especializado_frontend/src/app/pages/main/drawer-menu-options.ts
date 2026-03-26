import { ROUTE_PATHS } from '../../settings/app-routing.settings';

export interface IDrawerSubmenuItem {
  routerLink: string;
  routeName: string;
}

export interface IDrawerMenuItem {
  routeName: string;
  routerLink?: string;
  icon?: string;
  submenuItems?: IDrawerSubmenuItem[];
}

export const USER_TYPES = {
  admin: 'ADMIN_ME',
  periodist: 'USUARIO_PERIODISTA',
  oopp: 'USUARIO_OOPP',
  other: 'USUARIO_OEA'
};

export const DRAWER_MENU_LIST_ALL: IDrawerMenuItem[] = [
  {
    routeName: 'Menu',
    routerLink: `/home/panel`
  },
  {
    routeName: 'Consultas avanzadas',
    routerLink: `/home/resultado`
  },
  {
    routeName: 'Actas enviadas y devueltas del JEE',
    routerLink: `/home/actas-observadas`
  },
  {
    routeName: 'Mis reportes',
    routerLink: `/home/descargas`
  },
  {
    routeName: 'Reportes automáticos',
    routerLink: `/home/reportes-automaticos`
  },
  {
    routeName: 'Configuración de reportes automáticos',
    routerLink: `/home/${ROUTE_PATHS.configuracion_reportes}`
  },
  {
    routeName: 'Lista de configuración de reportes',
    routerLink: `/home/${ROUTE_PATHS.lista_configuraciones}`
  }
];

export const DRAWER_MENU_LIST_FOR_ADMIN: IDrawerMenuItem[] = [
  {
    routeName: 'Menu',
    routerLink: `/home/panel`
  },
  {
    routeName: 'Configuración de reportes automáticos',
    routerLink: `/home/${ROUTE_PATHS.configuracion_reportes}`
  },
  {
    routeName: 'Lista de configuración de reportes',
    routerLink: `/home/${ROUTE_PATHS.lista_configuraciones}`
  }
];

export const DRAWER_MENU_LIST_FOR_PERIODIST: IDrawerMenuItem[] = [
  {
    routeName: 'Menu',
    routerLink: `/home/panel`
  },
  {
    routeName: 'Consultas avanzadas',
    routerLink: `/home/resultado`
  },
  {
    routeName: 'Actas enviadas y recibidas del JEE',
    routerLink: `/home/actas-observadas`
  },
  {
    routeName: 'Mis reportes',
    routerLink: `/home/descargas`
  },
  {
    routeName: 'Reportes automáticos',
    routerLink: `/home/reportes-automaticos`
  }
];

export const DRAWER_MENU_LIST_FOR_OOPP: IDrawerMenuItem[] = DRAWER_MENU_LIST_FOR_PERIODIST;

export const DRAWER_MENU_LIST_FOR_OTHERS: IDrawerMenuItem[] = DRAWER_MENU_LIST_FOR_PERIODIST;
