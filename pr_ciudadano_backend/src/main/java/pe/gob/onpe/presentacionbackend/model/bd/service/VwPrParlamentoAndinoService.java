package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrParlamentoAndino;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.*;

public interface VwPrParlamentoAndinoService extends CrudService<VwPrParlamentoAndino> {

	List<ParticipanteParlamentoAndinoDto> listarParticipantesUbicacionGeografica(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto);
	Optional<ParlamentoCandidatosPaginaResponseDto> listarParticipantesUbicacionGeograficaPaginado(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto, int pagina, int tamanio);

	List<ParticipanteParlamentoAndinoDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto);
	List<ParticipanteCandidatoParlamentoAndinoDto> listarParticipantesPorCandidato(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto);
	List<ParticipanteCandidatoParlamentoAndinoDto> listarParticipantesPorOrganizacionPoliticaNombreCandidato(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto);

	List<OrganizacionPoliticaDto> listarOrganizacionPolitica();

}
