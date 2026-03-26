package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeLocalVotacionRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeUbigeoRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeUbigeoService;
import pe.gob.onpe.presentacionbackend.model.dto.*;

import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@EnableCaching
@RequiredArgsConstructor
public class MaeUbigeoServiceImpl implements MaeUbigeoService{
	

	private final MaeUbigeoRepository maeUbigeoRepository;
	private final MaeLocalVotacionRepositoryCustom maeLocalVotacionRepositoryCustom;
	private final MaeUbigeoRepositoryCustom maeUbigeRepositoryoCustom;
	private final CacheManager cacheManager;

	@Override
	public List<UbigeoDepartamentoDto> listarDepartamentosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro) {
		String cacheName = "ubigeos_departamento_cache";
		
		List<UbigeoDepartamentoDto> cachedResult = getCachedValue(cacheName, filtro);
		if (cachedResult != null && cachedResult.isEmpty()) {
			evictCache(cacheName, filtro);
			cachedResult = null;
		}
		
		if (cachedResult != null) {
			return cachedResult;
		}
		
		List<UbigeoDepartamentoDto> result = this.maeUbigeRepositoryoCustom.listarDepartamentosPorIdEleccionII(filtro)
				.stream()
				.sorted(Comparator.comparing(UbigeoDepartamentoDto::getNombre,
						Collator.getInstance(Locale.of("es", "PE"))))
				.toList();
		
		if (!result.isEmpty()) {
			putInCache(cacheName, filtro, result);
		}
		
		return result;
	}
	
	@Override
	public List<UbigeoProvinciaDto> listarProvinciasPorIdEleccionII(FiltroUbigeoProvinciaDto filtro) {
		String cacheName = "ubigeos_provincias_cache";
		
		List<UbigeoProvinciaDto> cachedResult = getCachedValue(cacheName, filtro);
		if (cachedResult != null && cachedResult.isEmpty()) {
			evictCache(cacheName, filtro);
			cachedResult = null;
		}
		
		if (cachedResult != null) {
			return cachedResult;
		}
		
		List<UbigeoProvinciaDto> result = this.maeUbigeRepositoryoCustom.listarProvinciasPorIdEleccionII(filtro)
				.stream()
				.sorted(Comparator.comparing(UbigeoProvinciaDto::getNombre,
						Collator.getInstance(Locale.of("es", "PE"))))
				.toList();
		
		if (!result.isEmpty()) {
			putInCache(cacheName, filtro, result);
		}
		
		return result;
	}

	@Override
	public List<UbigeoDistritoDto> listarDistritosPorIdEleccionII(FiltroUbigeoDistritoDto filtro) {
		String cacheName = "ubigeos_distritos_cache";
		
		List<UbigeoDistritoDto> cachedResult = getCachedValue(cacheName, filtro);
		if (cachedResult != null && cachedResult.isEmpty()) {
			evictCache(cacheName, filtro);
			cachedResult = null;
		}
		
		if (cachedResult != null) {
			return cachedResult;
		}
		
		List<UbigeoDistritoDto> result = this.maeUbigeRepositoryoCustom.listarDistritosPorIdEleccionII(filtro).stream()
				.sorted(Comparator.comparing(UbigeoDistritoDto::getNombre,
						Collator.getInstance(Locale.of("es", "PE"))))
				.toList();
		
		if (!result.isEmpty()) {
			putInCache(cacheName, filtro, result);
		}
		
		return result;
	}
	
	@Override
	public List<UbigeoLocalVotacionDto> listarLocalVotacionPorIdEleccion(FiltroUbigeoLocalVotacionDto filtro) {
		String cacheName = "ubigeos_local_votacion_cache";
		
		List<UbigeoLocalVotacionDto> cachedResult = getCachedValue(cacheName, filtro);
		if (cachedResult != null && cachedResult.isEmpty()) {
			evictCache(cacheName, filtro);
			cachedResult = null;
		}
		
		if (cachedResult != null) {
			return cachedResult;
		}
		
		List<UbigeoLocalVotacionDto> result = this.maeLocalVotacionRepositoryCustom.listarLocalVotacionPorIdEleccion(filtro);
		
		if (!result.isEmpty()) {
			putInCache(cacheName, filtro, result);
		}
		
		return result;
	}
	
	@Override
	public List<UbigeoDistritoDto> listarDepProvDistritosPorIdEleccionII(FiltroUbigeoDepartamentoDto filtro) {
		String cacheName = "ubigeos_dep_prov_dist_cache";
		
		List<UbigeoDistritoDto> cachedResult = getCachedValue(cacheName, filtro);
		if (cachedResult != null && cachedResult.isEmpty()) {
			evictCache(cacheName, filtro);
			cachedResult = null;
		}
		
		if (cachedResult != null) {
			return cachedResult;
		}
		
		List<UbigeoDistritoDto> result = this.maeUbigeRepositoryoCustom.listarDepProvDistritoPorIdEleccion().stream().sorted(Comparator.comparing(UbigeoDistritoDto::getNombre)).toList();
		
		if (!result.isEmpty()) {
			putInCache(cacheName, filtro, result);
		}
		
		return result;
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

	// Métodos auxiliares para manejo de caché
	
	@SuppressWarnings("unchecked")
	private <T> T getCachedValue(String cacheName, Object key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			Cache.ValueWrapper wrapper = cache.get(key);
			if (wrapper != null) {
				return (T) wrapper.get();
			}
		}
		return null;
	}
	
	private void evictCache(String cacheName, Object key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.evict(key);
		}
	}
	
	private void putInCache(String cacheName, Object key, Object value) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.put(key, value);
		}
	}

}
