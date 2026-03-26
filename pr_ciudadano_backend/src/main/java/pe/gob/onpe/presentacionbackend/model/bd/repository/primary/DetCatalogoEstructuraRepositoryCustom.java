package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoEstructura;

@Repository
public class DetCatalogoEstructuraRepositoryCustom {

	private final MongoOperations mongoOperations;
	
	public DetCatalogoEstructuraRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
	
	public DetCatalogoEstructura getDetCatalogoEstructura(Long idTabla, String columna, Long codigo) {
		Query query = new Query(
					Criteria.where("o_catalogo.$id").is(idTabla).and("cColumna").is(columna).and("nCodigo").is(codigo)
				);
		return mongoOperations.findOne(query, DetCatalogoEstructura.class);
	}
	
}
