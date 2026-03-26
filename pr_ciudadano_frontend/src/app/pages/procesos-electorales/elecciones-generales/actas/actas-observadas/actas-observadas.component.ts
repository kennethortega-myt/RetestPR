import { DatePipe, ViewportScroller } from '@angular/common';
import {
  AfterViewInit,
  Component,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject, merge, of, take, takeUntil } from 'rxjs';
import { MainHotMapComponent } from '../../../../../components/main-hot-map/main-hot-map.component';
import { ObservadaComponent } from '../../../../../components/actas-components/observada/observada.component';
import { AutocompleteInputComponent } from '../../../../../components/autocomplete-input/autocomplete-input.component';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { PopupActasProcesadasComponent } from '../../../../../components/popup-actas-procesadas/popup-actas-procesadas.component';
import { PopupCausalesObservacionComponent } from '../../../../../components/popup-causales-observacion/popup-causales-observacion.component';
import { ACTAS_RESUMEN_GENERAL_TITLE } from '../../../../../helpers/actas-resumen-general.helper';
import { generateFilterByLocationParams } from '../../../../../helpers/actas.helper';
import { applyFiltersEvent as applyActasFiltersEvent } from '../../../../../helpers/actas-filter.helper';
import {
  ACTA_CODIGO_ESTADO,
  ACTA_CODIGO_ESTADO_DESC,
  CONFIG_NAMES_FOR_ACTAS,
  ID_AMBITO_GEOGRAFICO,
  ID_ELECCION
} from '../../../../../helpers/constantes';
import { getElectionByIdElection } from '../../../../../helpers/encrypt-storage-eleccion';
import { IGetActasObserbadasParams } from '../../../../../interfaces/acta-api.interface';
import { Acta, Archivo, Content, Detalle, LineaTiempo, Mesa } from '../../../../../interfaces/acta-bean';
import { ActasForm } from '../../../../../interfaces/actas-observadas';
import { IActasComponent, MessagePersonageStranger } from '../../../../../interfaces/actas.interfaces';
import { CentEducativoBean } from '../../../../../interfaces/cent-educativo-bean';
import { LocalVotacion } from '../../../../../interfaces/elections.interfaces';
import { GenericFilterParams, getOptimizedObject, REGION_EXTRAJERO, REGION_PERU, RegionValue } from '../../../../../interfaces/filtro-settings';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { MapaCalor } from '../../../../../interfaces/resumen-general-bean';
import { Ubigeo } from '../../../../../interfaces/ubigeo-bean';
import { ActasService } from '../../../../../services/elecciones-generales/actas.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { UbigeoService } from '../../../../../services/elecciones-generales/ubigeo.service';
import { mapWithPoliticImage } from '../../../../../helpers/get-images.helper';
import { HourFormatPipe } from '../../../../../pipes/hour-format.pipe';

@Component({
  selector: 'app-actas-observadas',
  templateUrl: './actas-observadas.component.html',
  standalone: false
})
export class ActasObservadasComponent implements OnChanges, OnInit, OnDestroy, IActasComponent, AfterViewInit {
  @Input() idEleccion: any;
  @Input() resumen: Resumen;
  @Input() localesVotacion: LocalVotacion[];
  @Input() elecciones: any[];
  @Input() ingresoTabTres: boolean = false;
  @ViewChild('selectDepartamento', { static: true }) selectDepartamento: MatSelect;
  @ViewChild('selectProvincia', { static: true }) selectProvincia: MatSelect;
  @ViewChild('selectDistrito', { static: true }) selectDistrito: MatSelect;
  @ViewChild(AutocompleteInputComponent) autoComplete!: AutocompleteInputComponent<LocalVotacion>;
  @ViewChild(ObservadaComponent) observadaComponente!: ObservadaComponent;
  @ViewChild(MainHotMapComponent) mapaCalorViewChild: any;
  @ViewChild(GenericFilterUbigeoComponent) genericFilterUbigeoComponent: GenericFilterUbigeoComponent;
  Cantidadactas = 'actas-observadas.Cantidadactas';
  Acta = 'actas-observadas.Acta';
  CausalesEnvio = 'actas-observadas.CausalesEnvio';
  VerMapaCalor = 'actas-observadas.VerMapaCalor';
  ActaN = 'actas-observadas.ActaN';
  mostrarMapa2 = false;
  esPantallaChica = window.innerWidth < 960;
  randomImageUrl: string;
  etiquetaMapa: string = 'Porcentaje de actas Observadas';
  departamentos: Ubigeo[];
  provincias: Ubigeo[];
  distritos: Ubigeo[];
  centrosEducativos: Array<CentEducativoBean>;
  destroy$: Subject<boolean> = new Subject<boolean>();
  private participacionCancel$ = new Subject<void>();
  primeraVez = true;
  idAmbito: number = 1;
  deshabilitarBotonFiltrar: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  actas: Content[] = [];
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
  totalPaginas = 0;
  totalPaginasReales = 0;
  totalRegistros = 0;
  mapaCalor: MapaCalor[] = [];

