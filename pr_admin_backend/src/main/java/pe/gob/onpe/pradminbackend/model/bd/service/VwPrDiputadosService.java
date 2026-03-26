package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrDiputados;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrDiputadosService extends CrudService<VwPrDiputados> {
	List<TramaVistaFilaResponse> actualizarEleccionDiputado(List<VwPrDiputados> listaDiputadoActualizar, Long idActa, String usuario);
}
