package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeLocalVotacion;

public interface MaeLocalVotacionService extends CrudService<MaeLocalVotacion> {

    void delete(Long idCentroComputo, String proceso);

}
