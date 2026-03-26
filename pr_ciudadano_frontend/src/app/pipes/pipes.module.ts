import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PorcentajeFormatPipe } from './porcentaje-format.pipe';
import { HourFormatPipe } from './hour-format.pipe';

@NgModule({
  declarations: [PorcentajeFormatPipe, HourFormatPipe],
  imports: [CommonModule],
  exports: [PorcentajeFormatPipe, HourFormatPipe]
})
export class PipesModule {}
