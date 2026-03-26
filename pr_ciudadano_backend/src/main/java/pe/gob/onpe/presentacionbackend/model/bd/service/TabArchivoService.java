package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.TabArchivo;

public interface TabArchivoService {

	Optional<TabArchivo> getArchivoById(String idActa);

	
}
