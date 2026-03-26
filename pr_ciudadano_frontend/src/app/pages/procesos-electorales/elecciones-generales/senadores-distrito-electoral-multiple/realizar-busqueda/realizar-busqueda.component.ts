import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subject, distinctUntilChanged, forkJoin, pairwise, startWith, take, takeUntil } from "rxjs";

import {
  TIPO_FILTRO,
  MENSAJE_REPORTE,
} from "../../../../../helpers/constantes";
import { descargarPdf } from "../../../../../helpers/funciones";
import { Candidato } from "../../../../../interfaces/eleccion-congresal-bean";
import { IDescargarPdfCommonParams } from "../../../../../interfaces/reporte.interfaces";
import { Resumen } from "../../../../../interfaces/resumen-bean";
import { encryptStorageEleccion } from "../../../../../settings/encrypt-storage.settings";
import { DistritoElectoral } from "../../../../../interfaces/ubigeo-bean";
import { BehaviorResumenService } from "../../../../../services/elecciones-generales/behavior-resumen.service";
import { RandomImageService } from "../../../../../services/elecciones-generales/random-image.service";
import { ReporteService } from "../../../../../services/elecciones-generales/reporte.service";
import { SnackbarService } from "../../../../../services/elecciones-generales/snackbar.service";
import { UbigeoService } from "../../../../../services/elecciones-generales/ubigeo.service";
import { SenadoresDistritoElectoralMultipleService } from "../../../../../services/elecciones-generales/senadores-distrito-electoral-multiple.service";
import { GenericFilterUbigeoComponent } from "../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component";
import { GenericFilterParams } from "../../../../../interfaces/filtro-settings";
import { DatosOP } from "../../../../../interfaces/presidenciales.interfaces";

@Component({
  selector: "app-realizar-busqueda",
  templateUrl: "./realizar-busqueda.component.html",
  styleUrls: ["./realizar-busqueda.component.scss"],
  standalone: false,
})
export class RealizarBusquedaComponent implements OnInit, OnDestroy {
  // imagen aleatoria
  public randomImageUrl: string;
  @Input({ required: true }) datos: { resumen: Resumen; idEleccion: number };
  listRegion: DistritoElectoral[] = [];
  listAgrupacion: Candidato[] = [];
  listCandidato: Candidato[] = [];
  compactListPorCandidato: Candidato[] = [];
  listCandidatoOrigin: Candidato[] = [];
  destroy$: Subject<boolean> = new Subject<boolean>();
  verFiltroInicial: boolean = true;
  deshabilitarBotonBuscar: boolean = true;
  deshabilitarBotonBuscarOrganizacion: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  deshabilitarInputOrganizacionPolitica: boolean = true;

  resumen: Resumen;
  idEleccion = 0;
  primeraCarga: boolean = true;
  primeraCargaCandidato: boolean = true;
  primeraCargaCandidatoLocal: boolean = true;
  verInput: boolean = false;
  responseOrganizacion?: DatosOP;

  mensaje: string =
    "Todavía no se cuenta con información para mostrar en la opción seleccionada.";
  @ViewChild(GenericFilterUbigeoComponent, { static: false }) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  isUbigeoReady = false;
  public myFormUbigeo: FormGroup = this.fb.group({
    region: [0, Validators.required],
    organizacion: [0  , Validators.required],
    opcion: [1],
    nomCandidato: [{ value: null, disabled: true }],
  });
  deshabilitarBotonGenerarReporte: boolean = true;
  valorFiltroDistritoElectoral: number = null;
  valorFiltroOrganizacion: number = null;
  valorFiltroOrganizacionButtonReporte: number = null;
  buttonReportePdfclick: boolean = false;
  buttonReporteCsvclick: boolean = false;

