import { ViewportScroller } from '@angular/common';
import { AfterViewInit, Component, HostListener, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject, merge, take, takeUntil } from 'rxjs';
import { MainHotMapComponent } from '../../../../../components/main-hot-map/main-hot-map.component';
import { AutocompleteInputComponent } from '../../../../../components/autocomplete-input/autocomplete-input.component';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { ACTAS_RESUMEN_GENERAL_TITLE } from '../../../../../helpers/actas-resumen-general.helper';
import { generateFilterByLocationParams } from '../../../../../helpers/actas.helper';
import { applyFiltersEvent as applyActasFiltersEvent } from '../../../../../helpers/actas-filter.helper';
import {
  ACTA_CODIGO_ESTADO,
  CONFIG_NAMES_FOR_ACTAS,
  ID_AMBITO_GEOGRAFICO,
  ID_ELECCION
} from '../../../../../helpers/constantes';
import { getElectionByIdElection } from '../../../../../helpers/encrypt-storage-eleccion';
import { Acta, Archivo, Content, Detalle, LineaTiempo, Mesa } from '../../../../../interfaces/acta-bean';
import { IActasComponent, MessagePersonageStranger } from '../../../../../interfaces/actas.interfaces';
import { CentEducativoBean } from '../../../../../interfaces/cent-educativo-bean';
import { LocalVotacion } from '../../../../../interfaces/elections.interfaces';
import { GenericFilterParams, getOptimizedObject, REGION_EXTRAJERO, REGION_PERU, RegionValue } from '../../../../../interfaces/filtro-settings';
import { ParticipacionCiudadano } from '../../../../../interfaces/participacion-ciudadana.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { MapaCalor } from '../../../../../interfaces/resumen-general-bean';
import { Ubigeo } from '../../../../../interfaces/ubigeo-bean';
import { ActasService } from '../../../../../services/elecciones-generales/actas.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { UbigeoService } from '../../../../../services/elecciones-generales/ubigeo.service';
import { mapWithPoliticImage } from '../../../../../helpers/get-images.helper';

@Component({
  selector: 'app-actas-por-ambito',
  templateUrl: './actas-por-ambito.component.html',
  standalone: false
})
export class ActasPorAmbitoComponent implements OnInit, AfterViewInit, OnDestroy, IActasComponent {
  @Input() idEleccion: any;
  @Input() resumen: Resumen;
  @Input() elecciones: any[];
  @Input() ingresoTabDos: boolean = false;
  @ViewChild(AutocompleteInputComponent) autoComplete!: AutocompleteInputComponent<LocalVotacion>;
  @ViewChild(MainHotMapComponent) mapaCalorViewChild: any;
  @ViewChild(GenericFilterUbigeoComponent) genericFilterUbigeoComponent: GenericFilterUbigeoComponent;
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;

  public randomImageUrl: string;
  departamentos: Ubigeo[];
  provincias: Ubigeo[];
  distritos: Ubigeo[];
  centrosEducativos: Array<CentEducativoBean>;
  tiposActas: boolean = true;
  listaResultado = new Array();
  actasDetalleLista: boolean = false;
  DetalleActas: boolean = false;
  DetalleActasObs: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  private participacionCancel$ = new Subject<void>();
  idAmbito: number = 1;
  actas: Content[] = [];
  ubigeo = '000000';
  participantes: [ParticipacionCiudadano];
  mapaCalor: MapaCalor[] = [];

