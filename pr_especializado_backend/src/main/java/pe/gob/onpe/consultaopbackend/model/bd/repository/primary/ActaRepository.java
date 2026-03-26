package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrActa;

import java.util.List;
import java.util.Optional;

public interface ActaRepository extends MongoRepository<VwPrActa, Long> {
	Optional<VwPrActa> findById(Long id);
    List<VwPrActa> findById(Integer id);
	List<VwPrActa> findByIdEleccion(Long idEleccion);
	Page<VwPrActa> findByIdEleccion(Long idEleccion, Pageable pageable);

	List<VwPrActa> findByIdEleccionAndIdAmbitoGeografico(Long idEleccion, Integer idAmbitoGeografico);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeografico(Long idEleccion, Integer idAmbitoGeografico, Pageable pageable);
	
	List<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Pageable pageable);
	
	List<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Pageable pageable);

	List<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeo(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeo(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo, Pageable pageable);
	
	List<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo, Long idLocalVotacion);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(Long idEleccion, Integer idAmbitoGeografico, Integer nubigeoNivel01, Integer nubigeoNivel02, Long idUbigeo, Long idLocalVotacion, Pageable pageable);
		
	Page<VwPrActa> findByIdEleccionAndLineaTiempoCodigoEstadoActa(Integer idEleccion, String codigoEstadoActa, Pageable pageable);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndLineaTiempoCodigoEstadoActa(Integer idEleccion, Integer idAmbitoGeografico, String codigoEstadoActa,Pageable pageable);
	Page<VwPrActa> findByIdEleccionAndIdAmbitoGeograficoAndIdUbigeoAndLineaTiempoCodigoEstadoActa(Integer idEleccion, Integer idAmbitoGeografico,Long idUbigeo, String codigoEstadoActa, Pageable pageable);

	List<VwPrActa> findByIdEleccionAndIdUbigeo(Integer idEleccion, Long idUbigeo);
}