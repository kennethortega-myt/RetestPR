package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.dto.response.EleccionesMenuResponse;

public interface MaeEleccionService extends CrudService<MaeEleccion> {

    void delete(Long idCentroComputo, String proceso);

    List<MaeEleccion> findAll();

    List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo);
    
    /*--INICIO CODIGO TEMPORAL SOLO HASTA QUE EXISTA LA VISTAS EN BASE DE DATOS*/
    Optional<MaeEleccion> findById(Long id);
    /*--FIN CODIGO TEMPORAL SOLO HASTA QUE EXISTA LA VISTAS EN BASE DE DATOS*/
}
