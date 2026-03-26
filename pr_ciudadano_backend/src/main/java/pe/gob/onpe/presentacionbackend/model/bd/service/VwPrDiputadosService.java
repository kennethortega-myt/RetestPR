package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrDiputados;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteNombreDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.OrganizacionPoliticaDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteCandidatoDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteDiputadoDto;

public interface VwPrDiputadosService extends CrudService<VwPrDiputados> {
	
	List<ParticipanteDiputadoDto> listarParticipantesUbicacionGeografica(FiltroParticipanteDiputadoDto filtroParticipanteDto);
	List<ParticipanteDiputadoDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteDiputadoDto filtroParticipanteDto);
	List<ParticipanteCandidatoDiputadoDto> obtenerParticipantes(FiltroParticipanteDiputadoDto filtroParticipanteDto);
	List<ParticipanteCandidatoDiputadoDto> obtenerParticipantesNombre(FiltroParticipanteNombreDiputadoDto filtroParticipanteNombreDto);
	List<OrganizacionPoliticaDto> listarOrganizacionPolitica(FiltroParticipanteDiputadoDto filtroParticipanteDto);

}
