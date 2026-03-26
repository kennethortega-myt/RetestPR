package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrPresidenciales;

import java.util.List;
import java.util.Optional;

public interface VwPrPresidencialesRepository extends MongoRepository<VwPrPresidenciales, Integer> {

    Optional<VwPrPresidenciales> findById(Integer idVista);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);

}
