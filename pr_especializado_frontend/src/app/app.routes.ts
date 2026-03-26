import { Routes } from '@angular/router';
import {
  loginValidationGuard,
  redirectIfAuthenticatedGuard,
} from './guards/login-validation.guard';
import { InicioComponent } from './pages/inicio/inicio.component';
import { RecuperarContrasenaComponent } from './pages/inicio/recuperar-contrasena/recuperar-contrasena.component';
import { ActasObservadasComponent } from './pages/main/actas-observadas/actas-observadas.component';
import { ConfigurarReportesComponent } from './pages/main/administrador/configurar-reportes/configurar-reportes.component';
import { DetalleReportesComponent } from './pages/main/administrador/detalle-reportes/detalle-reportes.component';
import { PanelComponent } from './pages/main/panel/panel.component';
import { DescargasComponent } from './pages/main/descargas/descargas.component';
import { MainComponent } from './pages/main/main.component';
import { ReportesAutomaticosComponent } from './pages/main/reportes-automaticos/reportes-automaticos.component';
import { TablaComponent } from './pages/main/tabla/tabla.component';
import { ROUTE_PATHS } from './settings/app-routing.settings';
import { DescargaActasComponent } from './pages/main/descarga-actas/descarga-actas.component';
import { ConfigurarReporteDescargaActasComponent } from './pages/main/administrador/configurar-reporte-descarga-actas/configurar-reporte-descarga-actas.component';
import { DetalleReportesActasComponent } from './pages/main/administrador/detalle-reportes-actas/detalle-reportes-actas.component';

export const routes: Routes = [
  {
    path: ROUTE_PATHS.home,
    component: MainComponent,
    children: [
      { path: '', redirectTo: 'personaje', pathMatch: 'full' },
      { path: 'inicio', component: PanelComponent },
      { path: 'resultado', component: TablaComponent },
      { path: 'descargas', component: DescargasComponent },
      { path: 'descarga-actas', component: DescargaActasComponent },
      { path: 'actas-observadas', component: ActasObservadasComponent },
      { path: 'reportes-automaticos', component: ReportesAutomaticosComponent },
      {
        path: ROUTE_PATHS.configuracion_reportes,
        component: ConfigurarReportesComponent,
      },
      {
        path: ROUTE_PATHS.lista_configuraciones,
        component: DetalleReportesComponent,
      },
      {
        path: ROUTE_PATHS.configuracion_reportes_actas,
        component: ConfigurarReporteDescargaActasComponent,
      },
      {
        path: ROUTE_PATHS.lista_configuraciones_actas,
        component: DetalleReportesActasComponent,
      }
    ],
    canActivate: [loginValidationGuard],
  },
  {
    path: '',
    component: InicioComponent,
    canActivate: [redirectIfAuthenticatedGuard],
  },
  {
    path: ROUTE_PATHS.recuperar_contrasena,
    component: RecuperarContrasenaComponent,
  },
  { path: '**', redirectTo: ROUTE_PATHS.home },
];
