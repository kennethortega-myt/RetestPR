package pe.gob.onpe.presentacionbackend.model.bd.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.documents.secondary.TabCronReporteActas;

public interface TabReporteActasRepository extends MongoRepository<TabCronReporteActas, String> {

}
