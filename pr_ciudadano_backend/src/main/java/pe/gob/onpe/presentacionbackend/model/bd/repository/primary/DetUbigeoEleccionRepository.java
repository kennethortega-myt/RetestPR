package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;


public interface DetUbigeoEleccionRepository extends MongoRepository<DetUbigeoEleccion, Long> {
	List<DetUbigeoEleccion> findByEleccion(MaeEleccion eleccion);
}
