import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { debounceTime, map, Observable, takeUntil } from 'rxjs';
import { TIPO_FILTRO } from '../../helpers/constantes';
import { EnumAmbito } from '../../helpers/estado-enum';
import { isRevocatoria } from '../../helpers/storage-helpers/encrypt-storage.helper';
import { IGetActasObserbadasParams } from '../../interfaces/acta-api.interface';
import { Acta, Candidato, Eleccion, Mesa } from '../../interfaces/acta-bean';
import { IActasComponent } from '../../interfaces/actas.interfaces';
import { LocalVotacion } from '../../interfaces/elections.interfaces';
import { ParticipacionCiudadano } from '../../interfaces/participacion-ciudadana.interfaces';
import { FrontendResponse } from '../../interfaces/response.common';
import { ActaResumenGeneralTotalesResponse } from '../../interfaces/response/acta-resumen-general-response.interface';
import { MapaCalor } from '../../interfaces/resumen-general-bean';
import { RequestsService } from '../common/requests.service';
import { ActaApiService } from './acta-api.service';

@Injectable({
  providedIn: 'root'
})
export class ActasService {
  actasComponent: IActasComponent;
  deshabilitarBotonFiltrar: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  protected resumenTotales: ActaResumenGeneralTotalesResponse;

  constructor(
    private readonly actaApiService: ActaApiService,
    private readonly requestsService: RequestsService
  ) {}

  init(actasComponent: IActasComponent): void {
    actasComponent.controlAmbito = actasComponent.myFormUbigeo.controls['region'];
    actasComponent.randomImageUrl = actasComponent.randomImageService.getRandomImage();
    actasComponent.route.fragment.pipe(takeUntil(actasComponent.destroy$)).subscribe((f) => {
      const element = document.querySelector('#' + f);
      if (element) element.scrollIntoView();
    });
    this.setControlsValues(actasComponent.myFormUbigeo, ['eleccion'], [actasComponent.idEleccion]);
    actasComponent.listarDepartamentos(actasComponent.idEleccion, actasComponent.idAmbito);
    
    this.iniciarEventoValuesChange(actasComponent);

    setTimeout(() => {     
      actasComponent.listarParticipacionCiudadana(); 
    });
  }

  setControlsValues(form: FormGroup, keys: string[], values: (string | number)[] = [], emits: boolean[] = []) {
    try {
      keys.forEach((key, idx) => {
        const control = form.get(key);
        if (control) {
          const value = values[idx] ?? null;
          const emit = emits[idx] ?? false;
          control.setValue(value, { emitEvent: emit });
        } else {
          console.warn(`Control con llave '${key}' no existe en el formulario '${JSON.stringify(form)}'`);
        }
      });
    } catch (error) {
      console.error('Error al establecer valores en los controles del formulario:', error);
    }
  }

  setClassProperties(classComponet, keys: string[], values: any[] = []) {
    if (!classComponet || typeof classComponet !== 'object') {
      return;
    }
    keys.forEach((key, idx) => {
      if (classComponet.hasOwnProperty(key)) {
        classComponet[key] = values[idx] !== undefined ? values[idx] : null;
      } else {
        console.warn(`La propiedad '${key}' no existe en la clase '${classComponet}'`);
      }
    });
  }

