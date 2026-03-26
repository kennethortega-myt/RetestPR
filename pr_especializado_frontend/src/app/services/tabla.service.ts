import { Injectable } from '@angular/core';

import { AmbitoGeografico } from '../interfaces/output/ambito-geografico.model';
import { Eleccion } from '../interfaces/output/eleccion.model';
import { Ubigeo } from '../interfaces/output/ubigeo.model';

@Injectable({
  providedIn: 'root',
})
export class TablaService {
  ubigeoNivelUnoSeleccionado: Ubigeo | null = null;
  ubigeoNivelDosSeleccionado: Ubigeo | null = null;
  ubigeoNivelTresSeleccionado: Ubigeo | null = null;
  localVotacionSeleccionado: Ubigeo | null = null;
  eleccionSeleccionado: Eleccion | null = null;
  ambitoGeograficoSeleccionado: AmbitoGeografico | null = null;
  distritoElectoral: Ubigeo | null = null;
  constructor() {}
}
