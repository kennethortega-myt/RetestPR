import { Component, Input } from "@angular/core";
import { Candidato } from "../../../interfaces/acta-bean";
import { getCandidateImageFromAssets } from "../../../helpers/get-images.helper";

@Component({
  selector: "app-deta-lista-candidato",
  templateUrl: "./deta-lista-candidato.component.html",
  standalone: false,
})
export class DetaListaCandidatoComponent {
  @Input({ required: true }) candidatos: [Candidato];

  public votoText = 'deta-lista-candidato.votoText';
  public votosText = 'deta-lista-candidato.votosText';

  public getCandidateImageFromAssets(dniCandidato: string) {
    getCandidateImageFromAssets(dniCandidato);
  }
}
