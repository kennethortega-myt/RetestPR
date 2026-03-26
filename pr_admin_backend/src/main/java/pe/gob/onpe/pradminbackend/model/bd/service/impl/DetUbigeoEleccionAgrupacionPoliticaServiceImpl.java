package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.pradminbackend.model.bd.repository.DetUbigeoEleccionAgrupacionPoliticaRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.DetUbigeoEleccionAgrupacionPoliticaService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetUbigeoEleccionAgrupacionPoliticaServiceImpl implements DetUbigeoEleccionAgrupacionPoliticaService {

    private final DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository;

    @Override
    public void save(DetUbigeoEleccionAgrupacionPolitica k) {
        this.detUbigeoEleccionAgrupacionPoliticaRepository.save(k);
    }

    @Override
    public void saveAll(List<DetUbigeoEleccionAgrupacionPolitica> k) {
        this.detUbigeoEleccionAgrupacionPoliticaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detUbigeoEleccionAgrupacionPoliticaRepository.deleteAll();
    }

	@Override
	public List<DetUbigeoEleccionAgrupacionPolitica> findAll() {
		return Collections.emptyList();
	}

}
