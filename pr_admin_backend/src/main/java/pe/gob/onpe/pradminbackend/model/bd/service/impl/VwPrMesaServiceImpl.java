package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrMesa;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrMesaHistorico;
import pe.gob.onpe.pradminbackend.model.bd.repository.VwPrMesaRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.VwPrMesaService;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VwPrMesaServiceImpl implements VwPrMesaService {

	private final VwPrMesaRepository vwPrMesaRepository;

	@Override
	public void save(VwPrMesa k) {
		this.vwPrMesaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrMesa> k) {
		this.vwPrMesaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrMesaRepository.deleteAll();
	}

	@Override
	public List<VwPrMesa> findAll() {
		return this.vwPrMesaRepository.findAll();
	}

	@Override
	public List<TramaVistaFilaResponse> actualizarMesas(List<VwPrMesa> listaMesaActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaMesaActualizar.forEach(mesaDto -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				Optional<VwPrMesa> registro = vwPrMesaRepository.findById(mesaDto.getId());
				if(registro.isPresent()) {
					VwPrMesa registroActualizar = mapperCamposActualizar(mesaDto, registro.get(),idActa, usuario);
					vwPrMesaRepository.save(registroActualizar);
					filaActualizado = true;
				}else {
					log.info("Acta: {},  idFila no encontrado en la bd PR: {}", idActa, mesaDto.getId());
				}
				fila.setIdFila(mesaDto.getId().intValue());
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizarMesas en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, mesaDto.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

	private VwPrMesa mapperCamposActualizar(VwPrMesa registroNuevo, VwPrMesa registroActual, Long idActa, String usuario) {

		if (registroNuevo.getMesasInstaladas() != null ) {
			registroActual.setMesasInstaladas(registroNuevo.getMesasInstaladas());
		}
		if (registroNuevo.getMesasNoInstaladas() != null ) {
			registroActual.setMesasNoInstaladas(registroNuevo.getMesasNoInstaladas());
		}
		if (registroNuevo.getMesasPorInformar() != null ) {
			registroActual.setMesasPorInformar(registroNuevo.getMesasPorInformar());
		}

		registroActual.setCHistorico(obtenerHistoricos(registroActual,idActa,usuario));

		return registroActual;
	}

	private List<VwPrMesaHistorico> obtenerHistoricos(VwPrMesa registroActual, Long idActa, String usuario) {
		List<VwPrMesaHistorico> historicoTotal;
		if (registroActual.getCHistorico() != null && !registroActual.getCHistorico().isEmpty()) {
			historicoTotal = registroActual.getCHistorico();
			historicoTotal.add(this.mapperMesaHistorico(registroActual,idActa,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperMesaHistorico(registroActual, idActa, usuario));
		}
		return historicoTotal;
	}

	private VwPrMesaHistorico mapperMesaHistorico(VwPrMesa registroActual, Long idActa, String usuario) {
		VwPrMesaHistorico historico = new VwPrMesaHistorico();

		historico.setId(registroActual.getId());
		historico.setTotalMesas(registroActual.getTotalMesas());
		historico.setMesasNoInstaladas(registroActual.getMesasNoInstaladas());
		historico.setMesasInstaladas(registroActual.getMesasInstaladas());
		historico.setMesasPorInformar(registroActual.getMesasPorInformar());
		historico.setDistritoElectoral(registroActual.getDistritoElectoral());
		historico.setAmbitoGeografico(registroActual.getAmbitoGeografico());
		historico.setTipoFiltro(registroActual.getTipoFiltro());
		historico.setUbigeoNivel01(registroActual.getUbigeoNivel01());
		historico.setUbigeoNivel02(registroActual.getUbigeoNivel02());
		historico.setUbigeoNivel03(registroActual.getUbigeoNivel03());
		historico.setNActa(idActa);
		historico.setAudUsuarioModificacion(usuario);
		historico.setAudFechaModificacion(new Date());

		return historico;
	}

}
