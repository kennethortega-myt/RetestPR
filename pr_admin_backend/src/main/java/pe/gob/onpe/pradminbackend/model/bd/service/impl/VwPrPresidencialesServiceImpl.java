package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrPresidencialesRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrPresidencialesService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VwPrPresidencialesServiceImpl implements VwPrPresidencialesService {

	private final VwPrPresidencialesRepository vwPrPresidencialesRepository;

	@Override
	public void save(VwPrPresidenciales k) {
		this.vwPrPresidencialesRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrPresidenciales> k) {
		this.vwPrPresidencialesRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrPresidencialesRepository.deleteAll();
	}

	@Override
	public List<VwPrPresidenciales> findAll() {
		return this.vwPrPresidencialesRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarEleccionPresidencial(List<VwPrPresidenciales> listaPresidencialActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaPresidencialActualizar.forEach(presidencial -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrPresidenciales> registro = vwPrPresidencialesRepository.findById(presidencial.getId());
				if(registro.isPresent()) {
					VwPrPresidenciales registroActualizar = mapperCamposActualizar(presidencial, registro.get(),idActa, usuario);
					vwPrPresidencialesRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, presidencial.getId());
				}
				fila.setIdFila(presidencial.getId());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarEleccionPresidencial en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, presidencial.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

	VwPrPresidenciales mapperCamposActualizar(VwPrPresidenciales registroNuevo, VwPrPresidenciales registroActual,Long idActa, String usuario){
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}

	private List<VwPrPresidencialesHistorico> obtenerHistoricos(VwPrPresidenciales vistaPresidencialActual, Long acta, String usuario) {

		List<VwPrPresidencialesHistorico> historicoTotal;
		if (vistaPresidencialActual.getHistorico() != null) {
			historicoTotal = vistaPresidencialActual.getHistorico();
			historicoTotal.add(this.mapperPresidencialHistorico(vistaPresidencialActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperPresidencialHistorico(vistaPresidencialActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrPresidencialesHistorico mapperPresidencialHistorico(VwPrPresidenciales presidencialActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if(presidencialActual.getDetalle() != null) {
			detalleList = presidencialActual.getDetalle().stream()
					.map(data -> {
						List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
						if(data.getCandidato() != null ) {
							candidatoList = data.getCandidato().stream()
									.map(candidato -> {
										VwPrEleccionBaseDetalleCandidato dataPresih = new VwPrEleccionBaseDetalleCandidato();
												dataPresih.setNombres(candidato.getNombres());
												dataPresih.setApellidoPaterno(candidato.getApellidoMaterno());
												dataPresih.setApellidoMaterno(candidato.getApellidoPaterno());
												dataPresih.setCargo(candidato.getCargo());
												dataPresih.setDocumentoIdentidad(candidato.getDocumentoIdentidad());
										return dataPresih;
									})
									.toList();
						}
						VwPrEleccionBaseDetalle detallepresih = new VwPrEleccionBaseDetalle();
			            detallepresih.setAgrupacionPolitica(data.getAgrupacionPolitica());
			            detallepresih.setCodigo(data.getCodigo());
			            detallepresih.setEstado(data.getEstado());
			            detallepresih.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
			            detallepresih.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
			            detallepresih.setDescripcion(data.getDescripcion());
			            detallepresih.setVotos(data.getVotos());
			            detallepresih.setGrafico(data.getGrafico());
			            detallepresih.setPosicion(data.getPosicion());
			            detallepresih.setCandidato(candidatoList);
			            return detallepresih;
					})
					.toList();
		}
		return VwPrPresidencialesHistorico.builder()
				.id(presidencialActual.getId())
				.acta(acta)
				.totalElectoresHabiles(presidencialActual.getTotalElectoresHabiles())
				.totalActas(presidencialActual.getTotalActas())
				.participacionCiudadana(presidencialActual.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(presidencialActual.getPorcentajeParticipacionCiudadana())
				.actasContabilizadas(presidencialActual.getActasContabilizadas())
				.porcentajeActasContabilizadas(presidencialActual.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(presidencialActual.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(presidencialActual.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(presidencialActual.getActasPendientes())
				.porcentajeActasPendientes(presidencialActual.getPorcentajeActasPendientes())
				.totalVotosEmitidos(presidencialActual.getTotalVotosEmitidos())
				.totalVotosValidos(presidencialActual.getTotalVotosValidos())
				.audUsuarioModificacion(usuario)
				.audFechaModificacion(new Date())
				.detalle(detalleList)
				.build();
	}


}
