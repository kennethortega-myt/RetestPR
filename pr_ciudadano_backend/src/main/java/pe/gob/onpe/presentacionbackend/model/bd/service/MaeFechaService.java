package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeFecha;

public interface MaeFechaService extends CrudService<MaeFecha> {

    Optional<MaeFecha> findById(Integer id);
}
