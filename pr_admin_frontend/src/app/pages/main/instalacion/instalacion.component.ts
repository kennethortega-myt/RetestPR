import { Component, HostListener, inject, OnDestroy, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Subscription } from 'rxjs';

import { ImportarRequest } from '../../../interfaces/importar-request';
import { IMaeImportar, IMaeImportarComplemento, ImportarWsRequest } from '../../../interfaces/importar.interface';
import { MaeimportarService } from '../../../services/maeimportar.service';
import { DialogoConfirmacionComponent } from '../dialogo-confirmacion/dialogo-confirmacion.component';
import { DialogComponent } from '../../../components/dialog/dialog/dialog.component';
import { WebsocketService } from '../../../services/websocket.service';
import { IGenericInterface } from '../../../interfaces/general.interface';
import { LoadingService } from '../../../services/loading.service';
import { WsProgress } from '../../../interfaces/ws-progress.interface';
import { EnumProceso } from '../../../helpers/enums';

@Component({
  selector: 'app-instalacion',
  templateUrl: './instalacion.component.html',
  imports: [ReactiveFormsModule, CommonModule, MatProgressBarModule]
})
export class InstalacionComponent implements OnInit, OnDestroy {

  private readonly dialogo = inject(MatDialog);
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastrService);
  private readonly loaderService = inject(LoadingService);
  private readonly websocketService = inject(WebsocketService);
  private readonly maeimportarService = inject(MaeimportarService);
  
  public importarForm: FormGroup = this.formBuilder.group({
    opt: '',
  });

  private progressSubscription?: Subscription;
  public lstMaeImportar?: IMaeImportar[];
  public progresoImport: number = 0;
  public progresoDownload: number = 0;
  public maxProgreso: number = 100;
  public mensajes: string[] = [];
  public importarRequest?: ImportarRequest;
  public importarReq?: ImportarRequest;
  public seleccion: boolean = true;
  private procesoFinalizado = false;

  private readonly ESTADO_CONTINUA = '1';
  private readonly ESTADO_OK = '0';
  private readonly ESTADO_ERROR = '2';

  complementos : IMaeImportarComplemento[] = [
    {
      atributo: EnumProceso.ID_GETBD,
      icono: 'assets/icons/ico-base.svg',
      descripcion: 'Inicializa las correcciones principales',
    },
    {
      atributo: EnumProceso.ID_GETVISTAELECCION,
      icono: 'assets/icons/ico-obtenervistaeleccion.svg',
      descripcion: 'Genera la vista de resultados del proceso electoral',
    },
    {
      atributo: EnumProceso.ID_GETVISTACTA,
      icono: 'assets/icons/ico-obtenervistaacta.svg',
      descripcion: 'Genera la vista consolidada de resultados por acta electoral.',
    },
    {
      atributo: EnumProceso.ID_GETVISTAPARTICIPACION,
      icono: 'assets/icons/ico-obtenervistaparticipacionciudadana.svg',
      descripcion: 'Genera la vista consolidada de participación ciudadana.',
    },
    {
      atributo: EnumProceso.ID_GETVISTAMESA,
      icono: 'assets/icons/ico-obtenervistamesa.svg',
      descripcion: 'Genera la vista consolidada de resultados por mesa de votación.',
    },
    {
      atributo: EnumProceso.ID_GETVISTATOTALCPAP,
      icono: 'assets/icons/ico-agrupacionpolitica.svg',
      descripcion: '',
    }
  ];

  constructor() { }

  seleccionarTipo(valor: string) {
    this.importarForm.get('opt')!.setValue(valor);
    this.seleccion = false;
  }

  esActivo(valor: string): boolean {
    return this.importarForm.get('opt')!.value === valor;
  }

  ngOnInit() {
    this.progresoImport = 0;
    this.progresoDownload = 0;
    this.obtenerOpciones();

    this.progressSubscription = this.websocketService.progress$
      .subscribe((progress: WsProgress | null) => {
        if (this.procesoFinalizado) return;
        if (!progress) {
          return;
        }
        this.progresoImport = progress.porcentaje;
        switch (progress.estado) {
          case this.ESTADO_CONTINUA:
            if (progress.texto) {
              this.mensajes.push(progress.texto);
            }
            break;
          case this.ESTADO_OK:
            if (progress.texto) {
              this.mensajes.push(progress.texto);
            }
            this.procesoFinalizado = true;
            this.finalizarExito();
            break;
          case this.ESTADO_ERROR:
            console.log('progress:', progress)
            this.procesoFinalizado = true;
            this.finalizarError(progress.texto);
            break;
        }

        this.websocketService.clearProgress();
      });
  }

  private finalizarExito(): void {
    this.loaderService.endRequest();
    this.obtenerOpciones();
    this.seleccion = true;
    this.importarForm.get('opt')!.setValue('');
    this.toastService.success('Se ha finalizado la carga con éxito');
  }

  private finalizarError(mensaje?: string): void {
    this.loaderService.endRequest();
    this.importarForm.get('opt')!.setValue('');
    this.dialogo.open(DialogComponent, {
      width: '30%',
      minWidth: '320px',
      maxWidth: '100%',
      data: { mensaje: mensaje ?? 'Ocurrió un error durante la migración.' },
    }).afterClosed().subscribe(() => {
      this.seleccion = true;
    });
  }

  ngOnDestroy(): void {
      if(this.progressSubscription) {
        this.progressSubscription.unsubscribe();
      }
  }

  obtenerOpciones(): void {
    this.maeimportarService.obtener().subscribe({
      next: (resp: IGenericInterface<IMaeImportar[]>) => {
        this.lstMaeImportar = resp.data.map((item: IMaeImportar) => {
          const complemento = this.complementos.find(
            (d) => d.atributo === item.atributo
          );

          return {
            ...item,
            icono: complemento?.icono,
            descripcion: complemento?.descripcion ?? item.descripcion,
          };
        });
      },
    });
  }

  alertImportar(): void {
    let resultado: IMaeImportar[];
    resultado = this.lstMaeImportar!.filter(
      (importar) => importar.atributo == this.importarForm.get('opt')!.value
    );

    this.dialogo
      .open(DialogoConfirmacionComponent, {
        data: `¿Está seguro de que desea continuar con el proceso de ${resultado[0].etiqueta}?`,
      })
      .afterClosed()
      .subscribe((confirmado: boolean) => {
        if (confirmado) {
          this.importar();
        }
      });
  }

  public importar(): void {
    if (!this.lstMaeImportar) {
      return;
    }

    this.loaderService.startRequest();

    this.procesoFinalizado = false;
    this.mensajes = [];
    this.progresoImport = 0;

    const opcionSeleccionada = this.importarForm.get('opt')?.value;

    const requestObj: ImportarWsRequest = this.lstMaeImportar.reduce(
      (acc: ImportarWsRequest, curr: IMaeImportar) => {
        acc[curr.atributo] = opcionSeleccionada === curr.atributo ? 1 : 0;
        return acc;
      },
      {}
    );

    this.websocketService.publish('/app/importar-ws', requestObj);
  }

  @HostListener('document:contextmenu', ['$event'])
  disableRightClick(event: MouseEvent): void {
    event.preventDefault();
  }
}
