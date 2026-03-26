import { Component, ElementRef, Input, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, take, takeUntil } from 'rxjs';
import { GenericFilterUbigeoComponent } from '../../../../../components/generic-filter-ubigeo/generic-filter-ubigeo.component';
import { ACTAS_RESUMEN_GENERAL_TITLE } from '../../../../../helpers/actas-resumen-general.helper';
import { MAIN_ELECTION_IDS, TIPO_FILTRO, UBIGEO_LEVELS } from '../../../../../helpers/constantes';
import { getFilterTypeForBackend } from '../../../../../helpers/ubigeo-level.common';
import { GenericFilterParams } from '../../../../../interfaces/filtro-settings';
import { MesasDetail, MesasDetailParams } from '../../../../../interfaces/mesas-de-votacion.interfaces';
import { Resumen } from '../../../../../interfaces/resumen-bean';
import { MapaCalor, ResumenGeneral } from '../../../../../interfaces/resumen-general-bean';
import { MesasDeVotacionService } from '../../../../../services/elecciones-generales/mesas-de-votacion.service';
import { ResumenGeneralService } from '../../../../../services/elecciones-generales/resumen-general.service';

@Component({
  selector: 'app-actas-resumen',
  templateUrl: './actas-resumen.component.html',
  standalone: false
})
export class ActasResumenComponent implements OnInit {
  @Input() idEleccion: any;
  @Input() resumen: Resumen;
  @Input() idProceso: any;
  @Input() ingresoTabCuatro: boolean = false;
  @ViewChildren('mycharts') allMyCanvas: any;
  @ViewChild('selectDepartamento', { static: false, read: ElementRef }) selectDepartamento: ElementRef;
  @ViewChild('selectProvincia', { static: false, read: ElementRef }) selectProvincia: ElementRef;
  @ViewChild('selectDistrito', { static: false, read: ElementRef }) selectDistrito: ElementRef;
  @ViewChild(GenericFilterUbigeoComponent) mainFiltroUbigeoComponent: GenericFilterUbigeoComponent;
  mapaCalor: [MapaCalor];
  cargarPie = false;
  MAIN_ELECTION_IDS = MAIN_ELECTION_IDS;
  elecciones: ResumenGeneral[];
  deshabilitarBotonFiltrar: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  primeraVez = true;
  idAmbito: any = 1;

  deshabilitarDepartamento: boolean = false;
  deshabilitarProvincia: boolean = false;
  deshabilitarDistrito: boolean = false;
  verFiltro: boolean = false;
  TITLE = ACTAS_RESUMEN_GENERAL_TITLE;
  mesasDetail: MesasDetail = {} as MesasDetail;
  myFormUbigeo: FormGroup = this.fb.group({
    region: ['0', Validators.required],
    departamento: [{ value: '0', disabled: this.deshabilitarDepartamento }, Validators.required],
    provincia: [{ value: '0', disabled: this.deshabilitarProvincia }, Validators.required],
    distrito: [{ value: '0', disabled: this.deshabilitarDistrito }, Validators.required]
  });
  private temporalFiltro: string;

  constructor(
    private readonly fb: FormBuilder,
    private readonly resumenGeneralService: ResumenGeneralService,
    private readonly mesasDeVotacionService: MesasDeVotacionService) {}

  ngOnInit(): void {
    this.listarElecciones(0);
    this.loadMesasDetailByTheWorld();
  }

