package pe.gob.onpe.consultaopcron.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.consultaopcron.model.bd.documents.VwPrParlamentoAndino;

import java.util.List;
import java.util.Optional;

public interface VwPrParlamentoAndinoRepository extends MongoRepository<VwPrParlamentoAndino, Integer> {

    Optional<VwPrParlamentoAndino> findById(Integer idVista);

    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
}
