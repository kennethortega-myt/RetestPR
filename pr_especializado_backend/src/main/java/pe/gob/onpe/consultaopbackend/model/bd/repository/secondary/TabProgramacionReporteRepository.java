package pe.gob.onpe.consultaopbackend.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;

import java.util.List;
import java.util.Optional;


public interface TabProgramacionReporteRepository extends MongoRepository<TabReporteAutomatico, String> {
	List<TabReporteAutomatico> findByEstado(Integer estado);
	Optional<TabReporteAutomatico> findById(String id);

}
