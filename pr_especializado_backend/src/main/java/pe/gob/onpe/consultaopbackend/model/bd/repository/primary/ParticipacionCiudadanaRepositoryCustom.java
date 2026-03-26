package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.FiltroParticipacionCiudadana;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.ParticipacionCiudadanaResponseDto;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class ParticipacionCiudadanaRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;


    /**
     * Obtener lista de participación ciudadana filtrado por departamento
     * @param filtroParticipacionCiudadana
     * @return participacionCiudadanaResponseDto
     */
    public List<ParticipacionCiudadanaResponseDto> obtenerParticipacionCiudadanaXDep(FiltroParticipacionCiudadana filtroParticipacionCiudadana) {
        Query query = new Query(
                Criteria.where("tipoFiltro").is(filtroParticipacionCiudadana.getTipoFiltro())
        );
        List<VwPrParticipacionCiudadana> lstVwPrParticipacionCiudadana = mongoOperations.find(query, VwPrParticipacionCiudadana.class);

        return lstVwPrParticipacionCiudadana != null ? lstVwPrParticipacionCiudadana.stream()
                .map(data -> ParticipacionCiudadanaResponseDto.builder()
                        .ubigeoNivel01(data.getUbigeoNivel01())
                        .ubigeoNivel02(data.getUbigeoNivel02())
                        .ubigeoNivel03(data.getUbigeoNivel03())
                        //.idLocalVotacion(data.getIdLocalVotacion())
                        .totalElectoresHabiles(data.getTotalElectoresHabiles())
                        .build()
                ).toList() : Collections.emptyList();
    }


}
