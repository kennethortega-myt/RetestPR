import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, HostListener, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ACTA_CODIGO_ESTADO, ESTADO_ACTA, ID_ELECCION } from '../../../helpers/constantes';
import { getCandidateImageFromAssets, getPoliticImageFromAssets } from '../../../helpers/get-images.helper';
import { makeScaleValuesFromGenericArray } from '../../../helpers/handler-chart-data.common';
import { Archivo, Content, Detalle, LineaTiempo, Mesa } from '../../../interfaces/acta-bean';
import { ModalDetailVotes } from '../../../interfaces/modal-detail-votes.interface';
import { ModalDetailVotesService } from '../../../services/common/modal-detail-votes.service';
import { LINE_VOTE_DEFAULT_EMPTY_VOTES } from '../../linea-voto/linea-voto.constant';
import { LineaVotoVotes } from '../../linea-voto/linea-voto.interface';
import { ModalDetailVotesComponent } from '../../modal-detail-votes/modal-detail-votes.component';

@Component({
  selector: 'app-acta-presidencial',
  templateUrl: './acta-presidencial.component.html',
  standalone: false
})
export class ActaPresidencialComponent implements OnChanges, OnInit, AfterViewInit {
  @Input() detalleMesa: Mesa;
  @Input() detalle: Detalle[];
  @Input() lineaTiempo: [LineaTiempo];
  @Input() mesaSeleccionado: Content;
  @Input() archivos: [Archivo];
  @Input() numeroDeActa: string;
  @Input() nombreDeActa: string;
  private readonly defaultEmptyVotes = { ...LINE_VOTE_DEFAULT_EMPTY_VOTES };
  valorMaximo: number = 0;
  totalVotosEmitidos: number = 0;
  totalVotosValidos: number = 0;
  totalVotos: number = 0;
  escalaTotalVotos: number[];
  ACTA_CODIGO_ESTADO = ACTA_CODIGO_ESTADO;
  ID_ELECCION = ID_ELECCION;
  orderBy = 'Posicion';
  maxValueForScaleName: number[] = [];
  ctrlToggle = new FormControl(true);
  esPantallaChica = window.innerWidth < 960;
  emptyVotes: LineaVotoVotes = this.defaultEmptyVotes;
  nullVotes: LineaVotoVotes = this.defaultEmptyVotes;
  impugnedVotes: LineaVotoVotes = this.defaultEmptyVotes;
  totalVotes: LineaVotoVotes = this.defaultEmptyVotes;
  totalVotosTra = 'actas-components.totalVotos';
  ACTA_NO_INSTALADA = ESTADO_ACTA;
  
  constructor(
    public datepipe: DatePipe,
    private readonly dialog: MatDialog,
    private readonly modalDetailVotesService: ModalDetailVotesService
  ) {}
  @HostListener('window:resize', ['$event']) onResize(event: any): void {
    this.esPantallaChica = event.target.innerWidth < 960;
  }

