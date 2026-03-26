package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;

import java.util.List;

public interface MaeCandidatoRepository extends MongoRepository<MaeCandidato, Integer> {

    List<MaeCandidato> findByEleccion(MaeEleccion eleccion);
    List<MaeCandidato> findByEleccionAndDistritoElectoralAndAgrupacionPolitica(MaeEleccion eleccion, MaeDistritoElectoral distritoElectoral, MaeAgrupacionPolitica agrupacionPolitica);
    List<MaeCandidato> findByEleccionAndAgrupacionPolitica(MaeEleccion eleccion, MaeAgrupacionPolitica agrupacionPolitica);

}
