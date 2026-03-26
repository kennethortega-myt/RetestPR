import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../components/components.module';
import { PipesModule } from '../../../pipes/pipes.module';
import { ActaApiService } from '../../../services/elecciones-generales/acta-api.service';
import { ActasService } from '../../../services/elecciones-generales/actas.service';
import { BehaviorResumenService } from '../../../services/elecciones-generales/behavior-resumen.service';
import { EleccionCongresalApiService } from '../../../services/elecciones-generales/eleccion-congresal-api.service';
import { EleccionCongresalService } from '../../../services/elecciones-generales/eleccion-congresal.service';
import { EleccionesApiService } from '../../../services/elecciones-generales/elecciones-api.service';
import { EleccionesService } from '../../../services/elecciones-generales/elecciones.service';
import { ElectionsApiService } from '../../../services/elecciones-generales/elections-api.service';
import { ElectionsService } from '../../../services/elecciones-generales/elections.service';
import { HotMapService } from '../../../services/elecciones-generales/hot-map.service';
import { MesaApiService } from '../../../services/elecciones-generales/mesa-api.service';
import { MesaService } from '../../../services/elecciones-generales/mesa.service';
import { MesasDeVotacionService } from '../../../services/elecciones-generales/mesas-de-votacion.service';
import { PadronApiService } from '../../../services/elecciones-generales/padron-api.service';
import { PadronService } from '../../../services/elecciones-generales/padron.service';
import { ParlamentoService } from '../../../services/elecciones-generales/parlamento.service';
import { ParticipacionCiudadanaApiService } from '../../../services/elecciones-generales/participacion-ciudadana-api.service';
import { ParticipacionCiudadanaService } from '../../../services/elecciones-generales/participacion-ciudadana.service';
import { PresidencialesStoreService } from '../../../services/elecciones-generales/presidenciales-store.service';
import { PresidencialesService } from '../../../services/elecciones-generales/presidenciales.service';
import { RandomImageService } from '../../../services/elecciones-generales/random-image.service';
import { ReportManagerService } from '../../../services/elecciones-generales/report-manager.service';
import { ReporteApiService } from '../../../services/elecciones-generales/reporte-api.service';
import { ReporteWatcherService } from '../../../services/elecciones-generales/reporte-watcher.service';
import { ReporteService } from '../../../services/elecciones-generales/reporte.service';
import { ResumenGeneralApiService } from '../../../services/elecciones-generales/resumen-general-api.service';
import { ResumenGeneralService } from '../../../services/elecciones-generales/resumen-general.service';
import { SenadoresDistritoElectoralMultipleApiService } from '../../../services/elecciones-generales/senadores-distrito-electoral-multiple-api.service';
import { SenadoresDistritoElectoralMultipleService } from '../../../services/elecciones-generales/senadores-distrito-electoral-multiple.service';
import { SnackbarService } from '../../../services/elecciones-generales/snackbar.service';
import { UbigeoApiService } from '../../../services/elecciones-generales/ubigeo-api.service';
import { UbigeoStoreService } from '../../../services/elecciones-generales/ubigeo-store.service';
import { UbigeoService } from '../../../services/elecciones-generales/ubigeo.service';
import { DiputadosComponent } from './diputados/diputados.component';
import { eleccionesGeneralesRoutes } from './elecciones-generales.routes';
import { ParlamentoAndinoComponent } from './parlamento-andino/parlamento-andino.component';
import { PartCiudComponent } from './participacion-ciudadana/part-ciud/part-ciud.component';
import { ParticipacionCiudadanaComponent } from './participacion-ciudadana/participacion-ciudadana.component';
import { ActasOldModule } from './actas/actas-old.module';
import { DiputOldModule } from './diputados/diput-old.module';
import { PresidOldModule } from './presidenciales/presid-old.module';
import { SenadoresMultipleModule } from './senadores-distrito-electoral-multiple/senadores-multiple.module';
import { DistritalElectionComponent } from './resumen-general/distrital-election/distrital-election.component';
import { ResumenGeneralComponent } from './resumen-general/resumen-general.component';
import { SenadoresDistritoNacionalUnicoComponent } from './senadores-distrito-nacional-unico/senadores-distrito-nacional-unico.component';
import { PreguntasFrecuentesComponent } from './preguntas-frecuentes/preguntas-frecuentes.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { SitemapComponent } from './sitemap/sitemap.component';
import { ReportesAutomaticosComponent } from './reportes-automaticos/reportes-automaticos.component';

const COMPONENTS = [
  DiputadosComponent,
  ParlamentoAndinoComponent,
  ParticipacionCiudadanaComponent,
  PartCiudComponent,
  ResumenGeneralComponent,
  DistritalElectionComponent,
  SenadoresDistritoNacionalUnicoComponent,
  PreguntasFrecuentesComponent,
  SitemapComponent,
  ReportesAutomaticosComponent
];

const MATERIAL_MODULES = [
  MatTabsModule,
  MatButtonModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatCheckboxModule,
  MatDialogModule,
  MatSnackBarModule,
  MatTooltipModule,
  MatMenuModule,
  MatAutocompleteModule,
  MatIconModule,
  MatProgressSpinnerModule, 
  MatExpansionModule
];

@NgModule({
  declarations: [...COMPONENTS],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(eleccionesGeneralesRoutes),
    LayoutModule,
    TranslateModule,
    ComponentsModule,
    PipesModule,
    ActasOldModule,
    DiputOldModule,
    PresidOldModule,
    SenadoresMultipleModule,
    ...MATERIAL_MODULES
  ],
  providers: [
    ActaApiService,
    ActasService,
    BehaviorResumenService,
    EleccionCongresalApiService,
    EleccionCongresalService,
    EleccionesApiService,
    EleccionesService,
    ElectionsApiService,
    ElectionsService,
    HotMapService,
    MesaApiService,
    MesaService,
    MesasDeVotacionService,
    PadronApiService,
    PadronService,
    ParlamentoService,
    ParticipacionCiudadanaApiService,
    ParticipacionCiudadanaService,
    PresidencialesStoreService,
    PresidencialesService,
    RandomImageService,
    ReportManagerService,
    ReporteApiService,
    ReporteWatcherService,
    ReporteService,
    ResumenGeneralApiService,
    ResumenGeneralService,
    SenadoresDistritoElectoralMultipleApiService,
    SenadoresDistritoElectoralMultipleService,
    SnackbarService,
    UbigeoApiService,
    UbigeoStoreService,
    UbigeoService
  ],
  exports: [...COMPONENTS]
})
export class EleccionesGeneralesModule {}