  iniciarEventoValuesChange(actasComponent: IActasComponent): void {
    this.setControlsValues(
      actasComponent.myFormUbigeo,
      ['departamento', 'provincia', 'distrito', 'cent_educativo'],
      [0, 0, 0, 0]
    );

    actasComponent.myFormUbigeo.get('region').valueChanges
      .pipe(takeUntil(actasComponent.destroy$))
      .subscribe({
        next: (value) => {
        actasComponent.ubigeoMapaSeleccionado = '';
        actasComponent.detalleActa = null;
        actasComponent.localesVotacion = null;
        actasComponent.totalPaginasReales = 0;
        actasComponent.datoActa = null;
        this.setControlsValues(
          actasComponent.myFormUbigeo,
          ['departamento', 'provincia', 'distrito', 'cent_educativo'],
          [0, 0, 0, 0]
        );
        let idEleccion = actasComponent.myFormUbigeo.get('eleccion').value;

        actasComponent.cambioUbigeo(null, false);
        this.setClassProperties(this.actasComponent, ['deshabilitarBotonFiltrar'], [false]);

        if (isRevocatoria()) {
          actasComponent.listarParticipacionCiudadanaPorMapa(value);
        } else {
          actasComponent.listarDepartamentos(idEleccion, value);
        }

        this.limpiarControles(actasComponent);

        actasComponent.provincias = null;
        actasComponent.distritos = null;
        if (value == '1') {
          actasComponent.mensaje = actasComponent.messagePersonageStranger.nationality.region;
        } else {
          actasComponent.mensaje = actasComponent.messagePersonageStranger.stranger.region;
        }
      }
    });

    actasComponent.myFormUbigeo.get('departamento').valueChanges
      .pipe(takeUntil(actasComponent.destroy$))
      .subscribe({
        next: (value) => {
        this.setControlsValues(actasComponent.myFormUbigeo, ['provincia', 'distrito', 'cent_educativo'], [0, 0, 0]);

        actasComponent.distritos = null;
        actasComponent.localesVotacion = null;
        actasComponent.totalPaginasReales = 0;
        actasComponent.datoActa = null;
        this.setClassProperties(this, ['deshabilitarBotonLimpiar', 'deshabilitarBotonFiltrar'], [false, true]);
        actasComponent.detalleActa = null;
        this.limpiarControles(actasComponent);

        let idEleccion = actasComponent.myFormUbigeo.get('eleccion').value;
        let idAmbito = actasComponent.myFormUbigeo.get('region').value;
        let idDepartamento = actasComponent.myFormUbigeo.get('departamento').value;

        if (isRevocatoria()) {
          actasComponent.deshabilitarBotonLimpiar = value == 0;
          if (value != 0) {
            actasComponent.listarParticipacionCiudadanaPorMapa(value);
          } else {
            //actas obs
            actasComponent.mostrarMyFromFiltro = false;
            actasComponent.limpiarDetalle();
            //actas obs
            actasComponent.provincias = [];
            actasComponent.seleccionarPeru('');
          }
        } else {
          actasComponent.listarProvincias(idEleccion, idAmbito, idDepartamento);
          if (value == 0) {
            actasComponent.listarParticipacionCiudadana();
          } else {
            actasComponent.listarParticipacionCiudadana(TIPO_FILTRO.UBIGEO_NIVEL_01, idDepartamento, 0, 0, 1, value);
          }
        }
      }
    });

    actasComponent.myFormUbigeo.get('provincia').valueChanges
      .pipe(takeUntil(actasComponent.destroy$))
      .subscribe({
        next: (value) => {
        this.setControlsValues(actasComponent.myFormUbigeo, ['distrito', 'cent_educativo'], [0, 0], [false, false]);
        actasComponent.datoActa = null;
        actasComponent.detalleActa = null;
        actasComponent.localesVotacion = null;

        this.limpiarControles(actasComponent);
        let idEleccion = actasComponent.myFormUbigeo.get('eleccion').value;
        let idAmbito = actasComponent.myFormUbigeo.get('region').value;
        let idDepartamento = actasComponent.myFormUbigeo.get('departamento').value;
        let idProvincia = actasComponent.myFormUbigeo.get('provincia').value;

        actasComponent.deshabilitarBotonFiltrar = true;

        if (isRevocatoria()) {
          actasComponent.deshabilitarBotonLimpiar = false;

          if (value != 0) {
            actasComponent.listarParticipacionCiudadanaPorMapa(value);
          } else {
            actasComponent.mostrarMyFromFiltro = false;
            actasComponent.limpiarDetalle();

            actasComponent.distritos = [];
            actasComponent.listarParticipacionCiudadanaPorMapa(idDepartamento);
          }
        } else {
          actasComponent.listarDistritos(idEleccion, idAmbito, idProvincia);
          if (value == 0) {
            actasComponent.listarParticipacionCiudadana(
              TIPO_FILTRO.UBIGEO_NIVEL_01,
              idDepartamento,
              0,
              0,
              1,
              idDepartamento
            );
          } else {
            actasComponent.listarParticipacionCiudadana(
              TIPO_FILTRO.UBIGEO_NIVEL_02,
              idDepartamento,
              idProvincia,
              0,
              2,
              value
            );
          }
        }
      }
    });

    actasComponent.myFormUbigeo
      .get('distrito')
      .valueChanges.pipe(
        debounceTime(100), // Reducir debounce a 100ms
        takeUntil(actasComponent.destroy$)
      )
      .subscribe({
        next: (value) => {
          actasComponent.mostrarElementos = value != 0;

          this.setControlsValues(actasComponent.myFormUbigeo, ['cent_educativo'], [0]);
          let idAmbito = actasComponent.myFormUbigeo.get('region').value;
          let idDepartamento = actasComponent.myFormUbigeo.get('departamento').value;
          let idProvincia = actasComponent.myFormUbigeo.get('provincia').value;
          let idDistrito = actasComponent.myFormUbigeo.get('distrito').value;
          actasComponent.detalleActa = null;
          this.limpiarControles(actasComponent);

          actasComponent.deshabilitarBotonFiltrar = false;

          if (isRevocatoria()) {
            actasComponent.deshabilitarBotonLimpiar = false;

            if (value != 0) {
              actasComponent.listarParticipacionCiudadana(
                TIPO_FILTRO.UBIGEO_NIVEL_02,
                idDepartamento,
                idProvincia,
                idDistrito,
                3,
                value
              );
              actasComponent.listarParticipacionCiudadanaPorMapa(value);
              actasComponent.filtrar();
            } else {
              actasComponent.mostrarMyFromFiltro = false;
              actasComponent.limpiarDetalle();

              actasComponent.datoActa = null;
              actasComponent.listarParticipacionCiudadanaPorMapa(idProvincia);
            }
          } else {
            actasComponent.filtrar();

              if (value == '0') {
                actasComponent.listarParticipacionCiudadana(
                  TIPO_FILTRO.UBIGEO_NIVEL_02,
                  idDepartamento,
                  idProvincia,
                  0,
                  2,
                  idProvincia
                );
              } else {
                actasComponent.listarParticipacionCiudadana(
                  TIPO_FILTRO.UBIGEO_NIVEL_02,
                  idDepartamento,
                  idProvincia,
                  idDistrito,
                  3,
                  value
                );
              }
          }

          actasComponent.listarLocalesVotacion(idAmbito, value);
          actasComponent.autoComplete?.limpiarAutoComplete();
        }
      });
      
    actasComponent.myFormUbigeo.get('cent_educativo').valueChanges
      .pipe(takeUntil(actasComponent.destroy$))
      .subscribe({
        next: () => {
          actasComponent.detalleActa = null;
        }
    });
  }

