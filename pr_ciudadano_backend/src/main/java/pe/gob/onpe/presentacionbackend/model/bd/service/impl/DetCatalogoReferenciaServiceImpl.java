package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoReferencia;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.DetCatalogoReferenciaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.DetCatalogoReferenciaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetCatalogoReferenciaServiceImpl implements DetCatalogoReferenciaService {

    private final DetCatalogoReferenciaRepository detCatalogoReferenciaRepository;

    @Override
    public void save(DetCatalogoReferencia k) {
        this.detCatalogoReferenciaRepository.save(k);
    }

    @Override
    public void saveAll(List<DetCatalogoReferencia> k) {
        this.detCatalogoReferenciaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detCatalogoReferenciaRepository.deleteAll();
    }

	@Override
	public List<DetCatalogoReferencia> findAll() {
		return detCatalogoReferenciaRepository.findAll();
	}

}
