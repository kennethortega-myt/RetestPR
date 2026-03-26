import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { PadronProgresoService } from '../../../services/padron-progreso.service';
import { DialogoConfirmacionComponent } from '../dialogo-confirmacion/dialogo-confirmacion.component';
import { WebsocketService } from '../../../services/websocket.service';
import { Subscription } from 'rxjs';
import { WsProgress } from '../../../interfaces/ws-progress.interface';

@Component({
  selector: 'app-instalacion-paginado',
  templateUrl: './instalacion-paginado.component.html',
  imports: [CommonModule, MatProgressBarModule, MatProgressSpinnerModule],
})
export class InstalacionPaginadoComponent implements OnInit, OnDestroy {

  private readonly dialogo = inject(MatDialog);
  private readonly toastService = inject(ToastrService);
  private readonly websocketService = inject(WebsocketService);
  private readonly padronProgresoService = inject(PadronProgresoService);

  private progressSubscription?: Subscription;
  public progresoImport: number = 0;
  public btnSpinner = false;
  public mensajesPaginado: string[] = [];
  public padronCompleto: boolean = false;

  constructor(  ) {  }

  ngOnInit() {
    this.progresoImport = 0;

    if (sessionStorage.getItem('loadingws') === 'true') {
      this.btnSpinner = false;
    } else {
      this.btnSpinner = true;
    }

    this.progressSubscription = this.websocketService.progress$.subscribe((progress: WsProgress | null) => {
      if(progress) {
        this.progresoImport = progress.porcentaje;
        if(progress.texto){
          this.mensajesPaginado.push(progress.texto);
        }
        if (progress.estado == '0') {
          this.padronCompleto = true;
          this.toastService.success('Se ha finalizado la carga con éxito');
          sessionStorage.setItem('loadingws', 'false');
          this.btnSpinner = true;
        }
        this.websocketService.clearProgress();
      }
    });
    
    this.faltaMigrar();
  }

  ngOnDestroy(): void {
      if(this.progressSubscription) {
        this.progressSubscription.unsubscribe();
      }
  }

  alertImportar(): void {
    this.dialogo
      .open(DialogoConfirmacionComponent, {
        data: `¿Está seguro de que desea continuar con el proceso de Obtener padrón electoral?`,
      })
      .afterClosed()
      .subscribe((confirmado: boolean) => {
        if (confirmado) {
          this.importar();
        }
      });
  }

  public importar() {
    this.progresoImport = 0;
    sessionStorage.setItem('loadingws', 'false');
    this.btnSpinner = false;

    this.websocketService.publish('/app/importar-paginado-ws',null);
  }

  faltaMigrar() {
    this.padronProgresoService.finalizo().subscribe({
      next: (resp) => {
        this.padronCompleto = resp.data;
      },
      error: (err) => {
        console.log('error: ' + err);
      },
    });
  }
}
