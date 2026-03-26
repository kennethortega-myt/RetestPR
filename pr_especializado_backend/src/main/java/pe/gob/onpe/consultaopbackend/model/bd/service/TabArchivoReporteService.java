package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;

import java.util.Optional;

public interface TabArchivoReporteService {

	TabArchivo guardarArchivoReporte(TabReporte reporte, byte[] archivoReporte, String path, String formato);
	
	Optional<TabArchivo> getArchivoById(String idReporte);

	Optional<TabReporteCandidato> findByIdAndActivo(Integer codigoTipoEleccion, Integer activo);
}
