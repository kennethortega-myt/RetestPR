package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeFechaRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeFechaService;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaeFechaServiceImpl implements MaeFechaService {

    private final MaeFechaRepository maeFechaRepository;

    @Override
    public void save(MaeFecha maeFecha) {
        this.maeFechaRepository.save(maeFecha);
    }

    @Override
    public void saveAll(List<MaeFecha> k) {
        throw new UnsupportedOperationException("notImplemented() cannot be performed because ...");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("notImplemented() cannot be performed because ...");
    }

    @Override
    public List<MaeFecha> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<MaeFecha> findById(Integer id)  {
        return maeFechaRepository.findById(id);
    }



}
