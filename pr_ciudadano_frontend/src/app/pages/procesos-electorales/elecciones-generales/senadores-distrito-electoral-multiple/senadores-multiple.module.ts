import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../../components/components.module';
import { PipesModule } from '../../../../pipes/pipes.module';
import { RealizarBusquedaComponent } from './realizar-busqueda/realizar-busqueda.component';
import { ResultadoCandidatoComponent } from './resultado-candidato/resultado-candidato.component';
import { ResultadoUbicacionGeograficaComponent } from './resultado-ubicacion-geografica/resultado-ubicacion-geografica.component';
import { SenadoresDistritoElectoralMultipleComponent } from './senadores-distrito-electoral-multiple.component';
import { MatCheckboxModule } from '@angular/material/checkbox';

const COMPONENTS = [
  RealizarBusquedaComponent,
  ResultadoCandidatoComponent,
  ResultadoUbicacionGeograficaComponent,
  SenadoresDistritoElectoralMultipleComponent
];

@NgModule({
  declarations: [...COMPONENTS],
  imports: [
    CommonModule,
    ComponentsModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    PipesModule,
    MatIconModule,
    MatButtonModule,
    MatCheckboxModule
  ],
  exports: [...COMPONENTS]
})
export class SenadoresMultipleModule {}
