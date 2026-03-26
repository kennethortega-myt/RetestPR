package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;

public interface MaeEleccionRepository extends MongoRepository<MaeEleccion, Long> {

}
