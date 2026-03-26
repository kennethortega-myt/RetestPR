package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.*;

import java.util.List;
import java.util.Optional;

public interface ParticipacionCiudadanaService extends CrudService<VwPrParticipacionCiudadana> {


    Optional<ParticipacionTotalesResponseDto> obtenerTotales(FiltroParticipacionDto filtroParticipacionTotalesDto);
    Optional<ParticipacionDetalleResponseDto> listarUbigeos(FiltroParticipacionDto filtroParticipacionTotalesDto,int pagina, int tamanio);

    List<ParticipacionUbigeosResponseDto> listarUbigeosTotal(FiltroParticipacionDto filtroParticipacionTotalesDto);


    //reporte
    List<ParticipacionTotalesResponseReporteDto> listarUbigeosReporte(FiltroParticipacionReporteDto filtros);

}
