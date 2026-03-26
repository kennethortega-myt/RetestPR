import { DatePipe } from '@angular/common';
import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'onpeDate' })
export class OnpeDatePipe implements PipeTransform {
  private readonly datePipe: DatePipe;

  constructor(@Inject(LOCALE_ID) private locale: string) {
    this.datePipe = new DatePipe(this.locale);
  }

  transform(value: Date | string | number | null | undefined, format: string = 'dd/MM/yyyy hh:mm a'): string | null {
    if (value == null) {
      return null;
    }

    const formattedDate = this.datePipe.transform(value, format);

    if (formattedDate) {
      return formattedDate.replace(/\bam\b/gi, 'a.m.').replace(/\bpm\b/gi, 'p.m.');
    }

    return null;
  }
}
