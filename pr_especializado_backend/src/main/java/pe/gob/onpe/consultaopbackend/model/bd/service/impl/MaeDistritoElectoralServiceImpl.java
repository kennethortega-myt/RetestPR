package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeDistritoElectoralRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeDistritoElectoralService;
import pe.gob.onpe.consultaopbackend.model.dto.distritoelectoral.DistritosResponseDto;

import java.util.List;

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
				.filter(data -> data.getId()!= null)
				.map(maeDistritoElectoral-> {
					if(maeDistritoElectoral.getId() == 0) {
						maeDistritoElectoral.setId(30);
						maeDistritoElectoral.setNombre("TODOS");
					}
					return DistritosResponseDto.builder()
							.codigo(maeDistritoElectoral.getId())
							.nombre(maeDistritoElectoral.getNombre())
							.build();
				})
				.toList();
	}

}
