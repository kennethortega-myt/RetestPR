import { AfterViewInit, Component, ElementRef, HostListener, inject, Input, OnChanges, Renderer2, signal, SimpleChanges, ViewChild } from '@angular/core';
import { take } from 'rxjs';
import { VALOR_PONDERADO } from '../../helpers/constantes';
import { Acta } from '../../interfaces/output/acta-observada/detalle-acta-observada.model';
import { ActaApiService } from '../../services/acta-api.service';
import { CommonService } from '../../services/common/common.service';
import { EscalaCabeceraTabla } from '../escala-cabecera-tabla/escala-cabecera-tabla.model';
import { TablaEscrutinioModeloDos } from './tabla-escrutinio-modelo-dos.model';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { EscalaCabeceraTablaComponent } from '../escala-cabecera-tabla/escala-cabecera-tabla.component';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatDialog } from '@angular/material/dialog';
import { ModalDetailVotesComponent } from '../modal-detail-votes/modal-detail-votes.component';
import { PorcentajeFormatPipe } from '../../pipes/porcentaje-format.pipe';

@Component({
  selector: 'app-tabla-escrutinio-modelo-dos',
  templateUrl: './tabla-escrutinio-modelo-dos.component.html',
  styleUrl: './tabla-escrutinio-modelo-dos.component.scss',
  imports: [CommonModule, TranslateModule, EscalaCabeceraTablaComponent, MatAccordion, MatExpansionModule, 
    MatProgressBar, PorcentajeFormatPipe],
})
export class TablaEscrutinioModeloDosComponent implements AfterViewInit, OnChanges {
  private readonly renderer: Renderer2 = inject(Renderer2);
  private readonly actaApiService: ActaApiService = inject(ActaApiService);
  private readonly commonService: CommonService = inject(CommonService);
  readonly dialog = inject(MatDialog);
  @Input({ required: true }) listaTablaEscrutinioModeloDos: TablaEscrutinioModeloDos[] = [];
  @Input({ required: true }) idActa?: number;
  @Input({ required: false }) showAchurados?: boolean = false;
  @Input({ required: false }) detalleActa?: Acta = {} as Acta;
  @ViewChild('header', { static: false }) headerRef!: ElementRef<HTMLElement>;
  @ViewChild('tabla', { static: false }) tablaRef!: ElementRef<HTMLElement>;
  registrosEscalas: EscalaCabeceraTabla[] = [];
  listaTablaEscrutinioModeloDosPartidos: TablaEscrutinioModeloDos[] = [];
  listaTablaEscrutinioModeloDosOtros: TablaEscrutinioModeloDos[] = [];
  totalVotosEmitidos: number = 0;
  totalVotosValidos: number = 0;
  valorMaximoVotoEmitido: number = 0;
  getImagePoliticalOrganization = (code: string) => this.commonService.getImagePoliticalOrganization(code);
  readonly panelOpenState = signal(false);
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
    if (changes['listaTablaEscrutinioModeloDos']) {
      this.registrosEscalas = this.mapearRegistroEscala(
        changes['listaTablaEscrutinioModeloDos'].currentValue
      );
      this.cargarDatosLista();
      this.obtenerValorMaximoVotosEmitidos(this.listaTablaEscrutinioModeloDos);
    }
  }

  mapearRegistroEscala(lista: TablaEscrutinioModeloDos[]): EscalaCabeceraTabla[] {
    return lista.map((value) => {
      let escalaCabeceraTabla: EscalaCabeceraTabla = new EscalaCabeceraTabla();
      escalaCabeceraTabla.votos = value.totalVotos;
      return escalaCabeceraTabla;
    });
  }

  cargarDatosLista(): void {
    const orden = [80, 81, 82];

    let listaTemporal = this.listaTablaEscrutinioModeloDos
        .filter(x => !orden.includes(x.idPartidoPolitico))

    this.listaTablaEscrutinioModeloDosPartidos = listaTemporal;

    this.totalVotosEmitidos = this.obtenerTotalVotosEmitidos(
      this.listaTablaEscrutinioModeloDosPartidos
    );

    this.totalVotosValidos = this.obtenerTotalVotosValidos(this.listaTablaEscrutinioModeloDosPartidos);

    this.listaTablaEscrutinioModeloDosOtros =
      this.listaTablaEscrutinioModeloDos
        .filter(x => orden.includes(x.idPartidoPolitico))
        .sort((a, b) => orden.indexOf(a.idPartidoPolitico) - orden.indexOf(b.idPartidoPolitico));
  }

  orderPorPosicionTablaEscrutinio(
    lista: TablaEscrutinioModeloDos[]
  ): TablaEscrutinioModeloDos[] {
    return lista.slice().sort((a, b) => a.posicion - b.posicion);
  }

  orderPorTotalVotosTablaEscrutinio(
    lista: TablaEscrutinioModeloDos[]
  ): TablaEscrutinioModeloDos[] {
    return lista.slice().sort((a, b) => a.totalVotos - b.totalVotos);
  }

  orderPorTotalVotosTablaEscrutinioDesc(
    lista: TablaEscrutinioModeloDos[]
  ): TablaEscrutinioModeloDos[] {
    return lista.slice().sort((a, b) => b.totalVotos - a.totalVotos);
  }

  obtenerTotalVotosEmitidos(lista: TablaEscrutinioModeloDos[]): number {
    let suma = lista.reduce(
      (accumulator, current) => accumulator + current.totalVotos,
      0
    );
    return suma;
  }

  obtenerTotalVotosValidos(lista: TablaEscrutinioModeloDos[]): number {
    let suma = lista.reduce(
      (accumulator, current) => accumulator + current.votosValido,
      0
    );
    return suma;
  }

  obtenerValorMaximoVotosEmitidos(lista: TablaEscrutinioModeloDos[]): void {
    if (lista.length == 0) {
      this.valorMaximoVotoEmitido = 0;
    }
    this.valorMaximoVotoEmitido = Math.max(...lista.map((o) => o.totalVotos));
  }

  calcularPorcentajeBarra(totalVotos: number): number {
    if (this.valorMaximoVotoEmitido == 0) {
      return 0;
    }
    let calculo =
      (totalVotos / (this.valorMaximoVotoEmitido * VALOR_PONDERADO)) * 100;

    return calculo;
  }

  detalleVotos(event: Event, tabla: TablaEscrutinioModeloDos, tipo: boolean): void {
    if(this.esPantallaChica){
      event.stopPropagation();
      event.preventDefault();
      if(!tipo){        
        this.abrirModal(tabla);
        return;
      }
      
      if(tipo && !tabla.cargoDatos) {
        this.actaApiService
          .obtenerCandidatosPorIdAgrupacion(this.idActa!, tabla.idPartidoPolitico)
          .pipe(take(1))
          .subscribe({
            next: (result) => {
              tabla.candidatos = result.data!;
              tabla.cargoDatos = true;
              this.abrirModal(tabla, false);
            },
            error: () => {
              this.abrirModal(tabla, false);
            }
          });
      } else {
        this.abrirModal(tabla, false);
      }
    }
  }

  private abrirModal(tabla: TablaEscrutinioModeloDos, tipo?: boolean): void {
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

  mostrarDetalle(partido: TablaEscrutinioModeloDos): void {
    if (partido.cargoDatos) {
      return;
    }

    this.actaApiService
      .obtenerCandidatosPorIdAgrupacion(this.idActa!, partido.idPartidoPolitico)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.listaTablaEscrutinioModeloDosPartidos =
            this.listaTablaEscrutinioModeloDosPartidos.map((x) => {
              if (x.idPartidoPolitico == partido.idPartidoPolitico) {
                x.verDetalle = true;
                x.candidatos = result.data!;
                x.cargoDatos = true;
              } else {
                x.verDetalle = false;
              }
              return x;
            });
        },
      });
  }
}
