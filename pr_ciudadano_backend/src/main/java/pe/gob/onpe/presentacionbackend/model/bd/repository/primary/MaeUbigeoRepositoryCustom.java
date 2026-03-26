package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.dto.*;

import java.util.Arrays;
import java.util.List;

@Repository
public class MaeUbigeoRepositoryCustom {

    private final MongoOperations mongoOperations;

	private static final String UBIGEO_STRING = "ubigeo";
	private static final String MAE_UBIGEO_STRING = "mae_ubigeo";
	private static final String N_ID_UBIGEO_STRING = "n_id_ubigeo";
	private static final String UBIGEO_DETAILS_STRING = "ubigeo_details";
	private static final String PROVINCIA_DETAILS_STRING = "provincia_details";
	private static final String NOMBRE_STRING = "nombre";
	private static final String DET_UBIGEO_ELECCION_STRING = "det_ubigeo_eleccion";
	private static final String ELECCION_ID_STRING = "o_eleccion.$id";
	private static final String UBIGEO_DETAILS_C_UBIGEO = "ubigeo_details.c_ubigeo";

    public MaeUbigeoRepositoryCustom(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

	public List<UbigeoDepartamentoDto> listarDepartamentosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro) {

		Aggregation aggregation = Aggregation.newAggregation(
				// Primer $lookup para obtener detalles de ubigeo
				Aggregation.lookup(MAE_UBIGEO_STRING, N_ID_UBIGEO_STRING, "_id", UBIGEO_DETAILS_STRING),
				Aggregation.unwind(UBIGEO_DETAILS_STRING),

				// Segundo $lookup para obtener detalles de provincia
				Aggregation.lookup(MAE_UBIGEO_STRING, UBIGEO_DETAILS_STRING+".n_ubigeo_padre", "_id", PROVINCIA_DETAILS_STRING),
				Aggregation.unwind(PROVINCIA_DETAILS_STRING),

				// Tercer $lookup para obtener detalles de departamento
				Aggregation.lookup(MAE_UBIGEO_STRING, "provincia_details.n_ubigeo_padre", "_id", "departamento_details"),
				Aggregation.unwind("departamento_details"),

				// $match para filtrar por eleccion y asegurarse de que sea un departamento
				Aggregation.match(
						Criteria.where(ELECCION_ID_STRING).is(filtro.getIdEleccion())
								.and("departamento_details.n_tipo_ambito_geografico").is(filtro.getIdAmbitoGeografico())
								.and("departamento_details.n_ubigeo_padre").is(0)
				),

				// $group para agrupar los resultados
				Aggregation.group("departamento_details.c_ubigeo", "departamento_details.c_nombre")
						.first("departamento_details.c_ubigeo").as(UBIGEO_STRING)
						.first("departamento_details.c_nombre").as(NOMBRE_STRING),

				// $project para seleccionar los campos necesarios y excluir _id
				Aggregation.project(UBIGEO_STRING, NOMBRE_STRING)
						.andExclude("_id"),

				// $sort para ordenar por ubigeo
				Aggregation.sort(Sort.by(Sort.Order.asc(UBIGEO_STRING)))
		);

		AggregationResults<UbigeoDepartamentoDto> results = mongoOperations.aggregate(aggregation, DET_UBIGEO_ELECCION_STRING, UbigeoDepartamentoDto.class);
		return results.getMappedResults();
	}

