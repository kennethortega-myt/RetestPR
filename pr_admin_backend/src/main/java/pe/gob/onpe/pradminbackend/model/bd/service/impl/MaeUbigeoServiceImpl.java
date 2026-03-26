package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeUbigeoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeUbigeoService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaeUbigeoServiceImpl implements MaeUbigeoService{

	private final MaeUbigeoRepository maeUbigeoRepository;

	@Override
	public void save(MaeUbigeo k) {

		this.maeUbigeoRepository.save(k);

	}

	@Override
	public void saveAll(List<MaeUbigeo> k) {

		this.maeUbigeoRepository.saveAll(k);

	}

	@Override
	public void deleteAll() {

		this.maeUbigeoRepository.deleteAll();

	}

	@Override
	public List<MaeUbigeo> findAll() {

		return this.maeUbigeoRepository.findAll();

	}

}
