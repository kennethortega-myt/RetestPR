package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ReporteRequest;

public interface ReporteArchivoService {


    void generarReporte(ReporteRequest request, TabReporte reporte);

    void generarReporteCsv(ReporteRequest request, TabReporte reporte);

    void generarReporteObservados(ReporteRequest request, TabReporte reporte);
    void generarReporteObservadosCsv(ReporteRequest request, TabReporte reporte);

}
