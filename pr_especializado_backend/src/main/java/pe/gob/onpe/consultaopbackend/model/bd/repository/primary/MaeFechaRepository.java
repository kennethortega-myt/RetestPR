package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeFecha;

public interface MaeFechaRepository extends MongoRepository<MaeFecha, Integer> {

}
