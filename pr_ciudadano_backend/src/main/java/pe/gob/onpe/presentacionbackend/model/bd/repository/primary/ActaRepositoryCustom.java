package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaGroupCodigoEstadoActa;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMesaRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActasResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ActaRepositoryCustom {

	public static final String CODIGO_ESTADO_ACTA = "codigoEstadoActa";
	public static final String ID_UBIGEO = "idUbigeo";
	public static final String CODIGO_ESTADO_ACTA1 = "lineaTiempo.codigoEstadoActa";
	private MongoOperations mongoOperations;
	
	public ActaRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}

	public List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro) {
		Criteria criteria = Criteria.where("codigoMesa").is(filtro.getCodigoMesa());
		
		Query query = Query.query(criteria);
		query.with(Sort.by(Sort.Order.asc("orden")));
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
						.idUbigeo(vwPrActa.getIdUbigeo())
						.centroPoblado(vwPrActa.getCentroPoblado())
						.nombreLocalVotacion(vwPrActa.getNombreLocalVotacion())
						.codigoLocalVotacion(vwPrActa.getCodigoLocalVotacion())
						.codigoEstadoActa(vwPrActa.getCodigoEstadoActa())
						.totalElectoresHabiles(vwPrActa.getTotalElectoresHabiles())
						.totalVotosEmitidos(vwPrActa.getTotalVotosEmitidos())
						.totalVotosValidos(vwPrActa.getTotalVotosValidos())
						.totalAsistentes(vwPrActa.getTotalAsistentes())
						.porcentajeParticipacionCiudadana(vwPrActa.getPorcentajeParticipacionCiudadana())
						.estadoActa(vwPrActa.getEstadoActa())
						.estadoComputo(vwPrActa.getEstadoComputo())
						.descripcionEstadoActa(vwPrActa.getDescripcionEstadoActa())
						.detalle(vwPrActa.getDetalle())
						.lineaTiempo(vwPrActa.getLineaTiempo())
						.build())
				.toList();
	}
	
	public Page<VwPrActa> obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacion(ActaRequestDto filtro, Pageable pageable) {
		Criteria baseCriteria = construirCriteriosBase(filtro);

		Criteria estadoCriteria = construirCriterioEstadoResolucion(filtro);

		Criteria criteriosComunes = new Criteria().andOperator(baseCriteria, estadoCriteria);

		if (filtro.getDescripcionActaResolucion() != null) {
		    String descripcion = filtro.getDescripcionActaResolucion().trim();
		    
		    List<String> todosValores = Arrays.stream(descripcion.split(","))
		            .map(String::trim)
		            .filter(s -> !s.isEmpty())
		            .toList();
		    
		    final int indice2MAS = todosValores.indexOf("2MAS");
		    final boolean buscarMultiValores = indice2MAS != -1;
		    
		    final List<String> valoresIndividuales;
		    final List<String> valoresParaCombinaciones;
		    
		    if (buscarMultiValores) {
		        valoresIndividuales = indice2MAS > 0 ? todosValores.subList(0, indice2MAS) : Collections.emptyList();
		        valoresParaCombinaciones = indice2MAS < todosValores.size() - 1 ? 
		                todosValores.subList(indice2MAS + 1, todosValores.size()) : Collections.emptyList();
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
			                        .collect(Collectors.joining("|")), "i")
		        );
		        
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
			                        .collect(Collectors.joining("|")), "i")
		        );
		        
		        mongoOperations.find(Query.query(criteriaCompleto).with(pageable), VwPrActa.class)
		            .stream()
		            .filter(acta -> acta.getEstadoActaResolucion() != null)
		            .filter(acta -> acta.getEstadoActaResolucion().split(",").length >= 2)
		            .forEach(resultadosUnicos::add);
		    }
		    
		    List<VwPrActa> resultadosFinales = new ArrayList<>(resultadosUnicos);
		    resultadosFinales = resultadosFinales.stream()
		    		.sorted(Comparator.comparingLong(VwPrActa::getId))
		            .skip(pageable.getOffset())
		            .limit(pageable.getPageSize())
		            .toList();
		    
		    long total = resultadosUnicos.size();
		    
		    return new PageImpl<>(resultadosFinales, pageable, total);
		}

		return new PageImpl<>(
		    mongoOperations.find(Query.query(criteriosComunes).with(pageable), VwPrActa.class),
		    pageable,
		    mongoOperations.count(Query.query(criteriosComunes), VwPrActa.class)
		);
	}

	private Criteria construirCriteriosBase(ActaRequestDto filtro) {
		Criteria criteria = new Criteria();
		criteria.and("estadoActa").nin("N", "R", "Q");

		if (filtro.getIdAmbitoGeografico() != 0) {
			criteria.and("idAmbitoGeografico").is(filtro.getIdAmbitoGeografico());
		}
		if (filtro.getIdUbigeo() != 0) {
			criteria.and(ID_UBIGEO).is(filtro.getIdUbigeo());
		}
		if (filtro.getCodigoLocalVotacion() != null) {
			criteria.and("idLocalVotacion").is(filtro.getCodigoLocalVotacion());
		}

		return criteria;
	}

	private Criteria construirCriterioEstadoResolucion(ActaRequestDto filtro) {
		if (filtro.getResueltas() == null) {
			return Criteria.where(CODIGO_ESTADO_ACTA1).in("E", "R");
		}

		if (Boolean.TRUE.equals(filtro.getResueltas())) {
			return new Criteria().andOperator(
					Criteria.where(CODIGO_ESTADO_ACTA1).in("E", "R"),
					Criteria.where(CODIGO_ESTADO_ACTA1).in("L", "C")
			);
		} else {
			return new Criteria().andOperator(
					Criteria.where(CODIGO_ESTADO_ACTA1).in("E", "R"),
					Criteria.where(CODIGO_ESTADO_ACTA1).nin("L", "C")
			);
		}
	}
	
	public List<ActaGroupCodigoEstadoActa> findByActasGroupedByEstado(Long idUbigeo) {
		Criteria filtro = Criteria.where(ID_UBIGEO).is(idUbigeo);
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(filtro),
				Aggregation.group(CODIGO_ESTADO_ACTA).first(CODIGO_ESTADO_ACTA).as("estado").count().as("total")
				);
		AggregationResults<ActaGroupCodigoEstadoActa> results = this.mongoOperations.aggregate(aggregation, VwPrActa.class, ActaGroupCodigoEstadoActa.class);
		return results.getMappedResults();
	}

}
