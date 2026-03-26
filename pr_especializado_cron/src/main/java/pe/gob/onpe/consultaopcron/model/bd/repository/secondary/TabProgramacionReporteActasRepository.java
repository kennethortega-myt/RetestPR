package pe.gob.onpe.consultaopcron.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteActa;

import java.util.List;


public interface TabProgramacionReporteActasRepository extends MongoRepository<TabReporteActa, String> {
	List<TabReporteActa> findByEstado(Integer estado);

}
