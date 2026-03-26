package pe.gob.onpe.consultaopcron.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteAutomatico;

import java.util.List;


public interface TabProgramacionReporteRepository extends MongoRepository<TabReporteAutomatico, String> {
	List<TabReporteAutomatico> findByEstado(Integer estado);

}
