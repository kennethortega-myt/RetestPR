package pe.gob.onpe.pradminbackend.model.bd.secondary.repository;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ActaAgrupacion;

import java.util.List;


@Repository
public class MaeCandidatoRepositoryCustom {
	public static final String AGRUPACION_POLITICA = "agrupacionPolitica";
	public static final String ELECCION = "eleccion";
	private MongoOperations mongoOperations;

	public MaeCandidatoRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}

	
	public List<ActaAgrupacion> findByEleccionGroupedByAgrupacionPolitica(Long eleccionId) {
		Criteria filtro = Criteria.where(ELECCION).is(new MaeEleccion(eleccionId));
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filtro),
				Aggregation.group(AGRUPACION_POLITICA).count().as("total")
		);
		AggregationResults<ActaAgrupacion> results = this.mongoOperations.aggregate(aggregation, MaeCandidato.class, ActaAgrupacion.class);
		return results.getMappedResults();
	}
	
	public List<ActaAgrupacion> findByEleccionAndDistritoElectoralGroupedByAgrupacionPolitica(Long eleccionId, Integer distritoElectoral) {
		Criteria filtro = Criteria.where(ELECCION).is(new MaeEleccion(eleccionId))
				.and("distritoElectoral").is(new MaeDistritoElectoral(distritoElectoral));
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filtro),
				Aggregation.group(AGRUPACION_POLITICA).count().as("total")
		);
		AggregationResults<ActaAgrupacion> results = this.mongoOperations.aggregate(aggregation, MaeCandidato.class, ActaAgrupacion.class);
		return results.getMappedResults();
	}

}
