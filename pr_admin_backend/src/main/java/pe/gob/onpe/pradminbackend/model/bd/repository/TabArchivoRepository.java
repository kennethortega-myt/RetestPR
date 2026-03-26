package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;
import pe.gob.onpe.pradminbackend.model.bd.documents.TabArchivo;

import java.util.List;
import java.util.Optional;

public interface TabArchivoRepository extends MongoRepository<TabArchivo, String> {
    Optional<TabArchivo> findByTipoAndIdActa(Integer tipo, Long idActa);
    Optional<TabArchivo> findBycGuid(String cguid);
    Optional<TabArchivo> findBycGuidAndTipoAndIdActa(String cguid, Integer tipo, Long idActa);
    @Query("{ 'idActa': { $in: ?0 } }")
    List<pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo> findAllByIdActa(List<Long> idActaList);
}
