package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.pradminbackend.model.bd.repository.ParticipacionCiudadanaRepository;
import pe.gob.onpe.pradminbackend.model.bd.repository.ParticipacionCiudadanaRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.FiltroParticipacionDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ParticipacionTotalesResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipacionCiudadanaServiceImpl implements ParticipacionCiudadanaService {

	private final ParticipacionCiudadanaRepository participacionCiudadanaRepository;
	private final ParticipacionCiudadanaRepositoryCustom participacionCiudadanaRepositoryCustom;

	@Override
	public void save(VwPrParticipacionCiudadana k) {
		this.participacionCiudadanaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrParticipacionCiudadana> k) {
		this.participacionCiudadanaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.participacionCiudadanaRepository.deleteAll();
	}

	@Override
	public List<VwPrParticipacionCiudadana> findAll() {
		return this.participacionCiudadanaRepository.findAll();
	}
	@Override
	public List<TramaVistaFilaResponse> actualizarParticipacion(List<VwPrParticipacionCiudadana> listaParticipacionActualizar, Long idActa, String usuario) {
		List<TramaVistaFilaResponse> filasActualizados = new ArrayList<>();
		listaParticipacionActualizar.forEach(resume -> {
			boolean filaActualizado = false;
			TramaVistaFilaResponse fila = TramaVistaFilaResponse.builder().build();
			try {
				fila.setIdFila(resume.getId());
				filaActualizado = participacionCiudadanaRepositoryCustom.actualizarParticipacionCiudadana(resume, idActa, usuario);
				fila.setRecibido(filaActualizado);
			}catch(Exception e) {
				log.error("Error al actualizar en PR Acta: {} , Fila: {} , Mensaje: {}",idActa, resume.getId(), e.getMessage());
				fila.setRecibido(false);
				fila.setMensaje("Motivo: {}" + e.getMessage());
			}
			filasActualizados.add(fila);
		});

		return filasActualizados;
	}

    @Override
    public Optional<ParticipacionTotalesResponseDto> obtenerTotales(FiltroParticipacionDto filtros) {

        Predicate<FiltroParticipacionDto> tienefiltro = data -> data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
        Predicate<FiltroParticipacionDto> tieneAmbito = data -> data.getIdAmbitoGeografico() != null && data.getIdAmbitoGeografico() != 0;
        Predicate<FiltroParticipacionDto> tieneUbigeo1 = data -> data.getUbigeoNivel01() != null && data.getUbigeoNivel01() != 0;
        Predicate<FiltroParticipacionDto> tieneUbigeo2 = data -> data.getUbigeoNivel02() != null && data.getUbigeoNivel02() != 0;
        Predicate<FiltroParticipacionDto> tieneUbigeo3 = data -> data.getUbigeoNivel03() != null && data.getUbigeoNivel03() != 0;
        Predicate<FiltroParticipacionDto> tieneLocalVotacion = data -> data.getIdLocalVotacion() != null && data.getIdLocalVotacion().compareTo(0L) != 0;


        if(tienefiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
            return participacionCiudadanaRepository.findByTipoFiltro(filtros.getTipoFiltro())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        } else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){

            return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        } else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
            return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        } else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
            return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        } else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).and(tieneLocalVotacion.negate()).test(filtros)){
            return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        } else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).and(tieneLocalVotacion).test(filtros)){
            return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03AndIdLocalVotacion(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03(),filtros.getIdLocalVotacion())
                    .stream()
                    .findAny()
                    .map(ParticipacionCiudadanaServiceImpl::mapperTotales);
        }
        log.info("servicio de participación ciudadana - obtenerTotales - request no mapeado:  " + filtros);
        return Optional.empty();
    }

    private static ParticipacionTotalesResponseDto mapperTotales(VwPrParticipacionCiudadana registroUnico){

        return ParticipacionTotalesResponseDto.builder()
                .totalAusentes(registroUnico.getTotalAusentes())
                .totalElectoresHabiles(registroUnico.getTotalElectoresHabiles())
                .totalAsistentes(registroUnico.getTotalAsistentes())
                .porcentajeAsistentes(registroUnico.getPorcentajeAsistentes())
                .porcentajeAusentes(registroUnico.getPorcentajeAusentes())
                .build();
    }


}
