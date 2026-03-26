export interface ActaJeeBean {
  total: number;
  totalNormales: number;
  totalPendientes: number;
  totalObservadas: number;
  totalEnviadasJne: number;
  fechaRegistro: string;
  actas: Array<ActaBean>;
}

export interface ActaBean {
  actaId: number;
  mesaId: number;
  resolucionId: string;
  mesa: string;
  copia: string;
  eleccion: string;
  estadoActa: string;
  estadoMesa: string;
  estadoDigitacion: string;
  ubigeo: string;
  localVotacion: string;
  descripcionEstadoActa: string;
  descripcionEstadoMesa: string;
  electoresHabiles: number;
  cvas: string;
  fecha: string;
  imagenInstalacion: string;
  horaEscrutinio: string;
  horaInstalacion: string;
  imagenEscrutinio: string;
  actaSinDatos: string;
  solNulidad: string;
  actaSinFirma: string;
  actasIncompletas: string;
  observacionesJNE: string;
  agrupacionesPoliticas: AgrupolBean[];
}

export interface AgrupolBean {
  idAgrupol: number;
  codiAgrupol: string;
  idDetActa: string;
  nombreAgrupacionPolitica: string;
  votos: string;
  posicion: number;
  errorMaterial: string;
  ilegible: string;
  activo: number;
}

export interface AplicarActaBean {
  idResolucion: string;
  mesa: string;
  copia: string;
  siguiente: boolean;
}
