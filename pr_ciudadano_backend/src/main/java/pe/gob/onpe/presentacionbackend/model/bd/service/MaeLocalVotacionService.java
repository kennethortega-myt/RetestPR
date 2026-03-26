package pe.gob.onpe.presentacionbackend.model.bd.service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeLocalVotacion;

public interface MaeLocalVotacionService extends CrudService<MaeLocalVotacion> {

    void delete(Long idCentroComputo, String proceso);

}
