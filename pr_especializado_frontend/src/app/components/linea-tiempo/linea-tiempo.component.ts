import { Component, Input, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { EnumCodigoEstadoActaLineaTiempo } from '../../helpers/enums';
import { LineaTiempo } from '../../interfaces/output/acta-observada/linea-tiempo.model';

@Component({
  selector: 'app-linea-tiempo',
  templateUrl: './linea-tiempo.component.html',
  standalone: true,
  imports: [CommonModule, MatTooltipModule, TranslateModule],
})
export class LineaTiempoComponent {
  EnumCodigoEstadoActaLineaTiempo = EnumCodigoEstadoActaLineaTiempo;
  @Input({ required: true }) lineasTiempo?: [LineaTiempo];
  @Input({ required: true }) mensaje?: string;

  esPantallaChica = false;
  mostrarLineaTiempo = false;

  private readonly TRADUCCIONES_ESTADOS: Record<string, string> = {
    'Digitalización': 'LineaTiempo.Digitalizacion',
    'Digitación': 'LineaTiempo.Digitacion',
    'Contabilizada': 'LineaTiempo.Contabilizada',
    'Acta observada': 'LineaTiempo.ActaObservada',
    'Acta con nulidad': 'LineaTiempo.ActaConNulidad',
    'Para envío al JEE': 'LineaTiempo.ParaEnvioJEE',
    'Recepción de resolución': 'LineaTiempo.RecepcionResolucion',
    'Procesada con resolución': 'LineaTiempo.ProcesadaResolucion',
    'Mesa no instalada': 'LineaTiempo.MesaNoInstalada',
    'Acta extraviada': 'LineaTiempo.ActaExtraviada',
    'Resolución de ONPE': 'LineaTiempo.ResolucionONPE',
    'Acta siniestrada': 'LineaTiempo.ActaSiniestrada',
    'Resolución del JEE': 'LineaTiempo.ResolucionJEE',
  };

  constructor(private translate: TranslateService) {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  private checkScreenSize() {
    const isSmall = window.innerWidth < 960;
    this.esPantallaChica = isSmall;
    this.mostrarLineaTiempo = !isSmall;
  }

  toggleLineaTiempo() {
    if (this.esPantallaChica) {
      this.mostrarLineaTiempo = !this.mostrarLineaTiempo;
    }
  }

  obtenerRutaIcono(lineaTiempo: LineaTiempo): string {
    const ACTA_ICONS: Record<string, string> = {
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_DIGITALIZACION]: "assets/img/icons/estado-actas/ico_digitalizacion.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_DIGITACION]: "assets/img/icons/estado-actas/ico_digitacion.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_CONTABILIZADA]: "assets/img/icons/estado-actas/ico_acta_contabilizada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_OBSERVADA]: "assets/img/icons/estado-actas/ico_observada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_PARA_ENVIO_JEE]: "assets/img/icons/estado-actas/ico_jee.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_RECEPCION_RESOLUCION]: "assets/img/icons/estado-actas/ico_recepcion.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_PROCESADA_RESOLUCION]: "assets/img/icons/estado-actas/ico_acta_contabilizada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_MESA_NO_INSTALADA]: "assets/img/icons/estado-actas/ico_noencontrada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_EXTRAVIADA]: "assets/img/icons/estado-actas/ico_noencontrada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_RESOLUCIÓN_DE_ONPE]: "assets/img/icons/estado-actas/ico_resolucion.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_SINIESTRADA]: "assets/img/icons/estado-actas/ico_siniestrada.svg",
      [EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_RESOLUCION_DEL_JEE]: "assets/img/icons/estado-actas/ico_jee.svg",
    };

    return ACTA_ICONS[lineaTiempo.codigoEstadoActa ?? ""] ?? "";
  }

  private traducirTexto(texto: string): string {
    const textoLimpio = texto.trim();
    const claveTraduccion = this.TRADUCCIONES_ESTADOS[textoLimpio];
    
    if (claveTraduccion) {
      return this.translate.instant(claveTraduccion);
    }
    
    return texto;
  }

  obtenerDescripcion(lineaTiempo: LineaTiempo, reducir: boolean = false): string {
    let returnString = '';
    const limpiarDuplicados = (desc: string | null | undefined): string => {
      if (!desc) return '';
      const elementos = desc.split(',').map(d => this.traducirTexto(d.trim()));
      return [...new Set(elementos)].join(', ');
    };

    if (lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_OBSERVADA) {
      returnString = limpiarDuplicados(lineaTiempo.descripcionEstadoActaResolucion);
    } else {
      returnString = limpiarDuplicados(lineaTiempo.descripcionEstadoActa);
    }

    const shouldReduce = reducir || (this.esPantallaChica && lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_OBSERVADA);

    return shouldReduce ? this.getDescripcionReducida(returnString) : returnString;
  }

  getDescripcionReducida(desc: string | null | undefined): string {
    if (!desc) return '';
    if(this.esPantallaChica){
      return desc;
    }
    else{
    let maxLength = 17;
    return desc.length >= maxLength ? desc.substring(0, maxLength) + '...' : desc;
    }
  }

  limpiarDescripcionRepetida(descripcion: string | null | undefined): string {
    if (!descripcion) return '';

    const normalizar = (texto: string) =>
        texto
        .normalize('NFD')
        .replaceAll(/[\u0300-\u036f]/g, '')
        .trim()
        .toLowerCase();

    const elementosUnicos = new Map<string, string>();

    const elementos = descripcion.split(',');
    for (const original of elementos) {
      const textoTraducido = this.traducirTexto(original.trim());
      const clave = normalizar(textoTraducido);

      if (!elementosUnicos.has(clave)) {
        elementosUnicos.set(clave, textoTraducido);
      }
    }
    return Array.from(elementosUnicos.values()).join(', ');
  }

  isActaObservada(lineaTiempo: LineaTiempo): boolean {
    return lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_OBSERVADA;
  }

  isActaRoja(lineaTiempo: LineaTiempo): boolean {
    return lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_PARA_ENVIO_JEE ||
           lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_OBSERVADA ||
           lineaTiempo.codigoEstadoActa === EnumCodigoEstadoActaLineaTiempo.CODIGO_ESTADO_ACTA_RESOLUCION_DEL_JEE;
  }
}
