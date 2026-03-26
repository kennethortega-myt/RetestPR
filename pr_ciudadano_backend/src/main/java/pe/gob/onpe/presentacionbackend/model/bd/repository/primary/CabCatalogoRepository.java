package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.CabCatalogo;

import java.util.List;

public interface CabCatalogoRepository extends MongoRepository<CabCatalogo, Long> {

    List<CabCatalogo> findByMaestro(String maestro);

}
