package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeModulo;

import java.util.List;

@Repository
public class MaeModuloCustomRepository {

    private final MongoOperations mongoOperations;

    public MaeModuloCustomRepository(MongoOperations mongoOperations) {
        super();
        this.mongoOperations = mongoOperations;
    }

    public List<MaeModulo> findByNEleccion(Long nEleccion) {
        Criteria criteria = Criteria.where("nEleccion").is(nEleccion);
        Query query = Query.query(criteria);
        return mongoOperations.find(query, MaeModulo.class);
    }
}
