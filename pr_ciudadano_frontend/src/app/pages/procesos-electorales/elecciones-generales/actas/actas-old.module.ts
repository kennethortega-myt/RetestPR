import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { ComponentsModule } from '../../../../components/components.module';
import { PipesModule } from '../../../../pipes/pipes.module';
import { ActasObservadasComponent } from './actas-observadas/actas-observadas.component';
import { ActasPorAmbitoComponent } from './actas-por-ambito/actas-por-ambito.component';
import { ActasPorMesaComponent } from './actas-por-mesa/actas-por-mesa.component';
import { ActasResumenComponent } from './actas-resumen/actas-resumen.component';
import { ActasComponent } from './actas.component';

const MATERIAL = [
  MatSelectModule,
  MatFormFieldModule,
  MatInputModule,
  MatSlideToggleModule,
  MatDialogModule,
  MatTooltipModule,
  MatAutocompleteModule
];

const COMPONENTS = [
  ActasComponent,
  ActasObservadasComponent,
  ActasPorAmbitoComponent,
  ActasPorMesaComponent,
  ActasResumenComponent
];

@NgModule({
  declarations: [...COMPONENTS],
  imports: [
    CommonModule,
    ComponentsModule,
    ReactiveFormsModule,
    PdfViewerModule,
    NgxMaskDirective,
    NgxMaskPipe,
    TranslateModule,
    ...MATERIAL,
    PipesModule
  ],
  providers: [DatePipe],
  exports: [...COMPONENTS]
})
export class ActasOldModule {}
