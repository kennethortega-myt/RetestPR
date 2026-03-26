export interface IResumenGeneralResponse {
    id:number;
    nombre:number;
    totalElectoresHabiles:number;
    participacionCiudadana:number;
    porcentajeParticipacionCiudadana:number;
    totalActas:number;
    actasContabilizadas:number;
    porcentajeActasContabilizadas:number;
    actasObservadasEnviadas:number;
    porcentajeActasObservadasEnviadas:number;
    actasPendientes:number;
    porcentajeActasPendientes:number;
}

export interface ActaResumenGeneralTotalesResponse {
    idEleccion?: number;
    totalElectoresHabiles?: number;
    participacionCiudadanaTotal?: number;
    actasContabilizadas?: number;
    contabilizadas?: number;
    totalActas?: number;
    participacionCiudadana?: number;
    actasEnviadasJee?: number;
    enviadasJee?: number;
    actasPendientesJee?: number;
    pendientesJee?: number;
    fechaActualizacion?: string;
    idUbigeoDepartamento?: number;
    idUbigeoProvincia?: number;
    idUbigeoDistrito?: number;
    idUbigeoDistritoElectoral?: number;
    totalVotosEmitidos?: number;
    totalVotosValidos?: number;
    porcentajeVotosEmitidos?: number;
    porcentajeVotosValidos?: number;
}