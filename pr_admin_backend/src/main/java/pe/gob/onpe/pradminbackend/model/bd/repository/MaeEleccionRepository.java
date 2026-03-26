package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;

import java.util.Optional;

public interface MaeEleccionRepository extends MongoRepository<MaeEleccion, Long> {
    Optional<MaeEleccion> findByCodigo(String codigo);
}
