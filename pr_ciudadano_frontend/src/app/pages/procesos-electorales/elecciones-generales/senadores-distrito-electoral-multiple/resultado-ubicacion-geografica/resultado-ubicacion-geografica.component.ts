import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
  HostListener
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subject, pairwise, startWith, takeUntil, zip } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { IChartBarInfo } from '../../../../../interfaces/chart-bar-info.interface';
import { ID_AMBITO_GEOGRAFICO, TIPO_FILTRO } from '../../../../../helpers/constantes';
import { EnumMensajeGeneral } from '../../../../../helpers/estado-enum';
import { makeScaleValues } from '../../../../../helpers/handler-chart-data.common';
import { Mesa } from '../../../../../interfaces/acta-bean';
import { Participante, Candidato } from '../../../../../interfaces/eleccion-congresal-bean';
import { Region } from '../../../../../interfaces/elections.interfaces';
import { FrontendResponse } from '../../../../../interfaces/response.common';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { MapaCalor } from '../../../../../interfaces/resumen-general-bean';
import { DistritoElectoral } from '../../../../../interfaces/ubigeo-bean';
import { BehaviorResumenService } from '../../../../../services/elecciones-generales/behavior-resumen.service';
import { MesaService } from '../../../../../services/elecciones-generales/mesa.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { ResumenGeneralService } from '../../../../../services/elecciones-generales/resumen-general.service';
import { SenadoresDistritoElectoralMultipleService } from '../../../../../services/elecciones-generales/senadores-distrito-electoral-multiple.service';
import { UbigeoService } from '../../../../../services/elecciones-generales/ubigeo.service';
import { BreakpointObserver } from '@angular/cdk/layout';
import { BAR_GRAFIC_BREAKPOINTS, getNumberOfBarsForGrafic } from '../../../../../helpers/responsive-dimentions.helper';
import { ModalDetailVotesComponent } from '../../../../../components/modal-detail-votes/modal-detail-votes.component';
import { ModalDetailVotesService } from '../../../../../services/common/modal-detail-votes.service';
import { ModalDetailVotes } from '../../../../../interfaces/modal-detail-votes.interface';
import { DELAY_SEARCH } from '../../../../../constants/search-incremental.constants';
import { mapWithPoliticImage } from '../../../../../helpers/get-images.helper';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { GenericFilterParams } from '../../../../../interfaces/filtro-settings';

@Component({
  selector: 'app-resultado-ubicacion-geografica',
  templateUrl: './resultado-ubicacion-geografica.component.html',
  styleUrls: ['./resultado-ubicacion-geografica.component.scss'],
  standalone: false,
  
})
export class ResultadoUbicacionGeograficaComponent implements OnInit, OnDestroy {
  @Input() idEleccion = 0;
  @Input() resumen: Resumen;
  @Input() active: boolean = false;
  @Output() updateResumenGeneral = new EventEmitter<number>();
  @ViewChild(GenericFilterUbigeoComponent, { static: false }) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  isUbigeoReady = false;
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  randomImageUrl: string;
  myFormUbigeo: FormGroup = this.fb.group({
    region: [0, Validators.required]
  });
  myFormOrganizacion: FormGroup = this.fb.group({
    nomAgrupacion: [null]
  });
  mapaCalor: MapaCalor[];
  listRegion: DistritoElectoral[] = [];
  listParticipante: Participante[] = [];
  listOrganizacionPoliticaOrigin: Participante[] = [];
  filterButtonIsDisabled: boolean = true;
  deshabilitarBotonGenerarReporte: boolean = false;
  dataForDistrict: IChartBarInfo[] = [];
  dataForDistrictOrigin: IChartBarInfo[] = [];
  totalPaginasGrafica: number = -1;
  scalesForDistrict: number[] = [];
  destroy$: Subject<boolean> = new Subject<boolean>();
  valorMaximoParticipante: number = 0;
  regionSeleccionada: Region;
  escalaTotalVotos: number[];
  listOrganizacionPolitica: Participante[] = [];
  valorMaximo: number = 0;
  mesaTotales: Mesa;
  totalCandidatos = 0;
  totalVotos: number = 0;
  totalVotosEmitidos: number = 0;
  totalVotosValidos: number = 0;
  esExtranjero: boolean = false;
  primeraVez: boolean = true;
  mostrarTotalVotos: boolean = true;
  numberCandidatesForDistrict: number = 9;
  vermas1: boolean = false;
  primeraCarga: boolean = true;
  verInput: boolean = false;
  listCandidato: Candidato[] = [];
  listCandidatoOrigin: Candidato[] = [];
  deshabilitarBotonBuscarOrganizacion: boolean = true;
  esBuscarListaOrganizacion: boolean = false;
  mensaje: string = EnumMensajeGeneral.MENSAJE_PRESIONE_BOTON_FILTRAR;
  mensajeNoResultado: string = EnumMensajeGeneral.MENSAJE_NO_SE_ENCONTRARON_RESULTADOS;
  valorFiltroDistritoElectoral: number = null;
  labelFiltroDistritoElectoral: string = null;
  valorFiltroDistritoElectoralButtonReporte: number = null;
  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;
  heightGrafica: number;
  private resizeObserver: ResizeObserver;

