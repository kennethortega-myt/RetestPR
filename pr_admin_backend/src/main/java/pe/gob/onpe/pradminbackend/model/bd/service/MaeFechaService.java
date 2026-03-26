package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeFecha;

public interface MaeFechaService extends CrudService<MaeFecha> {

    Optional<MaeFecha> findById(Integer id);
}
