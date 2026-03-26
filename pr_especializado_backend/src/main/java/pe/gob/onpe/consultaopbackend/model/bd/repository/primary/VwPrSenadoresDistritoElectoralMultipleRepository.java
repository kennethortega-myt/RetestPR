package pe.gob.onpe.consultaopbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;

import java.util.List;

public interface VwPrSenadoresDistritoElectoralMultipleRepository extends MongoRepository<VwPrSenadoresDistritoElectoralMultiple, Integer> {
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltroAndDistritoElectoral(Integer tipoEleccion, String tipoFiltro, Integer distritoElectoral);
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndDistritoElectoralAndTipoFiltro(Integer tipoEleccion, Integer distritoElectoral, String tipoFiltro);
	
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01);
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02);
	List<VwPrSenadoresDistritoElectoralMultiple> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer ubigeoNivel01, Integer ubigeoNivel02, Integer ubigeoNivel03);
}
