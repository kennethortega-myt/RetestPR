import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { EscalaCabeceraTabla } from './escala-cabecera-tabla.model';
import { LENGTH_ESCALAS, VALOR_PONDERADO } from '../../helpers/constantes';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-escala-cabecera-tabla',
  templateUrl: './escala-cabecera-tabla.component.html',
  styleUrl: './escala-cabecera-tabla.component.scss',
  imports: [CommonModule],
})
export class EscalaCabeceraTablaComponent implements OnChanges {
  escalaTotalVotos: number[] = [];
  valorMaximo: number = 0;
  @Input({ required: true }) registrosParaEscala: EscalaCabeceraTabla[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['registrosParaEscala']) {
      this.calcularEscala(changes['registrosParaEscala'].currentValue);
    }
  }

  private calcularEscala(lista: EscalaCabeceraTabla[]): void {
    if (lista.length == 0) {
      this.cargarDatosPorDefectoEscala();
      return;
    }

    this.escalaTotalVotos = [];
    let valorMax = Math.max(...lista.map((x) => x.votos));
    this.valorMaximo = valorMax;
    if (valorMax == 0) {
      this.cargarDatosPorDefectoEscala();
    } else {
      let divisor = Math.ceil((valorMax * VALOR_PONDERADO) / LENGTH_ESCALAS);
      for (let index = 0; index <= LENGTH_ESCALAS; index++) {
        const valor = divisor * index;
        this.escalaTotalVotos.push(valor);
      }
    }
  }

  private cargarDatosPorDefectoEscala() {
    this.escalaTotalVotos = [0, 500, 1000, 1500, 2000];
  }
}
