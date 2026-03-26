package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeUbigeo;

public interface MaeUbigeoRepository extends MongoRepository<MaeUbigeo, Long> {

}
