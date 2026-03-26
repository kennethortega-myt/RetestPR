package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;

import java.util.List;
import java.util.Optional;

public interface VwPrSenadoresDistritoNacionalUnicoRepository extends MongoRepository<VwPrSenadoresDistritoNacionalUnico, Integer> {

    Optional<VwPrSenadoresDistritoNacionalUnico> findById(Integer idVista);

    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);

}
