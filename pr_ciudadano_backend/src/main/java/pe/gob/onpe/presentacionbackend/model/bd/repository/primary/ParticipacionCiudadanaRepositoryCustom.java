package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana.FiltroParticipacionCiudadana;
import pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana.ParticipacionCiudadanaResponseDto;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ParticipacionCiudadanaRepositoryCustom {

	private final MongoOperations mongoOperations;

	public List<ParticipacionCiudadanaResponseDto> obtenerParticipacionCiudadanaXDep(FiltroParticipacionCiudadana filtroParticipacionCiudadana) {
		Query query = new Query(
				Criteria.where("tipoFiltro").is(filtroParticipacionCiudadana.getTipoFiltro())
				);
		List<VwPrParticipacionCiudadana> lstVwPrParticipacionCiudadana = mongoOperations.find(query, VwPrParticipacionCiudadana.class);
		if(lstVwPrParticipacionCiudadana.isEmpty()) {
			return Collections.emptyList();
		}
		return lstVwPrParticipacionCiudadana.stream()
				.map(data -> ParticipacionCiudadanaResponseDto.builder()
						.ubigeoNivel01(data.getUbigeoNivel01())
						.ubigeoNivel02(data.getUbigeoNivel02())
						.ubigeoNivel03(data.getUbigeoNivel03())
						.totalElectoresHabiles(data.getTotalElectoresHabiles())
						.build()
						).toList();
	}


}
