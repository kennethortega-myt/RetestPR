package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;

public interface MaeCandidatoRepository extends MongoRepository<MaeCandidato, Integer> {
	List<MaeCandidato> findByEleccion(MaeEleccion eleccion);

	@Query("{_id: { $in: ?0 } })")
	List<MaeCandidato> findByIds(List<Integer> listIds);
	List<MaeCandidato> findByEleccionAndDistritoElectoral(MaeEleccion eleccion, MaeDistritoElectoral distritoElectoral);
	List<MaeCandidato> findByEleccionAndDistritoElectoralAndAgrupacionPolitica(MaeEleccion eleccion, MaeDistritoElectoral distritoElectoral, MaeAgrupacionPolitica agrupacionPolitica);
	List<MaeCandidato> findByEleccionAndAgrupacionPolitica(MaeEleccion eleccion, MaeAgrupacionPolitica agrupacionPolitica);

}
