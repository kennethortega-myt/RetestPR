package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.DetCatalogoEstructura;

import java.util.List;

public interface DetCatalogoEstructuraRepository extends MongoRepository<DetCatalogoEstructura, Long> {
    List<DetCatalogoEstructura> findByColumna(String cColumna);
}
