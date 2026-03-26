package pe.gob.onpe.pradminbackend.model.bd.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParticipacionCiudadanaHistorico;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ParticipacionCiudadanaRepositoryCustom {

	private final MongoOperations mongoOperations;

	public boolean actualizarParticipacionCiudadana(VwPrParticipacionCiudadana vistaParticipacionNuevo, Long idActa, String usuario) {
		boolean actualizado = false;
		Query query = new Query(
				Criteria.where("id").is(vistaParticipacionNuevo.getId())
		);
		VwPrParticipacionCiudadana vistaParticipacionActual = mongoOperations.findOne(query, VwPrParticipacionCiudadana.class);

		if ( null != vistaParticipacionActual) {
			Update update = mapearCamposActualizar(vistaParticipacionNuevo, vistaParticipacionActual , idActa, usuario);
			mongoOperations.updateFirst(query, update, VwPrParticipacionCiudadana.class);
			actualizado = true;
		}else {
			log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, vistaParticipacionNuevo.getId());
		}

		return actualizado;

	}
	
	private Update mapearCamposActualizar(VwPrParticipacionCiudadana participacionNuevo, VwPrParticipacionCiudadana participacionActual, Long idActa, String usuario) {
		Update update = new Update();

		if(participacionNuevo.getTipoFiltro() != null) {
			update.set("tipoFiltro",participacionNuevo.getTipoFiltro());
		}
		if(participacionNuevo.getAmbitoGeografico() != null) {
			update.set("ambitoGeografico",participacionNuevo.getAmbitoGeografico());
		}
		if(participacionNuevo.getUbigeoNivel01() != null) {
			update.set("ubigeoNivel01",participacionNuevo.getUbigeoNivel01());
		}
		if(participacionNuevo.getUbigeoNivel02() != null) {
			update.set("ubigeoNivel02",participacionNuevo.getUbigeoNivel02());
		}
		if(participacionNuevo.getUbigeoNivel03() != null) {
			update.set("ubigeoNivel03",participacionNuevo.getUbigeoNivel03());
		}
		if(participacionNuevo.getIdLocalVotacion() != null) {
			update.set("idLocalVotacion",participacionNuevo.getIdLocalVotacion());
		}
		if(participacionNuevo.getTotalElectoresHabiles() != null) {
			update.set("totalElectoresHabiles",participacionNuevo.getTotalElectoresHabiles());
		}
		if(participacionNuevo.getTotalAsistentes() != null) {
			update.set("totalAsistentes",participacionNuevo.getTotalAsistentes());
		}
		if(participacionNuevo.getTotalAusentes() != null) {
			update.set("totalAusentes",participacionNuevo.getTotalAusentes());
		}
		if(participacionNuevo.getPorcentajeAsistentes() != null) {
			update.set("porcentajeAsistentes",participacionNuevo.getPorcentajeAsistentes());
		}
		if(participacionNuevo.getPorcentajeAusentes() != null) {
			update.set("porcentajeAusentes",participacionNuevo.getPorcentajeAusentes());
		}

		update.set("c_historico", obtenerHistoricos(participacionActual, idActa, usuario));

		return update;
	}

	private List<VwPrParticipacionCiudadanaHistorico> obtenerHistoricos(VwPrParticipacionCiudadana participacionActual, Long acta, String usuario) {

		List<VwPrParticipacionCiudadanaHistorico> historicoTotal;
		if (participacionActual.getCHistorico() != null) {
			historicoTotal = participacionActual.getCHistorico();
			historicoTotal.add(this.mapperParticipacionHistorico(participacionActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperParticipacionHistorico(participacionActual, acta, usuario));
		}

		return historicoTotal;
	}
	
	private VwPrParticipacionCiudadanaHistorico mapperParticipacionHistorico(VwPrParticipacionCiudadana participacionActual, Long acta, String usuario) {

		VwPrParticipacionCiudadanaHistorico historico = new VwPrParticipacionCiudadanaHistorico();

		historico.setId(participacionActual.getId());
		historico.setNActa(acta);
		historico.setTipoFiltro(participacionActual.getTipoFiltro());
		historico.setAmbitoGeografico(participacionActual.getAmbitoGeografico());
		historico.setUbigeoNivel01(participacionActual.getUbigeoNivel01());
		historico.setUbigeoNivel02(participacionActual.getUbigeoNivel02());
		historico.setUbigeoNivel03(participacionActual.getUbigeoNivel03());
		historico.setIdLocalVotacion(participacionActual.getIdLocalVotacion());
		historico.setTotalElectoresHabiles(participacionActual.getTotalElectoresHabiles());
		historico.setTotalAsistentes(participacionActual.getTotalAsistentes());
		historico.setTotalAusentes(participacionActual.getTotalAusentes());
		historico.setPorcentajeAsistentes(participacionActual.getPorcentajeAsistentes());
		historico.setPorcentajeAusentes(participacionActual.getPorcentajeAusentes());
		historico.setAudUsuarioModificacion(usuario);
		historico.setAudFechaModificacion(new Date());

		return historico;
	}

}
