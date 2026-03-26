package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporte;

import java.util.Optional;

public interface TabArchivoReporteService {

	TabArchivo guardarArchivoReporte(TabReporte reporte, byte[] repote, String path, String formato);

	Optional<TabArchivo> getArchivoById(String idReporte);

}
