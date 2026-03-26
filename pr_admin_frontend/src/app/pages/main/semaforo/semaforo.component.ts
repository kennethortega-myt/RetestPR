import { Component, inject } from '@angular/core';

import { ArchivoService } from '../../../services/archivo.service';
import { ReportesService } from '../../../services/reportes.service';
import { EstadoSemaforo, SemaforoItem } from '../../../interfaces/semaforo.interface';

@Component({
  selector: 'app-semaforo',
  templateUrl: './semaforo.component.html',
  styleUrls: ['./semaforo.component.scss']
})
export class SemaforoComponent {

  private readonly archivoService = inject(ArchivoService);
  private readonly reporteService = inject(ReportesService);
  
  semaforos: SemaforoItem[] = [];
  
  constructor( ) {
    this.semaforos = [
      {
        key: 'db',
        label: 'Conexión a la base de datos',
        estado: 'ESPERA',
        validar: () => this.reporteService.validarServicioDB(),
      },
      {
        key: 'rabbit',
        label: 'Conexión al gestor de colas',
        estado: 'ESPERA',
        validar: () => this.reporteService.validarServicioRabbitmq(),
      },
      {
        key: 'fileserver',
        label: 'Conexión al repositorio de archivos',
        estado: 'ESPERA',
        validar: () => this.archivoService.validarServicioFileserver(),
      },
    ];
  }

  validar(item: SemaforoItem): void {
    item.estado = 'ESPERA';

    item.validar().subscribe({
      next: (result) => {
        if (result.success) {
          item.estado = result.data.estado ? 'OK' : 'ERROR';
        } else {
          item.estado = 'ERROR';
        }
      },
      error: () => {
        item.estado = 'ERROR';
      },
    });
  }

  getIcono(estado: EstadoSemaforo): string {
    const iconos: Record<EstadoSemaforo, string> = {
      ESPERA: 'assets/icons/iconEspera.svg',
      OK: 'assets/icons/iconCheckVerde.svg',
      ERROR: 'assets/icons/iconX.svg',
    };
    return iconos[estado];
  }
}
