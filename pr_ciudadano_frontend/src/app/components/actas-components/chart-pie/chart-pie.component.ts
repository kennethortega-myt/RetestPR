import { AfterViewInit, Component, Input } from "@angular/core";
import * as am5 from "@amcharts/amcharts5";
import * as am5percent from "@amcharts/amcharts5/percent";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

import { ResumenGeneral } from "../../../interfaces/resumen-general-bean";

@Component({
  selector: "app-chart-pie",
  templateUrl: "./chart-pie.component.html",
  standalone: false,
})
export class ChartPieComponent implements AfterViewInit {
  @Input() cargar: boolean = false;
  @Input() resumenGeneral: ResumenGeneral;
  @Input() id: string;
  constructor() {}

  ngAfterViewInit() {
    this.cargarChartPie(this.id);
  }

  cargarChartPie(id: string) {
    this.destroyChart(id);
    this.addLicense();

    let root = am5.Root.new(id);
    if (root._logo != undefined) {
      root._logo.dispose();
    }
    root.setThemes([am5themes_Animated.new(root)]);

    let chart = root.container.children.push(
      am5percent.PieChart.new(root, {
        radius: am5.percent(98),
        layout: root.horizontalLayout,
      })
    );

    chart.root.dom.style.height = "320px";
    chart.root.dom.style.width = "320px";

    let series = chart.series.push(
      am5percent.PieSeries.new(root, {
        name: "Series",
        categoryField: "country",
        valueField: "sales",
        alignLabels: false,
      })
    );
    series.slices.template.setAll({
      stroke: am5.color(0x000000),
      strokeWidth: 0,
    });
    series.labels.template.set("visible", false);
    series.ticks.template.set("visible", false);
    const data = [
      {
        country: "Contabilizada",
        sales: this.resumenGeneral.actasContabilizadas, // 33
        sliceSettings: {
          fill: am5.color(0x003874),
        },
      },
      {
        country: "Enviadas al JEE",
        sales: this.resumenGeneral.actasObservadasEnviadas, // 33
        sliceSettings: {
          fill: am5.color(0x6db2e2),
        },
      },
      {
        country: "Pendientes",
        sales: this.resumenGeneral.actasPendientes, // 33
        sliceSettings: {
          fill: am5.color(0xececec),
        },
      },
    ];

    series.slices.template.setAll({
      templateField: "sliceSettings",
    });
    series.data.setAll(data);

    series.slices.template.set("tooltipText", "");
    series.slices.template.set("toggleKey", "none");
    series.labels.template.setAll({
      fontSize: 10,
      width: 700,
      height: 700,
    });

    series.appear();
    chart.appear();
  }
  private addLicense() {
    if (am5.registry.licenses.length > 0) {
      am5.addLicense("AM5C357384425");
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
