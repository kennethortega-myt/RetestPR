package pe.gob.onpe.pradminbackend.model.bd.service;

import java.util.Optional;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeImportar;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;

public interface MaeImportarService extends CrudService<MaeImportar> {
	MaeImportar ultimoRegistro();
	Optional<MaeImportar> getId(int id);
	MaeImportar getAtributo(String atributo);
	EstadoServicioDto validarServicioBasedatos(); 
}
