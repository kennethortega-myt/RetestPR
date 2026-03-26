package pe.gob.onpe.pradminbackend.model.bd.secondary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo;

import java.util.List;

public interface TabArchivoReporteRepository extends MongoRepository<TabArchivo, String> {


    List<TabArchivo> findByIdIn(List<String> idsArchivos );
	
}
