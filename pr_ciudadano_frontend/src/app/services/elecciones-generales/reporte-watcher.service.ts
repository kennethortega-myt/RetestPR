import { Injectable } from '@angular/core';
import { Subject, filter } from 'rxjs';

export type ReporteType = 'resumen_general' 
  | 'presidenciales' 
  | 'diputados' 
  | 'parlamento_andino' 
  | 'participacion_ciudadana' 
  | 'actas';

export interface HeaderNagivatinReporter {
  id: number;
  reportType: ReporteType;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteWatcherService {

  /**
   * Este subject se subscribirá en el header para que mande el tipo de reporte que se debe generar
   */
  public watcherSubject = new Subject<ReporteType>()

  constructor() { }

  /**
   * Este método se subscribirá en cada una de las vistas que se necesite escuchar cuando generar un reporte
   */
  public observeWatcherForReport(type: ReporteType) {
    return this.watcherSubject
      .pipe(filter(value => value == type))
  }
}
