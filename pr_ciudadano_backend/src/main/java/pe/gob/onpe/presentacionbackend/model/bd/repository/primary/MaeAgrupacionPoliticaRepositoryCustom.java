package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;


@Repository
public class MaeAgrupacionPoliticaRepositoryCustom {

	Logger logger = LoggerFactory.getLogger(MaeAgrupacionPoliticaRepositoryCustom.class);
	
    private final MongoOperations mongoOperations;

    public MaeAgrupacionPoliticaRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
    
    public MaeAgrupacionPolitica getPosicionAndIdUbigeoEleccion(Long posicion, Long idDetUbigeoEleccion) {
        Criteria criteria = Criteria.where("nPosicion").is(posicion)
                .and("ubigeoEleccion.id").is(idDetUbigeoEleccion);
        Query query = Query.query(criteria);
        DetUbigeoEleccionAgrupacionPolitica agrupacionUbigeo = mongoOperations.findOne(query, DetUbigeoEleccionAgrupacionPolitica.class);
    	return agrupacionUbigeo != null ? agrupacionUbigeo.getAgrupacionPolitica(): null;
    }


}
