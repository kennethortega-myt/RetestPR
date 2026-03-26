import { Routes } from '@angular/router';
import { getProcesoElectoralAndAllElectionsGuard } from './guards/get-proceso-electoral-and-all-elections.guard';
import { getTipoDeProcesoElectoralGuard } from './guards/get-tipo-de-proceso-electoral.guard';
import { MainComponent } from './main/main.component';
import { Error404Component } from './pages/error-pages/error-404/error-404.component';
import { Error408Component } from './pages/error-pages/error-408/error-408.component';
import { Error500Component } from './pages/error-pages/error-500/error-500.component';
import { Error503Component } from './pages/error-pages/error-503/error-503.component';
import { ErrorGenericComponent } from './pages/error-pages/error-generic/error-generic.component';
import { eleccionesGeneralesRoutes } from './pages/procesos-electorales/elecciones-generales/elecciones-generales.routes';
import { RoutingPageComponent } from './pages/routing-page/routing-page.component';
import { PATHS } from './settings/app.routes.settings';

export const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [getTipoDeProcesoElectoralGuard]
  },
  {
    path: PATHS.main_routing_page,
    component: RoutingPageComponent,
    children: [...eleccionesGeneralesRoutes],
    canActivate: [getProcesoElectoralAndAllElectionsGuard]
  },
  { path: PATHS.pagina_no_encontrada, component: Error404Component },
  { path: PATHS.tiempo_de_espera_agotado, component: Error408Component },
  { path: PATHS.error_en_servidor, component: Error500Component },
  { path: PATHS.servicio_no_disponible, component: Error503Component },
  { path: PATHS.error_inesperado, component: ErrorGenericComponent },
  { path: '**', redirectTo: PATHS.pagina_no_encontrada }
];
