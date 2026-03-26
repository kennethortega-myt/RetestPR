package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.repository.*;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrSenadoresDistritoNacionalUnicoService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class VwPrSenadoresDistritoNacionalUnicoServiceImpl implements VwPrSenadoresDistritoNacionalUnicoService {

	private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;

	@Override
	public void save(VwPrSenadoresDistritoNacionalUnico k) {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrSenadoresDistritoNacionalUnico> k) {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.deleteAll();
	}

	@Override
	public List<VwPrSenadoresDistritoNacionalUnico> findAll() {
		return this.vwPrSenadoresDistritoNacionalUnicoRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarDistritoNacionalUnico(List<VwPrSenadoresDistritoNacionalUnico> listaParlamentoActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaParlamentoActualizar.forEach(senadoresdnu -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrSenadoresDistritoNacionalUnico> registro = vwPrSenadoresDistritoNacionalUnicoRepository.findById(senadoresdnu.getId());
				if(registro.isPresent()) {
					VwPrSenadoresDistritoNacionalUnico registroActualizar = mapperCamposActualizar(senadoresdnu, registro.get(),idActa, usuario);
					vwPrSenadoresDistritoNacionalUnicoRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, senadoresdnu.getId());
				}
				fila.setIdFila(senadoresdnu.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarDistritoNacionalUnico en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, senadoresdnu.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

	
	
	VwPrSenadoresDistritoNacionalUnico mapperCamposActualizar(VwPrSenadoresDistritoNacionalUnico registroNuevo, VwPrSenadoresDistritoNacionalUnico registroActual,Long idActa, String usuario) {
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}


	private List<VwPrSenadoresDistritoNacionalUnicoHistorico> obtenerHistoricos(VwPrSenadoresDistritoNacionalUnico vistaCongresalActual, Long acta, String usuario) {

		List<VwPrSenadoresDistritoNacionalUnicoHistorico> historicoTotal;
		if (vistaCongresalActual.getHistorico() != null) {
			historicoTotal = vistaCongresalActual.getHistorico();
			historicoTotal.add(this.mapperSenadoresUnicoHistorico(vistaCongresalActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperSenadoresUnicoHistorico(vistaCongresalActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrSenadoresDistritoNacionalUnicoHistorico mapperSenadoresUnicoHistorico(VwPrSenadoresDistritoNacionalUnico parlamentoActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if (parlamentoActual.getDetalle() != null) {
			detalleList = parlamentoActual.getDetalle().stream()
					.map(data -> {
						VwPrEleccionBaseDetalle detallesdnuh = new VwPrEleccionBaseDetalle();
			            detallesdnuh.setAgrupacionPolitica(data.getAgrupacionPolitica());
			            detallesdnuh.setCodigo(data.getCodigo());
			            detallesdnuh.setEstado(data.getEstado());
			            detallesdnuh.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
			            detallesdnuh.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
			            detallesdnuh.setDescripcion(data.getDescripcion());
			            detallesdnuh.setVotos(data.getVotos());
			            detallesdnuh.setGrafico(data.getGrafico());
			            detallesdnuh.setPosicion(data.getPosicion());
			            detallesdnuh.setCandidato(buildCandidato(data));	    
			            return detallesdnuh;
					})
					.toList();
		}
		return VwPrSenadoresDistritoNacionalUnicoHistorico.builder()
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

	private List<VwPrEleccionBaseDetalleCandidato> buildCandidato(VwPrEleccionBaseDetalle data) {
		return data.getCandidato() != null ? data.getCandidato().stream().map(candidato -> {
			VwPrEleccionBaseDetalleCandidato datadeu = new VwPrEleccionBaseDetalleCandidato();
			datadeu.setId(candidato.getId());
			datadeu.setLista(candidato.getLista());
			datadeu.setVotos(candidato.getVotos());
        	return datadeu;
		}).toList() : Collections.emptyList();
	}

	
	

}
