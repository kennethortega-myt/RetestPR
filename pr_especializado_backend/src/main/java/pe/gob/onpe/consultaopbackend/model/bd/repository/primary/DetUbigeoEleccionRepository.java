package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.DetUbigeoEleccion;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;

public interface DetUbigeoEleccionRepository extends MongoRepository<DetUbigeoEleccion, Long> {


    DetUbigeoEleccion findByUbigeoAndEleccion(MaeUbigeo ubigeo, MaeEleccion eleccion);
}
