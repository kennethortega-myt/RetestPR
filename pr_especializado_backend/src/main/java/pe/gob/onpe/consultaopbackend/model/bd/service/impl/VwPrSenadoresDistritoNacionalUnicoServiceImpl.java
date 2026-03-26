package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrSenadoresDistritoNacionalUnico;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeCandidatoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeCandidatoRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.VwPrSenadoresDistritoNacionalUnicoRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.VwPrSenadoresDistritoNacionalUnicoRepositoryCustom;
import pe.gob.onpe.consultaopbackend.model.bd.service.VwPrSenadoresDistritoNacionalUnicoService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaAgrupacion;
import pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritounico.*;

import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
public class VwPrSenadoresDistritoNacionalUnicoServiceImpl implements VwPrSenadoresDistritoNacionalUnicoService {


	private final String CADENA_INICIO = "(?i).*";
	private final String CADENA_FIN = ".*";

	@Autowired
	private VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;

	@Autowired
	private VwPrSenadoresDistritoNacionalUnicoRepositoryCustom vwPrSenadoresDistritoNacionalUnicoRepositoryCustom;

	@Autowired
	private MaeCandidatoRepository maeCandidatoRepository;

	@Autowired
	private MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom;


	@Override
	public void save(VwPrSenadoresDistritoNacionalUnico k) {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrSenadoresDistritoNacionalUnico> k) {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrSenadoresDistritoNacionalUnicoRepository.deleteAll();
	}

	@Override
	public List<VwPrSenadoresDistritoNacionalUnico> findAll() {
		return this.vwPrSenadoresDistritoNacionalUnicoRepository.findAll();
	}

