package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.CabCatalogo;
import pe.gob.onpe.presentacionbackend.model.bd.documents.DetCatalogoEstructura;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.CabCatalogoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.DetCatalogoEstructuraRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.DetCatalogoEstructuraService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetCatalogoEstructuraServiceImpl implements DetCatalogoEstructuraService {

    private final CabCatalogoRepository cabCatalogoRepository;
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

    @Override
    public List<DetCatalogoEstructura> findByMaestroAndColumna(String maestro, String columna) {

        List<CabCatalogo> cabCatalogos = this.cabCatalogoRepository.findByMaestro(maestro);

        if(!cabCatalogos.isEmpty()){
            CabCatalogo cabCatalogo = cabCatalogos.get(0);
            return this.detCatalogoEstructuraRepository.findByCatalogo(cabCatalogo.getId(),columna);
        }else{

            return new ArrayList<>();
        }

    }
}