  @Output() updateResumenGeneral = new EventEmitter<number>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly ubigeoService: UbigeoService,
    private readonly behaviorResumenService: BehaviorResumenService,
    private readonly senadoresDistritoElectoralMultipleService: SenadoresDistritoElectoralMultipleService,
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService,
    // imagen aleatoria
    private readonly randomImageService: RandomImageService
  ) {}
  ngOnInit(): void {
    this.cargaInicialDatos();
    // imagen aleatoria
    this.randomImageUrl = this.randomImageService.getRandomImage();
  }

  filtrar(params: GenericFilterParams): void {
    const regionValue = Number(params?.electoralDistrictId);
    if (!regionValue) return;

    if (this.valorFiltroDistritoElectoral === regionValue) return;

    this.valorFiltroDistritoElectoral = regionValue;

    this.myFormUbigeo.get("region")?.setValue(regionValue, { emitEvent: false });

    if (this.mainFiltroUbigeoComponent) {
      this.mainFiltroUbigeoComponent.setElectoralRegion(regionValue);
      this.mainFiltroUbigeoComponent.updateBreadcrumbStringRegion();
    }

    this.resetPorCambioRegion();

    setTimeout(() => {
      // Loading functionality removed
      this.buscarOrganizacion();
    }, 0);
  }

  cargaInicialDatos(): void {
    let idAgrupacionPolitica: number = 0;
    this.resumen = this.datos.resumen;
    this.idEleccion = this.datos.idEleccion;
    // Loading functionality removed
    let ubigeoNivel1 = 0;
    let idDistritoElectoral: number = this.resumen.idUbigeoDistritoElectoral;
    this.valorFiltroDistritoElectoral = this.resumen.idUbigeoDistritoElectoral;
    let nombreApellidoPartido: string = null;
    let tipoFiltro: string = TIPO_FILTRO.DISTRITO_ELECTORAL;
    const serviciosCombinados = forkJoin({
      regiones: this.ubigeoService
        .listarDistritoElectorales()
        .pipe(takeUntil(this.destroy$)),
      organizaciones: this.senadoresDistritoElectoralMultipleService
        .listarOrganizacionPolitica({
          idDistritoElectoral: idDistritoElectoral,
          idEleccion: this.idEleccion,
          tipoFiltro: tipoFiltro,
        })
        .pipe(takeUntil(this.destroy$)),
    });
    serviciosCombinados.subscribe({
      next: (result) => {
        this.listRegion = result.regiones.data;
        if (result.organizaciones.success) {
          this.listAgrupacion = result.organizaciones.data;
          this.mensaje =
            "Para continuar, por favor seleccione una organización política y presione el botón FILTRAR.";
        } else {
          this.mensaje =
            "Todavía no se cuenta con información para mostrar en la opción seleccionada.";
        }

        const initialRegion = Number(this.resumen.idUbigeoDistritoElectoral);

        this.myFormUbigeo.get("region")?.setValue(initialRegion, { emitEvent: false });

        this.isUbigeoReady = true;

        setTimeout(() => {
          if (!this.mainFiltroUbigeoComponent) return;

          this.mainFiltroUbigeoComponent.setElectoralRegion(initialRegion);
          this.buscarOrganizacion();
        }, 0);

        this.cargarInitEvent();
        this.primeraCarga = false;
      }
    });
  }

  private resetPorCambioRegion(): void {
    this.listAgrupacion = [];
    this.listCandidato = [];
    this.listCandidatoOrigin = [];
    this.compactListPorCandidato = [];
    this.responseOrganizacion = null;
    this.verInput = false;

    this.deshabilitarBotonBuscarOrganizacion = true;
    this.deshabilitarBotonBuscar = true;
    this.deshabilitarBotonGenerarReporte = true;
    this.deshabilitarBotonLimpiar = true;

    // resetea organización y candidato sin disparar eventos
    this.myFormUbigeo.get("organizacion")?.setValue(null);
    this.myFormUbigeo.get("nomCandidato")?.setValue(null, { emitEvent: false });
    this.myFormUbigeo.get("nomCandidato")?.disable({ emitEvent: false });

    this.mensaje = "Para continuar, por favor seleccione una organización política.";

    this.valorFiltroOrganizacion = 0;
    this.valorFiltroOrganizacionButtonReporte = null;
    this.buttonReportePdfclick = false;
    this.buttonReporteCsvclick = false;
  }

  cargarInitEvent(): void {
    this.myFormUbigeo
      .get("opcion")
      ?.valueChanges
      .pipe(
        startWith(null as any),
        pairwise(),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: ([prev, next]: Array<number>) => {
          if (next === 1) {
            this.verFiltroInicial = true;
            this.myFormUbigeo.get("nomCandidato")?.setValue(null, { emitEvent: false });
            this.deshabilitarBotonLimpiar = true;
          } else if (next === 2) {
            this.verFiltroInicial = false;
          }
        },
      });

    this.myFormUbigeo
      .get("organizacion")
      ?.valueChanges
      .pipe(
        startWith(0),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (value: number) => {
          if (value === 0) {
            this.myFormUbigeo.get("nomCandidato")?.disable({ emitEvent: false });
            this.deshabilitarBotonBuscar = true;
            this.deshabilitarBotonGenerarReporte = true;
            this.mensaje = "Para continuar, por favor seleccione una organización política.";
          } else {
            this.myFormUbigeo.get("nomCandidato")?.enable({ emitEvent: false });
            this.deshabilitarBotonBuscar = false;
            this.deshabilitarBotonGenerarReporte = false;
            this.mensaje = "";
          }

          this.myFormUbigeo.get("nomCandidato")?.setValue("", { emitEvent: false });
          this.primeraCargaCandidato = true;
          this.verInput = false;
          this.listCandidato = [];
        },
      });

    this.myFormUbigeo
      .get("nomCandidato")
      ?.valueChanges
      .pipe(
        startWith(null as any),
        pairwise(),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: ([prev, next]: Array<string>) => {
          if (next) {
            this.listCandidato = this.listCandidatoOrigin.filter((x) =>
              x.nombreCandidato.toLowerCase().includes(next.toLowerCase())
            );

            if (this.listCandidato.length === 0) {
              this.mensaje =
                "Lo sentimos, no se encontraron resultados que coincidan con su búsqueda. Por favor, verifique los criterios ingresados e inténtelo nuevamente.";
            } else {
              this.mensaje = "";
            }
            this.deshabilitarBotonLimpiar = false;
          } else {
            this.listCandidato = this.listCandidatoOrigin;
            this.deshabilitarBotonBuscar = true;
          }
        },
      });
  }
  buscarOrganizacion(): void {
    const regionValue = Number(this.myFormUbigeo.get("region")?.value);
    if (!regionValue) return;

    this.updateResumenGeneral.emit(regionValue);
    this.deshabilitarBotonBuscarOrganizacion = true;
    this.deshabilitarBotonGenerarReporte = true;

    const idDistritoElectoral = regionValue;
    const idEleccion = this.idEleccion;
    const tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;

    this.behaviorResumenService.setActualizarResumen(regionValue);
    this.valorFiltroOrganizacion = 0;

    this.senadoresDistritoElectoralMultipleService
      .listarOrganizacionPolitica({ idDistritoElectoral, idEleccion, tipoFiltro })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.deshabilitarBotonBuscarOrganizacion = false;
          // Loading functionality removed

          if (result.success) {
            this.listAgrupacion = result.data;
            this.myFormUbigeo.get("organizacion")?.setValue(0, { emitEvent: true });
            this.mensaje = "Para continuar, por favor seleccione una organización política.";
          } else {
            this.listAgrupacion = [];
            this.mensaje = "Todavía no se cuenta con información para mostrar en la opción seleccionada.";
          }
        },
        error: () => {
          this.deshabilitarBotonBuscarOrganizacion = false;
          // Loading functionality removed
        }
      });
  }

  buscar(): void {
    const organizacionValue = this.myFormUbigeo.get("organizacion").value;

    if (organizacionValue == 0) {
      this.valorFiltroOrganizacion = null;
      return;
    }

    if (this.valorFiltroOrganizacion !== organizacionValue) {
      this.valorFiltroOrganizacion = organizacionValue;
    } else {
      return;
    }

    this.deshabilitarBotonLimpiar = true;
    this.deshabilitarBotonBuscar = true;
    this.deshabilitarBotonGenerarReporte = true;

    this.listarCandidato(organizacionValue);
  }

  listarCandidato(
    codigoAgrupacionPolitica: number,
    nombreCandidato?: string
  ): void {
    const idAgrupacionPolitica = codigoAgrupacionPolitica;
    const idEleccion = this.idEleccion;
    const tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    const idDistritoElectoral = this.myFormUbigeo.get("region").value;

    // Loading functionality removed
    this.resetCandidatoLists();

    this.senadoresDistritoElectoralMultipleService
      .listarParticipantesCandidatoOrganizacion({
        idAgrupacionPolitica,
        idDistritoElectoral,
        idEleccion,
        tipoFiltro,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => this.handleCandidatoResult(result)
      });
  }

  private resetCandidatoLists(): void {
    this.listCandidatoOrigin = [];
    this.listCandidato = [];
    this.verInput = false;
    this.responseOrganizacion = null;
  }

  private handleCandidatoResult(result: any): void {
    // Loading functionality removed
    if (result.success) {
      this.listCandidato = result.data;
      this.compactListPorCandidato = result.data.slice(0,1);
      const responseOrganizacion = {
        totalVotosPorOP: result.totalVotosPorOP,
        porcentajeVotoEmitido: result.porcentajeVotoEmitido,
        porcentajeVotoValido: result.porcentajeVotoValido  };
      this.responseOrganizacion = responseOrganizacion;      
      this.listCandidatoOrigin = result.data;
      this.verInput = true;

      this.deshabilitarBotonGenerarReporte = false;
      this.primeraCargaCandidato = false;
      this.primeraCargaCandidatoLocal = true;
    } else {
      this.primeraCarga = true;
      this.primeraCargaCandidato = false;
      this.primeraCargaCandidatoLocal = false;
      this.mensaje =
        "Para continuar, por favor seleccione una organización política.";
    }
  }

  limpiarLista(): void {
    this.myFormUbigeo.get("nomCandidato").setValue(null, { emitEvent: false });
    this.listCandidato = this.listCandidatoOrigin;
    this.primeraCargaCandidato = true;
    this.deshabilitarBotonLimpiar = true;
  }

  limpiarTodo(): void {
    this.primeraCarga = false;
    this.listCandidato = [];
    this.myFormUbigeo.get("organizacion").setValue(0, { emitEvent: false });
  }

  descargarReporte(tipoReporte: number): void {
    const regionValue = this.myFormUbigeo.get("region").value;
    const organizacionValue = this.myFormUbigeo.get("organizacion").value;

    if (organizacionValue === 0) {
      return;
    }

    if (this.shouldUpdateFiltro(regionValue, tipoReporte)) {
      this.valorFiltroOrganizacionButtonReporte = regionValue;
    } else {
      this.snackbarService.showSnackbarWithSuccessMessage(
        "La descarga es una vez por ubicación.",
        "ambar"
      );
      return;
    }

    this.updateButtonClickState(tipoReporte);

    const idAmbitoGeografico = regionValue > 26 ? 2 : 1;
    const idDistritoElectoral = regionValue;
    const idEleccion = this.idEleccion;

    const objProceso = JSON.parse(
      encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO")
    );
    const tipoFiltro = TIPO_FILTRO.DISTRITO_ELECTORAL;
    const nombreProceso = objProceso.nombre;
    const nombreEleccion = "Elección de Senadores 27 / Realizar búsqueda";
    const idAgrupacionPolitica = organizacionValue;

    // Loading functionality removed

    const params: IDescargarPdfCommonParams = {
      tipoReporte,
      idAgrupacionPolitica,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      nombreProceso,
      nombreEleccion,
      tipoFiltro,
    };

    this.reporteService
      .descargarPdfEleccionSenadoresMultipleCandidatoOrganizacion(params)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          // Loading functionality removed
          descargarPdf("REALIZAR_BUSQUEDA", result);
          this.snackbarService.showSnackbarWithSuccessMessage();
        },
        error: (err) => {
          // Loading functionality removed
          this.snackbarService.showSnackbarWithSuccessMessage(
            MENSAJE_REPORTE.SIN_FIRMA_DIGITAL,
            "ambar"
          );
        },
      });
  }

  private shouldUpdateFiltro(
    regionValue: number,
    tipoReporte: number
  ): boolean {
    return (
      !this.valorFiltroOrganizacionButtonReporte ||
      this.valorFiltroOrganizacionButtonReporte !== regionValue ||
      (tipoReporte === 1 && !this.buttonReportePdfclick) ||
      (tipoReporte === 2 && !this.buttonReporteCsvclick)
    );
  }

  private updateButtonClickState(tipoReporte: number): void {
    if (tipoReporte === 1) {
      this.buttonReportePdfclick = true;
    } else if (tipoReporte === 2) {
      this.buttonReporteCsvclick = true;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  cargarResumen(): void {
    this.behaviorResumenService.setActualizarResumen(
      this.myFormUbigeo.get("region").value
    );
  }
}
