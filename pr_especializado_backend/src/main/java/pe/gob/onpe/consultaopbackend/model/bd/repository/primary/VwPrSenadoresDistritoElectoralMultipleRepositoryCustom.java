package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritomultiple.*;

import java.util.Arrays;
import java.util.List;

@Repository
public class VwPrSenadoresDistritoElectoralMultipleRepositoryCustom {
	private MongoOperations mongoOperations;

	public VwPrSenadoresDistritoElectoralMultipleRepositoryCustom(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public  List<ResponseParticipanteSenadorDistritoMultipleDto> participantesUbicacionGeograficaNombre(RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteNombreDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteNombreDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteNombreDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteNombreDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		   	    Aggregation.unwind("$detalle"),
		   	    Aggregation.sort(Sort.by(Sort.Order.desc("$detalle.votos"), Sort.Order.asc("$detalle.posicion"))),
		   	    Aggregation.project()
		   	    	.and("$detalle.agrupacionPolitica").as("idAgrupacionPolitica")
		   	    	.and("$detalle.codigo").as("codigoAgrupacionPolitica")
		   	    	.and("$detalle.descripcion").as("nombreAgrupacionPolitica")
		   	    	.and("$detalle.votos").as("totalVotosValidos")
		   	    	.and("$detalle.porcentajeVotosValidos").as("porcentajeVotosValidos")
		   	    	.and("$detalle.porcentajeVotosEmitidos").as("porcentajeVotosEmitidos")
		   	    	.and("$detalle.posicion").as("posicion")
		   	);
	    
		AggregationResults<ResponseParticipanteSenadorDistritoMultipleDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ResponseParticipanteSenadorDistritoMultipleDto.class);
		return results.getMappedResults();
	}
	
	public  List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> participantesCandidato(RequestParticipanteCandidatoSenadorDistritoMultipleDto filtroParticipanteCandidatoDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteCandidatoDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteCandidatoDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteCandidatoDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind("$detalle"),
		    	Aggregation.unwind("$detalle.candidatos"),
		    	Aggregation.project()
		    	 	.and("$detalle.agrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("$detalle.descripcion").as("nombreAgrupacionPolitica")
		    	    .and("$detalle.codigo").as("codigoAgrupacionPolitica")
		    	    .and("$detalle.candidatos.idCandidato").as("n_candidato")
		    	    .and("$detalle.candidatos.votos").as("n_votos")
		    	    .and("$detalle.candidatos.lista").as("n_lista"),
		    	Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
		    	Aggregation.unwind("$MaeCandidato", true),
		    	Aggregation.project()
		    	 	.and("idAgrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("nombreAgrupacionPolitica").as("nombreAgrupacionPolitica")
		    	    .and("codigoAgrupacionPolitica").as("codigoAgrupacionPolitica")
		    	    .and(context -> new Document("$concat", Arrays.asList(
		    	            "$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
		    	        ))).as("nombreCandidato")
		    	    .and("MaeCandidato.c_documento_identidad").as("dniCandidato")
		    	    .and("n_votos").as("totalVotosEmitidos")
		    	    .and("n_lista").as("lista"),
		    	Aggregation.sort(Sort.by(Sort.Order.desc("totalVotosEmitidos"), Sort.Order.asc("idAgrupacionPolitica"), Sort.Order.asc("lista")))
		    );
		     
		AggregationResults<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ResponseParticipanteCandidatoSenadorDistritoMultipleDto.class);
		return results.getMappedResults();
	}
	
	public  List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> participantesCandidatoOrganizacion(RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto filtroParticipanteCandidatoNombreDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteCandidatoNombreDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteCandidatoNombreDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteCandidatoNombreDto.getTipoFiltro());  
	    Criteria criteriaFiltro1 = Criteria.where("idAgrupacionPolitica").is(filtroParticipanteCandidatoNombreDto.getIdAgrupacionPolitica());
	    
    	Aggregation aggregation = Aggregation.newAggregation(
    			Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind("$detalle"),
		    	Aggregation.unwind("$detalle.candidatos"),
		    	Aggregation.project()
		    	  	.and("$detalle.agrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("$detalle.descripcion").as("nombreAgrupacionPolitica")
		    	    .and("$detalle.codigo").as("codigoAgrupacionPolitica")
		    	    .and("$detalle.candidatos.idCandidato").as("n_candidato")
		    	    .and("$detalle.candidatos.lista").as("n_lista")
		    	    .and("$detalle.candidatos.votos").as("n_votos"),
		    	Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
		    	Aggregation.unwind("$MaeCandidato", true),
		    	Aggregation.project()
		    		.and("idAgrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("nombreAgrupacionPolitica").as("nombreAgrupacionPolitica")
		    	    .and("codigoAgrupacionPolitica").as("codigoAgrupacionPolitica")
		    	    .and(context -> new Document("$concat", Arrays.asList(
		    	            "$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
		    	        ))).as("nombreCandidato")
		    	    .and("MaeCandidato.c_documento_identidad").as("dniCandidato")
		    	    .and("n_votos").as("totalVotosEmitidos")
		    	    .and("n_lista").as("lista"),
		    	Aggregation.match(criteriaFiltro1),
		    	Aggregation.sort(Sort.by(Sort.Order.desc("totalVotosEmitidos"), Sort.Order.asc("lista")))
		    );
		     
		AggregationResults<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ResponseParticipanteCandidatoSenadorDistritoMultipleDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteSenadoresDistritoMultipleReporteDto> participantesUbicacionGeograficaNombreReporte(FiltroEleccionSenadoresMultipleReporteDto filtroParticipanteNombreDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteNombreDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteNombreDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteNombreDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		   	    Aggregation.unwind("$detalle"),
		        //Aggregation.sort(Sort.by(Sort.Order.desc("$detalle.votos"), Sort.Order.asc("$detalle.posicion"))),
		   	    Aggregation.project()
		   	    	.and("$detalle.posicion").as("posicion")
		   	    	.and("$detalle.agrupacionPolitica").as("nAgrupacionPolitica")
		   	    	.and("$detalle.codigo").as("codOrgPolitica")
		   	    	.and("$detalle.descripcion").as("orgPolitica")
		   	    	.and("$detalle.votos").as("totalVotos")
		   	    	.and("$detalle.porcentajeVotosValidos").as("votosValidos")
		   	    	.and("$detalle.porcentajeVotosEmitidos").as("votosEmitidos"),
		   	    Aggregation.sort(Sort.by(Sort.Order.desc("totalVotos"), Sort.Order.asc("posicion")))
		   	);
	    
		AggregationResults<ParticipanteSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> participantesCandidatoReporte(FiltroEleccionSenadoresMultipleReporteDto filtroParticipanteCandidatoDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteCandidatoDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteCandidatoDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteCandidatoDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind("$detalle"),
		    	Aggregation.unwind("$detalle.candidatos"),
		    	Aggregation.project()
		    	 	.and("$detalle.agrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("$detalle.descripcion").as("nombreAgrupacionPolitica")
		    	    .and("$detalle.codigo").as("codigoAgrupacionPolitica")
		    	    .and("$detalle.candidatos.idCandidato").as("n_candidato")
		    	    .and("$detalle.candidatos.lista").as("n_lista")
		    	    .and("$detalle.candidatos.votos").as("n_votos"),
		    	Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
		    	Aggregation.unwind("$MaeCandidato", true),
		    	Aggregation.project()
		    	 	.and("idAgrupacionPolitica").as("idAgrupacionPolitica")
		    		.and("codigoAgrupacionPolitica").as("codOrgPolitica")
		    	    //.and("nombreAgrupacionPolitica").as("nombreAgrupacionPolitica")
		    	    .and(context -> new Document("$concat", Arrays.asList(
		    	            "$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
		    	        ))).as("nombreCandidato")
		    	    //.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
		    	    .and("n_votos").as("totalVotos")
		    	    .and("n_lista").as("lista"),
		    	Aggregation.sort(Sort.by(Sort.Order.desc("totalVotos"), Sort.Order.asc("idAgrupacionPolitica"), Sort.Order.asc("lista"))),
		    	Aggregation.limit(30)
		    );
		     
		AggregationResults<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteCandidatoSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> participantesCandidatoOrganizacionReporte(FiltroEleccionSenadoresMultipleCandidatoOrganizacionReporteDto filtroParticipanteCandidatoNombreDto) {
	    Criteria criteriaFiltro = Criteria.where("tipoEleccion").is(filtroParticipanteCandidatoNombreDto.getIdEleccion())
	        .and("distritoElectoral").is(filtroParticipanteCandidatoNombreDto.getIdDistritoElectoral())
	        .and("tipoFiltro").is(filtroParticipanteCandidatoNombreDto.getTipoFiltro());  
	    Criteria criteriaFiltro1 = Criteria.where("idAgrupacionPolitica").is(filtroParticipanteCandidatoNombreDto.getIdAgrupacionPolitica());
	    
    	Aggregation aggregation = Aggregation.newAggregation(
    			Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind("$detalle"),
		    	Aggregation.unwind("$detalle.candidatos"),
		    	Aggregation.project()
		    	  	.and("$detalle.agrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("$detalle.descripcion").as("orgPolitica")
		    	    .and("$detalle.codigo").as("codOrgPolitica")
		    	    .and("$detalle.candidatos.idCandidato").as("n_candidato")
		    	    .and("$detalle.candidatos.lista").as("n_lista")
		    	    .and("$detalle.candidatos.votos").as("n_votos"),
		    	Aggregation.lookup("mae_candidato", "n_candidato", "_id", "MaeCandidato"),
		    	Aggregation.unwind("$MaeCandidato", true),
		    	Aggregation.project()
		    		.and("idAgrupacionPolitica").as("idAgrupacionPolitica")
		    	    .and("orgPolitica").as("orgPolitica")
		    	    .and("codOrgPolitica").as("codOrgPolitica")
		    	    .and(context -> new Document("$concat", Arrays.asList(
		    	            "$MaeCandidato.c_nombres", " ", "$MaeCandidato.c_apellido_paterno", " ", "$MaeCandidato.c_apellido_materno"
		    	        ))).as("nombreCandidato")
		    	    //.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
		    	    .and("n_votos").as("totalVotos")
		    	    .and("n_lista").as("lista"),
		    	Aggregation.match(criteriaFiltro1),
		    	Aggregation.sort(Sort.by(Sort.Order.desc("totalVotos"), Sort.Order.asc("lista")))
		    );
		     
		AggregationResults<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteCandidatoSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
}
