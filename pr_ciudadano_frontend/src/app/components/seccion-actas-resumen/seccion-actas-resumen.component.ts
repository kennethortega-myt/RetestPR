import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  TemplateRef
} from '@angular/core';
import { Router } from '@angular/router';
import { getformattedNumberWithDecimals } from '../../helpers/numbers-helper.common';
import { Resumen } from '../../interfaces/resumen-bean';

@Component({
  selector: 'app-seccion-actas-resumen',
  templateUrl: './seccion-actas-resumen.component.html',
  standalone: false
})
export class SeccionActasResumenComponent implements OnChanges {
  public ActasContabilizadasKey = 'seccion-actas.actasContabilizadas';
  public TotalActasKey = 'seccion-actas.totalActas';
  public ParticipacionCiudadanaKey = 'seccion-actas.participacionCuidadana';
  public ActasEnvioJeeKey = 'seccion-actas.actasEnvioJee';
  public ActasPendientesKey = 'seccion-actas.actasPendientes';
  public ActualizadoKey = 'seccion-actas.actualizado';
  public ALasKey = 'seccion-actas.aLas';
  public ContabilizadasKey = 'seccion-actas.contabilizadas';
  public EnvioJeeKey = 'seccion-actas.envioJee';
  public PendientesKey = 'seccion-actas.pendientes';
  public ParticipacionCuidadanaKey = 'seccion-actas.participacionCiudadana';
  public VerActaKey = 'seccion-actas.verActas';
  public VerDetalleKey = 'seccion-actas.verDetalle';

  @Input() resumen?: Resumen;
  @Input() showMainButtons = false;
  @Output() seeDetailButtonAction = new EventEmitter();
  actasContabilizadas?: number;
  acumuladoActasEnviadasJEE?: number;
  @ContentChild('content', { static: false, read: TemplateRef }) contentTemplate!: TemplateRef<any>;
  desplegar = false; // Estado inicial
  hasContent = false;

  constructor(private router: Router) {}

  ngAfterContentInit() {
    this.hasContent = !!this.contentTemplate;
  }

  ngOnChanges(_: SimpleChanges): void {
    this.actasContabilizadas = this.getActasContabilizadas(this.resumen);
    this.acumuladoActasEnviadasJEE = this.getAcumuladoActasEnviadasJEE(this.resumen);
  }

  private getActasContabilizadas(resumen: any): number {
    if (resumen?.actasContabilizadas) {
      let actasContabilizadas = getformattedNumberWithDecimals(resumen.actasContabilizadas, 3);
      return actasContabilizadas < 0.2 ? 0.15 : actasContabilizadas;
    }
    return 0;
  }

  private getAcumuladoActasEnviadasJEE(resumen: any): number {
    if (resumen?.actasEnviadasJee) {
      const acumuladas = (resumen?.actasContabilizadas ?? 0) + resumen.actasEnviadasJee;
      let acumuladoActasEnviadasJEE = getformattedNumberWithDecimals(acumuladas, 3);
      return acumuladoActasEnviadasJEE < 0.3 ? 0.3 : acumuladoActasEnviadasJEE;
    }
    return 0;
  }

  verActas(): void {
    this.router.navigateByUrl('main/actas');
  }

  regresar(): void {
    this.router.navigateByUrl('main/resumen');
  }

  verDetalle(): void {
    this.seeDetailButtonAction.emit();
  }

  toggleClass() {
    this.desplegar = !this.desplegar; // Alterna el valor de isActive
  }
}
