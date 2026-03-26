import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { IChartBarInformacion } from './grafica-partidos-eleccion-distrital.interface';

@Component({
  selector: 'app-grafica-partidos-eleccion-distrital',
  templateUrl: './grafica-partidos-eleccion-distrital.component.html',
  standalone: false,
})
export class GraficaPartidosEleccionDistritalComponent {
  @ViewChild('politicalPartiesArticle')
  politicalPartiesArticle: ElementRef<HTMLElement>;

  @Input() information_for_bars: IChartBarInformacion[] = [];
  @Input() scaleValues: number[] = [];

  public scrollLeft() {
    if (this.politicalPartiesArticle) {
      this.politicalPartiesArticle.nativeElement.scrollLeft = 0;
    }
  }

  public scrollRight() {
    if (this.politicalPartiesArticle) {
      this.politicalPartiesArticle.nativeElement.scrollLeft = 1000;
    }
  }
}
