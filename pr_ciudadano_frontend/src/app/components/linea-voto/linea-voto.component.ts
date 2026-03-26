import { Component, Input, Output, EventEmitter } from '@angular/core';
import { LineaVotoVotes } from './linea-voto.interface';

@Component({
  selector: 'linea-voto',
  templateUrl: './linea-voto.component.html',
  standalone: false
})
export class LineaVotoComponent {
  @Input() title: string = '';
  @Input() isTotal = false;
  @Input() isPresidencial = false;
  @Input() votes: LineaVotoVotes;
  @Input() percentageOfEmptyAndNullVotes: string = '';
  @Output() votesChange: EventEmitter<LineaVotoVotes> = new EventEmitter<LineaVotoVotes>();
  @Output() percentageOfEmptyAndNullVotesChange: EventEmitter<string> = new EventEmitter<string>();

  public votosEmitidosTra = 'linea-voto.votosEmitidos';
  public votosValidosTra = 'linea-voto.votosValidos';
  public votosTotalesTra = 'linea-voto.votosTotales';
  public cantidadVotosTra = 'linea-voto.cantidadVotos';

  public getPercentageOfEmptyAndNullVotes(): string {
    return this.percentageOfEmptyAndNullVotes;
  }
}
