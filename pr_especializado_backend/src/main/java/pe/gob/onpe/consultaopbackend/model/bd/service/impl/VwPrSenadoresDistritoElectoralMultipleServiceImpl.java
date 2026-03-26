package pe.gob.onpe.consultaopbackend.model.bd.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.VwPrSenadoresDistritoElectoralMultipleRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.VwPrSenadoresDistritoElectoralMultipleRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.VwPrSenadoresDistritoElectoralMultipleService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VwPrSenadoresDistritoElectoralMultipleServiceImpl implements VwPrSenadoresDistritoElectoralMultipleService {
	private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
	private final VwPrSenadoresDistritoElectoralMultipleRepositoryCustom vwPrSenadoresDistritoElectoralMultipleRepositoryCustom;

	@Override
	public void save(VwPrSenadoresDistritoElectoralMultiple k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrSenadoresDistritoElectoralMultiple> k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.deleteAll();
	}

	@Override
	public List<VwPrSenadoresDistritoElectoralMultiple> findAll() {
		return this.vwPrSenadoresDistritoElectoralMultipleRepository.findAll();
	}



}
