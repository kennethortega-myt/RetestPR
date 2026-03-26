import { ChangeDetectorRef, ElementRef, Signal, SimpleChanges } from '@angular/core';
import { FormGroup, AbstractControl } from '@angular/forms';
import { MatSelect } from '@angular/material/select';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { RandomImageService } from '../services/elecciones-generales/random-image.service';
import { Content, Acta, Mesa, LineaTiempo, Archivo, Detalle } from './acta-bean';
import { CentEducativoBean } from './cent-educativo-bean';
import { LocalVotacion } from './elections.interfaces';
import { ParticipacionCiudadano } from './participacion-ciudadana.interfaces';
import { Resumen } from './resumen-bean';
import { MapaCalor } from './resumen-general-bean';
import { Ubigeo } from './ubigeo-bean';
import { AutocompleteInputComponent } from '../components/autocomplete-input/autocomplete-input.component';
import { ObservadaComponent } from '../components/actas-components/observada/observada.component';
import {
  ActaPorAmbitoDetalleArchivoResponse,
  ActaPorAmbitoDetalleDetalleResponse,
  ActaPorAmbitoDetalleResponse
} from './response/acta-por-ambito-response.interface';
import { GenericFilterUbigeoComponent } from '../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { ActasService } from '../services/elecciones-generales/actas.service';

export interface IEleccion {
  ID_ELECCION_MUNICIPAL: number;
  ID_ELECCION_GENERAL: number;
  ID_ELECCION_PRESIDENCIAL: number;
  ID_ELECCION_DIPUTADOS: number;
  ID_ELECCION_PARLAMENTO_ANDINO: number;
  ID_ELECCION_SENADOR_27: number;
  ID_ELECCION_SENADOR_33: number;
  ID_ELECCION_REVOCATORIA: number;
}

export interface IActaCodigoEstado {
  PENDIENTE: string;
  CONTABILIZADA: string;
  OBSERVADA: string;
  PARA_ENVIO_JEE: string;
}

export interface ActasSharedMapaCalor {
  ubigeo?: string;
  valor?: number;
}
export interface IActasComponent {
  route: ActivatedRoute;
  randomImageService: RandomImageService;
  randomImageUrl?: string;
  etiquetaMapa?: string;
  departamentos?: Ubigeo[];
  provincias?: Ubigeo[];
  distritos?: Ubigeo[];
  centrosEducativos?: Array<CentEducativoBean>;
  tiposActas?: boolean;
  listaResultado?: any[];
  actasDetalleLista?: boolean;
  DetalleActas?: boolean;
  DetalleActasObs?: boolean;
  destroy$?: Subject<boolean>;
  idAmbito?: number;
  deshabilitarBotonFiltrar?: boolean;
  deshabilitarBotonLimpiar?: boolean;
  actas?: Content[];
  ubigeo?: string;
  participantes?: [ParticipacionCiudadano];
  mapaCalor?: MapaCalor[];
  totalPaginas?: number;
  totalPaginasReales?: number;
  datoActa?: ActaPorAmbitoDetalleResponse;
  totalRegistros?: number;
  primeraVez?: boolean;
  primeraBusqueda?: boolean;
  datosActa?: Acta;
  paginaActual?: number;
  paginaAnterior?: number;
  habilitarBotonSiguiente?: boolean;
  habilitarBotonAnterior?: boolean;
  rangoMaximoPaginado?: number;
  grupoRangoPaginado?: number;
  valorMinimoRangoPaginado?: number;
  valorMaximoRangoPaginado?: number;
  cuentaVecesSeguiente?: number;
  participacionCiudadanaPorcentual?: number;
  listaPaginado?: any[];
  totalRegistroPorPagina?: number;
  loadingPaginacion?: boolean;
  ingresoTabDos?: boolean;
  cd?: ChangeDetectorRef;
  idEleccion?: number;
  resumen?: Resumen;
  localesVotacion?: LocalVotacion[];
  elecciones?: any[];
  ingresoTabTres?: boolean;
  detalleActa?: Mesa;
  observadaComponente?: ObservadaComponent;
  mapaCalorViewChild?: any; // Puede ser MapaCalorContabilizadaComponent o MainHotMapComponent
  esEstranjero?: boolean;
  nivelUbigeo?: number;
  codigoUbigeo?: string;
  genericFilterUbigeoComponent?: GenericFilterUbigeoComponent;
  enumIdAmbito?: string;
  nombreEtiquetaNivel01?: string;
  nombreEtiquetaNivel02?: string;
  nombreEtiquetaNivel03?: string;
  verFiltro?: boolean;
  datosObservada?: {};
  listaPaginaTotal?: any[];
  listaPagina?: any[];
  sizePagina?: number;
  codigoMesaForLineaTiempo?: string;
  configNameForLineTiempo?: string;
  lineaTiempo?: [LineaTiempo];
  listaArchivos?: [Archivo];
  detalle?: Detalle[] | ActaPorAmbitoDetalleDetalleResponse[];
  archivos?: [ActaPorAmbitoDetalleArchivoResponse];

