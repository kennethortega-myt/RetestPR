package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.TabArchivo;

import java.util.Optional;

public interface TabArchivoService {

	Optional<TabArchivo> getArchivoById(String idActa);
	
}
