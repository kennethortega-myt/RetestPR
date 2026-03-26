import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { FiltroModel } from '../interfaces/filtro.model';

@Injectable({
  providedIn: 'root',
})
export class ResultadoBehaviorService {
  filtrar = new BehaviorSubject({} as FiltroModel);

  filtrarObservable$ = this.filtrar.asObservable();

  ejecutarFiltrar(data: FiltroModel) {
    this.filtrar.next(data);
  }
}
