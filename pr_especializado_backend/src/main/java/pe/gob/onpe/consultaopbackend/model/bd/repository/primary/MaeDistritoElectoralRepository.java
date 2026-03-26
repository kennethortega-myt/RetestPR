package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeDistritoElectoral;

public interface MaeDistritoElectoralRepository extends MongoRepository<MaeDistritoElectoral, Integer> {

}
