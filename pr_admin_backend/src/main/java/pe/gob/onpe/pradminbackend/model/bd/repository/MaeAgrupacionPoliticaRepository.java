package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeAgrupacionPolitica;

public interface MaeAgrupacionPoliticaRepository extends MongoRepository<MaeAgrupacionPolitica, Long> {

}
