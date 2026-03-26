package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccionAgrupacionPolitica;

@Repository
public class DetUbigeoEleccionAgrupacionPoliticaRepositoryCustom {

    private final MongoOperations mongoOperations;

    public DetUbigeoEleccionAgrupacionPoliticaRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }



    public void deleteByIdCentroComputoAndProceso(Long idCentroComputo, String proceso) {
        Criteria criteria = Criteria.where("id.nCentroComputo").is(idCentroComputo)
                .and("id.cAcronimoProceso").is(proceso);
        Query query = Query.query(criteria);
        mongoOperations.remove(query, DetUbigeoEleccionAgrupacionPolitica.class);
    }

}
