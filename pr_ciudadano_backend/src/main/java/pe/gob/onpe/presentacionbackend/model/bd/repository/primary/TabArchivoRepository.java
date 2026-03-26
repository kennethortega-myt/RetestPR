package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.TabArchivo;

import java.util.List;


public interface TabArchivoRepository extends MongoRepository<TabArchivo, String> {
	
	List<TabArchivo> findByIdActa(Long idActa);

}
