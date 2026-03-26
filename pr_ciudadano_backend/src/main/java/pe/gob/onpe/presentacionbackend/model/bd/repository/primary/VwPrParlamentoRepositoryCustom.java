package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrParlamentoAndino;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.FiltroEleccionParlamentoReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.FiltroParticipanteParlamentoAndinoDto;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.ParticipanteCandidatoParlamentoAndinoDto;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.ParticipanteParlamentoReporteDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.springframework.data.mongodb.core.query.Query;

@Repository
public class VwPrParlamentoRepositoryCustom {

	public static final String PA_DETALLE_ESTADO = "detalle.estado";
	public static final String PA_TIPO_ELECCION = "tipoEleccion";
	public static final String PA_ID_CANDIDATO = "idCandidato";
	public static final String PA_N_CANDIDATO = "n_candidato";
	public static final String PA_TIPO_FILTRO = "tipoFiltro";
	public static final String PA_DETALLE_GRAFICO = "detalle.grafico";
	public static final String PA_AMBITO_GEOGRAFICO = "ambitoGeografico";
	public static final String PA_UBIGEO_NIVEL_01 = "ubigeoNivel01";
	public static final String PA_UBIGEO_NIVEL_02 = "ubigeoNivel02";
	public static final String PA_UBIGEO_NIVEL_03 = "ubigeoNivel03";
	public static final String PA_D_DETALLE = "$detalle";
	public static final String PA_D_DETALLE_CANDIDATO = "$detalle.candidato";
	public static final String PA_D_DETALLE_DESCRIPCION = "$detalle.descripcion";
	public static final String PA_NOMBRE_AGRUPACION_POLITICA = "nombreAgrupacionPolitica";
	public static final String PA_CODIGO_AGRUPACION_POLITICA = "codigoAgrupacionPolitica";
	public static final String PA_POSICION = "posicion";
	public static final String PA_TOTAL_VOTOS_EMITIDOS = "totalVotosEmitidos";
	public static final String PA_LISTA = "lista";
	public static final String PA_D_DETALLE_CANDIDATO_ID = "$detalle.candidato.id";
	public static final String PA_D_DETALLE_CANDIDATO_VOTOS = "$detalle.candidato.votos";
	public static final String PA_N_TOTAL_VOTOS = "n_total_votos";
	public static final String PA_D_DETALLE_CANDIDATO_LISTA = "$detalle.candidato.lista";
	public static final String PA_N_LISTA = "n_lista";
	public static final String PA_MAE_CANDIDATO = "mae_candidato";
	public static final String PA_D_MAE_CANDIDATO = "$MaeCandidato";
	public static final String PA_MAE_CANDIDATO1 = "MaeCandidato";
	public static final String PA_D_CONCAT = "$concat";
	public static final String PA_D_MAE_CANDIDATO_C_NOMBRES = "$MaeCandidato.c_nombres";
	public static final String PA_D_MAE_CANDIDATO_C_APELLIDO_PATERNO = "$MaeCandidato.c_apellido_paterno";
	public static final String PA_D_MAE_CANDIDATO_C_APELLIDO_MATERNO = "$MaeCandidato.c_apellido_materno";
	public static final String PA_NOMBRE_CANDIDATO = "nombreCandidato";
	public static final String PA_TOTAL_VOTOS_VALIDOS = "totalVotosValidos";
	public static final String PA_TOTAL_VOTOS = "totalVotos";
	public static final String PA_COD_ORG_POLITICA = "codOrgPolitica";
	private MongoOperations mongoOperations;

