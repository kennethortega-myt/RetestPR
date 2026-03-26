package pe.gob.onpe.presentacionbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaePadron;
import pe.gob.onpe.presentacionbackend.model.dto.padron.PadronDto;

public interface MaePadronService extends CrudService<MaePadron> {

    Optional<PadronDto> findByDni(String dni);
}
