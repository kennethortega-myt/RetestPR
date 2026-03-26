import { Component, Input } from "@angular/core";
import { IChartBarInfo } from "../../interfaces/chart-bar-info.interface";

@Component({
  selector: "app-grafica-img-nombre-part",
  templateUrl: "./grafica-img-nombre-part.component.html",
  standalone: false,
})
export class GraficaImgNombrePartComponent {
  @Input() info: IChartBarInfo = {} as IChartBarInfo;
  @Input() showImageOfCandidate: boolean;
}
