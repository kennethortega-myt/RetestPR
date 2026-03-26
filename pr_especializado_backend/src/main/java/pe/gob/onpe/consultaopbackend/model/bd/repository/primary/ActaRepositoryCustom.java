package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMesaRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActasResponseDto;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ActaRepositoryCustom {

	public static final String LINEA_TIEMPO_CODIGO_ESTADO_ACTA = "lineaTiempo.codigoEstadoActa";
	public static final String CODIGO_ESTADO_ACTA = "codigoEstadoActa";
	public static final String ID_AMBITO_GEOGRAFICO = "idAmbitoGeografico";
	public static final String ID_UBIGEO = "idUbigeo";
	public static final String ID_LOCAL_VOTACION = "idLocalVotacion";
	public static final String ID_ELECCION = "idEleccion";
	public static final String NUBIGEO_NIVEL_01 = "nubigeoNivel01";
	public static final String NUBIGEO_NIVEL_02 = "nubigeoNivel02";

	private MongoOperations mongoOperations;

	public ActaRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}

	public List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro) {
		Criteria criteria = Criteria.where("codigoMesa").is(filtro.getCodigoMesa());

		Query query = Query.query(criteria);
		query.with(Sort.by(Sort.Order.asc(ID_ELECCION)));
		List<VwPrActa> lstVwPrActa = mongoOperations.find(query, VwPrActa.class);

		return lstVwPrActa.stream()
				.filter(Objects::nonNull)
				.map(vwPrActa -> ActasResponseDto.builder()
						.id(vwPrActa.getId())
						.idMesa(vwPrActa.getIdMesa())
						.codigoMesa(vwPrActa.getCodigoMesa())
						.estadoActa(vwPrActa.getEstadoActa())
						.descripcionEstadoActa(vwPrActa.getDescripcionEstadoActa())
						.idEleccion(vwPrActa.getIdEleccion())
						.idAmbitoGeografico(vwPrActa.getIdAmbitoGeografico())
						.build())
				.toList();
	}

	public Page<VwPrActa> findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(
			ActaReqDto filtro, Pageable pageable) {
		Criteria criteria = new Criteria();
		if (filtro.getIdEleccion() != 0) {
			criteria.and(ID_ELECCION).is(filtro.getIdEleccion());
		}
		if (null != filtro.getIdDistritoElectoral() && filtro.getIdDistritoElectoral() != 0) {
			criteria.and(ConstantesComunes.ID_DISTRITO_ELECTORAL).is(filtro.getIdDistritoElectoral());
		}
		if (filtro.getIdAmbitoGeografico() != 0) {
			criteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}
		if (filtro.getUbigeoNivel01() != null) {
			criteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
		}
		if (filtro.getUbigeoNivel02() != null) {
			criteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
		}
		if (filtro.getIdUbigeo() != null) {
			criteria.and(ID_UBIGEO).is(Integer.parseInt(filtro.getIdUbigeo()));
		}
		if (filtro.getCodigoLocalVotacion() != 0) {
			criteria.and(ID_LOCAL_VOTACION).is(filtro.getCodigoLocalVotacion());
		}
		if (filtro.getCodigoEstadoActa() != null) {
			criteria.and(CODIGO_ESTADO_ACTA).is(filtro.getCodigoEstadoActa());
		}

		Query query = Query.query(criteria);

		long total = mongoOperations.count(query, VwPrActa.class);
		query.with(pageable);
		List<VwPrActa> lstVwPrActa = mongoOperations.find(query, VwPrActa.class);
		return new PageImpl<>(lstVwPrActa, pageable, total);
	}

	public List<VwPrActa> findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(
			ActaRequestDto filtro) {
		Criteria criteria = new Criteria();
		if (filtro.getIdEleccion() != 0) {
			criteria.and(ID_ELECCION).is(filtro.getIdEleccion());
		}
		if (filtro.getIdAmbitoGeografico() != 0) {
			criteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}
		if (filtro.getUbigeoNivel01() != null) {
			criteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
		}
		if (filtro.getUbigeoNivel02() != null) {
			criteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
		}
		if (filtro.getIdUbigeo() != null) {
			criteria.and(ID_UBIGEO).is(Integer.parseInt(filtro.getIdUbigeo()));
		}
		if (filtro.getCodigoLocalVotacion() != 0) {
			criteria.and(ID_LOCAL_VOTACION).is(filtro.getCodigoLocalVotacion());
		}

		if (filtro.getIdDistritoElectoral() != 0) {
			criteria.and(ConstantesComunes.ID_DISTRITO_ELECTORAL).is(filtro.getIdDistritoElectoral());
		}

		// SOLO ACTAS CONTABILIZADAS
		criteria.and(CODIGO_ESTADO_ACTA).is("C");

		Query query = Query.query(criteria);

		query.with(Sort.by(
				Sort.Order.asc(ID_AMBITO_GEOGRAFICO),
				Sort.Order.asc(NUBIGEO_NIVEL_01),
				Sort.Order.asc(NUBIGEO_NIVEL_02),
				Sort.Order.asc(ID_UBIGEO),
				Sort.Order.asc("idMesa")));

		return mongoOperations.find(query, VwPrActa.class);
	}

	public Page<VwPrActa> obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacion(ActaRequestDto filtro,
			Pageable pageable) {
		Criteria baseCriteria = new Criteria();
		baseCriteria.and(ConstantesComunes.ESTADO_ACTAS).nin("N", "R", "Q");

		if (filtro.getIdEleccion() != 0) {
			baseCriteria.and(ID_ELECCION).is(filtro.getIdEleccion());
		}

		if (filtro.getIdDistritoElectoral() != null && filtro.getIdDistritoElectoral() != 0
				&& filtro.getIdDistritoElectoral() != 30) {
			baseCriteria.and(ConstantesComunes.ID_DISTRITO_ELECTORAL).is(filtro.getIdDistritoElectoral());
		}
		if (null != filtro.getIdAmbitoGeografico() && filtro.getIdAmbitoGeografico() != 0) {
			baseCriteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}
		if (filtro.getUbigeoNivel01() != null) {
			baseCriteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
		}
		if (filtro.getUbigeoNivel02() != null) {
			baseCriteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
		}
		if (filtro.getIdUbigeo() != null) {
			baseCriteria.and(ID_UBIGEO).is(Long.parseLong(filtro.getIdUbigeo()));
		}
		if (null != filtro.getCodigoLocalVotacion() && filtro.getCodigoLocalVotacion().compareTo(0L) != 0) {
			baseCriteria.and(ID_LOCAL_VOTACION).is(filtro.getCodigoLocalVotacion());
		}

		Criteria estadoBase = Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R");

		if (filtro.getResueltas() == null) {
			baseCriteria.andOperator(estadoBase);
		} else {
			baseCriteria.andOperator(
					estadoBase,
					Boolean.TRUE.equals(filtro.getResueltas())
							? Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("L", "C")
							: Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).nin("L", "C"));
		}

		Criteria criteriosComunes = new Criteria().andOperator(baseCriteria);

		// Opción A: Ver solo el campo específico
		log.info("Valor de DescripcionActaResolucion: {}", filtro.getDescripcionActaResolucion());

		// Opción B: Ver el objeto 'filtro' completo (si tiene método toString)
		log.info("Objeto filtro completo: {}", filtro);

		if (filtro.getDescripcionActaResolucion() != null) {
			String descripcion = filtro.getDescripcionActaResolucion().trim();

			List<String> todosValores = Arrays.stream(descripcion.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.toList();

			Set<VwPrActa> resultadosUnicos = obtenerResultadosUnicos(
					todosValores, criteriosComunes, pageable);
			List<VwPrActa> resultadosFinales = new ArrayList<>(resultadosUnicos);
			resultadosFinales = resultadosFinales.stream()
					.sorted(Comparator.comparingLong(VwPrActa::getId))
					.skip(pageable.getOffset())
					.limit(pageable.getPageSize())
					.toList();

			long total = resultadosUnicos.size();

			return new PageImpl<>(resultadosFinales, pageable, total);
		}

		Query query = Query.query(criteriosComunes);
		long total = mongoOperations.count(query, VwPrActa.class);

		query.with(pageable);
		List<VwPrActa> lstVwPrActa = mongoOperations.find(query, VwPrActa.class).stream().toList();

		return new PageImpl<>(lstVwPrActa, pageable, total);
	}

	public Set<VwPrActa> obtenerResultadosUnicos(
			List<String> todosValores,
			Criteria criteriosComunes,
			Pageable pageable) {
		final int indice2MAS = todosValores.indexOf("2MAS");
		final boolean buscarMultiValores = indice2MAS != -1;

		final List<String> valoresIndividuales;
		final List<String> valoresParaCombinaciones;

		if (buscarMultiValores) {
			valoresIndividuales = indice2MAS > 0 ? todosValores.subList(0, indice2MAS) : Collections.emptyList();
			valoresParaCombinaciones = indice2MAS < todosValores.size() - 1
					? todosValores.subList(indice2MAS + 1, todosValores.size())
					: Collections.emptyList();
		} else {
			valoresIndividuales = todosValores;
			valoresParaCombinaciones = Collections.emptyList();
		}

		Set<VwPrActa> resultadosUnicos = new LinkedHashSet<>();

		if (!valoresIndividuales.isEmpty()) {
			Criteria criteriaCompleto = new Criteria().andOperator(
					criteriosComunes,
					Criteria.where("estadoActaResolucion")
							.regex(valoresIndividuales.stream()
									.map(String::trim)
									.map(Object::toString)
									.collect(Collectors.joining("|")), "i"));

			resultadosUnicos.addAll(mongoOperations.find(
					Query.query(criteriaCompleto).with(pageable),
					VwPrActa.class));
		}

		if (buscarMultiValores && !valoresParaCombinaciones.isEmpty()) {
			Criteria criteriaCompleto = new Criteria().andOperator(
					criteriosComunes,
					Criteria.where("estadoActaResolucion")
							.regex(valoresParaCombinaciones.stream()
									.map(String::trim)
									.map(Object::toString)
									.collect(Collectors.joining("|")), "i"));

			mongoOperations.find(Query.query(criteriaCompleto).with(pageable), VwPrActa.class)
					.stream()
					.filter(acta -> acta.getEstadoActaResolucion() != null)
					.filter(acta -> acta.getEstadoActaResolucion().split(",").length >= 2)
					.forEach(resultadosUnicos::add);
		}

		return resultadosUnicos;

	}

	public Map<String, Object> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(
			Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo,
			Long idLocalVotacion, Integer idDistritoElectoral) {
		
		Criteria trueCriteria = getCriteria(idEleccion, idAmbitoGeografico, nubigeoNivel01, nubigeoNivel02, idUbigeo, idLocalVotacion, idDistritoElectoral);

		Criteria falseCriteria = getCriteria(idEleccion, idAmbitoGeografico, nubigeoNivel01, nubigeoNivel02, idUbigeo, idLocalVotacion, idDistritoElectoral);

		// Criteria for "true" results
	    trueCriteria.andOperator(
			Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R"),
			Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("L","C")
	    );
	    
	    // Criteria for "false" results
	    falseCriteria.andOperator(
			Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R"),
			Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).nin("L","C")
	    );

	    // Query for true results
		trueCriteria.and(ConstantesComunes.ESTADO_ACTAS).nin("N","R","Q");
	    Query trueQuery = Query.query(trueCriteria);
        long trueTotal = mongoOperations.count(trueQuery, VwPrActa.class);

	    // Query for false results
		falseCriteria.and(ConstantesComunes.ESTADO_ACTAS).nin("N","R","Q");
	    Query falseQuery = Query.query(falseCriteria);
	    long falseTotal = mongoOperations.count(falseQuery, VwPrActa.class);

	    // Combine results and totals
	    Map<String, Object> resultMap = new HashMap<>();
	    resultMap.put("trueTotal", trueTotal);
	    resultMap.put("falseTotal", falseTotal);
	    return resultMap;
	}

	private static Criteria getCriteria(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01,
			Integer nubigeoNivel02, Long idUbigeo, Long idLocalVotacion, Integer idDistritoElectoral) {
		Criteria trueCriteria = new Criteria();
		if (idEleccion != 0) {
			trueCriteria.and(ID_ELECCION).is(idEleccion);
		}
		if (null != idDistritoElectoral && idDistritoElectoral != 0 && idDistritoElectoral != 30) {
			trueCriteria.and(ConstantesComunes.ID_DISTRITO_ELECTORAL).is(idDistritoElectoral);
		}
		if (null != idAmbitoGeografico && idAmbitoGeografico != 0) {
			trueCriteria.and(ID_AMBITO_GEOGRAFICO).is(idAmbitoGeografico);
		}
		if (null != nubigeoNivel01 && nubigeoNivel01 != 0) {
			trueCriteria.and(NUBIGEO_NIVEL_01).is(nubigeoNivel01);
		}
		if (null != nubigeoNivel02 && nubigeoNivel02 != 0) {
			trueCriteria.and(NUBIGEO_NIVEL_02).is(nubigeoNivel02);
		}
		if (null != idUbigeo && idUbigeo != 0) {
			trueCriteria.and(ID_UBIGEO).is(idUbigeo);
		}
		if (null != idLocalVotacion && idLocalVotacion != 0) {
			trueCriteria.and(ID_LOCAL_VOTACION).is(idLocalVotacion);
		}
		return trueCriteria;
	}

	public List<VwPrActa> obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(
			ActaRequestDto filtro) {
		Criteria criteria = new Criteria();

		// FILTRO SOLO ACTAS CONTABILIZADAS
		criteria.and(CODIGO_ESTADO_ACTA).is("C");
		criteria.and(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E");
		if (filtro.getIdEleccion() != 0) {
			criteria.and(ID_ELECCION).is(filtro.getIdEleccion());
		}
		if (null != filtro.getIdAmbitoGeografico() && filtro.getIdAmbitoGeografico() != 0) {
			criteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
		}
		if (filtro.getUbigeoNivel01() != null) {
			criteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
		}
		if (filtro.getUbigeoNivel02() != null) {
			criteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
		}
		if (filtro.getIdUbigeo() != null) {
			criteria.and(ID_UBIGEO).is(Long.parseLong(filtro.getIdUbigeo()));
		}

		if (filtro.getResueltas() != null) {
			if (Boolean.TRUE.equals(filtro.getResueltas())) {
				criteria.andOperator(
						Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R"),
						Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("L", "C"));
			} else {
				criteria.andOperator(
						Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R"),
						Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).nin("L", "C"));
			}
		} else {
			criteria.andOperator(
					Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E", "R"));
		}
		Query query = Query.query(criteria);

		query.with(Sort.by(
				Sort.Order.asc(ID_AMBITO_GEOGRAFICO),
				Sort.Order.asc(NUBIGEO_NIVEL_01),
				Sort.Order.asc(NUBIGEO_NIVEL_02),
				Sort.Order.asc(ID_UBIGEO),
				Sort.Order.asc("idMesa")));

		return mongoOperations.find(query, VwPrActa.class);
	}

}