  esEstranjero: boolean = false;
  nivelUbigeo: number = 0;
  codigoUbigeo: string = '';
  detalleActa: Mesa;
  primeraBusqueda: boolean = true;
  enumIdAmbito: any;
  nombreEtiquetaNivel01: string = 'DEPARTAMENTO';
  nombreEtiquetaNivel02: string = 'PROVINCIA';
  nombreEtiquetaNivel03: string = 'DISTRITO';
  verFiltro: boolean = true;
  datosObservada: {};
  listaPaginaTotal = [];
  listaPagina = new Array();
  sizePagina = 15;
  codigoMesaForLineaTiempo = '';
  configNameForLineTiempo = '';
  lineaTiempo: [LineaTiempo];
  listaArchivos: [Archivo];
  detalle: Detalle[] = [];
  mensajeInicial: string = '';
  mensajeInicialExtranjero: string = '';
  mensaje: string = '';
  messagePersonageStranger: MessagePersonageStranger = {
    nationality: { region: '', province: '', district: '' },
    stranger:    { region: '', province: '', district: '' }
  };
  ID_ELECCION = ID_ELECCION;
  ACTA_CODIGO_ESTADO = ACTA_CODIGO_ESTADO;
  ACTA_CODIGO_ESTADO_DESC = ACTA_CODIGO_ESTADO_DESC;
  DetalleActas: boolean = false;
  DetalleActasObs: boolean = false;
  mesaSeleccionado: Content;
  mostrarMyFromFiltro: boolean = false;
  mostrarElementos: boolean = false;
  ubigeoMapaSeleccionado: string = '';
  temporalFiltro: string;
  myForm: FormGroup = this.fb.group({
    filtro: [null, Validators.required]
  });
  myFormUbigeo: FormGroup<ActasForm> = new FormGroup<ActasForm>({
    region: this.fb.control(ID_AMBITO_GEOGRAFICO.ID_SIN_AMBITO_GEOGRAFICO, Validators.required),
    eleccion: this.fb.control(0, Validators.required),
    departamento: this.fb.control('0', Validators.required),
    provincia: this.fb.control('0', Validators.required),
    distrito: this.fb.control('0', Validators.required),
    cent_educativo: this.fb.control('0', Validators.required)
  });
  controlEleccion: any;
  controlAmbito: any;
  controlDepartamento: any;
  controlProvincia: any;
  electionName: string = '';
  regionFilter: RegionValue = REGION_PERU;
  private actasFilterString = '';
  private readonly _generateFilterByLocationParams = generateFilterByLocationParams;
  public readonly loading$ = of(false);
  private hourFormatPipe = new HourFormatPipe();

  constructor(
    public dialog: MatDialog,
    private readonly fb: FormBuilder,
    private readonly ubigeoService: UbigeoService,
    private readonly actaService: ActasService,
    public route: ActivatedRoute,
    private readonly scroller: ViewportScroller,
    public datepipe: DatePipe,
    public readonly randomImageService: RandomImageService,
    private readonly translateService: TranslateService
  ) {}

  @HostListener('window:resize', ['$event']) onResize(event: any) {
    const esPantallaChica = event.target.innerWidth < 960;
    if (esPantallaChica !== this.esPantallaChica) {
      this.esPantallaChica = esPantallaChica;
      this.ejecutClearFilterNew();
      this.updateIsSmallScreen();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes) {
      if (changes['ingresoTabTres']) {
        if (changes['ingresoTabTres'].currentValue) {
          this.listarParticipacionCiudadana();
        }
      }
    }
  }