  mensajeInicial?: string;
  mensajeInicialExtranjero?: string;
  messagePersonageStranger?: MessagePersonageStranger;
  mensaje?: string;
  ID_ELECCION?: IEleccion;
  ACTA_CODIGO_ESTADO?: IActaCodigoEstado;
  ACTA_CODIGO_ESTADO_DESC?: IActaCodigoEstado;
  mesaSeleccionado?: Content;
  selectDepartamento?: MatSelect | ElementRef;
  selectProvincia?: MatSelect | ElementRef;
  selectDistrito?: MatSelect | ElementRef;
  autoComplete?: AutocompleteInputComponent<LocalVotacion>;

  mostrarMyFromFiltro?: boolean;
  ubigeoMapaSeleccionado?: string;
  myForm?: FormGroup;
  myFormUbigeo?: FormGroup;
  temporalFiltro?: string;
  fragment?: string;

  controlEleccion?: AbstractControl;
  controlAmbito?: AbstractControl;
  controlDepartamento?: AbstractControl;
  controlProvincia?: AbstractControl;
  controlDistrito?: AbstractControl;
  // actaMapaCalor?: Signal<any>;
  actaMapaCalor?: Signal<ActasService>;
  mostrarElementos?: boolean;

  ngOnInit?(): void;
  ngAfterViewInit?(): void;
  ngOnChanges?(changes: SimpleChanges): void;
  seleccionarPeru?(data: string): void;
  listarActas?(): void;
  crearListaPaginado?(totalRegistros: number, pagina: number): void;
  obtenerListaPaginaPorGrupo?(listaPaginaTotal: void, grupo: void, pagina: void): void;
  listarActasPorPagina?(idUbigeo: void, codigoLocalVotacion: void, pagina: void): void;
  eventoPaginaSiguiente?(): void;
  eventoPaginaAnterior?(): void;
  filtrar?(): void;
  iniciarEventoValuesChange?(): void;
  VerActasdDetalle?(): void;
  VerDetalles?(mesa?: Content, index?: number): void;
  obtenerNombreEleccion?(detalle: Mesa): string;
  listarDepartamentos?(idEleccion: number, idAmbito: number): void;
  listarProvincias?(
    idEleccion: number,
    idAmbito: number,
    idUbigeoDepartamento: string,
    idProviciaSeleccionada?: string
  ): void;
  listarDistritos?(idEleccion: number, idAmbito: number, idUbigeoProvincias: string): void;
  listarParticipacionCiudadanaPorTipo?(tipo: number): void;
  retornarClaseCssSegunEstado?(acta: Content): string;
  listarLocalesVotacion?(idEleccion: void, idUbigeo: string): void;
  paginado?(numero: void): void;
  limpiar?(): void;
  obtenerPorcentajeParticipacionCiudadana?(detalle: Mesa): number;
  eventoMapBotton?(event: void): void;
  limpiarPaginado?(): void;
  mostrarNombreNivel01?(): string;
  mostrarNombreNivel02?(): string;
  mostrarNombreNivel03?(): string;
  navigateToSection?(section: string): void;
  cargarNombreEtiquetaNiveles?(idAmbito: number): void;
  inicioActualizacionComponentes?(): void;
  limpiarDetalle?(): void;
  limpiarDatos?(): void;
  listarActasObservadas?(resueltas?: void, mostrarMyFromFiltro?: boolean): void;
  setControlsValues?(form: FormGroup, keys: string[], values?: (string | number)[]): void;
  setClassProperties?(keys: string[], values?: any[]): void;
  cambioUbigeo?(data: string, cambioAmbito: boolean): void;
  isDataInvalid?(data: string): boolean;
  processUbigeo?(data: string): void;
  processDepartamento?(data: string): void;
  processProvinciaODistrito?(data: string, extraer: string): void;
  mostrarNombreNivel?(nivel: number): string;
  listarParticipacionCiudadana?(
    tipoFiltroP?: string,
    idUbigeoDepartamentoP?: number,
    idUbigeoProvinciaP?: number,
    idUbigeoDistritoP?: number,
    tipoNivelAmbito?: number,
    ubigeo?: string
  ): void;
  listarParticipacionCiudadanaPorMapa?(ubigeo?: string): void;
  obtenerUbigeosParticipates?(departamentos: Ubigeo[]);
}

export interface MessagePersonageStranger {
  nationality: MessagePersonage;
  stranger: MessagePersonage;
}

export interface MessagePersonage {
  region: string;
  province: string;
  district: string;
}
