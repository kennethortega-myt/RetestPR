export interface ReporteAutomaticoPaginado {
    data: ReporteEstructura
}

export interface ReporteEstructura {
    paginaActual: number;
    totalRegistros: number;
    totalPaginas: number;
    nombreTipoEleccion: string;
    iconoTipoEleccion: string;
    content: ActaDetalle[];
}

export interface ActaDetalle {
    fechaConsulta: number;
    estadoDescripcion: string;
    porcentaje: number;
    idArchivo: string;
    numeroRegistro: number;
}