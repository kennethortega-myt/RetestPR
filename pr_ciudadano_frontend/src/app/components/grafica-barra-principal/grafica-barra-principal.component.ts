import { Component, Input } from "@angular/core";
import { IChartBarInfo } from "../../interfaces/chart-bar-info.interface";

@Component({
  selector: "app-grafica-barra-principal",
  templateUrl: "./grafica-barra-principal.component.html",
  standalone: false,
})
export class GraficaBarraPrincipalComponent {
  @Input() info: IChartBarInfo = {} as IChartBarInfo;
  @Input() showImageOfCandidate: boolean;
  @Input() highLightTheMostVoted: boolean;
  @Input() index = 0;

  public getPercentageDescriptionForPoliticGroup(text: string): string {
    return text == "" ? "" : ` (${text}%)`;
  }

  public getPercentageWithThreeDecimals(value: number | null): string {
    if (!value) {
      return "0";
    }
    const valueStr = String(value);
    if (valueStr.split(".").length < 2) {
      return valueStr;
    }
    const [valueInteger, valueDecimal] = valueStr.split(".");
    let newValueDecimals: string;
    if (valueDecimal.length == 1) {
      newValueDecimals = valueDecimal + "00";
    } else {
      newValueDecimals = valueDecimal.length == 2 ? valueDecimal + "0" : valueDecimal;
    }
    return valueInteger + "." + newValueDecimals;
  }
}
