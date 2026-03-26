package pe.gob.onpe.pradminbackend.model.bd.repository;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActaHistorico;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ActaRequestDto;

import java.util.*;

@Slf4j
@Repository
public class ActaRepositoryCustom {

	public static final String CODIGO_ESTADO_ACTA = "codigoEstadoActa";
	public static final String ID_UBIGEO = "idUbigeo";

    public static final String LINEA_TIEMPO_CODIGO_ESTADO_ACTA = "lineaTiempo.codigoEstadoActa";
    public static final String ID_AMBITO_GEOGRAFICO = "idAmbitoGeografico";
    public static final String ID_LOCAL_VOTACION = "idLocalVotacion";
    public static final String ID_ELECCION = "idEleccion";
    public static final String NUBIGEO_NIVEL_01 = "nubigeoNivel01";
    public static final String NUBIGEO_NIVEL_02 = "nubigeoNivel02";
    public static final String ESTADO_ACTA = "estadoActa";
    public static final String ID_DISTRITO_ELECTORAL = "idDistritoElectoral";
    private MongoOperations mongoOperations;
	
	public ActaRepositoryCustom(MongoOperations mongoOperations) {
		super();
		this.mongoOperations = mongoOperations;
	}

	public boolean actualizarActa(VwPrActa actaNuevo, Long idActa, String usuario) {
		boolean actualizado = false;
		Query query = new Query(
				Criteria.where("id").is(actaNuevo.getId())
		);
		VwPrActa actaActual = mongoOperations.findOne(query, VwPrActa.class);
		log.info("Acta actual: " + actaActual);

		if ( null != actaActual) {
			Update update = mapearCamposActualizar(actaNuevo, actaActual , idActa, usuario);
			UpdateResult updated = mongoOperations.updateFirst(query, update, VwPrActa.class);
			log.info("UpdateResult " + updated.toString());
			log.info("Registros modificados: " + updated.getModifiedCount());
			actualizado = true;
		}else {
			log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, actaNuevo.getId());
		}

