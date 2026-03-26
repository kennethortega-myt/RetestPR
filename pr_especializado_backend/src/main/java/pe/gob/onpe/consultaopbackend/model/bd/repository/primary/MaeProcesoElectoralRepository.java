package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeProcesoElectoral;

import java.util.Optional;

public interface MaeProcesoElectoralRepository extends MongoRepository<MaeProcesoElectoral, Long> {

    MaeProcesoElectoral findBynActivo(Long activo);

    Optional<MaeProcesoElectoral> findById(Long id);


}
