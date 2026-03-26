import { Component, HostListener, inject, Input } from "@angular/core";

import { ACTA_CODIGO_ESTADO, ACTA_CODIGO_ESTADO_RESOLUCION, ACTA_LINEAT_CODIGO_ESTADO, CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO } from "../../../helpers/constantes";
import { Mesa } from "../../../interfaces/acta-bean";
import { ActasService } from "../../../services/elecciones-generales/actas.service";
import { AMBITO } from "../../../constants/constantes";

@Component({
  selector: "app-det-acta",
  templateUrl: "./det-acta.component.html",
  standalone: false,
})
export class DetActaComponent {
  public detalleActaText = "actas-shared-det-acta.detalleActa"
  public electoresHabiles = "actas-shared-det-acta.electoresHabiles"
  public totalVotantes = "actas-shared-det-acta.totalVotantes"
  public participacionCiudadana = "actas-shared-det-acta.participacionCiudadana"
  public estadoActa = "actas-shared-det-acta.estadoActa"
  public centroPoblado = "actas-shared-det-acta.centroPoblado"
  public solucionTecnologica = "actas-shared-det-acta.solucionTecnologica"
  codigoSolucion = CODE_SOLUCION_TECNOLOGICO_ESCRUTINIO;

  @Input() detalleActa: Mesa;
  @Input() idAmbito: number = 1;

  mostrarDatos = false;
  esPantallaChica = this.checkPantallaChica();

  private readonly actaService: ActasService = inject(ActasService);

  @HostListener('window:resize', ['$event'])
  onResize(event: UIEvent): void {
    const isSmall = (event.target as Window).innerWidth < 960;
    this.esPantallaChica = isSmall;
    this.mostrarDatos = !isSmall;
  }

  toggleDatos() {
    if (this.esPantallaChica) {
      this.mostrarDatos = !this.mostrarDatos;
    }
  }

  // ────── MÉTODOS PÚBLICOS ───────────────────────────────────

  protected obtenerPorcentajeParticipacionCiudadana(detalle: Mesa): number {
    if (!detalle?.totalVotosEmitidos || !detalle?.totalElectoresHabiles) return 0;
    return (detalle.totalVotosEmitidos / detalle.totalElectoresHabiles) * 100;
  }

  protected mostrarNombreNivel(nivel: number, capital = false): string {
    return this.actaService.mostrarNombreNivelBase(this.idAmbito, nivel, capital);
  }

  protected nombreAmbitoSelect() {
    return AMBITO[this.idAmbito];
  }

  protected mostrarTotalElectoresHabiles(detalle: Mesa): string {
    return this.stringify(detalle.totalElectoresHabiles);
  }

  protected mostrarValorTotalVotantes(detalle: Mesa): string {
    if (this.isMesaNoInstalada(detalle)) return '-';
    return this.shouldHideText(detalle) ? '' : this.stringify(detalle.totalAsistentes);
  }

  protected mostrarValorParticipacionCiudadana(detalle: Mesa): string {
    if (this.isMesaNoInstalada(detalle)) return '-';
    if (detalle.codigoEstadoActa === ACTA_CODIGO_ESTADO.PARA_ENVIO_JEE) return '';
    if (this.shouldHideText(detalle)) return '';
    return detalle.porcentajeParticipacionCiudadana != null
      ? `${detalle.porcentajeParticipacionCiudadana} %`
      : '';
  }

  protected mostrarEtiquetaAdicionalEstadoActa(detalle: Mesa): string {
    const descripcion = detalle?.descripcionSubEstadoActa || detalle?.estadoDescripcionActaResolucion;
    const upperDesc = descripcion ? ` (${descripcion.toUpperCase()})` : '';

    if (this.isMesaNoInstalada(detalle)) {
      return upperDesc;
    }

    const { estadoActaResolucion, codigoEstadoActa } = detalle;
    const esExtraviada = estadoActaResolucion === ACTA_CODIGO_ESTADO_RESOLUCION.EXTRAVIADA;
    const esSiniestrada = estadoActaResolucion === ACTA_CODIGO_ESTADO_RESOLUCION.SINIESTRADA;
    const esParaEnvioOJEE = codigoEstadoActa === ACTA_CODIGO_ESTADO.PARA_ENVIO_JEE;
    const esContabilizada = codigoEstadoActa === ACTA_CODIGO_ESTADO.CONTABILIZADA;

    return (esParaEnvioOJEE || esContabilizada) && (esExtraviada || esSiniestrada)
      ? upperDesc
      : '';
  }

  // ────── MÉTODOS PRIVADOS ──────────────────────────────────

  private stringify(value?: number | undefined): string {
    return value != null ? String(value) : '';
  }

  private checkPantallaChica(): boolean {
    return window.innerWidth < 960;
  }

  private isMesaNoInstalada(detalle: Mesa): boolean {
    return (
      detalle.codigoEstadoActa === ACTA_CODIGO_ESTADO.CONTABILIZADA &&
      detalle.estadoActa === ACTA_LINEAT_CODIGO_ESTADO.MESA_NO_INSTALADA
    );
  }

  private shouldHideText(detalle: Mesa): boolean {
    const esContabilizada = detalle.codigoEstadoActa === ACTA_CODIGO_ESTADO.CONTABILIZADA;
    const estadoRes = detalle.estadoActaResolucion;
    const esOcultable = [ACTA_CODIGO_ESTADO_RESOLUCION.EXTRAVIADA, ACTA_CODIGO_ESTADO_RESOLUCION.SINIESTRADA].includes(estadoRes);
    return (!esContabilizada && esOcultable) || detalle.codigoEstadoActa === ACTA_CODIGO_ESTADO.PENDIENTE;
  }
}
