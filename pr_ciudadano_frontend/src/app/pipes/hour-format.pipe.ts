import { Pipe, PipeTransform } from '@angular/core';
import dayjs from 'dayjs';

@Pipe({
  name: 'hourFormat',
  standalone: false
})
export class HourFormatPipe implements PipeTransform {
    transform(value: Date | string | number): string {
        if (!value) {
        return '';
        }
        
        return dayjs(value)
        .format('hh:mm:ss A')
        .replace('AM', 'a. m.')
        .replace('PM', 'p. m.');
    }
}
