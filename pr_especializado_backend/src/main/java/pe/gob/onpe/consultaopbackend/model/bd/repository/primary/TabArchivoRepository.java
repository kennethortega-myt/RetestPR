package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabArchivo;

import java.util.List;


public interface TabArchivoRepository extends MongoRepository<TabArchivo, String> {
	
	List<TabArchivo> findByIdActa(Long idActa);
	List<TabArchivo> findByIdActaAndTipo(Long idActa, Integer tipo);

	@Query("{ 'idActa': { $in: ?0 } }")
    List<TabArchivo> findAllByIdActa(List<Long> idActaList);

    List<TabArchivo> findBycGuid(String cGuid);

}
