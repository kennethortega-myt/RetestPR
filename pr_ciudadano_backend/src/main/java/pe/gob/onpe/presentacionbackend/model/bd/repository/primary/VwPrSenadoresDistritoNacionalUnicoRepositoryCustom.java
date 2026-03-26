package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.bson.Document;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.FiltroEleccionSenadoresUnicosReporteDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.FiltroParticipanteSenadoresUnicosDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.ParticipanteCandidatoSenadoresUnicosDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.ParticipanteSenadoresUnicosReporteDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.springframework.data.mongodb.core.query.Query;


@Repository
public class VwPrSenadoresDistritoNacionalUnicoRepositoryCustom {

	public static final String TIPO_ELECCION = "tipoEleccion";
	public static final String TIPO_FILTRO = "tipoFiltro";
	public static final String DETALLE_GRAFICO = "detalle.grafico";
	public static final String AMBITO_GEOGRAFICO = "ambitoGeografico";
	public static final String UBIGEO_NIVEL_01 = "ubigeoNivel01";
	public static final String UBIGEO_NIVEL_02 = "ubigeoNivel02";
	public static final String UBIGEO_NIVEL_03 = "ubigeoNivel03";
	public static final String D_DETALLE = "$detalle";
	public static final String D_DETALLE_CANDIDATO = "$detalle.candidato";
	public static final String NOMBRE_AGRUPACION_POLITICA = "nombreAgrupacionPolitica";
	public static final String CODIGO_AGRUPACION_POLITICA = "codigoAgrupacionPolitica";
	public static final String TOTAL_VOTOS_EMITIDOS = "totalVotosEmitidos";
	public static final String LISTA = "lista";
	public static final String ID_CANDIDATO = "idCandidato";
	public static final String N_CANDIDATO = "n_candidato";
	public static final String D_DETALLE_CANDIDATO_ID = "$detalle.candidato.id";
	public static final String N_TOTAL_VOTOS = "n_total_votos";
	public static final String LISTA1 = "n_lista";
	public static final String D_DETALLE_CANDIDATO_VOTOS = "$detalle.candidato.votos";
	public static final String D_DETALLE_CANDIDATO_LISTA = "$detalle.candidato.lista";
	public static final String MAE_CANDIDATO = "MaeCandidato";
	public static final String MAE_CANDIDATO1 = "mae_candidato";
	public static final String D_MAE_CANDIDATO = "$MaeCandidato";
	public static final String D_CONCAT = "$concat";
	public static final String D_MAE_CANDIDATO_C_APELLIDO_PATERNO = "$MaeCandidato.c_apellido_paterno";
	public static final String D_MAE_CANDIDATO_C_NOMBRES = "$MaeCandidato.c_nombres";
	public static final String NOMBRE_CANDIDATO = "nombreCandidato";
	public static final String TOTAL_VOTOS_VALIDOS = "totalVotosValidos";
	public static final String COD_ORG_POLITICA = "codOrgPolitica";
	public static final String TOTAL_VOTOS = "totalVotos";
	public static final String D_MAE_CANDIDATO_C_APELLIDO_MATERNO = "$MaeCandidato.c_apellido_materno";
	public static final String D_POSICION = "posicion";
	private MongoOperations mongoOperations;

