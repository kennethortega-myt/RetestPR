package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.*;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.*;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import java.util.*;

@Repository
public class MaeResumenGeneralRepositoryCustom {

    public static final String PORCENTAJE_ACTAS_CONTABILIZADAS = "porcentajeActasContabilizadas";
	public static final String ACTAS_CONTABILIZADAS = "actasContabilizadas";
	public static final String TOTAL_ACTAS = "totalActas";
	public static final String PORCENTAJE_PARTICIPACION_CIUDADANA = "porcentajeParticipacionCiudadana";
	public static final String PORCENTAJE_ACTAS_OBSERVADAS_ENVIADAS = "porcentajeActasObservadasEnviadas";
	public static final String ACTAS_OBSERVADAS_ENVIADAS = "actasObservadasEnviadas";
	public static final String PORCENTAJE_ACTAS_PENDIENTES = "porcentajeActasPendientes";
	public static final String ACTAS_PENDIENTES = "actasPendientes";
	public static final String TOTAL_VOTOS_EMITIDOS = "totalVotosEmitidos";
	public static final String TOTAL_VOTOS_VALIDOS = "totalVotosValidos";
	public static final String TIPO_ELECCION = "tipoEleccion";
	public static final String TIPO_FILTRO = "tipoFiltro";
	public static final String AMBITO_GEOGRAFICO = "ambitoGeografico";
	public static final String UBIGEO_NIVEL_01 = "ubigeoNivel01";
	public static final String UBIGEO_NIVEL_02 = "ubigeoNivel02";
	public static final String UBIGEO_NIVEL_03 = "ubigeoNivel03";
	private final MongoOperations mongoOperations;

	public MaeResumenGeneralRepositoryCustom(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Optional<ActaEleccionDto> obtenerTotalesPorEleccion(FiltroActaEleccionDto filtros) {

		Criteria filterCriteria = obtenerQueryFiltrosResumen(filtros);

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria),
				Aggregation.project()
						.and(PORCENTAJE_ACTAS_CONTABILIZADAS).as(ACTAS_CONTABILIZADAS)
						.and(ACTAS_CONTABILIZADAS).as("contabilizadas")
						.and(TOTAL_ACTAS).as(TOTAL_ACTAS)
						.and(PORCENTAJE_PARTICIPACION_CIUDADANA).as("participacionCiudadana")
						.and(PORCENTAJE_ACTAS_OBSERVADAS_ENVIADAS).as("actasEnviadasJee")
						.and(ACTAS_OBSERVADAS_ENVIADAS).as("enviadasJee")
						.and(PORCENTAJE_ACTAS_PENDIENTES).as("actasPendientesJee")
						.and(ACTAS_PENDIENTES).as("pendientesJee")
						.and("audFechaModificacion").as("fechaActualizacion")
						.and(TIPO_ELECCION).as("idEleccion")
						.and("totalElectoresHabiles").as("totalElectoresHabiles")
						.and("participacionCiudadana").as("participacionCiudadanaTotal")
						.and(TOTAL_VOTOS_EMITIDOS).as(TOTAL_VOTOS_EMITIDOS)
						.and(TOTAL_VOTOS_VALIDOS).as(TOTAL_VOTOS_VALIDOS)
		);

