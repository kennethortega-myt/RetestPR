import { Component, ViewChild } from '@angular/core';

import { ChartPieComponent } from '../chart-pie/chart-pie.component';
import { EnumIdEleccion } from '../../helpers/enums';
import { cargarGraficaPieActasModeloDos } from '../../helpers/funciones';
import { ResumenTotal } from '../../interfaces/output/resumen-total.model';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-resumen-grafica-modelo-dos',
  templateUrl: './resumen-grafica-modelo-dos.component.html',
  imports: [TranslateModule, CommonModule, ChartPieComponent],
})
export class ResumenGraficaModeloDosComponent {
  @ViewChild(ChartPieComponent) chartPieComponent?: ChartPieComponent;
  resumenTotal?: ResumenTotal;
  EnumIdEleccion = EnumIdEleccion;

  cargarDatos(resumenTotal: ResumenTotal) {
    this.resumenTotal = resumenTotal;
    this.chartPieComponent?.cargarChartPie(
      'chartdiv01',
      cargarGraficaPieActasModeloDos(resumenTotal),
      this.noContieneDatosCorrectosResumen(resumenTotal)
    );
  }

  noContieneDatosCorrectosResumen(resumenTotal: ResumenTotal): boolean {
    return resumenTotal.contabilizadas == 0 && resumenTotal.enviadasJee == 0;
  }
}
