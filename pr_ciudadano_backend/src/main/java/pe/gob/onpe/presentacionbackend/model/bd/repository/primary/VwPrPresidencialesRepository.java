package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrPresidenciales;

import java.util.List;
import java.util.Optional;

public interface VwPrPresidencialesRepository extends MongoRepository<VwPrPresidenciales, Integer> {

    Optional<VwPrPresidenciales> findById(Integer idVista);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambito);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);
    List<VwPrPresidenciales> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);

}
