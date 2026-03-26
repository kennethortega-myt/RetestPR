import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, delay, filter, of, startWith, take, takeUntil } from 'rxjs';
import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import { ViewportScroller } from '@angular/common';

import { ID_ELECCION, ACTA_CODIGO_ESTADO, CONFIG_NAMES_FOR_ACTAS } from '../../../../../helpers/constantes';
import { LineaTiempo, Detalle, Mesa, Archivo } from '../../../../../interfaces/acta-bean';
import { ParticipacionCiudadano } from '../../../../../interfaces/participacion-ciudadana.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { ActasService } from '../../../../../services/elecciones-generales/actas.service';
import { ParticipacionCiudadanaService } from '../../../../../services/elecciones-generales/participacion-ciudadana.service';
import { RandomImageService } from '../../../../../services/elecciones-generales/random-image.service';
import { PadronService } from '../../../../../services/elecciones-generales/padron.service';
import { ACTAS_RESUMEN_GENERAL_TITLE } from '../../../../../helpers/actas-resumen-general.helper';
import { TranslateService } from '@ngx-translate/core';
import { getElectionByIdElection } from '../../../../../helpers/encrypt-storage-eleccion';
import { mapWithPoliticImage } from '../../../../../helpers/get-images.helper';

@Component({
  selector: 'app-actas-por-mesa',
  templateUrl: './actas-por-mesa.component.html',
  standalone: false
})
export class ActasPorMesaComponent implements OnInit {
  // imagen aleatoria
  public randomImageUrl: string;
  DetalleActas: boolean = false;
  DetalleActasObs: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  participantes: [ParticipacionCiudadano];
  root: any;
  lineaTiempo: [LineaTiempo];
  codigoMesaForLineaTiempo = '';
  configNameForLineTiempo = '';
  detalle: Detalle[] = [];
  detalleActa: Mesa;
  actas: Mesa[];
  datosActa: Mesa;
  ID_ELECCION = ID_ELECCION;
  ACTA_CODIGO_ESTADO = ACTA_CODIGO_ESTADO;
  primeraVez: boolean = true;
  buscarPorMesa: boolean = false;
  nroMesa: string = '';
  nroDNI: string = '';
  listaArchivos: [Archivo];
  deshabilitarBtnBuscarMesa: boolean = false;
  deshabilitarBtnBuscarDni: boolean = false;
  idAmbitoGeografico: number = 1;

  @ViewChild('chartdiv2') chartElement: ElementRef<HTMLElement>;
  @Input() idEleccion: any;
  @Input() ingreso: boolean = false;
  @Input() resumen: Resumen;
  @Input() elecciones: any[];

  public myFormActa: FormGroup = this.fb.group({
    mesa: ['', Validators.required],
    dni: ['', Validators.required]
  });

  public electionName = '';

  orderBy = 'Posicion';
  constructor(
    private readonly fb: FormBuilder,
    private readonly actaService: ActasService,
    private readonly participacionCiudadanaService: ParticipacionCiudadanaService,
    private readonly scroller: ViewportScroller,
    private readonly padronService: PadronService,
    private readonly randomImageService: RandomImageService,
    private readonly translate: TranslateService
  ) {}

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

  initValuesChanged(): void {
    this.myFormActa
      .get('mesa')
      .valueChanges.pipe(
        startWith(''),
        filter((value) => {
          this.myFormActa.get('dni').setValue('', { emitEvent: false });

          if (value.length > 5) {
            this.deshabilitarBtnBuscarMesa = false;
            return true;
          } else {
            this.deshabilitarBtnBuscarMesa = true;
            this.deshabilitarBtnBuscarDni = true;
            return false;
          }
        })
      )
      .subscribe({
        next: (_) => {
          this.deshabilitarBtnBuscarMesa = false;
        }
      });

    this.myFormActa
      .get('dni')
      .valueChanges.pipe(
        startWith(''),
        filter((value) => {
          this.myFormActa.get('mesa').setValue('', { emitEvent: false });
          if (value.length > 7) {
            this.deshabilitarBtnBuscarDni = false;
            return true;
          } else {
            this.deshabilitarBtnBuscarDni = true;
            this.deshabilitarBtnBuscarMesa = true;
            return false;
          }
        })
      )
      .subscribe({
        next: (result) => {}
      });
  }

