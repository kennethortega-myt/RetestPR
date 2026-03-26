package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeLocalVotacion;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeLocalVotacionRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeLocalVotacionService;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaeLocalVotacionServiceImpl implements MaeLocalVotacionService {

	private final MaeLocalVotacionRepository maeLocalVotacionRepository;
	
	@Override
	public void save(MaeLocalVotacion k) {
		this.maeLocalVotacionRepository.save(k);
	}

	@Override
	public void saveAll(List<MaeLocalVotacion> k) {
		this.maeLocalVotacionRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.maeLocalVotacionRepository.deleteAll();
	}

	@Override
	public void delete(Long idCentroComputo, String proceso) {
		this.maeLocalVotacionRepository.deleteAll();
	}

	@Override
	public List<MaeLocalVotacion> findAll() {
		return Collections.emptyList();
	}

}
