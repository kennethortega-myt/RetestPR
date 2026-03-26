import { AfterViewInit, Component, HostListener, Input, OnDestroy, SimpleChanges } from '@angular/core';
import { IChartBarInfo } from '../../interfaces/chart-bar-info.interface';
import { MatTooltip } from '@angular/material/tooltip';
import { Subscription } from 'rxjs';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { BAR_GRAFIC_BREAKPOINTS } from '../../helpers/responsive-dimentions.helper';
import { getTranslatedString } from '../../helpers/basic-helpers/string.helper';
import { getDecimalPipe } from '../../helpers/numbers-helper.common';
import { mapWithPoliticImage } from '../../helpers/get-images.helper';

@Component({
  selector: 'app-grafica-distrito-electoral',
  templateUrl: './grafica-distrito-electoral.component.html',
  standalone: false
})
export class GraficaDistritoElectoralComponent implements AfterViewInit, OnDestroy {
  @Input() information_for_bars: IChartBarInfo[] = [];
  @Input() scaleValues: number[] = [];
  @Input() totalPaginasGrafica: number;
  @Input() set triggerReset(value: boolean) {
    if (value !== undefined) {
      this.reset();
    }
  }
  dimensionInicial: number = 1000;
  contaPagina: number = 0;
  information_for_bars_temporal: IChartBarInfo[] = [];
  listaEscalaFinal: number[] = [];
  listaEscalaFinalLinea: number[] = [];
  traslateQuantityVotes: string = 'resultado-ubicacion-geografica.cantidad_votos';
  traslateVotesValids: string = 'resultado-ubicacion-geografica.votos_validos';
  isMobile = false;
  tooltips: MatTooltip[] = [];
  indexActiveBar: number = -1;
  private readonly breakpointSubscription: Subscription;

  constructor(private readonly breakpointObserver: BreakpointObserver) {
    this.breakpointSubscription = this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).subscribe((result) => {
      this.isMobile = result.breakpoints[Breakpoints.XSmall] || result.breakpoints[Breakpoints.Small];
    });
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll(event: Event): void {
    if (this.isMobile) {
      this.closeAllTooltips();
    }
  }

  @HostListener('window:touchstart', ['$event'])
  onTouchStart(event: Event): void {
    if (this.isMobile) {
      this.closeAllTooltips();
    }
  }

  @HostListener('window:touchmove', ['$event'])
  onTouchMove(event: Event): void {
    if (this.isMobile) {
      this.closeAllTooltips();
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (this.isMobile) {
      this.handleDocumentClick(event);
    }
  }

  @HostListener('window:orientationchange', ['$event'])
  onOrientationChange(event: Event): void {
    if (this.isMobile) {
      this.closeAllTooltips();
    }
  }

  @HostListener('window:resize', ['$event'])
  onWindowResize(event: Event): void {
    if (this.isMobile) {
      this.closeAllTooltips();
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.obtenerEscalaValores();
    }, 50);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const information_for_bars = changes['information_for_bars'];
    if (information_for_bars != undefined) {
      if (information_for_bars.currentValue != undefined) {
        this.obtenerEscalaValores();
      }
    }
  }

  anterior(): void {
    if (this.contaPagina !== 0) {
      this.contaPagina = this.contaPagina - 1;
      this.updateInformationForBarsTemporal();
    }
  }

  siguiente(): void {
    this.contaPagina = this.contaPagina + 1;
    this.updateInformationForBarsTemporal();

    this.registerCurrentTooltips();
  }

  obtenerEscalaValores(): void {
    let cantidadEscala = 9;
    let valorMayor = Math.max(...this.information_for_bars.map((m) => m.number_of_valid_votes));

    let listEscala: number[] = [];

    if (valorMayor == 0) {
      listEscala.push(0);
      listEscala.push(10);
      listEscala.push(20);
      listEscala.push(30);
      listEscala.push(40);
      listEscala.push(50);
      listEscala.push(60);
      listEscala.push(70);
      listEscala.push(80);
      listEscala.push(90);
      listEscala.push(100);
    }

    if (valorMayor > 0) {
      let multiplo = Math.ceil(valorMayor / cantidadEscala);
      let escala = 0;
      for (let index = 0; index <= cantidadEscala; index++) {
        listEscala.push(escala);
        escala = escala + multiplo;
      }
    }
    let valorMayorEscala = Math.max(...listEscala.map((m) => m));
    this.listaEscalaFinal = [...listEscala].reverse();
    this.listaEscalaFinalLinea = this.listaEscalaFinal.slice(0, -1);

    this.information_for_bars = this.information_for_bars.map((x) => {
      x.percentage_for_chart = valorMayor == 0 ? 0 : (x.number_of_valid_votes / valorMayorEscala) * 100;
      return x;
    });
    this.updateInformationForBarsTemporal();
    this.registerCurrentTooltips();
  }

  // Métodos para manejar tooltips en dispositivos móviles
  toggleTooltip(tooltip: MatTooltip, indexActiveBar: number): void {
    if (this.isMobile) {
      if (this.indexActiveBar === indexActiveBar) {
        this.indexActiveBar = -1;
        tooltip.hide();
      } else {
        this.indexActiveBar = indexActiveBar;
        tooltip.toggle();
      }
    }
    this.addTooltip(tooltip);
  }

  // Método para agregar tooltip al array de referencias
  addTooltip(tooltip: MatTooltip): void {
    if (tooltip && !this.tooltips.includes(tooltip)) {
      this.tooltips.push(tooltip);
    }
  }

  generateInfoTooltipComplete(info: IChartBarInfo): string {
    const _value = `
      ${info.name_of_candidate}
      ${getTranslatedString(this.traslateQuantityVotes)}: ${getDecimalPipe(info.number_of_valid_votes, 3)}
      ${getTranslatedString(this.traslateVotesValids)}: ${getDecimalPipe(info.percentage_valid_votes, 3)}%`;

    return _value;
  }

  // Método para registrar todos los tooltips de la sección actual
  private registerCurrentTooltips(): void {
    // Limpiar array de tooltips
    this.tooltips = [];

    // Los tooltips se registrarán automáticamente cuando se haga click
    // ya que addTooltip se llama en cada click
  }

  // Método para cerrar todos los tooltips activos
  private closeAllTooltips(): void {
    if (this.isMobile) {
      this.indexActiveBar = -1;
      this.tooltips.forEach((tooltip) => {
        if (tooltip) {
          tooltip.hide();
        }
      });
    }
  }

  // Manejar clicks en el documento para cerrar tooltips solo si no es en un elemento con tooltip
  private handleDocumentClick(event: Event): void {
    if (this.isMobile) {
      const target = event.target as HTMLElement;

      // Verificar si el click fue en un elemento que tiene tooltip
      const hasTooltip =
        target.closest('[matTooltip]') || target.closest('[mat-raised-button]') || target.hasAttribute('matTooltip');

      // Solo cerrar tooltips si el click NO fue en un elemento con tooltip
      if (!hasTooltip) {
        // Usar setTimeout para permitir que el tooltip se muestre primero si fue clickeado
        setTimeout(() => {
          this.closeAllTooltips();
        }, 100);
      }
    }
  }

  private reset(): void {
    this.contaPagina = 0;
    this.updateInformationForBarsTemporal();
  }

  private updateInformationForBarsTemporal(): void {    
    this.information_for_bars_temporal = mapWithPoliticImage(
      this.information_for_bars.filter(x => x.group === this.contaPagina)
    );
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }
}
