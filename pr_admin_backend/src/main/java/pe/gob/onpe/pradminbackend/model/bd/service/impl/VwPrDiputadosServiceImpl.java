package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrDiputados;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrDiputadosHistorico;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrEleccionBaseDetalleCandidato;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrDiputadosRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrDiputadosService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class VwPrDiputadosServiceImpl implements VwPrDiputadosService {

	private final VwPrDiputadosRepository vwPrDiputadosRepository;

	public VwPrDiputadosServiceImpl(VwPrDiputadosRepository vwPrDiputadosRepository) {
		super();
		this.vwPrDiputadosRepository = vwPrDiputadosRepository;
	}

	@Override
	public void save(VwPrDiputados k) {
		this.vwPrDiputadosRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrDiputados> k) {
		this.vwPrDiputadosRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrDiputadosRepository.deleteAll();
	}

	@Override
	public List<VwPrDiputados> findAll() {
		return this.vwPrDiputadosRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarEleccionDiputado(List<VwPrDiputados> listaDiputadoActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaDiputadoActualizar.forEach(diputado -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrDiputados> registro = vwPrDiputadosRepository.findById(diputado.getId());
				if(registro.isPresent()) {
					VwPrDiputados registroActualizar = mapperCamposActualizar(diputado, registro.get(),idActa, usuario);
					vwPrDiputadosRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, diputado.getId());
				}
				fila.setIdFila(diputado.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarEleccionDiputado en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, diputado.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

	VwPrDiputados mapperCamposActualizar(VwPrDiputados registroNuevo, VwPrDiputados registroActual,Long idActa, String usuario){
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}

	private List<VwPrDiputadosHistorico> obtenerHistoricos(VwPrDiputados vistaDiputadoActual, Long acta, String usuario) {

		List<VwPrDiputadosHistorico> historicoTotal;
		if (vistaDiputadoActual.getHistorico() != null) {
			historicoTotal = vistaDiputadoActual.getHistorico();
			historicoTotal.add(this.mapperResumenHistorico(vistaDiputadoActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperResumenHistorico(vistaDiputadoActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrDiputadosHistorico mapperResumenHistorico(VwPrDiputados diputadoActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if (diputadoActual.getDetalle() != null) {
		    detalleList = diputadoActual.getDetalle().stream()
		        .map(data -> {
		            List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
		            if (data.getCandidato() != null) {
		                candidatoList = data.getCandidato().stream()
		                    .map(candidato ->{
		                    	VwPrEleccionBaseDetalleCandidato candidatodh = new VwPrEleccionBaseDetalleCandidato();
		                    	candidatodh.setId(candidato.getId());
		                    	candidatodh.setLista(candidato.getLista());
		                    	candidatodh.setVotos(candidato.getVotos());
		                        return candidatodh;
		                    })
		                    .toList();
		            }
		            VwPrEleccionBaseDetalle detalledh = new VwPrEleccionBaseDetalle();
		            detalledh.setAgrupacionPolitica(data.getAgrupacionPolitica());
		            detalledh.setCodigo(data.getCodigo());
		            detalledh.setEstado(data.getEstado());
		            detalledh.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
		            detalledh.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
		            detalledh.setDescripcion(data.getDescripcion());
		            detalledh.setVotos(data.getVotos());
		            detalledh.setGrafico(data.getGrafico());
		            detalledh.setPosicion(data.getPosicion());
		            detalledh.setCandidato(candidatoList);
		            return detalledh;
		        })
		        .toList();
		}

		return VwPrDiputadosHistorico.builder()
		    .id(diputadoActual.getId())
		    .acta(acta)
		    .totalElectoresHabiles(diputadoActual.getTotalElectoresHabiles())
		    .totalActas(diputadoActual.getTotalActas())
		    .participacionCiudadana(diputadoActual.getParticipacionCiudadana())
		    .porcentajeParticipacionCiudadana(diputadoActual.getPorcentajeParticipacionCiudadana())
		    .actasContabilizadas(diputadoActual.getActasContabilizadas())
		    .porcentajeActasContabilizadas(diputadoActual.getPorcentajeActasContabilizadas())
		    .actasObservadasEnviadas(diputadoActual.getActasObservadasEnviadas())
		    .porcentajeActasObservadasEnviadas(diputadoActual.getPorcentajeActasObservadasEnviadas())
		    .actasPendientes(diputadoActual.getActasPendientes())
		    .porcentajeActasPendientes(diputadoActual.getPorcentajeActasPendientes())
		    .totalVotosEmitidos(diputadoActual.getTotalVotosEmitidos())
			.totalVotosValidos(diputadoActual.getTotalVotosValidos())
		    .audUsuarioModificacion(usuario)
		    .audFechaModificacion(new Date())
		    .detalle(detalleList)
		    .build();
	}
	
}
