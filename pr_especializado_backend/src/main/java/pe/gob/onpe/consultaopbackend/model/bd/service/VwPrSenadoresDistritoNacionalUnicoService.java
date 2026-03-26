package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.*;

import java.util.List;

public interface VwPrSenadoresDistritoNacionalUnicoService extends CrudService<VwPrSenadoresDistritoNacionalUnico> {

    List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeografica(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorCandidato(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorOrganizacionPoliticaNombreCandidato(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);

    List<OrganizacionPoliticaSenadoresDto> listarOrganizacionPolitica();

    //reporte
    List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesUbicacionGeograficaReporte(FiltroEleccionSenadoresUnicosReporteDto filtros);
    List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesCandidatoReporte(FiltroEleccionSenadoresUnicosReporteDto filtros);

    List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesOrganizacionReporte(FiltroEleccionSenadoresUnicosReporteDto filtros);

    List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesResumengGeneralReporte(FiltroEleccionSenadoresUnicosReporteDto filtros);
}