  esEstranjero: boolean = false;
  nivelUbigeo: number = 0;
  codigoUbigeo: string = '';
  totalPaginas = 0;
  totalPaginasReales = 0;
  totalRegistros = 0;
  primeraVez = true;
  primeraBusqueda = true;
  datosActa: Acta;
  paginaActual = 0;
  paginaAnterior = 0;
  habilitarBotonSiguiente = false;
  habilitarBotonAnterior = false;
  rangoMaximoPaginado = 4;
  grupoRangoPaginado = 1;
  valorMinimoRangoPaginado = 1;
  valorMaximoRangoPaginado = 4;
  cuentaVecesSeguiente = 1;
  deshabilitarBotonFiltrar: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  lineaTiempo: [LineaTiempo];
  detalleActa: Mesa;
  detalle: Detalle[] = [];
  participacionCiudadanaPorcentual: number = 0;
  ID_ELECCION = ID_ELECCION;
  ACTA_CODIGO_ESTADO = ACTA_CODIGO_ESTADO;
  mesaSeleccionado: Content;
  listaPaginado: any[];
  listaPaginaTotal = new Array();
  listaPagina = new Array();
  sizePagina = 15;
  totalRegistroPorPagina = 15;
  listaArchivos: [Archivo] = null;
  enumIdAmbito: any;
  mensajeInicial: string = '';
  mensajeInicialExtranjero: string = '';
  messagePersonageStranger: MessagePersonageStranger = {
    nationality: { region: '', province: '', district: '' },
    stranger:    { region: '', province: '', district: '' }
  };
  mensaje: string = '';
  ubigeoMapaSeleccionado: string = '';
  mostrarElementos: boolean = false;
  codigoMesaForLineaTiempo = '';
  configNameForLineTiempo = '';
  loadingPaginacion: boolean = false;
  temporalFiltro: string;
  fragment: string;
  localesVotacion: LocalVotacion[] = [];
  controlEleccion: any;
  controlAmbito: any;
  controlDepartamento: any;
  controlProvincia: any;
  controlDistrito: any;
  myFormUbigeo: FormGroup = this.fb.group({
    region: [ID_AMBITO_GEOGRAFICO.ID_SIN_AMBITO_GEOGRAFICO, Validators.required],
    eleccion: [0, Validators.required],
    departamento: ['0', Validators.required],
    provincia: ['0', Validators.required],
    distrito: ['0', Validators.required],
    cent_educativo: [0, Validators.required]
  });
  electionName: string = '';
  regionFilter: RegionValue = REGION_PERU;
  private isSearching: boolean = false;
  private readonly searchSubject = new Subject<void>();
  private readonly _generateFilterByLocationParams = generateFilterByLocationParams;

  constructor(
    private readonly fb: FormBuilder,
    private readonly ubigeoService: UbigeoService,
    private readonly actaService: ActasService,
    public route: ActivatedRoute,
    private readonly scroller: ViewportScroller,
    public readonly randomImageService: RandomImageService,
    private readonly translate: TranslateService
  ) {}

  @HostListener('window:resize', ['$event']) onResize(event: any) {
    this.esPantallaChica = event.target.innerWidth < 960;
    this.updateIsSmallScreen();
  }

  ngOnInit(): void {
    this.mensajeInicial           = this.translate.instant('personaje.seleccione_region');
    this.mensajeInicialExtranjero = this.translate.instant('personaje.seleccione_continente');
    this.mensaje                  = this.mensajeInicial;
    this.messagePersonageStranger = {
      nationality: {
        region:   this.translate.instant('personaje.seleccione_region'),
        province: this.translate.instant('personaje.seleccione_provincia'),
        district: this.translate.instant('personaje.seleccione_distrito')
      },
      stranger: {
        region:   this.translate.instant('personaje.seleccione_continente'),
        province: this.translate.instant('personaje.seleccione_pais'),
        district: this.translate.instant('personaje.seleccione_ciudad')
      }
    };
    this.actaService.init(this);
  }

  ngAfterViewInit(): void {
    this.updateIsSmallScreen();
    this.genericFilterUbigeoComponent.breadcrumbString = REGION_PERU;
    try {
      document.querySelector('#' + this.fragment).scrollIntoView();
    } catch (e) {}
  }

