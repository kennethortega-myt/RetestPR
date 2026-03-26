package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.consultaopbackend.model.dto.maeeleccion.MaeEleccionSelectResDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.EleccionesMenuResponse;

import java.util.List;
import java.util.Optional;

public interface MaeEleccionService extends CrudService<MaeEleccion> {

    void delete(Long idCentroComputo, String proceso);

    List<MaeEleccion> findAll();

    List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo);
    
    List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProceso(Long idProceso);
    List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByProcesoForConfigReport(Long idProceso);
    List<MaeEleccionSelectResDto> obtenerMaeEleccionSelectByReporteActas(Long idProceso);

    /*--INICIO CODIGO TEMPORAL SOLO HASTA QUE EXISTA LA VISTAS EN BASE DE DATOS*/
    Optional<MaeEleccion> findById(Long id);
    /*--FIN CODIGO TEMPORAL SOLO HASTA QUE EXISTA LA VISTAS EN BASE DE DATOS*/
}