  ngOnInit(): void {
    this.ctrlToggle.valueChanges.subscribe({
      next: (value) => {
        if (!value) {
          this.orderBy = 'Posicion';
        } else {
          this.orderBy = 'Votos';
        }
        this.toggleOrderVotosPosicion();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.calcularEscala(this.detalle);
    if (changes) {
      if (changes['detalle'] != undefined) {
        if (this.detalle != undefined) {
          this.detalle = this.detalle.sort((a, b) => {
            return b.nvotos - a.nvotos;
          });
          this.calcularEscala(this.detalle);
          this.calcularTotales();

          const values = this.detalle.map((d) => d.nvotos);
          this.maxValueForScaleName = makeScaleValuesFromGenericArray(values, 5).reverse();
        }
      }
    }
  }

  ngAfterViewInit(): void {
    this.loadVotesEmptyNullImpugned();
  }

  calcularTotales(): void {
    this.totalVotosEmitidos = this.detalle
      .map((t) => t['nporcentajeVotosEmitidos'] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotosValidos = this.detalle
      .map((t) => t['nporcentajeVotosValidos'] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotos = this.detalle.map((t) => t['nvotos'] ?? 0).reduce((acc, value) => acc + value, 0);
  }

  obtenerNombreCandidato(detalle: Detalle): string {
    let candidato = detalle.candidato ? detalle.candidato[0] : null;
    if (candidato) {
      return candidato.nombres + ' ' + candidato.apellidoPaterno + ' ' + candidato.apellidoMaterno;
    } else {
      return '';
    }
  }

  detalleVotos(detail: Detalle): void {
    if (detail?.estado == 20) {
      return;
    }
    if (this.esPantallaChica) {
      const data: Partial<ModalDetailVotes> = {
        politicalPartyImage: this.getPoliticalPartyImage(detail.ccodigo),
        politicalPartyName: detail.descripcion,
        candidateImage: this.getCandidateImage(detail?.candidato[0]?.cdocumentoIdentidad),
        candidateName: this.obtenerNombreCandidato(detail),
        votesNumber: detail.nvotos,
        votesEmittedPercentage: detail.nporcentajeVotosEmitidos,
        votesValidPercentage: detail.nporcentajeVotosValidos
      };
      this.modalDetailVotesService.setData(data);
      this.dialog.open(ModalDetailVotesComponent, {
        width: '400px',
        maxWidth: '80vw',
        panelClass: 'popup-votos-detalle'
      });
    }
  }

  public calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }
    let valorMax = 0;
    if (this.detalle && this.detalle.length > 0) {
      valorMax = Math.max(...this.detalle.map((x) => x.nvotos));
    }

    let agregado = valorMax * 0.05;
    let votoCalculado = valorMax + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }

  private calcularEscala(lista: any[]): void {
    this.escalaTotalVotos = [];
    let valorMax = Math.max(...lista.map((x) => x.nvotos));
    this.valorMaximo = valorMax;
    if (valorMax == 0) {
      this.escalaTotalVotos = [0, 70, 140, 220, 300];
    } else {
      let divisor = Math.ceil((valorMax * 1.05) / 4);
      for (let index = 0; index < 5; index++) {
        const valor = divisor * index;
        this.escalaTotalVotos.push(valor);
      }
    }
  }

  toggleOrderVotosPosicion() {
    const compare = (a: Detalle, b: Detalle, prop: keyof Detalle, secondaryProp: keyof Detalle) =>
      a[prop] === b[prop]
        ? (a[secondaryProp] as number) - (b[secondaryProp] as number)
        : (b[prop] as number) - (a[prop] as number);

    if (this.orderBy === 'Votos') {
      this.detalle = this.detalle.sort((a, b) => compare(a, b, 'nvotos', 'estado'));
    } else {
      this.detalle = this.detalle.sort((a, b) => a.nposicion - b.nposicion);
    }
    this.orderBy = this.orderBy === 'Posicion' ? 'Votos' : 'Posicion';
  }

  public getPosicionRegla(valor: number, index: number): string {    
    if (!this.maxValueForScaleName || this.maxValueForScaleName.length === 0) return '0%';

    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    if (valorMaximo === 0) return '0%';
    let retirado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + retirado;
    let valorRetorno = (valor * 100) / votoCalculado;

    if (this.maxValueForScaleName.length - 1 == index) {
      return '100%';
    }

    return valorRetorno + '%';
  }

  public getReglaValorIteracion(valor: number, index: number): number {
    if (!this.maxValueForScaleName || this.maxValueForScaleName.length === 0) {
        return valor;
    }
      
    const lastIndex = this.maxValueForScaleName.length - 1;
    if (lastIndex === index) {
      const lastValue = this.maxValueForScaleName[lastIndex];
      return valor + (lastValue === 300 ? 0 : 1);
    }
    
    return valor;
  }

  getCandidateImage(dni: string): string {
    return getCandidateImageFromAssets(dni);
  }

  getPoliticalPartyImage(codigoAgrupacionPolitica: string): string {
    return getPoliticImageFromAssets(codigoAgrupacionPolitica);
  }

  detailTypeVotesEmptyNullImpugned(vote: LineaVotoVotes, title: string): void {
    const data: Partial<ModalDetailVotes> = {
      politicalPartyShow: false,
      candidateImageShow: false,
      candidateName: title,
      votesNumber: vote.totalVotosValidos,
      votesEmittedPercentage: vote.porcentajeVotosEmitidos,
      votesValidPercentage: vote.porcentajeVotosValidos
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
      porcentajeVotosValidos: _empty?.nporcentajeVotosValidos ?? 0,
      porcentajeVotosEmitidos: _empty?.nporcentajeVotosEmitidos ?? 0,
      totalVotosValidos: _empty?.nvotos ?? 0,
      totalVotosEmitidos: _empty?.nvotos ?? 0
    };
    this.nullVotes = {
      porcentajeVotosValidos: _null?.nporcentajeVotosValidos ?? 0,
      porcentajeVotosEmitidos: _null?.nporcentajeVotosEmitidos ?? 0,
      totalVotosValidos: _null?.nvotos ?? 0,
      totalVotosEmitidos: _null?.nvotos ?? 0
    };
    this.impugnedVotes = {
      porcentajeVotosValidos: _impugned?.nporcentajeVotosValidos ?? 0,
      porcentajeVotosEmitidos: _impugned?.nporcentajeVotosEmitidos ?? 0,
      totalVotosValidos: _impugned?.nvotos ?? 0,
      totalVotosEmitidos: _impugned?.nvotos ?? 0
    };
    
    const { totalVotosValidos, totalVotosEmitidos } = this.detalleMesa;
    this.totalVotes = {
      porcentajeVotosValidos: totalVotosValidos > 0 ? 100 : totalVotosValidos,
      porcentajeVotosEmitidos: totalVotosEmitidos > 0 ? 100 : totalVotosEmitidos,
      totalVotosValidos: totalVotosValidos,
      totalVotosEmitidos: totalVotosEmitidos
    };
  }
}
