package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.pradminbackend.model.bd.repository.ActaRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.ActaRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.service.ActaService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class ActaServiceImpl implements ActaService {

	private final ActaRepository actaRepository;
	private final ActaRepositoryCustom actaRepositoryCustom;
	
	public ActaServiceImpl(ActaRepository actaRepository, ActaRepositoryCustom actaRepositoryCustom) {
		super();
		this.actaRepository = actaRepository;
		this.actaRepositoryCustom = actaRepositoryCustom;
	}

	@Override
	public void save(VwPrActa k) {
		this.actaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrActa> k) {
		this.actaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.actaRepository.deleteAll();
	}

	@Override
	public List<VwPrActa> findAll() {
		return this.actaRepository.findAll();
	}
	
	@Override
	public List<TramaVistaFilaResponse> actualizarActas(List<VwPrActa> listaActasActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaActasActualizar.forEach(actaNueva -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				fila.setIdFila(actaNueva.getId().intValue());
				filaActualizado = actaRepositoryCustom.actualizarActa(actaNueva, idActa, usuario);
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizar en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, actaNueva.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}
}
