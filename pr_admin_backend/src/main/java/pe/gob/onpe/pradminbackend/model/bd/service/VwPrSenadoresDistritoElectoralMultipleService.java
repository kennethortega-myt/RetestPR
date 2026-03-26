package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrSenadoresDistritoElectoralMultipleService extends CrudService<VwPrSenadoresDistritoElectoralMultiple> {
	List<TramaVistaFilaResponse> actualizarDistritoElectoralMultiple(List<VwPrSenadoresDistritoElectoralMultiple> listaParlamentoActualizar, Long idActa, String usuario);
}