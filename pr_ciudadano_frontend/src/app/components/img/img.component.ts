import { Component, Input } from '@angular/core';
import { IRevocatoriaAutoridadItem } from '../../interfaces/revocatoria.interfaces';
import { ActaPorAmbitoDetalleDetalleResponse } from '../../interfaces/response/acta-por-ambito-response.interface';

@Component({
  selector: 'app-img',
  templateUrl: './img.component.html',
  styles: [],
  standalone: false,
})
export class ImgComponent {
  @Input() hasWrapper = true;
  @Input() class = 'img';
  @Input() alt: string;
  @Input() src: string;
  @Input() item: IRevocatoriaAutoridadItem | ActaPorAmbitoDetalleDetalleResponse;

  public onErrorImg(event: Event) {
    switch (this.item?.sexo) {
      case 1:
        this.src = 'assets/img/candidatos/avatar2_v2.jpg';
        break;
      case 2:
        this.src = 'assets/img/candidatos/avatar1_v2.jpg';
        break;
      default:
        this.src = 'assets/img/candidatos/avatar.jpg';
    }
  }
}