  toggleMapa() {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  getEstadoTexto(codigoEstado: string): string {
    switch (codigoEstado) {
      case 'C':
        return 'Contabilizada';
      case 'P':
        return 'Pendiente para envío a JEE';
      case 'E':
        return 'Observado';
      default:
        return 'Desconocido';
    }
  }

  getEstadoTraducido(codigo: string): string {
    return this.translate.instant('estado-acta.' + codigo);
  }

  limpiar(): void {
    this.mostrarElementos = false;
    this.actaService.limpiar(this);
    this.mostrarElementos = false;
  }

  ejecutClearFilterNew(): void {
    this.myFormUbigeo.get('cent_educativo').setValue(null, { emitEvent: false });
    this.genericFilterUbigeoComponent.clearFilterNew();
    this.setBreadcrumbStringForcePeru();
    this.limpiar();
  }

  listarActas(): void {
    if (this.isSearching) {
      return;
    }

    this.isSearching = true;
    let idUbigeo = Number(this.myFormUbigeo.controls['distrito'].value);
    let codigoLocalVotacion =
      Number(this.myFormUbigeo.controls['cent_educativo'].value) == 0
        ? null
        : Number(this.myFormUbigeo.controls['cent_educativo'].value);

    this.idAmbito = Number(this.myFormUbigeo.controls['region'].value);
    this.listaPagina = [];

    if (!idUbigeo) {
      this.actas = [];
      this.totalRegistros = 0;
      this.totalPaginasReales = 0;
      this.isSearching = false;
      return;
    }

    this.actaService
      .listarActas(codigoLocalVotacion, this.idAmbito, idUbigeo, this.paginaActual, this.sizePagina)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.isSearching = false;
          if (result.success) {
            if (result.data.content.length > 0) {
              this.actas = result.data.content;
              this.totalRegistros = result.data.totalRegistros;
              this.totalPaginasReales = result.data.totalPaginas;

              this.datosActa = result.data;

              this.crearListaPaginado(this.totalRegistros, this.paginaActual);
            } else {
              this.totalRegistros = 0;
              this.actas = [];
            }
          } else {
            this.totalRegistros = 0;
            this.actas = [];
          }
          this.mensaje = '';
        },
        error: (err) => {
          console.error('Error en listarActas:', err);
          this.deshabilitarBotonFiltrar = false;
          this.totalRegistros = 0;
          this.actas = [];
          this.isSearching = false;
        }
      });
  }

  crearListaPaginado(totalRegistros: number, pagina: number): void {
    pagina = pagina + 1;
    let totalPaginas = Math.ceil(totalRegistros / this.sizePagina);
    let contPagina = 1;
    let contaGrupo = 1;
    let totalPaginaPorGrupo = 5;
    let contPaginaAcumulado = 1;
    this.listaPaginaTotal = [];

    while (contPaginaAcumulado <= totalPaginas) {
      let mostrarBotonAnterior = true;
      let mostrarBotonSiguiente = true;

      this.listaPaginaTotal.push({
        grupo: contaGrupo,
        pagina: contPaginaAcumulado,
        mostrarBotonAnterior: mostrarBotonAnterior,
        mostrarBotonSiguiente: mostrarBotonSiguiente
      });
      contPagina = contPagina + 1;
      if (totalPaginaPorGrupo == contPagina) {
        contaGrupo = contaGrupo + 1;
        contPagina = 1;
      }

      contPaginaAcumulado = contPaginaAcumulado + 1;
    }
    if (this.listaPaginaTotal.length > 0) {
      let registro = this.listaPaginaTotal.find((x) => x.pagina == pagina);
      this.obtenerListaPaginaPorGrupo(this.listaPaginaTotal, registro.grupo, pagina);
    }
  }

  obtenerListaPaginaPorGrupo(listaPaginaTotal, grupo, pagina): void {
    let maxGrupo = Math.max(...listaPaginaTotal.map((o) => o.grupo));
    let minGrupo = Math.min(...listaPaginaTotal.map((o) => o.grupo));
    if (grupo > minGrupo && grupo < maxGrupo) {
      this.habilitarBotonSiguiente = true;
      this.habilitarBotonAnterior = true;
    } else if (grupo == minGrupo && grupo < maxGrupo) {
      this.habilitarBotonSiguiente = true;
      this.habilitarBotonAnterior = false;
    } else if (grupo == minGrupo && grupo == maxGrupo) {
      this.habilitarBotonSiguiente = false;
      this.habilitarBotonAnterior = false;
    } else if (grupo > minGrupo && grupo == maxGrupo) {
      this.habilitarBotonSiguiente = false;
      this.habilitarBotonAnterior = true;
    }

    this.listaPagina = listaPaginaTotal.filter((x) => x.grupo == grupo);
    this.listaPagina = this.listaPagina.map((x) => {
      if (x.pagina == pagina) {
        x.seleccionado = true;
      } else {
        x.seleccionado = false;
      }
      return x;
    });
  }

  listarActasPorPagina(idUbigeo, codigoLocalVotacion, pagina): void {
    let paginaEncontrada = this.listaPagina.find((x) => x.pagina == pagina + 1);
    if (paginaEncontrada.seleccionado) {
      return;
    }
    this.actaService
      .listarActas(codigoLocalVotacion, this.idAmbito, idUbigeo, pagina, this.sizePagina)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.actas = result.data.content;
          this.totalRegistros = result.data.totalRegistros;
          this.totalPaginasReales = result.data.totalPaginas;
          this.datosActa = result.data;
          this.listaPagina = this.listaPagina.map((x) => {
            if (x.pagina == pagina + 1) {
              x.seleccionado = true;
            } else {
              x.seleccionado = false;
            }
            return x;
          });
        },
        error: (error) => {}
      });
  }

  eventoPaginaSiguiente(): void {
    this.detalleActa = null;
    this.primeraBusqueda = false;
    this.listaPaginaTotal = [];
    if (this.listaPagina.length === 0) {
      return;
    }
    this.paginaActual = this.listaPagina[this.listaPagina.length - 1].pagina;
    this.listarActas();
  }

  eventoPaginaAnterior(): void {
    this.detalleActa = null;
    this.primeraBusqueda = false;
    this.listaPaginaTotal = [];
    this.paginaActual = this.listaPagina[0].pagina - 2;
    this.listarActas();
  }

  filtrar(): void {
    this.actaService.filtrar(this);
    this.deshabilitarBotonLimpiar = false;
    // Resetear página a 0 para nuevo filtro
    this.paginaActual = 0;
    this.listarActas(); // Llamada directa para el botón filtrar
  }

  VerActasdDetalle(): void {
    this.tiposActas = false;
    this.actasDetalleLista = true;
  }

  VerDetalles(mesa: Content, index: number): void {
    this.validateSelectedMesa(mesa);
    this.DetalleActas = true;
    this.DetalleActasObs = false;
    this.mesaSeleccionado = mesa;
    this.detalle = null;
    this.actaService
      .buscarMesaPorId(mesa.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.detalleActa = result.data;
            this.lineaTiempo = result.data.lineaTiempo;
            this.detalle = mapWithPoliticImage(result.data.detalle);
            this.listaArchivos = result.data.archivos;
            this.codigoMesaForLineaTiempo = result.data.codigoMesa;
            mesa.codigoEstadoActa = this.detalleActa.codigoEstadoActa;
            this.actas[index] = mesa;
            const selectedConfigName = CONFIG_NAMES_FOR_ACTAS.find((e) => e.electionId == result.data.idEleccion);
            this.configNameForLineTiempo = selectedConfigName ? selectedConfigName.name : 'acta';

            this.loadElectionName(result.data.idEleccion);
            setTimeout(() => {
              this.scroller.scrollToAnchor('detalle-view-111');
            }, 500);
          }
        },
        error: (error) => {}
      });
  }

  listarDepartamentos(idEleccion, idAmbito): void {
    this.ubigeoService
      .listarDepartamentos(idEleccion, idAmbito)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.departamentos = resp.data;
          this.myFormUbigeo.get('departamento').setValue(0, { emitEvent: false });
        }
      });
  }

  obtenerNombreEleccion(detalle: Mesa): string {
    if (detalle == null) {
      return '';
    }
    if (detalle.idEleccion == null) {
      return '';
    }
    let retorno = '';
    if (detalle.idEleccion == ID_ELECCION.ID_ELECCION_MUNICIPAL) {
      retorno = ' Municipal';
    }
    return retorno;
  }

  listarProvincias(idEleccion, idAmbito, idUbigeoDepartamento, idProviciaSeleccionada: string = '0'): void {
    this.ubigeoService
      .listarProvincias(idEleccion, idAmbito, idUbigeoDepartamento)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.provincias = resp.data;
          this.myFormUbigeo.get('provincia').setValue(Number(idProviciaSeleccionada), { emitEvent: false });
        }
      });
  }

  listarDistritos(idEleccion, idAmbito, idUbigeoProvincias): void {
    this.ubigeoService
      .listarDistritos(idEleccion, idAmbito, idUbigeoProvincias)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.distritos = resp.data;
          this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });
        }
      });
  }

  listarParticipacionCiudadana(
    tipoFiltroP: string = 'ambito_geografico',
    idUbigeoDepartamentoP: number = 0,
    idUbigeoProvinciaP: number = 0,
    idUbigeoDistritoP: number = 0,
    tipoNivelAmbito: number = 0,
    ubigeo: string = ''
  ): void {
    // Cancelar petición anterior en vuelo para evitar race conditions
    this.participacionCancel$.next();
    let codigoAgrupacionPolitica = 0;
    let idAmbitoGeografico = this.myFormUbigeo.controls['region'].value;
    let idUbigeoDepartamento = idUbigeoDepartamentoP;
    let idUbigeoProvincia = idUbigeoProvinciaP;
    let idUbigeoDistrito = idUbigeoDistritoP;
    let tipoFiltro = tipoFiltroP;

    this.actaService
      .listarMapaCalor(
        codigoAgrupacionPolitica,
        idAmbitoGeografico,
        this.idEleccion,
        idUbigeoDepartamento,
        idUbigeoProvincia,
        idUbigeoDistrito,
        tipoFiltro
      )
      .pipe(takeUntil(merge(this.destroy$, this.participacionCancel$)))
      .subscribe({
        next: (value) => {
          this.mapaCalor = value.data || [];
          const nacional = idAmbitoGeografico == '1';
          // Actualizar variables del mapa
          this.esEstranjero = !nacional;
          this.nivelUbigeo = tipoNivelAmbito;
          this.codigoUbigeo = ubigeo;
        },
        error: (err) => {}
      });
  }

  listarLocalesVotacion(idEleccion, idUbigeo): void {
    this.ubigeoService
      .listarLocales(idEleccion, idUbigeo)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.localesVotacion = result.data;
          this.myFormUbigeo.get('cent_educativo').setValue(null, { emitEvent: false });
          // Loading functionality removed
        }
      });
  }

  detectarDistritoPorMapa($event): void {
    this.mostrarElementos = true;
  }

  eventoMapBotton(event): void {
    this.actaService.listarParticipacionCiudadanaPorTipo(this, event);
    if (this.genericFilterUbigeoComponent) {
      const regionValue: RegionValue = event === 1 ? REGION_PERU : REGION_EXTRAJERO;
      this.genericFilterUbigeoComponent.ubigeoForm.controls.region.setValue(regionValue, { emitEvent: false });
      this.genericFilterUbigeoComponent.regionChanged();
      this.genericFilterUbigeoComponent.updateBreadcrumbStringFromExternal({ idAmbitoGeografico: event });
    }
  }

  retornarClaseCssSegunEstado(acta: Content): string {
    let nombreClase = '';
    if (acta.estadoActa == 'C') {
      nombreClase = 'contabilizada';
    } else if (acta.estadoActa == 'P') {
      nombreClase = 'pendiente';
    } else if (acta.estadoActa == 'O') {
      nombreClase = 'observado';
    }
    if (acta.idEleccion == ID_ELECCION.ID_ELECCION_MUNICIPAL) {
      nombreClase = nombreClase + ' ico-distrital';
    }
    return nombreClase;
  }

  paginado(numero): void {
    this.detalleActa = null;
    this.paginaAnterior = this.paginaActual;
    this.paginaActual = numero - 1;

    let idUbigeo = Number(this.myFormUbigeo.get('distrito').value);
    let codigoLocalVotacion =
      this.myFormUbigeo.get('cent_educativo').value == 0 ? null : this.myFormUbigeo.get('cent_educativo').value;

    this.listarActasPorPagina(idUbigeo, codigoLocalVotacion, this.paginaActual);
  }
  obtenerPorcentajeParticipacionCiudadana(detalle: Mesa): number {
    if (!detalle) {
      return 0;
    }
    if (detalle.totalVotosEmitidos == null) {
      return 0;
    }
    let calculo = (detalle.totalVotosEmitidos / detalle.totalElectoresHabiles) * 100;

    return calculo;
  }

  cambioUbigeo(data: string): void {
    if (data === null) {
      return;
    }
    const formattedId = String(data).padStart(6, '0');

    const idAmbitoGeografico = this.myFormUbigeo.get('region').value;
    const filterParams = this._generateFilterByLocationParams(formattedId);
    if (idAmbitoGeografico === 1) {
      this.genericFilterUbigeoComponent.setUbigeoParams(filterParams);
    } else {
      this.genericFilterUbigeoComponent.setUbigeoParamsExtrangero(filterParams);
    }
    this.genericFilterUbigeoComponent.applyUbigeoFilters();
    this.genericFilterUbigeoComponent.updateBreadcrumbStringFromExternal(getOptimizedObject({
      idAmbitoGeografico,
      ubigeoNivel1: filterParams.departmentUbigeoId,
      ubigeoNivel2: filterParams.provinceUbigeoId,
      ubigeoNivel3: filterParams.districtUbigeoId,
    } as GenericFilterParams));
  }

  limpiarPaginado(): void {
    this.actaService.limpiarPaginado(this);
  }

  mostrarNombreNivel(nivel: number, capital: boolean = false): string {
    return this.actaService.mostrarNombreNivel(this, nivel, capital);
  }

  public isOnInitialValue(): boolean {
    return this.genericFilterUbigeoComponent?.isOnInitialValue() ?? true;
  }  

  applyFiltersEvent(params: GenericFilterParams): void {
    this.mensaje = applyActasFiltersEvent(params, this.myFormUbigeo, this.translate, () => this.limpiar());
  }

  private validateSelectedMesa(selectedMesa: Content): void {
    this.actas.forEach(acta => {
      acta.esSeleccionado = acta.id === selectedMesa.id;
    });
  }

  private loadElectionName(electionId: number): void {
    const electionName = ACTAS_RESUMEN_GENERAL_TITLE[electionId];
    if (electionName) {
      this.electionName = electionName;
    } else {
      const elections = getElectionByIdElection(electionId);
      if (elections) {
        this.electionName = elections.nombre;
        return;
      }
    }
  }

  private updateIsSmallScreen(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = false;
      this.sizePagina = 5;
    } else {
      this.mostrarMapa2 = true;
      this.sizePagina = 15;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.searchSubject.complete();
  }

  private setBreadcrumbStringForcePeru(): void {
    this.genericFilterUbigeoComponent.breadcrumbString = REGION_PERU;
  }
}
