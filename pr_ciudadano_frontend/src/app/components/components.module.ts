import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { PipesModule } from '../pipes/pipes.module';
import { AccesibilidadComponent } from './accesibilidad/accesibilidad.component';
import { ACTAS_COMPONENTS } from './actas-components';
import { ActasFiltersComponent } from './actas-filters/actas-filters.component';
import { AutocompleteInputComponent } from './autocomplete-input/autocomplete-input.component';
import { BASIC_COMPONENTS } from './basic-components';
import { DisclaimerBrowserComponent } from './disclaimer-browser/disclaimer-browser.component';
import { ElectionTabsComponent } from './election-tabs/election-tabs.component';
import { FooterComponent } from './footer/footer.component';
import { GenericFilterUbigeoComponent } from './generic-filter-ubigeo/generic-filter-ubigeo.component';
import { GraficaBarraPrincipalComponent } from './grafica-barra-principal/grafica-barra-principal.component';
import { GraficaCongresalCandidatoComponent } from './grafica-congresal-candidato/grafica-congresal-candidato.component';
import { GraficaDistritoElectoralComponent } from './grafica-distrito-electoral/grafica-distrito-electoral.component';
import { GraficaImgNombrePartComponent } from './grafica-img-nombre-part/grafica-img-nombre-part.component';
import { GraficaPartidosEleccionDistritalComponent } from './grafica-partidos-eleccion-distrital/grafica-partidos-eleccion-distrital.component';
import { GraficaPartidosComponent } from './grafica-partidos/grafica-partidos.component';
import { GraficaRevocatoriaComponent } from './grafica-revocatoria/grafica-revocatoria.component';
import { HeaderComponent } from './header/header.component';
import { ImgComponent } from './img/img.component';
import { LineaVotoComponent } from './linea-voto/linea-voto.component';
import { ListaCandidatoComponent } from './lista-candidato/lista-candidato.component';
import { ListaDeCandidatosYOrganizacionesResultadoComponent } from './lista-de-candidatos-y-organizaciones-resultado/lista-de-candidatos-y-organizaciones-resultado.component';
import { LoadingComponent } from './loading/loading.component';
import { MainHotMapComponent } from './main-hot-map/main-hot-map.component';
import { MenuMovilComponent } from './menu-movil/menu-movil.component';
import { ModalDetailVotesComponent } from './modal-detail-votes/modal-detail-votes.component';
import { PaginationComponent } from './pagination/pagination.component';
import { PersonOpneNoInformationComponent } from './person-opne-no-information/person-opne-no-information.component';
import { PopCompartirComponent } from './pop-compartir/pop-compartir.component';
import { PopFilterOpComponent } from './pop-filter-op/pop-filter-op.component';
import { PopupActasProcesadasComponent } from './popup-actas-procesadas/popup-actas-procesadas.component';
import { PopupArchivoNoDisponibleComponent } from './popup-archivo-no-disponible/popup-archivo-no-disponible.component';
import { PopupCausalesObservacionComponent } from './popup-causales-observacion/popup-causales-observacion.component';
import { PopupFiltroUbigeoComponent } from './popup-filtro-ubigeo/popup-filtro-ubigeo.component';
import { PopupReportesAutomaticosComponent } from './popup-reportes-automaticos/popup-reportes-automaticos.component';
import { ReporteAutomaticoComponent } from "./reporte-automatico/reporte-automatico.component";
import { ResumenMesasComponent } from './resumen-mesas/resumen-mesas.component';
import { ResumenTotalVotosComponent } from './resumen-total-votos/resumen-total-votos.component';
import { SeccionActasResumenComponent } from './seccion-actas-resumen/seccion-actas-resumen.component';
import { DownloadMessageComponent } from './snackbar-messages/download-message/download-message.component';
import { WarningDownloadMessageComponent } from './snackbar-messages/warning-download-message/warning-download-message.component';
import { TabCandidatoParlamentoComponent } from './tab-candidato-parlamento/tab-candidato-parlamento.component';
import { TabRealizarBusquedaComponent } from './tab-realizar-busqueda/tab-realizar-busqueda.component';
import { TabUbicacionGeograficaParlamentoComponent } from './tab-ubicacion-geografica-parlamento/tab-ubicacion-geografica-parlamento.component';

const COMPONENTS = [
  AccesibilidadComponent,
  ActasFiltersComponent,
  GenericFilterUbigeoComponent,
  PopupFiltroUbigeoComponent,
  PopCompartirComponent,
  MenuMovilComponent,
  MainHotMapComponent,
  SeccionActasResumenComponent,
  HeaderComponent,
  FooterComponent,
  GraficaBarraPrincipalComponent,
  GraficaCongresalCandidatoComponent,
  GraficaDistritoElectoralComponent,
  GraficaImgNombrePartComponent,
  ResumenMesasComponent,
  PaginationComponent,
  PersonOpneNoInformationComponent,
  ResumenMesasComponent,
  ReporteAutomaticoComponent,
  PopupReportesAutomaticosComponent,
  ResumenTotalVotosComponent,
  DownloadMessageComponent,
  WarningDownloadMessageComponent,
  ElectionTabsComponent,
  LineaVotoComponent,
  ListaCandidatoComponent,
  ListaDeCandidatosYOrganizacionesResultadoComponent,
  GraficaPartidosComponent,
  GraficaPartidosEleccionDistritalComponent,
  ImgComponent,
  TabCandidatoParlamentoComponent,
  TabRealizarBusquedaComponent,
  TabUbicacionGeograficaParlamentoComponent,
  MenuMovilComponent,
  AutocompleteInputComponent,
  ModalDetailVotesComponent,
  PopupCausalesObservacionComponent,
  GraficaRevocatoriaComponent,
  PopFilterOpComponent,
  PopupActasProcesadasComponent,
  PopupArchivoNoDisponibleComponent,
  LoadingComponent,
  DisclaimerBrowserComponent,
  ...BASIC_COMPONENTS,
  ...ACTAS_COMPONENTS
];

const MATERIAL_MODULES = [
  MatSelectModule,
  MatSnackBarModule,
  MatTooltipModule,
  MatMenuModule,
  MatButtonModule,
  MatInputModule,
  MatFormFieldModule,
  MatCheckboxModule,
  MatDialogModule,
  MatAutocompleteModule,
  MatIconModule,
  MatExpansionModule,
  MatListModule,
  MatDatepickerModule,
  MatTableModule,
  MatPaginatorModule,
  MatProgressSpinnerModule,
  MatSlideToggleModule
];

@NgModule({
  declarations: [...COMPONENTS],
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    TranslateModule, 
    PdfViewerModule, 
    ...MATERIAL_MODULES, 
    PipesModule, 
    RouterModule
  ],
  exports: [...COMPONENTS]
})
export class ComponentsModule {}
