package pe.gob.onpe.presentacionbackend.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabArchivo;

import java.util.List;

public interface TabArchivoReporteRepository extends MongoRepository<TabArchivo, String> {

    List<TabArchivo> findByIdIn(List<String> idsArchivos );
    	
}
