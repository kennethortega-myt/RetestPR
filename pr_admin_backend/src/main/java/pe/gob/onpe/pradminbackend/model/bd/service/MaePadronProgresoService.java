package pe.gob.onpe.pradminbackend.model.bd.service;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaePadronProgreso;

public interface MaePadronProgresoService extends CrudService<MaePadronProgreso> {
	MaePadronProgreso ultimoRegistro();
}