	public VwPrSenadoresDistritoNacionalUnicoRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}
	
	public Page<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosGraficoPaginado(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto, int numeroPagina, int tamanioPagina) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}


	    Pageable pageable = PageRequest.of(numeroPagina, tamanioPagina);

	    // Agregación para obtener el total de elementos
	    Aggregation totalAggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind(D_DETALLE),
	        Aggregation.unwind(D_DETALLE_CANDIDATO),
	        Aggregation.project()
	            .and("detalle.descripcion").as(NOMBRE_AGRUPACION_POLITICA)
	            .and("detalle.cCodigo").as(CODIGO_AGRUPACION_POLITICA)
	            .and("detalle.candidato.votos").as(TOTAL_VOTOS_EMITIDOS)
	            .and("detalle.candidato.lista").as(LISTA)
				.and("detalle.candidato.id").as(ID_CANDIDATO),
			Aggregation.sort(Sort.Direction.DESC,TOTAL_VOTOS_EMITIDOS)
	    );
	    AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> totalResults = this.mongoOperations.aggregate(totalAggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);
	    long total = totalResults.getMappedResults().size();

	    // Agregación para obtener los resultados paginados
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(filterCriteria),
	        Aggregation.unwind(D_DETALLE),
	        Aggregation.unwind(D_DETALLE_CANDIDATO),
	        Aggregation.project()
	            .and("detalle.descripcion").as(NOMBRE_AGRUPACION_POLITICA)
	            .and("detalle.cCodigo").as(CODIGO_AGRUPACION_POLITICA)
	            .and("detalle.candidato.votos").as(TOTAL_VOTOS_EMITIDOS)
	            .and("detalle.candidato.lista").as(LISTA)
				.and("detalle.candidato.id").as(ID_CANDIDATO),
			Aggregation.sort(Sort.Direction.DESC,TOTAL_VOTOS_EMITIDOS),
	        Aggregation.skip((long)pageable.getPageNumber() * pageable.getPageSize()),
	        Aggregation.limit(pageable.getPageSize())
	    );


	    AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);
	    List<ParticipanteCandidatoSenadoresUnicosDto> mappedResults = results.getMappedResults();

	    return new PageImpl<>(mappedResults, pageable, total);
	}


	public List<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosGraficoAll(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(D_DETALLE),
				Aggregation.unwind(D_DETALLE_CANDIDATO),
				Aggregation.project()
						.and("$detalle.descripcion").as(NOMBRE_AGRUPACION_POLITICA)
						.and("$detalle.codigo").as(CODIGO_AGRUPACION_POLITICA)
						.and("$detalle.posicion").as(D_POSICION)
						.and(D_DETALLE_CANDIDATO_ID).as(N_CANDIDATO)
						.and(D_DETALLE_CANDIDATO_VOTOS).as(N_TOTAL_VOTOS)
						.and(D_DETALLE_CANDIDATO_LISTA).as(LISTA1)
				// Otros campos que desees mantener
				,
				Aggregation.lookup(MAE_CANDIDATO1, N_CANDIDATO, "_id", MAE_CANDIDATO),
				Aggregation.unwind(D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(D_POSICION).as(D_POSICION)
						.and(NOMBRE_AGRUPACION_POLITICA).as(NOMBRE_AGRUPACION_POLITICA)
						.and(CODIGO_AGRUPACION_POLITICA).as(CODIGO_AGRUPACION_POLITICA)
						.and(context -> new Document(D_CONCAT, Arrays.asList(
								D_MAE_CANDIDATO_C_NOMBRES, " ", D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as(NOMBRE_CANDIDATO)
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and(N_TOTAL_VOTOS).as(TOTAL_VOTOS_VALIDOS)
						.and(LISTA1).as(LISTA),
				Aggregation.sort(
					Sort.by(
						Sort.Order.desc(TOTAL_VOTOS_VALIDOS),
						Sort.Order.asc(D_POSICION),
						Sort.Order.asc(LISTA)
					)
				),
				Aggregation.project().andExclude(D_POSICION)
		);

		AggregationResults<ParticipanteCandidatoSenadoresUnicosDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteCandidatoSenadoresUnicosDto.class);

        return results.getMappedResults();
	}

	public List<ParticipanteCandidatoSenadoresUnicosDto> buscarCandidatosAgrupacionPoliticaNombre(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {

		Criteria filterCriteria = construirCriteriaBase(filtroParticipanteDto);

		Criteria criteriaFiltroAdicional = Criteria.where(CODIGO_AGRUPACION_POLITICA)
				.is(filtroParticipanteDto.getIdAgrupacionPolitica());

		if (filtroParticipanteDto.getNombreCandidato() != null && !filtroParticipanteDto.getNombreCandidato().isEmpty()) {
			criteriaFiltroAdicional.and(NOMBRE_CANDIDATO).regex(filtroParticipanteDto.getNombreCandidato(), "i");
		}

		Aggregation aggregationOriginal = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(D_DETALLE),
				Aggregation.unwind(D_DETALLE_CANDIDATO),
				Aggregation.project()
						.and("$detalle.descripcion").as(NOMBRE_AGRUPACION_POLITICA)
						.and("$detalle.agrupacionPolitica").as(CODIGO_AGRUPACION_POLITICA)
						.and(D_DETALLE_CANDIDATO_ID).as(ID_CANDIDATO)
						.and(D_DETALLE_CANDIDATO_VOTOS).as(TOTAL_VOTOS_VALIDOS)
						.and(D_DETALLE_CANDIDATO_LISTA).as(LISTA),
				Aggregation.lookup(MAE_CANDIDATO1, ID_CANDIDATO, "_id", MAE_CANDIDATO),
				Aggregation.unwind(D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(NOMBRE_AGRUPACION_POLITICA).as(NOMBRE_AGRUPACION_POLITICA)
						.and(CODIGO_AGRUPACION_POLITICA).as(CODIGO_AGRUPACION_POLITICA)
						.and(context -> new Document(D_CONCAT, Arrays.asList(
								D_MAE_CANDIDATO_C_NOMBRES, " ",
								D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ",
								D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as(NOMBRE_CANDIDATO)
						.and("MaeCandidato.c_documento_identidad").as("dniCandidato")
						.and(TOTAL_VOTOS_VALIDOS).as(TOTAL_VOTOS_VALIDOS)
						.and(LISTA).as(LISTA)
						.and(ID_CANDIDATO).as(ID_CANDIDATO),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC, TOTAL_VOTOS_VALIDOS)
		);

		List<ParticipanteCandidatoSenadoresUnicosDto> listaConVotos =
				new ArrayList<>(mongoOperations.aggregate(
						aggregationOriginal,
						VwPrSenadoresDistritoNacionalUnico.class,
						ParticipanteCandidatoSenadoresUnicosDto.class
				).getMappedResults());

		Set<Integer> idsConVoto = listaConVotos.stream()
				.map(ParticipanteCandidatoSenadoresUnicosDto::getIdCandidato)
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

		List<Document> candidatosMae = mongoOperations.find(q, Document.class, MAE_CANDIDATO1);

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

		for (Document doc : candidatosMae) {
			Integer idCand = doc.getInteger("_id");

			if (!idsConVoto.contains(idCand)) {
				ParticipanteCandidatoSenadoresUnicosDto dto = ParticipanteCandidatoSenadoresUnicosDto.builder().build();

				dto.setNombreAgrupacionPolitica(nombreAgrupacionPolitica);

				dto.setCodigoAgrupacionPolitica(filtroParticipanteDto.getIdAgrupacionPolitica());

				String nombre = doc.getString("c_nombres") + " " +
								doc.getString("c_apellido_paterno") + " " +
								doc.getString("c_apellido_materno");

				dto.setNombreCandidato(nombre);
				dto.setDniCandidato(doc.getString("c_documento_identidad"));
				dto.setLista(doc.getInteger(LISTA1, 0));
				dto.setTotalVotosValidos(0);
				dto.setIdCandidato(idCand);

				listaConVotos.add(dto);
			}
		}

		return listaConVotos.stream()
				.sorted(Comparator.comparing(ParticipanteCandidatoSenadoresUnicosDto::getTotalVotosValidos)
						.reversed()
						.thenComparing(ParticipanteCandidatoSenadoresUnicosDto::getLista))
				.toList();
	}

	private Criteria construirCriteriaBase(FiltroParticipanteSenadoresUnicosDto filtro) {
		Criteria criteria = Criteria.where(TIPO_ELECCION).is(filtro.getIdEleccion())
				.and(TIPO_FILTRO).is(filtro.getTipoFiltro())
				.and(DETALLE_GRAFICO).is(1);

		if (esValido(filtro.getIdAmbitoGeografico())) {
			criteria.and(AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}
		if (esValido(filtro.getUbigeoNivel1())) {
			criteria.and(UBIGEO_NIVEL_01).is(filtro.getUbigeoNivel1());
		}
		if (esValido(filtro.getUbigeoNivel2())) {
			criteria.and(UBIGEO_NIVEL_02).is(filtro.getUbigeoNivel2());
		}
		if (esValido(filtro.getUbigeoNivel3())) {
			criteria.and(UBIGEO_NIVEL_03).is(filtro.getUbigeoNivel3());
		}

		return criteria;
	}

	private boolean esValido(Integer valor) {
		return valor != null && valor != 0;
	}

	//reporte
	public List<ParticipanteSenadoresUnicosReporteDto> buscarCandidatosGraficoAllReporte(FiltroEleccionSenadoresUnicosReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(D_DETALLE),
				Aggregation.unwind(D_DETALLE_CANDIDATO),
				Aggregation.project()

						.and("$detalle.cCodigo").as(COD_ORG_POLITICA)
						.and(D_DETALLE_CANDIDATO_ID).as(N_CANDIDATO)
						.and(D_DETALLE_CANDIDATO_VOTOS).as(N_TOTAL_VOTOS)
						.and(D_DETALLE_CANDIDATO_LISTA).as(LISTA1)
				// Otros campos que desees mantener
				,
				Aggregation.lookup(MAE_CANDIDATO1, N_CANDIDATO, "_id", MAE_CANDIDATO),
				Aggregation.unwind(D_MAE_CANDIDATO, true),
				Aggregation.project()

						.and(COD_ORG_POLITICA).as(COD_ORG_POLITICA)
						.and(context -> new Document(D_CONCAT, Arrays.asList(
								D_MAE_CANDIDATO_C_NOMBRES, " ", D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as("candidato")
						.and(N_TOTAL_VOTOS).as(TOTAL_VOTOS)
						.and(LISTA1).as(LISTA),
				Aggregation.sort(Sort.Direction.ASC,COD_ORG_POLITICA),
				Aggregation.sort(Sort.Direction.ASC,LISTA),
				Aggregation.sort(Sort.Direction.DESC,TOTAL_VOTOS)
		);

		AggregationResults<ParticipanteSenadoresUnicosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteSenadoresUnicosReporteDto.class);

		return results.getMappedResults();
	}

	public List<ParticipanteSenadoresUnicosReporteDto> buscarCandidatosAgrupacionPoliticaNombreReporte(FiltroEleccionSenadoresUnicosReporteDto filtroParticipanteDto) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtroParticipanteDto.getIdEleccion())
				.and(TIPO_FILTRO).is(filtroParticipanteDto.getTipoFiltro())
				.and(DETALLE_GRAFICO).is(1);

		if(filtroParticipanteDto.getIdAmbitoGeografico() != null && filtroParticipanteDto.getIdAmbitoGeografico() != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtroParticipanteDto.getIdAmbitoGeografico());
		}
		if(filtroParticipanteDto.getUbigeoNivel1() != null && filtroParticipanteDto.getUbigeoNivel1() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtroParticipanteDto.getUbigeoNivel1());
		}
		if(filtroParticipanteDto.getUbigeoNivel2() != null && filtroParticipanteDto.getUbigeoNivel2() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtroParticipanteDto.getUbigeoNivel2());
		}
		if(filtroParticipanteDto.getUbigeoNivel3() != null && filtroParticipanteDto.getUbigeoNivel3() != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtroParticipanteDto.getUbigeoNivel3());
		}

		Criteria criteriaFiltroAdicional = Criteria.where(COD_ORG_POLITICA).is(filtroParticipanteDto.getIdOrgPolitica());


		// Agregación para obtener los resultados
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.unwind(D_DETALLE),
				Aggregation.unwind(D_DETALLE_CANDIDATO),
				Aggregation.project()

						.and("$detalle.nAgrupacionPolitica").as(CODIGO_AGRUPACION_POLITICA)
						.and(D_DETALLE_CANDIDATO_ID).as(N_CANDIDATO)
						.and(D_DETALLE_CANDIDATO_VOTOS).as(N_TOTAL_VOTOS)
						.and(D_DETALLE_CANDIDATO_LISTA).as(LISTA1),

				Aggregation.lookup(MAE_CANDIDATO1, N_CANDIDATO, "_id", MAE_CANDIDATO),
				Aggregation.unwind(D_MAE_CANDIDATO, true),
				Aggregation.project()
						.and(CODIGO_AGRUPACION_POLITICA).as(COD_ORG_POLITICA)
						.and(context -> new Document(D_CONCAT, Arrays.asList(
								D_MAE_CANDIDATO_C_NOMBRES, " ", D_MAE_CANDIDATO_C_APELLIDO_PATERNO, " ", D_MAE_CANDIDATO_C_APELLIDO_MATERNO
						))).as("candidato")

						.and(N_TOTAL_VOTOS).as(TOTAL_VOTOS)
						.and(LISTA1).as(LISTA),
				Aggregation.match(criteriaFiltroAdicional),
				Aggregation.sort(Sort.Direction.DESC,TOTAL_VOTOS)
		);

		AggregationResults<ParticipanteSenadoresUnicosReporteDto> results = this.mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, ParticipanteSenadoresUnicosReporteDto.class);

		return results.getMappedResults();
	}

}
