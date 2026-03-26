package pe.gob.onpe.presentacionbackend.model.bd.repository.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;

public interface VwPrTotalCandidatosPorAgrupacionPoliticaRepository extends MongoRepository<VwPrTotalCandidatosPorAgrupacionPolitica, Long> {
	List<VwPrTotalCandidatosPorAgrupacionPolitica> findByDistritoElectoral(Integer distritoElectoral);

	List<VwPrTotalCandidatosPorAgrupacionPolitica> findByEleccion(Integer idEleccion);
	List<VwPrTotalCandidatosPorAgrupacionPolitica> findByEleccionAndDistritoElectoral(Integer idEleccion,Integer idDistritoElectoral);

}
