import { NgModule } from '@angular/core';
import { SoloAlfabeticosDirective } from './directive/solo-alfabeticos.directive';
import { CommonModule } from '@angular/common';
import { NoEspacioInicioDirective } from './directive/no-espacio-inicio.directive';
import { SoloNumerosDirective } from './directive/solo-numeros.directive';
import { UppercaseDirective } from './directive/uppercase.directive';
import { LazyImageComponent } from './lazy-image/lazy-image.component';
import { MinLengthDirective } from './directive/min-length.directive';
import { NumerosHashDirective } from './directive/numeros-hash.directive';
import { TextoValidoDirective } from './directive/texto-valido.directive';
import { AlertsComponent } from './alerts/alerts.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { DialogConfirmComponent } from './dialog/dialog-confirm/dialog-confirm.component';
import { DialogComponent } from './dialog/dialog/dialog.component';
import { HeaderComponent } from './header/header.component';
import { ValidaPasswordDirective } from './directive/valida-password.directive';
import { PersonajeMensajeComponent } from './personaje-mensaje/personaje-mensaje.component';

@NgModule({
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatMenuModule, MatIconModule],
  declarations: [
    PersonajeMensajeComponent,
    SoloAlfabeticosDirective,
    NoEspacioInicioDirective,
    SoloNumerosDirective,
    UppercaseDirective,
    LazyImageComponent,
    MinLengthDirective,
    NumerosHashDirective,
    TextoValidoDirective,
    AlertsComponent,
    DialogConfirmComponent,
    DialogComponent,
    HeaderComponent,
    ValidaPasswordDirective,
  ],
  exports: [
    PersonajeMensajeComponent,
    SoloAlfabeticosDirective,
    NoEspacioInicioDirective,
    SoloNumerosDirective,
    UppercaseDirective,
    LazyImageComponent,
    MinLengthDirective,
    NumerosHashDirective,
    TextoValidoDirective,
    HeaderComponent,
    ValidaPasswordDirective,
  ],
  providers: [],
})
export class ComponentsModule {}
