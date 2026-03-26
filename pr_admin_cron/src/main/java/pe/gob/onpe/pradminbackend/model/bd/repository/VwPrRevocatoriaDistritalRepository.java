package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrRevocatoriaDistrital;

import java.util.List;

public interface VwPrRevocatoriaDistritalRepository extends MongoRepository<VwPrRevocatoriaDistrital, Integer> {
    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);

    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);

    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01);

    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02);

    List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03);

}
