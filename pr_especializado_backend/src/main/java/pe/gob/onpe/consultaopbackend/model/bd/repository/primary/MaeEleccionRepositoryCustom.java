package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeModulo;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteAutomaticoService;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.EleccionesMenuResponse;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MaeEleccionRepositoryCustom {

    private final MongoOperations mongoOperations;
    private final TabReporteAutomaticoService tabReporteAutomaticoService;

    public MaeEleccionRepositoryCustom(
    		MongoOperations mongoOperations,
    		TabReporteAutomaticoService tabReporteAutomaticoService
    ) {
        this.mongoOperations = mongoOperations;
        this.tabReporteAutomaticoService = tabReporteAutomaticoService;
    }

    public void deleteByIdCentroComputoAndProceso(Long idCentroComputo, String proceso) {
        Criteria criteria = Criteria.where("id.nCentroComputo").is(idCentroComputo)
                .and("id.cAcronimoProceso").is(proceso);
        Query query = Query.query(criteria);
        mongoOperations.remove(query, MaeEleccion.class);
    }

    public List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo) {
        Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("nActivo").is(activo);
        Query query = Query.query(criteria);
        List<MaeEleccion> eleccionesActivas =  mongoOperations.find(query, MaeEleccion.class);

        List<Long> idElecciones = new ArrayList<>();
        if(!eleccionesActivas.isEmpty()) {
            eleccionesActivas.forEach(eleccion ->
                    idElecciones.add(eleccion.getId()));
        }

        //Se agrega el id igual a cero para considerar los menus por defecto
        idElecciones.add(ConstantesComunes.CODIGO_ELECCION_CERO);

        Criteria filterCriteria = Criteria.where("nEleccion").in(idElecciones)
                .and("nActivo").is(1);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(filterCriteria),
                Aggregation.sort(Sort.Direction.ASC,"nOrden"),
                Aggregation.project()
                        .and("cNombre").as("nombre")
                        .and("nPadre").as("padre")
                        .and("bHijos").as("hijos")
                        .and("cIcono").as("icono")
                        .and("nOrden").as("orden")
                        .and("nEleccion").as("idEleccion")
                        .and("cUrl").as("url")
                        .and("cPrincipal").as("esPrincipal")

        );
        AggregationResults<EleccionesMenuResponse> results = mongoOperations.aggregate(
                aggregation, MaeModulo.class, EleccionesMenuResponse.class
        );

        return results.getMappedResults();
    }
    
    public List<MaeEleccion> findMaeEleccionByProceso(Long idProceso) {
        Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("activo").is(1);
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.asc("codigo")));
        return mongoOperations.find(query, MaeEleccion.class);
    }
    
    public List<MaeEleccion> findMaeEleccionByProcesoForConfigReport(Long idProceso) {
    	List<TabReporteAutomaticoResDto> listTabReporteAutomatico = tabReporteAutomaticoService.obtenerTodos();
    	List<Integer> idsElecciones = listTabReporteAutomatico.stream()
    	        .map(TabReporteAutomaticoResDto::getEleccionId)
    	        .collect(Collectors.toList());
    	
        Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("activo").is(1).and("id").nin(idsElecciones);

        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.asc("codigo")));
        return mongoOperations.find(query, MaeEleccion.class);
    }
}
