package pe.gob.onpe.presentacionbackend.model.bd.service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabArchivo;

import java.util.Optional;

public interface TabArchivoReporteService {
	Optional<TabArchivo> getArchivoById(String idReporte);
	Optional<TabReporteCandidato> findByIdAndActivo(Integer codigoTipoEleccion, Integer activo);
}