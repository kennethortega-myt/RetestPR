package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeEleccionRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeEleccionRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeEleccionService;
import pe.gob.onpe.presentacionbackend.model.dto.response.EleccionesMenuResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaeEleccionServiceImpl implements MaeEleccionService {

    private final MaeEleccionRepository maeEleccionRepository;
    private final MaeEleccionRepositoryCustom maeEleccionRepositoryCustom;

    @Override
    public void save(MaeEleccion k) {
        this.maeEleccionRepository.save(k);
    }

    @Override
    public void saveAll(List<MaeEleccion> k) {
        this.maeEleccionRepository.saveAll(k);
    }

    @Override
    public void delete(Long idCentroComputo, String proceso) {
        this.maeEleccionRepositoryCustom.deleteByIdCentroComputoAndProceso(idCentroComputo, proceso);
    }

    public List<MaeEleccion> findAll() {
        return this.maeEleccionRepository.findAll();
    }

    @Override
	public List<EleccionesMenuResponse> findEleccionesByProceso(Long idProceso, Integer activo) {

		return this.maeEleccionRepositoryCustom.findEleccionesByProceso(idProceso, activo);

	}

	@Override
    public void deleteAll() {
        this.maeEleccionRepository.deleteAll();
    }

	@Override
	public Optional<MaeEleccion> findById(Long id) {
		return this.maeEleccionRepository.findById(id);
	}

}
