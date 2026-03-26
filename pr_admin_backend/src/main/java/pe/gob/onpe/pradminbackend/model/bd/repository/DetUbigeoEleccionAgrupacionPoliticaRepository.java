package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;

import java.util.List;

public interface DetUbigeoEleccionAgrupacionPoliticaRepository extends MongoRepository<DetUbigeoEleccionAgrupacionPolitica, Long> {
    List<DetUbigeoEleccionAgrupacionPolitica> findByUbigeoEleccionEleccion(MaeEleccion eleccion);
}
