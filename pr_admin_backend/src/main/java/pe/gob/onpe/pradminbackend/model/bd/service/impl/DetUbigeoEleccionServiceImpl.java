package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetUbigeoEleccion;
import pe.gob.onpe.pradminbackend.model.bd.repository.DetUbigeoEleccionRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.DetUbigeoEleccionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetUbigeoEleccionServiceImpl implements DetUbigeoEleccionService {

    private final DetUbigeoEleccionRepository detUbigeoEleccionRepository;

    @Override
    public void save(DetUbigeoEleccion k) {
        this.detUbigeoEleccionRepository.save(k);
    }

    @Override
    public void saveAll(List<DetUbigeoEleccion> k) {
        this.detUbigeoEleccionRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detUbigeoEleccionRepository.deleteAll();
    }

	@Override
	public List<DetUbigeoEleccion> findAll() {
		return detUbigeoEleccionRepository.findAll();
	}

}
