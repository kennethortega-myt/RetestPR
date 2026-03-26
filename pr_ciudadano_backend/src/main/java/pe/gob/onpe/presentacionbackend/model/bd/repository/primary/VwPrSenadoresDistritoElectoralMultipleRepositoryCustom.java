package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.FiltroEleccionSenadoresMultipleCandidatoOrganizacionReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.FiltroEleccionSenadoresMultipleReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ParticipanteCandidatoSenadoresDistritoMultipleReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ParticipanteSenadoresDistritoMultipleReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteSenadorDistritoMultipleDto;

import static pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrDiputadosRepositoryCustom.DF_DETALLENESTADO;
import static pe.gob.onpe.presentacionbackend.utils.ConstantesComunes.OP_ESTADO_NOPARTICIPA;

@Repository
public class VwPrSenadoresDistritoElectoralMultipleRepositoryCustom {
	public static final String F_TIPOELECCION = "tipoEleccion";
	public static final String F_DISTRITOELECTORAL = "distritoElectoral";
	public static final String F_TIPOFILTRO = "tipoFiltro";
	public static final String F_DETALLENESTADO = "c_detalle.n_estado";
	public static final String F_NOMBRECANDIDATO = "nombreCandidato";
	
	public static final String O_DETALLEDESCRIPCION = "$detalle.descripcion";
	public static final String O_DETALLEAGRUPOLITICA = "$detalle.agrupacionPolitica";
	public static final String O_DETALLECODIGO = "$detalle.codigo";
	public static final String O_DETALLENVOTOS = "$detalle.votos";
	public static final String O_DETALLENPOSICION = "$detalle.posicion";
	public static final String O_DETALLENAGRUPACIONPOLITICA = "$detalle.agrupacionPolitica";
	public static final String O_DETALLECANDIDATOID = "$detalle.candidato.id";
	public static final String O_DETALLECANDIDATOVOTOS = "$detalle.candidato.votos";
	public static final String O_DETALLECANDIDATOLISTA = "$detalle.candidato.lista";
	public static final String O_MAECANDIDATO = "mae_candidato";
	public static final String O_MAECANDIDATONOMBRES = "$MaeCandidato.c_nombres";
	public static final String O_MAECANDIDATOPATERNO = "$MaeCandidato.c_apellido_paterno";
	public static final String O_MAECANDIDATOMATERNO = "$MaeCandidato.c_apellido_materno";
	public static final String O_MAECANDIDATODNI = "MaeCandidato.c_documento_identidad";
	
	public static final String D_DETALLE = "$detalle";
	public static final String D_DETALLE_CANDIDATO = "$detalle.candidato";
	public static final String D_MAECANDIDATO = "$MaeCandidato";
	public static final String D_MAECANDIDATO1 = "MaeCandidato";
	public static final String D_IDAGRUPOLITICA = "idAgrupacionPolitica";
	public static final String D_NOMBREAGRUPOLITICA = "nombreAgrupacionPolitica";
	public static final String D_CODIGOAGRUPOLITICA = "codigoAgrupacionPolitica";
	public static final String D_NOMBRECANDIDATO = "nombreCandidato";
	public static final String D_CONCAT = "$concat";
	public static final String D_N_CANDIDATO = "n_candidato";
	public static final String D_N_VOTOS = "n_votos";
	public static final String D_N_LISTA = "n_lista";
	public static final String D_ORGPOLITICA = "orgPolitica";
	public static final String D_CANDIDATODNI = "dniCandidato";
	public static final String D_TOTALVOTOSVALIDOS = "totalVotosValidos";
	public static final String D_LISTA = "lista";
	public static final String D_TOTALVOTOSEMITIDOS = "totalVotosEmitidos";
	public static final String D_CODORGPOLITICA = "codOrgPolitica";
	public static final String D_TOTALVOTOS = "totalVotos";
	public static final String D_POSICION = "posicion";

	public static final String DO_UNWIND = "$unwind";
	public static final String DO_IFNULL = "$ifNull";
	public static final String DO_MATCH = "$match";

