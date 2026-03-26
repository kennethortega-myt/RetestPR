import { AfterViewInit, Component, ElementRef, HostListener, inject, Input, OnChanges, Renderer2, SimpleChanges, ViewChild } from '@angular/core';
import { LENGTH_ESCALAS, VALOR_PONDERADO } from '../../helpers/constantes';
import { Acta } from '../../interfaces/output/acta-observada/detalle-acta-observada.model';
import { ModalDetailVotesComponent } from '../modal-detail-votes/modal-detail-votes.component';
import { TablaEscrutinioModeloUno } from './tabla-escrutinio-modelo-uno.model';
import { CommonService } from '../../services/common/common.service';
import { EscalaCabeceraTabla } from '../escala-cabecera-tabla/escala-cabecera-tabla.model';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { MatProgressBar } from '@angular/material/progress-bar';
import { EscalaCabeceraTablaComponent } from '../escala-cabecera-tabla/escala-cabecera-tabla.component';
import { PorcentajeFormatPipe } from '../../pipes/porcentaje-format.pipe';

@Component({
  selector: 'app-tabla-escrutinio-modelo-uno',
  templateUrl: './tabla-escrutinio-modelo-uno.component.html',
  imports: [CommonModule, TranslateModule, MatProgressBar, EscalaCabeceraTablaComponent, PorcentajeFormatPipe],
})
export class TablaEscrutinioModeloUnoComponent implements AfterViewInit, OnChanges {
  readonly dialog = inject(MatDialog);
  private readonly renderer: Renderer2 = inject(Renderer2);
  private readonly commonService: CommonService = inject(CommonService);
  @Input({ required: true }) listaTablaEscrutinioModeloUno: TablaEscrutinioModeloUno[] = [];
  @Input({ required: false }) showAchurados?: boolean = false;
  @Input({ required: false }) detalleActa?: Acta = {} as Acta;
  @ViewChild('header', { static: false }) headerRef!: ElementRef<HTMLElement>;
  @ViewChild('tabla', { static: false }) tablaRef!: ElementRef<HTMLElement>;
  registrosEscalas: EscalaCabeceraTabla[] = [];
  listaTablaEscrutinioModeloUnoPartidos: TablaEscrutinioModeloUno[] = [];
  listaTablaEscrutinioModeloUnoOtros: TablaEscrutinioModeloUno[] = [];
  totalVotosEmitidos: number = 0;
  totalVotosValidos: number = 0;
  valorMaximoVotoEmitido: number = 0;
  getImageCandidate = (dni: string) => this.commonService.getImageCandidate(dni);
  getImagePoliticalOrganization = (code: string) => this.commonService.getImagePoliticalOrganization(code);
  private headerHeight = 0;

