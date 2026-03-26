import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { ACCESSIBILITY_COMPONENTS, ACCESSIBILITY_DIRECTIVES } from './accessibility';
import { ChartMapaComponent } from './chart-mapa/chart-mapa.component';
import { ChartPieComponent } from './chart-pie/chart-pie.component';
import { DisclaimerBrowserComponent } from './disclaimer-browser/disclaimer-browser.component';
import { EscalaCabeceraTablaComponent } from './escala-cabecera-tabla/escala-cabecera-tabla.component';
import { FiltroUbicacionComponent } from './filtro-ubicacion/filtro-ubicacion.component';
import { FiltroComponent } from './filtro/filtro.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { LeyendaFormulaModeloUnoComponent } from './leyenda-formula-modelo-uno/leyenda-formula-modelo-uno.component';
import { LineaTiempoComponent } from './linea-tiempo/linea-tiempo.component';
import { LoadingComponent } from './loading/loading.component';
import { PersonajeMensajeComponent } from './personaje-mensaje/personaje-mensaje.component';
import { ResumenEtiquetaComponent } from './resumen-etiqueta/resumen-etiqueta.component';
import { ResumenGraficaModeloDosComponent } from './resumen-grafica-modelo-dos/resumen-grafica-modelo-dos.component';
import { ResumenGraficaModeloUnoComponent } from './resumen-grafica-modelo-uno/resumen-grafica-modelo-uno.component';
import { TablaEscrutinioModeloDosComponent } from './tabla-escrutinio-modelo-dos/tabla-escrutinio-modelo-dos.component';
import { TablaEscrutinioModeloUnoComponent } from './tabla-escrutinio-modelo-uno/tabla-escrutinio-modelo-uno.component';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    PdfViewerModule,
    MatExpansionModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ChartMapaComponent,
    HeaderComponent,
    FooterComponent,
    LoadingComponent,
    ResumenEtiquetaComponent,
    ResumenGraficaModeloUnoComponent,
    MatSelectModule,
    MatIconModule,
    MatTooltipModule,
    MatDialogModule,
    MatProgressBarModule,
    TranslateModule,
    MatMenuModule,
    FiltroComponent,
    TablaEscrutinioModeloUnoComponent,
    EscalaCabeceraTablaComponent,
    LineaTiempoComponent,
    TablaEscrutinioModeloDosComponent,
    PersonajeMensajeComponent,
    ChartPieComponent,
    ResumenGraficaModeloDosComponent,
    LeyendaFormulaModeloUnoComponent,
    FiltroUbicacionComponent,
    DisclaimerBrowserComponent,
    ...ACCESSIBILITY_COMPONENTS,
    ...ACCESSIBILITY_DIRECTIVES
  ],

  exports: [
    HeaderComponent,
    PersonajeMensajeComponent,
    ChartPieComponent,
    ChartMapaComponent,
    LoadingComponent,
    FooterComponent,
    FiltroComponent,
    FiltroUbicacionComponent,
    ResumenEtiquetaComponent,
    ResumenGraficaModeloDosComponent,
    ResumenGraficaModeloUnoComponent,
    LineaTiempoComponent,
    TablaEscrutinioModeloUnoComponent,
    LeyendaFormulaModeloUnoComponent,
    EscalaCabeceraTablaComponent,
    PdfViewerModule,
    TablaEscrutinioModeloDosComponent,
    DisclaimerBrowserComponent,
    ...ACCESSIBILITY_COMPONENTS,
    ...ACCESSIBILITY_DIRECTIVES
  ]
})
export class ComponentsModule {
  constructor() {}
}
