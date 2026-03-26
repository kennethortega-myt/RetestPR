import { Injectable } from '@angular/core';
import { BaseAdapter } from './base.adapter';
import { LocalVotacion } from '../output/local-votacion.model';

@Injectable({
  providedIn: 'root',
})
export class LocalVotacionAdapter implements BaseAdapter<LocalVotacion> {
  constructor() {}

  adapt(item: any): LocalVotacion | null {
    if (!item) return null;

    let retorno = new LocalVotacion();
    retorno.value = item.codigoLocalVotacion;
    retorno.text = item.nombreLocalVotacion;
    return retorno;
  }
}
