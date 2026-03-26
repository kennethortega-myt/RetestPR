package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccion;

@Repository
public class DetUbigeoEleccionRepositoryCustom {

    private final MongoOperations mongoOperations;

    public DetUbigeoEleccionRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void deleteByIdCentroComputoAndProceso(Long idCentroComputo, String proceso) {
        Criteria criteria = Criteria.where("id.nCentroComputo").is(idCentroComputo)
                .and("id.cAcronimoProceso").is(proceso);
        Query query = Query.query(criteria);
        mongoOperations.remove(query, DetUbigeoEleccion.class);
    }

}
