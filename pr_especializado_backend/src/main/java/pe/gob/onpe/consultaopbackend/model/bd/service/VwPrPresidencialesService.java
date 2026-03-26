package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrPresidenciales;
import pe.gob.onpe.consultaopbackend.model.dto.eleccionpresidencial.*;

import java.util.List;

public interface VwPrPresidencialesService extends CrudService<VwPrPresidenciales> {

    List<ParticipantePresidencialDto> listarParticipantesUbicacionGeografica(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);
    List<ParticipantePresidencialDto> listarParticipantesUbicacionGeograficaNombre(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);
    List<ParticipantePresidencialDto> listarParticipantesOrganizacionPolitica(FiltroEleccionPresidencialDto filtroEleccionPresidencialDto);

    //reporte
    List<ParticipantePresidencialReporteDto> listarParticipantesUbicacionGeograficaReporte(FiltroEleccionPresidencialReporteDto filtros);
    List<ParticipantePresidencialReporteDto> listarParticipantesUbicacionResumenGeneral(FiltroEleccionPresidencialReporteDto filtros);

    List<ParticipantePresidencialOrganizacionReporteDto> listarParticipantesOrganizacionPoliticaReporte(FiltroEleccionPresidencialReporteDto filtros);



}

