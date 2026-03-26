import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'porcentajeFormat',
})
export class PorcentajeFormatPipe implements PipeTransform {
  transform(value: number): string {
    if (value == null) return '0%';
    if (value == 0 || value == 100) return value + '%';
    return value.toFixed(3) + '%';
  }
}
