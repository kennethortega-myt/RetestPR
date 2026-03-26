package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.presentacionbackend.model.dto.distritoelectoral.DistritosResponseDto;

public interface MaeDistritoElectoralService extends CrudService<MaeDistritoElectoral> {
	List<DistritosResponseDto> listarDistritos();
}
