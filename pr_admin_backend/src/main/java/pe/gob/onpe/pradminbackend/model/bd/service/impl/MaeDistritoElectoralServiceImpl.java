package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeDistritoElectoralRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeDistritoElectoralService;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaeDistritoElectoralServiceImpl implements MaeDistritoElectoralService {

	private final MaeDistritoElectoralRepository distritoElectoralRepository;

	@Override
	public void save(MaeDistritoElectoral k) {
		this.distritoElectoralRepository.save(k);
	}

	@Override
	public void saveAll(List<MaeDistritoElectoral> k) {
		this.distritoElectoralRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.distritoElectoralRepository.deleteAll();
	}

	@Override
	public List<MaeDistritoElectoral> findAll() {
		return this.distritoElectoralRepository.findAll();
	}

}
