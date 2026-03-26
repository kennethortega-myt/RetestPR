package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.ArrayList;
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

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrDiputados;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroEleccionDiputadosCandidatoReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroEleccionDiputadosReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteNombreDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteCandidatoDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteCandidatoDiputadosReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.utils.ConstantesComunes;

@Repository
public class VwPrDiputadosRepositoryCustom {
	public static final String DF_TIPOELECCION = "tipoEleccion";
	public static final String DF_DISTRITOELECTORAL = "distritoElectoral";
	public static final String DF_TIPOFILTRO = "tipoFiltro";
	public static final String DF_DETALLENESTADO = "c_detalle.n_estado";
	
	public static final String DO_DETALLEDESCRIPCION = "$detalle.descripcion";
	public static final String DO_DETALLECODIGO = "$detalle.codigo";
	public static final String DO_DETALLENVOTOS = "$detalle.votos"; 
	public static final String DO_DETALLENPOSICION = "$detalle.posicion";
	public static final String DO_DETALLENAGRUPACIONPOLITICA = "$detalle.agrupacionPolitica";
	public static final String DO_DETALLEPORCENTAJEVOTOSVALIDOS = "$detalle.porcentajeVotosValidos";
	public static final String DO_DETALLEPORCENTAJEVOTOSEMITIDOS = "$detalle.porcentajeVotosEmitidos";
	
	public static final String DO_DETALLECANDIDATOID = "$detalle.candidato.id";
	public static final String DO_DETALLECANDIDATOVOTOS = "$detalle.candidato.votos";
	public static final String DO_DETALLECANDIDATOLISTA = "$detalle.candidato.lista";
	public static final String DO_MAECANDIDATO = "mae_candidato";
	public static final String DO_MAECANDIDATONOMBRES = "$MaeCandidato.c_nombres";
	public static final String DO_MAECANDIDATOPATERNO = "$MaeCandidato.c_apellido_paterno";
	public static final String DO_MAECANDIDATOMATERNO = "$MaeCandidato.c_apellido_materno";
	public static final String DO_MAECANDIDATODNI = "MaeCandidato.c_documento_identidad";
	
	public static final String DD_DETALLE = "$detalle";
	public static final String DD_DETALLE_CANDIDATO = "$detalle.candidato";
	public static final String DD_MAECANDIDATO = "$MaeCandidato";
	public static final String DD_MAECANDIDATO1 = "MaeCandidato";
	public static final String DD_IDAGRUPOLITICA = "idAgrupacionPolitica";
	public static final String DD_NOMBREAGRUPOLITICA = "nombreAgrupacionPolitica";
	public static final String DD_CODIGOAGRUPOLITICA = "codigoAgrupacionPolitica";
	public static final String DD_POSICION = "posicion";
	public static final String DD_NOMBRECANDIDATO = "nombreCandidato";
	public static final String DD_CONCAT = "$concat";
	public static final String DD_N_CANDIDATO = "n_candidato";
	public static final String DD_N_VOTOS = "n_total_votos";
	public static final String DD_N_LISTA = "n_lista";
	public static final String DD_ORGPOLITICA = "orgPolitica";
	public static final String DD_CANDIDATODNI = "dniCandidato";
	public static final String DD_TOTALVOTOSVALIDOS = "totalVotosValidos";
	public static final String DD_LISTA = "lista";
	public static final String DD_TOTALVOTOSEMITIDOS = "totalVotosEmitidos";
	public static final String DD_CODORGPOLITICA = "codOrgPolitica";
	public static final String DD_TOTALVOTOS = "totalVotos";

	public static final String DO_UNWIND = "$unwind";
	public static final String DO_IFNULL = "$ifNull";
	public static final String DO_MATCH = "$match";

	private MongoOperations mongoOperations;

