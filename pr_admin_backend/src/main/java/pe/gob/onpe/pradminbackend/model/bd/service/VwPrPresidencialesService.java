package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrPresidenciales;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrPresidencialesService extends CrudService<VwPrPresidenciales> {
    List<TramaVistaFilaResponse> actualizarEleccionPresidencial(List<VwPrPresidenciales> listaPresidencialActualizar, Long idActa, String usuario);
}

