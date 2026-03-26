package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeLocalVotacion;

public interface MaeLocalVotacionRepository extends MongoRepository<MaeLocalVotacion, Long> {


}
