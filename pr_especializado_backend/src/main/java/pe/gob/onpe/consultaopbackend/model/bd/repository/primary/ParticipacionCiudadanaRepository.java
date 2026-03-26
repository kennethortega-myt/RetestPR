package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrParticipacionCiudadana;

import java.util.List;
import java.util.Optional;

public interface ParticipacionCiudadanaRepository extends MongoRepository<VwPrParticipacionCiudadana, Integer> {

    List<VwPrParticipacionCiudadana> findByTipoFiltro(String tipoFiltro);
    Page<VwPrParticipacionCiudadana> findByTipoFiltro(String tipoFiltro, Pageable pageable);

    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeografico(String tipoFiltro, Integer ambito);

    Page<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeografico(String tipoFiltro, Integer ambito, Pageable pageable);
    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(String tipoFiltro, Integer ambito, Integer ubigeo1);

    Page<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(String tipoFiltro, Integer ambito, Integer ubigeo1, Pageable pageable);
    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);

    Page<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Pageable pageable);
    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);
    Page<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3, Pageable pageable);

    Optional<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03AndIdLocalVotacion(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3,Long idLocalVotacion);

}

