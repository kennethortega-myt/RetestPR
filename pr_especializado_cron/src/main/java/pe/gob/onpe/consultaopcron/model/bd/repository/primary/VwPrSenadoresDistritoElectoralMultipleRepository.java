package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;

public interface VwPrSenadoresDistritoElectoralMultipleRepository extends MongoRepository<VwPrSenadoresDistritoElectoralMultiple, Integer> {

	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
}
