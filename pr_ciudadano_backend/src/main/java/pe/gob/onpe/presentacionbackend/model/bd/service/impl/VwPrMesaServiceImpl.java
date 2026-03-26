package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrMesa;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrMesaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrMesaService;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.FiltroMesaTotalesDto;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.MesaTotalesDto;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class VwPrMesaServiceImpl implements VwPrMesaService {

	private final VwPrMesaRepository vwPrMesaRepository;

	@Override
	public void save(VwPrMesa k) {
		this.vwPrMesaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrMesa> k) {
		this.vwPrMesaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrMesaRepository.deleteAll();
	}

	@Override
	public List<VwPrMesa> findAll() {
		return this.vwPrMesaRepository.findAll();
	}

	@Override
	public Optional<MesaTotalesDto> obtenerMesasTotales(FiltroMesaTotalesDto filtros) {

		Predicate<FiltroMesaTotalesDto> tieneTipoFiltro = data -> data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroMesaTotalesDto> tieneAmbito = data -> data.getAmbitoGeografico()!=null && data.getAmbitoGeografico() != 0;
		Predicate<FiltroMesaTotalesDto> tieneDistritoElectoral = data -> data.getDistritoElectoral()!=null && data.getDistritoElectoral() != 0;
		Predicate<FiltroMesaTotalesDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroMesaTotalesDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroMesaTotalesDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrMesa> registros = null;
		if(tieneTipoFiltro.and(tieneAmbito.negate()).and(tieneDistritoElectoral.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrMesaRepository.findByTipoFiltro(filtros.getTipoFiltro());

		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneDistritoElectoral.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrMesaRepository.findByTipoFiltroAndAmbitoGeografico(filtros.getTipoFiltro(),filtros.getAmbitoGeografico());

		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneDistritoElectoral).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)) {
			registros = vwPrMesaRepository.findByTipoFiltroAndAmbitoGeograficoAndDistritoElectoral(filtros.getTipoFiltro(), filtros.getAmbitoGeografico(), filtros.getDistritoElectoral());

		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneDistritoElectoral.negate()).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrMesaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getTipoFiltro(),filtros.getAmbitoGeografico(),filtros.getUbigeoNivel1());

		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneDistritoElectoral.negate()).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrMesaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getTipoFiltro(),filtros.getAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());

		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneDistritoElectoral.negate()).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrMesaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getTipoFiltro(),filtros.getAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(), filtros.getUbigeoNivel3());
		} else {
			registros = Collections.emptyList();
		}

		return construirRespuesta(registros);

	}

	private Optional<MesaTotalesDto> construirRespuesta(List<VwPrMesa> registros){

		if(registros.isEmpty()) {
			return Optional.empty();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR vista mesa, size: {} ", registros.size());
			return Optional.empty();
		}
		VwPrMesa mesa = registros.get(0);
		return  Optional.of(MesaTotalesDto.builder()
						.mesasInstaladas(mesa.getMesasInstaladas())
						.mesasNoInstaladas(mesa.getMesasNoInstaladas())
						.mesasPendientes(mesa.getMesasPorInformar())
				.build());
	}

}
