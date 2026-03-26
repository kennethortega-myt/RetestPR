package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;

import java.util.List;
import java.util.Optional;

public interface MaeUbigeoRepository extends MongoRepository<MaeUbigeo, Long> {

    @Override
    Optional<MaeUbigeo> findById(Long aLong);

    @Query("{_id: { $in: ?0 } })")
    List<MaeUbigeo> findByIds(List<Long> listIds);
}
