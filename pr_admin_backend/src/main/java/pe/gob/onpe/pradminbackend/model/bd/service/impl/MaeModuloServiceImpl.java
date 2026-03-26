package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeModulo;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeModuloRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeModuloService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaeModuloServiceImpl implements MaeModuloService {

	private final MaeModuloRepository maeModuloRepository;

	@Override
	public void save(MaeModulo k) {
		this.maeModuloRepository.save(k);
	}

	@Override
	public void saveAll(List<MaeModulo> k) {
		this.maeModuloRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.maeModuloRepository.deleteAll();
	}

	@Override
	public List<MaeModulo> findAll() {
		return this.maeModuloRepository.findAll();
	}

}
