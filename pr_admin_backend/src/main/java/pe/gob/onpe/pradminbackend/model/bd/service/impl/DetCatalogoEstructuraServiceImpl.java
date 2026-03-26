package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.DetCatalogoEstructura;
import pe.gob.onpe.pradminbackend.model.bd.repository.DetCatalogoEstructuraRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.DetCatalogoEstructuraService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetCatalogoEstructuraServiceImpl implements DetCatalogoEstructuraService {

    private final DetCatalogoEstructuraRepository detCatalogoEstructuraRepository;

    @Override
    public void save(DetCatalogoEstructura k) {
        this.detCatalogoEstructuraRepository.save(k);
    }

    @Override
    public void saveAll(List<DetCatalogoEstructura> k) {
        this.detCatalogoEstructuraRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detCatalogoEstructuraRepository.deleteAll();
    }

	@Override
	public List<DetCatalogoEstructura> findAll() {
		return this.detCatalogoEstructuraRepository.findAll();
	}

}
