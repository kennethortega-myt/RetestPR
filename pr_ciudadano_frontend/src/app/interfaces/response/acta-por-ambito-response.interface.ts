export interface ActaPorAmbitoDetalleResponse {
    id: number;
    idMesa: number;
    codigoMesa: string;
    descripcionMesa: string;
    idEleccion: number;
    ubigeoNivel01: string;
    ubigeoNivel02: string;
    ubigeoNivel03: string;
    centroPoblado: string;
    nombreLocalVotacion: string;
    totalElectoresHabiles: number;
    totalVotosEmitidos: number;
    totalVotosValidos: number;
    totalAsistentes: number;
    porcentajeParticipacionCiudadana: number;
    estadoActa: string;
    estadoComputo: string;
    codigoEstadoActa: string;
    descripcionEstadoActa: string;
    estadoActaResolucion: string;
    estadoDescripcionActaResolucion: string;
    descripcionSubEstadoActa: string;
    detalle?:[ActaPorAmbitoDetalleDetalleResponse];    
    archivos?:[ActaPorAmbitoDetalleArchivoResponse]
    lineaTiempo?: [ActaPorAmbitoDetalleLineaTiempoResponse]
}

export interface ActaPorAmbitoDetalleDetalleResponse {
    descripcion: string;
    estado: number;
    grafico: number;
    totalCandidatos: number;
    candidato: [ActaPorAmbitoDetalleDetalleCandidatoResponse];
    nvotos: number;
    ccodigo: string;
    nporcentajeVotosValidos: number;
    nporcentajeVotosEmitidos: number;
    nagrupacionPolitica: number;
    nposicion: number;
    cargo?:string;
    sexo?:number;
}

export interface ActaPorAmbitoDetalleDetalleCandidatoResponse {
    apellidoPaterno: string;
    apellidoMaterno: string;
    nombres: string;
    cdocumentoIdentidad: string;
    ccargo: string;
    votos:number;
}

export interface ActaPorAmbitoDetalleArchivoResponse {
    id: string;
    tipo: number;
    nombre: string;
    descripcion: string;
    daudFechaCreacion: string;
}
export interface ActaPorAmbitoDetalleLineaTiempoResponse {
    codigoEstadoActa:string;
    descripcionEstadoActa:string;
    descripcionEstadoActaResolucion:string;
    fechaRegistro:string;
}