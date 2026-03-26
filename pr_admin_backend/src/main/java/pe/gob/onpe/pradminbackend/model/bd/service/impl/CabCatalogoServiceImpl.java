package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.CabCatalogo;
import pe.gob.onpe.pradminbackend.model.bd.repository.CabCatalogoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.CabCatalogoService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CabCatalogoServiceImpl implements CabCatalogoService {

    private final CabCatalogoRepository cabCatalogoRepository;

    @Override
    public void save(CabCatalogo k) {
        this.cabCatalogoRepository.save(k);
    }

    @Override
    public void saveAll(List<CabCatalogo> k) {
        this.cabCatalogoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.cabCatalogoRepository.deleteAll();
    }

	@Override
	public List<CabCatalogo> findAll() {
		return Collections.emptyList();
	}

}