		return actualizado;

	}

	private Update mapearCamposActualizar(VwPrActa actaNuevo, VwPrActa actaActual, Long idActa, String usuario) {
		Update update = new Update();

		if(actaNuevo.getCodigoMesa() != null) {
			update.set("codigoMesa",actaNuevo.getCodigoMesa());
		}
		if(actaNuevo.getIdSolucionTecnologica() != null) {
			update.set("idSolucionTecnologica",actaNuevo.getIdSolucionTecnologica());
		}
		if(actaNuevo.getDescripcionSolucionTecnologica() != null) {
			update.set("descripcionSolucionTecnologica",actaNuevo.getDescripcionSolucionTecnologica());
		}
		if(actaNuevo.getNumeroCopia() != null) {
			update.set("numeroCopia",actaNuevo.getNumeroCopia());
		}
		if(actaNuevo.getIdUbigeoEleccion() != null) {
			update.set("idUbigeoEleccion",actaNuevo.getIdUbigeoEleccion());
		}
		if(actaNuevo.getIdEleccion()!= null) {
			update.set(ID_ELECCION,actaNuevo.getIdEleccion());
		}
		if(actaNuevo.getIdAmbitoGeografico()!= null) {
			update.set(ID_AMBITO_GEOGRAFICO,actaNuevo.getIdAmbitoGeografico());
		}
		if(actaNuevo.getIdUbigeo()!= null) {
			update.set(ID_UBIGEO, actaNuevo.getIdUbigeo());
		}
		if(actaNuevo.getIdDistritoElectoral()!=null) {
			update.set(ID_DISTRITO_ELECTORAL, actaNuevo.getIdDistritoElectoral());
		}
		if(actaNuevo.getNubigeoNivel01()!=null) {
			update.set(NUBIGEO_NIVEL_01, actaNuevo.getNubigeoNivel01());
		}
		if(actaNuevo.getNubigeoNivel02()!=null) {
			update.set(NUBIGEO_NIVEL_02, actaNuevo.getNubigeoNivel02());
		}
		if(actaNuevo.getUbigeoNombreNivel01()!= null) {
			update.set("ubigeoNombreNivel01",actaNuevo.getUbigeoNombreNivel01());
		}
		if(actaNuevo.getUbigeoNombreNivel02()!= null) {
			update.set("ubigeoNombreNivel02",actaNuevo.getUbigeoNombreNivel02());
		}
		if(actaNuevo.getUbigeoNombreNivel03()!= null) {
			update.set("ubigeoNombreNivel03",actaNuevo.getUbigeoNombreNivel03());
		}
		if(actaNuevo.getCentroPoblado()!= null) {
			update.set("centroPoblado",actaNuevo.getCentroPoblado());
		}
		if(actaNuevo.getIdLocalVotacion()!= null) {
			update.set(ID_LOCAL_VOTACION,actaNuevo.getIdLocalVotacion());
		}
		if(actaNuevo.getNombreLocalVotacion()!= null) {
			update.set("nombreLocalVotacion",actaNuevo.getNombreLocalVotacion());
		}

		mapearActualizacion(actaNuevo,update);

		update.set("c_historico", obtenerHistoricos(actaActual, idActa, usuario));

		return update;
	}

	private void mapearActualizacion(VwPrActa actaNuevo,Update update) {

		if(actaNuevo.getCodigoLocalVotacion()!= null) {
			update.set("codigoLocalVotacion",actaNuevo.getCodigoLocalVotacion());
		}
		if(actaNuevo.getNombreLocalVotacion()!= null) {
			update.set("nombreLocalVotacion;",actaNuevo.getNombreLocalVotacion());
		}
		if(actaNuevo.getTotalElectoresHabiles() != null) {
			update.set("totalElectoresHabiles",actaNuevo.getTotalElectoresHabiles());
		}
		if(actaNuevo.getTotalAsistentes() != null) {
			update.set("totalAsistentes",actaNuevo.getTotalAsistentes());
		}
		if(actaNuevo.getTotalVotosEmitidos() != null) {
			update.set("totalVotosEmitidos",actaNuevo.getTotalVotosEmitidos());
		}
		if(actaNuevo.getTotalVotosValidos()!= null) {
			update.set("totalVotosValidos",actaNuevo.getTotalVotosValidos());
		}
		if(actaNuevo.getPorcentajeParticipacionCiudadana() != null) {
			update.set("porcentajeParticipacionCiudadana",actaNuevo.getPorcentajeParticipacionCiudadana());
		}
		if(actaNuevo.getEstadoActa() != null) {
			update.set(ESTADO_ACTA,actaNuevo.getEstadoActa());
		}
		if(actaNuevo.getEstadoComputo() != null) {
			update.set("estadoComputo",actaNuevo.getEstadoComputo());
		}
		if(actaNuevo.getCodigoEstadoActa() != null) {
			update.set(CODIGO_ESTADO_ACTA,actaNuevo.getCodigoEstadoActa());
		}
		if(actaNuevo.getDescripcionEstadoActa() != null) {
			update.set("descripcionEstadoActa",actaNuevo.getDescripcionEstadoActa());
		}
		if(actaNuevo.getEstadoActaResolucion() != null) {
			update.set("estadoActaResolucion",actaNuevo.getEstadoActaResolucion());
		}
		update.set("descripcionSubEstadoActa", actaNuevo.getDescripcionSubEstadoActa());
		update.set("estadoDescripcionActaResolucion",actaNuevo.getEstadoDescripcionActaResolucion());
		if(actaNuevo.getDetalle() != null) {
			update.set("detalle",actaNuevo.getDetalle());
		}
		if(actaNuevo.getLineaTiempo() != null) {
			update.set("lineaTiempo",actaNuevo.getLineaTiempo());
		}
	}

	private List<VwPrActaHistorico> obtenerHistoricos(VwPrActa actaActual, Long acta, String usuario) {

		List<VwPrActaHistorico> historicoTotal;
		if (actaActual.getHistorico() != null) {
			historicoTotal = actaActual.getHistorico();
			historicoTotal.add(this.mapperActaHistorico(actaActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperActaHistorico(actaActual, acta, usuario));
		}

		return historicoTotal;
	}

	private VwPrActaHistorico mapperActaHistorico(VwPrActa actaActual, Long acta, String usuario) {
		VwPrActaHistorico historico = new VwPrActaHistorico();
		historico.setId(actaActual.getId());
		historico.setIdMesa(actaActual.getIdMesa());
		historico.setCodigoMesa(actaActual.getCodigoMesa());
		historico.setIdSolucionTecnologica(actaActual.getIdSolucionTecnologica());
		historico.setDescripcionSolucionTecnologica(actaActual.getDescripcionSolucionTecnologica());
		historico.setNumeroCopia(actaActual.getNumeroCopia());
		historico.setIdUbigeoEleccion(actaActual.getIdUbigeoEleccion());
		historico.setIdEleccion(actaActual.getIdEleccion());
		historico.setIdAmbitoGeografico(actaActual.getIdAmbitoGeografico());
		historico.setIdDistritoElectoral(actaActual.getIdDistritoElectoral());
		historico.setIdUbigeo(actaActual.getIdUbigeo());
		historico.setNubigeoNivel01(actaActual.getNubigeoNivel01());
		historico.setNubigeoNivel02(actaActual.getNubigeoNivel02());
		historico.setUbigeoNombreNivel01(actaActual.getUbigeoNombreNivel01());
		historico.setUbigeoNombreNivel02(actaActual.getUbigeoNombreNivel02());
		historico.setUbigeoNombreNivel03(actaActual.getUbigeoNombreNivel03());
		historico.setCentroPoblado(actaActual.getCentroPoblado());
		historico.setIdLocalVotacion(actaActual.getIdLocalVotacion());
		historico.setNombreLocalVotacion(actaActual.getNombreLocalVotacion());
		historico.setCodigoLocalVotacion(actaActual.getCodigoLocalVotacion());
		historico.setDigitoChequeoEscrutinio(actaActual.getDigitoChequeoEscrutinio());
		historico.setDigitoChequeoInstalacion(actaActual.getDigitoChequeoInstalacion());
		historico.setDigitoChequeoSufragio(actaActual.getDigitoChequeoSufragio());

		historico.setTotalElectoresHabiles(actaActual.getTotalElectoresHabiles());
		historico.setTotalAsistentes(actaActual.getTotalAsistentes());
		historico.setTotalVotosValidos(actaActual.getTotalVotosValidos());
		historico.setTotalVotosEmitidos(actaActual.getTotalVotosEmitidos());
		historico.setPorcentajeParticipacionCiudadana(actaActual.getPorcentajeParticipacionCiudadana());

		historico.setEstadoActa(actaActual.getEstadoActa());
		historico.setEstadoComputo(actaActual.getEstadoComputo());
		historico.setCodigoEstadoActa(actaActual.getCodigoEstadoActa());
		historico.setDescripcionEstadoActa(actaActual.getDescripcionEstadoActa());

		historico.setEstadoActaResolucion(actaActual.getEstadoActaResolucion());
		historico.setEstadoDescripcionActaResolucion(actaActual.getEstadoDescripcionActaResolucion());
		historico.setLineaTiempo(actaActual.getLineaTiempo());
		historico.setDetalle(actaActual.getDetalle());

		historico.setNActa(acta);
		historico.setAudUsuarioModificacion(usuario);
		historico.setAudFechaModificacion(new Date());

		return historico;
	}

    public List<VwPrActa> findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(ActaRequestDto filtro) {
        Criteria criteria = new Criteria();
        if(filtro.getIdEleccion() != 0) {
            criteria.and(ID_ELECCION).is(filtro.getIdEleccion());
        }
        if(filtro.getIdAmbitoGeografico()!=0) {
            criteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
        }
        if(filtro.getUbigeoNivel01() != null) {
            criteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
        }
        if(filtro.getUbigeoNivel02() != null) {
            criteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
        }
        if(filtro.getIdUbigeo() != null) {
            criteria.and(ID_UBIGEO).is(Integer.parseInt(filtro.getIdUbigeo()));
        }
        if(filtro.getCodigoLocalVotacion()!=0) {
            criteria.and(ID_LOCAL_VOTACION).is(filtro.getCodigoLocalVotacion());
        }

        if(filtro.getIdDistritoElectoral()!=0) {
            criteria.and(ID_DISTRITO_ELECTORAL).is(filtro.getIdDistritoElectoral());
        }

        //SOLO ACTAS CONTABILIZADAS
        criteria.and(CODIGO_ESTADO_ACTA).is("C");

        Query query = Query.query(criteria);

        return mongoOperations.find(query, VwPrActa.class);
    }

    public List<VwPrActa> obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(ActaRequestDto filtro) {
        Criteria criteria = new Criteria();
        if(filtro.getIdEleccion() != 0) {
            criteria.and(ID_ELECCION).is(filtro.getIdEleccion());
        }
        if(null != filtro.getIdAmbitoGeografico() && filtro.getIdAmbitoGeografico()!=0) {
            criteria.and(ID_AMBITO_GEOGRAFICO).is(filtro.getIdAmbitoGeografico());
        }
        if(filtro.getUbigeoNivel01() != null) {
            criteria.and(NUBIGEO_NIVEL_01).is(Integer.parseInt(filtro.getUbigeoNivel01()));
        }
        if(filtro.getUbigeoNivel02() != null) {
            criteria.and(NUBIGEO_NIVEL_02).is(Integer.parseInt(filtro.getUbigeoNivel02()));
        }
        if(filtro.getIdUbigeo() != null) {
            criteria.and(ID_UBIGEO).is(Long.parseLong(filtro.getIdUbigeo()));
        }

        if(filtro.getResueltas()!= null) {
            if(Boolean.TRUE.equals(filtro.getResueltas())) {
                criteria.andOperator(
                        Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E","R"),
                        Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("L","C")
                );
            } else {
                criteria.andOperator(
                        Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E","R"),
                        Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).nin("L","C")
                );
            }
        } else {
            criteria.andOperator(
                    Criteria.where(LINEA_TIEMPO_CODIGO_ESTADO_ACTA).in("E","R")
            );
        }
        Query query = Query.query(criteria);

        return mongoOperations.find(query, VwPrActa.class);
    }

    public Map<String, Object> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo, Long idLocalVotacion, Integer idDistritoElectoral) {

        Criteria trueCriteria = getCriteria(idEleccion, idAmbitoGeografico, nubigeoNivel01, nubigeoNivel02, idUbigeo, idLocalVotacion, idDistritoElectoral);

        Criteria falseCriteria = getCriteria(idEleccion, idAmbitoGeografico, nubigeoNivel01, nubigeoNivel02, idUbigeo, idLocalVotacion, idDistritoElectoral);

        // Criteria for "true" results
        trueCriteria.andOperator(
                Criteria.where(CODIGO_ESTADO_ACTA).in("C")
        );

        // Criteria for "false" results
        falseCriteria.andOperator(
                Criteria.where(CODIGO_ESTADO_ACTA).in("E")
        );

        // Query for true results
        trueCriteria.and(ESTADO_ACTA).nin("N","R","Q");
        Query trueQuery = Query.query(trueCriteria);

        long trueTotal = mongoOperations.find(trueQuery, VwPrActa.class).stream()
                .filter(vw -> {
                    var lineaTiempo = vw.getLineaTiempo();
                    if (lineaTiempo == null || lineaTiempo.isEmpty()) return false;
                    return lineaTiempo.stream().anyMatch(t -> "E".equals(t.getCodigoEstadoActa()));
                })
                .count();
        // Query for false results
        falseCriteria.and(ESTADO_ACTA).nin("N","R","Q");
        Query falseQuery = Query.query(falseCriteria);
        long falseTotal = mongoOperations.count(falseQuery, VwPrActa.class);

        // Combine results and totals
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("trueTotal", trueTotal);
        resultMap.put("falseTotal", falseTotal);
        return resultMap;
    }

    private static Criteria getCriteria(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo, Long idLocalVotacion,Integer idDistritoElectoral) {
        Criteria trueCriteria = new Criteria();
        if(idEleccion != 0) {
            trueCriteria.and(ID_ELECCION).is(idEleccion);
        }
        if(null != idDistritoElectoral && idDistritoElectoral != 0 && idDistritoElectoral != 30){
            trueCriteria.and(ID_DISTRITO_ELECTORAL).is(idDistritoElectoral);
        }
        if(null != idAmbitoGeografico && idAmbitoGeografico != 0) {
            trueCriteria.and(ID_AMBITO_GEOGRAFICO).is(idAmbitoGeografico);
        }
        if(null != nubigeoNivel01 && nubigeoNivel01 != 0) {
            trueCriteria.and(NUBIGEO_NIVEL_01).is(nubigeoNivel01);
        }
        if(null != nubigeoNivel02 && nubigeoNivel02 != 0) {
            trueCriteria.and(NUBIGEO_NIVEL_02).is(nubigeoNivel02);
        }
        if(null != idUbigeo && idUbigeo != 0) {
            trueCriteria.and(ID_UBIGEO).is(idUbigeo);
        }
        if(null != idLocalVotacion && idLocalVotacion != 0) {
            trueCriteria.and(ID_LOCAL_VOTACION).is(idLocalVotacion);
        }
        return trueCriteria;
    }


}
