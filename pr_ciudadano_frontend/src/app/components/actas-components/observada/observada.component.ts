import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

import { ID_ELECCION, ACTA_CODIGO_ESTADO, ACTA_CODIGO_ESTADO_DESC } from "../../../helpers/constantes";
import { EstadoActa } from "../../../helpers/estado-enum";
import { Content, Mesa, LineaTiempo, Detalle, Archivo } from "../../../interfaces/acta-bean";
import { CentEducativoBean } from "../../../interfaces/cent-educativo-bean";
import { DistritoBean } from "../../../interfaces/distrito";
import { ItemBean } from "../../../interfaces/item-bean";
import { ProvinciaBean } from "../../../interfaces/provincia-bean";
import { MapaCalor } from "../../../interfaces/resumen-general-bean";
import { ActasService } from "../../../services/elecciones-generales/actas.service";
import { URL_PATHS_TO_REDIRECT } from "../../../settings/app.routes.settings";
import { HistoricoComponent } from "../historico/historico.component";
import { ACTAS_RESUMEN_GENERAL_TITLE } from "../../../helpers/actas-resumen-general.helper";

@Component({
  selector: "app-observada",
  templateUrl: "./observada.component.html",
  standalone: false,
})
export class ObservadaComponent implements OnChanges, OnDestroy {
  tiposActas: boolean = false;
  actasDetalleLista: boolean = false;
  DetalleActas: boolean = false;
  DetalleActasObs: boolean = false;
  @ViewChild(HistoricoComponent) hijoComponent: HistoricoComponent;
  linea: boolean = false;
  ID_ELECCION = ID_ELECCION;
  ACTA_CODIGO_ESTADO = ACTA_CODIGO_ESTADO;
  ACTA_CODIGO_ESTADO_DESC = ACTA_CODIGO_ESTADO_DESC;

  @Input() actas: [Content];

  //Paginado
  paginaActual = 0;
  paginaAnterior = 0;
  habilitarBotonSiguiente = false;
  habilitarBotonAnterior = false;
  rangoMaximoPaginado = 4;
  grupoRangoPaginado = 1;
  valorMinimoRangoPaginado = 1;
  valorMaximoRangoPaginado = 4;
  cuentaVecesSeguiente = 1;
  datosActa: Mesa;
  lineaTiempo: [LineaTiempo];
  detalle: [Detalle];
  @Input() mapaCalor: [MapaCalor];
  @Input() limpiarDatos: boolean;
  @Input() elecciones: any[];
  totalPaginas = 0;
  totalPaginasReales = 0;
  totalRegistros = 0;
  @Input() idEleccion: any;
  listaPaginas: any[] = [];
  @Input() detalleActa: Mesa;
  @Input() datosObservada: {};
  @Output() eventPaginado = new EventEmitter();
  @Output() eventFiltro = new EventEmitter();
  @Output() eventSiguiente = new EventEmitter();
  @Output() eventAnterior = new EventEmitter();
  listaPaginaTotal = [];
  listaArchivos: [Archivo];
  estado: string | object;

  public ActasObservadas = "observada.ActasObservadas"
  public Filtro = "observada.Filtro"
  public Filtrar = "observada.Filtrar"
  public Todos = "observada.Todos"
  public Actaeleccion = "observada.Actaeleccion"
  public ActaN = "observada.ActaN"
  public Electoreshabiles = "observada.Electoreshabiles"
  public Totalvotantes = "observada.Totalvotantes"
  public Participacionciudadana = "observada.Participacionciudadana"
  public Acta = "observada.Acta"
  public Departamento = "observada.Departamento"
  public Provincia = "observada.Provincia"
  public Distrito = "observada.Distrito"
  public Localvotacion = "observada.Localvotacion"
  public CentroPoblado = "observada.CentroPoblado";
  private readonly destroy$: Subject<boolean> = new Subject<boolean>();
  public myForm: FormGroup = this.fb.group({
    filtro: [null, Validators.required],
  });

