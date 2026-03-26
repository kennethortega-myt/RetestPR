import { Injectable } from '@angular/core';

import { BaseAdapter } from './base.adapter';
import { TipoEleccion } from '../output/tipo-eleccion.model';

@Injectable({
  providedIn: 'root',
})
export class TipoEleccionAdapter implements BaseAdapter<TipoEleccion> {
  constructor() {}

  adapt(item: any): TipoEleccion | null {
    if (!item) return null;

    let retorno = new TipoEleccion();
    retorno.value = item.idEleccion;
    retorno.text = item.nombre;
    return retorno;
  }
}
