package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.*;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrParlamentoAndinoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrParlamentoRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrParlamentoAndinoService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaAgrupacion;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.*;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static pe.gob.onpe.presentacionbackend.utils.ConstantesComunes.OP_ESTADO_NOPARTICIPA;

@RequiredArgsConstructor
@Service
@Slf4j
public class VwPrParlamentoAndinoServiceImpl implements VwPrParlamentoAndinoService {

	private static final String CADENA_INICIO = "(?i).*";
	private static final String CADENA_FIN = ".*";

	private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
	private final VwPrParlamentoRepositoryCustom vwPrParlamentoRepositoryCustom;	
	private final MaeCandidatoRepository maeCandidatoRepository;	
	private final MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom;

	@Override
	public void save(VwPrParlamentoAndino k) {
		this.vwPrParlamentoAndinoRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrParlamentoAndino> k) {
		this.vwPrParlamentoAndinoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrParlamentoAndinoRepository.deleteAll();
	}

	@Override
	public List<VwPrParlamentoAndino> findAll() {
		return this.vwPrParlamentoAndinoRepository.findAll();
	}

	@Override
	public List<ParticipanteParlamentoAndinoDto> listarParticipantesUbicacionGeografica(
			FiltroParticipanteParlamentoAndinoDto filtros) {


		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrParlamentoAndino> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,1,null, null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return  construirRespuesta(registros,1,null, null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return  construirRespuesta(registros,1,null, null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return  construirRespuesta(registros,1,null, null);
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,1,null, null);
		} else {
			log.info("Servicio parlamento andino - listarParticipantesUbicacionGeografica, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public Optional<ParlamentoCandidatosPaginaResponseDto> listarParticipantesUbicacionGeograficaPaginado(FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto, int pagina, int tamanio) {

		Page<ParticipanteCandidatoParlamentoAndinoDto> lstCandidatos = vwPrParlamentoRepositoryCustom.buscarCandidatosGraficoPaginado(filtroParticipanteDto, pagina, tamanio);

		List<Integer> idsCandidatos = lstCandidatos.get().filter(Objects::nonNull).map(ParticipanteCandidatoParlamentoAndinoDto::getIdCandidato).toList();

		List<MaeCandidato> listCandidatos;
		if (!idsCandidatos.isEmpty())
			listCandidatos = maeCandidatoRepository.findByIds(idsCandidatos);
		else {
			listCandidatos = new ArrayList<>();
		}
		lstCandidatos.get().forEach(candidatoSinDatos -> {
			Optional<MaeCandidato> maeCandidato = listCandidatos.stream().filter(c -> c.getId().equals(candidatoSinDatos.getIdCandidato())).findFirst();
			maeCandidato.ifPresent(newCandadato -> {
				candidatoSinDatos.setNombreCandidato(newCandadato.getNombres() + newCandadato.getApellidoPaterno() + newCandadato.getApellidoMaterno() );
				candidatoSinDatos.setDniCandidato(newCandadato.getDocumentoIdentidad());
			});
		});
		return construirRespuestaPaginado(lstCandidatos);

	}

	private Optional<ParlamentoCandidatosPaginaResponseDto> construirRespuestaPaginado(Page<ParticipanteCandidatoParlamentoAndinoDto> registros){
		List<ParticipanteCandidatoParlamentoAndinoDto> candidatos = registros.getContent().stream().toList();
		return Optional.of(ParlamentoCandidatosPaginaResponseDto.builder()
				.content(candidatos)
				.paginaActual(registros.getNumber())
				.totalPaginas(registros.getTotalPages())
				.totalRegistros(registros.getTotalElements())
				.build());
	}

	@Override
	public List<ParticipanteParlamentoAndinoDto> listarParticipantesUbicacionGeograficaNombre(
			FiltroParticipanteParlamentoAndinoDto filtros) {
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneEleccionAndFiltro = data ->
				data.getTipoFiltro()!=null && !data.getTipoFiltro().isEmpty() && data.getIdEleccion()!=null
						&& data.getIdEleccion() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneAmbito = data -> data.getIdAmbitoGeografico()!=null && data.getIdAmbitoGeografico() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo1 = data -> data.getUbigeoNivel1()!= null && data.getUbigeoNivel1() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo2 = data -> data.getUbigeoNivel2()!= null && data.getUbigeoNivel2() != 0;
		Predicate<FiltroParticipanteParlamentoAndinoDto> tieneUbigeo3 = data -> data.getUbigeoNivel3()!= null && data.getUbigeoNivel3() != 0;

		List<VwPrParlamentoAndino> registros = null;
		if(tieneEleccionAndFiltro.and(tieneAmbito.negate()).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(filtros.getIdEleccion(), filtros.getTipoFiltro());
			return  construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1.negate()).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeografico(filtros.getIdEleccion(), filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2.negate()).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3.negate()).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else if(tieneEleccionAndFiltro.and(tieneAmbito).and(tieneUbigeo1).and(tieneUbigeo2).and(tieneUbigeo3).test(filtros)){
			registros = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltroAndAmbitoGeograficoAndUbigeoNivel01AndUbigeoNivel02AndUbigeoNivel03(filtros.getIdEleccion(),filtros.getTipoFiltro(),filtros.getIdAmbitoGeografico(),filtros.getUbigeoNivel1(),filtros.getUbigeoNivel2(),filtros.getUbigeoNivel3());
			return construirRespuesta(registros,null,filtros.getNombrePartidoPolitico(),filtros.getIdEleccion());
		} else {
			log.info("Servicio parlamento andino - listarParticipantesUbicacionGeograficaNombre, se retorna vacio - request no mapeado: " + filtros);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ParticipanteCandidatoParlamentoAndinoDto> listarParticipantesPorCandidato(
			FiltroParticipanteParlamentoAndinoDto filtros) {

		return vwPrParlamentoRepositoryCustom.buscarCandidatosGraficoAll(filtros);
	}

	@Override
	public List<ParticipanteCandidatoParlamentoAndinoDto> listarParticipantesPorOrganizacionPoliticaNombreCandidato(FiltroParticipanteParlamentoAndinoDto filtros) {

		List<ParticipanteCandidatoParlamentoAndinoDto> lstCandidatosConsulta = vwPrParlamentoRepositoryCustom.buscarCandidatosAgrupacionPoliticaNombre(filtros);

		lstCandidatosConsulta.forEach(item -> item.setIdCandidato(null));

		List<ParticipanteCandidatoParlamentoAndinoDto> listaCompleta = new ArrayList<>(List.copyOf(lstCandidatosConsulta));

		if(filtros.getNombreCandidato() != null && !filtros.getNombreCandidato().isEmpty()) {
			listaCompleta = listaCompleta.stream().filter(data -> data.getNombreCandidato()
					.matches(CADENA_INICIO + filtros.getNombreCandidato().trim() + CADENA_FIN))
					.toList();
		}

		return listaCompleta.stream().sorted(
				Comparator.comparingInt(ParticipanteCandidatoParlamentoAndinoDto::getTotalVotosValidos)
				.reversed()
				.thenComparingInt(ParticipanteCandidatoParlamentoAndinoDto::getLista)).toList();
	}

	@Override
	public List<OrganizacionPoliticaDto> listarOrganizacionPolitica() {
		List<VwPrParlamentoAndino> lista= vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(Integer.parseInt(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo().toString()), "eleccion");
		return lista.stream().flatMap(org -> org.getDetalle().stream())
				.filter(det -> det.getGrafico().compareTo(1) == 0)
				.filter(det -> det.getEstado().compareTo(1) == 0)
				.map(detalle ->
						OrganizacionPoliticaDto.builder()
								.codigoAgrupacionPolitica(Integer.valueOf(detalle.getCodigo()))
								.nombreAgrupacionPolitica(detalle.getDescripcion())
								.build()
				)
				.sorted(Comparator.comparing(OrganizacionPoliticaDto::getNombreAgrupacionPolitica))
				.toList();
	}
	private List<ParticipanteParlamentoAndinoDto> construirRespuesta(List<VwPrParlamentoAndino> registros, Integer grafico, String nombreAgrupacionPolitica, Integer idEleccion){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info("Los filtros indicados no corresponden a un registro en la bd PR, size: {} ", registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = registros.get(0).getDetalle().stream()
				.filter(data -> Objects.nonNull(data.getEstado()))
				.filter(data -> !data.getEstado().equals(OP_ESTADO_NOPARTICIPA))
				.filter(data -> data.getEstado().compareTo(1) == 0)
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
		List<ParticipanteParlamentoAndinoDto> elementos = null;
		if(idEleccion == null || idEleccion == 0) {
            return  detalle.stream().map(VwPrParlamentoAndinoServiceImpl::mapperCampos).toList();
		}else {
			listaTotalAgrupacionPolitica = maeCandidatoRepositoryCustom.findByEleccionGroupedByAgrupacionPolitica(idEleccion.longValue());
			elementos =   detalle.stream().map(VwPrParlamentoAndinoServiceImpl::mapperCampos)
					.toList();
			elementos.forEach(elemento -> {
				Optional<ActaAgrupacion> regis = listaTotalAgrupacionPolitica.stream().filter(ele -> ele.getId().getId().compareTo(Long.parseLong(elemento.getCodigoAgrupacionPolitica())) == 0).findFirst();
				regis.ifPresent(dato -> elemento.setTotalCandidatos(dato.getTotal()));

			});

			return elementos;
		}
	}

	private static ParticipanteParlamentoAndinoDto mapperCampos(VwPrEleccionBaseDetalle registro){

		return ParticipanteParlamentoAndinoDto.builder()
				.totalVotosValidos(registro.getVotos())
				.porcentajeVotosEmitidos(registro.getPorcentajeVotosEmitidos())
				.porcentajeVotosValidos(registro.getPorcentajeVotosValidos())
				.codigoAgrupacionPolitica(registro.getAgrupacionPolitica().toString())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.totalCandidatos(registro.getCandidato().size())
				.build();
	}

}
