package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrParlamentoAndinoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrParlamentoAndinoService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class VwPrParlamentoAndinoServiceImpl implements VwPrParlamentoAndinoService {

	private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;

	@Override
	public void save(VwPrParlamentoAndino k) {
		this.vwPrParlamentoAndinoRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrParlamentoAndino> k) {
		this.vwPrParlamentoAndinoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrParlamentoAndinoRepository.deleteAll();
	}

	@Override
	public List<VwPrParlamentoAndino> findAll() {
		return this.vwPrParlamentoAndinoRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarParlamentoAndino(List<VwPrParlamentoAndino> listaParlamentoActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaParlamentoActualizar.forEach(parlamentoAndino -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrParlamentoAndino> registro = vwPrParlamentoAndinoRepository.findById(parlamentoAndino.getId());
				if(registro.isPresent()) {
					VwPrParlamentoAndino registroActualizar = mapperCamposActualizar(parlamentoAndino, registro.get(),idActa, usuario);
					vwPrParlamentoAndinoRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, parlamentoAndino.getId());
				}
				fila.setIdFila(parlamentoAndino.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarParlamentoAndino en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, parlamentoAndino.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

	

	VwPrParlamentoAndino mapperCamposActualizar(VwPrParlamentoAndino registroNuevo, VwPrParlamentoAndino registroActual,Long idActa, String usuario){
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}


	private List<VwPrParlamentoAndinoHistorico> obtenerHistoricos(VwPrParlamentoAndino vistaCongresalActual, Long acta, String usuario) {

		List<VwPrParlamentoAndinoHistorico> historicoTotal;
		if (vistaCongresalActual.getHistorico() != null) {
			historicoTotal = vistaCongresalActual.getHistorico();
			historicoTotal.add(this.mapperParlamentoHistorico(vistaCongresalActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperParlamentoHistorico(vistaCongresalActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrParlamentoAndinoHistorico mapperParlamentoHistorico(VwPrParlamentoAndino parlamentoActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if (parlamentoActual.getDetalle() != null) {
			detalleList = parlamentoActual.getDetalle().stream()
					.map(data -> {
						VwPrEleccionBaseDetalle detallepah = new VwPrEleccionBaseDetalle();
			            detallepah.setAgrupacionPolitica(data.getAgrupacionPolitica());
			            detallepah.setCodigo(data.getCodigo());
			            detallepah.setEstado(data.getEstado());
			            detallepah.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
			            detallepah.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
			            detallepah.setDescripcion(data.getDescripcion());
			            detallepah.setVotos(data.getVotos());
			            detallepah.setGrafico(data.getGrafico());
			            detallepah.setPosicion(data.getPosicion());
			            detallepah.setCandidato(buildCandidato(data));	    
			            return detallepah;
					})
					.toList();
		}
		return VwPrParlamentoAndinoHistorico.builder()
				.id(parlamentoActual.getId())
				.acta(acta)
				.totalElectoresHabiles(parlamentoActual.getTotalElectoresHabiles())
				.totalActas(parlamentoActual.getTotalActas())
				.participacionCiudadana(parlamentoActual.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(parlamentoActual.getPorcentajeParticipacionCiudadana())
				.actasContabilizadas(parlamentoActual.getActasContabilizadas())
				.porcentajeActasContabilizadas(parlamentoActual.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(parlamentoActual.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(parlamentoActual.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(parlamentoActual.getActasPendientes())
				.porcentajeActasPendientes(parlamentoActual.getPorcentajeActasPendientes())
				.totalVotosEmitidos(parlamentoActual.getTotalVotosEmitidos())
				.totalVotosValidos(parlamentoActual.getTotalVotosValidos())
				.audUsuarioModificacion(usuario)
				.audFechaModificacion(new Date())
				.detalle(detalleList)
				.build();
	}

	private  List<VwPrEleccionBaseDetalleCandidato> buildCandidato(VwPrEleccionBaseDetalle data) {
		return data.getCandidato() != null ? data.getCandidato().stream().map(candidato -> {
				VwPrEleccionBaseDetalleCandidato datap = new VwPrEleccionBaseDetalleCandidato();
				datap.setId(candidato.getId());
				datap.setLista(candidato.getLista());
				datap.setVotos(candidato.getVotos());
                return datap;
		}).toList() : Collections.emptyList();
	}

}
