package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeLocalVotacionRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeUbigeoRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeUbigeoService;
import pe.gob.onpe.consultaopbackend.model.dto.*;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MaeUbigeoServiceImpl implements MaeUbigeoService {
	

	private final MaeUbigeoRepository maeUbigeoRepository;
	private final MaeLocalVotacionRepositoryCustom maeLocalVotacionRepositoryCustom;
	private final MaeUbigeoRepositoryCustom maeUbigeRepositoryoCustom;

	@Override
	public List<String> findDistinctDepartamentos() {
		return maeUbigeRepositoryoCustom.findDistinctDepartamentos();
	}

	@Override
	public List<UbigeoDepartamentoDto> listarDepartamentosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro) {

		return this.maeUbigeRepositoryoCustom.listarDepartamentosPorIdEleccionII(filtro)
				.stream()
				.sorted(Comparator.comparing(UbigeoDepartamentoDto::getNombre)).toList();
	}
	
	@Override
	public List<UbigeoProvinciaDto> listarProvinciasPorIdEleccionII(FiltroUbigeoProvinciaDto filtro) {
		return this.maeUbigeRepositoryoCustom.listarProvinciasPorIdEleccionII(filtro)
				.stream()
				.sorted(Comparator.comparing(UbigeoProvinciaDto::getNombre)).toList();
	}

	@Override
	public List<UbigeoDistritoDto> listarDistritosPorIdEleccionII(FiltroUbigeoDistritoDto filtro) {
		return this.maeUbigeRepositoryoCustom.listarDistritosPorIdEleccionII(filtro).stream()
				.sorted(Comparator.comparing(UbigeoDistritoDto::getNombre)).toList();
	}
	
	@Override
	public List<UbigeoLocalVotacionDto> listarLocalVotacionPorIdEleccion(FiltroUbigeoLocalVotacionDto filtro) {
		return this.maeLocalVotacionRepositoryCustom.listarLocalVotacionPorIdEleccion(filtro);
	}
	
	@Override
	public List<UbigeoDistritoDto> listarDepProvDistritosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro) {
		return this.maeUbigeRepositoryoCustom.listarDepProvDistritoPorIdEleccion(filtro).stream().sorted(Comparator.comparing(UbigeoDistritoDto::getNombre)).toList();
	}

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
