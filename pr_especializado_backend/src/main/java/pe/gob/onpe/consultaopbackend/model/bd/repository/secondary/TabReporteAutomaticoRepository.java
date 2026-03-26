package pe.gob.onpe.consultaopbackend.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;

public interface TabReporteAutomaticoRepository extends MongoRepository<TabReporteAutomatico, String> {

}
