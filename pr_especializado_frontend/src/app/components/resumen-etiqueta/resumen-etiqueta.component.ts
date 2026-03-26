import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { formatearFecha } from '../../helpers/funciones';
import { FiltroModel } from '../../interfaces/filtro.model';
import { ResumenTotal } from '../../interfaces/output/resumen-total.model';
import { ResumenEtiqueta } from './resumen-etiqueta.model';
import { PopResolucionesComponent } from '../../pages/main/actas-observadas/pop-resoluciones/pop-resoluciones.component';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-resumen-etiqueta',
  templateUrl: './resumen-etiqueta.component.html',
  imports: [ TranslateModule, CommonModule]
})
export class ResumenEtiquetaComponent {
  resumenEtiqueta?: ResumenEtiqueta;

  @Input() resumenTotal?: ResumenTotal;
  @Input() filtroModel?: FiltroModel;
  @Input() cadenaFiltro: string = '';

  @Output() generarReporteEvent = new EventEmitter<string>();

  constructor(public dialog: MatDialog) {}

  public generateReport() {
    this.generarReporteEvent.emit();
  }

  openDialog2() {
    this.dialog.open(PopResolucionesComponent, {
      width: '675px', // Ancho del modal
      maxWidth: '98%',
      panelClass: 'modal-resolucion',
      data: {},
    });
  }

  public obtenerDatosFiltro(
    filtroModel?: FiltroModel,
    resumenTotal?: ResumenTotal
  ) {
    this.filtroModel = filtroModel;

    if(resumenTotal) {
      this.resumenTotal = resumenTotal;
    }

    let cadena =
      '<span>[' +
      filtroModel?.nombreTipoEleccion +
      ']</span>' +
      (
        filtroModel?.esEleccionParaDistritoElectoral ?
        '/<span>' + filtroModel?.nombreDistritoElectoral + '</span>' :
        '/<span>' + filtroModel?.nombreAmbitoGeografico + '</span>'
      );

    let textoNivel01 = filtroModel?.nombreUbigeoNivel01
      ? '/<span>' + filtroModel?.nombreUbigeoNivel01 + '</span>'
      : '';
    let textoNivel02 = filtroModel?.nombreUbigeoNivel02
      ? '/<span>' + filtroModel?.nombreUbigeoNivel02 + '</span>'
      : '';
    let textoNivel03 = filtroModel?.nombreUbigeoNivel03
      ? '/<span>' + filtroModel?.nombreUbigeoNivel03 + '</span>'
      : '';
    let textoLocalVotacion = filtroModel?.nombreLocalVotacion
      ? '/<span>' + filtroModel?.nombreLocalVotacion + '</span>'
      : '';

    cadena =
      cadena + textoNivel01 + textoNivel02 + textoNivel03 + textoLocalVotacion;

    this.cadenaFiltro = cadena;
  }

  public obtenerEleccion() : string {
    const filtroModel = this.filtroModel;
    return filtroModel?.nombreTipoEleccion ?? '';
  }

  formatearFecha(data: ResumenTotal): string | null {
    return formatearFecha(data.fechaActualizacion!);
  }
}
