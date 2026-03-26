import { ApplicationConfig, provideZoneChangeDetection, LOCALE_ID } from '@angular/core';
import {
  provideRouter,
  withInMemoryScrolling,
  withRouterConfig,
} from '@angular/router';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
  withJsonpSupport
} from '@angular/common/http';
import { MatPaginatorIntl, MAT_PAGINATOR_DEFAULT_OPTIONS } from '@angular/material/paginator';
import { provideNativeDateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { routes } from './app.routes';
import { HttpErrorInterceptorService } from './interceptors/http-error-interceptor.service';
import { HttpLoadingInterceptorService } from './interceptors/http-loading-interceptor.service';
import { TokenInterceptorService } from './interceptors/token-interceptor.service';
import { CustomPaginatorIntl } from './services/custom-paginator-intl.service';

// --- NUEVO PARA NGX-TRANSLATE ---

import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
registerLocaleData(localeEs);

const savedLang = localStorage.getItem('selectedLang') ?? 'es';


export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideNativeDateAdapter(),
    provideRouter(
      routes,
      // withDebugTracing(),
      withRouterConfig({
        onSameUrlNavigation: 'reload',
      }),
      withInMemoryScrolling({
        anchorScrolling: 'enabled',
        scrollPositionRestoration: 'enabled',
      })
    ),
    provideHttpClient(withJsonpSupport(), withInterceptorsFromDi()),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: './assets/i18n/',
        suffix: '.json'
      }),
      lang: savedLang,
      fallbackLang: 'es'
    }),

    // Configuración regional para Angular Material Datepicker
    { provide: LOCALE_ID, useValue: 'es-ES' },
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },


    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptorService,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpLoadingInterceptorService,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptorService,
      multi: true,
    },
    {
      provide: MatPaginatorIntl,
      useClass: CustomPaginatorIntl,
    },
    {
      provide: MAT_PAGINATOR_DEFAULT_OPTIONS,
      useValue: {
        pageSize: 10,
        showFirstLastButtons: true
      }
    },
  ],
};
