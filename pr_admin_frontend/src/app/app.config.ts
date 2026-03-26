import { ApplicationConfig, LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { provideToastr } from 'ngx-toastr';
import { newTokenInterceptor } from './interceptors/new-token.interceptor';
import { loaderInterceptor } from './interceptors/loader.interceptor';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import localeEs from '@angular/common/locales/es';
import { registerLocaleData } from '@angular/common';
import { provideNgIdle } from '@ng-idle/core';

registerLocaleData(localeEs);

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: LOCALE_ID, useValue: 'es-ES' },
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes
      // withDebugTracing()
    ),
    provideNgIdle(),
    provideHttpClient(
      withInterceptorsFromDi(),
      withInterceptors([newTokenInterceptor, loaderInterceptor])
    ),
    provideToastr(), // Toastr providers
  ],
};