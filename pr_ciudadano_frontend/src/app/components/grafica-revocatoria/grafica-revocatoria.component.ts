import { Component, Input } from "@angular/core";
import { IChartBarInfo } from "../../interfaces/chart-bar-info.interface";
import { Subscription } from "rxjs";
import { BreakpointObserver } from "@angular/cdk/layout";
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGraficGroup } from "../../helpers/responsive-dimentions.helper";
import { Servicebg } from '../../servicebg';
@Component({
  selector: "app-grafica-revocatoria",
  templateUrl: "./grafica-revocatoria.component.html",
  styleUrl: "./grafica-revocatoria.component.scss",
  standalone: false
})
export class GraficaRevocatoriaComponent {
  @Input() title: string = "";
  @Input() information_for_bars: IChartBarInfo[] = [];
  @Input() scaleValues: number[] = [];
  tooltipClass: string = 'custom-tooltip2 background-color-base'; 
  private subscription: Subscription;
  @Input() highLightTheMostVoted = false;

  public currentNumberOfPage = 0;
  public numberOfPages = 0;
  private numberOfBarsBySection = 6;
  public showScrollLeftButton = false;
  public showScrollRirghtButton = true;
  public barsBySection: IChartBarInfo[] = [];

  public resetCurrentNumberOfPage = true;

  public isResponsive = false;
  private breakpointSubscription: Subscription;

  constructor(
    private readonly breakpointObserver: BreakpointObserver,
    private readonly servicebg: Servicebg
  ) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initialGraficLoad();
    }, 50);
  }

  ngOnInit( ) {
    
    this.breakpointSubscription = this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).subscribe((result) => {
      this.numberOfBarsBySection = getNumberOfBarsForGraficGroup(result.breakpoints);
      if (this.information_for_bars.length > 0) {
        this.initialGraficLoad();
      }
    });
    
    this.subscription = this.servicebg.backgroundClass$.subscribe(backgroundClass => {
      this.tooltipClass = 'custom-tooltip2 ' + backgroundClass;
    });
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private initialGraficLoad() {
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
  }

  public scrollLeft() {
    this.currentNumberOfPage = this.currentNumberOfPage - 1;

    this.showScrollLeftButton = this.currentNumberOfPage != 0;
    this.showScrollRirghtButton = this.currentNumberOfPage < this.numberOfPages - 1;

    this.barsBySection = this.information_for_bars.filter((_, index) => {
      return (
        this.currentNumberOfPage * this.numberOfBarsBySection <= index &&
        index < (this.currentNumberOfPage + 1) * this.numberOfBarsBySection
      );
    });
  }

  public scrollRight() {
    this.currentNumberOfPage = this.currentNumberOfPage + 1;

    this.showScrollLeftButton = this.currentNumberOfPage != 0;
    this.showScrollRirghtButton = this.currentNumberOfPage < this.numberOfPages - 1;

    this.barsBySection = this.information_for_bars.filter((_, index) => {
      return (
        this.currentNumberOfPage * this.numberOfBarsBySection <= index &&
        index < (this.currentNumberOfPage + 1) * this.numberOfBarsBySection
      );
    });
  }

  public getNumberOfList(value: number | null): string {
    if (!value) {
      return "";
    }
    return "Nº Lista: " + value;
  }

  public getPercentageDescriptionForCandidate(text: string): string {
    return text == "" ? "" : ` (${text} %)`;
  }

  public getPercentageDescriptionForPoliticGroup(text: string): string {
    return text == "" ? "" : ` (${text} %)`;
  }

  public getPercentageWithThreeDecimals(value: number | null): string {
    if (value === null || value === undefined) {
      return "0";
    }

    const valueStr = value.toFixed(3);
    return valueStr.endsWith(".000") ? valueStr.slice(0, -4) : valueStr;
  }

  handleImageError(event: Event): void {
    const element = event.target as HTMLImageElement;
    element.src = "assets/img/candidatos/avatar.jpg";
  }

  handleImageErrorImgolitic(element: IChartBarInfo): void {
    element.no_have_urlAgrupacionImage = true;
  }
}
