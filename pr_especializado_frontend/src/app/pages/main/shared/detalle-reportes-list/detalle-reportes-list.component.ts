import { DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../../components/components.module';
import { IReportConfigFront } from './detalle-reportes-list.interfaces';

@Component({
  selector: 'app-detalle-reportes-list',
  templateUrl: './detalle-reportes-list.component.html',
  styleUrl: './detalle-reportes-list.component.scss',
  standalone: true,
  imports: [TranslateModule, DatePipe, ComponentsModule],
})
export class DetalleReportesListComponent {
  @Input({ required: true }) titleKey!: string;
  @Input({ required: true }) reportConfigList: IReportConfigFront[] = [];

  @Output() generar = new EventEmitter<IReportConfigFront>();
  @Output() detenerReanudar = new EventEmitter<IReportConfigFront>();
  @Output() editar = new EventEmitter<IReportConfigFront>();

  onGenerar(item: IReportConfigFront): void {
    this.generar.emit(item);
  }

  onDetenerReanudar(item: IReportConfigFront): void {
    this.detenerReanudar.emit(item);
  }

  onEditar(item: IReportConfigFront): void {
    this.editar.emit(item);
  }
}
