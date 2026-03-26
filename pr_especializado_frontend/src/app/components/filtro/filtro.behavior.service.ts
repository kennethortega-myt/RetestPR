import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { EnumIdAmbito } from '../../helpers/enums';

@Injectable({
  providedIn: 'root',
})
export class FiltroBehaviorService {
  seleccionarUbigeo = new BehaviorSubject('');
  seleccionarUbigeoObservable$ = this.seleccionarUbigeo.asObservable();

  seleccionarDistritoElectoral = new BehaviorSubject('');
  seleccionarDistritoElectoralObservable$ = this.seleccionarDistritoElectoral.asObservable();

  seleccionarIdAmbitoGeografico = new BehaviorSubject(EnumIdAmbito.NACIONAL);
  seleccionarIdAmbitoGeograficoObservable$ =
    this.seleccionarIdAmbitoGeografico.asObservable();

  actualizarSeleccionUbigeo(ubigeo: string) {
    this.seleccionarUbigeo.next(ubigeo);
  }

  actualizarSeleccionDistritoElectoral(idDistritoElectoral: string) {
    this.seleccionarDistritoElectoral.next(idDistritoElectoral);
  }

  cambiarIdAmbitoGeografico(idAmbitoGeografico: number) {
    this.seleccionarIdAmbitoGeografico.next(idAmbitoGeografico);
  }
}
