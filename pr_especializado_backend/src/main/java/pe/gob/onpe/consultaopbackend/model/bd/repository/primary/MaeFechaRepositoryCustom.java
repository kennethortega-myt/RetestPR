package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeFecha;

@Repository
public class MaeFechaRepositoryCustom {

    private final MongoOperations mongoOperations;

    public MaeFechaRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
    
    public MaeFecha obtenerFechaProceso(String id) {
    	Query query = new Query(Criteria.where("id").is(id));
        return mongoOperations.findOne(query, MaeFecha.class);
    }

   
}
