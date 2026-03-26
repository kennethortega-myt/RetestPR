package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrMesa;

import java.util.List;

public interface VwPrMesaRepository extends MongoRepository<VwPrMesa, Long> {
    List<VwPrMesa> findByTipoFiltro(String tipoFiltro);
    List<VwPrMesa> findByTipoFiltroAndAmbitoGeografico(String tipoFiltro, Integer ambito);
    List<VwPrMesa> findByTipoFiltroAndAmbitoGeograficoAndDistritoElectoral(String tipoFiltro, Integer ambito, Integer distritoElectoral);
    List<VwPrMesa> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(String tipoFiltro, Integer ambito, Integer ubigeo1);
    List<VwPrMesa> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);
    List<VwPrMesa> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);

}
