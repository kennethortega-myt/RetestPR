package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoEstructura;

import java.util.List;

public interface DetCatalogoEstructuraRepository extends MongoRepository<DetCatalogoEstructura, Long> {

    @Query(value = "{'catalogo': ?0,'columna': ?1}", fields = "{'_id' : 1, 'columna' : 1, 'nombre' : 1,'codigo' : 1,'codigo' : 1,'tipo' : 1,'orden' : 1,'activo':1 }")
    List<DetCatalogoEstructura> findByCatalogo(Long catalogo, String cColumna);

}
