import { Component, Input, SimpleChanges } from "@angular/core";
import { Candidato } from "../../interfaces/eleccion-congresal-bean";
import { mapWithPoliticImage } from "../../helpers/get-images.helper";
import { DatosOP } from "../../interfaces/presidenciales.interfaces";

@Component({
  selector: "app-lista-candidato",
  templateUrl: "./lista-candidato.component.html",
  styles: [],
  standalone: false,
})
export class ListaCandidatoComponent {
  valorMaximo: number;
  @Input({ required: true }) listCandidato: Candidato[];
  @Input() compactMode: boolean = false; // Nuevo input para modo compacto
  @Input() datosOrganizacion?: DatosOP; // Nuevo input para la cantidad de votos por OP
  @Input() showLogoOP?: boolean = true; // Nuevo input para mostrar/ocultar el logo (por defecto sí se muestra)
  @Input() showNombreOP?: boolean = true; // Nuevo input para mostrar/ocultar el nombre de la OP
  public maxValueForScaleName: number[] = [];

  public cantidadVotosTra = 'linea-voto.cantidadVotos';
  public votosEmitidos = 'linea-voto.votosEmitidos';
  public votosValidos = 'linea-voto.votosValidos';

  ngAfterViewInit() {
    this.loadImg();
  }

  loadImg(): void {
    if (this.listCandidato?.length) {
      this.listCandidato = mapWithPoliticImage(this.listCandidato)
    }
  }

  ngOnInit(): void {
    this.calcular();
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    const listCandidato = changes["listCandidato"];
    if (!listCandidato.firstChange) {
      this.calcular();
      this.loadImg();
    }
  }

  calcular(): void {
    this.valorMaximo = 0;
    this.valorMaximo = Math.max(...this.listCandidato.map(o => o.totalVotosEmitidos), 0);
  }

  handleImageError(event: Event): void {
    const element = event.target as HTMLImageElement;
    element.src = "assets/img/candidatos/avatar.jpg";
  }
}