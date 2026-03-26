package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.consultaopbackend.model.dto.distritoelectoral.DistritosResponseDto;

import java.util.List;

public interface MaeDistritoElectoralService extends CrudService<MaeDistritoElectoral> {
	List<DistritosResponseDto> listarDistritos();
}
