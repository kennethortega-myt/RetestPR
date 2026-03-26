package pe.gob.onpe.presentacionbackend.model.bd.service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccion;

public interface DetUbigeoEleccionService extends CrudService<DetUbigeoEleccion> {

    void delete(Long idCentroComputo, String proceso);

}