	@Override
	public List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeografica(FiltroParticipanteSenadoresUnicosDto filtros) {
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrSenadoresDistritoNacionalUnico> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,1,null,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,1,null,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,1,null,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,1,null,null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,1,null,null);
		} else {
			log.info("Servicio parlamento andino - listarParticipantesUbicacionGeografica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipanteSenadoresUnicosDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteSenadoresUnicosDto filtros) {
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroParticipanteSenadoresUnicosDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrSenadoresDistritoNacionalUnico> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else {
			log.info("Servicio parlamento andino - listarParticipantesUbicacionGeograficaNombre, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorCandidato(FiltroParticipanteSenadoresUnicosDto filtros) {
		List<ParticipanteCandidatoSenadoresUnicosDto> lstCandidatos = vwPrSenadoresDistritoNacionalUnicoRepositoryCustom.buscarCandidatosGraficoAll(filtros);
		return  lstCandidatos;
	}

	@Override
	public List<ParticipanteCandidatoSenadoresUnicosDto> listarParticipantesPorOrganizacionPoliticaNombreCandidato(FiltroParticipanteSenadoresUnicosDto filtros) {
		List<ParticipanteCandidatoSenadoresUnicosDto> lstCandidatosConsulta = vwPrSenadoresDistritoNacionalUnicoRepositoryCustom.buscarCandidatosAgrupacionPoliticaNombre(filtros);

		lstCandidatosConsulta.forEach(item -> item.setIdCandidato(null));

		List<ParticipanteCandidatoSenadoresUnicosDto> listaCompleta = new ArrayList<>(List.copyOf(lstCandidatosConsulta));

		if(filtros.getNombreCandidato() != null && !filtros.getNombreCandidato().isEmpty()) {
			listaCompleta = listaCompleta.stream().filter(data -> data.getNombreCandidato()
							.matches(CADENA_INICIO + filtros.getNombreCandidato().trim() + CADENA_FIN))
					.toList();
		}

		return listaCompleta.stream().sorted(
				Comparator.comparingInt(ParticipanteCandidatoSenadoresUnicosDto::getTotalVotosValidos)
						.reversed()
						.thenComparingInt(ParticipanteCandidatoSenadoresUnicosDto::getLista)).toList();
	}

	@Override
	public List<OrganizacionPoliticaSenadoresDto> listarOrganizacionPolitica() {
		List<VwPrSenadoresDistritoNacionalUnico> lista= vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(14, "eleccion");
		List<OrganizacionPoliticaSenadoresDto> data = lista.stream().flatMap((org) -> org.getDetalle().stream())
				.filter(det -> det.getGrafico().compareTo(1) == 0)
				.map(detalle ->
						OrganizacionPoliticaSenadoresDto.builder()
								.codigoAgrupacionPolitica(Integer.valueOf(detalle.getCodigo()))
								.nombreAgrupacionPolitica(detalle.getDescripcion())
								.build()
				).toList();
		return data;
	}


	private List<ParticipanteSenadoresUnicosDto> construirRespuesta(List<VwPrSenadoresDistritoNacionalUnico> registros, Integer grafico, String nombreAgrupacionPolitica, Integer idEleccion){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = registros.get(0).getDetalle().stream()
				.sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion)).toList();

		if(grafico != null) {
			detalle = detalle.stream().filter(data -> data.getGrafico().compareTo(grafico) == 0).toList();
		}

		if(nombreAgrupacionPolitica != null && !nombreAgrupacionPolitica.isEmpty()) {
			detalle = detalle.stream()
					.filter(data -> data.getGrafico().compareTo(1) == 0)
					.filter(data ->
							data.getDescripcion().matches(CADENA_INICIO + nombreAgrupacionPolitica + CADENA_FIN)
					).toList();
		}

		List<ActaAgrupacion> listaTotalAgrupacionPolitica;
		List<ParticipanteSenadoresUnicosDto> elementos = null;
		if(idEleccion == null || idEleccion == 0) {
			listaTotalAgrupacionPolitica = null;
			return  detalle.stream().map(VwPrSenadoresDistritoNacionalUnicoServiceImpl::mapperCampos).toList();
		}else {
			listaTotalAgrupacionPolitica = maeCandidatoRepositoryCustom.findByEleccionGroupedByAgrupacionPolitica(idEleccion.longValue());
			elementos =   detalle.stream().map(VwPrSenadoresDistritoNacionalUnicoServiceImpl::mapperCampos)
					.toList();
			elementos.forEach(elemento -> {
				Optional<ActaAgrupacion> regis = listaTotalAgrupacionPolitica.stream().filter(ele -> ele.getId().getId().compareTo(elemento.getCodigoAgrupacionPolitica().longValue()) == 0).findFirst();
				regis.ifPresent(dato -> elemento.setTotalCandidatos(dato.getTotal()));

			});

			return elementos;
		}

	}

	private static ParticipanteSenadoresUnicosDto mapperCampos(VwPrEleccionBaseDetalle registro){

		return ParticipanteSenadoresUnicosDto.builder()
				.totalVotosValidos(registro.getVotos())
				.porcentajeVotosEmitidos(registro.getPorcentajeVotosEmitidos())
				.porcentajeVotosValidos(registro.getPorcentajeVotosValidos())
				.codigoAgrupacionPolitica(registro.getAgrupacionPolitica())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.totalCandidatos(registro.getCandidato().size())
				.build();
	}


	@Override
	public List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesUbicacionGeograficaReporte(FiltroEleccionSenadoresUnicosReporteDto filtros) {
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrSenadoresDistritoNacionalUnico> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuestaReporte(registros,true,true);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuestaReporte(registros,true,true);
		} else {
			log.info("Servicio senadores unicos reporte - listarParticipantesUbicacionGeografica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesCandidatoReporte(FiltroEleccionSenadoresUnicosReporteDto filtros) {

		return vwPrSenadoresDistritoNacionalUnicoRepositoryCustom.buscarCandidatosGraficoAllReporte(filtros);
	}

	@Override
	public List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesOrganizacionReporte(FiltroEleccionSenadoresUnicosReporteDto filtros) {
		return  vwPrSenadoresDistritoNacionalUnicoRepositoryCustom.buscarCandidatosAgrupacionPoliticaNombreReporte(filtros);

	}

	@Override
	public List<ParticipanteSenadoresUnicosReporteDto> listarParticipantesResumengGeneralReporte(FiltroEleccionSenadoresUnicosReporteDto filtros) {
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroEleccionSenadoresUnicosReporteDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrSenadoresDistritoNacionalUnico> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuestaReporte(registros,false,false);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuestaReporte(registros,false,false);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuestaReporte(registros,false,false);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuestaReporte(registros,false,false);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuestaReporte(registros,false,false);
		} else {
			log.info("Servicio senadores unicos reporte - listarParticipantesResumenGeneral, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	private List<ParticipanteSenadoresUnicosReporteDto> construirRespuestaReporte(List<VwPrSenadoresDistritoNacionalUnico> registros, boolean conVotoNulo, boolean conCandidato){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados para reporte no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = new ArrayList<>(registros.get(0).getDetalle().stream()
				.filter(data -> data.getVotos() != null)
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

	private static ParticipanteSenadoresUnicosReporteDto mapperCamposReporte(VwPrEleccionBaseDetalle registro, boolean mostrarCandidato){

		int totalCandidatos = 0;
		if(mostrarCandidato && !registro.getCandidato().isEmpty()) {
			totalCandidatos = registro.getCandidato().size();
		}

		return ParticipanteSenadoresUnicosReporteDto.builder()
				.totalVotos(registro.getVotos() != null ? registro.getVotos() : 0)
				.orgPolitica(registro.getDescripcion())
				.codOrgPolitica(registro.getCodigo())
				.totalCandidatos(totalCandidatos)
				.votosEmitidos(registro.getPorcentajeVotosEmitidos())
				.votosValidos(registro.getPorcentajeVotosValidos())
				.build();
	}

}