  limpiarControles(actasComponent: IActasComponent, resetMessage: boolean = false): void {
    actasComponent.actas = [];
    actasComponent.datosActa = null;
    actasComponent.totalRegistros = 0;
    actasComponent.listaPagina = [];
    actasComponent.totalPaginasReales = 0;
    if (resetMessage) {
      if (actasComponent.myFormUbigeo.get('region').value == '1') {
        actasComponent.mensaje = actasComponent.mensajeInicial;
      } else {
        actasComponent.mensaje = actasComponent.mensajeInicialExtranjero;
      }
    }
  }

  filtrar(actasComponent: IActasComponent): void {
    let filtro = '';

    const revocatoriaRegionValue = isRevocatoria()
      ? actasComponent.genericFilterUbigeoComponent.electoralRevocatoriaForm.get('region').value
      : null;

    const electoralDistrictValue = isRevocatoria()
      ? actasComponent.genericFilterUbigeoComponent.electoralRevocatoriaForm.get('location').value
      : null;

    filtro =
      actasComponent.myFormUbigeo.get('region').value +
      (isRevocatoria()
        ? revocatoriaRegionValue + electoralDistrictValue
        : actasComponent.myFormUbigeo.get('departamento').value +
          actasComponent.myFormUbigeo.get('provincia').value +
          actasComponent.myFormUbigeo.get('distrito').value) +
      actasComponent.myFormUbigeo.get('cent_educativo').value;

    if (actasComponent.temporalFiltro == undefined) {
      actasComponent.temporalFiltro =
        actasComponent.myFormUbigeo.get('region').value +
        (isRevocatoria()
          ? revocatoriaRegionValue + electoralDistrictValue
          : actasComponent.myFormUbigeo.get('departamento').value +
            actasComponent.myFormUbigeo.get('provincia').value +
            actasComponent.myFormUbigeo.get('distrito').value) +
        actasComponent.myFormUbigeo.get('cent_educativo').value;
    } else if (actasComponent.temporalFiltro == filtro) {
      return;
    } else if (actasComponent.temporalFiltro != filtro) {
      actasComponent.temporalFiltro = filtro;
    }

    actasComponent.primeraBusqueda = false;
    actasComponent.listaPaginaTotal = [];
    actasComponent.detalleActa = null;
    actasComponent.paginaActual = 0;

    if(isRevocatoria()){
      actasComponent.genericFilterUbigeoComponent.cleanButtonIsDisabled = false;
      actasComponent.datoActa = null;
      //actas obs
      actasComponent.DetalleActas = null;
      //actas obs
      actasComponent.listarParticipacionCiudadanaPorMapa(revocatoriaRegionValue);
    }
  }

