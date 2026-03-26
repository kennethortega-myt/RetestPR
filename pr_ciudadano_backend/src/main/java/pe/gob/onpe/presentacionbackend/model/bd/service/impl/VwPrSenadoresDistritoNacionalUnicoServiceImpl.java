package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.*;
import java.util.function.Predicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.*;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.*;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrSenadoresDistritoNacionalUnicoService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaAgrupacion;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.*;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import org.springframework.stereotype.Service;

import static pe.gob.onpe.presentacionbackend.utils.ConstantesComunes.OP_ESTADO_NOPARTICIPA;

@RequiredArgsConstructor
@Service
@Slf4j
public class VwPrSenadoresDistritoNacionalUnicoServiceImpl implements VwPrSenadoresDistritoNacionalUnicoService {


	private static final String CADENA_INICIO = "(?i).*";
	private static final String CADENA_FIN = ".*";

	private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
	private final VwPrSenadoresDistritoNacionalUnicoRepositoryCustom vwPrSenadoresDistritoNacionalUnicoRepositoryCustom;
	private final MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom;


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
		return vwPrSenadoresDistritoNacionalUnicoRepositoryCustom.buscarCandidatosGraficoAll(filtros);
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
		List<VwPrSenadoresDistritoNacionalUnico> lista= vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(Integer.parseInt(TipoEleccionEnum.SENADORES_33.getCodigo().toString()), "eleccion");
		return lista.stream().flatMap(org -> org.getDetalle().stream())
				.filter(det -> det.getGrafico().compareTo(1) == 0)
				.filter(det -> det.getEstado().compareTo(1) == 0)
				.map(detalle ->
						OrganizacionPoliticaSenadoresDto.builder()
								.codigoAgrupacionPolitica(Integer.valueOf(detalle.getCodigo()))
								.nombreAgrupacionPolitica(detalle.getDescripcion())
								.build()
				)
				.sorted(Comparator.comparing(OrganizacionPoliticaSenadoresDto::getNombreAgrupacionPolitica))
				.toList();
	}


	private List<ParticipanteSenadoresUnicosDto> construirRespuesta(List<VwPrSenadoresDistritoNacionalUnico> registros, Integer grafico, String nombreAgrupacionPolitica, Integer idEleccion){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = registros.get(0).getDetalle().stream()
				.filter(data -> data.getEstado() != null && !data.getEstado().equals(OP_ESTADO_NOPARTICIPA))
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
			return  detalle.stream().map(VwPrSenadoresDistritoNacionalUnicoServiceImpl::mapperCampos).toList();
		}else {
			listaTotalAgrupacionPolitica = maeCandidatoRepositoryCustom.findByEleccionGroupedByAgrupacionPolitica(idEleccion.longValue());
			elementos =   detalle.stream().map(VwPrSenadoresDistritoNacionalUnicoServiceImpl::mapperCampos)
					.toList();
			elementos.forEach(elemento -> {
				Optional<ActaAgrupacion> regis = listaTotalAgrupacionPolitica.stream().filter(ele -> ele.getId().getId().compareTo(Long.parseLong(elemento.getCodigoAgrupacionPolitica())) == 0).findFirst();
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
				.codigoAgrupacionPolitica(registro.getAgrupacionPolitica().toString())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.totalCandidatos(registro.getCandidato().size())
				.build();
	}

}
