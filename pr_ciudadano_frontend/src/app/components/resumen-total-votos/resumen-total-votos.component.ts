import { Component, Input } from "@angular/core";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: "app-resumen-total-votos",
  templateUrl: "./resumen-total-votos.component.html",
  standalone: false,
})
export class ResumenTotalVotosComponent {
  @Input() useAlterTotalCastedVotesKey?: boolean = false;

  public TotVotosValidosKey = 'resumen-total.totalVotosValidados';
  public TotVotosEmitidosKey = 'resumen-total.totalVotosEmitidos';
  public TotVotosEmitidosAlternativoKey = 'resumen-total.totalVotosEmitidosAlternativo';
  public VotosEmitidosKey = 'resumen-total.votosEmitidos';
  public CantidadVotosKey = 'resumen-total.cantidadVotos';
  public TotalVotosEmitidosKey = 'resumen-total.totalVotosEmitidosC';
  public VotosValidadosKey = 'resumen-total.votosValidados';
  public TotalVotosValidadosKey = 'resumen-total.totalVotosValidadosC';
}
