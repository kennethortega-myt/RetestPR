import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import dayjs from 'dayjs';
import { FiltroModel } from '../../interfaces/filtro.model';
import { TranslateModule } from '@ngx-translate/core';
import { OnpeDatePipe } from '../../pipes/onpe-date.pipe';

@Component({
  selector: 'app-filtro-info',
  standalone: true,
  imports: [CommonModule, TranslateModule, OnpeDatePipe],
  templateUrl: './filtro-info.component.html',
  styleUrls: ['./filtro-info.component.scss'],
})
export class FiltroInfoComponent implements OnInit {
  @Input() filtroData?: FiltroModel;
  currentTimestamp!: Date;

  ngOnInit(): void {
    this.currentTimestamp = dayjs().toDate();
  }


  getFilterText(): string {
    if (!this.filtroData) return '';

    const parts: string[] = [];
    if (this.filtroData.idTipoEleccion) {
      parts.push(this.filtroData.nombreTipoEleccion);
    }

    if (this.filtroData.nombreUbigeoNivel01) {
      parts.push(this.filtroData.nombreUbigeoNivel01);
    }

    if (this.filtroData.nombreUbigeoNivel02) {
      parts.push(this.filtroData.nombreUbigeoNivel02);
    }

    if (this.filtroData.nombreUbigeoNivel03) {
      parts.push(this.filtroData.nombreUbigeoNivel03);
    }

    return parts.join(' / ');
  }
}