  limpiar(actasComponent: IActasComponent): void {
    this.limpiarControles(actasComponent, true);
    actasComponent.paginaActual = 0;
    actasComponent.localesVotacion = [];
    actasComponent.detalle = null;
    actasComponent.datoActa = null;
    actasComponent.detalleActa = null;
    actasComponent.lineaTiempo = null;
    actasComponent.DetalleActas = null;
    actasComponent.temporalFiltro = undefined;
    actasComponent.provincias = null;
    actasComponent.distritos = null;
    actasComponent.datoActa = null;
    this.setClassProperties(
      actasComponent,
      ['deshabilitarBotonLimpiar', 'habilitarBotonSiguiente', 'habilitarBotonAnterior'],
      [true, false, false]
    );
    this.setControlsValues(
      actasComponent.myFormUbigeo,
      ['departamento', 'provincia', 'distrito', 'cent_educativo'],
      [0, 0, 0, 0]
    );
     actasComponent.listarParticipacionCiudadana();
    actasComponent.autoComplete?.limpiarAutoComplete();

    // if(isRevocatoria()){
    //   actasComponent.seleccionarPeru('');
    // }
  }

  listarActasObservadas(
    params: IGetActasObserbadasParams,
    page: number,
    size: number
  ): Observable<FrontendResponse<Acta>> {
    return this.actaApiService.listarActasObservadas(params, page, size);
  }

