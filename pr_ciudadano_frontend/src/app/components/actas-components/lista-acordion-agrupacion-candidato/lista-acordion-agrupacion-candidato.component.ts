import { AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { take } from 'rxjs';
import { makeScaleValuesFromGenericArray } from '../../../helpers/handler-chart-data.common';
import { Detalle, Mesa } from '../../../interfaces/acta-bean';
import { Candidato } from '../../../interfaces/eleccion-congresal-bean';
import { ModalDetailVotes, ModalDetailVotesCandidate } from '../../../interfaces/modal-detail-votes.interface';
import { ModalDetailVotesService } from '../../../services/common/modal-detail-votes.service';
import { ActasService } from '../../../services/elecciones-generales/actas.service';
import { ModalDetailVotesComponent } from '../../modal-detail-votes/modal-detail-votes.component';
import { getPoliticImageFromAssets, mapWithPoliticImage } from '../../../helpers/get-images.helper';
import { LineaVotoVotes } from '../../linea-voto/linea-voto.interface';
import { LINE_VOTE_DEFAULT_EMPTY_VOTES } from '../../linea-voto/linea-voto.constant';

@Component({
  selector: 'app-lista-acordion-agrupacion-candidato',
  templateUrl: './lista-acordion-agrupacion-candidato.component.html',
  standalone: false
})
export class ListaAcordionAgrupacionCandidatoComponent implements OnInit, AfterViewInit, OnChanges {
  @Input({ required: true }) totalVotosEmitidos: number;
  @Input({ required: true }) totalVotosValidos: number;
  @Input({ required: true }) detalle: Detalle[] = [];
  @Input({ required: true }) idActa: number;
  @Input({ required: true }) detalleActa: Mesa;
  @Input({ required: false }) useAlterTotalCastedVotesKey: boolean = false;
  private readonly defaultEmptyVotes = { ...LINE_VOTE_DEFAULT_EMPTY_VOTES };
  OrdenarPor = 'actas-components.ordenarPor';
  acta = 'actas-components.acta';
  votosText = 'actas-components.votos';
  infoActaElectoralTra = 'actas-components.infoActaElectoral';
  votosEmitidosTra = 'actas-components.votosEmitidos';
  votosValidosTra = 'actas-components.votosValidos';
  votosBlancoTra = 'actas-components.votosBlanco';
  cantidadVotosTra = 'actas-components.cantidadVotos';
  totalVotosTra = 'actas-components.totalVotos';
  emptyVotes: LineaVotoVotes = this.defaultEmptyVotes;
  nullVotes: LineaVotoVotes = this.defaultEmptyVotes;
  impugnedVotes: LineaVotoVotes = this.defaultEmptyVotes;
  totalVotes: LineaVotoVotes = this.defaultEmptyVotes;
  maxValueForScaleName: number[] = [];
  valorMaximo: number = 0;
  escalaTotalVotos: number[];
  candidatos: [Candidato];
  totalVotos: number = 0;
  detalleOrganizacion?: Detalle[];
  detalleNoOrganizacion?: Detalle[];
  orderBy = 'Posicion';
  orderByValue = {
    votos: 'Votos',
    posicion: 'Posicion'
  };
  ctrlToggle = new FormControl(true);

  // Propiedades para detectar pantalla pequeña
  esPantallaChica = window.innerWidth < 960;

  // Nueva propiedad para la GUIA visual (independiente de maxValueForScaleName)
  guiaRegla: number[] = [];

  // Factor configurable: cuánto mayor debe ser el tope respecto al máximo real.
  private FACTOR_TOPE = 1.5;
  private DIVISIONES_GUIA = 4;

  constructor(
    private readonly actasService: ActasService,
    private dialog: MatDialog,
    private readonly modalDetailVotesService: ModalDetailVotesService
  ) {
    // Listener para detectar cambios de tamaño de pantalla
    window.addEventListener('resize', () => {
      this.esPantallaChica = window.innerWidth < 960;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.ctrlToggle.setValue(true)
    if (changes['detalle']) {
      // Mantener la lógica actual para escala/barras (no la cambiamos).
      this.calcularEscala(this.detalle);

      if (this.detalle != undefined) {
        const values = this.detalle.map((d) => d.nvotos);
        // Conservas tu comportamiento original (si depende de makeScaleValuesFromGenericArray)
        this.maxValueForScaleName = makeScaleValuesFromGenericArray(values, 5).reverse();
      }

      // GENERAR SOLO LA GUIA VISUAL (independiente) a partir de los datos recibidos
      this.guiaRegla = this.generarGuiaVisual(this.detalle);
    }

    // REINICIO AL CAMBIAR DE ACTA (DETALLE)
    if (changes['idActa'] && !changes['idActa'].firstChange) {
      this.resetOrdenamiento();
    }
  }

  ngOnInit(): void {
    this.calcularEscala(this.detalle);
    if (this.detalle != undefined) {
      const values = this.detalle.map((d) => d.nvotos);
      this.maxValueForScaleName = makeScaleValuesFromGenericArray(values, 5).reverse();
    }

    // Generar guía visual al inicializar
    this.guiaRegla = this.generarGuiaVisual(this.detalle);

    this.ctrlToggle.valueChanges.subscribe({
      next: (value) => {
        if (!value) {
          this.orderBy = this.orderByValue.posicion;
        } else {
          this.orderBy = this.orderByValue.votos;
        }
        this.toggleOrderVotosPosicion();
      }
    });
  }

  ngAfterViewInit(): void {
    this.loadVotesEmptyNullImpugned();
  }

  private resetOrdenamiento(): void {
    this.orderBy = this.orderByValue.posicion;

    this.ctrlToggle.setValue(true, { emitEvent: false });
    this.orderBy == 'Votos';
  }

  private generarGuiaVisual(lista: Detalle[] | undefined): number[] {
    const divisiones = this.DIVISIONES_GUIA ?? 4;

    if (!lista || lista.length === 0) {
      const fallback: number[] = [];
      for (let i = 0; i <= divisiones; i++) fallback.push(i);
      this.valorMaximo = fallback[fallback.length - 1];
      return fallback;
    }

    // 1) máximo real de votos
    const maxReal = Math.max(...lista.map((x) => x.nvotos || 0));

    // 2) margen mínimo (al menos 1 voto, o 5% del máximo)
    const margin = Math.max(1, Math.ceil(maxReal * 0.05));

    // 3) calcular el step entero mínimo que cubre (maxReal + margin) en 'divisiones' tramos
    let step = Math.ceil((maxReal + margin) / divisiones);

    // 4) calcular tope como múltiplo exacto de divisiones (tope = step * divisiones)
    let valorTope = step * divisiones;

    // 5) asegurar que valorTope > maxReal (por si ceil lo iguala)
    if (valorTope <= maxReal) {
      step += 1;
      valorTope = step * divisiones;
    }

    // 6) construir la guía (enteros)
    const guia: number[] = [];
    for (let i = 0; i <= divisiones; i++) {
      guia.push(Math.round(step * i));
    }

    // 7) almacenar valorTope para uso visual si hace falta
    this.valorMaximo = valorTope;

    return guia;
  }

  // Método simple para abrir modal con detalles de agrupación política
  abrirDetalleVotos(det: Detalle): void {
    const candidates: ModalDetailVotesCandidate[] =
      det.candidato?.map((_) => ({
        list: _.lista,
        fullName: _.nombreCompleto,
        votes: _.votos
      })) || [];

    const data: ModalDetailVotes = {
      // Partido Político
      politicalPartyShow: det.estado !== 20,
      politicalPartyImage: det.estado !== 20 ? getPoliticImageFromAssets(det.ccodigo) : '',

      politicalPartyImageShow: det.estado !== 20,
      politicalPartyName: det.descripcion,
      // Candidato - para agrupación no mostramos candidato específico
      candidateImage: '',
      candidateImageShow: false,
      candidateName: det.totalCandidatos ? `Total de candidatos: ${det.totalCandidatos}` : det.descripcion,
      candidateNameShow: true,
      // Votos
      votesNumber: det.nvotos,
      // Votos Emitidos
      votesEmittedPercentage: det.nporcentajeVotosEmitidos || 0,
      votesEmittedShow: true,
      // Votos Válidos
      votesValidPercentage: det.nporcentajeVotosValidos || 0,
      votesValidShow: det.estado !== 20, // No mostrar para estados especiales
      // Candidatos
      candidatesShow: true,
      candidates
    };

    this.modalDetailVotesService.setData(data);

    if (this.esPantallaChica) {
      // Abrir modal
      this.dialog.open(ModalDetailVotesComponent, {
        width: '400px',
        maxWidth: '80vw',
        maxHeight: '80vh',
        panelClass: 'popup-votos-detalle'
      });
    }
  }

  toggleOrderVotosPosicion(): void {
    const compareVotos = (a: Detalle, b: Detalle) =>
      b.nvotos === a.nvotos ? a.estado - b.estado : b.nvotos - a.nvotos;
    const comparePosicion = (a: Detalle, b: Detalle) => a.nposicion - b.nposicion;
    this.detalle = mapWithPoliticImage(this.detalle);
    this.detalle = this.detalle.sort(this.orderBy === this.orderByValue.votos ? compareVotos : comparePosicion);
    this.orderBy = this.orderBy === 'Posicion' ? 'Votos' : 'Posicion';
  }

  listarCandidato(detalle: Detalle): void {
    const { estado, nagrupacionPolitica } = detalle;
    if (estado == 20) {
      return;
    }

    const agrupacionSeleccionada = this.detalle.find((x) => x.nagrupacionPolitica == nagrupacionPolitica);

    if (agrupacionSeleccionada.candidato) {
      if (this.esPantallaChica) {
        this.abrirDetalleVotos(detalle);
      } else {
        detalle.seleccionado = !detalle.seleccionado;
      }

      return;
    }

    if (agrupacionSeleccionada.candidato == null) {
      let idActa = this.idActa;
      this.actasService
        .listarCandidatosPorAgrupacion(idActa, nagrupacionPolitica)
        .pipe(take(1))
        .subscribe({
          next: (resp) => {
            const candidates: ModalDetailVotesCandidate[] =
              resp.data?.map((candidate) => ({
                list: candidate.lista,
                fullName: candidate.nombreCompleto,
                votes: candidate.votos
              })) || [];

            this.detalle
              .filter((x) => x.nagrupacionPolitica == nagrupacionPolitica)
              .forEach((y) => (y.candidato = resp.data));

            if (this.esPantallaChica) {
              this.abrirDetalleVotos(detalle);
            } else {
              detalle.seleccionado = !detalle.seleccionado;
            }
          }
        });
    }
  }

  calcularEscala(lista: Detalle[]): void {
    this.escalaTotalVotos = [];
    if (lista) {
      let valorMax = Math.max(...lista.map((x) => x.nvotos));
      this.valorMaximo = valorMax;

      if (valorMax == 0) {
        this.escalaTotalVotos.push(0);
        this.escalaTotalVotos.push(500);
        this.escalaTotalVotos.push(1000);
        this.escalaTotalVotos.push(1500);
        this.escalaTotalVotos.push(2000);
      } else {
        let divisor = Math.ceil((valorMax * 1.05) / 4);
        for (let index = 0; index < 5; index++) {
          const valor = divisor * index;
          this.escalaTotalVotos.push(valor);
        }
      }
    }
  }

  calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }

    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    let agregado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }

  getPosicionRegla(_valor: number, index: number): string {
    if (!this.guiaRegla || this.guiaRegla.length <= 1) return '0%';
    const total = this.guiaRegla.length - 1;
    const porcentaje = (index / total) * 100;
    return `${porcentaje}%`;
  }

  getReglaValorIteracion(valor: number, _index?: number): number {
    return Math.round(valor);
  }

  detailTypeVotes(vote: LineaVotoVotes, title: string): void {
    const data: Partial<ModalDetailVotes> = {
      politicalPartyShow: false,
      candidateImageShow: false,
      candidateName: title,
      votesNumber: vote.totalVotosValidos,
      votesEmittedPercentage: vote.porcentajeVotosEmitidos,
      votesValidPercentage: undefined
    };
    this.modalDetailVotesService.setData(data);

    if (this.esPantallaChica) {
      this.dialog.open(ModalDetailVotesComponent, {
        width: '400px',
        maxWidth: '80vw',
        panelClass: 'popup-votos-detalle'
      });
    }
  }

  private loadVotesEmptyNullImpugned(): void {
    const _empty = this.detalle.find((det) => det.nagrupacionPolitica == 80);
    const _null = this.detalle.find((det) => det.nagrupacionPolitica == 81);
    const _impugned = this.detalle.find((det) => det.nagrupacionPolitica == 82);

    this.emptyVotes = {
      porcentajeVotosValidos: _empty.nporcentajeVotosValidos,
      porcentajeVotosEmitidos: _empty.nporcentajeVotosEmitidos,
      totalVotosValidos: _empty.nvotos,
      totalVotosEmitidos: _empty.nvotos
    };
    this.nullVotes = {
      porcentajeVotosValidos: _null.nporcentajeVotosValidos,
      porcentajeVotosEmitidos: _null.nporcentajeVotosEmitidos,
      totalVotosValidos: _null.nvotos,
      totalVotosEmitidos: _null.nvotos
    };
    this.impugnedVotes = {
      porcentajeVotosValidos: _impugned.nporcentajeVotosValidos,
      porcentajeVotosEmitidos: _impugned.nporcentajeVotosEmitidos,
      totalVotosValidos: _impugned.nvotos,
      totalVotosEmitidos: _impugned.nvotos
    };
    this.totalVotes = {
      porcentajeVotosValidos: this.totalVotosValidos > 0 ? 100 : this.totalVotosValidos,
      porcentajeVotosEmitidos: this.totalVotosEmitidos > 0 ? 100 : this.totalVotosEmitidos,
      totalVotosValidos: this.totalVotosValidos,
      totalVotosEmitidos: this.totalVotosEmitidos
    };
  }
}
