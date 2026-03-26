package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana.*;

public interface ParticipacionCiudadanaService extends CrudService<VwPrParticipacionCiudadana> {

    List<ParticipacionCiudadanaResponseDto> obtenerParticipacionCiudadanaXDep(FiltroParticipacionCiudadana filtroParticipacionCiudadana);

    Optional<ParticipacionTotalesResponseDto> obtenerTotales(FiltroParticipacionDto filtroParticipacionTotalesDto);
    Optional<ParticipacionDetalleResponseDto> listarUbigeos(FiltroParticipacionDto filtroParticipacionTotalesDto,int pagina, int tamanio);

    List<ParticipacionUbigeosResponseDto> listarUbigeosTotal(FiltroParticipacionDto filtroParticipacionTotalesDto);

    List<ActaMapaCalorResponseDto>  	listarMapaCalor(ActaMapaCalorRequestDto filtros);


        //reporte
    List<ParticipacionTotalesResponseReporteDto> listarUbigeosReporte(FiltroParticipacionReporteDto filtros);

}