  listarParticipacionCiudadanaPorTipo(actasComponent: IActasComponent, tipo: number) {
    this.setControlsValues(
      actasComponent.myFormUbigeo,
      ['region', 'departamento', 'provincia', 'distrito', 'cent_educativo'],
      [tipo, 0, 0, 0, 0],
      [true]
    );
    let idAmbitoGeografico = actasComponent.myFormUbigeo.controls['region'].value;

    actasComponent.provincias = [];
    actasComponent.distritos = [];
    actasComponent.localesVotacion = [];

    let codigoAgrupacionPolitica = 0;
    let idUbigeoDepartamento = 0;
    let idUbigeoProvincia = 0;
    let idUbigeoDistrito = 0;

    let tipoFiltro = 'ambito_geografico';
    actasComponent.lineaTiempo = null;
    actasComponent.detalleActa = null;
    // Loading functionality removed

    this.listarMapaCalorObservadas(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      actasComponent.idEleccion,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito,
      tipoFiltro
    )
      .pipe(takeUntil(actasComponent.destroy$))
      .subscribe({
        next: (value) => {
          // Loading functionality removed

          let esNacional = true;
          let data = value.data;

          if (tipo == 1) {
            //Nacional
            actasComponent.enumIdAmbito = EnumAmbito.NACIONAL;
          } else {
            //Extranjero
            actasComponent.enumIdAmbito = EnumAmbito.EXTRANJERO;
            esNacional = false;
          }
          if(actasComponent.mapaCalorViewChild.cargaInicialMapa){
            // Legacy map component (old approach)
            actasComponent.mapaCalorViewChild.cargaInicialMapa(data, 'chartdiv', esNacional);
          } else {
            // MainHotMapComponent: update @Input bindings to trigger ngOnChanges → loadMapByType()
            actasComponent.esEstranjero = !esNacional;
            actasComponent.nivelUbigeo = 0;
            actasComponent.codigoUbigeo = '';
            actasComponent.mapaCalor = data || [];
          }
        },
        error: (error) => {/* Loading functionality removed */}
      });
  }

  listarMapaCalorObservadas(
    codigoAgrupacionPolitica: number,
    idAmbitoGeografico: number,
    idEleccion: number,
    idUbigeoDepartamento: number,
    idUbigeoProvincia: number,
    idUbigeoDistrito: number,
    tipoFiltro: string
  ): Observable<FrontendResponse<[MapaCalor]>> {
    return this.actaApiService.listarMapaCalorObservadas(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      idEleccion,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito,
      tipoFiltro
    );
  }

  buscarMesaPorId(idMesa: number): Observable<FrontendResponse<Mesa>> {
    return this.actaApiService.buscarMesaPorId(idMesa);
  }

  limpiarPaginado(actasComponent: IActasComponent): void {
    this.setClassProperties(actasComponent, ['habilitarBotonAnterior', 'habilitarBotonSiguiente'], [false, false]);
    actasComponent.paginaActual = 0;
    actasComponent.paginaAnterior = 0;
    actasComponent.listaPagina = [];
  }

  mostrarNombreNivel(actasComponent: IActasComponent, nivel: number, capital: boolean = false): string {
    const region = actasComponent.myFormUbigeo.get('region').value;
    return this.mostrarNombreNivelBase(region, nivel, capital);
  }

  mostrarNombreNivelBase(idAmbito: number, nivel: number, capital: boolean = false): string {
    const nombresPorNivel: { [key: number]: string[] } = {
      1: ['REGIÓN', 'PROVINCIA', 'DISTRITO', 'LOCAL DE VOTACIÓN', 'ÁMBITO'],
      2: ['CONTINENTE', 'PAÍS', 'ESTADO', 'LOCAL DE VOTACIÓN', 'ÁMBITO']
    };
    const nombreNivel = nombresPorNivel[idAmbito]?.[nivel] || '';
    return capital ? this.capitalizeFirstLetter(nombreNivel) : nombreNivel;
  }

  cambioUbigeo(data: string, cambioAmbito: boolean = false, actasComponent?: IActasComponent) {
    this.actasComponent = actasComponent;
    this.actasComponent.controlEleccion = this.actasComponent.myFormUbigeo.controls['eleccion'];
    this.actasComponent.controlAmbito = this.actasComponent.myFormUbigeo.controls['region'];
    this.actasComponent.controlDepartamento = this.actasComponent.myFormUbigeo.controls['departamento'];
    this.actasComponent.controlProvincia = this.actasComponent.myFormUbigeo.controls['provincia'];
    this.actasComponent.controlDistrito = this.actasComponent.myFormUbigeo.controls['distrito'];

    if (this.actasComponent.ubigeoMapaSeleccionado == '' || data == null) {
      this.actasComponent.ubigeoMapaSeleccionado = data;
    } else if (this.actasComponent.ubigeoMapaSeleccionado == data) {
      return;
    } else {
      this.actasComponent.ubigeoMapaSeleccionado = data;
    }

    this.setClassProperties(this.actasComponent, ['habilitarBotonAnterior', 'habilitarBotonSiguiente'], [false, false]);
    if (this.actasComponent.myForm) this.setControlsValues(this.actasComponent.myForm, ['filtro']);
    this.actasComponent.totalRegistros = 0;
    this.actasComponent.datosActa = null;
    this.actasComponent.actas = [];

    if (cambioAmbito) {
      this.actasComponent.listarParticipacionCiudadana();
      return;
    }

    if (this.isDataInvalid(data)) return;

    this.processUbigeo(data);
    if (cambioAmbito) {
      this.actasComponent.autoComplete.limpiarAutoComplete();
    }
  }

