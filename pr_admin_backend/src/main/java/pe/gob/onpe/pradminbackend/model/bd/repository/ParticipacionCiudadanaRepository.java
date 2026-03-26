package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParticipacionCiudadana;

import java.util.List;
import java.util.Optional;

public interface ParticipacionCiudadanaRepository extends MongoRepository<VwPrParticipacionCiudadana, Integer> {
    List<VwPrParticipacionCiudadana> findByTipoFiltro(String tipoFiltro);

    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeografico(String tipoFiltro, Integer ambito);

    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(String tipoFiltro, Integer ambito, Integer ubigeo1);

    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);

    List<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);

    Optional<VwPrParticipacionCiudadana> findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03AndIdLocalVotacion(String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3, Long idLocalVotacion);

}

