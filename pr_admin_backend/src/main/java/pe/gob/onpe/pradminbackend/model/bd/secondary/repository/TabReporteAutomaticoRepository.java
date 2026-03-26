package pe.gob.onpe.pradminbackend.model.bd.secondary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;

import java.util.List;
import java.util.Optional;

public interface TabReporteAutomaticoRepository extends MongoRepository<TabReporteAutomatico, String> {
    List<TabReporteAutomatico> findByEstado(Integer estado);
    Optional<TabReporteAutomatico> findById(String id);

}
