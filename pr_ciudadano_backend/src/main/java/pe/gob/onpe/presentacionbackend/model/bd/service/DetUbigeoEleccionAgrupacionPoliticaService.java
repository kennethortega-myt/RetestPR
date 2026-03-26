package pe.gob.onpe.presentacionbackend.model.bd.service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccionAgrupacionPolitica;

public interface DetUbigeoEleccionAgrupacionPoliticaService extends CrudService<DetUbigeoEleccionAgrupacionPolitica> {

    void delete(Long idCentroComputo, String proceso);

}
