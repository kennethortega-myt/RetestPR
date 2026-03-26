package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.ParticipacionCiudadanaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.ParticipacionCiudadanaRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipacionCiudadanaServiceImpl implements ParticipacionCiudadanaService {


	private static final String TOTAL = "total";
	private static final String AMBITO_GEOGRAFICO = "ambito_geografico";
	private static final String UBIGEO_NIVEL_01 = "ubigeo_nivel_01";
	private static final String UBIGEO_NIVEL_02 = "ubigeo_nivel_02";
	private static final String UBIGEO_NIVEL_03 = "ubigeo_nivel_03";
	public static final String LOCAL_VOTACION = "local_votacion";

	private final ParticipacionCiudadanaRepository participacionCiudadanaRepository;
	private final ParticipacionCiudadanaRepositoryCustom participacionCiudadanaRepositoryCustom;
	private final MaeUbigeoRepository maeUbigeoRepository;

	@Override
	public void save(VwPrParticipacionCiudadana k) {
		this.participacionCiudadanaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrParticipacionCiudadana> k) {
		this.participacionCiudadanaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.participacionCiudadanaRepository.deleteAll();
	}

	@Override
	public List<VwPrParticipacionCiudadana> findAll() {
		return this.participacionCiudadanaRepository.findAll();
	}

	@Override
	public List<ParticipacionCiudadanaResponseDto> obtenerParticipacionCiudadanaXDep(FiltroParticipacionCiudadana filtroParticipacionCiudadana) {
		return this.participacionCiudadanaRepositoryCustom.obtenerParticipacionCiudadanaXDep(filtroParticipacionCiudadana);
	}

	@Override
	public Optional<ParticipacionTotalesResponseDto> obtenerTotales(FiltroParticipacionDto filtros) {

		Predicate<FiltroParticipacionDto> tienefiltro = data -> data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionDto> tieneAmbito = data -> data.getIdAmbitoGeografico() != null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo1 = data -> data.getUbigeoNivel01() != null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo2 = data -> data.getUbigeoNivel02() != null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo3 = data -> data.getUbigeoNivel03() != null && data.getUbigeoNivel03() != 0;
		Predicate<FiltroParticipacionDto> tieneLocalVotacion = data -> data.getIdLocalVotacion() != null && data.getIdLocalVotacion().compareTo(0L) != 0;


		if(tienefiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
			return participacionCiudadanaRepository.findByTipoFiltro(filtros.getTipoFiltro())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){

			return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
			return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).and(tieneLocalVotacion.negate()).test(filtros)){
			return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).and(tieneLocalVotacion.negate()).test(filtros)){
			return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).and(tieneLocalVotacion).test(filtros)){
			return participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03AndIdLocalVotacion(filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03(),filtros.getIdLocalVotacion())
					.stream()
					.findAny()
					.map(ParticipacionCiudadanaServiceImpl::mapperTotales);
		}
		log.info("servicio de participación ciudadana - obtenerTotales - request no mapeado:  " + filtros);
		return Optional.empty();
	}

	@Override
	public Optional<ParticipacionDetalleResponseDto> listarUbigeos(FiltroParticipacionDto filtros, int pagina, int tamanio) {

		Predicate<FiltroParticipacionDto> tienefiltroLu = data ->	data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionDto> tieneAmbitoLu = data -> data.getIdAmbitoGeografico()!= null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo1Lu = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo2Lu = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo3Lu = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltroLu = switch (filtros.getTipoFiltro()) {
			case TOTAL -> AMBITO_GEOGRAFICO;
            case AMBITO_GEOGRAFICO -> UBIGEO_NIVEL_01;
            case UBIGEO_NIVEL_01 -> UBIGEO_NIVEL_02;
            case UBIGEO_NIVEL_02 -> UBIGEO_NIVEL_03;
            case UBIGEO_NIVEL_03 -> LOCAL_VOTACION;
            default -> "";
        };

		Pageable paginacion = PageRequest.of(pagina,tamanio);
		Page<VwPrParticipacionCiudadana> registros = null;

		if(tienefiltroLu.and(tieneAmbitoLu.negate()).and(tieneUbigeo1Lu.negate()).and(tieneUbigeo2Lu.negate()).and(tieneUbigeo3Lu.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltro(tipoFiltroLu,paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltroLu.and(tieneAmbitoLu).and(tieneUbigeo1Lu.negate()).and(tieneUbigeo2Lu.negate()).and(tieneUbigeo3Lu.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(tipoFiltroLu,filtros.getIdAmbitoGeografico(),paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltroLu.and(tieneAmbitoLu).and(tieneUbigeo1Lu).and(tieneUbigeo2Lu.negate()).and(tieneUbigeo3Lu.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(tipoFiltroLu,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltroLu.and(tieneAmbitoLu).and(tieneUbigeo1Lu).and(tieneUbigeo2Lu).and(tieneUbigeo3Lu.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(tipoFiltroLu,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(), paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltroLu.and(tieneAmbitoLu).and(tieneUbigeo1Lu).and(tieneUbigeo2Lu).and(tieneUbigeo3Lu).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(tipoFiltroLu,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03(), paginacion);
			return construirRespuesta(registros);
		} else {
			log.info("servicio de participación ciudadana - listarUbigeos - request no mapeado:  " + filtros);
			return Optional.empty();
		}

	}

	private Optional<ParticipacionDetalleResponseDto> construirRespuesta(Page<VwPrParticipacionCiudadana> registros){

		List<ParticipacionUbigeosResponseDto> ubigeos = registros.getContent().stream()
				.map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
				.toList();

		return Optional.of(ParticipacionDetalleResponseDto.builder()
				.ubigeos(ubigeos)
				.paginaActual(registros.getNumber())
				.totalPaginas(registros.getTotalPages())
				.totalRegistros(registros.getTotalElements())
				.build());
	}

	private static ParticipacionTotalesResponseDto mapperTotales(VwPrParticipacionCiudadana registroUnico){

		return ParticipacionTotalesResponseDto.builder()
				.totalAusentes(registroUnico.getTotalAusentes())
				.totalElectoresHabiles(registroUnico.getTotalElectoresHabiles())
				.totalAsistentes(registroUnico.getTotalAsistentes())
				.porcentajeAsistentes(registroUnico.getPorcentajeAsistentes())
				.porcentajeAusentes(registroUnico.getPorcentajeAusentes())
				.build();
	}

	private static ParticipacionUbigeosResponseDto mapperUbigeos(VwPrParticipacionCiudadana registroUnico){

		return ParticipacionUbigeosResponseDto.builder()
				.porcentajeAsistentes(registroUnico.getPorcentajeAsistentes())
				.porcentajeAusentes(registroUnico.getPorcentajeAusentes())
				.ambitoGeografico(registroUnico.getAmbitoGeografico())
				.ubigeoNivel01(registroUnico.getUbigeoNivel01())
				.ubigeoNivel02(registroUnico.getUbigeoNivel02())
				.ubigeoNivel03(registroUnico.getUbigeoNivel03())
				.idLocalVotacion(registroUnico.getIdLocalVotacion())
				.build();
	}

	@Override
	public List<ParticipacionUbigeosResponseDto> listarUbigeosTotal(FiltroParticipacionDto filtros) {

		Predicate<FiltroParticipacionDto> tienefiltro = data ->	data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!= null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltro = switch (filtros.getTipoFiltro()) {
			case TOTAL -> AMBITO_GEOGRAFICO;
			case AMBITO_GEOGRAFICO -> UBIGEO_NIVEL_01;
			case UBIGEO_NIVEL_01 -> UBIGEO_NIVEL_02;
			case UBIGEO_NIVEL_02 -> UBIGEO_NIVEL_03;
			case UBIGEO_NIVEL_03 -> LOCAL_VOTACION;
			default -> "";
		};

		List<ParticipacionUbigeosResponseDto> registros = null;

		if(tienefiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltro(tipoFiltro)
					.stream().map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
					.toList();
			return  registros;
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(tipoFiltro,filtros.getIdAmbitoGeografico()).stream().map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
					.toList();
			return  registros;
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01()).stream().map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
					.toList();
			return  registros;
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02()).stream().map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
					.toList();
			return  registros;
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03()).stream().map(ParticipacionCiudadanaServiceImpl::mapperUbigeos)
					.toList();
			return  registros;
		} else {
			log.info("servicio de participación ciudadana - listarUbigeos total - request no mapeado:  " + filtros);
			return Collections.emptyList();
		}

	}

	@Override
	public List<ActaMapaCalorResponseDto> listarMapaCalor(ActaMapaCalorRequestDto filtros) {

		Predicate<ActaMapaCalorRequestDto> tieneTipoFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty();
		Predicate<ActaMapaCalorRequestDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<ActaMapaCalorRequestDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltro = obtenerTipoFiltro(filtros.getTipoFiltro());

		List<VwPrParticipacionCiudadana> registros = null;
		if(tieneTipoFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltro(tipoFiltro);
			return  construirRespuesta(registros);
		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(tipoFiltro,filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros);
		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
			return  construirRespuesta(registros);
		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
			return  construirRespuesta(registros);
		} else if(tieneTipoFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
			return construirRespuesta(registros);
		} else {
			return Collections.emptyList();
		}

	}

	private String obtenerTipoFiltro(String tipoFiltro)  {

		return switch (tipoFiltro) {
			case "todos" -> TOTAL;
			case TOTAL -> AMBITO_GEOGRAFICO;
			case AMBITO_GEOGRAFICO -> UBIGEO_NIVEL_01;
			case UBIGEO_NIVEL_01 -> UBIGEO_NIVEL_02;
			case UBIGEO_NIVEL_02 -> UBIGEO_NIVEL_03;
			case UBIGEO_NIVEL_03 -> LOCAL_VOTACION;
			default -> "";
		};
	}

	private List<ActaMapaCalorResponseDto> construirRespuesta(List<VwPrParticipacionCiudadana> registros){

		return  registros.stream()
				.map(ParticipacionCiudadanaServiceImpl::mapperMapaCalor)
				.toList();
	}
	private static ActaMapaCalorResponseDto mapperMapaCalor(VwPrParticipacionCiudadana registro){

		return ActaMapaCalorResponseDto.builder()
				.porcentajeAsistentes(registro.getPorcentajeAsistentes())
				.asistentes(registro.getTotalAsistentes())
				.ambitoGeografico(registro.getAmbitoGeografico())
				.ubigeoNivel01(registro.getUbigeoNivel01())
				.ubigeoNivel02(registro.getUbigeoNivel02())
				.ubigeoNivel03(registro.getUbigeoNivel03())
				.build();

	}
	@Override
	public List<ParticipacionTotalesResponseReporteDto> listarUbigeosReporte(FiltroParticipacionReporteDto filtros) {
		Predicate<FiltroParticipacionReporteDto> tienefiltro = data ->	data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!= null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltro = switch (filtros.getTipoFiltro()) {
			case TOTAL -> AMBITO_GEOGRAFICO;
			case AMBITO_GEOGRAFICO -> UBIGEO_NIVEL_01;
			case UBIGEO_NIVEL_01 -> UBIGEO_NIVEL_02;
			case UBIGEO_NIVEL_02, UBIGEO_NIVEL_03 -> UBIGEO_NIVEL_03;
            default -> "";
		};


		List<VwPrParticipacionCiudadana> registros = null;

		if(tienefiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltro(tipoFiltro);
			return  construirRespuestaReporte(registros,filtros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(tipoFiltro,filtros.getIdAmbitoGeografico());
			return  construirRespuestaReporte(registros,filtros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01());
			return  construirRespuestaReporte(registros,filtros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02());
			return  construirRespuestaReporte(registros,filtros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03());
			return construirRespuestaReporte(registros,filtros);
		} else {
			log.info("servicio de participación ciudadana - listarUbigeos reporte - request no mapeado:  " + filtros);
			return Collections.emptyList();
		}
	}

	private List<ParticipacionTotalesResponseReporteDto> construirRespuestaReporte(List<VwPrParticipacionCiudadana> registros, FiltroParticipacionReporteDto filtros) {


		List<ParticipacionTotalesResponseReporteDto> listaReporte = registros.stream()
				.map(data -> mapperCamposReporte(data,filtros.getTipoFiltro()))
				.toList();

		if(!filtros.getTipoFiltro().equals(TOTAL)){
			List<Long> idUbigeos = listaReporte.stream().map(ParticipacionTotalesResponseReporteDto::getIdDetalleUbicacion)
					.filter(Objects::nonNull)
					.toList();
			List<MaeUbigeo> ubigeospc;
			if(!idUbigeos.isEmpty()){
				ubigeospc = maeUbigeoRepository.findByIds(idUbigeos);
			} else {
				ubigeospc = null;
			}
			if(ubigeospc!= null){
				listaReporte.forEach(data ->  {
					Optional<MaeUbigeo> ubigeo = ubigeospc.stream()
							.filter(ele -> ele.getId().compareTo(data.getIdDetalleUbicacion())==0).findFirst();
					ubigeo.ifPresent(ubi ->  data.setDetalleUbicacion(ubi.getCNombre()));
				});
			}


		}

		return  listaReporte;
	}

	private static ParticipacionTotalesResponseReporteDto mapperCamposReporte(VwPrParticipacionCiudadana registro, String tipoFiltro){

		Integer idDetalle = 0;
		String detalleUbicacion = null;

		switch (tipoFiltro) {
			case TOTAL -> {
				detalleUbicacion = registro.getAmbitoGeografico().compareTo(1) == 0 ? "PERÚ" : "EXTRANJERO";
				idDetalle = registro.getAmbitoGeografico();
			}
			case AMBITO_GEOGRAFICO -> idDetalle = registro.getUbigeoNivel01();
			case UBIGEO_NIVEL_01 -> idDetalle = registro.getUbigeoNivel02();
			case UBIGEO_NIVEL_02, UBIGEO_NIVEL_03 -> idDetalle = registro.getUbigeoNivel03();
			default -> idDetalle = 0;
        }

		return ParticipacionTotalesResponseReporteDto.builder()
				.idDetalleUbicacion(Long.valueOf(idDetalle))
				.detalleUbicacion(detalleUbicacion)
				.asistentes(registro.getTotalAsistentes())
				.asistentesPorcentaje(registro.getPorcentajeAsistentes())
				.ausentes(registro.getTotalAusentes())
				.ausentesPorcentaje(registro.getPorcentajeAusentes())
				.electoresHabiles(registro.getTotalElectoresHabiles())
				.build();
	}

}
