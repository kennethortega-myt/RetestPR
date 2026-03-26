package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeDistritoElectoralRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeDistritoElectoralService;
import pe.gob.onpe.presentacionbackend.model.dto.distritoelectoral.DistritosResponseDto;

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

	@Override
	public List<DistritosResponseDto> listarDistritos() {
		List<MaeDistritoElectoral> lstMaeDistritoElectoral = this.distritoElectoralRepository.findAll();
		return lstMaeDistritoElectoral.stream()
				.filter(data -> data.getId()!=0)
				.map(maeDistritoElectoral-> DistritosResponseDto.builder()
						.codigo(maeDistritoElectoral.getId())
						.nombre(maeDistritoElectoral.getNombre())
						.build())
				.toList();
	}

}
