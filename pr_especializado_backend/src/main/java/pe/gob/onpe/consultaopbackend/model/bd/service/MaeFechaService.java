package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeFecha;

import java.util.Optional;

public interface MaeFechaService extends CrudService<MaeFecha> {

    Optional<MaeFecha> findById(Integer id);
}
