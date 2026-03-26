import { MatDateFormats, NativeDateAdapter } from '@angular/material/core';
import { Injectable } from '@angular/core';
import dayjs from 'dayjs';
import 'dayjs/locale/es';

// ✅ Función utilitaria para timestamp legible (ejemplo: "11-9-2025__15-22-45")
export function getCurrentDateTime(): string {
  const date = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');

  return `${pad(date.getDate())}-${pad(date.getMonth() + 1)}-${date.getFullYear()}__` +
    `${pad(date.getHours())}-${pad(date.getMinutes())}-${pad(date.getSeconds())}`;
}

// ✅ Formatos personalizados de Angular Material
export const MY_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DD/MM/YYYY',
    dateA11yLabel: 'DD/MM/YYYY',
    monthYearA11yLabel: 'DD/MM/YYYY',
  },
};

@Injectable()
export class CustomDateAdapter extends NativeDateAdapter {
  override format(date: Date, displayFormat: Object): string {
    if (displayFormat === 'input') {
      const pad = (n: number) => String(n).padStart(2, '0');
      return `${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
    }
    return super.format(date, displayFormat);
  }

  // ✅ Función privada común para generar fecha/hora legible
  private getFormattedCurrentDateTime(): string {
    return dayjs()
      .locale('es')
      .format('DD/MM/YYYY [a las] HH:mm:ss');
  }

  getFormattedDateTimeObject(): { date: string; hour: string } {
    const formatted = dayjs().locale('es');
    return {
      date: formatted.format('DD/MM/YYYY'),
      hour: formatted.format('HH:mm:ss')
    };
  }

  generarMensajeActasObservadas(): string {
    return 'constantes.MENSAJE_ACTAS_OBSERVADAS_SIN_RESULTADOS';
  }

  generarMensajeConsultasAvanzadas(): string {
    return 'constantes.MENSAJE_NO_DATA_CONSULTA';
  }

  generarMensajeMisReportes(): string {
    return 'constantes.MENSAJE_NO_DATA_REPORTES';
  }
}
