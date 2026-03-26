package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeFechaRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeFechaService;

import java.util.List;
import java.util.Optional;

@Service
public class MaeFechaServiceImpl implements MaeFechaService {

    @Autowired
    private MaeFechaRepository maeFechaRepository;

    @Override
    public void save(MaeFecha maeFecha) {
        this.maeFechaRepository.save(maeFecha);
    }

    @Override
    public void saveAll(List<MaeFecha> k) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<MaeFecha> findAll() {
        return null;
    }

    @Override
    public Optional<MaeFecha> findById(Integer id)  {
        return maeFechaRepository.findById(id);
    }



}