  ngOnInit(): void {
    this.randomImageUrl = this.randomImageService.getRandomImage();
    this.mensajeInicial           = this.translateService.instant('personaje.seleccione_distrito');
    this.mensajeInicialExtranjero = this.translateService.instant('personaje.seleccione_ciudad');
    this.mensaje                  = this.mensajeInicial;
    this.messagePersonageStranger = {
      nationality: {
        region:   this.translateService.instant('personaje.seleccione_region'),
        province: this.translateService.instant('personaje.seleccione_provincia'),
        district: this.translateService.instant('personaje.seleccione_distrito')
      },
      stranger: {
        region:   this.translateService.instant('personaje.seleccione_continente'),
        province: this.translateService.instant('personaje.seleccione_pais'),
        district: this.translateService.instant('personaje.seleccione_ciudad')
      }
    };
    this.actaService.init(this);
  }

  ngAfterViewInit(): void {
    this.setBreadcrumbStringForcePeru();
    this.updateIsSmallScreen();
  }

  detectarDistritoPorMapa($event): void {
    this.mostrarElementos = true;
  }

  toggleMapa(): void {
    if (this.esPantallaChica) {
      this.mostrarMapa2 = !this.mostrarMapa2;
    }
  }

  public isOnInitialValue(): boolean {
    return this.genericFilterUbigeoComponent?.isOnInitialValue() ?? false;
  }

  applyFiltersEvent(params: GenericFilterParams) {
    this.mensaje = applyActasFiltersEvent(params, this.myFormUbigeo, this.translateService, () => this.limpiar());
  }

  openPopup1(): void {
    const dialogRef = this.dialog.open(PopupActasProcesadasComponent, {
      width: '40%',
      maxWidth: '100%',
      panelClass: 'popup-centrado'
    });
    dialogRef.afterClosed().subscribe((result) => {});
  }

  openPopup2(): void {
    const dialogRef = this.dialog.open(PopupCausalesObservacionComponent, {
      width: '80%',
      maxHeight: '100%',
      panelClass: 'popup-centrado2'
    });
    dialogRef.afterClosed().subscribe((result) => {});
  }

  getNombreMesa(acta: Content): string {
    if (!acta) {
      return '';
    }
    if (acta.descripcionMesa && acta.descripcionMesa.trim() !== '') {
      return acta.descripcionMesa;
    }
    return `Mesa ${acta.codigoMesa}`;
  }

  getTextoBotonMesa(acta: Content): string {
    if (!acta) {
      return '';
    }
    if (acta.descripcionMesa && acta.descripcionMesa.trim() !== '') {
      return `${acta.descripcionMesa} - ${acta.codigoMesa}`;
    }
    return acta.codigoMesa;
  }

  tieneDescripcionMesa(acta: Content): boolean {
    return acta?.descripcionMesa?.trim() !== '';
  }

  getEstadoTraducido(codigo: string): string {
    if (!codigo) {
      return this.translateService.instant('estado-acta.desconocido');
    }
    const key = `estado-acta.${codigo}`;
    const traduccion = this.translateService.instant(key);
    if (traduccion !== key) {
      return traduccion;
    }    
    return this.translateService.instant('estado-acta.desconocido');
  }

  getEstadoTraducidoRobusto(codigo: string): string {
    if (!codigo) {
      return this.translateService.instant('estado-acta.desconocido');
    }
    const mapeoEstados: { [key: string]: string } = {
      C: 'CONTABILIZADA',
      P: 'PENDIENTE',
      E: 'OBSERVADA',
      O: 'OBSERVADA',
      CONTABILIZADA: 'CONTABILIZADA',
      PENDIENTE: 'PENDIENTE',
      OBSERVADA: 'OBSERVADA',
      PARA_ENVIO_JEE: 'PARA_ENVIO_JEE'
    };
    const codigoNormalizado = mapeoEstados[codigo.toUpperCase()] || codigo;
    const key = `estado-acta.${codigoNormalizado}`;
    const traduccion = this.translateService.instant(key);    
    if (traduccion !== key) {
      return traduccion;
    }
    
    return this.translateService.instant('estado-acta.desconocido');
  }

  getTextoIcono(tipoIcono: string): string {
    const key = `iconos.${tipoIcono}`;
    const traduccion = this.translateService.instant(key);
    
    if (traduccion !== key) {
      return traduccion;
    }    
    return tipoIcono;
  }

  getMensajeTraducido(key: string, params?: any): string {
    return this.translateService.instant(key, params);
  }

  debugActaInfo(acta: Content, index?: number): void {
    console.groupEnd();
  }

