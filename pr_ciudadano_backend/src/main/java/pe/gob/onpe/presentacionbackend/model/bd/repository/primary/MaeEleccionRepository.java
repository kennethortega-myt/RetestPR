package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeProcesoElectoral;

import java.util.Optional;

public interface MaeEleccionRepository extends MongoRepository<MaeEleccion, Long> {

    Optional<MaeEleccion> findByProcesoElectoralAndPrincipalAndActivo(MaeProcesoElectoral procesoElectoral,Integer principal,Integer activo);
}
