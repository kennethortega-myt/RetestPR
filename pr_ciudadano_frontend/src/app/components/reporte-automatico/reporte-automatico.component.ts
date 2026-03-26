import { Component, inject, Input } from "@angular/core";
import { DetalleActa, DetalleReporteAutomaticoPorTipoEleccion } from "../../interfaces/response/detalle-por-tipo-eleccion.interface";
import { ReporteApiService } from "../../services/elecciones-generales/reporte-api.service";
import { BlobConUrl, BlobWithFilename } from "../actas-components/modal-visor-pdf/modal-visor-pdf.interface";
import { catchError, delay, of, take, throwError } from "rxjs";
import { DialogService } from "../../services/elecciones-generales/dialog.service";
import { PopupReportesAutomaticosComponent } from "../popup-reportes-automaticos/popup-reportes-automaticos.component";
import { getRandomImage } from "../../helpers/random-image";
import { PopupArchivoNoDisponibleComponent } from "../popup-archivo-no-disponible/popup-archivo-no-disponible.component";


@Component({
  selector: "app-reporte-automatico",
  templateUrl: "./reporte-automatico.component.html",
  standalone: false,
})
export class ReporteAutomaticoComponent {
  public randomImageUrl: string = getRandomImage();
  maxValueForScaleName: number[] = [];
  private readonly dialogService = inject(DialogService);
  private readonly reporteApiService = inject(ReporteApiService);

  @Input({ required: true }) detalleReporteAutomatico: DetalleReporteAutomaticoPorTipoEleccion;

  private descargarArchivo(url: string, nombreArchivo: string): void {
    const link = document.createElement("a");
    link.href = url;
    link.download = nombreArchivo;
    link.target = "_blank";
    link.click();
    URL.revokeObjectURL(url);
    link.remove();
  }

  descargarArchivoActa(acta: DetalleActa) {
    this.reporteApiService.descargarZipReportesAutomaticosPorUUID(acta.idActa)
      .pipe(
        take(1),
        catchError(() => {
          this.dialogService.openComponentData(PopupArchivoNoDisponibleComponent);
          return throwError(() => new Error('Error al descargar'));
        })
      )
      .subscribe((result: BlobWithFilename) => 
        this.descargarArchivo(result.url,result.filename)
      );
  }

  descargarReporte(tipoEleccion: DetalleReporteAutomaticoPorTipoEleccion) {
    this.reporteApiService
      .descargarReporteCandidato(Number(tipoEleccion.codigoTipoEleccion))
      .pipe(
        take(1),
        catchError(() => {
          this.dialogService.openComponentData(PopupArchivoNoDisponibleComponent);
          return throwError(() => new Error('Error al descargar'));
        })
      )
      .subscribe((result: BlobWithFilename) => 
        this.descargarArchivo(result.url, result.filename) 
      );
  }

  obtenerEstadoReporteAutomatico (estado: number): string {
    let estadoActa = "";
    switch(estado){
      case 2:
        estadoActa = "Contabilizada";
        break;
      case 3:
        estadoActa = "En Proceso";
        break;
      case 4:
        estadoActa = "Sin Datos";
        break;
      default:
        break;
    }
    return estadoActa;
  }

  verReportes(codigoTipoEleccion: string): void{
    this.dialogService.openComponentData(
      PopupReportesAutomaticosComponent, 
      {        
        minWidth: '60%',
        maxHeight: '95vh',
        maxWidth: '100%', 
        data: { codigoTipoEleccion } 
      }
    ).pipe(
      delay(500)
    ).subscribe(
      () => {/* Loading functionality removed */}
    );
  }

  calcularWith(votosValidados: number): string {
    if (votosValidados == 0) {
      return '0';
    }

    let valorMaximo = this.maxValueForScaleName[this.maxValueForScaleName.length - 1];
    let agregado = valorMaximo * 0.05;
    let votoCalculado = valorMaximo + agregado;
    let retorno2 = (votosValidados / votoCalculado) * 100;
    return retorno2.toString() + '%';
  }

}
