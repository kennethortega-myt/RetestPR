import { Component, Input } from '@angular/core';
import * as am5 from '@amcharts/amcharts5';
import * as am5percent from '@amcharts/amcharts5/percent';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';

import { ChartPie } from '../../interfaces/chart-pie.model';

@Component({
  selector: 'app-chart-pie',
  templateUrl: './chart-pie.component.html',
})
export class ChartPieComponent {
  @Input({ required: true }) id?: string;

  cargarChartPie(
    id: string,
    listaChartPieDatos: ChartPie[],
    noTieneDatosCorrectos?: boolean
  ): void {
    this.destroyChart(id);
    this.addLicense();
    let root = am5.Root.new(id);
    if (root._logo != undefined) {
      root._logo.dispose();
    }
    root.setThemes([am5themes_Animated.new(root)]);
    let chart = root.container.children.push(am5percent.PieChart.new(root, {}));

        let series = chart.series.push(
      am5percent.PieSeries.new(root, {
        name: 'Series',
        categoryField: 'nombre',
        valueField: 'valor',
      })
    );
    series.slices.template.setAll({
      templateField: 'sliceSettings',
    });

   // Configuración común para labels, ticks y tooltips
    series.labels.template.set('visible', false);
    series.ticks.template.set('visible', false);
    series.slices.template.set('tooltipText', '');
    series.slices.template.set('toggleKey', 'none');

    if (noTieneDatosCorrectos) {
      // Datos para el pie (un solo slice con valor 1 para círculo completo)
      const datosPie = [
        {
          nombre: 'Sin datos',
          valor: 1,
          sliceSettings: {
            fill: am5.color(0xEBEBEB),
            stroke: am5.color(0xCCCCCC),
            strokeWidth: 2
          }
        }
      ];

      series.data.setAll(datosPie);

      // Deshabilitar interacción (no clickeable)
      series.slices.template.set('interactive', false);

      // Ocultar tooltips completamente
      series.slices.template.set('tooltipText', '');

      // Ocultar labels dentro del slice (porcentajes)
      series.labels.template.set('forceHidden', true);

      series.appear();
      chart.appear();
      return;
    }

    series.data.setAll(listaChartPieDatos);
    series.labels.template.set('visible', false);
    series.ticks.template.set('visible', false);
    series.slices.template.set('tooltipText', '');
    series.slices.template.set('toggleKey', 'none');
    series.appear();
    chart.appear();
  }

  private addLicense() {
    if (am5.registry.licenses.length > 0) {
      am5.addLicense('AM5C357384425');
    }
  }
  private destroyChart(nombreDiv: string) {
    am5.array.each(am5.registry.rootElements, function (root) {
      if (root != undefined) {
        if (root.dom != undefined) {
          if (root.dom.id == nombreDiv) {
            root.dispose();
          }
        }
      }
    });
  }
}
