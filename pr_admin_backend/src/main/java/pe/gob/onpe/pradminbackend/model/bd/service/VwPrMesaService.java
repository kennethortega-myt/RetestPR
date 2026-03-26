package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrMesa;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaFilaResponse;

public interface VwPrMesaService extends CrudService<VwPrMesa> {
    List<TramaVistaFilaResponse> actualizarMesas(List<VwPrMesa> listaMesaActualizar, Long idActa, String usuario);
}
