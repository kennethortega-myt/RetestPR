package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.FiltroEleccionSenadoresUnicosReporteDto;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.FiltroParticipanteSenadoresUnicosDto;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.ParticipanteCandidatoSenadoresUnicosDto;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.ParticipanteSenadoresUnicosReporteDto;

import java.util.Arrays;
import java.util.List;

@Repository
public class VwPrSenadoresDistritoNacionalUnicoRepositoryCustom {

	private MongoOperations mongoOperations;

	public VwPrSenadoresDistritoNacionalUnicoRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}
	
	public Page<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosGraficoPaginado(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto, int numeroPagina, int tamanioPagina) {

		Criteria filterCriteria = Criteria.where("tipoEleccion").is(filtroParticipanteDto.getIdEleccion())
				.and("tipoFiltro").is(filtroParticipanteDto.getTipoFiltro())
				.and("detalle.grafico").is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and("ambitoGeografico").is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and("ubigeoNivel01").is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and("ubigeoNivel02").is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and("ubigeoNivel03").is(filtroParticipanteDto.getUbigeoNivel3());
		}


	    Pageable pageable = PageRequest.of(numeroPagina, tamanioPagina);

	    // Agregación para obtener el total de elementos
	    Aggregation totalAggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind("$detalle"),
	        Aggregation.unwind("$detalle.candidato"),
	        Aggregation.project()
	            .and("detalle.descripcion").as("nombreAgrupacionPolitica")
	            .and("detalle.cCodigo").as("codigoAgrupacionPolitica")
	            .and("detalle.candidato.votos").as("totalVotosEmitidos")
	            .and("detalle.candidato.lista").as("lista")
				.and("detalle.candidato.id").as("idCandidato"),
			Aggregation.sort(Sort.Direction.DESC,"totalVotosEmitidos")
	    );
	    AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> totalResults = this.mongoOperations.aggregate(totalAggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);
	    long total = totalResults.getMappedResults().size();

	    // Agregación para obtener los resultados paginados
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind("$detalle"),
	        Aggregation.unwind("$detalle.candidato"),
	        //Aggregation.lookup("mae_candidato", "detalle.cCandidato.id", "id", "mae_candidato"),
	        //Aggregation.unwind("$mae_candidato", true),
	        Aggregation.project()
	            .and("detalle.descripcion").as("nombreAgrupacionPolitica")
	            .and("detalle.cCodigo").as("codigoAgrupacionPolitica")
	            //.and("mae_candidato.c_nombres").as("nombreCandidato")
	            //.and("mae_candidato.documentoIdentidad").as("dniCandidato")
	            .and("detalle.candidato.votos").as("totalVotosEmitidos")
	            .and("detalle.candidato.lista").as("lista")
				.and("detalle.candidato.id").as("idCandidato"),
			Aggregation.sort(Sort.Direction.DESC,"totalVotosEmitidos"),
	        Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	        Aggregation.limit(pageable.getPageSize())
	    );


	    AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);
	    List<ParticipanteCandidatoSenadoresUnicosDto> mappedResults = results.getMappedResults();

	    return new PageImpl<>(mappedResults, pageable, total);
	}


	public List<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosGraficoAll(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where("tipoEleccion").is(filtroParticipanteDto.getIdEleccion())
				.and("tipoFiltro").is(filtroParticipanteDto.getTipoFiltro())
				.and("detalle.grafico").is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and("ambitoGeografico").is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and("ubigeoNivel01").is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and("ubigeoNivel02").is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and("ubigeoNivel03").is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind("$detalle"),
				Aggregation.unwind("$detalle.candidato"),
				Aggregation.project()
						.and("$detalle.descripcion").as("nombreAgrupacionPolitica")
						.and("$detalle.cCodigo").as("codigoAgrupacionPolitica")
						.and("$detalle.candidato.id").as("n_candidato")
						.and("$detalle.candidato.votos").as("n_total_votos")
						.and("$detalle.candidato.lista").as("n_lista")
				// Otros campos que desees mantener
				,
				Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
				Aggregation.unwind("$MaeCandidato", true),
				Aggregation.project()
						.and("nombreAgrupacionPolitica").as("nombreAgrupacionPolitica")
						.and("codigoAgrupacionPolitica").as("codigoAgrupacionPolitica")
						.and(context -> new Document("$concat", Arrays.asList(
								"$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
						))).as("nombreCandidato")
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and("n_total_votos").as("totalVotosValidos")
						.and("n_lista").as("lista"),
				Aggregation.sort(Sort.Direction.DESC,"totalVotosValidos")
		);

		AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);

        return results.getMappedResults();
	}

	public List<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosAgrupacionPoliticaNombre(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where("tipoEleccion").is(filtroParticipanteDto.getIdEleccion())
				.and("tipoFiltro").is(filtroParticipanteDto.getTipoFiltro())
				.and("detalle.grafico").is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and("ambitoGeografico").is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and("ubigeoNivel01").is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and("ubigeoNivel02").is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and("ubigeoNivel03").is(filtroParticipanteDto.getUbigeoNivel3());
		}

		Criteria criteriaFiltroAdicional = Criteria.where("codigoAgrupacionPolitica").is(filtroParticipanteDto.getIdAgrupacionPolitica());
		if(filtroParticipanteDto.getNombreCandidato() != null && !filtroParticipanteDto.getNombreCandidato().isEmpty()) {
			criteriaFiltroAdicional.and("nombreCandidato").regex(filtroParticipanteDto.getNombreCandidato(), "i");
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind("$detalle"),
				Aggregation.unwind("$detalle.candidato"),
				Aggregation.project()
						.and("$detalle.descripcion").as("nombreAgrupacionPolitica")
						.and("$detalle.nAgrupacionPolitica").as("codigoAgrupacionPolitica")
						.and("$detalle.candidato.id").as("n_candidato")
						.and("$detalle.candidato.votos").as("n_total_votos")
						.and("$detalle.candidato.lista").as("n_lista"),
				// match con mae_candidato
				Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
				Aggregation.unwind("$MaeCandidato", true),
				Aggregation.project()
						.and("nombreAgrupacionPolitica").as("nombreAgrupacionPolitica")
						.and("codigoAgrupacionPolitica").as("codigoAgrupacionPolitica")
						.and(context -> new Document("$concat", Arrays.asList(
								"$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
						))).as("nombreCandidato")
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and("n_total_votos").as("totalVotosValidos")
						.and("n_lista").as("lista")
						.and("n_candidato").as("idCandidato"),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC,"totalVotosValidos")
		);

		AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);

		return results.getMappedResults();
	}

	//reporte
	public List<ParticipanteSenadoresUnicosReporteDto> buscarCandidatosGraficoAllReporte(FiltroEleccionSenadoresUnicosReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where("tipoEleccion").is(filtroParticipanteDto.getIdEleccion())
				.and("tipoFiltro").is(filtroParticipanteDto.getTipoFiltro())
				.and("detalle.grafico").is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and("ambitoGeografico").is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and("ubigeoNivel01").is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and("ubigeoNivel02").is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and("ubigeoNivel03").is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind("$detalle"),
				Aggregation.unwind("$detalle.candidato"),
				Aggregation.project()

						.and("$detalle.cCodigo").as("codOrgPolitica")
						.and("$detalle.candidato.id").as("n_candidato")
						.and("$detalle.candidato.votos").as("n_total_votos")
						.and("$detalle.candidato.lista").as("n_lista")
				// Otros campos que desees mantener
				,
				Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
				Aggregation.unwind("$MaeCandidato", true),
				Aggregation.project()

						.and("codOrgPolitica").as("codOrgPolitica")
						.and(context -> new Document("$concat", Arrays.asList(
								"$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
						))).as("candidato")
						.and("n_total_votos").as("totalVotos")
						.and("n_lista").as("lista"),
				Aggregation.sort(Sort.Direction.ASC,"codOrgPolitica"),
				Aggregation.sort(Sort.Direction.ASC,"lista"),
				Aggregation.sort(Sort.Direction.DESC,"totalVotos")
		);

		AggregationResults<ParticipanteSenadoresUnicosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteSenadoresUnicosReporteDto.class);

		return results.getMappedResults();
	}

	public List<ParticipanteSenadoresUnicosReporteDto> buscarCandidatosAgrupacionPoliticaNombreReporte(FiltroEleccionSenadoresUnicosReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where("tipoEleccion").is(filtroParticipanteDto.getIdEleccion())
				.and("tipoFiltro").is(filtroParticipanteDto.getTipoFiltro())
				.and("detalle.grafico").is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and("ambitoGeografico").is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and("ubigeoNivel01").is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and("ubigeoNivel02").is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and("ubigeoNivel03").is(filtroParticipanteDto.getUbigeoNivel3());
		}

		Criteria criteriaFiltroAdicional = Criteria.where("codOrgPolitica").is(filtroParticipanteDto.getIdOrgPolitica());


		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind("$detalle"),
				Aggregation.unwind("$detalle.candidato"),
				Aggregation.project()

						.and("$detalle.nAgrupacionPolitica").as("codigoAgrupacionPolitica")
						.and("$detalle.candidato.id").as("n_candidato")
						.and("$detalle.candidato.votos").as("n_total_votos")
						.and("$detalle.candidato.lista").as("n_lista"),
				// match con mae_candidato
				Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
				Aggregation.unwind("$MaeCandidato", true),
				Aggregation.project()
						.and("codigoAgrupacionPolitica").as("codOrgPolitica")
						.and(context -> new Document("$concat", Arrays.asList(
								"$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
						))).as("candidato")

						.and("n_total_votos").as("totalVotos")
						.and("n_lista").as("lista"),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC,"totalVotos")
		);

		AggregationResults<ParticipanteSenadoresUnicosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteSenadoresUnicosReporteDto.class);

		return results.getMappedResults();
	}

}
