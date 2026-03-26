package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import org.springframework.data.domain.Page;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrRevocatoriaDistrital;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteReqDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteResDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.TotalesDistritalesDto;

public interface VwPrRevocatoriaDistritalService extends CrudService<VwPrRevocatoriaDistrital> {
	Page<ParticipanteDto> listarParticipantes(String cargo);
	List<ParticipanteDto> listarParticipantesv1(String cargo);
	TotalesDistritalesDto obtenerTotales();
	List<ParticipanteResDto> listarParticipantesUbicacionGeografica(ParticipanteReqDto participanteReq);
}
