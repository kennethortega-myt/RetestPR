package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrTotalCandidatosPorAgrupacionPoliticaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrTotalCandidatosPorAgrupacionPoliticaService;

@Service
public class VwPrTotalCandidatosPorAgrupacionPoliticaServiceImpl implements VwPrTotalCandidatosPorAgrupacionPoliticaService {

	private final VwPrTotalCandidatosPorAgrupacionPoliticaRepository vwPrTotalCandidatosPorAgrupacionPoliticaRepository;

	public VwPrTotalCandidatosPorAgrupacionPoliticaServiceImpl(
			VwPrTotalCandidatosPorAgrupacionPoliticaRepository vwPrTotalCandidatosPorAgrupacionPoliticaRepository) {
		super();
		this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository = vwPrTotalCandidatosPorAgrupacionPoliticaRepository;
	}

	@Override
	public void save(VwPrTotalCandidatosPorAgrupacionPolitica k) {
		this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrTotalCandidatosPorAgrupacionPolitica> k) {
		this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository.deleteAll();
	}

	@Override
	public List<VwPrTotalCandidatosPorAgrupacionPolitica> findAll() {
		return this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository.findAll();
	}
	
}
