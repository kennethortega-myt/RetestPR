package pe.gob.onpe.pradminbackend.model.bd.secondary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporte;

public interface TabReporteRepository extends MongoRepository<TabReporte, String> {


}
