export interface ActaPorAmbitoDetalleRequest {
    idAmbitoGeografico: number;
    idUbigeo: number;
    codigoLocalVotacion: number;
    resultas: boolean;
    descripcionActaResolucion?: string;
}