	public VwPrDiputadosRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}

	public  List<ParticipanteCandidatoDiputadoDto> buscarCandidatosGraficoAll(FiltroParticipanteDiputadoDto filtroParticipanteDto) {
	    Criteria criteriaFiltro = Criteria.where(DF_TIPOELECCION).is(filtroParticipanteDto.getIdEleccion())
	        .and(DF_DISTRITOELECTORAL).is(filtroParticipanteDto.getIdDistritoElectoral())
	        .and(DF_TIPOFILTRO).is(filtroParticipanteDto.getTipoFiltro());
	    
	    Criteria criteriaFiltroOp = Criteria.where(DF_DETALLENESTADO).ne(ConstantesComunes.OP_ESTADO_NOPARTICIPA);
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    	    Aggregation.match(criteriaFiltro),
	    	    Aggregation.unwind(DD_DETALLE),
	    	    Aggregation.match(criteriaFiltroOp),
	    	    Aggregation.unwind(DD_DETALLE_CANDIDATO),
	    	    Aggregation.project()
	    	        .and(DO_DETALLEDESCRIPCION).as(DD_NOMBREAGRUPOLITICA)
	    	        .and(DO_DETALLECODIGO).as(DD_CODIGOAGRUPOLITICA)
					.and(DO_DETALLENPOSICION).as(DD_POSICION)
	    	        .and(DO_DETALLECANDIDATOID).as(DD_N_CANDIDATO)
	    	        .and(DO_DETALLECANDIDATOVOTOS).as(DD_N_VOTOS)
	    	        .and(DO_DETALLECANDIDATOLISTA).as(DD_N_LISTA),
	    	    Aggregation.lookup(DO_MAECANDIDATO, DD_N_CANDIDATO, "_id", DD_MAECANDIDATO1),
	    	    Aggregation.unwind(DD_MAECANDIDATO, true),
	    	    Aggregation.project()
					.and(DD_POSICION).as(DD_POSICION)
	    	        .and(DD_NOMBREAGRUPOLITICA).as(DD_NOMBREAGRUPOLITICA)
	    	        .and(DD_CODIGOAGRUPOLITICA).as(DD_CODIGOAGRUPOLITICA)
	    	        .and(context -> new Document(DD_CONCAT, Arrays.asList(
	    	                DO_MAECANDIDATONOMBRES, " ", DO_MAECANDIDATOPATERNO, " ", DO_MAECANDIDATOMATERNO
	    	            ))).as(DD_NOMBRECANDIDATO)
	    	        .and(DO_MAECANDIDATODNI).as(DD_CANDIDATODNI)
	    	        .and(DD_N_VOTOS).as(DD_TOTALVOTOSVALIDOS)
	    	        .and(DD_N_LISTA).as(DD_LISTA),
	    	    Aggregation.sort(
					Sort.by(
						Sort.Order.desc(DD_TOTALVOTOSVALIDOS), 
						Sort.Order.asc(DD_POSICION), 
						Sort.Order.asc(DD_LISTA)
					)
				),
				Aggregation.project().andExclude(DD_POSICION)
	    	);
	    
	    AggregationResults<ParticipanteCandidatoDiputadoDto> results = this.mongoOperations.aggregate(aggregation, VwPrDiputados.class, ParticipanteCandidatoDiputadoDto.class);
	    return results.getMappedResults();
	}

	
	public List<ParticipanteCandidatoDiputadoDto> buscarCandidatosNombre(FiltroParticipanteNombreDiputadoDto filtro) {

		Document matchMaeCandidato = new Document(DO_MATCH,
				new Document("o_agrupacionPolitica.$id", filtro.getIdAgrupacionPolitica())
						.append("o_eleccion.$id", filtro.getIdEleccion())
						.append("o_distritoElectoral.$id", filtro.getIdDistritoElectoral())
		);

		Document lookupVw = new Document("$lookup",
				new Document("from", "vw_pr_diputados")
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
										.append("n_votos", "$c_detalle.c_candidato.n_total_votos")
										.append(DD_N_LISTA, "$c_detalle.c_candidato.n_lista")
								)
						))
						.append("as", "vwCandidato")
		);

		Document unwindVw = new Document(DO_UNWIND,
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
				.append(DD_IDAGRUPOLITICA, "$o_agrupacionPolitica.$id")
				.append(DD_NOMBREAGRUPOLITICA,
						new Document(DO_IFNULL, Arrays.asList("$agrupacion.c_descripcion", "")))
				.append(DD_CODIGOAGRUPOLITICA,
						new Document(DO_IFNULL, Arrays.asList("$agrupacion.c_codigo", "")))
				.append(DD_NOMBRECANDIDATO,
						new Document(DD_CONCAT, Arrays.asList(
								new Document(DO_IFNULL, Arrays.asList("$c_nombres", "")), " ",
								new Document(DO_IFNULL, Arrays.asList("$c_apellido_paterno", "")), " ",
								new Document(DO_IFNULL, Arrays.asList("$c_apellido_materno", ""))
						)))
				.append(DD_CANDIDATODNI, "$c_documento_identidad")
				.append(DD_TOTALVOTOSEMITIDOS, new Document(DO_IFNULL, Arrays.asList("$vwCandidato.n_votos", 0)))
				.append(DD_LISTA, new Document(DO_IFNULL, Arrays.asList("$vwCandidato.n_lista", "$n_lista")))
		);

		List<AggregationOperation> operations = new ArrayList<>(Arrays.asList(
				context -> matchMaeCandidato,
				context -> lookupVw,
				context -> unwindVw,
				context -> lookupAgrup,
				context -> unwindAgrup,
				context -> project
		));

		if (filtro.getNombreApellido() != null && !filtro.getNombreApellido().trim().isEmpty()) {
			Document matchNombre = new Document(DO_MATCH, new Document(DD_NOMBRECANDIDATO,
					new Document("$regex", filtro.getNombreApellido()).append("$options", "i")));
			operations.add(context -> matchNombre);
		}

		Document sort = new Document("$sort", new Document(DD_TOTALVOTOSEMITIDOS, -1).append(DD_LISTA, 1));
		operations.add(context -> sort);

		Aggregation agg = Aggregation.newAggregation(operations);

		AggregationResults<ParticipanteCandidatoDiputadoDto> results =
				this.mongoOperations.aggregate(agg, DO_MAECANDIDATO, ParticipanteCandidatoDiputadoDto.class);

		return results.getMappedResults();
	}
	
	public  List<ParticipanteDiputadoDto> buscarAgrupacionPoliticaNombre(FiltroParticipanteDiputadoDto filtroParticipanteNombreDto) {
	    Criteria criteriaFiltro = Criteria.where(DF_TIPOELECCION).is(filtroParticipanteNombreDto.getIdEleccion())
	        .and(DF_DISTRITOELECTORAL).is(filtroParticipanteNombreDto.getIdDistritoElectoral())
	        .and(DF_TIPOFILTRO).is(filtroParticipanteNombreDto.getTipoFiltro());
	    
	    if(filtroParticipanteNombreDto.getNombreApellidoPartido() != null) {
	    	Criteria criteriaBusca = Criteria.where("c_detalle.c_descripcion").regex(filtroParticipanteNombreDto.getNombreApellidoPartido(), "i")
	    			.and("c_detalle.n_grafico").is(1)
	    			.and(DF_DETALLENESTADO).ne(ConstantesComunes.OP_ESTADO_NOPARTICIPA);
	    	Aggregation aggregation = Aggregation.newAggregation(
		    	    Aggregation.match(criteriaFiltro),
		    	    Aggregation.unwind(DD_DETALLE),
		    	    Aggregation.match(criteriaBusca),
		    	    Aggregation.sort(Sort.by(Sort.Order.desc(DO_DETALLENVOTOS), Sort.Order.asc(DO_DETALLENPOSICION))),
		    	    Aggregation.project()
		    	    	.and(DO_DETALLENAGRUPACIONPOLITICA).as(DD_IDAGRUPOLITICA)
		    	    	.and(DO_DETALLECODIGO).as(DD_CODIGOAGRUPOLITICA)
		    	        .and(DO_DETALLEDESCRIPCION).as(DD_NOMBREAGRUPOLITICA)
		    	        .and(DO_DETALLENVOTOS).as(DD_TOTALVOTOSVALIDOS)
		    	        .and(DO_DETALLEPORCENTAJEVOTOSVALIDOS).as("porcentajeVotosValidos")
		    	        .and(DO_DETALLEPORCENTAJEVOTOSEMITIDOS).as("porcentajeVotosEmitidos")
		    	        .and(DO_DETALLENPOSICION).as(DD_POSICION)
		    	);
		    AggregationResults<ParticipanteDiputadoDto> results = this.mongoOperations.aggregate(aggregation, VwPrDiputados.class, ParticipanteDiputadoDto.class);
		    return results.getMappedResults();
	    } else {
	    	Criteria criteriaBusca = Criteria.where(DF_DETALLENESTADO).ne(ConstantesComunes.OP_ESTADO_NOPARTICIPA);
	    	Aggregation aggregation = Aggregation.newAggregation(
		    	    Aggregation.match(criteriaFiltro),
		    	    Aggregation.unwind(DD_DETALLE),
		    	    Aggregation.match(criteriaBusca),
		    	    Aggregation.sort(Sort.by(Sort.Order.desc(DO_DETALLENVOTOS), Sort.Order.asc(DO_DETALLENPOSICION))),
		    	    Aggregation.project()
		    	    	.and(DO_DETALLENAGRUPACIONPOLITICA).as(DD_IDAGRUPOLITICA)
		    	    	.and(DO_DETALLECODIGO).as(DD_CODIGOAGRUPOLITICA)
		    	    	.and(DO_DETALLEDESCRIPCION).as(DD_NOMBREAGRUPOLITICA)
		    	    	.and(DO_DETALLENVOTOS).as(DD_TOTALVOTOSVALIDOS)
		    	    	.and(DO_DETALLEPORCENTAJEVOTOSVALIDOS).as("porcentajeVotosValidos")
		    	    	.and(DO_DETALLEPORCENTAJEVOTOSEMITIDOS).as("porcentajeVotosEmitidos")
		    	    	.and(DO_DETALLENPOSICION).as(DD_POSICION)
		    	);
		    AggregationResults<ParticipanteDiputadoDto> results = this.mongoOperations.aggregate(aggregation, VwPrDiputados.class, ParticipanteDiputadoDto.class);
		    return results.getMappedResults();
	    }
	}
	
	public  List<ParticipanteCandidatoDiputadosReporteDto> participantesCandidatoReporte(FiltroEleccionDiputadosReporteDto filtroParticipanteCandidatoDto) {
	    Criteria criteriaFiltro = Criteria.where(DF_TIPOELECCION).is(filtroParticipanteCandidatoDto.getIdEleccion())
	        .and(DF_DISTRITOELECTORAL).is(filtroParticipanteCandidatoDto.getIdDistritoElectoral())
	        .and(DF_TIPOFILTRO).is(filtroParticipanteCandidatoDto.getTipoFiltro());
	    
	    Criteria criteriaFiltroOp = Criteria.where(DF_DETALLENESTADO).ne(ConstantesComunes.OP_ESTADO_NOPARTICIPA);
	    
	    Aggregation aggregation = Aggregation.newAggregation(
	    		Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind(DD_DETALLE),
		    	Aggregation.match(criteriaFiltroOp),
		    	Aggregation.unwind(DD_DETALLE_CANDIDATO),
		    	Aggregation.project()
		    	 	.and(DO_DETALLENAGRUPACIONPOLITICA).as(DD_IDAGRUPOLITICA)
		    	    .and(DO_DETALLEDESCRIPCION).as(DD_NOMBREAGRUPOLITICA)
		    	    .and(DO_DETALLECODIGO).as(DD_CODIGOAGRUPOLITICA)
		    	    .and(DO_DETALLECANDIDATOID).as(DD_N_CANDIDATO)
		    	    .and(DO_DETALLECANDIDATOLISTA).as(DD_N_LISTA)
		    	    .and(DO_DETALLECANDIDATOVOTOS).as(DD_N_VOTOS),
		    	Aggregation.lookup(DO_MAECANDIDATO, DD_N_CANDIDATO, "_id", DD_MAECANDIDATO1),
		    	Aggregation.unwind(DD_MAECANDIDATO, true),
		    	Aggregation.project()
		    		.and(DD_IDAGRUPOLITICA).as(DD_IDAGRUPOLITICA)
		    		.and(DD_CODIGOAGRUPOLITICA).as(DD_CODORGPOLITICA)
		    	    .and(context -> new Document(DD_CONCAT, Arrays.asList(
		    	            DO_MAECANDIDATONOMBRES, " ", DO_MAECANDIDATOPATERNO, " ", DO_MAECANDIDATOMATERNO
		    	        ))).as(DD_NOMBRECANDIDATO)
		    	    .and(DD_N_VOTOS).as(DD_TOTALVOTOS)
		    	    .and(DD_N_LISTA).as(DD_LISTA),
		    	Aggregation.sort(Sort.by(Sort.Order.desc(DD_TOTALVOTOS), Sort.Order.asc(DD_IDAGRUPOLITICA), Sort.Order.asc(DD_LISTA))),
		    	Aggregation.limit(30)
		    );
		     
		AggregationResults<ParticipanteCandidatoDiputadosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrDiputados.class, ParticipanteCandidatoDiputadosReporteDto.class);
		return results.getMappedResults();
	}
	
	public  List<ParticipanteCandidatoDiputadosReporteDto> participantesCandidatoOrganizacionReporte(FiltroEleccionDiputadosCandidatoReporteDto filtroParticipanteCandidatoNombreDto) {
	    Criteria criteriaFiltro = Criteria.where(DF_TIPOELECCION).is(filtroParticipanteCandidatoNombreDto.getIdEleccion())
	        .and(DF_DISTRITOELECTORAL).is(filtroParticipanteCandidatoNombreDto.getIdDistritoElectoral())
	        .and(DF_TIPOFILTRO).is(filtroParticipanteCandidatoNombreDto.getTipoFiltro());
	    Criteria criteriaFiltro1 = Criteria.where(DD_IDAGRUPOLITICA).is(filtroParticipanteCandidatoNombreDto.getIdAgrupacionPolitica());
	    
    	Aggregation aggregation = Aggregation.newAggregation(
    			Aggregation.match(criteriaFiltro),
		    	Aggregation.unwind(DD_DETALLE),
		    	Aggregation.unwind(DD_DETALLE_CANDIDATO),
		    	Aggregation.project()
		    	  	.and(DO_DETALLENAGRUPACIONPOLITICA).as(DD_IDAGRUPOLITICA)
		    	    .and(DO_DETALLEDESCRIPCION).as(DD_ORGPOLITICA)
		    	    .and(DO_DETALLECODIGO).as(DD_CODORGPOLITICA)
		    	    .and(DO_DETALLECANDIDATOID).as(DD_N_CANDIDATO)
		    	    .and(DO_DETALLECANDIDATOLISTA).as(DD_N_LISTA)
		    	    .and(DO_DETALLECANDIDATOVOTOS).as(DD_N_VOTOS),
		    	Aggregation.lookup(DO_MAECANDIDATO, DD_N_CANDIDATO, "_id", DD_MAECANDIDATO1),
		    	Aggregation.unwind(DD_MAECANDIDATO, true),
		    	Aggregation.project()
		    		.and(DD_IDAGRUPOLITICA).as(DD_IDAGRUPOLITICA)
		    	    .and(DD_ORGPOLITICA).as(DD_ORGPOLITICA)
		    	    .and(DD_CODORGPOLITICA).as(DD_CODORGPOLITICA)
		    	    .and(context -> new Document(DD_CONCAT, Arrays.asList(
		    	            DO_MAECANDIDATONOMBRES, " ", DO_MAECANDIDATOPATERNO, " ", DO_MAECANDIDATOMATERNO
		    	        ))).as(DD_NOMBRECANDIDATO)
		    	    .and(DD_N_VOTOS).as(DD_TOTALVOTOS)
		    	    .and(DD_N_LISTA).as(DD_LISTA),
		    	Aggregation.match(criteriaFiltro1),
		    	Aggregation.sort(Sort.by(Sort.Order.desc(DD_TOTALVOTOS), Sort.Order.asc(DD_LISTA)))
		    );
		     
		AggregationResults<ParticipanteCandidatoDiputadosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrDiputados.class, ParticipanteCandidatoDiputadosReporteDto.class);
		return results.getMappedResults();
	}
	
}