  limpiar(): void {
    this.myFormUbigeo.get('departamento').setValue(0, { emitEvent: false });
    this.myFormUbigeo.get('provincia').setValue(0, { emitEvent: false });
    this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });
    this.listarElecciones(0);
  }

  listarElecciones(idDistrito: number): void {
    const { region, departamento, provincia, distrito } = this.myFormUbigeo.value;
    const filtro: string = `${region}${departamento}${provincia}${distrito}`;

    if (this.temporalFiltro == undefined) {
      this.temporalFiltro = filtro;
    } else if (this.temporalFiltro == filtro) {
      return;
    } else if (this.temporalFiltro != filtro) {
      this.temporalFiltro = filtro;
    }

    this.elecciones = [];

    let idProceso = this.idProceso;
    let activo = 1;
    let tipoFiltro = 'eleccion';
    let idAmbito = this.myFormUbigeo.get('region').value;
    let idAmbitoGeografico = null;
    let idNivel01 = Number(this.myFormUbigeo.get('departamento').value);
    let idNivel02 = Number(this.myFormUbigeo.get('provincia').value);

    if (idAmbito == 1 || idAmbito == 2) {
      tipoFiltro = 'ambito_geografico';
      idAmbitoGeografico = idAmbito;
    }
    if (idDistrito != 0) {
      tipoFiltro = 'ubigeo_nivel_03';
    } else if (idNivel02 != 0) {
      tipoFiltro = 'ubigeo_nivel_02';
    } else if (idNivel01 != 0) {
      tipoFiltro = 'ubigeo_nivel_01';
    }

    // Loading functionality removed
    this.resumenGeneralService
      .listarElecciones(activo, idProceso, idNivel01, idNivel02, idDistrito, tipoFiltro, idAmbitoGeografico)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          // Loading functionality removed
          this.elecciones = result.data;
        },
        error: (error) => {/* Loading functionality removed */}
      });
  }

  replaceTitulos(strTitulo: string): string {
    return strTitulo.replace('MULTIPLE', 'MÚLTIPLE').replace('UNICO', 'ÚNICO');
  }

  obtenerTotal(eleccion: ResumenGeneral): number {
    return eleccion.actasContabilizadas + eleccion.actasObservadasEnviadas + eleccion.actasPendientes;
  }

  mostrarResumen(): void {
    let region = this.myFormUbigeo.get('region').value;
    if (region > 0) {
      this.myFormUbigeo.get('departamento').enable();
    } else {
      this.myFormUbigeo.get('departamento').disable();
    }
  }

  applyFiltersEvent(params: GenericFilterParams): void {
    const { idAmbitoGeografico, ubigeoNivel1, ubigeoNivel2, ubigeoNivel3 } = params;
    switch (params.tipoFiltro) {
      case TIPO_FILTRO.AMBITO_GEOGRAFICO:
      case TIPO_FILTRO.ELECCION:
        this.myFormUbigeo.controls['region'].setValue(idAmbitoGeografico, { emitEvent: false });
        this.deshabilitarBotonFiltrar = false;
        this.myFormUbigeo.get('departamento').setValue(0, { emitEvent: false });
        this.myFormUbigeo.get('provincia').setValue(0, { emitEvent: false });
        this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });
        if (params.idAmbitoGeografico) {
          this.verFiltro = true;
        } else {
          this.verFiltro = false;
          this.listarElecciones(0);
          this.loadMesasDetailByTheWorld();
        }
        break;
      case TIPO_FILTRO.UBIGEO_NIVEL_01:
        this.myFormUbigeo.controls['region'].setValue(idAmbitoGeografico, { emitEvent: false });
        this.myFormUbigeo.controls['departamento'].setValue(ubigeoNivel1, { emitEvent: false });
        this.myFormUbigeo.get('provincia').setValue(0, { emitEvent: false });
        this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });

        this.deshabilitarBotonFiltrar = !ubigeoNivel1;
        break;
      case TIPO_FILTRO.UBIGEO_NIVEL_02:
        this.myFormUbigeo.controls['region'].setValue(idAmbitoGeografico, { emitEvent: false });
        this.myFormUbigeo.controls['departamento'].setValue(ubigeoNivel1, { emitEvent: false });
        this.myFormUbigeo.controls['provincia'].setValue(ubigeoNivel2, { emitEvent: false }); 
        this.myFormUbigeo.get('distrito').setValue(0, { emitEvent: false });       
        this.deshabilitarBotonFiltrar = !ubigeoNivel2;
        break;
      case TIPO_FILTRO.UBIGEO_NIVEL_03:
        this.myFormUbigeo.controls['region'].setValue(idAmbitoGeografico, { emitEvent: false });
        this.myFormUbigeo.controls['departamento'].setValue(ubigeoNivel1, { emitEvent: false });
        this.myFormUbigeo.controls['provincia'].setValue(ubigeoNivel2, { emitEvent: false });
        this.myFormUbigeo.controls['distrito'].setValue(ubigeoNivel3, { emitEvent: false });
        this.deshabilitarBotonFiltrar = !ubigeoNivel3;
        break;
      default:
        break;
    }

    const idDistrito: number = Number(ubigeoNivel3) || 0;
    this.primeraVez = false;
    this.listarElecciones(idDistrito);
    const customParamsMesasDetail = {
      ambitoGeografico: idAmbitoGeografico,
      tipoFiltro: getFilterTypeForBackend({
        departmentUbigeoId: ubigeoNivel1,
        provinceUbigeoId: ubigeoNivel2,
        districtUbigeoId: ubigeoNivel3
      }),
      ubigeoNivel1,
      ubigeoNivel2,
      ubigeoNivel3
    };
    this.loadMesasDetail(customParamsMesasDetail);
  }

  reiniciarListaPorCambioDeFiltro($event: any) {
    this.listarElecciones(0);
    this.loadMesasDetailByTheWorld();
  }

  private loadMesasDetailByTheWorld() {
    this.mesasDeVotacionService
      .getMesasDetail$({
        tipoFiltro: UBIGEO_LEVELS.ELECTION
      })
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

  private loadMesasDetail(params: MesasDetailParams) {
    this.mesasDeVotacionService
      .getMesasDetail$(params)
      .pipe(take(1))
      .subscribe((response) => {
        if (response.success) {
          this.mesasDetail = response.data;
        }
      });
  }

}
