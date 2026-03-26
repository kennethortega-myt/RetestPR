package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrRevocatoriaDistrital;

public interface VwPrRevocatoriaDistritalRepository extends MongoRepository<VwPrRevocatoriaDistrital, Integer> {
	List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
	Page<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro, Pageable pageable);
	
	List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
	Page<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Pageable pageable);
	
	List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01);
	Page<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Pageable pageable);
	
	List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02);
	Page<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Pageable pageable);
	
	List<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03);
	Page<VwPrRevocatoriaDistrital> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03, Pageable pageable);
	
	@Query("{'c_detalle.c_cargo':?0}")
	Page<VwPrRevocatoriaDistrital> findByDetalleCargo(String cargo, Pageable pageable);
	
	@Query("{'c_detalle.c_cargo':?0}")
	List<VwPrRevocatoriaDistrital> findByDetalleCargoV1(String cargo);
}
