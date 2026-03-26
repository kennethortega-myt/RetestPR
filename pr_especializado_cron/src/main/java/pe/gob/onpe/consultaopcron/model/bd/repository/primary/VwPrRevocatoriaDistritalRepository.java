package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrRevocatoriaDistrital;

import java.util.List;

public interface VwPrRevocatoriaDistritalRepository extends MongoRepository<VwPrRevocatoriaDistrital, Integer> {
    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
}