  isDataInvalid(data: string): boolean {
    if (data == null || data.trim() === '') {
      return true;
    }
    const valorEntero = parseInt(data, 10);
    return isNaN(valorEntero);
  }

  processUbigeo(data: string) {
    let extraer = data.toString().substring(2, 6);
    this.actasComponent.actas = [];
    this.actasComponent.detalleActa = null;
    this.actasComponent.limpiarPaginado();

    if (extraer == '0000') {
      this.processDepartamento(data);
    } else {
      this.processProvinciaODistrito(data);
    }
  }

  processDepartamento(data: string) {
    // this.setClassProperties(
    //   this.actasComponent,
    //   ["deshabilitarBotonLimpiar", "deshabilitarBotonFiltrar"],
    //   [true, true]
    // );
    this.setControlsValues(
      this.actasComponent.myFormUbigeo,
      ['departamento', 'provincia', 'distrito'],
      [data.toString(), 0, 0]
    );
    this.actasComponent.listarProvincias(
      this.actasComponent.controlEleccion.value,
      this.actasComponent.controlAmbito.value,
      data
    );
    this.actasComponent.listarParticipacionCiudadana(TIPO_FILTRO.UBIGEO_NIVEL_01, 0, 0, 0, 1, data);
  }

  processProvinciaODistrito(data: string) {
    let extraer = data.toString().substring(4, 6);
    let extraerDepartamento = data.toString().substring(0, 2) + '0000';

    if (extraer == '00') {
      if (extraerDepartamento == this.actasComponent.controlDepartamento.value)
        this.setControlsValues(this.actasComponent.myFormUbigeo, ['provincia'], [data.toString()]);
      this.actasComponent.listarDistritos(
        this.actasComponent.controlEleccion.value,
        this.actasComponent.controlAmbito.value,
        data
      );
      this.setClassProperties(
        this.actasComponent,
        ['deshabilitarBotonLimpiar', 'deshabilitarBotonFiltrar'],
        [true, true]
      );
      this.actasComponent.listarParticipacionCiudadana(
        TIPO_FILTRO.UBIGEO_NIVEL_02,
        this.actasComponent.controlDepartamento.value,
        this.actasComponent.controlProvincia.value,
        0,
        2,
        data
      );
    } else {
      this.setControlsValues(this.actasComponent.myFormUbigeo, ['distrito'], [data.toString()]);
      this.setClassProperties(
        this.actasComponent,
        ['deshabilitarBotonLimpiar', 'deshabilitarBotonFiltrar'],
        [false, false]
      );
      if (this.actasComponent.controlAmbito.value == '1') {
        this.actasComponent.filtrar();
        this.actasComponent.listarLocalesVotacion(this.actasComponent.controlEleccion.value, data);
        this.actasComponent.listarParticipacionCiudadana(
          TIPO_FILTRO.UBIGEO_NIVEL_02,
          this.actasComponent.controlDepartamento.value,
          this.actasComponent.controlProvincia.value,
          this.actasComponent.controlDistrito.value,
          3,
          data
        );
      }
    }
  }

  capitalizeFirstLetter(str: string) {
    return str.charAt(0).toUpperCase() + str.toLowerCase().slice(1);
  }

  listarElecciones(idEleccion: number): Observable<FrontendResponse<[Eleccion]>> {
    return this.actaApiService.listarElecciones(idEleccion);
  }

