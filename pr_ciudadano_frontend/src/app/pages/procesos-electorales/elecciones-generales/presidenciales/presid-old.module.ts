import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core'; // , inject
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../../components/components.module';
import { PipesModule } from '../../../../pipes/pipes.module';
import { TabOrganizacionPoliticaPresidencialesResComponent } from './tab-organizacion-politica-presidenciales-res/tab-organizacion-politica-presidenciales-res.component';
import { TabOrganizacionPoliticaPresidencialesComponent } from './tab-organizacion-politica-presidenciales/tab-organizacion-politica-presidenciales.component';
import { TabUbicacionGeograficaPresidencialesComponent } from './tab-ubicacion-geografica-presidenciales/tab-ubicacion-geografica-presidenciales.component';
import { PresidencialesComponent } from './presidenciales.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [
    TabUbicacionGeograficaPresidencialesComponent,
    TabOrganizacionPoliticaPresidencialesComponent,
    TabOrganizacionPoliticaPresidencialesResComponent,
    TabUbicacionGeograficaPresidencialesComponent,
    PresidencialesComponent
  ],
  imports: [
    CommonModule,
    ComponentsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    TranslateModule,
    PipesModule,
    MatIconModule,
    MatButtonModule
  ],
  exports: [
    TabUbicacionGeograficaPresidencialesComponent,
    TabOrganizacionPoliticaPresidencialesComponent,
    TabOrganizacionPoliticaPresidencialesResComponent,
    TabUbicacionGeograficaPresidencialesComponent,
    PresidencialesComponent
  ]
})
export class PresidOldModule {}
