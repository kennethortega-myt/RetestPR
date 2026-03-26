package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaePadron;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaePadronRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaePadronService;
import pe.gob.onpe.presentacionbackend.model.dto.padron.PadronDto;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaePadronServiceImpl implements MaePadronService {

    private final MaePadronRepository maePadronRepository;

	@Override
	public void save(MaePadron k) {
		this.maePadronRepository.save(k);
	}
    
	@Override
	public void saveAll(List<MaePadron> k) {
		this.maePadronRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.maePadronRepository.deleteAll();
	}

	@Override
	public List<MaePadron> findAll() {
		return this.maePadronRepository.findAll();
	}

    @Override
    public Optional<PadronDto> findByDni(String dni) {
        return this.maePadronRepository.findById(dni)
                .map(data -> PadronDto.builder()
                        .dni(data.getId())
                        .mesa(data.getMesa())
                        .build());
    }
}
