import { DatePipe } from '@angular/common';

// Formats an ISO or date string into dd/MM/yyyy H:mm:ss (returns empty string for empty input)
export function formatDateTime(fecha: string): string | null {
  if (fecha == '') {
    return '';
  }
  return new DatePipe('en-US').transform(fecha, 'dd/MM/yyyy H:mm:ss');
}

