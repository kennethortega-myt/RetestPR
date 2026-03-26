package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaePuestaCero;

public interface MaePuestaCeroRepository extends MongoRepository<MaePuestaCero, String> {

}
