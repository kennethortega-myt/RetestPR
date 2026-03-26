package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeProcesoElectoral;

@Repository
public class MaeProcesoElectoralRepositoryCustom {

    private final MongoOperations mongoOperations;

    public MaeProcesoElectoralRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public MaeProcesoElectoral getProcesoPorAcronimo(String acronimo) {
        Query query = new Query(Criteria.where("cAcronimo").is(acronimo));
        return mongoOperations.findOne(query, MaeProcesoElectoral.class);
    }


}