	public VwPrParlamentoRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}
	
	public Page<ParticipanteCandidatoParlamentoAndinoDto> buscarCandidatosGraficoPaginado(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto, int numeroPagina, int tamanioPagina) {

		Criteria filterCriteria = Criteria.where(PA_TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(PA_TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(PA_DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(PA_AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}


	    Pageable pageable = PageRequest.of(numeroPagina, tamanioPagina);

	    // Agregación para obtener el total de elementos
	    Aggregation totalAggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind(PA_D_DETALLE),
	        Aggregation.unwind(PA_D_DETALLE_CANDIDATO),
	        Aggregation.project()
	            .and("detalle.descripcion").as(PA_NOMBRE_AGRUPACION_POLITICA)
	            .and("detalle.codigo").as(PA_CODIGO_AGRUPACION_POLITICA)
	            .and("detalle.candidato.votos").as(PA_TOTAL_VOTOS_EMITIDOS)
	            .and("detalle.candidato.lista").as(PA_LISTA)
				.and("detalle.candidato.id").as(PA_ID_CANDIDATO),
			Aggregation.sort(Sort.Direction.DESC,PA_TOTAL_VOTOS_EMITIDOS)
	    );
	    AggregationResults<ParticipanteCandidatoParlamentoAndinoDto> totalResults = this.mongoOperations.aggregate(totalAggregation, VwPrParlamentoAndino.class, ParticipanteCandidatoParlamentoAndinoDto.class);
	    long total = totalResults.getMappedResults().size();

	    // Agregación para obtener los resultados paginados
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind(PA_D_DETALLE),
	        Aggregation.unwind(PA_D_DETALLE_CANDIDATO),

	        Aggregation.project()
	            .and("detalle.descripcion").as(PA_NOMBRE_AGRUPACION_POLITICA)
	            .and("detalle.codigo").as(PA_CODIGO_AGRUPACION_POLITICA)

	            .and("detalle.candidato.votos").as(PA_TOTAL_VOTOS_EMITIDOS)
	            .and("detalle.candidato.lista").as(PA_LISTA)
				.and("detalle.candidato.id").as(PA_ID_CANDIDATO),
			Aggregation.sort(Sort.Direction.DESC,PA_TOTAL_VOTOS_EMITIDOS),
	        Aggregation.skip((long)pageable.getPageNumber() * pageable.getPageSize()),
	        Aggregation.limit(pageable.getPageSize())
	    );


	    AggregationResults<ParticipanteCandidatoParlamentoAndinoDto> results = this.mongoOperations.aggregate(aggregation, VwPrParlamentoAndino.class, ParticipanteCandidatoParlamentoAndinoDto.class);
	    List<ParticipanteCandidatoParlamentoAndinoDto> mappedResults = results.getMappedResults();

	    return new PageImpl<>(mappedResults, pageable, total);
	}


	public List<ParticipanteCandidatoParlamentoAndinoDto> buscarCandidatosGraficoAll(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(PA_TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(PA_TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(PA_DETALLE_ESTADO).is(1)
				.and(PA_DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(PA_AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(PA_D_DETALLE),
				Aggregation.unwind(PA_D_DETALLE_CANDIDATO),
				Aggregation.project()
						.and(PA_D_DETALLE_DESCRIPCION).as(PA_NOMBRE_AGRUPACION_POLITICA)
						.and("$detalle.codigo").as(PA_CODIGO_AGRUPACION_POLITICA)
						.and("$detalle.posicion").as(PA_POSICION)
						.and(PA_D_DETALLE_CANDIDATO_ID).as(PA_N_CANDIDATO)
						.and(PA_D_DETALLE_CANDIDATO_VOTOS).as(PA_N_TOTAL_VOTOS)
						.and(PA_D_DETALLE_CANDIDATO_LISTA).as(PA_N_LISTA)
				// Otros campos que desees mantener
				,
				Aggregation.lookup(PA_MAE_CANDIDATO, PA_N_CANDIDATO, "_id", PA_MAE_CANDIDATO1),
				Aggregation.unwind(PA_D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(PA_POSICION).as(PA_POSICION)
						.and(PA_NOMBRE_AGRUPACION_POLITICA).as(PA_NOMBRE_AGRUPACION_POLITICA)
						.and(PA_CODIGO_AGRUPACION_POLITICA).as(PA_CODIGO_AGRUPACION_POLITICA)
						.and(context -> new Document(PA_D_CONCAT, Arrays.asList(
								PA_D_MAE_CANDIDATO_C_NOMBRES, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as(PA_NOMBRE_CANDIDATO)
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and(PA_N_TOTAL_VOTOS).as(PA_TOTAL_VOTOS_VALIDOS)
						.and(PA_N_LISTA).as(PA_LISTA),
				Aggregation.sort(
					Sort.by(
						Sort.Order.desc(PA_TOTAL_VOTOS_VALIDOS),
						Sort.Order.asc(PA_POSICION),
						Sort.Order.asc(PA_LISTA)
					)
				),
				Aggregation.project().andExclude(PA_POSICION)
		);

		AggregationResults<ParticipanteCandidatoParlamentoAndinoDto> results = this.mongoOperations.aggregate(aggregation, VwPrParlamentoAndino.class, ParticipanteCandidatoParlamentoAndinoDto.class);

        return results.getMappedResults();
	}

	public List<ParticipanteCandidatoParlamentoAndinoDto> buscarCandidatosAgrupacionPoliticaNombre(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {
		Criteria filterCriteria = buildFilterCriteria(filtroParticipanteDto);

		Criteria criteriaFiltroAdicional = Criteria.where(PA_CODIGO_AGRUPACION_POLITICA)
				.is(filtroParticipanteDto.getIdAgrupacionPolitica());

		if (filtroParticipanteDto.getNombreCandidato() != null && !filtroParticipanteDto.getNombreCandidato().isEmpty()) {
			criteriaFiltroAdicional.and(PA_NOMBRE_CANDIDATO).regex(filtroParticipanteDto.getNombreCandidato(), "i");
		}

		Aggregation aggregationOriginal = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(PA_D_DETALLE),
				Aggregation.unwind(PA_D_DETALLE_CANDIDATO),
				Aggregation.project()
						.and(PA_D_DETALLE_DESCRIPCION).as(PA_NOMBRE_AGRUPACION_POLITICA)
						.and("$detalle.agrupacionPolitica").as(PA_CODIGO_AGRUPACION_POLITICA)
						.and(PA_D_DETALLE_CANDIDATO_ID).as(PA_ID_CANDIDATO)
						.and(PA_D_DETALLE_CANDIDATO_VOTOS).as(PA_TOTAL_VOTOS_VALIDOS)
						.and(PA_D_DETALLE_CANDIDATO_LISTA).as(PA_LISTA),
				Aggregation.lookup(PA_MAE_CANDIDATO, PA_ID_CANDIDATO, "_id", PA_MAE_CANDIDATO1),
				Aggregation.unwind(PA_D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(PA_NOMBRE_AGRUPACION_POLITICA).as(PA_NOMBRE_AGRUPACION_POLITICA)
						.and(PA_CODIGO_AGRUPACION_POLITICA).as(PA_CODIGO_AGRUPACION_POLITICA)
						.and(context -> new Document(PA_D_CONCAT, Arrays.asList(
								PA_D_MAE_CANDIDATO_C_NOMBRES, " ",
								PA_D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ",
								PA_D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as(PA_NOMBRE_CANDIDATO)
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and(PA_TOTAL_VOTOS_VALIDOS).as(PA_TOTAL_VOTOS_VALIDOS)
						.and(PA_LISTA).as(PA_LISTA)
						.and(PA_ID_CANDIDATO).as(PA_ID_CANDIDATO),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC, PA_TOTAL_VOTOS_VALIDOS)
		);

		List<ParticipanteCandidatoParlamentoAndinoDto> listaConVotos =
				new ArrayList<>(mongoOperations.aggregate(
						aggregationOriginal,
						VwPrParlamentoAndino.class,
						ParticipanteCandidatoParlamentoAndinoDto.class
				).getMappedResults());

		String nombreAgrupacionPolitica = null;
		if (!listaConVotos.isEmpty()) {
			nombreAgrupacionPolitica = listaConVotos.get(0).getNombreAgrupacionPolitica();
		} else {
			Query qAgr = new Query(Criteria.where("_id").is(filtroParticipanteDto.getIdAgrupacionPolitica()));
			Document docAgr = mongoOperations.findOne(qAgr, Document.class, "mae_agrupacion_politica");
			if (docAgr != null) {
				nombreAgrupacionPolitica = docAgr.getString("c_descripcion");
			}
		}

		Set<Integer> idsConVoto = listaConVotos.stream()
				.map(ParticipanteCandidatoParlamentoAndinoDto::getIdCandidato)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Query q = new Query();
		
		Criteria criteriaBase = Criteria.where("o_eleccion.$id").is(filtroParticipanteDto.getIdEleccion())
				.and("o_agrupacionPolitica.$id").is(filtroParticipanteDto.getIdAgrupacionPolitica());
		
		if (!idsConVoto.isEmpty()) {
			criteriaBase.and("_id").nin(idsConVoto);
		}
		
		q.addCriteria(criteriaBase);

		if (filtroParticipanteDto.getNombreCandidato() != null && !filtroParticipanteDto.getNombreCandidato().isEmpty()) {
			Criteria criteriaNombreMae = new Criteria().orOperator(
					Criteria.where("c_nombres").regex(filtroParticipanteDto.getNombreCandidato(), "i"),
					Criteria.where("c_apellido_paterno").regex(filtroParticipanteDto.getNombreCandidato(), "i"),
					Criteria.where("c_apellido_materno").regex(filtroParticipanteDto.getNombreCandidato(), "i")
			);
			q.addCriteria(criteriaNombreMae);
		}

		List<Document> candidatosMae = mongoOperations.find(q, Document.class, PA_MAE_CANDIDATO);

		for (Document doc : candidatosMae) {
			Integer idCand = doc.getInteger("_id");

			if (!idsConVoto.contains(idCand)) {
				ParticipanteCandidatoParlamentoAndinoDto dto = ParticipanteCandidatoParlamentoAndinoDto.builder().build();

				dto.setNombreAgrupacionPolitica(nombreAgrupacionPolitica);

				dto.setCodigoAgrupacionPolitica(filtroParticipanteDto.getIdAgrupacionPolitica());

				String nombre = doc.getString("c_nombres") + " " +
								doc.getString("c_apellido_paterno") + " " +
								doc.getString("c_apellido_materno");

				dto.setNombreCandidato(nombre);
				dto.setDniCandidato(doc.getString("c_documento_identidad"));
				dto.setLista(doc.getInteger(PA_N_LISTA, 0));
				dto.setTotalVotosValidos(0);
				dto.setIdCandidato(idCand);

				listaConVotos.add(dto);
			}
		}

		return listaConVotos.stream()
				.sorted(Comparator.comparing(ParticipanteCandidatoParlamentoAndinoDto::getTotalVotosValidos)
						.reversed()
						.thenComparing(ParticipanteCandidatoParlamentoAndinoDto::getLista))
				.toList();
	}

	private Criteria buildFilterCriteria(FiltroParticipanteParlamentoAndinoDto filtro) {

		Criteria criteria = Criteria.where(PA_TIPO_ELECCION).is(filtro.getIdEleccion())
				.and(PA_TIPO_FILTRO).is(filtro.getTipoFiltro())
				.and(PA_DETALLE_ESTADO).is(1)
				.and(PA_DETALLE_GRAFICO).is(1);

		if (filtro.getIdAmbitoGeografico() != null && filtro.getIdAmbitoGeografico() != 0) {
			criteria.and(PA_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}

		if (filtro.getUbigeoNivel1() != null && filtro.getUbigeoNivel1() != 0) {
			criteria.and(PA_UBIGEO_NIVEL_01).is(filtro.getUbigeoNivel1());
		}

		if (filtro.getUbigeoNivel2() != null && filtro.getUbigeoNivel2() != 0) {
			criteria.and(PA_UBIGEO_NIVEL_02).is(filtro.getUbigeoNivel2());
		}

		if (filtro.getUbigeoNivel3() != null && filtro.getUbigeoNivel3() != 0) {
			criteria.and(PA_UBIGEO_NIVEL_03).is(filtro.getUbigeoNivel3());
		}

		return criteria;
	}

	public List<ParticipanteParlamentoReporteDto> buscarCandidatosGraficoAllReporte(FiltroEleccionParlamentoReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(PA_TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(PA_TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(PA_DETALLE_ESTADO).is(1)
				.and(PA_DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(PA_AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(PA_D_DETALLE),
				Aggregation.unwind(PA_D_DETALLE_CANDIDATO),
				Aggregation.project()

						.and("$detalle.cCodigo").as(PA_COD_ORG_POLITICA)
						.and(PA_D_DETALLE_CANDIDATO_ID).as(PA_N_CANDIDATO)
						.and(PA_D_DETALLE_CANDIDATO_VOTOS).as(PA_N_TOTAL_VOTOS)
						.and(PA_D_DETALLE_CANDIDATO_LISTA).as(PA_N_LISTA)
				// Otros campos que desees mantener
				,
				Aggregation.lookup(PA_MAE_CANDIDATO, PA_N_CANDIDATO, "_id", PA_MAE_CANDIDATO1),
				Aggregation.unwind(PA_D_MAE_CANDIDATO, true),
				Aggregation.project()

						.and(PA_COD_ORG_POLITICA).as(PA_COD_ORG_POLITICA)
						.and(context -> new Document(PA_D_CONCAT, Arrays.asList(
								PA_D_MAE_CANDIDATO_C_NOMBRES, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as("candidato")
						.and(PA_N_TOTAL_VOTOS).as(PA_TOTAL_VOTOS)
						.and(PA_N_LISTA).as(PA_LISTA),
				Aggregation.sort(Sort.Direction.DESC,PA_TOTAL_VOTOS)
		);

		AggregationResults<ParticipanteParlamentoReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrParlamentoAndino.class, ParticipanteParlamentoReporteDto.class);

		return results.getMappedResults();
	}

	public List<ParticipanteParlamentoReporteDto> buscarCandidatosAgrupacionPoliticaNombreReporte(FiltroEleccionParlamentoReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(PA_TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(PA_TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(PA_DETALLE_ESTADO).is(1)
				.and(PA_DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(PA_AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(PA_UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		Criteria criteriaFiltroAdicional = Criteria.where(PA_COD_ORG_POLITICA).is(filtroParticipanteDto.getIdOrgPolitica());


		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(PA_D_DETALLE),
				Aggregation.unwind(PA_D_DETALLE_CANDIDATO),
				Aggregation.project()

						.and("$detalle.nAgrupacionPolitica").as(PA_CODIGO_AGRUPACION_POLITICA)
						.and(PA_D_DETALLE_CANDIDATO_ID).as(PA_N_CANDIDATO)
						.and(PA_D_DETALLE_CANDIDATO_VOTOS).as(PA_N_TOTAL_VOTOS)
						.and(PA_D_DETALLE_CANDIDATO_LISTA).as(PA_N_LISTA),
				// match con mae_candidato
				Aggregation.lookup(PA_MAE_CANDIDATO, PA_N_CANDIDATO, "_id", PA_MAE_CANDIDATO1),
				Aggregation.unwind(PA_D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(PA_CODIGO_AGRUPACION_POLITICA).as(PA_COD_ORG_POLITICA)
						.and(context -> new Document(PA_D_CONCAT, Arrays.asList(
								PA_D_MAE_CANDIDATO_C_NOMBRES, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", PA_D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as("candidato")

						.and(PA_N_TOTAL_VOTOS).as(PA_TOTAL_VOTOS)
						.and(PA_N_LISTA).as(PA_LISTA),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC,PA_TOTAL_VOTOS)
		);

		AggregationResults<ParticipanteParlamentoReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrParlamentoAndino.class, ParticipanteParlamentoReporteDto.class);

		return results.getMappedResults();
	}



}