  public electionName = "";

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private readonly fb: FormBuilder,
    private readonly actaService: ActasService
  ) {}

  ngOnInit() {
    this.tiposActas = true;
    this.actasDetalleLista = false;
    this.DetalleActas = false;
    this.DetalleActasObs = false;
    this.myForm.get("filtro").valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.detalleActa = null;
          this.eventFiltro.emit(value);
        },
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes) {
      if (changes["mapaCalor"]) {
        if (changes["mapaCalor"].currentValue) {
          this.initMapTwo(this.mapaCalor);
        }
      }
      if (changes["datosObservada"]) {
        if (changes["datosObservada"].currentValue) {
          let data = changes["datosObservada"].currentValue;
          this.actas = data.actas;
          this.totalPaginasReales = data.totalPaginasReales;
          this.totalRegistros = data.totalRegistros;
          this.totalPaginas = data.totalPaginas;

          this.crearListaPaginado(this.totalRegistros, this.paginaActual);
        }
      }
    }
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
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
      let mostrarBotonAnterior = true;
      let mostrarBotonSiguiente = true;

      this.listaPaginaTotal.push({
        grupo: contaGrupo,
        pagina: contPaginaAcumulado,
        mostrarBotonAnterior: mostrarBotonAnterior,
        mostrarBotonSiguiente: mostrarBotonSiguiente,
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

  obtenerListaPaginaPorGrupo(listaPaginaTotal, grupo: number, pagina: number): void {
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

    this.listaPaginas = listaPaginaTotal.filter((x) => x.grupo == grupo);
    this.listaPaginas = this.listaPaginas.map((x) => {
      if (x.pagina == pagina) {
        x.seleccionado = true;
      } else {
        x.seleccionado = false;
      }
      return x;
    });
  }

  limpiar(): void {
    this.listaPaginas = null;
    this.actas = null;
    this.detalleActa = null;
    this.lineaTiempo = null;
    this.paginaActual = 0;
    this.myForm.get("filtro").setValue(null);
  }

  limpiarDetalle(): void {
    this.detalleActa = null;
  }

  VerDetallesObservados() {
    this.DetalleActasObs = true;
    this.DetalleActas = false;
  }

  listDepartamento: Array<ItemBean>;
  listProvincia: Array<ProvinciaBean>;
  listDistrito: Array<DistritoBean>;
  listCentEducativo: Array<CentEducativoBean>;

  paginado(numero: number): void {
    this.paginaAnterior = this.paginaActual;
    this.paginaActual = numero - 1;
    this.eventPaginado.emit({
      paginaAnterior: this.paginaAnterior,
      paginaActual: this.paginaActual,
      resueltas: this.myForm.get("filtro").value,
    });
  }

  Actas() {
    this.router.navigate([URL_PATHS_TO_REDIRECT.actas]);
  }

  verLinea() {
    this.estado = EstadoActa.OBSERVADA;
    this.linea = true;
    this.hijoComponent.actualizarEstado(this.estado);
  }

  retornarUrlIcono(acta: Content): string {
    return this.retornarClaseCssSegunEstado(acta);
  }

  retornarClaseCssSegunEstado(acta: Content): string {
    let nombreClase = "";
    if (acta.estadoActa == "C") {
      nombreClase = "contabilizada";
    } else if (acta.estadoActa == "P") {
      nombreClase = "pendiente";
    } else if (acta.estadoActa == "O") {
      nombreClase = "observado";
    }
    if (acta.idEleccion == 4) {
      nombreClase = nombreClase + " ico-distrital";
    }
    return nombreClase;
  }

  obtenerNombreEleccion(detalle: Mesa): string {
    if (detalle == null) {
      return "";
    }
    if (detalle.idEleccion == null) {
      return "";
    }
    let retorno = "";
    if (detalle.idEleccion == ID_ELECCION.ID_ELECCION_MUNICIPAL) {
      retorno = " Municipal";
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

  VerDetalles(idMesa: number) {
    this.actas.forEach(acta => {
      acta.esSeleccionado = acta.id === idMesa;
    });
    this.DetalleActas = true;
    this.DetalleActasObs = false;

    this.actaService
      .buscarMesaPorId(idMesa)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.datosActa = result.data;
          this.detalleActa = result.data;
          this.lineaTiempo = result.data.lineaTiempo;
          this.detalle = result.data.detalle;
          this.listaArchivos = result.data.archivos;

          this.loadElectionName(result.data.idEleccion);
        },
      });
  }

  initMapTwo(mapaCalor: [MapaCalor]) {
    am5.addLicense("AM5M357387632");

    let root = am5.Root.new("chartdivobs");
    if (root._logo != undefined) {
      root._logo.dispose();
    }
    root.setThemes([am5themes_Animated.new(root)]);

    let chart = root.container.children.push(
      am5map.MapChart.new(root, {
        panX: "rotateX",
        projection: am5map.geoMercator(),
        layout: root.horizontalLayout,
        paddingBottom: 20,
      })
    );

    let polygonSeries = chart.series.push(
      am5map.MapPolygonSeries.new(root, {
        calculateAggregates: true,
        valueField: "value",
      })
    );

    this.loadGeodataTwo(chart, polygonSeries, root, mapaCalor);

    polygonSeries.mapPolygons.template.setAll({
      tooltipText: "{name}",
      interactive: true,
    });

    polygonSeries.mapPolygons.template.states.create("hover", {
      fill: am5.color(0x295789),
    });

    polygonSeries.set("heatRules", [
      {
        target: polygonSeries.mapPolygons.template,
        dataField: "value",
        min: am5.color(0xdfe5eb),
        max: am5.color(0x295789),
        key: "fill",
      },
    ]);

    polygonSeries.mapPolygons.template.events.on("pointerover", function (ev) {});

    let valorMax = Math.max(...this.mapaCalor.map((x) => x.actasContabilizadas));

    let heatLegend = chart.children.push(
      am5.HeatLegend.new(root, {
        orientation: "horizontal",
        startColor: am5.color(0xdfe5eb),
        endColor: am5.color(0x295789),
        startText: "0",
        endText: valorMax.toString(),
        centerY: am5.p50,
        y: am5.p100,
        centerX: am5.p100,
        x: am5.p50,
        width: 200,
      })
    );

    let labels = heatLegend.children.push(
      am5.Container.new(root, {
        width: am5.p100,
        x: am5.percent(33),
        y: am5.percent(50),
      })
    );
    labels.children.push(
      am5.Label.new(root, {
        text: "Porcentaje de actas contabilizadas",
      })
    );

    heatLegend.startLabel.setAll({
      fontSize: 12,
      fill: heatLegend.get("startColor"),
    });

    heatLegend.endLabel.setAll({
      fontSize: 12,
      fill: heatLegend.get("endColor"),
    });

    polygonSeries.events.on("datavalidated", function () {
      heatLegend.set("startValue", polygonSeries.getPrivate("valueLow"));
      heatLegend.set("endValue", polygonSeries.getPrivate("valueHigh"));
    });
  }

  loadGeodataTwo(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: [MapaCalor]
  ) {
    this.actaService.loadGeodataTwo(chart, polygonSeries, root, mapaCalor);
  }

  eventoPaginaAnterior(): void {
    this.paginaActual = this.listaPaginas[0].pagina - 2;
    this.eventAnterior.emit({
      paginaActual: this.paginaActual,
      resueltas: this.myForm.get("filtro").value,
    });
  }

  eventoPaginaSiguiente(): void {
    this.paginaActual = this.listaPaginas[this.listaPaginas.length - 1].pagina;
    this.eventSiguiente.emit({
      paginaActual: this.paginaActual,
      resueltas: this.myForm.get("filtro").value,
    });
  }

  private loadElectionName(electionId: number) {
    this.electionName = ACTAS_RESUMEN_GENERAL_TITLE[electionId];
  }
}
