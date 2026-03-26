package pe.gob.onpe.presentacionbackend.model.bd.service;


import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;


public interface MaeAgrupacionPoliticaService extends CrudService<MaeAgrupacionPolitica> {

    public MaeAgrupacionPolitica getPosicionAndIdUbigeoEleccion(Long posicion, Long idUbigeoEleccion);
    
   

}
