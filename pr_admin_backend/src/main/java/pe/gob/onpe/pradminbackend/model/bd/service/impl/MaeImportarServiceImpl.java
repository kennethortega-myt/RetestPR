package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import pe.gob.onpe.pradminbackend.model.bd.documents.MaeImportar;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeImportarRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeImportarService;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;

@Service
public class MaeImportarServiceImpl implements MaeImportarService {
	
	private final MaeImportarRepository maeImportarRepository;
	private final MongoOperations mongoOperations;
	
	public MaeImportarServiceImpl(MaeImportarRepository maeImportarRepository, MongoOperations mongoOperations) {
		this.maeImportarRepository = maeImportarRepository;
		this.mongoOperations = mongoOperations;
	}

	@Override
	public void save(MaeImportar k) {
		this.maeImportarRepository.save(k);
	}

	@Override
	public void saveAll(List<MaeImportar> k) {
		maeImportarRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		maeImportarRepository.deleteAll();
	}

	@Override
	public List<MaeImportar> findAll() {
		return this.maeImportarRepository.findAll();
	}
	
	

	@Override
	public MaeImportar ultimoRegistro() {
		Query query = new Query().with(Sort.by(Sort.Order.desc("_id"))).limit(1);
		return this.mongoOperations.findOne(query, MaeImportar.class);
	}

	@Override
	public Optional<MaeImportar> getId(int id) {
		return this.maeImportarRepository.findById(id);
	}
	
	@Override
	public MaeImportar getAtributo(String atributo) {
		List<MaeImportar> maeImportar = this.maeImportarRepository.findByAtributo(atributo);
		return maeImportar.get(0);
	}
	
	@Override
	public EstadoServicioDto validarServicioBasedatos() {
		try {
			Query query = new Query(Criteria.where("c_atributo").is("getBd"));
			MaeImportar primerRegistro = this.mongoOperations.findOne(query, MaeImportar.class);
	        if (primerRegistro != null) {
	            return EstadoServicioDto.builder()
	                    .estado(true).build();
	        } else {
	            return EstadoServicioDto.builder()
	                    .estado(false).build();
	        }
	    } catch (Exception e) {
	        return EstadoServicioDto.builder()
	                .estado(false)
	                .mensaje("No disponible: " + e.getMessage()).build();
	    }
	}

}
