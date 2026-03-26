import { Component, Input, SimpleChanges } from "@angular/core";
import { getNewMaxValueFromArray } from "../../helpers/handler-chart-data.common";
import { DatosOP, ResultOfParticipantsItem } from "../../interfaces/presidenciales.interfaces";
import { getCandidateImageFromAssets, getPoliticImageFromAssets } from "../../helpers/get-images.helper";

@Component({
  selector: "app-lista-de-candidatos-y-organizaciones-resultado",
  templateUrl: "./lista-de-candidatos-y-organizaciones-resultado.component.html",
  standalone: false,
})
export class ListaDeCandidatosYOrganizacionesResultadoComponent {
  
  public totalVotosKey = 'linea-voto.cantidadVotos';
  
  @Input() resultOfParticipants: ResultOfParticipantsItem[] = [];
  @Input() maxValueForScaleName: number[] = [];
  @Input() showLogoOP?: boolean = true; // Nueva propiedad para definir si se muestra o no la imagen
  @Input() compactMode?: boolean; // Nueva propiedad para determinar si es compact mode o no
  @Input() showNombreOP?: boolean = true;
  @Input() datosOrganizacion?: DatosOP;

  public votosValidos = 'linea-voto.votosValidos';
  public cantidadVotosTra = 'linea-voto.cantidadVotos';
  public votosEmitidos = 'linea-voto.votosEmitidos';
  public valorMaximo: number;

  ngOnInit(): void {
    this.calcular();
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes["resultOfParticipants"]) {
      const resultOfParticipants = changes["resultOfParticipants"];
      if (!resultOfParticipants.firstChange) {
        this.calcular();
      }
    }
  }

  calcular(): void {
    this.valorMaximo = 0;
    this.valorMaximo = Math.max(...this.resultOfParticipants.map(o => o.totalVotosValidos), 0);
  }

  public getPercentageOfEmptyAndNullVotes(totalVotes: number): string {
    if (this.maxValueForScaleName.length == 0) {
      return "0%";
    }
    const maxValue = getNewMaxValueFromArray(this.maxValueForScaleName);
    const percentage = (totalVotes / maxValue) * 100;
    const currentPercentage = percentage > 100 ? 100 : percentage;
    const widthInPer = (maxValue != 0 ? currentPercentage : 0) + "%";
    return widthInPer;
  }

  public getCandidateImage(candidato: ResultOfParticipantsItem) {
    return getCandidateImageFromAssets(candidato.dniCandidato);
  }

  public getPartidoImage(candidato: ResultOfParticipantsItem) {
    return getPoliticImageFromAssets(candidato.codigoAgrupacionPolitica);
  }
}
