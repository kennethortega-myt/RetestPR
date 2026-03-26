package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.*;

public interface ResumenGeneralService {

    Optional<ActaEleccionDto> obtenerTotalesPorEleccion(FiltroActaEleccionDto filtros);
    List<ParticipanteDto> listarParticipantesPorEleccion(FiltroParticipanteDto filtros);

    List<ActaMapaCalorResponseDto> listarMapaCalor(ActaMapaCalorRequestDto filtros, String actaCodigoEstado);
    
    List<VistaResumenGeneralDto> obtenerElecciones(FiltroEleccionesDto filtro);
    Page<VistaResumenGeneralDto> obtenerRevocatorias(FiltroRevocatoriasDto filtro, int pagina, int tamanio);
    
    List<VistaResumenGeneralDto> obtenerRevocatoriasv1(FiltroRevocatoriasDto filtro);

}
