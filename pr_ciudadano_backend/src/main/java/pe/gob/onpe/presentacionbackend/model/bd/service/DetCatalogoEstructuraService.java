package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.List;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoEstructura;

public interface DetCatalogoEstructuraService extends CrudService<DetCatalogoEstructura> {

    List<DetCatalogoEstructura> findByMaestroAndColumna(String maestro, String columna);

}
