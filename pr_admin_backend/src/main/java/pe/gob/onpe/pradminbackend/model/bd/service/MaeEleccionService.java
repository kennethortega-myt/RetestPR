package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.pradminbackend.model.dto.maeeleccion.MaeEleccionSelectResDto;
import pe.gob.onpe.pradminbackend.model.dto.response.EleccionesMenuResponse;

import java.util.List;

public interface MaeEleccionService extends CrudService<MaeEleccion> {
    List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo);
    List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProcesoForConfigReport(Long idProceso);
    List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProceso(Long idProceso);

}
