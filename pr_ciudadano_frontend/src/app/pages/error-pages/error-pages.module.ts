import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../components/components.module';
import { Error404Component } from './error-404/error-404.component';
import { Error408Component } from './error-408/error-408.component';
import { Error500Component } from './error-500/error-500.component';
import { Error503Component } from './error-503/error-503.component';
import { ErrorGenericComponent } from './error-generic/error-generic.component';

const COMPONENTS = [Error404Component, Error408Component, Error500Component, Error503Component, ErrorGenericComponent];

@NgModule({
  declarations: [...COMPONENTS],
  imports: [CommonModule, RouterModule, TranslateModule, ComponentsModule],
  exports: [...COMPONENTS]
})
export class ErrorPagesModule {}
