package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.*;

public interface VwPrSenadoresDistritoNacionalUnicoService extends CrudService<VwPrSenadoresDistritoNacionalUnico> {

    List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeografica(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorCandidato(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorOrganizacionPoliticaNombreCandidato(FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto);
    List<OrganizacionPoliticaSenadoresDto> listarOrganizacionPolitica();

}
