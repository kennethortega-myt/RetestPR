package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.FiltroParticipacionDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ParticipacionTotalesResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface ParticipacionCiudadanaService extends CrudService<VwPrParticipacionCiudadana> {
    List<TramaVistaFilaResponse> actualizarParticipacion(List<VwPrParticipacionCiudadana> listaParticipacionActualizar, Long idActa, String usuario);
    Optional<ParticipacionTotalesResponseDto> obtenerTotales(FiltroParticipacionDto filtroParticipacionTotalesDto);
}
