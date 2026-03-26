package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeModulo;
import pe.gob.onpe.presentacionbackend.model.dto.response.EleccionesMenuResponse;
import pe.gob.onpe.presentacionbackend.utils.ConstantesComunes;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MaeEleccionRepositoryCustom {

    private final MongoOperations mongoOperations;

    private static final String ORDEN = "orden";

    public MaeEleccionRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void deleteByIdCentroComputoAndProceso(Long idCentroComputo, String proceso) {
        Criteria criteria = Criteria.where("id.nCentroComputo").is(idCentroComputo)
                .and("id.cAcronimoProceso").is(proceso);
        Query query = Query.query(criteria);
        mongoOperations.remove(query, MaeEleccion.class);
    }

    public List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo) {
        Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("activo").is(activo);
        Query query = Query.query(criteria);
        List<MaeEleccion> eleccionesActivas =  mongoOperations.find(query, MaeEleccion.class);

        List<Long> idElecciones = new ArrayList<>();
        if(!eleccionesActivas.isEmpty()) {
            eleccionesActivas.forEach(eleccion ->
                    idElecciones.add(eleccion.getId()));
        }

        //Se agrega el id igual a cero para considerar los menus por defecto
        idElecciones.add(ConstantesComunes.CODIGO_ELECCION_CERO);

        Criteria filterCriteria = Criteria.where("eleccion").in(idElecciones)
                .and("activo").is(1);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(filterCriteria),
                Aggregation.sort(Sort.Direction.ASC, ORDEN),
                Aggregation.project()
                        .and("nombre").as("nombre")
                        .and("padre").as("padre")
                        .and("hijos").as("hijos")
                        .and("icono").as("icono")
                        .and(ORDEN).as(ORDEN)
                        .and("eleccion").as("idEleccion")
                        .and("url").as("url")
                        .and("principal").as("esPrincipal")
                        .and("descripcion").as("descripcion")

        );
        AggregationResults<EleccionesMenuResponse> results = mongoOperations.aggregate(
                aggregation, MaeModulo.class, EleccionesMenuResponse.class
        );

        return results.getMappedResults();
    }
}
