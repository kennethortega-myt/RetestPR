package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeCandidatoService;

@Service
public class MaeCandidatoServiceImpl implements MaeCandidatoService {	
	
	private final MaeCandidatoRepository maeCandidatoRepository;

	public MaeCandidatoServiceImpl(MaeCandidatoRepository maeCandidatoRepository) {
		super();
		this.maeCandidatoRepository = maeCandidatoRepository;
	}

	@Override
	public void save(MaeCandidato k) {
		this.maeCandidatoRepository.save(k);
	}

	@Override
	public void saveAll(List<MaeCandidato> k) {
		this.maeCandidatoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.maeCandidatoRepository.deleteAll();
	}

	@Override
	public List<MaeCandidato> findAll() {
		return this.maeCandidatoRepository.findAll();
	}

}