	public List<UbigeoProvinciaDto> listarProvinciasPorIdEleccionII(FiltroUbigeoProvinciaDto filtro) {

		Aggregation aggregation = Aggregation.newAggregation(
				// Primer $lookup para obtener detalles de ubigeo
				Aggregation.lookup(MAE_UBIGEO_STRING, N_ID_UBIGEO_STRING, "_id", UBIGEO_DETAILS_STRING),
				Aggregation.unwind(UBIGEO_DETAILS_STRING),

				// Segundo $lookup para obtener detalles de provincia
				Aggregation.lookup(MAE_UBIGEO_STRING, "ubigeo_details.n_ubigeo_padre", "_id", PROVINCIA_DETAILS_STRING),
				Aggregation.unwind(PROVINCIA_DETAILS_STRING),

				// $match para filtrar por eleccion y asegurarse de que sea un departamento
				Aggregation.match(
						Criteria.where(ELECCION_ID_STRING).is(filtro.getIdEleccion())
								.and("provincia_details.n_tipo_ambito_geografico").is(filtro.getIdAmbitoGeografico())
								.and("provincia_details.n_ubigeo_padre").is(filtro.getIdUbigeoDepartamento())
				),

				// $group para agrupar los resultados
				Aggregation.group("provincia_details.c_ubigeo", "provincia_details.c_nombre")
						.first("provincia_details.c_ubigeo").as(UBIGEO_STRING)
						.first("provincia_details.c_nombre").as(NOMBRE_STRING),

				// $project para seleccionar los campos necesarios y excluir _id
				Aggregation.project(UBIGEO_STRING, NOMBRE_STRING)
						.andExclude("_id"),

				// $sort para ordenar por ubigeo
				Aggregation.sort(Sort.by(Sort.Order.asc(UBIGEO_STRING)))
		);

		AggregationResults<UbigeoProvinciaDto> results = mongoOperations.aggregate(aggregation, DET_UBIGEO_ELECCION_STRING, UbigeoProvinciaDto.class);
		return results.getMappedResults();
	}

	public List<UbigeoDistritoDto> listarDistritosPorIdEleccionII(FiltroUbigeoDistritoDto filtro) {

		Aggregation aggregation = Aggregation.newAggregation(
				// Primer $lookup para obtener detalles de ubigeo
				Aggregation.lookup(MAE_UBIGEO_STRING, N_ID_UBIGEO_STRING, "_id", UBIGEO_DETAILS_STRING),
				Aggregation.unwind(UBIGEO_DETAILS_STRING),

				// $match para filtrar por eleccion y asegurarse de que sea un departamento
				Aggregation.match(
						Criteria.where(ELECCION_ID_STRING).is(filtro.getIdEleccion())
								.and("ubigeo_details.n_tipo_ambito_geografico").is(filtro.getIdAmbitoGeografico())
								.and("ubigeo_details.n_ubigeo_padre").is(filtro.getIdUbigeoProvincia())
				),

				// $group para agrupar los resultados
				Aggregation.group(UBIGEO_DETAILS_C_UBIGEO, "ubigeo_details.c_nombre")
						.first(UBIGEO_DETAILS_C_UBIGEO).as(UBIGEO_STRING)
						.first("ubigeo_details.c_nombre").as(NOMBRE_STRING),

				// $project para seleccionar los campos necesarios y excluir _id
				Aggregation.project(UBIGEO_STRING, NOMBRE_STRING)
						.andExclude("_id"),

				// $sort para ordenar por ubigeo
				Aggregation.sort(Sort.by(Sort.Order.asc(UBIGEO_STRING)))
		);

		AggregationResults<UbigeoDistritoDto> results = mongoOperations.aggregate(aggregation, DET_UBIGEO_ELECCION_STRING, UbigeoDistritoDto.class);
		return results.getMappedResults();
	}
	
	public List<UbigeoDistritoDto> listarDepProvDistritoPorIdEleccion() {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.lookup(MAE_UBIGEO_STRING, N_ID_UBIGEO_STRING, "_id", UBIGEO_DETAILS_STRING),
				Aggregation.unwind(UBIGEO_DETAILS_STRING),
				Aggregation.project()
					.and(UBIGEO_DETAILS_C_UBIGEO).as(UBIGEO_STRING)
					.and(context -> new Document("$concat", Arrays.asList(
						"$ubigeo_details.c_departamento", " \\ ", "$ubigeo_details.c_provincia", " \\ ", "$ubigeo_details.c_nombre"
					))).as(NOMBRE_STRING)
					.andExclude("_id"),
				Aggregation.sort(Sort.by(Sort.Order.asc(UBIGEO_STRING)))
		);
		AggregationResults<UbigeoDistritoDto> results = mongoOperations.aggregate(aggregation, DET_UBIGEO_ELECCION_STRING, UbigeoDistritoDto.class);
		return results.getMappedResults();
	}

}
