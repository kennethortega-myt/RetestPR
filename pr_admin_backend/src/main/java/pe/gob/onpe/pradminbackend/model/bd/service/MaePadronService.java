package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaePadron;
import pe.gob.onpe.pradminbackend.model.dto.padron.PadronDto;

public interface MaePadronService extends CrudService<MaePadron> {

    Optional<PadronDto> findByDni(String dni);
}
