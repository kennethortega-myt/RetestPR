package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrDiputados;

import java.util.List;
import java.util.Optional;

public interface VwPrDiputadosRepository extends MongoRepository<VwPrDiputados, Integer> {

	Optional<VwPrDiputados> findById(Integer idVista);

	List<VwPrDiputados> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);

}
