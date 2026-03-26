package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeFecha;

public interface MaeFechaRepository extends MongoRepository<MaeFecha, Integer> {

}
