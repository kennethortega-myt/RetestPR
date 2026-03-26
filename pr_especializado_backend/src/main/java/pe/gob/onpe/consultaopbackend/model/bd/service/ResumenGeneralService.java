package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.*;

import java.util.List;
import java.util.Optional;

public interface ResumenGeneralService {

    Optional<ActaResDto> obtenerTotalesPorEleccion(FiltroActaEleccionDto filtros);
    Optional<ActaRespuestaReporteDto> obtenerTotalesPorEleccionParaReporte(FiltroActaEleccionReporteDto filtros);
     List<ActaMapaCalorResponseDto> listarMapaCalor(ActaMapaCalorRequestDto filtros, String actaCodigoEstado);
    List<VistaResumenGeneralDto> obtenerElecciones(FiltroEleccionesDto filtro);
}
