export class FiltroModel {
  idUbigeoNivel01?: string;
  idUbigeoNivel02?: string;
  idUbigeoNivel03?: string;
  idLocalVotacion?: number;
  idTipoEleccion?: number;
  idAmbitoGeografico?: number;
  resueltas?: boolean | null;
  idDistritoElectoral?: number | null;
  esEleccionParaDistritoElectoral?: boolean;

  nombreTipoEleccion: string = '';
  nombreAmbitoGeografico?: string;
  nombreDistritoElectoral?: string;
  nombreUbigeoNivel01?: string;
  nombreUbigeoNivel02?: string;
  nombreUbigeoNivel03?: string;
  nombreLocalVotacion?: string;
}
