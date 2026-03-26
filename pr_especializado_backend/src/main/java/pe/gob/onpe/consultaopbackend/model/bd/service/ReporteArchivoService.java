package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRequest;

public interface ReporteArchivoService {


    void generarReporte(ReporteRequest request, TabReporte reporte);

    void generarReporteCsv(ReporteRequest request, TabReporte reporte);

    void generarReporteObservados(ReporteRequest request, TabReporte reporte);
    void generarReporteObservadosCsv(ReporteRequest request, TabReporte reporte);

}
