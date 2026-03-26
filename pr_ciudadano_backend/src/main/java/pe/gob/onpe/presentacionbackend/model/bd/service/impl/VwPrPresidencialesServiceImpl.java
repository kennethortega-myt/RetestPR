package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.*;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrPresidencialesRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrPresidencialesService;
import pe.gob.onpe.presentacionbackend.model.dto.eleccionpresidencial.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static pe.gob.onpe.presentacionbackend.utils.ConstantesComunes.OP_ESTADO_NOPARTICIPA;

@Service
@Slf4j
@RequiredArgsConstructor
public class VwPrPresidencialesServiceImpl implements VwPrPresidencialesService {

	private static final String CADENA_INICIO = "(?i).*";
	private static final String CADENA_FIN = ".*";
	public static final String ELECCION = "eleccion";
	public static final String AMBITO_GEOGRAFICO = "ambito_geografico";
	public static final String UBIGEO_NIVEL_01 = "ubigeo_nivel_01";
	public static final String UBIGEO_NIVEL_02 = "ubigeo_nivel_02";
	public static final String UBIGEO_NIVEL_03 = "ubigeo_nivel_03";

	private final VwPrPresidencialesRepository vwPrPresidencialesRepository;

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
		Predicate<FiltroEleccionPresidencialDto> tieneEleccionAndFiltroop = data ->
				data.getTipoFiltro()!= null && !data.getTipoFiltro().isEmpty()
						&& data.getIdEleccion() != null && data.getIdEleccion() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneAmbitoop = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo1op = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo2op = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionPresidencialDto> tieneUbigeo3op = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;


		List<VwPrPresidenciales> registros = null;
		if(tieneEleccionAndFiltroop.and(tieneAmbitoop.negate()).and(tieneUbigeo1op.negate()).and(tieneUbigeo2op.negate()).and(tieneUbigeo3op.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltroop.and(tieneAmbitoop).and(tieneUbigeo1op.negate()).and(tieneUbigeo2op.negate()).and(tieneUbigeo3op.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltroop.and(tieneAmbitoop).and(tieneUbigeo1op).and(tieneUbigeo2op.negate()).and(tieneUbigeo3op.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltroop.and(tieneAmbitoop).and(tieneUbigeo1op).and(tieneUbigeo2op).and(tieneUbigeo3op.negate()).test(filtros)){
			registros = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,1,null);
		} else if(tieneEleccionAndFiltroop.and(tieneAmbitoop).and(tieneUbigeo1op).and(tieneUbigeo2op).and(tieneUbigeo3op).test(filtros)){
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
		List<VwPrEleccionBaseDetalle> detallePresi = registros.get(0).getDetalle().stream()
				.filter(data -> data.getVotos() != null)
				.filter(data -> null != data.getEstado())
				.filter(data -> !data.getEstado().equals(OP_ESTADO_NOPARTICIPA))
				.filter(data -> data.getEstado().compareTo(1) == 0)
				//.distinct()
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.toList();
		if(grafico != null) {
			detallePresi = detallePresi.stream().filter(data -> data.getGrafico().compareTo(grafico) == 0).toList();
		}
		if(nombreApellidoAgrupacion != null && !nombreApellidoAgrupacion.isEmpty()) {
			detallePresi = detallePresi.stream()
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

		return  detallePresi.stream().map(VwPrPresidencialesServiceImpl::mapperCampos)
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
				.codigoAgrupacionPolitica(registro.getAgrupacionPolitica().toString())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.nombreCandidato(nombreCompletoCandidato)
				.dniCandidato(dniCandidato)
				.build();
	}

	VwPrPresidenciales mapperCamposActualizar(VwPrPresidenciales registroNuevo, VwPrPresidenciales registroActual,Long idActa, String usuario){
		VwPrEleccionBaseServiceImpl.mapeoCamposActualizar(registroNuevo, registroActual);
		registroActual.setHistorico(obtenerHistoricos(registroActual,idActa,usuario));
		return registroActual;
	}

	private List<VwPrPresidencialesHistorico> obtenerHistoricos(VwPrPresidenciales vistaPresidencialActual, Long acta, String usuario) {

		List<VwPrPresidencialesHistorico> historicoTotal;
		if (vistaPresidencialActual.getHistorico() != null) {
			historicoTotal = vistaPresidencialActual.getHistorico();
			historicoTotal.add(this.mapperPresidencialHistorico(vistaPresidencialActual,acta,usuario));
		} else {
			historicoTotal = Collections.singletonList(this.mapperPresidencialHistorico(vistaPresidencialActual, acta, usuario));
		}
		return historicoTotal;
	}

	private VwPrPresidencialesHistorico mapperPresidencialHistorico(VwPrPresidenciales presidencialActual, Long acta, String usuario) {
		List<VwPrEleccionBaseDetalle> detalleList = Collections.emptyList();
		if(presidencialActual.getDetalle() != null) {
			detalleList = presidencialActual.getDetalle().stream()
					.map(data -> {
						List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
						if(data.getCandidato() != null ) {
							candidatoList = data.getCandidato().stream()
									.map(candidato -> {
										VwPrEleccionBaseDetalleCandidato dataPresih = new VwPrEleccionBaseDetalleCandidato();
												dataPresih.setNombres(candidato.getNombres());
												dataPresih.setApellidoPaterno(candidato.getApellidoMaterno());
												dataPresih.setApellidoMaterno(candidato.getApellidoPaterno());
												dataPresih.setCargo(candidato.getCargo());
												dataPresih.setDocumentoIdentidad(candidato.getDocumentoIdentidad());
										return dataPresih;
									})
									.toList();
						}
						VwPrEleccionBaseDetalle detallepresih = new VwPrEleccionBaseDetalle();
			            detallepresih.setAgrupacionPolitica(data.getAgrupacionPolitica());
			            detallepresih.setCodigo(data.getCodigo());
			            detallepresih.setEstado(data.getEstado());
			            detallepresih.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
			            detallepresih.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
			            detallepresih.setDescripcion(data.getDescripcion());
			            detallepresih.setVotos(data.getVotos());
			            detallepresih.setGrafico(data.getGrafico());
			            detallepresih.setPosicion(data.getPosicion());
			            detallepresih.setCandidato(candidatoList);
			            return detallepresih;
					})
					.toList();
		}
		return VwPrPresidencialesHistorico.builder()
				.id(presidencialActual.getId())
				.acta(acta)
				.totalElectoresHabiles(presidencialActual.getTotalElectoresHabiles())
				.totalActas(presidencialActual.getTotalActas())
				.participacionCiudadana(presidencialActual.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(presidencialActual.getPorcentajeParticipacionCiudadana())
				.actasContabilizadas(presidencialActual.getActasContabilizadas())
				.porcentajeActasContabilizadas(presidencialActual.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(presidencialActual.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(presidencialActual.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(presidencialActual.getActasPendientes())
				.porcentajeActasPendientes(presidencialActual.getPorcentajeActasPendientes())
				.totalVotosEmitidos(presidencialActual.getTotalVotosEmitidos())
				.totalVotosValidos(presidencialActual.getTotalVotosValidos())
				.audUsuarioModificacion(usuario)
				.audFechaModificacion(new Date())
				.detalle(detalleList)
				.build();
	}


}