  esPantallaChica = window.innerWidth < 960;

  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    this.esPantallaChica = (event.target as Window).innerWidth < 960;
  }

  ngAfterViewInit(): void {
    // Inicializar la altura del header después de que la vista esté lista
    if (this.headerRef) {
      this.headerHeight = this.headerRef.nativeElement.offsetHeight;
    }
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    if (!this.headerRef || !this.tablaRef) return;

    const header = this.headerRef.nativeElement;
    const tabla = this.tablaRef.nativeElement;

    const tablaRect = tabla.getBoundingClientRect();
    this.headerHeight = header.offsetHeight;

    // Si la tabla entra en la vista
    if (tablaRect.top <= 0 && tablaRect.bottom > this.headerHeight) {
      this.setFixedHeader(header, tabla);
    }
    // Si ya se pasa el final de la tabla
    else if (tablaRect.bottom <= this.headerHeight) {
      this.setAbsoluteHeader(header, tabla);
    }
    // Si aún no llegas a la tabla
    else {
      this.resetHeader(header, tabla);
    }
  }

  private setFixedHeader(header: HTMLElement, tabla: HTMLElement): void {
    this.renderer.setStyle(header, 'position', 'fixed');
    this.renderer.setStyle(header, 'top', '0');
    this.renderer.setStyle(header, 'left', '0');
    this.renderer.setStyle(header, 'right', '0');
    this.renderer.setStyle(header, 'z-index', '100');
    this.renderer.setStyle(header, 'background', '#f8f8f8');
    this.renderer.setStyle(header, 'padding', '0 32px');
    this.renderer.setStyle(header, 'width', '100%');
    this.renderer.setStyle(header, 'box-sizing', 'border-box');
    this.renderer.setStyle(tabla, 'margin-top', `${this.headerHeight}px`);
  }

  private setAbsoluteHeader(header: HTMLElement, tabla: HTMLElement): void {
    this.renderer.setStyle(header, 'position', 'absolute');
    this.renderer.setStyle(header, 'top', `${tabla.offsetHeight - this.headerHeight}px`);
    this.renderer.setStyle(header, 'padding', '0');
    this.renderer.setStyle(header, 'width', '100%');
    this.renderer.setStyle(header, 'box-sizing', 'border-box');
    this.renderer.setStyle(tabla, 'margin-top', `${this.headerHeight}px`);

    // Mantener otros estilos del estado fixed
    this.renderer.setStyle(header, 'left', '0');
    this.renderer.setStyle(header, 'right', '0');
    this.renderer.setStyle(header, 'z-index', '100');
    this.renderer.setStyle(header, 'background', '#f8f8f8');
  }

  private resetHeader(header: HTMLElement, tabla: HTMLElement): void {
    this.renderer.setStyle(header, 'position', 'static');
    this.renderer.removeStyle(header, 'top');
    this.renderer.removeStyle(header, 'left');
    this.renderer.removeStyle(header, 'right');
    this.renderer.removeStyle(header, 'z-index');
    this.renderer.removeStyle(header, 'background');
    this.renderer.removeStyle(header, 'padding');
    this.renderer.removeStyle(header, 'width');
    this.renderer.removeStyle(header, 'box-sizing');
    this.renderer.setStyle(tabla, 'margin-top', '0');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['listaTablaEscrutinioModeloUno']) {
      this.registrosEscalas = this.mapearRegistroEscala(
        changes['listaTablaEscrutinioModeloUno'].currentValue
      );
      this.cargarDatosLista();
      this.obtenerValorMaximoVotosEmitidos(this.listaTablaEscrutinioModeloUno);
    }
  }

  cargarDatosLista(): void {
    this.obtenerValorMaximoVotosEmitidos(this.listaTablaEscrutinioModeloUno);

    const orden = [80, 81, 82];
    let listaTemporal = this.listaTablaEscrutinioModeloUno
        .filter(x => !orden.includes(x.idPartidoPolitico))

    this.listaTablaEscrutinioModeloUnoPartidos = listaTemporal.map(item => ({
      ...item,
      porcentajeBarra: this.valorMaximoVotoEmitido
        ? (item.totalVotos / this.valorMaximoVotoEmitido) * 100
        : 0
    }));

    this.totalVotosEmitidos = this.obtenerTotalVotosEmitidos(this.listaTablaEscrutinioModeloUno);

    this.totalVotosValidos = this.obtenerTotalVotosValidos(this.listaTablaEscrutinioModeloUno);

    this.listaTablaEscrutinioModeloUnoOtros =
      this.listaTablaEscrutinioModeloUno
        .filter(x => orden.includes(x.idPartidoPolitico))
        .sort((a, b) => orden.indexOf(a.idPartidoPolitico) - orden.indexOf(b.idPartidoPolitico)).map(item => ({
          ...item,
          porcentajeBarra: this.valorMaximoVotoEmitido
            ? (item.totalVotos / this.valorMaximoVotoEmitido) * 100
            : 0
        }));
  }

  mapearRegistroEscala(lista: TablaEscrutinioModeloUno[]): EscalaCabeceraTabla[] {
    return lista.map((value) => {
      let escalaCabeceraTabla: EscalaCabeceraTabla = new EscalaCabeceraTabla();
      escalaCabeceraTabla.votos = value.totalVotos;
      return escalaCabeceraTabla;
    });
  }

  orderPorPosicionTablaEscrutinio(
    lista: TablaEscrutinioModeloUno[]
  ): TablaEscrutinioModeloUno[] {
    return lista.slice().sort((a, b) => a.posicion - b.posicion);
  }

  orderPorTotalVotosTablaEscrutinio(
    lista: TablaEscrutinioModeloUno[]
  ): TablaEscrutinioModeloUno[] {
    return lista.slice().sort((a, b) => a.totalVotos - b.totalVotos);
  }

  orderPorTotalVotosTablaEscrutinioDesc(
    lista: TablaEscrutinioModeloUno[]
  ): TablaEscrutinioModeloUno[] {
    return lista.slice().sort((a, b) => b.totalVotos - a.totalVotos);
  }

  obtenerTotalVotosEmitidos(lista: TablaEscrutinioModeloUno[]): number {
    let suma = lista.reduce(
      (accumulator, current) => accumulator + current.totalVotos,
      0
    );
    return suma;
  }

  obtenerTotalVotosValidos(lista: TablaEscrutinioModeloUno[]): number {
    let suma = lista.reduce(
      (accumulator, current) => accumulator + current.votosValido,
      0
    );
    return suma;
  }

  obtenerValorMaximoVotosEmitidos(lista: TablaEscrutinioModeloUno[]): void {
    if (!lista?.length) {
      this.valorMaximoVotoEmitido = 0;
      return;
    }

    const maxVotos = Math.max(...lista.map(o => o.totalVotos));
    const divisor = Math.ceil((maxVotos * VALOR_PONDERADO) / LENGTH_ESCALAS);
    this.valorMaximoVotoEmitido = divisor * LENGTH_ESCALAS;
  }
  
  detalleVotos(tabla: TablaEscrutinioModeloUno, tipo?: boolean): void {
    if(this.esPantallaChica){
      this.dialog.open(ModalDetailVotesComponent, {
        data: {
          tablaModel: tabla,
          tipoModel: tipo
        },
        maxHeight: '90vh',
        maxWidth: '90vw',
        width: '90vw',
        panelClass: 'modal-detail-votes-container'
      });
    }
  }
}
