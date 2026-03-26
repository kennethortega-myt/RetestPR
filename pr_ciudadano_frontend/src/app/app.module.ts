import { LayoutModule } from '@angular/cdk/layout';
import { HttpClient, HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { Injector, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { ComponentsModule } from './components/components.module';
import { GlobalKeyboardShortcutsDirective } from './directives/global-keyboard-shortcuts.directive';
import { AppInjector } from './helpers/app-injector';
import { headerInterceptor } from './interceptors/header-interceptor';
import { MainComponent } from './main/main.component';
import { ErrorPagesModule } from './pages/error-pages/error-pages.module';
import { PROCESOS_ELECTORALES_MODULES } from './pages/procesos-electorales';
import { RoutingPageComponent } from './pages/routing-page/routing-page.component';
import { BaseUbigeoService } from './services/common/base-ubigeo.service';
import { RequestsService } from './services/common/requests.service';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

const MATERIAL_MODULES = [
  MatButtonModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatCheckboxModule,
  MatDialogModule,
  MatSnackBarModule,
  MatTooltipModule,
  MatMenuModule,
  MatAutocompleteModule,
  MatIconModule,
  MatProgressSpinnerModule,
  MatTabsModule,
  MatTableModule,
  MatPaginatorModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatBottomSheetModule
];

@NgModule({
  declarations: [AppComponent, MainComponent, RoutingPageComponent, GlobalKeyboardShortcutsDirective],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    ReactiveFormsModule,
    FormsModule,
    LayoutModule,
    NgxMaskDirective,
    NgxMaskPipe,
    TranslateModule.forRoot({
      defaultLanguage: localStorage.getItem('selectedLang') ?? 'es',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    ComponentsModule,
    ErrorPagesModule,
    ...PROCESOS_ELECTORALES_MODULES,
    ...MATERIAL_MODULES
  ],
  providers: [
    provideHttpClient(withInterceptors([headerInterceptor])),
    // getProcesoElectoralAndAllElectionsGuard,
    // getTipoDeProcesoElectoralGuard,
    RequestsService,
    BaseUbigeoService,
    provideNgxMask()
    // { provide: HTTP_INTERCEPTORS, useClass: redirectionForErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(injector: Injector) {
    // Inicializa el inyector global para helpers no inyectables
    AppInjector.setInjector(injector);
  }
}
