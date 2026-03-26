package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeImportar;

import java.util.List;


public interface MaeImportarRepository extends MongoRepository<MaeImportar, Integer> {
	List<MaeImportar> findByAtributo(String atributo);
	boolean existsByExito(boolean exito);
}
