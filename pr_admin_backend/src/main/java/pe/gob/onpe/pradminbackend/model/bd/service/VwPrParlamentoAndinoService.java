package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrParlamentoAndino;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrParlamentoAndinoService extends CrudService<VwPrParlamentoAndino> {
	List<TramaVistaFilaResponse> actualizarParlamentoAndino(List<VwPrParlamentoAndino> listaParlamentoActualizar, Long idActa, String usuario);
}
