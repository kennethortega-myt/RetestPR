import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, Component, HostListener, Input, OnDestroy } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';
import { Subscription } from 'rxjs';
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGrafic } from '../../helpers/responsive-dimentions.helper';
import { IChartBarInfo } from '../../interfaces/chart-bar-info.interface';
import { getTranslatedString } from '../../helpers/basic-helpers/string.helper';
import { getDecimalPipe } from '../../helpers/numbers-helper.common';

@Component({
  selector: 'app-grafica-partidos',
  templateUrl: './grafica-partidos.component.html',
  styleUrls: ['./grafica-partidos.component.scss'],
  standalone: false
})
export class GraficaPartidosComponent implements AfterViewInit, OnDestroy {
  @Input() information_for_bars: IChartBarInfo[] = [];
  @Input() scaleValues: number[] = [];
  @Input() highLightTheMostVoted = false;
  @Input() showImageOfCandidate = false;
  @Input() showOnlyDefaultCandidate = false;
  @Input() hideVotesValidsOnTooltip = false;
  currentNumberOfPage = 0;
  numberOfPages = 0;
  showScrollLeftButton = false;
  showScrollRirghtButton = true;
  barsBySection: IChartBarInfo[] = [];
  resetCurrentNumberOfPage = true;
  isResponsive = false;
  isMobile = false;
  tooltips: MatTooltip[] = [];
  indexActiveBar: number = -1;
  traslateQuantityVotes: string = 'resultado-ubicacion-geografica.cantidad_votos';
  traslateVotesValids: string = 'resultado-ubicacion-geografica.votos_validos';
  private numberOfBarsBySection = 9;
  private readonly breakpointSubscription: Subscription;

  constructor(private readonly breakpointObserver: BreakpointObserver) {
    this.breakpointSubscription = this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).subscribe((result) => {
      this.numberOfBarsBySection = getNumberOfBarsForGrafic(result.breakpoints);
      this.isMobile = result.breakpoints[Breakpoints.XSmall] || result.breakpoints[Breakpoints.Small];
      if (this.information_for_bars.length > 0) {
        this.initialGraficLoad();
      }
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initialGraficLoad();
    }, 50);
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

  initialGraficLoad(): void {
    this.currentNumberOfPage = 0;
    this.numberOfPages = Math.ceil(this.information_for_bars.length / this.numberOfBarsBySection);

    this.showScrollLeftButton = this.currentNumberOfPage != 0;
    this.showScrollRirghtButton = this.currentNumberOfPage < this.numberOfPages - 1;

    this.barsBySection = this.information_for_bars.filter((_, index) => {
      return (
        this.currentNumberOfPage * this.numberOfBarsBySection <= index &&
        index < (this.currentNumberOfPage + 1) * this.numberOfBarsBySection
      );
    });

    // Inicializar tooltips
    this.registerCurrentTooltips();
  }

  scrollLeft(): void {
    this.currentNumberOfPage = this.currentNumberOfPage - 1;

    this.showScrollLeftButton = this.currentNumberOfPage != 0;
    this.showScrollRirghtButton = this.currentNumberOfPage < this.numberOfPages - 1;

    this.barsBySection = this.information_for_bars.filter((_, index) => {
      return (
        this.currentNumberOfPage * this.numberOfBarsBySection <= index &&
        index < (this.currentNumberOfPage + 1) * this.numberOfBarsBySection
      );
    });

    // Cerrar tooltips y limpiar referencias al cambiar de página
    this.closeAllTooltips();
    this.registerCurrentTooltips();
  }

  scrollRight(): void {
    this.currentNumberOfPage = this.currentNumberOfPage + 1;

    this.showScrollLeftButton = this.currentNumberOfPage != 0;
    this.showScrollRirghtButton = this.currentNumberOfPage < this.numberOfPages - 1;

    this.barsBySection = this.information_for_bars.filter((_, index) => {
      return (
        this.currentNumberOfPage * this.numberOfBarsBySection <= index &&
        index < (this.currentNumberOfPage + 1) * this.numberOfBarsBySection
      );
    });

    // Cerrar tooltips y limpiar referencias al cambiar de página
    this.closeAllTooltips();
    this.registerCurrentTooltips();
  }

  getNumberOfList(value: number | null): string {
    if (!value) {
      return '';
    }
    return 'Número: ' + value;
  }

  getPercentageDescriptionForCandidate(text: string): string {
    return text == '' ? '' : ` (${text} %)`;
  }

  getPercentageDescriptionForPoliticGroup(text: string): string {
    return text == '' ? '' : ` (${text} %)`;
  }

  getPercentageWithThreeDecimals(value: number | null): string {
    if (value === null || value === undefined) {
      return '0';
    }

    const valueStr = value.toFixed(3);
    return valueStr.endsWith('.000') ? valueStr.slice(0, -4) : valueStr;
  }

  handleImageError(event: Event): void {
    const element = event.target as HTMLImageElement;
    element.src = 'assets/img/candidatos/avatar.jpg';
  }

  handleImageErrorImgolitic(element: IChartBarInfo): void {
    element.no_have_urlAgrupacionImage = true;
  }

  // Métodos para manejar tooltips en dispositivos móviles
  toggleTooltip(tooltip: MatTooltip, indexActiveBar: number): void {
    if (this.isMobile) {
      this.indexActiveBar = indexActiveBar;
      tooltip.toggle()
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
      ${this.getNumberOfList(info.lista)}`;
    
    return _value + this.generateInfoTooltip(info);
  }

  generateInfoTooltip(info: IChartBarInfo): string {
    const _value = `
      ${getTranslatedString(this.traslateQuantityVotes)}: ${getDecimalPipe(info.number_of_valid_votes, 3)}
    `;
    if (this.hideVotesValidsOnTooltip) {
      return _value;
    } else {
      return _value + this.generateInfoTooltipOnlyVotesValids(info);
    }
  }

  private generateInfoTooltipOnlyVotesValids(info: IChartBarInfo): string {
    return `${getTranslatedString(this.traslateVotesValids)}: ${getDecimalPipe(info.percentage_valid_votes, 3, true)}%`; 
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

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }
}
