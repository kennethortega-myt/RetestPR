package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeAgrupacionPoliticaRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeAgrupacionPoliticaService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaeAgrupacionPoliticaServiceImpl implements MaeAgrupacionPoliticaService {

    private final MaeAgrupacionPoliticaRepository maeAgrupacionPoliticaRepository;

    @Override
    public void save(MaeAgrupacionPolitica k) {
        this.maeAgrupacionPoliticaRepository.save(k);
    }

    @Override
    public void saveAll(List<MaeAgrupacionPolitica> k) {
        this.maeAgrupacionPoliticaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.maeAgrupacionPoliticaRepository.deleteAll();
    }

	@Override
	public List<MaeAgrupacionPolitica> findAll() {
		return maeAgrupacionPoliticaRepository.findAll();
	}

}
