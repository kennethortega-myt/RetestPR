export interface DetalleReporteAutomaticoPorTipoEleccion {
    nombreTipoEleccion: string;
    codigoTipoEleccion: string;
    iconoTipoEleccion: string;
    totalesPorTipoEleccion: number;
    reporteDescarga: string;
    actas: DetalleActa[];
}

export interface DetalleActa {
    idActa: string;
    fechaActa: number;
    porcentaje: number;
    rutaActa: string;
    estadoActa: number;
}