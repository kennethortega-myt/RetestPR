package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrDiputados;

import java.util.List;
import java.util.Optional;

public interface VwPrDiputadosRepository extends MongoRepository<VwPrDiputados, Integer> {
    Optional<VwPrDiputados> findById(Integer idVista);

    List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndDistritoElectoral(Integer tipoEleccion, String tipoFiltro, Integer distritoElectoral);
    List<VwPrDiputados> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
    List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
    List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambito, Integer ubigeo1);
    List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);
    List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);

}
