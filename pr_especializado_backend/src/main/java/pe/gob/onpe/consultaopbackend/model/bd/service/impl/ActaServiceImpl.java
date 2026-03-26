package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import pe.gob.onpe.consultaopbackend.model.bd.documents.*;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ActaService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.*;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ActasResponseReporteDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaCsvDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaDto;
import pe.gob.onpe.consultaopbackend.utils.enums.AmbitoGeograficoEnum;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;
import pe.gob.onpe.consultaopbackend.utils.PrUtils;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionMayusculaEnum;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActaServiceImpl implements ActaService {

	private final ActaRepository actaRepository;
	private final ActaRepositoryCustom actaRepositoryCustom;
	private final MaeCandidatoRepository maeCandidatoRepository;
	private final MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom;
	private final TabArchivoRepository tabArchivoRepository;
	private final MaeFechaRepository maeFechaRepository;
	private final VwPrTotalCandidatosPorAgrupacionPoliticaRepository vwTotalCandidatos;

	@Override
	public void save(VwPrActa k) {
		this.actaRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrActa> k) {
		this.actaRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.actaRepository.deleteAll();
	}

	@Override
	public List<VwPrActa> findAll() {
		return this.actaRepository.findAll();
	}

	int cont = 1;

	@Override
	public ActaResponseDto obtenerActaPorId(Long id) {
		Optional<VwPrActa> oVwPrActa = this.actaRepository.findById(id);
		if (oVwPrActa.isEmpty()) {
			return null;
		}
		VwPrActa vwPrActa = oVwPrActa.get();
		List<ActaResponseDetalleDto> detallesActualizados = new ArrayList<>();
		if (vwPrActa.getIdEleccion().equals(TipoEleccionEnum.PRESIDENCIAL.getCodigo())) {
			List<MaeCandidato> lstCandidatos = this.maeCandidatoRepository
					.findByEleccion(new MaeEleccion(vwPrActa.getIdEleccion()));
			manejarDetallesParaPresidencial(vwPrActa, lstCandidatos, detallesActualizados);
		} else if (vwPrActa.getIdEleccion().equals(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo())
				|| vwPrActa.getIdEleccion().equals(TipoEleccionEnum.SENADORES_33.getCodigo())) {
			List<ActaAgrupacion> lstActaCandidato = this.maeCandidatoRepositoryCustom
					.findByEleccionGroupedByAgrupacionPolitica(vwPrActa.getIdEleccion());
			manejarDetallesParaOtros(vwPrActa, lstActaCandidato, detallesActualizados);
		} else if (vwPrActa.getIdEleccion().equals(TipoEleccionEnum.DIPUTADOS.getCodigo())
				|| vwPrActa.getIdEleccion().equals(TipoEleccionEnum.SENADORES_27.getCodigo())) {
			List<ActaAgrupacion> lstActaCandidato = this.maeCandidatoRepositoryCustom
					.findByEleccionAndDistritoElectoralGroupedByAgrupacionPolitica(vwPrActa.getIdEleccion(),
							vwPrActa.getIdDistritoElectoral());
			manejarDetallesParaOtros(vwPrActa, lstActaCandidato, detallesActualizados);
		}

		List<VwPrActaLineaTiempo> lineaTiempoActualizado = vwPrActa.getLineaTiempo().stream()
				.sorted(Comparator.comparing(VwPrActaLineaTiempo::getFechaRegistro)).toList();

		List<TabArchivo> lstTabArchivo = this.tabArchivoRepository.findByIdActa(id);
		List<TabArchivoDownDto> lstTabArchivoDownDto = new ArrayList<>();

		if (!lstTabArchivo.isEmpty()) {
			List<TabArchivo> lstTabArchivo1 = lstTabArchivo.stream()
					.sorted(Comparator.comparing(TabArchivo::getTipo)
							.thenComparing(Comparator.comparing(TabArchivo::getDAudFechaCreacion).reversed()))
					.toList();
			Long resoucionTotal = lstTabArchivo.stream()
					.filter(f -> f.getTipo().equals(ConstantesComunes.TIPO_ARCHIVO_RESOLUCION)).count();
			AtomicInteger counter = new AtomicInteger(resoucionTotal.intValue());
			lstTabArchivoDownDto = lstTabArchivo1.stream()
					.map(data -> {
						TabArchivoDownDto resp = new TabArchivoDownDto();
						resp.setId(data.getId());
						resp.setTipo(data.getTipo());
						resp.setNombre(data.getCNombreOriginal());
						String descripcion = ConstantesComunes.obtenerDescripcion(data.getTipo(), counter);
						resp.setDescripcion(descripcion);
						resp.setDAudFechaCreacion(data.getDAudFechaCreacion());
						return resp;
					})
					.toList();
		}

		return ActaResponseDto.builder()
				.id(vwPrActa.getId())
				.idEleccion(vwPrActa.getIdEleccion())
				.idAmbitoGeografico(vwPrActa.getIdAmbitoGeografico())
				.descripcionMesa(vwPrActa.getCodigoMesa())
				.codigoMesa(vwPrActa.getCodigoMesa())
				.totalElectoresHabiles(vwPrActa.getTotalElectoresHabiles())
				.totalVotosEmitidos(vwPrActa.getTotalVotosEmitidos())
				.totalVotosValidos(vwPrActa.getTotalVotosValidos())
				.porcentajeParticipacionCiudadana(vwPrActa.getPorcentajeParticipacionCiudadana())
				.ubigeoNivel01(vwPrActa.getUbigeoNombreNivel01())
				.ubigeoNivel02(vwPrActa.getUbigeoNombreNivel02())
				.ubigeoNivel03(vwPrActa.getUbigeoNombreNivel03())
				.centroPoblado(vwPrActa.getCentroPoblado())
				.nombreLocalVotacion(vwPrActa.getNombreLocalVotacion())
				.totalAsistentes(vwPrActa.getTotalAsistentes())
				.codigoEstadoActa(vwPrActa.getCodigoEstadoActa())
				.estadoComputo(vwPrActa.getEstadoComputo())
				.estadoActa(vwPrActa.getEstadoActa())
				.descripcionEstadoActa(vwPrActa.getDescripcionEstadoActa())
				.estadoActaResolucion(vwPrActa.getEstadoActaResolucion())
				.estadoDescripcionActaResolucion(vwPrActa.getEstadoDescripcionActaResolucion())
				.descripcionSubEstadoActa(vwPrActa.getDescripcionSubEstadoActa())
				.detalle(detallesActualizados)
				.lineaTiempo(lineaTiempoActualizado)
				.codigoSolucionTecnologica(vwPrActa.getNSolucionTecnologica())
				.descripcionSolucionTecnologica(vwPrActa.getCDescripcionSolucionTecnologica())
				.archivos(lstTabArchivoDownDto)
				.build();
	}

	private void manejarDetallesParaPresidencial(VwPrActa vwPrActa, List<MaeCandidato> lstCandidatos,
			List<ActaResponseDetalleDto> detallesActualizados) {
		for (VwPrActaDetalle detalle : vwPrActa.getDetalle()) {
			List<ActaResponseDetalleCandidatoDto> candidatosAgrupacion = lstCandidatos.stream()
					.filter(candidato -> candidato.getCargo() == 10 && candidato.getAgrupacionPolitica().getId()
							.equals(Long.parseLong(detalle.getNAgrupacionPolitica().toString())))
					.map(candidato -> ActaResponseDetalleCandidatoDto.builder()
							.cDocumentoIdentidad(candidato.getDocumentoIdentidad())
							.apellidoPaterno(candidato.getApellidoPaterno())
							.apellidoMaterno(candidato.getApellidoMaterno())
							.nombres(candidato.getNombres())
							.build())
					.toList();
			ActaResponseDetalleDto d = ActaResponseDetalleDto.builder()
					.nAgrupacionPolitica(detalle.getNAgrupacionPolitica())
					.nPosicion(detalle.getNPosicion())
					.cCodigo(detalle.getCCodigo())
					.descripcion(detalle.getDescripcion())
					.nVotos(detalle.getNVotos() != null ? detalle.getNVotos() : 0)
					.estado(detalle.getEstado())
					.nPorcentajeVotosValidos(detalle.getNPorcentajeVotosValidos())
					.nPorcentajeVotosEmitidos(detalle.getNPorcentajeVotosEmitidos())
					.grafico(detalle.getGrafico())
					.candidato(candidatosAgrupacion)
					.build();
			detallesActualizados.add(d);
		}
	}

	private void manejarDetallesParaOtros(VwPrActa vwPrActa, List<ActaAgrupacion> lstActaCandidato,
			List<ActaResponseDetalleDto> detallesActualizados) {
		for (VwPrActaDetalle detalle : vwPrActa.getDetalle()) {
			int n = lstActaCandidato.stream()
					.filter(candidato -> candidato.getId().getId()
							.equals(Long.parseLong(detalle.getNAgrupacionPolitica().toString())))
					.map(ActaAgrupacion::getTotal)
					.findFirst()
					.orElse(0);

			ActaResponseDetalleDto d = ActaResponseDetalleDto.builder()
					.nAgrupacionPolitica(detalle.getNAgrupacionPolitica())
					.nPosicion(detalle.getNPosicion())
					.cCodigo(detalle.getCCodigo())
					.descripcion(detalle.getDescripcion())
					.estado(detalle.getEstado())
					.nVotos(detalle.getNVotos() != null ? detalle.getNVotos() : 0)
					.nPorcentajeVotosValidos(detalle.getNPorcentajeVotosValidos())
					.nPorcentajeVotosEmitidos(detalle.getNPorcentajeVotosEmitidos())
					.grafico(detalle.getGrafico())
					.totalCandidatos(n)
					.build();
			detallesActualizados.add(d);
		}
	}

	@Override
	public Optional<ActaPaginaResponseDto> obtenerActas(ActaReqDto filtro, String codigoOp, int pagina, int tamanio) {
		log.info("Ingresa a obtenerActas");
		Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Order.asc("idMesa")));
		filtro.setIdAmbitoGeografico(filtro.getIdAmbitoGeografico() == null ? 0 : filtro.getIdAmbitoGeografico());
		filtro.setCodigoLocalVotacion(filtro.getCodigoLocalVotacion() == null ? 0 : filtro.getCodigoLocalVotacion());

		/* ALL FOR ELETORAL DISTRICT */
		if (Objects.equals(Long.valueOf(filtro.getIdEleccion()), TipoEleccionEnum.DIPUTADOS.getCodigo())
				|| Objects.equals(Long.valueOf(filtro.getIdEleccion()), TipoEleccionEnum.SENADORES_27.getCodigo())) {
			if (Objects.equals(filtro.getIdDistritoElectoral(), 30)) {
				filtro.setIdDistritoElectoral(null);
			}
		}

		Page<VwPrActa> lstVwPrActa = actaRepositoryCustom
				.findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(
						filtro, pageable);

		List<Long> idsActa = lstVwPrActa.stream()
				.filter(acta -> "C".equals(acta.getCodigoEstadoActa()) || "E".equals(acta.getCodigoEstadoActa()))
				.map(VwPrActa::getId)
				.toList();
		List<TabArchivo> lstTabArchivo = null;
		if (!lstVwPrActa.isEmpty()) {
			lstTabArchivo = this.tabArchivoRepository.findAllByIdActa(idsActa);
		}
		return construirRespuestaActa(lstVwPrActa, lstTabArchivo, codigoOp);
	}

	@Override
	public Optional<ActaPaginaResponseDto> obtenerActasObservadas(ActaRequestDto filtro, int pagina, int tamanio) {
		Pageable paginacion = PageRequest.of(pagina, tamanio,
				Sort.by(Sort.Order.asc("idMesa"), Sort.Order.asc("idEleccion")));
		Page<VwPrActa> lstVwPrActa = null;
		lstVwPrActa = actaRepositoryCustom.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacion(filtro,
				paginacion);
		return construirRespuestaObs(lstVwPrActa);
	}

	@Override
	public List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro) {
		return this.actaRepositoryCustom.obtenerActaMesa(filtro);
	}

	private Optional<ActaPaginaResponseDto> construirRespuestaObs(Page<VwPrActa> registros) {
		List<ActasResponseDto> actas = registros.getContent().stream()
				.map(ActaServiceImpl::mapperActasObs)
				.toList();

		return Optional.of(ActaPaginaResponseDto.builder()
				.content(actas)
				.paginaActual(registros.getNumber())
				.totalPaginas(registros.getTotalPages())
				.totalRegistros(registros.getTotalElements())
				.build());
	}

	private static ActasResponseDto mapperActasObs(VwPrActa registro) {

		return ActasResponseDto.builder()
				.id(registro.getId())
				.idMesa(registro.getIdMesa())
				.codigoMesa(registro.getCodigoMesa())
				.idAmbitoGeografico(registro.getIdAmbitoGeografico())
				.idEleccion(registro.getIdEleccion())
				.estadoActa(registro.getEstadoActa())
				.estadoComputo(registro.getEstadoComputo())
				.codigoEstadoActa(registro.getCodigoEstadoActa())
				.descripcionEstadoActa(registro.getDescripcionEstadoActa())
				.build();
	}

	@Override
	public List<ActaAgrupacionCandidatoRes> listarCandidatos(Long id, Long idAgru) {

		log.info("listarCandidatos id : {}, idAgru: {}", id, idAgru);

		Optional<VwPrActa> oVwPrActa = actaRepository.findById(id);
		if (oVwPrActa.isEmpty()) {
			return Collections.emptyList();
		}

		VwPrActa acta = oVwPrActa.get();
		Long idEleccion = acta.getIdEleccion();
		List<MaeCandidato> lstCandidato = new ArrayList<>();

		if (TipoEleccionEnum.DIPUTADOS.getCodigo().equals(idEleccion)
				|| TipoEleccionEnum.SENADORES_27.getCodigo().equals(idEleccion)) {

			lstCandidato = maeCandidatoRepository
					.findByEleccionAndDistritoElectoralAndAgrupacionPolitica(
							new MaeEleccion(idEleccion),
							new MaeDistritoElectoral(acta.getIdDistritoElectoral()),
							new MaeAgrupacionPolitica(idAgru));
		}

		if (TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo().equals(idEleccion)
				|| TipoEleccionEnum.SENADORES_33.getCodigo().equals(idEleccion)) {

			lstCandidato = maeCandidatoRepository
					.findByEleccionAndAgrupacionPolitica(
							new MaeEleccion(idEleccion),
							new MaeAgrupacionPolitica(idAgru));
		}

		lstCandidato.sort(Comparator.comparingInt(MaeCandidato::getLista));

		return lstCandidato.stream()
				.<ActaAgrupacionCandidatoRes>map(candidato -> {
					Optional<VwPrActaDetalleCandidato> candi = acta.getDetalle().stream()
							.filter(f -> f.getNAgrupacionPolitica().equals(idAgru.intValue()))
							.flatMap(c -> c.getCandidato().stream())
							.filter(f1 -> f1.getId().equals(candidato.getId()))
							.findFirst();

					return ActaAgrupacionCandidatoRes.builder()
							.documentoIdentidad(candidato.getDocumentoIdentidad())
							.nombreCompleto(
									candidato.getNombres() + " "
											+ candidato.getApellidoPaterno() + " "
											+ candidato.getApellidoMaterno())
							.lista(candidato.getLista())
							.votos(candi.map(VwPrActaDetalleCandidato::getVotos).orElse(0L))
							.build();
				})
				.toList();
	}

	@Override
	public ReporteRespuestaDto obtenerActasReporte(ActaRequestDto filtro) {
		filtro.setIdAmbitoGeografico(filtro.getIdAmbitoGeografico() == null ? 0 : filtro.getIdAmbitoGeografico());
		filtro.setCodigoLocalVotacion(filtro.getCodigoLocalVotacion() == null ? 0 : filtro.getCodigoLocalVotacion());
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom
				.findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(
						filtro);

		List<Long> idsActa = lstVwPrActa.stream()
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.map(VwPrActa::getId).toList();
		List<TabArchivo> lstTabArchivo = null;
		if (!lstVwPrActa.isEmpty()) {
			lstTabArchivo = this.tabArchivoRepository.findAllByIdActa(idsActa);
		}
		return construirRespuestaActaReporte(lstVwPrActa, lstTabArchivo, filtro.getCodigoOp());
	}

	@Override
	public ReporteRespuestaCsvDto obtenerActasReporteSinArchivo(ActaRequestDto filtro) {
		filtro.setIdAmbitoGeografico(filtro.getIdAmbitoGeografico() == null ? 0 : filtro.getIdAmbitoGeografico());
		filtro.setCodigoLocalVotacion(filtro.getCodigoLocalVotacion() == null ? 0 : filtro.getCodigoLocalVotacion());
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom
				.findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(
						filtro);
		lstVwPrActa.sort(Comparator.comparing(VwPrActa::getIdMesa));
		if (filtro.getIdEleccion() == 10) {
			return construirRespuestaActaReportePresidencialCsv(lstVwPrActa);
		} else {

			VwPrTotalCandidatosPorAgrupacionPolitica totalCandidatos;
			if (null != filtro.getIdDistritoElectoral() && filtro.getIdDistritoElectoral() != 0) {
				totalCandidatos = vwTotalCandidatos
						.findByEleccionAndDistritoElectoral(filtro.getIdEleccion(), filtro.getIdDistritoElectoral())
						.stream()
						.findAny()
						.orElse(null);
			} else {
				totalCandidatos = vwTotalCandidatos.findByEleccion(filtro.getIdEleccion())
						.stream()
						.findAny()
						.orElse(null);
			}

			return construirRespuestaActaReportePorCandidatosCsv(lstVwPrActa, totalCandidatos);
		}
	}

	@Override
	public ReporteRespuestaCsvDto obtenerActasReporteObservadasCsv(ActaRequestDto filtro) {
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom
				.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(filtro);
		lstVwPrActa.sort(Comparator.comparing(VwPrActa::getIdMesa));
		if (filtro.getIdEleccion() == 10) {
			return construirRespuestaActaReportePresidencialCsv(lstVwPrActa);
		} else {
			VwPrTotalCandidatosPorAgrupacionPolitica totalCandidatos;
			if (null != filtro.getIdDistritoElectoral() && filtro.getIdDistritoElectoral() != 0) {
				totalCandidatos = vwTotalCandidatos
						.findByEleccionAndDistritoElectoral(filtro.getIdEleccion(), filtro.getIdDistritoElectoral())
						.stream()
						.findAny()
						.orElse(null);
			} else {
				totalCandidatos = vwTotalCandidatos.findByEleccion(filtro.getIdEleccion())
						.stream()
						.findAny()
						.orElse(null);
			}

			return construirRespuestaActaReportePorCandidatosCsv(lstVwPrActa, totalCandidatos);
		}
	}

	@Override
	public ReporteRespuestaDto obtenerActasReporteObservadas(ActaRequestDto filtro) {
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom
				.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(filtro);
		List<Long> idsActa = lstVwPrActa.stream().map(VwPrActa::getId).toList();
		List<TabArchivo> lstTabArchivo = null;
		if (!lstVwPrActa.isEmpty()) {
			lstTabArchivo = this.tabArchivoRepository.findAllByIdActa(idsActa);
		}
		return construirRespuestaActaReporte(lstVwPrActa, lstTabArchivo, filtro.getCodigoOp());
	}

	private Optional<ActaPaginaResponseDto> construirRespuestaActa(Page<VwPrActa> listaActasPaginado,
			List<TabArchivo> listArchivos, String codigoOp) {

		List<ActasResponseDto> actas = listaActasPaginado.stream()
				.map(acta -> {
					int nvotos = acta.getDetalle().stream()
							.filter(d -> d.getCCodigo().equals(codigoOp))
							.findFirst()
							.map(VwPrActaDetalle::getNVotos)
							.orElse(0);
					return ActaServiceImpl.mapperActasActas(acta, nvotos);
				})
				.toList();

		if (null != listArchivos) {
			actas.forEach(data -> {

				List<TabArchivo> listaArchivosDescendente = listArchivos.stream()
						.filter(ele -> ele.getIdActa().compareTo(data.getId()) == 0)
						.sorted(Comparator.comparing(TabArchivo::getTipo)
								.thenComparing(Comparator.comparing(TabArchivo::getDAudFechaCreacion).reversed()))
						.toList();
				long resoucionTotal = listArchivos.stream()
						.filter(ele -> ele.getIdActa().compareTo(data.getId()) == 0)
						.filter(f -> f.getTipo().equals(ConstantesComunes.TIPO_ARCHIVO_RESOLUCION)).count();
				AtomicLong counterLong = new AtomicLong(resoucionTotal);
				AtomicInteger counterInt = new AtomicInteger((int) counterLong.get());
				List<TabArchivoDownDto> lstTabArchivoDownDto = listaArchivosDescendente.stream()
						.map(dataArchivo -> {
							TabArchivoDownDto resp = new TabArchivoDownDto();
							resp.setId(dataArchivo.getId());
							resp.setTipo(dataArchivo.getTipo());
							resp.setNombre(dataArchivo.getCNombreOriginal());
							String descripcion = ConstantesComunes.obtenerDescripcion(dataArchivo.getTipo(),
									counterInt);
							resp.setDescripcion(descripcion);
							resp.setDAudFechaCreacion(dataArchivo.getDAudFechaCreacion());
							return resp;
						}).toList();

				data.setArchivos(lstTabArchivoDownDto);
			});
		}

		return Optional.of(ActaPaginaResponseDto.builder()
				.content(actas)
				.paginaActual(listaActasPaginado.getNumber())
				.totalRegistros(listaActasPaginado.getTotalElements())
				.totalPaginas(listaActasPaginado.getTotalPages())
				.build());
	}

	private static ActasResponseDto mapperActasActas(VwPrActa registro, int nvotos) {

		return ActasResponseDto.builder()
				.id(registro.getId())
				.idMesa(registro.getIdMesa())
				.codigoMesa(registro.getCodigoMesa())
				.idAmbitoGeografico(registro.getIdAmbitoGeografico())
				.descripcionAmbitoGeografico(AmbitoGeograficoEnum.obtenerDescripcion(registro.getIdAmbitoGeografico()))
				.idEleccion(registro.getIdEleccion())
				.descripcionEleccion(TipoEleccionEnum.obtenerDescripcion(registro.getIdEleccion()))
				.ubigeoNivel01(registro.getUbigeoNombreNivel01())
				.ubigeoNivel02(registro.getUbigeoNombreNivel02())
				.ubigeoNivel03(registro.getUbigeoNombreNivel03())
				.nombreLocalVotacion(registro.getNombreLocalVotacion())
				.totalElectoresHabiles(registro.getTotalElectoresHabiles())
				.estadoActa(registro.getEstadoActa())
				.estadoComputo(registro.getEstadoComputo())
				.codigoEstadoActa(registro.getCodigoEstadoActa())
				.descripcionEstadoActa(registro.getDescripcionEstadoActa())
				.totalVotosEmitidos(nvotos)
				.build();
	}

	private static ActasResponseReporteDto mapperActasActasReporte(VwPrActa registro, List<Integer> nvotos,
			int votosOrgPolitica, VwPrActaDetalle actaDetalle) {

		return ActasResponseReporteDto.builder()
				.id(registro.getId())
				.idMesa(registro.getIdMesa())
				.codigoMesa(registro.getCodigoMesa())
				.idAmbitoGeografico(registro.getIdAmbitoGeografico())
				.descripcionAmbitoGeografico(AmbitoGeograficoEnum.obtenerDescripcion(registro.getIdAmbitoGeografico()))
				.idEleccion(registro.getIdEleccion())
				.descripcionEleccion(TipoEleccionMayusculaEnum.obtenerDescripcion(registro.getIdEleccion()))
				.ubigeoNivel01(registro.getUbigeoNombreNivel01())
				.ubigeoNivel02(registro.getUbigeoNombreNivel02())
				.ubigeoNivel03(registro.getUbigeoNombreNivel03())
				.centroPoblado(registro.getCentroPoblado())
				.nombreLocalVotacion(registro.getNombreLocalVotacion())
				.nombreOrganizacionPolitica(
						Objects.nonNull(actaDetalle) ? actaDetalle.getDescripcion() : StringUtils.EMPTY)
				.posicionOrganizacionPolitica(Objects.nonNull(actaDetalle) ? actaDetalle.getNPosicion() : 0)
				.totalElectoresHabiles(registro.getTotalElectoresHabiles())
				.descripcionEstadoActa(registro.getDescripcionEstadoActa())
				.listaVotosOrgPolitica(nvotos)
				.totalVotosEmitidos(votosOrgPolitica)
				.build();
	}

	private ReporteRespuestaDto construirRespuestaActaReporte(List<VwPrActa> listaActas, List<TabArchivo> listArchivos,
			Integer codigoOp) {

		List<String> listaOrgPoliticaNombre = listaActas.stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.toList().stream().findAny()
				.map(acta -> acta.getDetalle().stream()
						.filter(Objects::nonNull)
						.filter(data -> data.getEstado() == 1)
						.filter(data -> data.getNPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getNPosicion))
						.map(VwPrActaDetalle::getDescripcion).toList())
				.orElse(Collections.emptyList());

		List<String> listaOrgPoliticaNombreFinal = new ArrayList<>(listaOrgPoliticaNombre);
		if (!listaOrgPoliticaNombre.isEmpty()) {
			listaOrgPoliticaNombreFinal.add("VOTOS EMITIDOS");// SE AGREGA TOTAL VOTOS EMITIDOS EN CABECERA
		}

		List<ActasResponseReporteDto> listaActasTotal = listaActas.stream()
				.filter(Objects::nonNull)
				.map(acta -> {
					List<Integer> votosTodos = Collections.emptyList();
					int votosOrgPolitica = 0;
					if (null != acta.getCodigoEstadoActa() && acta.getCodigoEstadoActa().equals("C")) {
						votosTodos = acta.getDetalle().stream()
								.filter(Objects::nonNull)
								.filter(data -> data.getEstado() == 1)
								.filter(data -> data.getNPosicion() < 82)
								.sorted(Comparator.comparing(VwPrActaDetalle::getNPosicion))
								.map(data -> Optional.ofNullable(data.getNVotos()).orElse(0))
								.toList();
						if (codigoOp != null) {
							votosOrgPolitica = acta.getDetalle().stream()
									.filter(Objects::nonNull)
									.filter(data -> data.getEstado() == 1)
									.filter(data -> data.getNPosicion() < 82)
									.filter(d -> d.getNAgrupacionPolitica().equals(codigoOp))
									.findFirst()
									.map(VwPrActaDetalle::getNVotos)
									.orElse(0);
						}
					}
					List<Integer> votosTodosFinal = new ArrayList<>(votosTodos);
					if (!votosTodos.isEmpty()) {
						votosTodosFinal.add(acta.getTotalVotosEmitidos());// SE AGREGA TOTAL VOTOS EMITIDOS EN DETALLE
					}

					return ActaServiceImpl.mapperActasActasReporte(acta, votosTodosFinal, votosOrgPolitica, null);
				}).toList();

		if (null != listArchivos) {
			listaActasTotal.stream()
					.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
					.filter(data -> data.getCodigoEstadoActa().equals("C"))
					.forEach(data -> {
						List<TabArchivo> archivoActa = listArchivos.stream()
								.filter(ele -> ele.getIdActa().compareTo(data.getId()) == 0
										&& (ele.getTipo() == 1 || ele.getTipo() == 0))
								.toList();
						List<TabArchivo> archivoResoluciones = listArchivos.stream()
								.filter(ele -> ele.getIdActa().compareTo(data.getId()) == 0 && ele.getTipo() == 2)
								.toList();

						List<String> nombreActas = archivoActa.stream().map(TabArchivo::getId).toList();
						List<String> nombreResoluciones = archivoResoluciones.stream().map(TabArchivo::getId).toList();
						data.setArchivoResolucionId(nombreResoluciones);
						data.setArchivoActaId(nombreActas);
					});
		}

		return ReporteRespuestaDto.builder()
				.registrosReporte(listaActasTotal)
				.listaOrgPolitica(listaOrgPoliticaNombreFinal)
				.build();
	}

	private ReporteRespuestaCsvDto construirRespuestaActaReportePresidencialCsv(List<VwPrActa> listaActas) {

		List<String> listaOrgPoliticaNombre = listaActas.stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.toList().stream()
				.filter(data -> data.getDetalle() != null)
				.findAny()
				.map(acta -> acta.getDetalle().stream()
						.filter(Objects::nonNull)
						.filter(data -> data.getEstado() == 1)
						.filter(data -> data.getNPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getNPosicion))
						.map(VwPrActaDetalle::getDescripcion).toList())
				.orElse(Collections.emptyList());

		List<String> listaOrgPoliticaNombreFinal = new ArrayList<>(listaOrgPoliticaNombre);

		List<ActasResponseReporteDto> listaActasTotal = listaActas.stream()
				.filter(Objects::nonNull)
				.map(acta -> {
					List<Integer> votosTodos = Collections.emptyList();
					int votosOrgPolitica = 0;
					if (null != acta.getCodigoEstadoActa() && acta.getCodigoEstadoActa().equals("C")) {
						votosTodos = acta.getDetalle().stream()
								.filter(Objects::nonNull)
								.filter(data -> data.getEstado() == 1)
								.filter(data -> data.getNPosicion() < 82)
								.sorted(Comparator.comparing(VwPrActaDetalle::getNPosicion))
								.map(data -> Optional.ofNullable(data.getNVotos()).orElse(0))
								.toList();
					}

					return ActaServiceImpl.mapperActasActasReporte(acta, votosTodos, votosOrgPolitica, null);
				}).toList();

		return ReporteRespuestaCsvDto.builder()
				.registrosReporte(listaActasTotal)
				.listaOrgPolitica(listaOrgPoliticaNombreFinal)
				.build();
	}

	private ReporteRespuestaCsvDto construirRespuestaActaReportePorCandidatosCsv(
			List<VwPrActa> listaActas,
			VwPrTotalCandidatosPorAgrupacionPolitica totalCandidatos) {

		int totalCandidatosReporte = totalCandidatos.getTotalCandidatos() != null
				? Integer.parseInt(totalCandidatos.getTotalCandidatos())
				: 0;

		// 1. Generar lista de cabeceras dinámicas
		List<String> listaCandidatos = (totalCandidatosReporte > 0)
				? IntStream.rangeClosed(1, totalCandidatosReporte)
						.mapToObj(i -> "Candidato " + i)
						.toList()
				: List.of();

		// 2. Construir registros de salida
		List<ActasResponseReporteDto> listaActasTotal = listaActas.stream()
				.filter(Objects::nonNull)
				.filter(acta -> "C".equals(acta.getCodigoEstadoActa())) // solo actas computadas
				.flatMap(acta -> Optional.ofNullable(acta.getDetalle())
						.orElse(Collections.emptyList())
						.stream()
						.filter(detalle -> detalle.getEstado() == 1)
						.filter(detalle -> detalle.getNPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getNPosicion))
						.map(detalle -> {
							// arreglo fijo con ceros
							int[] votosPorCandidato = new int[totalCandidatosReporte];
							Arrays.fill(votosPorCandidato, 0);

							// completar los votos en su posición correspondiente
							Optional.ofNullable(detalle.getCandidato())
									.orElse(Collections.emptyList())
									.forEach(c -> {
										if (c.getLista() != null
												&& c.getLista() > 0
												&& c.getLista() <= totalCandidatosReporte) {
											votosPorCandidato[c.getLista() - 1] = Optional.ofNullable(c.getVotos())
													.orElse(0L).intValue();
										}
									});

							// total votos organización207

							int totalVotosOrg = Optional.ofNullable(detalle.getNVotos()).orElse(0);

							// mapper recibe lista inmutable
							return ActaServiceImpl.mapperActasActasReporte(
									acta,
									Arrays.stream(votosPorCandidato).boxed().toList(),
									totalVotosOrg,
									detalle // nombre organización política
							);
						}))
				.toList();

		// 3. Devolver DTO
		return ReporteRespuestaCsvDto.builder()
				.registrosReporte(listaActasTotal)
				.listaOrgPolitica(listaCandidatos) // columnas dinámicas
				.build();
	}

	@Override
	public Optional<ResumenActasObservadasResDto> obtenerResumenActasObservadas(ResumenActasObservadasReqDto filtro) {
		Long idEleccion = filtro.getIdEleccion().longValue();
		Integer idAmbitoGeografico = filtro.getIdAmbitoGeografico();
		Integer ubigeoNivel01 = filtro.getUbigeoNivel01() != null
				? PrUtils.parseStringToInteger(filtro.getUbigeoNivel01())
				: 0;
		Integer ubigeoNivel02 = filtro.getUbigeoNivel02() != null
				? PrUtils.parseStringToInteger(filtro.getUbigeoNivel02())
				: 0;
		Long idUbigeo = filtro.getIdUbigeo() != null ? PrUtils.parseStringToLong(filtro.getIdUbigeo()) : 0;
		Long codigoLocalVotacion = filtro.getCodigoLocalVotacion();
		Integer idDistritoElectoral = filtro.getIdDistritoElectoral() == null ? 0 : filtro.getIdDistritoElectoral();

		Map<String, Object> lstVwPrActa = actaRepositoryCustom
				.findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(
						idEleccion,
						idAmbitoGeografico,
						ubigeoNivel01,
						ubigeoNivel02,
						idUbigeo,
						codigoLocalVotacion,
						idDistritoElectoral);

		MaeFecha fechaProceso = maeFechaRepository.findById(1)
				.orElse(MaeFecha.builder().fechaProceso(new Date()).build());

		return Optional.of(ResumenActasObservadasResDto.builder()
				.idEleccion(idEleccion)
				.contabilizadas(Integer.parseInt(lstVwPrActa.get("trueTotal").toString()))
				.enviadasJee(Integer.parseInt(lstVwPrActa.get("falseTotal").toString()))
				.totalActas(Integer.parseInt(lstVwPrActa.get("trueTotal").toString())
						+ Integer.parseInt(lstVwPrActa.get("falseTotal").toString()))
				.fechaActualizacion(fechaProceso.getFechaProceso())
				.build());
	}

}
