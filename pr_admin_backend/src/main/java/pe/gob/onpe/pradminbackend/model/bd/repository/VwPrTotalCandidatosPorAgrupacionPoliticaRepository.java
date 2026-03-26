package pe.gob.onpe.pradminbackend.model.bd.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;

import java.util.List;

public interface VwPrTotalCandidatosPorAgrupacionPoliticaRepository extends MongoRepository<VwPrTotalCandidatosPorAgrupacionPolitica, Long> {
    List<VwPrTotalCandidatosPorAgrupacionPolitica> findByEleccion(Integer idEleccion);
    List<VwPrTotalCandidatosPorAgrupacionPolitica> findByEleccionAndDistritoElectoral(Integer idEleccion,Integer idDistritoElectoral);
}