		return switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
			case "Presidencial" -> obtenerTotalesPresidencial(aggregation);
			case "Diputados" -> obtenerTotalesDiputados(aggregation);
			case "Parlamento Andino" ->obtenerTotalesParlamentoAndino(aggregation);
			default -> Optional.empty();
		};


	}

	private Optional<ActaEleccionDto> obtenerTotalesPresidencial(Aggregation aggregation) {
		AggregationResults<ActaEleccionDto> results = mongoOperations.aggregate(
				aggregation, VwPrPresidenciales.class, ActaEleccionDto.class
		);

		ActaEleccionDto objeto =  results.getUniqueMappedResult();
		return objeto != null ? Optional.of(objeto): Optional.empty();
	}
	private Optional<ActaEleccionDto> obtenerTotalesDiputados(Aggregation aggregation) {
		AggregationResults<ActaEleccionDto> results = mongoOperations.aggregate(
				aggregation, VwPrDiputados.class, ActaEleccionDto.class
		);

		ActaEleccionDto objeto =  results.getUniqueMappedResult();
		return objeto != null ? Optional.of(objeto): Optional.empty();
	}
	private Optional<ActaEleccionDto> obtenerTotalesParlamentoAndino(Aggregation aggregation) {
		AggregationResults<ActaEleccionDto> results = mongoOperations.aggregate(
				aggregation, VwPrParlamentoAndino.class, ActaEleccionDto.class
		);

		ActaEleccionDto objeto =  results.getUniqueMappedResult();
		return objeto != null ? Optional.of(objeto): Optional.empty();
	}

	public List<ParticipanteDto> listarParticipantesPorEleccion(FiltroParticipanteDto filtros) {


		Criteria filterCriteria = obtenerQueryFiltros(filtros);
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filterCriteria)
		);

		return switch (TipoEleccionEnum.obtenerDescripcion(filtros.getIdEleccion().longValue())) {
			case "Presidencial" -> obtenerDataPresidencial(aggregation);
			case "Diputados" -> obtenerDataDiputados(aggregation);
			case "Parlamento Andino" -> obtenerDataParlamentoAndino(aggregation);
			case "Senadores 27" -> obtenerDataSenadores27(aggregation);
			case "Senadores 33" -> obtenerDataSenadores33(aggregation);
			case "Revocatoria Distrital" -> obtenerDataRevocatoriaDistrital(aggregation);
			default -> Collections.emptyList();
		};

	}

	private int getVotos(Integer votos) {
		return votos != null ? votos : 0;
	}

	private Criteria obtenerQueryFiltros(FiltroParticipanteDto filtros) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtros.getIdEleccion());

		if(filtros.getTipoFiltro() != null && !filtros.getTipoFiltro().isEmpty()) {
			filterCriteria.and(TIPO_FILTRO).is(filtros.getTipoFiltro());
		}
		if (filtros.getIdAmbitoGeografico() != null && filtros.getIdAmbitoGeografico().compareTo(0) != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtros.getIdAmbitoGeografico());
		}
		if (filtros.getIdUbigeoDepartamento() != null && filtros.getIdUbigeoDepartamento().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtros.getIdUbigeoDepartamento());
		}
		if (filtros.getIdUbigeoProvincia() != null && filtros.getIdUbigeoProvincia().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtros.getIdUbigeoProvincia());
		}
		if (filtros.getIdUbigeoDistrito() != null && filtros.getIdUbigeoDistrito().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtros.getIdUbigeoDistrito());
		}
		if (filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) != 0) {
			filterCriteria.and("n_distrito_electoral").is(filtros.getIdDistritoElectoral());
		}

		return filterCriteria;

	}

	private Criteria obtenerQueryFiltrosResumen(FiltroActaEleccionDto filtros) {

		Criteria filterCriteria = Criteria.where(TIPO_ELECCION).is(filtros.getIdEleccion());

		if(filtros.getTipoFiltro() != null && !filtros.getTipoFiltro().isEmpty()) {
			filterCriteria.and(TIPO_FILTRO).is(filtros.getTipoFiltro());
		}
		if (filtros.getIdAmbitoGeografico() != null && filtros.getIdAmbitoGeografico().compareTo(0) != 0) {
			filterCriteria.and(AMBITO_GEOGRAFICO).is(filtros.getIdAmbitoGeografico());
		}
		if (filtros.getIdUbigeoDepartamento() != null && filtros.getIdUbigeoDepartamento().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_01).is(filtros.getIdUbigeoDepartamento());
		}
		if (filtros.getIdUbigeoProvincia() != null && filtros.getIdUbigeoProvincia().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_02).is(filtros.getIdUbigeoProvincia());
		}
		if (filtros.getIdUbigeoDistrito() != null && filtros.getIdUbigeoDistrito().compareTo(0) != 0) {
			filterCriteria.and(UBIGEO_NIVEL_03).is(filtros.getIdUbigeoDistrito());
		}
		if (filtros.getIdDistritoElectoral() != null && filtros.getIdDistritoElectoral().compareTo(0) != 0) {
			filterCriteria.and("distritoElectoral").is(filtros.getIdDistritoElectoral());
		}

		return filterCriteria;

	}

	public List<MaeEleccion> findEleccionesByProceso(Long idProceso, Integer activo) {
        Criteria criteria = Criteria.where("procesoElectoral.id").is(idProceso).and("activo").is(activo);
        Query query = Query.query(criteria);
        return mongoOperations.find(query, MaeEleccion.class);
    }

	private List<ParticipanteDto> obtenerDataPresidencial(Aggregation aggregation) {
		AggregationResults<VwPrPresidenciales> results = mongoOperations.aggregate(aggregation, VwPrPresidenciales.class, VwPrPresidenciales.class);
		VwPrPresidenciales vwPrPresidencial = results.getUniqueMappedResult();
		return vwPrPresidencial != null ? vwPrPresidencial.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.distinct()
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos).thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> {
					String nombreCompletoCandidato = "";
					String dniCandidato = "";
					if(!data.getCandidato().isEmpty()) {
						nombreCompletoCandidato = data.getCandidato().get(0).getNombres() + " " + data.getCandidato().get(0).getApellidoPaterno()+ " " + data.getCandidato().get(0).getApellidoMaterno();
						dniCandidato = data.getCandidato().get(0).getDocumentoIdentidad();
					}
					return	ParticipanteDto.builder()
							.totalVotosValidos(getVotos(data.getVotos()))
							.nombreAgrupacionPolitica(data.getDescripcion())
							.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
							.nombreCandidato(nombreCompletoCandidato)
							.dniCandidato(dniCandidato)
							.porcentajeVotosValidos(data.getPorcentajeVotosValidos())
							.porcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos())
							.build();
				}).toList() : Collections.emptyList();
	}

	private List<ParticipanteDto> obtenerDataDiputados(Aggregation aggregation) {
		AggregationResults<VwPrDiputados> results = mongoOperations.aggregate(aggregation, VwPrDiputados.class, VwPrDiputados.class);
		VwPrDiputados vwPrDiputados = results.getUniqueMappedResult();
		return vwPrDiputados != null ? vwPrDiputados.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.sorted(Comparator.comparingLong(VwPrEleccionBaseDetalle::getVotos).thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> ParticipanteDto.builder()
							.totalVotosValidos(getVotos(data.getVotos()))
							.nombreAgrupacionPolitica(data.getDescripcion())
							.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
							.porcentajeVotosValidos(data.getPorcentajeVotosValidos())
							.porcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos())
							.build()
				).toList() : Collections.emptyList();
	}
	
	private List<ParticipanteDto> obtenerDataSenadores27(Aggregation aggregation) {
		AggregationResults<VwPrSenadoresDistritoElectoralMultiple> results = mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoElectoralMultiple.class, VwPrSenadoresDistritoElectoralMultiple.class);
		VwPrSenadoresDistritoElectoralMultiple vwPrSenadoresDistritoElectoralMultiple = results.getUniqueMappedResult();
		return vwPrSenadoresDistritoElectoralMultiple != null ? vwPrSenadoresDistritoElectoralMultiple.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.sorted(Comparator.comparingLong(VwPrEleccionBaseDetalle::getVotos).thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> ParticipanteDto.builder()
							.totalVotosValidos(getVotos(data.getVotos()))
							.nombreAgrupacionPolitica(data.getDescripcion())
							.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
							.porcentajeVotosValidos(data.getPorcentajeVotosValidos())
							.porcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos())
							.build()
				).toList() : Collections.emptyList();
	}

	private List<ParticipanteDto> obtenerDataParlamentoAndino(Aggregation aggregation) {
		AggregationResults<VwPrParlamentoAndino> results = mongoOperations.aggregate(aggregation, VwPrParlamentoAndino.class, VwPrParlamentoAndino.class);
		VwPrParlamentoAndino vwPrParlamentoAndino = results.getUniqueMappedResult();
		return vwPrParlamentoAndino != null ? vwPrParlamentoAndino.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos).thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> ParticipanteDto.builder()
							.totalVotosValidos(getVotos(data.getVotos()))
							.nombreAgrupacionPolitica(data.getDescripcion())
							.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
							.porcentajeVotosValidos(data.getPorcentajeVotosValidos())
							.porcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos())
							.build()
				).toList() : Collections.emptyList();
	}
	
	private List<ParticipanteDto> obtenerDataSenadores33(Aggregation aggregation) {
		AggregationResults<VwPrSenadoresDistritoNacionalUnico> results = mongoOperations.aggregate(aggregation, VwPrSenadoresDistritoNacionalUnico.class, VwPrSenadoresDistritoNacionalUnico.class);
		VwPrSenadoresDistritoNacionalUnico vwPrSenadoresDistritoNacionalUnico = results.getUniqueMappedResult();
		return vwPrSenadoresDistritoNacionalUnico != null ? vwPrSenadoresDistritoNacionalUnico.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos).thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> ParticipanteDto.builder()
							.totalVotosValidos(getVotos(data.getVotos()))
							.nombreAgrupacionPolitica(data.getDescripcion())
							.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
							.porcentajeVotosValidos(data.getPorcentajeVotosValidos())
							.porcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos())
							.build()
				).toList() : Collections.emptyList();
	}
	
	private List<ParticipanteDto> obtenerDataRevocatoriaDistrital(Aggregation aggregation) {
		AggregationResults<VwPrRevocatoriaDistrital> results = mongoOperations.aggregate(aggregation, VwPrRevocatoriaDistrital.class, VwPrRevocatoriaDistrital.class);
		VwPrRevocatoriaDistrital vwPrRevocatoriaDistrital = results.getUniqueMappedResult();
		return vwPrRevocatoriaDistrital != null ? vwPrRevocatoriaDistrital.getDetalle().stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.sorted(Comparator.comparing(VwPrEleccionBaseDetalle::getDescripcion))
				.filter(data -> data.getGrafico() != null)
				.filter(data -> data.getGrafico() == 1)
				.map(data -> {
					List<ParticipanteCandidatoDto> candidatos = new ArrayList<>();
					if(data.getCandidato()!=null && !data.getCandidato().isEmpty()) {
						candidatos = data.getCandidato().stream()
								.map(candidato -> ParticipanteCandidatoDto.builder()
										.codigoOpcionVoto(candidato.getCodigoOpcionVoto())
										.posicionOpcionVoto(candidato.getPosicionOpcionVoto())
										.totalVotos(candidato.getVotos())
										.descripcionOpcionVoto(candidato.getDescripcionOpcionVoto())
										.porcentajeVotosEmitidos(candidato.getPorcentajeVotosEmitidos())
										.porcentajeVotosValidos(candidato.getPorcentajeVotosValidos())
										.build())
								.toList();
					}
					return ParticipanteDto.builder()
					.nombreAgrupacionPolitica(data.getDescripcion())
					.codigoAgrupacionPolitica(data.getAgrupacionPolitica())
					.cargo(data.getCargo())
					.candidato(candidatos)
					.build();
				}).toList() : Collections.emptyList();
	}
	
}
