package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaePadronProgreso;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaePadronProgresoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaePadronProgresoService;

@Service
public class MaePadronProgresoServiceImpl implements MaePadronProgresoService {
	
	private final MaePadronProgresoRepository maePadronProgresoRepository;
	private final MongoOperations mongoOperations;

	public MaePadronProgresoServiceImpl(MaePadronProgresoRepository maePadronProgresoRepository,
			MongoOperations mongoOperations) {
		super();
		this.maePadronProgresoRepository = maePadronProgresoRepository;
		this.mongoOperations = mongoOperations;
	}

	@Override
	public void save(MaePadronProgreso k) {
		this.maePadronProgresoRepository.save(k);
	}

	@Override
	public void saveAll(List<MaePadronProgreso> k) {
		this.maePadronProgresoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.maePadronProgresoRepository.deleteAll();
	}

	@Override
	public List<MaePadronProgreso> findAll() {
		return this.maePadronProgresoRepository.findAll();
	}

	@Override
	public MaePadronProgreso ultimoRegistro() {
		Query query = new Query().with(Sort.by(Sort.Order.desc("_id"))).limit(1);
		return this.mongoOperations.findOne(query, MaePadronProgreso.class);
	}

}