  listarActas(
    codigoLocalVotacion: number,
    idAmbitoGeografico: number,
    idUbigeo: number,
    page: number,
    size: number
  ): Observable<FrontendResponse<Acta>> {
    return this.actaApiService.listarActas(codigoLocalVotacion, idAmbitoGeografico, idUbigeo, page, size);
  }

  buscarMesa(codigoMesa: string): Observable<FrontendResponse<[Mesa]>> {
    return this.actaApiService.buscarMesa(codigoMesa);
  }

  listarLocales(idEleccion: number, idUbigeo: number): Observable<FrontendResponse<[LocalVotacion]>> {
    return this.actaApiService.listarLocales(idEleccion, idUbigeo);
  }

  listarMapaCalor(
    codigoAgrupacionPolitica: number,
    idAmbitoGeografico: number,
    idEleccion: number,
    idUbigeoDepartamento: number,
    idUbigeoProvincia: number,
    idUbigeoDistrito: number,
    tipoFiltro: string
  ): Observable<FrontendResponse<[MapaCalor]>> {
    return this.actaApiService.listarMapaCalor(
      codigoAgrupacionPolitica,
      idAmbitoGeografico,
      idEleccion,
      idUbigeoDepartamento,
      idUbigeoProvincia,
      idUbigeoDistrito,
      tipoFiltro
    );
  }

  descargarImagen(idActa: string): Observable<Blob> {
    return this.actaApiService.descargarImagen(idActa);
  }

  descargarPdf(idActa: string): Observable<Blob> {
    return this.actaApiService.descargarPdf(idActa);
  }

  listarCandidatosPorAgrupacion(idActa: number, idAgrupacion: number): Observable<FrontendResponse<[Candidato]>> {
    return this.actaApiService.listarCandidatosPorAgrupacion(idActa, idAgrupacion);
  }

  isMapaCalor(eleccion: MapaCalor | ParticipacionCiudadano): eleccion is MapaCalor {
    return (eleccion as MapaCalor).porcentajeActasContabilizadas !== undefined;
  }

  loadGeodataTwo(
    chart: am5map.MapChart,
    polygonSeries: am5map.MapPolygonSeries,
    root: am5.Root,
    mapaCalor: [MapaCalor | ParticipacionCiudadano]
  ) {
    chart.set('projection', am5map.geoMercator());
    let title = '';

    const map = `./assets/lib/amcharts5/geodata/json/peruLow.json`;
    am5.net.load(map, chart).then((result) => {
      let geodata = am5.JSONParser.parse(result.response);
      let data = [];
      for (const feature of geodata.features) {
        let id_ = feature.id;
        let value_: number | undefined = 0;
        if (!(feature.id == 'PE-LMA' || feature.id == 'PE-LKT')) {
          let tmp = mapaCalor.find((x) => x.ubigeoNivel01 == Number(feature.id));
          if (tmp) {
            if ('totalElectoresHabiles' in tmp) {
              value_ = tmp.totalElectoresHabiles;
            } else if ('porcentajeActasContabilizadas' in tmp) {
              value_ = tmp.porcentajeActasContabilizadas;
            }
          }
        }
        data.push({
          id: id_,
          value: value_
        });
      }

      polygonSeries.set('geoJSON', geodata);
      if (this.isMapaCalor(mapaCalor[0])) {
        polygonSeries.set('fill', am5.color(0xdfe5eb));
      }
      polygonSeries.data.setAll(data);
    });

    chart.seriesContainer.children.push(
      am5.Label.new(root, {
        x: 5,
        y: 5,
        text: title,
        background: am5.RoundedRectangle.new(root, {
          fill: am5.color(0xffffff),
          fillOpacity: 0.2
        })
      })
    );
  }

  public cargarJsonMapa(ruta: string) {
    return this.requestsService.get(ruta).pipe(map((x) => x.body));
  }

  setResumenTotales(resumenTotales: ActaResumenGeneralTotalesResponse) {
    this.resumenTotales = resumenTotales;
  }

  getResumenTotales() {
    return this.resumenTotales;
  }
}
