package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.DetUbigeoEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.DetUbigeoEleccionRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.DetUbigeoEleccionRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.DetUbigeoEleccionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetUbigeoEleccionServiceImpl implements DetUbigeoEleccionService {

    private final DetUbigeoEleccionRepository detUbigeoEleccionRepository;
    private final DetUbigeoEleccionRepositoryCustom detUbigeoEleccionRepositoryCustom;

    @Override
    public void save(DetUbigeoEleccion k) {
        this.detUbigeoEleccionRepository.save(k);
    }

    @Override
    public void saveAll(List<DetUbigeoEleccion> k) {
        this.detUbigeoEleccionRepository.saveAll(k);
    }

    @Override
    public void delete(Long idCentroComputo, String proceso) {
        this.detUbigeoEleccionRepositoryCustom.deleteByIdCentroComputoAndProceso(idCentroComputo, proceso);
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
