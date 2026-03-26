import { Component, inject } from '@angular/core';
import { ElectionsApiService } from '../../../../services/elecciones-generales/elections-api.service';
import { IProcesoElectoralData } from '../../../../interfaces/proceso-electoral.interface';
import { delay, switchMap, take } from 'rxjs';
import { DetalleReporteAutomaticoPorTipoEleccion } from '../../../../interfaces/response/detalle-por-tipo-eleccion.interface';
import { ReporteService } from '../../../../services/elecciones-generales/reporte.service';
import { getRandomImage } from '../../../../helpers/random-image';
import { CODIGO_TIPO_ELECCION } from '../../../../helpers/constantes';
import { FechaApiService } from '../../../../services/elecciones-generales/fecha.service';
import { Fecha } from '../../../../interfaces/fecha';

@Component({
  selector: 'app-reportes-automaticos',
  standalone: false,
  templateUrl: './reportes-automaticos.component.html',
})
export class ReportesAutomaticosComponent {

  public procesoElectoralActivo: IProcesoElectoralData;
  public reportesAutomaticosPorTipoEleccion: DetalleReporteAutomaticoPorTipoEleccion[];

  private readonly electionService = inject(ElectionsApiService);
  private readonly fechaService = inject(FechaApiService);
  private readonly reporteService = inject(ReporteService);

  public randomImageUrl: string = getRandomImage();
  public existsPresidencyReports = false;
  public listaFecha: Fecha;

  constructor() {
    this.obtenerDetallesReportesAutomaticos();
    this.obtenerFechaActualizacion();
  }

  obtenerDetallesReportesAutomaticos (): void {
    this.electionService.obtenerProcesoElectoralActivo().pipe(
      take(1),
      switchMap(result => {
        this.procesoElectoralActivo = result.data;
        return this.reporteService
          .obtenerReportesAutomaticosPorTipoEleccion(result.data.id);
      })
    ).subscribe(result => {
      this.reportesAutomaticosPorTipoEleccion = result.body;
      const detallePresidencia = this.reportesAutomaticosPorTipoEleccion.find(r => r.codigoTipoEleccion === CODIGO_TIPO_ELECCION.PRESIDENCY);
      if (detallePresidencia.totalesPorTipoEleccion > 0) {
        this.existsPresidencyReports = true;        
      } else {
        this.existsPresidencyReports = false;
      }
      this.ordenarReportesPorTipoEleccion();
    });
  }

  obtenerFechaActualizacion(): void {
    this.fechaService.listarFecha()
      .pipe(take(1))
      .subscribe(result => {
        this.listaFecha = result.data;
      });
  }


  ordenarReportesPorTipoEleccion(): void {
    const orden: Record<string, number> = {
      "Presidencial": 1,
      "Senadores Distrito Electoral Único": 2,
      "Senadores Distrito Electoral Múltiple": 3,
      "Diputados": 4,
      "Parlamento Andino": 5,
    };

    this.reportesAutomaticosPorTipoEleccion.sort((a, b) => {
      const ordenA = orden[a.nombreTipoEleccion] ?? 99;
      const ordenB = orden[b.nombreTipoEleccion] ?? 99;
      return ordenA - ordenB;
    });
  }


  actualizarReportesAutomaticos (): void {
    this.reportesAutomaticosPorTipoEleccion = [];
    this.obtenerDetallesReportesAutomaticos();
  }
}
