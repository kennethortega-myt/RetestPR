package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;

import java.util.List;

@Repository
@Slf4j
public class MaeResumenGeneralRepositoryCustom {

	private final MongoOperations mongoOperations;

	public MaeResumenGeneralRepositoryCustom(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}


	public List<MaeEleccion> findEleccionesByProceso(Long idProceso, Integer activo) {
		Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("nActivo").is(activo);
		Query query = Query.query(criteria);
		return mongoOperations.find(query, MaeEleccion.class);
	}




}
