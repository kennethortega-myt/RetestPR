import { Routes } from '@angular/router';

import { InstalacionPaginadoComponent } from './pages/main/instalacion-paginado/instalacion-paginado.component';
import { InstalacionComponent } from './pages/main/instalacion/instalacion.component';
import { MainComponent } from './pages/main/main.component';
import { PrincipalComponent } from './pages/main/principal/principal.component';
import { SemaforoComponent } from './pages/main/semaforo/semaforo.component';
import { Error404Component } from './pages/error-pages/error-404/error-404.component';
import { checkAuthGuard } from './guards/check-auth.guard';
import { ROUTE_PATHS } from './services/settings/app-routing.settings';
import { ConfigurarReportesComponent } from './pages/main/configurar-reportes/configurar-reportes.component';
import { DetalleReportesComponent } from './pages/main/detalle-reportes/detalle-reportes.component';
import { MaestroComponent } from './pages/main/maestro/maestro.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/inicio', // Redirige a la página de inicio, no al AppComponent
    pathMatch: 'full',
  },
  {
    path: 'login',
    redirectTo: '/inicio', // Redirige a la página de inicio
  },
  {
    path: 'inicio',
    loadComponent: () => import('./pages/inicio/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: ROUTE_PATHS.configuracion_reportes,
    component: ConfigurarReportesComponent,
  },
  {
    path: ROUTE_PATHS.lista_configuraciones,
    component: DetalleReportesComponent,
  },
  {
    path: 'main',
    component: MainComponent,  // AppComponent solo como contenedor para las rutas de /main
    canActivate: [checkAuthGuard], 
    children: [
      {
        path: ROUTE_PATHS.configuracion_reportes,
        component: ConfigurarReportesComponent,
      },
      {
        path: ROUTE_PATHS.lista_configuraciones,
        component: DetalleReportesComponent,
      },
      {
        path: '',
        redirectTo: 'principal',
        pathMatch: 'full',
      },
      {
        path: 'principal',
        component: PrincipalComponent,
      },
      {
        path: 'instalacion',
        component: InstalacionComponent,
      },
      {
        path: 'maestro',
        component: MaestroComponent,
      },
      {
        path: 'instalacion-paginado',
        component: InstalacionPaginadoComponent,
      },
      {
        path: 'semaforo',
        component: SemaforoComponent,
      },
      {
        path: 'pagina-no-encontrada',
        component: Error404Component,
      },
      {
        path: '**',
        redirectTo: 'pagina-no-encontrada',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    component: Error404Component,
  },
  // { path: '**', redirectTo: '/', pathMatch: 'full' },
];
