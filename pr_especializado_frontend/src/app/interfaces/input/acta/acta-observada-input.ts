export class ActaObservadaInput {
  codigoLocalVotacion?: number;
  idAmbitoGeografico?: number;
  idEleccion?: number;
  idUbigeo?: string;
  resueltas?: boolean;
  ubigeoNivel01?: string;
  ubigeoNivel02?: string;
  idDistritoElectoral?: number | null;
  esEleccionParaDistritoElectoral?: boolean;
  descripcionActaResolucion?: string;
}
