package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeUbigeo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrEleccionBaseDetalleCandidato;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrPresidenciales;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeUbigeoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.VwPrPresidencialesRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.VwPrPresidencialesService;
import pe.gob.onpe.consultaopbackend.model.dto.eleccionpresidencial.*;

import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
public class VwPrPresidencialesServiceImpl implements VwPrPresidencialesService {

	private final String CADENA_INICIO = "(?i).*";
	private final String CADENA_FIN = ".*";
	@Autowired
	private VwPrPresidencialesRepository vwPrPresidencialesRepository;

	@Autowired
	private MaeUbigeoRepository maeUbigeoRepository;

	@Override
	public void save(VwPrPresidenciales k) {
		this.vwPrPresidencialesRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrPresidenciales> k) {
		this.vwPrPresidencialesRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrPresidencialesRepository.deleteAll();
	}

	@Override
	public List<VwPrPresidenciales> findAll() {
		return this.vwPrPresidencialesRepository.findAll();
	}

	@Override
	public List<ParticipantePresidencialDto> listarParticipantesUbicacionGeografica(FiltroEleccionPresidencialDto filtros) {

		Predicate<FiltroEleccionPresidencialDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneAmbito = data ->  data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;


		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,1,null);
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantesUbicacionGeografica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}

	}

	@Override
	public List<ParticipantePresidencialDto> listarParticipantesUbicacionGeograficaNombre(FiltroEleccionPresidencialDto filtros) {
		Predicate<FiltroEleccionPresidencialDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,null, filtros.getNombreApellidoPartido());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,null, filtros.getNombreApellidoPartido());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,null, filtros.getNombreApellidoPartido());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,null, filtros.getNombreApellidoPartido());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,null, filtros.getNombreApellidoPartido());
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantesUbicacionGeograficaNombre, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipantePresidencialDto> listarParticipantesOrganizacionPolitica(FiltroEleccionPresidencialDto filtros) {
		Predicate<FiltroEleccionPresidencialDto> tieneEleccionAndFiltro= data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;


		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,1,null);
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantesOrganizacionPolitica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}


	private List<ParticipantePresidencialDto> construirRespuesta(List<VwPrPresidenciales> registros, Integer grafico, String nombreApellidoAgrupacion){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = registros.get(0).getDetalle().stream()
				.filter(data -> data.getVotos() != null)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.distinct()
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.toList();
		if(grafico != null) {
			detalle = detalle.stream().filter(data -> data.getGrafico().compareTo(grafico) == 0).toList();
		}
		if(nombreApellidoAgrupacion != null && !nombreApellidoAgrupacion.isEmpty()) {
			detalle = detalle.stream()
					 .filter(data -> data.getGrafico().compareTo(1) == 0)
					 .filter(data ->
						data.getDescripcion().matches(CADENA_INICIO + nombreApellidoAgrupacion + CADENA_FIN)
						|| data.getCandidato().stream()
								.anyMatch(candidato ->
									   candidato.getNombres().matches(CADENA_INICIO + nombreApellidoAgrupacion + CADENA_FIN)
									|| candidato.getApellidoMaterno().matches(CADENA_INICIO + nombreApellidoAgrupacion + CADENA_FIN)
									|| candidato.getApellidoPaterno().matches(CADENA_INICIO + nombreApellidoAgrupacion + CADENA_FIN))
					 ).toList();
		}

		return  detalle.stream().map(VwPrPresidencialesServiceImpl::mapperCampos)
				.toList();
	}
	private static ParticipantePresidencialDto mapperCampos(VwPrEleccionBaseDetalle registro){
		VwPrEleccionBaseDetalleCandidato candidatoBd = null;
		String nombreCompletoCandidato = "";
		String dniCandidato = "";
		if(!registro.getCandidato().isEmpty()) {
			candidatoBd =  registro.getCandidato().get(0);
			nombreCompletoCandidato = candidatoBd.getNombres() + " " + candidatoBd.getApellidoPaterno() + " " + candidatoBd.getApellidoMaterno();
			dniCandidato = candidatoBd.getDocumentoIdentidad();
		}

		return ParticipantePresidencialDto.builder()
				.totalVotosValidos(registro.getVotos() != null ? registro.getVotos() : 0)
				.porcentajeVotosEmitidos(registro.getPorcentajeVotosEmitidos())
				.porcentajeVotosValidos(registro.getPorcentajeVotosValidos())
				.codigoAgrupacionPolitica(registro.getAgrupacionPolitica())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.nombreCandidato(nombreCompletoCandidato)
				.dniCandidato(dniCandidato)
				.build();
	}



	@Override
	public List<ParticipantePresidencialReporteDto> listarParticipantesUbicacionGeograficaReporte(FiltroEleccionPresidencialReporteDto filtros) {

		Predicate<FiltroEleccionPresidencialReporteDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return  construirRespuestaReporte(registros,true,true);
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantesUbicacionGeograficaReporte, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}

	}

	@Override
	public List<ParticipantePresidencialReporteDto> listarParticipantesUbicacionResumenGeneral(FiltroEleccionPresidencialReporteDto filtros) {
		Predicate<FiltroEleccionPresidencialReporteDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuestaReporte(registros,false,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuestaReporte(registros,false,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuestaReporte(registros,false,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuestaReporte(registros,false,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return  construirRespuestaReporte(registros,false,true);
		} else {
			log.info("Servicio eleccion presidencial - listarParticipantes resumen general, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipantePresidencialOrganizacionReporteDto> listarParticipantesOrganizacionPoliticaReporte(FiltroEleccionPresidencialReporteDto filtros) {

		Predicate<FiltroEleccionPresidencialReporteDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneAmbito= data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;


		String tipoFiltro = switch (filtros.getTipoFiltro()) {
			case "eleccion" -> "ambito_geografico";
			case "ambito_geografico" -> "ubigeo_nivel_01";
			case "ubigeo_nivel_01" -> "ubigeo_nivel_02";
			case "ubigeo_nivel_02", "ubigeo_nivel_03" -> "ubigeo_nivel_03";
            case "distrito_electoral" -> "distrito_electoral";
			default -> "";
		};

		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), tipoFiltro);
			return  construirRespuestaOrganizacionReporte(registros,filtros);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), tipoFiltro,filtros.getIdAmbitoGeografico());
			return  construirRespuestaOrganizacionReporte(registros,filtros);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuestaOrganizacionReporte(registros,filtros);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuestaOrganizacionReporte(registros,filtros);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),tipoFiltro,filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuestaOrganizacionReporte(registros,filtros);
		} else {
			return Collections.emptyList();
		}

	}

	private List<ParticipantePresidencialReporteDto> construirRespuestaReporte(List<VwPrPresidenciales> registros,boolean conVotoNulo, boolean conCandidato){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados para reporte no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = new ArrayList<>(registros.get(0).getDetalle().stream()
				.filter(data -> data.getVotos() != null)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.distinct()
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.toList());
		if(conVotoNulo) {
			Optional<VwPrEleccionBaseDetalle> votoBlanco = detalle.stream().filter(data -> data.getCodigo().equals("80")).findFirst();
			Optional<VwPrEleccionBaseDetalle> votoNulo = detalle.stream().filter(data -> data.getCodigo().equals("81")).findFirst();

			detalle.removeIf(data -> data.getCodigo().equals("80") || data.getCodigo().equals("81"));

			votoBlanco.ifPresent(detalle::add);
			votoNulo.ifPresent(detalle::add);
		} else {
			detalle.removeIf(data -> data.getCodigo().equals("80") || data.getCodigo().equals("81"));
		}


		return  detalle.stream().map(detalleRegistro -> mapperCamposReporte(detalleRegistro,conCandidato))
				.toList();
	}

	private List<ParticipantePresidencialOrganizacionReporteDto> construirRespuestaOrganizacionReporte(List<VwPrPresidenciales> registros, FiltroEleccionPresidencialReporteDto filtros){
		List<ParticipantePresidencialOrganizacionReporteDto> listaReporte = new ArrayList<>(registros.stream()
				.map(data -> mapperCamposOrgPoliticaReporte(data, filtros.getTipoFiltro(), filtros.getIdOrgPolitica()))
				.toList());


		if(!filtros.getTipoFiltro().equals("eleccion")){
			List<Long> idUbigeos = listaReporte.stream().map(ParticipantePresidencialOrganizacionReporteDto::getIdDetalleUbicacion)
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

		listaReporte.sort(Comparator.comparing(ParticipantePresidencialOrganizacionReporteDto::getDetalleUbicacion));

		return  listaReporte;

	}

	private static ParticipantePresidencialReporteDto mapperCamposReporte(VwPrEleccionBaseDetalle registro, boolean mostrarCandidato){

		VwPrEleccionBaseDetalleCandidato candidatoBd = null;
		String nombreCompletoCandidato = "";
		if(mostrarCandidato && !registro.getCandidato().isEmpty()) {
			candidatoBd =  registro.getCandidato().get(0);
			nombreCompletoCandidato = candidatoBd.getNombres() + " " + candidatoBd.getApellidoPaterno() + " " + candidatoBd.getApellidoMaterno();
		}

		return ParticipantePresidencialReporteDto.builder()
				.totalVotos(registro.getVotos() != null ? registro.getVotos() : 0)
				.orgPolitica(registro.getDescripcion())
				.codOrgPolitica(registro.getCodigo())
				.candidato(nombreCompletoCandidato)
				.votosEmitidos(registro.getPorcentajeVotosEmitidos())
				.votosValidos(registro.getPorcentajeVotosValidos())
				.build();
	}

	private static ParticipantePresidencialOrganizacionReporteDto mapperCamposOrgPoliticaReporte(VwPrPresidenciales registro, String tipoFiltro, Integer orgPolitica){

		Integer votos = 0;
		Double votosValidos = 0.0;
		Integer idDetalle = 0;
		String detalleUbicacion = null;
		Optional<VwPrEleccionBaseDetalle> deltalle = registro.getDetalle().stream()
				.filter(detalle -> detalle.getAgrupacionPolitica().compareTo(orgPolitica) == 0)
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				.findAny();
		VwPrEleccionBaseDetalle presidencialesDetalle = null;
		if(deltalle.isPresent()){
			presidencialesDetalle = deltalle.get();
			votos = presidencialesDetalle.getVotos();
			votosValidos = presidencialesDetalle.getPorcentajeVotosValidos();
		}

        switch (tipoFiltro) {
            case "eleccion" -> {
                detalleUbicacion = registro.getAmbitoGeografico().compareTo(1) == 0 ? "PERÚ" : "EXTRANJERO";
                idDetalle = registro.getAmbitoGeografico();
            }
            case "ambito_geografico" -> idDetalle = registro.getUbigeoNivel01();
            case "ubigeo_nivel_01" -> idDetalle = registro.getUbigeoNivel02();
            case "ubigeo_nivel_02", "ubigeo_nivel_03" -> idDetalle = registro.getUbigeoNivel03();
        }

		return ParticipantePresidencialOrganizacionReporteDto.builder()
				.totalVotos(votos)
				.idDetalleUbicacion(Long.valueOf(idDetalle))
				.detalleUbicacion(detalleUbicacion)
				.votosValidos(votosValidos)
				.build();
	}

}
