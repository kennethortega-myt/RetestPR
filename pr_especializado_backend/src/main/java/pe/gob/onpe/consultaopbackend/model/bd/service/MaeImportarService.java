package pe.gob.onpe.consultaopbackend.model.bd.service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeImportar;

public interface MaeImportarService extends CrudService<MaeImportar> {
    Boolean validarProcesoImportar();
}
