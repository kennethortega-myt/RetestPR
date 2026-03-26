package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeProcesoElectoral;

import java.util.Optional;


public interface MaeProcesoElectoralRepository extends MongoRepository<MaeProcesoElectoral, Long> {

    MaeProcesoElectoral findByActivo(Integer activo);
    
    Optional<MaeProcesoElectoral> findById(Long id);
    
   
}