  ngOnInit(): void {
    this.initValuesChanged();
    this.randomImageUrl = this.randomImageService.getRandomImage();
  }

  VerDetalles(mesa: Mesa, index?: number) {
    this.detalle = [];
    this.showLoading();
    this.DetalleActas = true;
    this.DetalleActasObs = false;

    this.actas = this.actas.map((x) => {
      return {
        ...x,
        esSeleccionado: x.id == mesa.id
      } as Mesa;
    });

    mesa.esSeleccionado = true;
    this.actaService
      .buscarMesaPorId(mesa.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.datosActa = result.data;
          this.detalleActa = result.data;
          this.lineaTiempo = result.data.lineaTiempo;
          this.codigoMesaForLineaTiempo = result.data.codigoMesa;
          const selectedConfigName = CONFIG_NAMES_FOR_ACTAS.find((e) => e.electionId == result.data.idEleccion);
          this.configNameForLineTiempo = selectedConfigName ? selectedConfigName.name : 'acta';

          this.detalle = mapWithPoliticImage(result.data.detalle);

          this.listaArchivos = result.data.archivos;

          mesa.codigoEstadoActa = this.detalleActa.codigoEstadoActa;
          this.actas[index] = mesa;

          this.loadElectionName(result.data.idEleccion);
          setTimeout(() => {
            this.scroller.scrollToAnchor('detalle-view-111');
          }, 500);
        }
      });
    this.hideLoading();
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

  showLoading() {
    // Loading functionality removed
  }

  hideLoading() {
    setTimeout(() => {
      // Loading functionality removed
    }, 500);
  }

  buscar(): void {
    let codigoMesa: string = this.myFormActa.controls['mesa'].value;
    if (codigoMesa.length < 6) return;
    this.showLoading();
    this.deshabilitarBtnBuscarMesa = true;
    this.limpiarDatos();
    this.actas = null;
    if (!codigoMesa) {
      this.buscarPorMesa = true;
      this.primeraVez = false;
      return;
    }
    this.nroMesa = codigoMesa;
    this.nroDNI = '';
    this.actaService
      .buscarMesa(codigoMesa)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.buscarPorMesa = true;
          this.primeraVez = false;
          if (result.success) {
            this.actas = result.data;
            this.idAmbitoGeografico = this.actas[0].idAmbitoGeografico;
            if (this.actas.length == 1) {
              this.VerDetalles(this.actas[0]);
              let idUbigeo = this.actas[0].idUbigeo;
              let codigoDistrito = idUbigeo.toString().padStart(6, '0');
              let codigoProvincia = codigoDistrito.toString().substring(0, 4) + '00';

              this.cargarMapa(codigoProvincia, codigoDistrito);
            }
          } else {
            this.actas = [];
          }
          this.deshabilitarBtnBuscarMesa = false;
        },
        error: (err) => {
          this.deshabilitarBtnBuscarMesa = false;
        },
        complete: () => {
          this.cleanInputs();
        }
      });
    this.hideLoading();
  }

  buscarPorDni(): void {
    let dni = this.myFormActa.get('dni').value;
    if (dni.length < 8) return;
    this.showLoading();
    this.limpiarDatos();
    if (!dni) {
      this.buscarPorMesa = false;
      this.primeraVez = false;
      return;
    }
    this.nroMesa = '';
    this.nroDNI = dni;
    this.deshabilitarBtnBuscarDni = true;
    this.actas = null;
    this.padronService
      .buscarPorDni(dni)
      .pipe(take(1))
      .subscribe({
        next: (result) => {
          this.deshabilitarBtnBuscarDni = false;
          if (!result.success) {
            this.buscarPorMesa = false;
            this.primeraVez = false;
            this.actas = [];
            return;
          }
          this.buscarPorMesa = false;
          this.primeraVez = false;
          this.actaService
            .buscarMesa(result.data.mesa)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (result) => {
                if (result.success) {
                  this.actas = result.data ?? null;
                  if (this.actas.length == 1) {
                    this.VerDetalles(this.actas[0]);
                    let idUbigeo = this.actas[0].idUbigeo;
                    let codigoDistrito = idUbigeo.toString().padStart(6, '0');
                    let codigoProvincia = idUbigeo.toString().substring(0, 4) + '00';

                    this.cargarMapa(codigoProvincia, codigoDistrito);
                  }
                } else {
                  this.actas = [];
                }
              }
            });
        },
        error: (err) => {
          this.deshabilitarBtnBuscarDni = false;
        },
        complete: () => {
          this.cleanInputs();
        }
      });
    this.hideLoading();
  }

  cleanInputs() {
    this.myFormActa.get('mesa').setValue('');
    this.myFormActa.get('dni').setValue('');
  }

  limpiarDatos(): void {
    this.lineaTiempo = null;
    this.detalleActa = null;
    this.DetalleActas = null;
    this.detalle = null;
  }

  listarParticipacionCiudadana() {
    this.participacionCiudadanaService
      .listarParticipacionCiudadana('ubigeo_nivel_01')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.participantes = value.data;
          of(this.participantes)
            .pipe(
              delay(1000) // Simulate a 3-second network delay
            )
            .subscribe({
              next: (result) => {
                this.initMapTwo(this.participantes);
              }
            });
        }
      });
  }

  cargarMapa(codigoProvincia: string, codigoDistrito: string) {
    this.destroyMap(this.root);
    let root = am5.Root.new('chartmapa2');
    this.root = root;
    root.setThemes([am5themes_Animated.new(root)]);

    const distritoSelected = [
      {
        id: codigoDistrito,
        polygonSettings: {
          fill: am5.color(0x469cd0)
        }
      }
    ];

    const map = `./assets/lib/amcharts5/geodata/json/provincias/${codigoProvincia}.json`;

    let chart = root.container.children.push(
      am5map.MapChart.new(root, {
        wheelY: 'none',
        wheelX: 'none'
      })
    );

    am5.net.load(map, chart).then(function (result) {
      chart.series.push(
        am5map.MapPolygonSeries.new(root, {
          geoJSON: am5.JSONParser.parse(result.response),
          fill: am5.color(0xc3c3c3),
          stroke: am5.color(0xffffff)
        })
      );
    });

    am5.net.load(map, chart).then(function (result_select) {
      let transformDataSelected = am5.JSONParser.parse(result_select.response);

      transformDataSelected.features.forEach((element) => {
        element.id = element.properties.ID;
      });

      transformDataSelected.features = transformDataSelected.features.filter(
        (element) => element.properties.ID == codigoDistrito
      );

      let polygonSeries_Seleccionados = chart.series.push(
        am5map.MapPolygonSeries.new(root, {
          geoJSON: transformDataSelected,
          fill: am5.color(0x003770)
        })
      );

      polygonSeries_Seleccionados.mapPolygons.template.setAll({
        tooltipText: '[fontFamily: NotoSans-regular][fontSize: 14px]{name}',
        interactive: true,
        cursorOverStyle: 'pointer',
        templateField: 'polygonSettings'
      });

      polygonSeries_Seleccionados.data.setAll(distritoSelected);
      polygonSeries_Seleccionados.show();
      polygonSeries_Seleccionados.mapPolygons.template.states.create('hover', {
        fill: am5.color(0x469cd0)
      });
    });
  }

  initMapTwo(participantes: [ParticipacionCiudadano]) {
    this.destroyMap(this.root);

    let root = am5.Root.new('chartmapa2');

    this.root = root;
    root.setThemes([am5themes_Animated.new(root)]);

    let chart = root.container.children.push(
      am5map.MapChart.new(root, {
        panX: 'rotateX',
        projection: am5map.geoMercator(),
        layout: root.horizontalLayout,
        paddingBottom: 20
      })
    );

    let polygonSeries = chart.series.push(
      am5map.MapPolygonSeries.new(root, {
        calculateAggregates: true,
        valueField: 'value'
      })
    );

    this.loadGeodataTwo(chart, polygonSeries, root, participantes);

    polygonSeries.mapPolygons.template.setAll({
      tooltipText: '{name}',
      interactive: true
    });

    polygonSeries.mapPolygons.template.states.create('hover', {
      fill: am5.color(0x677935)
    });

    polygonSeries.set('heatRules', [
      {
        target: polygonSeries.mapPolygons.template,
        dataField: 'value',
        min: am5.color(0x8ab7ff),
        max: am5.color(0x25529a),
        key: 'fill'
      }
    ]);

    polygonSeries.mapPolygons.template.events.on('pointerover', function (ev) {});

    let valorMax = Math.max(...participantes.map((x) => x.totalElectoresHabiles));

    let heatLegend = chart.children.push(
      am5.HeatLegend.new(root, {
        orientation: 'horizontal',
        startColor: am5.color(0x8ab7ff),
        endColor: am5.color(0x25529a),
        startText: '0',
        endText: valorMax.toString(),
        marginLeft: -140,
        y: 240,
        x: 10,
        width: 200
      })
    );

    heatLegend.startLabel.setAll({
      fontSize: 12,
      fill: heatLegend.get('startColor')
    });

    heatLegend.endLabel.setAll({
      fontSize: 12,
      fill: heatLegend.get('endColor')
    });

    // change this to template when possible
    polygonSeries.events.on('datavalidated', function () {
      heatLegend.set('startValue', polygonSeries.getPrivate('valueLow'));
      heatLegend.set('endValue', polygonSeries.getPrivate('valueHigh'));
    });
  }

  loadGeodataTwo(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    participantes: [ParticipacionCiudadano]
  ) {
    this.actaService.loadGeodataTwo(chart, polygonSeries, root, participantes);
  }

  private destroyMap(root: am5.Root): void {
    if (root) {
      root.dispose();
    }
  }

  retornarClaseCssSegunEstado(acta: Mesa): string {
    let nombreClase = '';
    if (acta.descripcionEstadoActa == 'Observada' || acta.descripcionEstadoActa == 'Pendiente') {
      nombreClase = 'observado';
    }
    if (acta.idEleccion == ID_ELECCION.ID_ELECCION_MUNICIPAL) {
      nombreClase = nombreClase + ' ico-distrital';
    }
    return nombreClase;
  }

  obtenerPorcentajeParticipacionCiudadana(detalle: Mesa): number {
    if (detalle == null) {
      return 0;
    }

    if (detalle.totalVotosEmitidos == null) {
      return 0;
    }
    let calculo = (detalle.totalVotosEmitidos / detalle.totalElectoresHabiles) * 100;
    return calculo;
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

  toggleOrderVotosPosicion() {
    const compareVotos = (a: Detalle, b: Detalle) =>
      b.nvotos === a.nvotos ? a.estado - b.estado : b.nvotos - a.nvotos;
    const comparePosicion = (a: Detalle, b: Detalle) => a.nposicion - b.nposicion;

    this.detalle = this.detalle.sort(this.orderBy === 'Votos' ? compareVotos : comparePosicion);
    this.orderBy = this.orderBy === 'Posicion' ? 'Votos' : 'Posicion';
  }
}
