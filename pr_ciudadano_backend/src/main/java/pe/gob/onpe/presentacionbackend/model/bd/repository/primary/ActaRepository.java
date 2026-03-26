package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActa;

import java.util.List;
import java.util.Optional;

public interface ActaRepository extends MongoRepository<VwPrActa, Long> {
	Optional<VwPrActa> findById(Long id);

	List<VwPrActa> findByIdAmbitoGeografico(Integer idAmbitoGeografico);
	Page<VwPrActa> findByIdAmbitoGeografico(Integer idAmbitoGeografico, Pageable pageable);

	List<VwPrActa> findByIdAmbitoGeograficoAndIdUbigeo(Integer idAmbitoGeografico,Long idUbigeo);
	Page<VwPrActa> findByIdAmbitoGeograficoAndIdUbigeo(Integer idAmbitoGeografico,Long idUbigeo, Pageable pageable);
	
	List<VwPrActa> findByIdAmbitoGeograficoAndIdUbigeoAndIdLocalVotacion(Integer idAmbitoGeografico, Long idUbigeo, Long idLocalVotacion);
	Page<VwPrActa> findByIdAmbitoGeograficoAndIdUbigeoAndIdLocalVotacion(Integer idAmbitoGeofrafico, Long idUbigeo, Long idLocalVotacion, Pageable pageable);
	List<VwPrActa> findByIdEleccionAndIdUbigeo(Integer idEleccion, Long idUbigeo);
}