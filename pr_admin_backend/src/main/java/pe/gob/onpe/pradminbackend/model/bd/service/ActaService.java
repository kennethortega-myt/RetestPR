package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface ActaService extends CrudService<VwPrActa> {
	List<TramaVistaFilaResponse> actualizarActas(List<VwPrActa> listaActasActualizar, Long idActa, String usuario);
}
