package pe.gob.onpe.pradminbackend.model.bd.service;



import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrRevocatoriaDistrital;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrRevocatoriaDistritalService extends CrudService<VwPrRevocatoriaDistrital> {
	List<TramaVistaFilaResponse> actualizarRevocatoriaDistrital(List<VwPrRevocatoriaDistrital> listaRevocatoriaDistritalActualizar, Long idActa, String usuario);
}
