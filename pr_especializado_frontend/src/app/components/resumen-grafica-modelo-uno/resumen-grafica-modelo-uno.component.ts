import { Component, Input, ViewChild } from '@angular/core';

import { EnumIdEleccion } from '../../helpers/enums';
import {
  cargarGraficaPieActas,
  cargarGraficaPieParticipacionCiudadanaDos,
} from '../../helpers/funciones';
import { getIconImageForElectionId } from '../../helpers/icon-image-for-election-id';
import { ResumenTotal } from '../../interfaces/output/resumen-total.model';
import { ChartPieComponent } from '../chart-pie/chart-pie.component';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { PorcentajeFormatPipe } from '../../pipes/porcentaje-format.pipe';

@Component({
  selector: 'app-resumen-grafica-modelo-uno',
  templateUrl: './resumen-grafica-modelo-uno.component.html',
  styleUrl: './resumen-grafica-modelo-uno.component.scss',
  imports: [ TranslateModule, CommonModule, PorcentajeFormatPipe]
})
export class ResumenGraficaModeloUnoComponent {
  @ViewChild(ChartPieComponent) chartPieComponent?: ChartPieComponent;
  EnumIdEleccion = EnumIdEleccion;

  @Input() resumenTotal?: ResumenTotal;
  @Input() idTipoEleccion?: number;

  public get iconSrc(): string {
    return getIconImageForElectionId(this.idTipoEleccion);
  }

  cargarDatos(resumenTotal: ResumenTotal) {
    this.resumenTotal = resumenTotal;
    this.chartPieComponent?.cargarChartPie(
      'chartdiv01',
      cargarGraficaPieActas(resumenTotal),
      false
    );
    this.chartPieComponent?.cargarChartPie(
      'chartdiv02',
      cargarGraficaPieParticipacionCiudadanaDos(resumenTotal),
      this.noContieneDatosCorrectosResumen(resumenTotal)
    );
  }

  noContieneDatosCorrectosResumen(resumenTotal: ResumenTotal): boolean {
    return resumenTotal.contabilizadas == 0 && resumenTotal.enviadasJee == 0;
  }
}