  listarDepartamentos(idEleccion, idAmbito): void {
    this.actasFilterString = '';
    this.ubigeoService
      .listarDepartamentos(idEleccion, idAmbito)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.departamentos = resp.data;
          this.actaService.setControlsValues(this.myFormUbigeo, ['departamento'], [0]);
        }
      });
  }

  listarProvincias(idEleccion, idAmbito, idUbigeoDepartamento, idProviciaSeleccionada: string = '0'): void {
    this.actasFilterString = '';
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
    this.actasFilterString = '';
    this.ubigeoService
      .listarDistritos(idEleccion, idAmbito, idUbigeoProvincias)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.distritos = resp.data;
          this.myFormUbigeo.get('cent_educativo').setValue(0, { emitEvent: false });
          this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });
        }
      });
  }

  cargarNombreEtiquetaNiveles(idAmbito): void {
    if (idAmbito == 1) {
      this.nombreEtiquetaNivel01 = this.getMensajeTraducido('niveles.departamento');
      this.nombreEtiquetaNivel02 = this.getMensajeTraducido('niveles.provincia');
      this.nombreEtiquetaNivel03 = this.getMensajeTraducido('niveles.distrito');
    } else if (idAmbito == 2) {
      this.nombreEtiquetaNivel01 = this.getMensajeTraducido('niveles.continente');
      this.nombreEtiquetaNivel02 = this.getMensajeTraducido('niveles.pais');
      this.nombreEtiquetaNivel03 = this.getMensajeTraducido('niveles.estado');
    }
  }

  inicioActualizacionComponentes(): void {
    this.myFormUbigeo.controls.region.valueChanges.subscribe((region) => {
      this.idAmbito = region as number;
      this.listarDepartamentos(this.idEleccion, region);
      this.cargarNombreEtiquetaNiveles(region);
      if (region === 1) {
        this.myFormUbigeo.get('departamento').enable();
        this.myFormUbigeo.get('provincia').enable();
        this.myFormUbigeo.get('distrito').enable();
        this.myFormUbigeo.get('cent_educativo').enable();
      }
    });

    this.myFormUbigeo.get('departamento').valueChanges.subscribe((departamento) => {
      if (!this.primeraVez) {
        this.myFormUbigeo.controls['distrito'].setValue('0', {
          emitEvent: false
        });

        this.deshabilitarBotonFiltrar = true;
        this.listarProvincias(this.idEleccion, this.idAmbito, departamento);
      }
    });

    this.myFormUbigeo.get('provincia').valueChanges.subscribe((provincia) => {
      this.localesVotacion = null;
      if (!this.primeraVez) {
        this.listarDistritos(this.idEleccion, this.idAmbito, provincia);
      }
    });

    this.myFormUbigeo.get('distrito').valueChanges.subscribe((distrito) => {
      const isInvalidDistrito = distrito === '0' || distrito == null;
      if (!this.primeraVez && !isInvalidDistrito) {
        this.listarLocalesVotacion(this.idEleccion, distrito);
      }
      this.deshabilitarBotonFiltrar = isInvalidDistrito;
      this.mostrarElementos = !isInvalidDistrito;
    });

    this.myFormUbigeo.get('cent_educativo').valueChanges.subscribe((centroEducativo) => {
      if (!this.primeraVez) {
        if (!(centroEducativo == null || centroEducativo == '0')) {
          this.deshabilitarBotonFiltrar = false;
        }
      }
    });
  }

  limpiar(): void {
    this.mostrarMyFromFiltro = false;
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

  limpiarDetalle(): void {
    this.detalleActa = null;
  }

  showLoading(): void {
    // Loading functionality removed
  }

  hideLoading(): void {
    setTimeout(() => {
      // Loading functionality removed
    }, 500);
  }

  filtrar(): void {
    this.actaService.filtrar(this);
    this.actaService.setControlsValues(this.myForm, ['filtro']);
    this.limpiarDatos();
    this.listarActasObservadas(null, true);
    if (this.observadaComponente != undefined) {
      this.observadaComponente.limpiarDetalle();
    }
  }

  limpiarDatos(): void {
    this.mapaCalor = null;
    this.totalPaginas = 0;
    this.totalPaginasReales = 0;
    this.totalRegistros = 0;
  }

  listarLocalesVotacion(idEleccion, idUbigeo): void {
    this.ubigeoService
      .listarLocales(idEleccion, idUbigeo)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.localesVotacion = result.data;
          this.myFormUbigeo.get('cent_educativo').setValue(0, { emitEvent: false });
        }
      });
  }

  paginado(numero): void {
    this.paginaAnterior = this.paginaActual;
    this.paginaActual = numero - 1;

    let resueltas = this.myForm.get('filtro').value;

    this.listarActasObservadas(resueltas);
  }

  listarActasObservadas(resueltas, mostrarMyFromFiltro: boolean = false): void {
    const idUbigeo = Number(this.myFormUbigeo.controls.distrito.value);
    if (idUbigeo) {
      let codigoLocalVotacion =
        this.myFormUbigeo.controls.cent_educativo.value == '0' ? null : this.myFormUbigeo.controls.cent_educativo.value;

      // Loading functionality removed
      this.mensaje = '';
      this.actas = [];
      this.listaPagina = [];
      this.totalRegistros = 0;

      const params: IGetActasObserbadasParams = {
        codigoLocalVotacion: (codigoLocalVotacion ?? 0) as string,
        idAmbitoGeografico: Number(this.myFormUbigeo.controls.region.value),
        idUbigeo: idUbigeo,
        resueltas: resueltas,
        descripcionActaResolucion: idUbigeo ? this.actasFilterString : null
      };

      this.actaService
        .listarActasObservadas(params, this.paginaActual, this.sizePagina)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.actas = result.data.content;
              this.totalRegistros = result.data.totalRegistros;
              this.totalPaginasReales = result.data.totalPaginas;

              this.datosActa = result.data;
              this.datosObservada = {
                actas: this.actas,
                totalPaginasReales: this.totalPaginasReales,
                totalRegistros: this.totalRegistros,
                totalPaginas: this.totalPaginas
              };
              if (this.totalRegistros === 0) {
                if (mostrarMyFromFiltro === true) {
                  this.mostrarMyFromFiltro = false;
                  this.mensaje = "actas-shared-mensaje-personaje.sinInfoBusquedaActaEnviadas"
                } else {                  
                  this.mensaje = "actas-shared-mensaje-personaje.sinInfoFiltroActaEnviadas"
                }
              } else {
                if (mostrarMyFromFiltro === true) {
                  this.mostrarMyFromFiltro = true;
                }
                this.mensaje = '';
                this.crearListaPaginado(this.totalRegistros, this.paginaActual);
              }
            } else {
              this.mensaje = '';
              this.actas = [];
            }
          },
          error: (err) => {
            this.mensaje = '';
            this.deshabilitarBotonFiltrar = false;
            // Loading functionality removed
            console.error('Error al listar actas observadas:', err);
          }
        });
    } else {
      this.mostrarMyFromFiltro = false;
      this.mensaje =
        this.myFormUbigeo.get('region')?.value === 2
          ? this.getMensajeTraducido('Para continuar, por favor seleccione un estado a consultar.')
          : this.getMensajeTraducido('Para continuar, por favor seleccione un distrito a consultar.');
      this.actas = [];
      this.totalRegistros = 0;
    }
  }

  listarParticipacionCiudadana(
    tipoFiltroP: string = 'ambito_geografico',
    idUbigeoDepartamentoP: number = 0,
    idUbigeoProvinciaP: number = 0,
    idUbigeoDistritoP: number = 0,
    tipoNivelAmbito: number = 0,
    ubigeo: string = ''
  ): void {
    // Loading functionality removed
    // Cancelar petición anterior en vuelo para evitar race conditions
    this.participacionCancel$.next();
    let codigoAgrupacionPolitica = 0;
    let idAmbitoGeografico = this.myFormUbigeo.controls.region.value;
    let idUbigeoDepartamento = idUbigeoDepartamentoP;
    let idUbigeoProvincia = idUbigeoProvinciaP;
    let idUbigeoDistrito = idUbigeoDistritoP;
    let tipoFiltro = tipoFiltroP;

    this.actaService
      .listarMapaCalorObservadas(
        codigoAgrupacionPolitica,
        idAmbitoGeografico as number,
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

          this.esEstranjero = !nacional;
          this.nivelUbigeo = tipoNivelAmbito;
          this.codigoUbigeo = ubigeo;

          // Loading functionality removed
        },
        error: (err) => {
          // Loading functionality removed
          console.error('Error al listar participación ciudadana:', err);
        }
      });
  }

  eventoPaginaAnterior(): void {
    this.listaPaginaTotal = [];
    let resueltas = this.myForm.get('filtro').value;
    this.paginaActual = this.listaPagina[0].pagina - 2;
    this.listarActasObservadas(resueltas);
  }

  eventoPaginaSiguiente(): void {
    this.listaPaginaTotal = [];
    let resueltas = this.myForm.get('filtro').value;
    this.paginaActual = this.listaPagina?.at(-1)?.pagina ?? 0;
    this.listarActasObservadas(resueltas);
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

  cambioUbigeo(data: string) {
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

  obtenerNombreEleccion(detalle: Mesa): string {
    if (detalle == null) {
      return '';
    }
    if (detalle.idEleccion == null) {
      return '';
    }
    let retorno = '';
    if (detalle.idEleccion == ID_ELECCION.ID_ELECCION_MUNICIPAL) {
      retorno = this.getMensajeTraducido('elecciones.municipal');
    }
    return retorno;
  }

  obtenerPorcentajeParticipacionCiudadana(detalle: Mesa): number {
    if (detalle == null) {
      return 0;
    }
    let calculo = (detalle.totalVotosEmitidos / detalle.totalElectoresHabiles) * 100;
    return calculo;
  }

  VerDetalles(mesa: Content, index: number): void {
    this.detalle = [];
    this.showLoading();
    this.actas.forEach(acta => {
        acta.esSeleccionado = acta.id === mesa.id;
    });
    this.DetalleActas = true;
    this.DetalleActasObs = false;
    this.mesaSeleccionado = mesa;
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
            mesa.codigoEstadoActa = this.detalleActa.codigoEstadoActa;
            this.actas[index] = mesa;
            this.codigoMesaForLineaTiempo = result.data.codigoMesa;
            const selectedConfigName = CONFIG_NAMES_FOR_ACTAS.find((e) => e.electionId == result.data.idEleccion);
            this.configNameForLineTiempo = selectedConfigName ? selectedConfigName.name : 'acta';

            this.loadElectionName(result.data.idEleccion);
            setTimeout(() => {
              this.scroller.scrollToAnchor('detalle-view-111');
            }, 500);
          }
        }
      });
    this.hideLoading();
  }

  crearListaPaginado(totalRegistros: number, pagina: number): void {
    pagina = pagina + 1;
    let totalRegistroPagina = 12;
    let totalPaginas = Math.ceil(totalRegistros / totalRegistroPagina);
    let contPagina = 1;
    let contaGrupo = 1;
    let totalPaginaPorGrupo = 5;
    let contPaginaAcumulado = 1;
    this.listaPaginaTotal = [];

    while (contPaginaAcumulado <= totalPaginas) {
      this.listaPaginaTotal.push({
        grupo: contaGrupo,
        pagina: contPaginaAcumulado,
        mostrarBotonAnterior: true,
        mostrarBotonSiguiente: true
      });
      contPagina = contPagina + 1;
      if (totalPaginaPorGrupo == contPagina) {
        contaGrupo = contaGrupo + 1;
        contPagina = 1;
      }
      contPaginaAcumulado = contPaginaAcumulado + 1;
    }

    let registro = this.listaPaginaTotal.find((x) => x.pagina == pagina);
    this.obtenerListaPaginaPorGrupo(this.listaPaginaTotal, registro.grupo, pagina);
  }

  obtenerListaPaginaPorGrupo(listaPaginaTotal, grupo, pagina): void {
    let maxGrupo = Math.max(...listaPaginaTotal.map((o) => o.grupo));
    let minGrupo = Math.min(...listaPaginaTotal.map((o) => o.grupo));
    if (grupo > minGrupo && grupo < maxGrupo) {
      this.actaService.setClassProperties(this, ['habilitarBotonSiguiente', 'habilitarBotonAnterior'], [true, true]);
    } else if (grupo == minGrupo && grupo < maxGrupo) {
      this.actaService.setClassProperties(this, ['habilitarBotonSiguiente', 'habilitarBotonAnterior'], [true, false]);
    } else if (grupo == minGrupo && grupo == maxGrupo) {
      this.actaService.setClassProperties(this, ['habilitarBotonSiguiente', 'habilitarBotonAnterior'], [false, false]);
    } else if (grupo > minGrupo && grupo == maxGrupo) {
      this.actaService.setClassProperties(this, ['habilitarBotonSiguiente', 'habilitarBotonAnterior'], [false, true]);
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

  mostrarNombreNivel(nivel: number, capital: boolean = false): string {
    return this.actaService.mostrarNombreNivel(this, nivel, capital);
  }

  public applyFilterForActas($event: string): void {
    this.mensaje = '';
    this.mostrarMyFromFiltro = true;
    this.actasFilterString = $event;
    let resueltas = this.myForm.get('filtro').value;
    this.limpiarDetalle();
    this.listarActasObservadas(resueltas);
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

  private setBreadcrumbStringForcePeru(): void {
    this.genericFilterUbigeoComponent.breadcrumbString = REGION_PERU;
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
    this.destroy$.unsubscribe();
  }
}
