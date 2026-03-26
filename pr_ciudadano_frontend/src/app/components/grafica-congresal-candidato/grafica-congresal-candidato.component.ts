import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatTooltip } from '@angular/material/tooltip';
import { Subscription } from 'rxjs';
import { IBarInfo } from './grafica-congresal-candidato.interfaces';
import { mapWithPoliticImage } from '../../helpers/get-images.helper';

@Component({
  selector: "app-grafica-congresal-candidato",
  templateUrl: "./grafica-congresal-candidato.component.html",
  standalone: false,
})

export class GraficaCongresalCandidatoComponent implements OnDestroy {
  @ViewChild('politicalPartiesArticle')
  politicalPartiesArticle: ElementRef<HTMLElement>;
  @Input() scaleValues: number[] = [];
  multiForPercentage = 1;

  information_for_bars: IBarInfo[] = [];
  @Input() information_for_bars_origin: IBarInfo[] = [];

  @Output() siguiente: EventEmitter<number> = new EventEmitter();
  @Output() anterior: EventEmitter<number> = new EventEmitter();
  @Input({ required: true }) paginaActual: number = 0;
  @Input({ required: true }) totalPagina: number = 0;
  contaPagina: number = 0;
  listaEscalaFinal: number[] = [];
  listaEscalaFinalLinea: number[] = [];
  isMobile = false;
  private activeTooltip: MatTooltip | null = null;
  private readonly breakpointSubscription: Subscription;

  constructor(private readonly breakpointObserver: BreakpointObserver) {
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .subscribe(result => {
        this.isMobile =
          result.breakpoints[Breakpoints.XSmall] ||
          result.breakpoints[Breakpoints.Small];
        this.closeTooltip();
      });
  }

  ngOnInit(): void {
    this.obtenerEscalaValores();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const information_for_bars_origin = changes['information_for_bars_origin'];
    if (information_for_bars_origin) {
      this.obtenerEscalaValores();
    }
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (!this.isMobile) {
      return;
    }

    const target = event.target as HTMLElement;
    const clickedTooltipHost = target.closest('[matTooltip]');
    if (!clickedTooltipHost) {
      this.closeTooltip();
    }
  }

  @HostListener('window:resize')
  onWindowResize(): void {
    this.closeTooltip();
  }

  toggleTooltip(tooltip: MatTooltip, event: Event): void {
    if (!this.isMobile) {
      return;
    }

    event.stopPropagation();

    if (this.activeTooltip && this.activeTooltip !== tooltip) {
      this.activeTooltip.hide(0);
    }

    tooltip.toggle();
    this.activeTooltip = tooltip;
  }

  private closeTooltip(): void {
    if (this.activeTooltip) {
      this.activeTooltip.hide(0);
      this.activeTooltip = null;
    }
  }

  public scrollLeft() {
    this.contaPagina--;
    this.information_for_bars = mapWithPoliticImage(
      this.information_for_bars_origin.filter(x => x.group === this.contaPagina)
    );
  }

  public scrollRight() {
    this.contaPagina++;
    this.information_for_bars = mapWithPoliticImage(
      this.information_for_bars_origin.filter(x => x.group === this.contaPagina)
    );
  }

  obtenerEscalaValores(): void {
    const cantidadEscala = 9;
    const valorMayor = Math.max(
      ...this.information_for_bars_origin.map(m => m.number_of_valid_votes)
    );

    let listEscala: number[] = [];

    if (valorMayor === 0) {
      listEscala = [0,10,20,30,40,50,60,70,80,90,100];

      this.information_for_bars = mapWithPoliticImage(
        this.information_for_bars_origin.filter(x => x.group === this.contaPagina)
      );
      return;
    }

    const multiplo = Math.ceil(valorMayor / cantidadEscala);
    for (let i = 0; i <= cantidadEscala; i++) {
      listEscala.push(i * multiplo);
    }

    const valorMayorEscala = Math.max(...listEscala);

    this.listaEscalaFinal = [...listEscala].reverse();
    this.listaEscalaFinalLinea = this.listaEscalaFinal.slice(0, -1);

    this.information_for_bars_origin = this.information_for_bars_origin.map(x => ({
      ...x,
      percentage_of_valid_votes:
        valorMayor === 0
          ? 0
          : (x.number_of_valid_votes / valorMayorEscala) * 100
    }));

    this.information_for_bars = mapWithPoliticImage(
      this.information_for_bars_origin.filter(x => x.group === this.contaPagina)
    );
  }
  
  handleImageError(event: Event): void {
    const element = event.target as HTMLImageElement;
    element.src = 'assets/img/candidatos/avatar.jpg';
  }
  
}
