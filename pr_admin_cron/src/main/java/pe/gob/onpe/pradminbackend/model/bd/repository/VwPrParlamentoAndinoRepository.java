package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParlamentoAndino;

import java.util.List;
import java.util.Optional;

public interface VwPrParlamentoAndinoRepository extends MongoRepository<VwPrParlamentoAndino, Integer> {
    Optional<VwPrParlamentoAndino> findById(Integer idVista);

    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);

    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01);
    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02);
    List<VwPrParlamentoAndino> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03);
}
