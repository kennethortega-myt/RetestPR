package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;

import java.util.List;

public interface MaeCandidatoRepository extends MongoRepository<MaeCandidato, Integer> {
    List<MaeCandidato> findByEleccion(MaeEleccion eleccion);
    List<MaeCandidato> findByEleccionAndDistritoElectoralAndAgrupacionPolitica(MaeEleccion eleccion, MaeDistritoElectoral distritoElectoral, MaeAgrupacionPolitica agrupacionPolitica);
    List<MaeCandidato> findByEleccionAndAgrupacionPolitica(MaeEleccion eleccion, MaeAgrupacionPolitica agrupacionPolitica);
}
