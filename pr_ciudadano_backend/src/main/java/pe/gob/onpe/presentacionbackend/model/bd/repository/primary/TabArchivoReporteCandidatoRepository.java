package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabReporteCandidato;

import java.util.Optional;

public interface TabArchivoReporteCandidatoRepository extends MongoRepository<TabReporteCandidato, Integer> {
    Optional<TabReporteCandidato> findByIdAndActivo(Integer id, Integer activo);
}

