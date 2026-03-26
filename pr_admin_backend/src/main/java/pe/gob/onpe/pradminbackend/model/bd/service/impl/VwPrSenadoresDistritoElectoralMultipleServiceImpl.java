package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalleCandidato;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultipleHistorico;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrSenadoresDistritoElectoralMultipleRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrSenadoresDistritoElectoralMultipleService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VwPrSenadoresDistritoElectoralMultipleServiceImpl implements VwPrSenadoresDistritoElectoralMultipleService {
	private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;

	public VwPrSenadoresDistritoElectoralMultipleServiceImpl(
			VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository) {
		super();
		this.vwPrSenadoresDistritoElectoralMultipleRepository = vwPrSenadoresDistritoElectoralMultipleRepository;
	}

	@Override
	public void save(VwPrSenadoresDistritoElectoralMultiple k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrSenadoresDistritoElectoralMultiple> k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.deleteAll();
	}

	@Override
	public List<VwPrSenadoresDistritoElectoralMultiple> findAll() {
		return this.vwPrSenadoresDistritoElectoralMultipleRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarDistritoElectoralMultiple(List<VwPrSenadoresDistritoElectoralMultiple> listaParlamentoActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaParlamentoActualizar.forEach(senadoresdem -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrSenadoresDistritoElectoralMultiple> registro = vwPrSenadoresDistritoElectoralMultipleRepository.findById(senadoresdem.getId());
				if(registro.isPresent()) {
					VwPrSenadoresDistritoElectoralMultiple registroActualizar = mapperCamposActualizar(senadoresdem, registro.get(),idActa, usuario);
					vwPrSenadoresDistritoElectoralMultipleRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, senadoresdem.getId());
				}
				fila.setIdFila(senadoresdem.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarDistritoElectoralMultiple en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, senadoresdem.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}
	
	VwPrSenadoresDistritoElectoralMultiple mapperCamposActualizar(VwPrSenadoresDistritoElectoralMultiple registroNuevo, VwPrSenadoresDistritoElectoralMultiple registroActual,Long idActa, String usuario) {
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}

	private List<VwPrSenadoresDistritoElectoralMultipleHistorico> obtenerHistoricos(VwPrSenadoresDistritoElectoralMultiple vistaActual, Long acta, String usuario) {

		List<VwPrSenadoresDistritoElectoralMultipleHistorico> historicoTotal;
		if (vistaActual.getHistorico() != null) {
			historicoTotal = vistaActual.getHistorico();
			historicoTotal.add(this.mapperSenadoresMultipleHistorico(vistaActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperSenadoresMultipleHistorico(vistaActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrSenadoresDistritoElectoralMultipleHistorico mapperSenadoresMultipleHistorico(VwPrSenadoresDistritoElectoralMultiple registroActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if (registroActual.getDetalle() != null) {
			detalleList = registroActual.getDetalle().stream()
					.map(data -> {
						List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
						if(data.getCandidato() != null ) {
							candidatoList = data.getCandidato().stream()
									.map(candidato ->{
										VwPrEleccionBaseDetalleCandidato datadem = new VwPrEleccionBaseDetalleCandidato();
										datadem.setId(candidato.getId());
										datadem.setLista(candidato.getLista());
										datadem.setVotos(candidato.getVotos());
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
			            detallesdemh.setCandidato(candidatoList);
			            return detallesdemh;
					})
					.toList();
		}
		return VwPrSenadoresDistritoElectoralMultipleHistorico.builder()
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
