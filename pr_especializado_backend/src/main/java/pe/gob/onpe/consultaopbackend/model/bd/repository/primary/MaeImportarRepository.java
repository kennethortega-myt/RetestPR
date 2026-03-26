package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeImportar;

public interface MaeImportarRepository extends MongoRepository<MaeImportar, Long> {

}
