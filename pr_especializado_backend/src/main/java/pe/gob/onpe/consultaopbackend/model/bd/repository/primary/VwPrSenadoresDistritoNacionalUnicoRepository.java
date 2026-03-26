package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;

import java.util.List;
import java.util.Optional;

public interface VwPrSenadoresDistritoNacionalUnicoRepository extends MongoRepository<VwPrSenadoresDistritoNacionalUnico, Integer> {

    Optional<VwPrSenadoresDistritoNacionalUnico> findById(Integer idVista);

    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01);
    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02);
    List<VwPrSenadoresDistritoNacionalUnico> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03);

}
