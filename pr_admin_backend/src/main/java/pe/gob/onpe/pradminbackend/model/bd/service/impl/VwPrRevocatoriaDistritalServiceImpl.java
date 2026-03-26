package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalleCandidato;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrRevocatoriaDistrital;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrRevocatoriaDistritalHistorico;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrRevocatoriaDistritalRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrRevocatoriaDistritalService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwPrRevocatoriaDistritalServiceImpl implements VwPrRevocatoriaDistritalService {
	
	private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

	@Override
	public void save(VwPrRevocatoriaDistrital k) {
		this.vwPrRevocatoriaDistritalRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrRevocatoriaDistrital> k) {
		this.vwPrRevocatoriaDistritalRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrRevocatoriaDistritalRepository.deleteAll();
		
	}

	@Override
	public List<VwPrRevocatoriaDistrital> findAll() {
		return this.vwPrRevocatoriaDistritalRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarRevocatoriaDistrital(List<VwPrRevocatoriaDistrital> listaRevocatoriaDistritalActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaRevocatoriaDistritalActualizar.forEach(revocatoriadistrital -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrRevocatoriaDistrital> registro = vwPrRevocatoriaDistritalRepository.findById(revocatoriadistrital.getId());
				if(registro.isPresent()) {
					VwPrRevocatoriaDistrital registroActualizar = mapperCamposActualizar(revocatoriadistrital, registro.get(),idActa, usuario);
					vwPrRevocatoriaDistritalRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, revocatoriadistrital.getId());
				}
				fila.setIdFila(revocatoriadistrital.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarRevocatoriaDistrital en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, revocatoriadistrital.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}
	
	VwPrRevocatoriaDistrital mapperCamposActualizar(VwPrRevocatoriaDistrital registroNuevo, VwPrRevocatoriaDistrital registroActual,Long idActa, String usuario) {
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}
	
	private List<VwPrRevocatoriaDistritalHistorico> obtenerHistoricos(VwPrRevocatoriaDistrital vistaActual, Long acta, String usuario) {

		List<VwPrRevocatoriaDistritalHistorico> historicoTotal;
		if (vistaActual.getHistorico() != null) {
			historicoTotal = vistaActual.getHistorico();
			historicoTotal.add(this.mapperSenadoresMultipleHistorico(vistaActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperSenadoresMultipleHistorico(vistaActual, acta, usuario));
		}
		return historicoTotal;
	}
	
	private VwPrRevocatoriaDistritalHistorico mapperSenadoresMultipleHistorico(VwPrRevocatoriaDistrital registroActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if (registroActual.getDetalle() != null) {
			detalleList = registroActual.getDetalle().stream()
					.map(data -> {
						List<VwPrEleccionBaseDetalleCandidato> lstCandidatoRd = Collections.emptyList();
						if(data.getCandidato() != null ) {
							lstCandidatoRd = data.getCandidato().stream()
									.map(candidatoRd ->{
										VwPrEleccionBaseDetalleCandidato datadem = new VwPrEleccionBaseDetalleCandidato();
										datadem.setId(candidatoRd.getId());
										datadem.setLista(candidatoRd.getLista());
										datadem.setVotos(candidatoRd.getVotos());
					                	return datadem;
									})
									.toList();
						}
						VwPrEleccionBaseDetalle detallesdemh = new VwPrEleccionBaseDetalle();
			            detallesdemh.setAgrupacionPolitica(data.getAgrupacionPolitica());
			            detallesdemh.setCodigo(data.getCodigo());
			            detallesdemh.setEstado(data.getEstado());
			            detallesdemh.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
			            detallesdemh.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
			            detallesdemh.setDescripcion(data.getDescripcion());
			            detallesdemh.setVotos(data.getVotos());
			            detallesdemh.setGrafico(data.getGrafico());
			            detallesdemh.setPosicion(data.getPosicion());
			            detallesdemh.setCandidato(lstCandidatoRd);
			            return detallesdemh;
					})
					.toList();
		}
		return VwPrRevocatoriaDistritalHistorico.builder()
				.id(registroActual.getId())
				.acta(acta)
				.totalElectoresHabiles(registroActual.getTotalElectoresHabiles())
				.totalActas(registroActual.getTotalActas())
				.participacionCiudadana(registroActual.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(registroActual.getPorcentajeParticipacionCiudadana())
				.actasContabilizadas(registroActual.getActasContabilizadas())
				.porcentajeActasContabilizadas(registroActual.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(registroActual.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(registroActual.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(registroActual.getActasPendientes())
				.porcentajeActasPendientes(registroActual.getPorcentajeActasPendientes())
				.totalVotosEmitidos(registroActual.getTotalVotosEmitidos())
				.totalVotosValidos(registroActual.getTotalVotosValidos())
				.audUsuarioModificacion(usuario)
				.audFechaModificacion(new Date())
				.detalle(detalleList)
				.build();
	}
	
}
