package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeAgrupacionPoliticaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeAgrupacionPoliticaRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeAgrupacionPoliticaService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaeAgrupacionPoliticaServiceImpl implements MaeAgrupacionPoliticaService {

    private final MaeAgrupacionPoliticaRepository maeAgrupacionPoliticaRepository;
    private final MaeAgrupacionPoliticaRepositoryCustom maeAgrupacionPoliticaRepositoryCustom;

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

	public MaeAgrupacionPolitica getPosicionAndIdUbigeoEleccion(Long posicion, Long idUbigeoEleccion) {
		return this.maeAgrupacionPoliticaRepositoryCustom.getPosicionAndIdUbigeoEleccion(posicion,idUbigeoEleccion);

	}


}
