import { Injectable } from '@angular/core';

import { Ubigeo } from '../output/ubigeo.model';
import { BaseAdapter } from './base.adapter';

@Injectable({
  providedIn: 'root',
})
export class UbigeoAdapter implements BaseAdapter<Ubigeo> {
  adapt(item: any): Ubigeo | null {
    if (!item) return null;

    return {
      value: item.ubigeo ?? item.codigo,
      text: item.nombre,
    } as Ubigeo;
  }
}
