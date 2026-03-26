import { Component, OnInit, ViewChild } from '@angular/core';
import { FiltroComponent } from '../../../components/filtro/filtro.component';
import { FiltroModel } from '../../../interfaces/filtro.model';
import { ResultadoBehaviorService } from '../../../services/resultado.behavior.service';
import { ResultadoComponent } from './resultado/resultado.component';
import { ComponentsModule } from '../../../components/components.module';
import { NgIf } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

@Component({
  selector: 'app-tabla',
  templateUrl: './tabla.component.html',
  imports: [
    ComponentsModule,
    NgIf,
    ResultadoComponent,
    TranslateModule
  ],
  standalone: true,
})
export class TablaComponent implements OnInit {
  constructor(
    private readonly resultadoBehaviorService: ResultadoBehaviorService,
    private readonly translate: TranslateService,
  ) {}

  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];

  mensaje: string = 'constantes.MENSAJE_ORIGINAL';
  filtroModel?: FiltroModel;

  isContentReady: boolean = false;

  @ViewChild(ResultadoComponent) resultadoComponent?: ResultadoComponent;
  @ViewChild(FiltroComponent) filtroComponent?: FiltroComponent;

  ngOnInit(): void {
    this.isContentReady = true;
    this.cargarMensajeInicial();
  }

  private cargarMensajeInicial(): void {
    this.translate.get('SeleccioneEleccion').subscribe((text: string) => {
      this.mensaje = text;
    });
  }

  mostrarMensaje(value: string) {
    this.mensaje = value;
  }

  filtrar(data: FiltroModel) {
    this.filtroModel = data;
    this.resultadoComponent?.filtrar(data);
    this.resultadoComponent?.form.get('estadoActa')?.setValue('', { emitEvent: false });
  }

  cambiosFiltroDinamicos(data: FiltroModel) {
    this.mensaje = '';
    this.filtroModel = data;
    this.resultadoBehaviorService.ejecutarFiltrar(data);
  }

  limpiar({ filtro, absolute }: { filtro: FiltroModel; absolute: boolean }): void {
    this.mensaje = absolute ? 'constantes.MENSAJE_ORIGINAL' : '';

    Object.assign(filtro, {
      nombreUbigeoNivel01: '',
      nombreUbigeoNivel02: '',
      nombreUbigeoNivel03: '',
      nombreAmbitoGeografico: 'TODOS',
      idDistritoElectoral: 30,
    });

    this.resultadoComponent?.limpiar({ filtro, absolute });
  }
}