  constructor(
    private readonly fb: FormBuilder,
    private readonly ubigeoService: UbigeoService,
    private readonly mesaService: MesaService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly senadoresDistritoElectoralMultipleService: SenadoresDistritoElectoralMultipleService,
    private readonly randomImageService: RandomImageService,
    private readonly breakpointObserver: BreakpointObserver,
    private readonly modalDetailVotesService: ModalDetailVotesService,
    protected dialog: MatDialog,
  ) {
    this.breakpointObserver.observe(BAR_GRAFIC_BREAKPOINTS).subscribe((result) => {
      this.numberCandidatesForDistrict = getNumberOfBarsForGrafic(result.breakpoints);
      this.scalesForDistrict = makeScaleValues(this.listParticipante, this.numberCandidatesForDistrict);
      this.dataForDistrictOrigin = [];
      this.dataForDistrictOrigin = this.separarPorGrupo(this.dataForDistrict, this.numberCandidatesForDistrict);
    });
  }

  ngOnInit(): void {
    this.cargarResumen();
    this.cargarDatosInicial();
    // imagen aleatoria
    this.randomImageUrl = this.randomImageService.getRandomImage();
    this.myFormOrganizacion
      .get('nomAgrupacion')
      .valueChanges.pipe(debounceTime(DELAY_SEARCH), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((searchTerm: string) => this.listarOrganizacionPolitica(searchTerm));
  }

  ngAfterViewInit() {
    // Crear el ResizeObserver cuando la vista esté inicializada
    this.resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        // Obtener la altura del div que está siendo observado
        const height = entry.contentRect.height;
        setTimeout(() => {
          this.heightGrafica = Math.round(height);
          this.cargarResumen();
        }, 1000);
        // Aquí puedes manejar el cambio de altura como desees, por ejemplo:
        // Actualizar la altura del mapa o realizar otros ajustes.
      }
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;

    if (!this.esPantallaChica) {
      this.mostrarMapa2 = true;
    } else {
      this.mostrarMapa2 = false;
    }
  }
  toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  cargarDatosInicial(): void {
    let codigoAgrupacionPolitica = '0';
    let idAmbitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let idUbigeoDepartamento = 0;
    let idUbigeoProvincia = 0;
    let idUbigeoDistrito = 0;
    let idEleccion = this.idEleccion;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let distritoElectoral = this.resumen.idUbigeoDistritoElectoral;
    this.valorFiltroDistritoElectoral = distritoElectoral;

    let obsMapaCalor$ = this.resumenGeneralService.listarMapaCalor(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      this.idEleccion,
      tipoFiltro,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito
    );
    let obsParticipantes$ = this.senadoresDistritoElectoralMultipleService.listarParticipantesPorUbicacionGeografica({
      idDistritoElectoral: distritoElectoral,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro
    });
    let obsRegiones$ = this.ubigeoService.listarDistritoElectorales().pipe(takeUntil(this.destroy$));



    this.dataForDistrictOrigin = [];
    zip(obsMapaCalor$, obsParticipantes$, obsRegiones$)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ([obs1, obs2, obs3]) => {
          this.cargarMapaCalor(obs1);
          this.listarParticipantes(obs2);
          this.listarOrganizacionesPoliticas(obs2);
          this.listRegion = obs3.data;
          
          const initialRegion = Number(this.resumen.idUbigeoDistritoElectoral);

          this.myFormUbigeo.get('region')?.setValue(initialRegion, { emitEvent: false });

          queueMicrotask(() => {
            if (!this.mainFiltroUbigeoComponent) return;
            this.mainFiltroUbigeoComponent.setElectoralRegion(initialRegion);
            this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
            this.isUbigeoReady = true;
          });
        },
        error: (err) => {
          console.error('Error al cargar datos', err);
        }
      });
  }

  filtrar(params: GenericFilterParams): void {
    const region_value = params?.electoralDistrictId;
    if (region_value == null) return;

    this.myFormUbigeo.get('region')?.setValue(region_value, { emitEvent: false });

    // sincroniza breadcrumb/label del filtro genérico
    if (this.mainFiltroUbigeoComponent) {
      this.mainFiltroUbigeoComponent.setElectoralRegion(Number(region_value));
      this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();

      this.labelFiltroDistritoElectoral = this.mainFiltroUbigeoComponent.getRegionForOnlyRegions();
      this.mainFiltroUbigeoComponent.breadcrumbString = this.labelFiltroDistritoElectoral;
    }

    // evita recargar si es el mismo distrito
    if (this.valorFiltroDistritoElectoral === region_value) return;
    this.valorFiltroDistritoElectoral = region_value;

    this.updateResumenGeneral.emit(this.valorFiltroDistritoElectoral);
    this.esBuscarListaOrganizacion = false;
    this.primeraVez = false;
    this.behaviorResumenService.setActualizarResumen(region_value);
    this.deshabilitarBotonGenerarReporte = false;
    this.filterButtonIsDisabled = true;

    let codigoAgrupacionPolitica = '0';
    let idAmbitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let idUbigeoDepartamento = 0;
    let idUbigeoProvincia = 0;
    let idUbigeoDistrito = 0;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let idEleccion = this.idEleccion;

    if (region_value == 27) {
      idAmbitoGeografico = 2;
      tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    }

    const obsMapaCalor$ = this.resumenGeneralService.listarMapaCalor(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      this.idEleccion,
      tipoFiltro,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito
    );

    const obsParticipantes$ =
      this.senadoresDistritoElectoralMultipleService.listarParticipantesPorUbicacionGeografica({
        idDistritoElectoral: region_value,
        idEleccion,
        tipoFiltro
      });

    this.dataForDistrictOrigin = [];

    const nombreAgrupacion = this.myFormOrganizacion.get('nomAgrupacion')?.value;

    zip(obsMapaCalor$, obsParticipantes$)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ([obs1, obs2]) => {
          this.cargarMapaCalor(obs1);
          this.listarParticipantes(obs2);
          this.listarOrganizacionesPoliticas(obs2);

          if (nombreAgrupacion && nombreAgrupacion.trim() !== '') {
            const filtro = nombreAgrupacion.trim().toLowerCase();
            this.listOrganizacionPolitica = mapWithPoliticImage(
              this.listOrganizacionPoliticaOrigin.filter(
                (org) =>
                  org.nombreAgrupacionPolitica &&
                  org.nombreAgrupacionPolitica.toLowerCase().includes(filtro)
              )
            );
          } else {
            this.listOrganizacionPolitica = mapWithPoliticImage([...this.listOrganizacionPoliticaOrigin]);
          }

          this.totalCandidatos = this.listOrganizacionPolitica.length;
        },
        error: (err) => console.error('Error al cargar datos', err)
      });
  }

  cargarMapaCalor(data: FrontendResponse<[MapaCalor]>): void {
    this.esExtranjero = this.myFormUbigeo.get('region').value == 27;
    this.mapaCalor = data.data;
  }

  private sortParticipantesPorVotos(lista: any[]): any[] {
    return lista.sort((a, b) => b.totalVotosValidos - a.totalVotosValidos);
  }

  
  getNumberOfPoliticalOrganizations(organizations: Participante[]) {
      return organizations.filter(org =>
        org.codigoAgrupacionPolitica !== "80" &&
        org.codigoAgrupacionPolitica !== "81" &&
        org.totalCandidatos > 0
      ).length;
  }

  listarParticipantes(data: FrontendResponse<[Participante]>): void {
    if (data.data == undefined) {
      this.listParticipante = [];
      this.dataForDistrictOrigin = [];
      this.mensaje = 'Todavía no se cuenta con información para mostrar en la opción seleccionada.';
      return;
    }

    let listaParticipanteGrafica: Participante[] =
      data.data != undefined
        ? data.data.filter((x) => 
          x.codigoAgrupacionPolitica != '80' 
        && x.codigoAgrupacionPolitica != '81'
        && x.totalCandidatos > 0)
        : [];
    this.listParticipante = listaParticipanteGrafica;
    let valorMax = Math.max(...this.listParticipante.map((x) => x.totalVotosValidos));
    this.valorMaximoParticipante = valorMax;

    const listaTmp = this.sortParticipantesPorVotos(listaParticipanteGrafica);

    this.dataForDistrict = listaTmp.map((x) => {
      let data: IChartBarInfo = {
        percentage_for_chart:
          this.valorMaximoParticipante == 0 ? 0 : (x.totalVotosValidos / this.valorMaximoParticipante) * 100,
        number_of_valid_votes: x.totalVotosValidos,
        url_candidate_image: null,
        urlAgrupacionImage: null,
        name_of_candidate: x.nombreCandidato,
        name_of_politic_group: x.nombreAgrupacionPolitica,
        number_of_list: null,
        code_of_politic_group: x.codigoAgrupacionPolitica,
        percentage_valid_votes: x.porcentajeVotosValidos
      };
      return data;
    });

    this.scalesForDistrict = makeScaleValues(this.listParticipante, this.numberCandidatesForDistrict);
    this.dataForDistrictOrigin = [];
    this.dataForDistrictOrigin = this.separarPorGrupo(this.dataForDistrict, this.numberCandidatesForDistrict);
    this.obtenerTotalesMesa();
    this.calcularEscala(this.listParticipante);
  }

  listarOrganizacionesPoliticas(data: FrontendResponse<[Participante]>): void {
    let nombreApellidoPartido = this.myFormOrganizacion.get('nomAgrupacion').value;
    if (!data.success) {
      this.listOrganizacionPolitica = [];
      return;
    }
    
    this.listOrganizacionPolitica = mapWithPoliticImage(data.data);
    this.listOrganizacionPoliticaOrigin = mapWithPoliticImage(data.data);
    // this.totalCandidatos = !nombreApellidoPartido
    //   ? this.listOrganizacionPolitica.length - 2
    //   : this.listOrganizacionPolitica.length;
    this.totalCandidatos = this.listOrganizacionPolitica.length;
    this.calcularTotales();
    this.calcularEscala(this.listOrganizacionPolitica);
  }

  calcularEscala(lista: Participante[]): void {
    this.escalaTotalVotos = [];
    let valorMax = Math.max(...lista.map((x) => x.totalVotosValidos));
    this.valorMaximo = valorMax;

    if (valorMax == 0) {
      this.escalaTotalVotos.push(0);
      this.escalaTotalVotos.push(70);
      this.escalaTotalVotos.push(140);
      this.escalaTotalVotos.push(220);
      this.escalaTotalVotos.push(300);
    } else {
      let obtenerAgregadoPorcentual = valorMax * 0.05;
      let valorMaxTmp = valorMax + obtenerAgregadoPorcentual;

      let divisor = Math.ceil(valorMaxTmp / 4);

      for (let index = 0; index < 5; index++) {
        const valor = divisor * index;
        this.escalaTotalVotos.push(valor);
      }
    }
  }

  calcularTotales(): void {
    this.totalVotosEmitidos = this.listOrganizacionPolitica
      .map((t) => t['porcentajeVotosEmitidos'] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotosValidos = this.listOrganizacionPolitica
      .map((t) => t['porcentajeVotosValidos'] ?? 0)
      .reduce((acc, value) => acc + value, 0);

    this.totalVotos = this.listOrganizacionPolitica
      .map((t) => t['totalVotosValidos'] ?? 0)
      .reduce((acc, value) => acc + value, 0);
  }

  irSeccionBaja($element: HTMLElement): void {
    window.scrollTo({ top: 790, left: 0, behavior: 'smooth' });
  }

  buscarAgrupacion(): void {
    let nombreAgrupacion =
      this.myFormOrganizacion.get('nomAgrupacion').value == ''
        ? null
        : this.myFormOrganizacion.get('nomAgrupacion').value;

    this.listarOrganizacionPolitica(nombreAgrupacion);
  }

  listarOrganizacionPolitica(nombreCandidato: string): void {
    let nombreApellidoPartido = nombreCandidato;

    if (nombreApellidoPartido) {
      this.mostrarTotalVotos = false;      
      this.listOrganizacionPolitica = mapWithPoliticImage(
        this.listOrganizacionPoliticaOrigin.filter((x) =>
          x.nombreAgrupacionPolitica.toLowerCase().includes(nombreApellidoPartido.toLowerCase())
        )
      );
    } else {
      this.mostrarTotalVotos = true;
      this.listOrganizacionPolitica = mapWithPoliticImage([...this.listOrganizacionPoliticaOrigin]);
    }
    // this.totalCandidatos = !nombreApellidoPartido
    //   ? this.listOrganizacionPolitica.length - 2
    //   : this.listOrganizacionPolitica.length;
    this.totalCandidatos = this.listOrganizacionPolitica.length;
  }

  limpiarLista(): void {
    this.myFormOrganizacion.get('nomAgrupacion').setValue(null);
    this.listOrganizacionPolitica = mapWithPoliticImage([...this.listOrganizacionPoliticaOrigin]);
    // this.totalCandidatos = this.listOrganizacionPolitica.length - 2;
    this.totalCandidatos = this.listOrganizacionPolitica.length;
    this.mostrarTotalVotos = true;
  }

  calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }

    let agregado = this.valorMaximo * 0.05;
    let votoCalculado = this.valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }

  irSeccionArriba($element: HTMLElement): void {
    window.scrollTo({ top: 1, left: 0, behavior: 'smooth' });
  }

  cargarEventChange(): void {
    this.myFormOrganizacion
      .get('nomAgrupacion')
      .valueChanges.pipe(startWith(0), pairwise())
      .subscribe({
        next: ([prev, next]: Array<number>) => {
          if (next == 0) {
            this.myFormUbigeo.get('nomCandidato').disable();
            this.deshabilitarBotonBuscarOrganizacion = true;
          } else {
            this.myFormUbigeo.get('nomCandidato').enable();
            this.deshabilitarBotonBuscarOrganizacion = false;
          }
          this.verInput = false;
          this.listCandidato = [];
        }
      });
  }

  seleccionarDistritoElectoralPorId(value: number): void {
    this.filtrar({ electoralDistrictId: Number(value) });
  }

  seleccionarDistritoElectoralPorParams(params: any): void {
    if (params?.electoralDistrictId) {
      this.seleccionarDistritoElectoralPorId(params.electoralDistrictId);
    }
  }

  cargarResumen(): void {
   setTimeout(() => {
    this.behaviorResumenService.setActualizarResumen(this.myFormUbigeo.get('region').value);
   }, 50);
  }

  getPosicionRegla(valor: number, index: number): string {
    let valorMaximo = this.escalaTotalVotos[this.escalaTotalVotos.length - 1];
    let retirado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + retirado;
    let valorRetorno = (valor * 100) / votoCalculado;

    if (this.escalaTotalVotos.length - 1 == index) {
      return '100%';
    }

    return valorRetorno + '%';
  }

  detailVote(value: Participante, politicalPartyImageShow: boolean = true): void {
    if (this.esPantallaChica) {
      const data: Partial<ModalDetailVotes> = {
        // Partido Politico
        politicalPartyImage: value.urlAgrupacionImage,
        politicalPartyName: value.nombreAgrupacionPolitica,
        politicalPartyImageShow,
        // Candidato
        candidateImageShow: false,
        candidateNameShow: false,
        // Votos
        votesNumber: value.totalVotosValidos,
        // Votos Emitidos
        votesEmittedPercentage: value.porcentajeVotosEmitidos,
        // Votos Validos
        votesValidPercentage: value.porcentajeVotosValidos
      };
      this.openModalDetailVotes(data);
    }
  }

  detailTypeVotes(value: Participante, title: string): void {
    if (!this.esPantallaChica) {
      return;
    }
    const data: Partial<ModalDetailVotes> = {
      // Partido Politico
      politicalPartyShow: false,
      // Candidato
      candidateImageShow: false,
      candidateName: title,
      // Votos
      votesNumber: value.totalVotosValidos,
      // Votos Emitidos
      votesEmittedPercentage: value.porcentajeVotosEmitidos,
      // Votos Validos
      votesValidPercentage: value.porcentajeVotosValidos
    };
    this.openModalDetailVotes(data);
  }

  getReglaValorIteracion(valor: number, index: number): number {
    if (this.escalaTotalVotos.length - 1 == index) {
      return valor + (this.valorMaximo == 0 ? 0 : 1);
    }

    return valor;
  }

  private obtenerTotalesMesa(): void {
    let ambitoGeografico = ID_AMBITO_GEOGRAFICO.ID_NACIONAL;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let ubigeoNivel1 = 0;
    let ubigeoNivel2 = 0;
    let ubigeoNivel3 = 0;
    let distritoElectoral = this.myFormUbigeo.get('region')?.value;
    this.mesaService
      .obtenerTotales(ambitoGeografico, tipoFiltro, ubigeoNivel1, ubigeoNivel2, ubigeoNivel3, distritoElectoral)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.mesaTotales = result.data;
        }
      });
  }

  private separarPorGrupo(lista: IChartBarInfo[], tamanioGrupo: number): IChartBarInfo[] {
    let listaResult: IChartBarInfo[] = [];
    let grupo: number = 0;
    let conta: number = 1;
    for (const element of lista) {
      if (conta > tamanioGrupo) {
        grupo = grupo + 1;
        conta = 1;
      }
      element.group = grupo;
      listaResult.push(element);
      conta = conta + 1;
    }
    this.totalPaginasGrafica = grupo;
    return listaResult;
  }

  private openModalDetailVotes(data: Partial<ModalDetailVotes>): void {
    this.modalDetailVotesService.setData(data);
    this.dialog.open(ModalDetailVotesComponent, {
      width: '400px',
      maxWidth: '80vw',
      panelClass: 'popup-votos-detalle'
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.behaviorResumenService.setActualizarResumen(0);
  }
}
