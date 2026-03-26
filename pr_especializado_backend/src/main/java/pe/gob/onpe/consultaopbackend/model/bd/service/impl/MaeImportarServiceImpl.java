package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeImportar;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeImportarRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeImportarService;

import java.util.List;

@Service
public class MaeImportarServiceImpl implements MaeImportarService {

    @Autowired
    private MaeImportarRepository maeImportarRepository;

    @Override
    public void save(MaeImportar maeImportar) {
        this.maeImportarRepository.save(maeImportar);
    }

    @Override
    public void saveAll(List<MaeImportar> list) {
        this.maeImportarRepository.saveAll(list);
    }

    @Override
    public void deleteAll() {
        this.maeImportarRepository.deleteAll();
    }

    @Override
    public List<MaeImportar> findAll() {
        return this.maeImportarRepository.findAll();
    }

    @Override
    public Boolean validarProcesoImportar() {
        List<MaeImportar> list = this.maeImportarRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().allMatch(item -> Boolean.TRUE.equals(item.getExito()));
    }
}
