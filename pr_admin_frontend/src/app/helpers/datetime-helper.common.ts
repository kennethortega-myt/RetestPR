import { MatDateFormats } from '@angular/material/core';
import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';
import dayjs from 'dayjs';
import 'dayjs/locale/es';

// Utilidades de fecha/hora usando dayjs
// Formato legible: ejemplo "11-09-2025__15-22-45"
export function getCurrentDateTime(): string {
  return dayjs().format('DD-MM-YYYY__HH-mm-ss');
}

// Formatos personalizados de Angular Material (se usan por los pickers)
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
      return dayjs(date).format('DD/MM/YYYY');
    }
    return super.format(date, displayFormat);
  }

  // Formato consistente en español con hora en 24h
  private getFormattedCurrentDateTime(): string {
    return dayjs()
      .locale('es')
      .format('DD/MM/YYYY [a las] HH:mm:ss [h]');
  }

  generarMensajeActasObservadas(): string {
    return `De acuerdo a la actualización del ${this.getFormattedCurrentDateTime()}, no se cuenta con actas observadas en el criterio de búsqueda seleccionado.`;
  }

  generarMensajeConsultasAvanzadas(): string {
    return `De acuerdo a la actualización del ${this.getFormattedCurrentDateTime()}, no se cuenta con actas en el criterio de búsqueda seleccionado.`;
  }

  generarMensajeMisRepoprtes(): string {
    return `De acuerdo a la actualización del ${this.getFormattedCurrentDateTime()}, no se cuenta con reportes en el criterio de búsqueda seleccionado.`;
  }
}