	private MongoOperations mongoOperations;

	public VwPrSenadoresDistritoElectoralMultipleRepositoryCustom(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	public  List<ResponseParticipanteSenadorDistritoMultipleDto> participantesUbicacionGeograficaNombre(RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteNombreDto) {
	    Criteria criteriaFiltro = Criteria.where(F_TIPOELECCION).is(filtroParticipanteNombreDto.getIdEleccion())
	        .and(F_DISTRITOELECTORAL).is(filtroParticipanteNombreDto.getIdDistritoElectoral())
	        .and(F_TIPOFILTRO).is(filtroParticipanteNombreDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		   	    Aggregation.unwind(D_DETALLE),
				Aggregation.match(Criteria.where("detalle.estado").ne(OP_ESTADO_NOPARTICIPA)),
		   	    Aggregation.sort(Sort.by(Sort.Order.desc(O_DETALLENVOTOS), Sort.Order.asc(O_DETALLENPOSICION))),
		   	    Aggregation.project()
		   	    	.and(O_DETALLEAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		   	    	.and(O_DETALLECODIGO).as(D_CODIGOAGRUPOLITICA)
		   	    	.and(O_DETALLEDESCRIPCION).as(D_NOMBREAGRUPOLITICA)
		   	    	.and(O_DETALLENVOTOS).as(D_TOTALVOTOSVALIDOS)
		   	    	.and("$detalle.porcentajeVotosValidos").as("porcentajeVotosValidos")
		   	    	.and("$detalle.porcentajeVotosEmitidos").as("porcentajeVotosEmitidos")
		   	    	.and(O_DETALLENPOSICION).as(D_POSICION)
		   	);
	    
		AggregationResults<ResponseParticipanteSenadorDistritoMultipleDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ResponseParticipanteSenadorDistritoMultipleDto.class);
		return results.getMappedResults();
	}
	
	public  List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> participantesCandidato(RequestParticipanteCandidatoSenadorDistritoMultipleDto filtroParticipanteCandidatoDto) {
	    Criteria criteriaFiltro = Criteria.where(F_TIPOELECCION).is(filtroParticipanteCandidatoDto.getIdEleccion())
	        .and(F_DISTRITOELECTORAL).is(filtroParticipanteCandidatoDto.getIdDistritoElectoral())
	        .and(F_TIPOFILTRO).is(filtroParticipanteCandidatoDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind(D_DETALLE),
		    	Aggregation.unwind(D_DETALLE_CANDIDATO),
		    	Aggregation.project()
		    	 	.and(O_DETALLEAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    	    .and(O_DETALLEDESCRIPCION).as(D_NOMBREAGRUPOLITICA)
		    	    .and(O_DETALLECODIGO).as(D_CODIGOAGRUPOLITICA)
					.and(O_DETALLENPOSICION).as(D_POSICION)
		    	    .and(O_DETALLECANDIDATOID).as(D_N_CANDIDATO)
		    	    .and(O_DETALLECANDIDATOVOTOS).as(D_N_VOTOS)
		    	    .and(O_DETALLECANDIDATOLISTA).as(D_N_LISTA),
		    	Aggregation.lookup(O_MAECANDIDATO, D_N_CANDIDATO, "_id", D_MAECANDIDATO1),
		    	Aggregation.unwind(D_MAECANDIDATO, true),
		    	Aggregation.project()
					.and(D_POSICION).as(D_POSICION)
		    	 	.and(D_IDAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    	    .and(D_NOMBREAGRUPOLITICA).as(D_NOMBREAGRUPOLITICA)
		    	    .and(D_CODIGOAGRUPOLITICA).as(D_CODIGOAGRUPOLITICA)
		    	    .and(context -> new Document(D_CONCAT, Arrays.asList(
		    	            O_MAECANDIDATONOMBRES, " ", O_MAECANDIDATOPATERNO, " ", O_MAECANDIDATOMATERNO
		    	        ))).as(D_NOMBRECANDIDATO)
		    	    .and(O_MAECANDIDATODNI).as(D_CANDIDATODNI)
		    	    .and(D_N_VOTOS).as(D_TOTALVOTOSVALIDOS)
		    	    .and(D_N_LISTA).as(D_LISTA),
		    	Aggregation.sort(
					Sort.by(
						Sort.Order.desc(D_TOTALVOTOSVALIDOS), 
						Sort.Order.asc(D_POSICION), 
						Sort.Order.asc(D_LISTA)
					)
				),
				Aggregation.project().andExclude(D_POSICION)
		    );
		     
		AggregationResults<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ResponseParticipanteCandidatoSenadorDistritoMultipleDto.class);
		return results.getMappedResults();
	}

	public List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> participantesCandidatoOrganizacion(
			RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto filtro) {

		Document matchMaeCandidato = new Document(DO_MATCH,
				new Document("o_agrupacionPolitica.$id", filtro.getIdAgrupacionPolitica())
						.append("o_eleccion.$id", filtro.getIdEleccion())
						.append("o_distritoElectoral.$id", filtro.getIdDistritoElectoral())
		);

		Document lookupVw = new Document("$lookup",
				new Document("from", "vw_pr_senadores_distrito_multiple")
						.append("let", new Document()
								.append("candidatoId", "$_id")
								.append("agrupId", "$o_agrupacionPolitica.$id"))
						.append("pipeline", Arrays.asList(
								new Document(DO_MATCH, new Document()
										.append("n_tipo_eleccion", filtro.getIdEleccion())
										.append("n_distrito_electoral", filtro.getIdDistritoElectoral())
										.append("c_tipo_filtro", filtro.getTipoFiltro())
								),
								new Document(DO_UNWIND, "$c_detalle"),
								new Document(DO_UNWIND, "$c_detalle.c_candidato"),
								new Document(DO_MATCH, new Document("$expr",
										new Document("$and", Arrays.asList(
												new Document("$eq", Arrays.asList("$c_detalle.n_agrupacion_politica", "$$agrupId")),
												new Document("$eq", Arrays.asList("$c_detalle.c_candidato.n_candidato", "$$candidatoId"))
										))
								)),
								new Document("$project", new Document()
										.append("_id", 0)
										.append(D_N_VOTOS, "$c_detalle.c_candidato.n_total_votos")
										.append(D_N_LISTA, "$c_detalle.c_candidato.n_lista")
								)
						))
						.append("as", "vwCandidato")
		);

		Document unwindVwCandidato = new Document(DO_UNWIND,
				new Document("path", "$vwCandidato").append("preserveNullAndEmptyArrays", true));

		Document lookupAgrup = new Document("$lookup",
				new Document("from", "mae_agrupacion_politica")
						.append("localField", "o_agrupacionPolitica.$id")
						.append("foreignField", "_id")
						.append("as", "agrupacion")
		);

		Document unwindAgrup = new Document(DO_UNWIND,
				new Document("path", "$agrupacion").append("preserveNullAndEmptyArrays", true));

		Document project = new Document("$project", new Document()
				.append("_id", "$_id")
				.append(D_IDAGRUPOLITICA, new Document("$toInt", "$o_agrupacionPolitica.$id"))
				.append(D_NOMBREAGRUPOLITICA,
						new Document(DO_IFNULL, Arrays.asList("$agrupacion.c_descripcion", "")))
				.append(D_CODIGOAGRUPOLITICA,
						new Document(DO_IFNULL, Arrays.asList("$agrupacion.c_codigo", "")))
				.append(D_NOMBRECANDIDATO,
						new Document(D_CONCAT, Arrays.asList(
								new Document(DO_IFNULL, Arrays.asList("$c_nombres", "")), " ",
								new Document(DO_IFNULL, Arrays.asList("$c_apellido_paterno", "")), " ",
								new Document(DO_IFNULL, Arrays.asList("$c_apellido_materno", ""))
						)))

				.append(D_CANDIDATODNI, "$c_documento_identidad")
				.append(D_TOTALVOTOSEMITIDOS,
						new Document(DO_IFNULL, Arrays.asList("$vwCandidato.n_votos", 0)))
				.append(D_LISTA,
						new Document(DO_IFNULL, Arrays.asList("$vwCandidato.n_lista", "$n_lista")))
		);

		Document sort = new Document("$sort", new Document(D_TOTALVOTOSEMITIDOS, -1).append(D_LISTA, 1));

		AggregationOperation opMatchMae = context -> matchMaeCandidato;
		AggregationOperation opLookupVw = context -> lookupVw;
		AggregationOperation opUnwindVw = context -> unwindVwCandidato;
		AggregationOperation opLookupAgrup = context -> lookupAgrup;
		AggregationOperation opUnwindAgrup = context -> unwindAgrup;
		AggregationOperation opProject = context -> project;
		AggregationOperation opSort = context -> sort;

		Aggregation agg = Aggregation.newAggregation(
				opMatchMae,
				opLookupVw,
				opUnwindVw,
				opLookupAgrup,
				opUnwindAgrup,
				opProject,
				opSort
		);

		AggregationResults<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> results =
				this.mongoOperations.aggregate(agg, O_MAECANDIDATO, ResponseParticipanteCandidatoSenadorDistritoMultipleDto.class);

		return results.getMappedResults();
	}

	
	public  List<ParticipanteSenadoresDistritoMultipleReporteDto> participantesUbicacionGeograficaNombreReporte(FiltroEleccionSenadoresMultipleReporteDto filtroParticipanteNombreDto) {
	    Criteria criteriaFiltro = Criteria.where(F_TIPOELECCION).is(filtroParticipanteNombreDto.getIdEleccion())
	        .and(F_DISTRITOELECTORAL).is(filtroParticipanteNombreDto.getIdDistritoElectoral())
	        .and(F_TIPOFILTRO).is(filtroParticipanteNombreDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		   	    Aggregation.unwind(D_DETALLE),
		   	    Aggregation.project()
		   	    	.and(O_DETALLENPOSICION).as(D_POSICION)
		   	    	.and(O_DETALLEAGRUPOLITICA).as("nAgrupacionPolitica")
		   	    	.and(O_DETALLECODIGO).as(D_CODORGPOLITICA)
		   	    	.and(O_DETALLEDESCRIPCION).as(D_ORGPOLITICA)
		   	    	.and(O_DETALLENVOTOS).as(D_TOTALVOTOS)
		   	    	.and("$detalle.porcentajeVotosValidos").as("votosValidos")
		   	    	.and("$detalle.porcentajeVotosEmitidos").as("votosEmitidos"),
		   	    Aggregation.sort(Sort.by(Sort.Order.desc(D_TOTALVOTOS), Sort.Order.asc(D_POSICION)))
		   	);
	    
		AggregationResults<ParticipanteSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> participantesCandidatoReporte(FiltroEleccionSenadoresMultipleReporteDto filtroParticipanteCandidatoDto) {
	    Criteria criteriaFiltro = Criteria.where(F_TIPOELECCION).is(filtroParticipanteCandidatoDto.getIdEleccion())
	        .and(F_DISTRITOELECTORAL).is(filtroParticipanteCandidatoDto.getIdDistritoElectoral())
	        .and(F_TIPOFILTRO).is(filtroParticipanteCandidatoDto.getTipoFiltro());
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind(D_DETALLE),
		    	Aggregation.unwind(D_DETALLE_CANDIDATO),
		    	Aggregation.project()
		    	 	.and(O_DETALLEAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    	    .and(O_DETALLEDESCRIPCION).as(D_NOMBREAGRUPOLITICA)
		    	    .and(O_DETALLECODIGO).as(D_CODIGOAGRUPOLITICA)
		    	    .and(O_DETALLECANDIDATOID).as(D_N_CANDIDATO)
		    	    .and(O_DETALLECANDIDATOLISTA).as(D_N_LISTA)
		    	    .and(O_DETALLECANDIDATOVOTOS).as(D_N_VOTOS),
		    	Aggregation.lookup(O_MAECANDIDATO, D_N_CANDIDATO, "_id", D_MAECANDIDATO1),
		    	Aggregation.unwind(D_MAECANDIDATO, true),
		    	Aggregation.project()
		    	 	.and(D_IDAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    		.and(D_CODIGOAGRUPOLITICA).as(D_CODORGPOLITICA)
		    	    .and(context -> new Document(D_CONCAT, Arrays.asList(
		    	            O_MAECANDIDATONOMBRES, " ", O_MAECANDIDATOPATERNO, " ", O_MAECANDIDATOMATERNO
		    	        ))).as(D_NOMBRECANDIDATO)
		    	    .and(D_N_VOTOS).as(D_TOTALVOTOS)
		    	    .and(D_N_LISTA).as(D_LISTA),
		    	Aggregation.sort(Sort.by(Sort.Order.desc(D_TOTALVOTOS), Sort.Order.asc(D_IDAGRUPOLITICA), Sort.Order.asc(D_LISTA))),
		    	Aggregation.limit(30)
		    );
		     
		AggregationResults<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteCandidatoSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> participantesCandidatoOrganizacionReporte(FiltroEleccionSenadoresMultipleCandidatoOrganizacionReporteDto filtroParticipanteCandidatoNombreDto) {
	    Criteria criteriaFiltro = Criteria.where(F_TIPOELECCION).is(filtroParticipanteCandidatoNombreDto.getIdEleccion())
	        .and(F_DISTRITOELECTORAL).is(filtroParticipanteCandidatoNombreDto.getIdDistritoElectoral())
	        .and(F_TIPOFILTRO).is(filtroParticipanteCandidatoNombreDto.getTipoFiltro());  
	    Criteria criteriaFiltro1 = Criteria.where(D_IDAGRUPOLITICA).is(filtroParticipanteCandidatoNombreDto.getIdAgrupacionPolitica());
	    
    	Aggregation aggregation = Aggregation.newAggregation(
    			Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind(D_DETALLE),
		    	Aggregation.unwind(D_DETALLE_CANDIDATO),
		    	Aggregation.project()
		    	  	.and(O_DETALLEAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    	    .and(O_DETALLEDESCRIPCION).as(D_ORGPOLITICA)
		    	    .and(O_DETALLECODIGO).as(D_CODORGPOLITICA)
		    	    .and(O_DETALLECANDIDATOID).as(D_N_CANDIDATO)
		    	    .and(O_DETALLECANDIDATOLISTA).as(D_N_LISTA)
		    	    .and(O_DETALLECANDIDATOVOTOS).as(D_N_VOTOS),
		    	Aggregation.lookup(O_MAECANDIDATO, D_N_CANDIDATO, "_id", D_MAECANDIDATO1),
		    	Aggregation.unwind(D_MAECANDIDATO, true),
		    	Aggregation.project()
		    		.and(D_IDAGRUPOLITICA).as(D_IDAGRUPOLITICA)
		    	    .and(D_ORGPOLITICA).as(D_ORGPOLITICA)
		    	    .and(D_CODORGPOLITICA).as(D_CODORGPOLITICA)
		    	    .and(context -> new Document(D_CONCAT, Arrays.asList(
		    	            O_MAECANDIDATONOMBRES, " ", O_MAECANDIDATOPATERNO, " ", O_MAECANDIDATOMATERNO
		    	        ))).as(D_NOMBRECANDIDATO)
		    	    .and(D_N_VOTOS).as(D_TOTALVOTOS)
		    	    .and(D_N_LISTA).as(D_LISTA),
		    	Aggregation.match(criteriaFiltro1),
		    	Aggregation.sort(Sort.by(Sort.Order.desc(D_TOTALVOTOS), Sort.Order.asc(D_LISTA)))
		    );
		     
		AggregationResults<ParticipanteCandidatoSenadoresDistritoMultipleReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, ParticipanteCandidatoSenadoresDistritoMultipleReporteDto.class);
		return results.getMappedResults();
	}
}
