import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../../components/components.module';
import { PipesModule } from '../../../../pipes/pipes.module';
import { RealizarBusquedaDiputadoComponent } from './realizar-busqueda-diputado/realizar-busqueda-diputado.component';
import { ResultadoPorCandidatoComponent } from './resultado-por-candidato/resultado-por-candidato.component';
import { ResultadoPorUbicacionGeograficaComponent } from './resultado-por-ubicacion-geografica/resultado-por-ubicacion-geografica.component';

const components = [
  RealizarBusquedaDiputadoComponent,
  ResultadoPorCandidatoComponent,
  ResultadoPorUbicacionGeograficaComponent
];

@NgModule({
  declarations: [...components],
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
  exports: [...components]
})
export class DiputOldModule {}
