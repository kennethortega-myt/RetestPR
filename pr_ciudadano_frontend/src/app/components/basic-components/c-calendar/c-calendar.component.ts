import { Component, Input  } from '@angular/core';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatCalendarCellClassFunction} from '@angular/material/datepicker';

@Component({
  selector: 'app-c-calendar',
  providers: [provideNativeDateAdapter()],
  templateUrl: './c-calendar.component.html',
  standalone: false
})
export class CCalendarComponent {
  @Input() placeholder: string = '';
  @Input() label: string = '';
  dateClass: MatCalendarCellClassFunction<Date> = (cellDate, view) => {
    if (view === 'month') {
      const date = cellDate.getDate();

      return date === 1 || date === 20 ? 'example-custom-date-class' : '';
    }

    return '';
  };
}
