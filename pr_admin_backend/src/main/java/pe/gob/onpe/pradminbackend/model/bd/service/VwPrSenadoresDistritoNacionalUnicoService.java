package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrSenadoresDistritoNacionalUnicoService extends CrudService<VwPrSenadoresDistritoNacionalUnico> {
    List<TramaVistaFilaResponse> actualizarDistritoNacionalUnico(List<VwPrSenadoresDistritoNacionalUnico> listaParlamentoActualizar, Long idActa, String usuario);
}
