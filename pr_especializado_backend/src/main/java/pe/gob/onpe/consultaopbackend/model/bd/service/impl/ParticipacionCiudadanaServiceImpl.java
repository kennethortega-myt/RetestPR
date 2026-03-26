package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrParticipacionCiudadana;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.ParticipacionCiudadanaRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.ParticipacionCiudadanaRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
public class ParticipacionCiudadanaServiceImpl implements ParticipacionCiudadanaService {
	
	@Autowired
	private ParticipacionCiudadanaRepository participacionCiudadanaRepository;

	@Autowired
	private ParticipacionCiudadanaRepositoryCustom participacionCiudadanaRepositoryCustom;

	@Autowired
	private MaeUbigeoRepository maeUbigeoRepository;

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

		Predicate<FiltroParticipacionDto> tienefiltro = data ->	data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!= null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltro = switch (filtros.getTipoFiltro()) {
			case "total" -> "ambito_geografico";
            case "ambito_geografico" -> "ubigeo_nivel_01";
            case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
            case "ubigeo_nivel_02" -> "ubigeo_nivel_03";
            case "ubigeo_nivel_03" -> "local_votacion";
            default -> "";
        };

		Pageable paginacion = PageRequest.of(pagina,tamanio);
		Page<VwPrParticipacionCiudadana> registros = null;

		if(tienefiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltro(tipoFiltro,paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeografico(tipoFiltro,filtros.getIdAmbitoGeografico(),paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(), paginacion);
			return  construirRespuesta(registros);
		} else if(tienefiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = participacionCiudadanaRepository.findByTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel01(),filtros.getUbigeoNivel02(),filtros.getUbigeoNivel03(), paginacion);
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
			case "total" -> "ambito_geografico";
			case "ambito_geografico" -> "ubigeo_nivel_01";
			case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
			case "ubigeo_nivel_02" -> "ubigeo_nivel_03";
			case "ubigeo_nivel_03" -> "local_votacion";
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
	public List<ParticipacionTotalesResponseReporteDto> listarUbigeosReporte(FiltroParticipacionReporteDto filtros) {
		Predicate<FiltroParticipacionReporteDto> tienefiltro = data ->	data.getTipoFiltro() != null && !data.getTipoFiltro().isEmpty();
		Predicate<FiltroParticipacionReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!= null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel01()!= null && data.getUbigeoNivel01() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel02()!= null && data.getUbigeoNivel02() != 0;
		Predicate<FiltroParticipacionReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel03()!= null && data.getUbigeoNivel03() != 0;

		String tipoFiltro = switch (filtros.getTipoFiltro()) {
			case "total" -> "ambito_geografico";
			case "ambito_geografico" -> "ubigeo_nivel_01";
			case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
			case "ubigeo_nivel_02", "ubigeo_nivel_03" -> "ubigeo_nivel_03";
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


		if(!filtros.getTipoFiltro().equals("total")){
			List<Long> idUbigeos = listaReporte.stream().map(ParticipacionTotalesResponseReporteDto::getIdDetalleUbicacion)
					.filter(Objects::nonNull)
					.toList();
			List<MaeUbigeo> ubigeos;
			if(!idUbigeos.isEmpty()){
				ubigeos = maeUbigeoRepository.findByIds(idUbigeos);
			} else {
				ubigeos = null;
			}
			if(ubigeos!= null){
				listaReporte.forEach(data ->  {
					Optional<MaeUbigeo> ubigeo = ubigeos.stream()
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
			case "total" -> {
				detalleUbicacion = registro.getAmbitoGeografico().compareTo(1) == 0 ? "PERÚ" : "EXTRANJERO";
				idDetalle = registro.getAmbitoGeografico();
			}
			case "ambito_geografico" -> idDetalle = registro.getUbigeoNivel01();
			case "ubigeo_nivel_01" -> idDetalle = registro.getUbigeoNivel02();
			case "ubigeo_nivel_02", "ubigeo_nivel_03" -> idDetalle = registro.getUbigeoNivel03();
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
