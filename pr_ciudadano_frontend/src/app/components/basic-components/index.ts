import { CAlertComponent } from './c-alert/c-alert.component';
import { CCalendarComponent } from './c-calendar/c-calendar.component';
import { CInputComponent } from './c-input/c-input.component';
import { CSelectMvaluesComponent } from './c-select-mvalues/c-select-mvalues.component';
import { CSelectComponent } from './c-select/c-select.component';
import { CSidebarComponent } from './c-sidebar/c-sidebar.component';
import { CTableComponent } from './c-table/c-table.component';
import { CTextAreaComponent } from './c-text-area/c-text-area.component';

/**
 * This const is imported in ComponentsModule to be used in other modules when is necessary
 */
export const BASIC_COMPONENTS = [
  CAlertComponent,
  CCalendarComponent,
  CInputComponent,
  CSelectComponent,
  CSelectMvaluesComponent,
  CSidebarComponent,
  CTableComponent,
  CTextAreaComponent
];

export * from './c-alert/c-alert.component';
export * from './c-calendar/c-calendar.component';
export * from './c-input/c-input.component';
export * from './c-select-mvalues/c-select-mvalues.component';
export * from './c-select/c-select.component';
export * from './c-sidebar/c-sidebar.component';
export * from './c-table/c-table.component';
export * from './c-text-area/c-text-area.component';
