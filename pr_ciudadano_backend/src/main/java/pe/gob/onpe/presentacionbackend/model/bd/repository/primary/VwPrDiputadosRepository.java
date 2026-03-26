package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrDiputados;

import java.util.List;
import java.util.Optional;

public interface VwPrDiputadosRepository extends MongoRepository<VwPrDiputados, Integer> {

	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndDistritoElectoral(Integer tipoEleccion, String tipoFiltro, Integer distritoElectoral);
	Optional<VwPrDiputados> findById(Integer idVista);
	List<VwPrDiputados> findByTipoEleccionAndDistritoElectoralAndTipoFiltro(Integer tipoEleccion, Integer distritoElectoral, String tipoFiltro);
	
	List<VwPrDiputados> findByTipoEleccionAndTipoFiltro(Integer tipoEleccion, String tipoFiltro);
	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico);
	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndDistritoElectoral(Integer tipoEleccion, String tipoFiltro, Integer ambitoGeografico, Integer distritoElectoral);

	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(Integer tipoEleccion, String tipoFiltro, Integer ambito, Integer ubigeo1);
	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2);
	List<VwPrDiputados> findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(Integer tipoEleccion,String tipoFiltro, Integer ambito, Integer ubigeo1, Integer ubigeo2, Integer ubigeo3);
}
