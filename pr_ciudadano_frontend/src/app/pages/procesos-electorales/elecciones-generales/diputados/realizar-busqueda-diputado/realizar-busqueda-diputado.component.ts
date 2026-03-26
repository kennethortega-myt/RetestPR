import { Component, inject, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { distinctUntilChanged, forkJoin, pairwise, startWith, Subject, take, takeUntil } from 'rxjs';
import { MENSAJE_REPORTE, TIPO_FILTRO } from '../../../../../helpers/constantes';
import { descargarPdf } from '../../../../../helpers/funciones';
import { Agrupacion, Candidato } from '../../../../../interfaces/eleccion-congresal-bean';
import { GenericFilterParams } from '../../../../../interfaces/filtro-settings';
import { IDescargarPdfCommonParams } from '../../../../../interfaces/reporte.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { DistritoElectoral } from '../../../../../interfaces/ubigeo-bean';
import { BehaviorResumenService } from '../../../../../services/elecciones-generales/behavior-resumen.service';
import { EleccionCongresalService } from '../../../../../services/elecciones-generales/eleccion-congresal.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { ReporteService } from '../../../../../services/elecciones-generales/reporte.service';
import { SnackbarService } from '../../../../../services/elecciones-generales/snackbar.service';
import { UbigeoService } from '../../../../../services/elecciones-generales/ubigeo.service';
import { encryptStorageEleccion } from '../../../../../settings/encrypt-storage.settings';
import { DatosOP } from '../../../../../interfaces/presidenciales.interfaces';

@Component({
  selector: 'app-realizar-busqueda-diputado',
  templateUrl: './realizar-busqueda-diputado.component.html',
  styleUrls: ['./realizar-busqueda-diputado.component.scss'],
  standalone: false
})
export class RealizarBusquedaDiputadoComponent implements OnInit, OnDestroy {
  public randomImageUrl: string;
  listRegion: DistritoElectoral[] = [];
  listAgrupacion: Agrupacion[] = [];
  listCandidato: Candidato[] = [];
  compactListPorCandidato: Candidato[] = [];
  datosOP?: DatosOP;
  listCandidatoOrigin: Candidato[] = [];
  destroy$: Subject<boolean> = new Subject<boolean>();
  verFiltroInicial: boolean = true;
  deshabilitarBotonBuscar: boolean = true;
  deshabilitarBotonBuscarOrganizacion: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  deshabilitarInputOrganizacionPolitica: boolean = true;
  listOpcion: { id: number; nombre: string }[] = [
    { id: 1, nombre: 'Por nombre de organización política' },
    { id: 2, nombre: 'Por nombre de candidato' }
  ];
  @Input({ required: true }) datos: { resumen: Resumen; idEleccion };
  private formBuilder = inject(FormBuilder);
  resumen: Resumen;
  idEleccion = 0;
  primeraCargaCandidato: boolean = true;
  verInput: boolean = false;
  mensaje: string = 'Todavía no se cuenta con información para mostrar en la opción seleccionada.';
  mensajeInterno: string = '';
  public myFormUbigeo: FormGroup = this.formBuilder.group({
    region: ['0', Validators.required],
    organizacion: ['0', Validators.required],
    opcion: [1],
    nomCandidato: [{ value: null, disabled: true }]
  });

  deshabilitarBotonGenerarReporte: boolean = true;
  valorFiltroDistritoElectoral: number = null;
  valorFiltroOrganizacion: number = null;
  valorFiltroOrganizacionButtonReporte: number = null;
  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;

  constructor(
    private readonly ubigeoService: UbigeoService,
    private readonly eleccionCongresalService: EleccionCongresalService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService,
    public dialog: MatDialog,
    private readonly randomImageService: RandomImageService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  ngOnInit(): void {
    this.cargaInicialDatos();
    this.randomImageUrl = this.randomImageService.getRandomImage();
  }

  cargaInicialDatos(): void {
    this.resumen = this.datos.resumen;
    this.idEleccion = this.datos.idEleccion;
    // Loading functionality removed
    let idDistritoElectoral: number = this.resumen.idUbigeoDistritoElectoral;
    this.valorFiltroDistritoElectoral = this.resumen.idUbigeoDistritoElectoral;
    let idEleccion: number = this.idEleccion;
    let nombreApellidoPartido: string = null;
    let tipoFiltro: string = TIPO_FILTRO.DISTRITO_ELECTORAL;
    this.mensaje = '';
    this.mensajeInterno = '';

    const serviciosCombinados = forkJoin({
      regiones: this.ubigeoService.listarDistritoElectorales().pipe(takeUntil(this.destroy$)),
      organizaciones: this.eleccionCongresalService
        .listarOrganizacionesPorBusqueda(idDistritoElectoral, idEleccion, nombreApellidoPartido, tipoFiltro)
        .pipe(takeUntil(this.destroy$))
    });
    serviciosCombinados.subscribe({
      next: (result) => {
        this.listRegion = result.regiones.data;
        this.myFormUbigeo.get('region').setValue(this.resumen.idUbigeoDistritoElectoral);
        if (result.organizaciones.success) {
          this.listAgrupacion = result.organizaciones.data;
          if (this.listAgrupacion.length > 0) {
            this.mensajeInterno =
              'Para continuar, por favor seleccione una organización política y presione el botón FILTRAR.';
          } else {
            this.mensaje = 'Todavía no se cuenta con información para mostrar en la opción seleccionada.';
          }
        } else {
          this.mensaje = 'Todavía no se cuenta con información para mostrar en la opción seleccionada.';
        }

        this.cargarInitEvent();
        // Loading functionality removed
      },
      error: (err) => {
        // Loading functionality removed
      }
    });
  }

  cargarInitEvent(): void {
    this.myFormUbigeo
      .get('region')
      .valueChanges.pipe(startWith(null as string), pairwise())
      .subscribe({
        next: (_) => {
          this.listAgrupacion = [];
          this.deshabilitarBotonBuscarOrganizacion = false;
          this.verInput = false;
          this.mensaje = 'Para continuar, por favor presione el botón FILTRAR.';
          this.mensajeInterno = '';
          this.primeraCargaCandidato = true;
          this.limpiarTodo();
        }
      });
    this.myFormUbigeo
      .get('opcion')
      .valueChanges.pipe(startWith(null as string), pairwise())
      .subscribe({
        next: ([_, next]: Array<number>) => {
          if (next == 1) {
            this.verFiltroInicial = true;
            this.myFormUbigeo.get('nomCandidato').setValue(null, { emitEvent: false });
            this.deshabilitarBotonLimpiar = true;
          } else if (next == 2) {
            this.verFiltroInicial = false;
          }
        }
      });

    this.myFormUbigeo
      .get('organizacion')
      .valueChanges.pipe(startWith(0), distinctUntilChanged())
      .subscribe({
        next: (value: number) => {
          if (value == 0) {
            this.myFormUbigeo.get('nomCandidato').disable({ emitEvent: false });
            this.deshabilitarBotonBuscar = true;
            this.deshabilitarBotonGenerarReporte = true;
          } else {
            this.myFormUbigeo.get('nomCandidato').enable({ emitEvent: false });
            this.deshabilitarBotonBuscar = false;
            this.deshabilitarBotonGenerarReporte = true;
          }
          this.primeraCargaCandidato = true;
          this.verInput = false;
          this.listCandidato = [];
          this.mensajeInterno = 'Para continuar, por favor seleccione una organización política.';
          this.myFormUbigeo.get('nomCandidato').setValue('', { emitEvent: false });
        }
      });

    this.myFormUbigeo
      .get('nomCandidato')
      .valueChanges.pipe(startWith(null as string), pairwise())
      .subscribe({
        next: ([_, next]: Array<string>) => {
          if (next) {
            this.listCandidato = this.listCandidatoOrigin.filter((x) =>
              x.nombreCandidato.toLowerCase().includes(next.toLocaleLowerCase())
            );
            if (this.listCandidato.length == 0) {
              this.mensajeInterno =
                'Lo sentimos, no se encontraron resultados que coincidan con su búsqueda. Por favor, verifique los criterios ingresados e inténtelo nuevamente.';
            }
            this.deshabilitarBotonLimpiar = false;
          } else {
            this.listCandidato = this.listCandidatoOrigin;
            this.mensaje = '';
            this.mensajeInterno = '';
            this.deshabilitarBotonBuscar = true;
          }
        }
      });
  }

  listarCandidato(codigoAgrupacionPolitica: number, nombreCandidato?: string): void {
    let idAgrupacionPolitica = codigoAgrupacionPolitica;
    let idEleccion = this.idEleccion;
    let nombreApellido = this.myFormUbigeo.get('nomCandidato').value;
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let idDistritoElectoral = this.myFormUbigeo.get('region').value;
    // Loading functionality removed
    this.listCandidatoOrigin = [];
    this.listCandidato = [];
    this.verInput = false;
    this.mensaje = '';
    this.mensajeInterno = '';
    this.datosOP = null;
    this.eleccionCongresalService
      .listarCandidatosPorAgrupacionPolitica(
        idAgrupacionPolitica,
        idDistritoElectoral,
        idEleccion,
        nombreApellido,
        tipoFiltro
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listCandidato = result.data;
            this.compactListPorCandidato = result.data.slice(0, 1);
            const responseOP = {
            totalVotosPorOP: result.totalVotosPorOP,
            porcentajeVotoEmitido: result.porcentajeVotoEmitido,
            porcentajeVotoValido: result.porcentajeVotoValido  };
            this.datosOP = responseOP;
            this.listCandidatoOrigin = result.data;
            this.verInput = true;
            this.deshabilitarBotonGenerarReporte = false;
          } else {
            this.deshabilitarBotonGenerarReporte = true;
            this.mensajeInterno = 'Para continuar, por favor seleccione una organización política.';
          }
          this.primeraCargaCandidato = false;
          // Loading functionality removed
        },
        error: (_) => {
          // Loading functionality removed
        }
      });
  }

  listarCandidatoPorNombre(): void {
    let nombreApellido = this.myFormUbigeo.get('nomCandidato').value;

    if (!this.myFormUbigeo.valid) {
      return;
    }

    this.listCandidato = this.listCandidatoOrigin.filter((x) =>
      x.nombreCandidato.toLowerCase().includes(nombreApellido.toLowerCase())
    );
  }

  buscarOrganizacion(): void {
    if (this.valorFiltroDistritoElectoral != this.myFormUbigeo.get('region').value) {
      this.valorFiltroDistritoElectoral = this.myFormUbigeo.get('region').value;
    } else {
      return;
    }
    this.deshabilitarBotonBuscarOrganizacion = true;
    this.deshabilitarBotonGenerarReporte = true;
    this.valorFiltroOrganizacion = 0;
    this.mensaje = '';
    this.listAgrupacion = [];
    this.myFormUbigeo.get('organizacion').setValue(null);
    this.mensajeInterno = '';
    this.behaviorResumenService.setActualizarResumen(this.myFormUbigeo.get('region').value);
    this.eleccionCongresalService
      .listarOrganizacionesPorBusqueda(
        this.myFormUbigeo.get('region').value,
        this.idEleccion,
        null,
        TIPO_FILTRO.DISTRITO_ELECTORAL
      )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.listAgrupacion = result.data;
            this.mensaje = '';

            this.mensajeInterno = 'Para continuar, por favor seleccione una organización política';
          } else {
            this.mensaje = 'Todavía no se cuenta con información para mostrar en la opción seleccionada.';
          }
        }
      });
  }

  buscar(): void {
    const organizacionValue = this.myFormUbigeo.get('organizacion').value;
    if (organizacionValue == 0) {
      this.valorFiltroOrganizacion = null;
      return;
    }

    if (this.valorFiltroOrganizacion === organizacionValue) {
      return;
    }

    this.valorFiltroOrganizacion = organizacionValue;

    this.deshabilitarBotonLimpiar = true;
    this.deshabilitarBotonBuscar = true;
    this.deshabilitarBotonGenerarReporte = true;

    this.listarCandidato(organizacionValue);
  }

  limpiarTodo(): void {
    this.listCandidato = [];
    this.myFormUbigeo.get('organizacion').setValue(0, { emitEvent: false });
  }

  limpiarLista(): void {
    this.myFormUbigeo.get('nomCandidato').setValue(null, { emitEvent: false });
    this.listCandidato = this.listCandidatoOrigin;
    this.primeraCargaCandidato = true;
    this.deshabilitarBotonLimpiar = true;
    this.mensaje = '';
    this.mensajeInterno = '';
  }

  descargarReporte(tipoReporte: number): void {
    const organizacionValue = this.myFormUbigeo.get('organizacion').value;
    if (organizacionValue === 0) {
      return;
    }
    const shouldResetFiltro = this.valorFiltroOrganizacionButtonReporte !== organizacionValue;
    const isFirstPdfClick = tipoReporte === 1 && !this.buttonReportePdfclick;
    const isFirstCsvClick = tipoReporte === 2 && !this.buttonReporteCsvclick;
    if (shouldResetFiltro || isFirstPdfClick || isFirstCsvClick) {
      this.valorFiltroOrganizacionButtonReporte = organizacionValue;
    } else {
      this.snackbarService.showSnackbarWithSuccessMessage('La descarga es una vez por ubicación.', 'ambar');
      return;
    }

    if (tipoReporte === 1) this.buttonReportePdfclick = true;
    if (tipoReporte === 2) this.buttonReporteCsvclick = true;

    const regionValue = this.myFormUbigeo.get('region').value;
    const idAmbitoGeografico = regionValue > 26 ? 2 : 1;
    const idDistritoElectoral = regionValue;
    const idEleccion = this.idEleccion;

    let objProceso = JSON.parse(encryptStorageEleccion.getItem('PROCESO_ELECTORAL_ACTIVO'));
    let tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    let nombreProceso = objProceso.nombre;
    let nombreEleccion = 'Elección Congresal / Realizar búsqueda';
    let idAgrupacionPolitica = organizacionValue;

    // Loading functionality removed

    const params: IDescargarPdfCommonParams = {
      tipoReporte,
      idAgrupacionPolitica,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      nombreProceso,
      nombreEleccion,
      tipoFiltro
    };

    this.reporteService
      .descargarPdfEleccionDiputadosCandidatoOrganizacion(params)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          // Loading functionality removed
          descargarPdf('Reporte_Congresales_Busqueda', result);
          this.snackbarService.showSnackbarWithSuccessMessage();
        },
        error: (_) => {
          // Loading functionality removed
          this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, 'ambar');
        }
      });
  }

  cargarResumen(): void {
    this.behaviorResumenService.setActualizarResumen(this.myFormUbigeo.get('region').value);
  }

  onChangeRegion(): void {
    this.buscarOrganizacion();
  }

  filtrar(params: GenericFilterParams): void {
    const region_value = params?.electoralDistrictId;
    if (region_value == null) {
      return;
    }
    this.myFormUbigeo.get('region')?.setValue(region_value, { emitEvent: false });
    this.buscarOrganizacion();
  }
}
