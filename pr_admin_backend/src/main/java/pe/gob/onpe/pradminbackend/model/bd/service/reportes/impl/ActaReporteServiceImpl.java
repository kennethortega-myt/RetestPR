package pe.gob.onpe.pradminbackend.model.bd.service.reportes.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActaDetalle;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.repository.*;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.MaeCandidatoRepositoryCustom;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ActaReporteService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.*;
import pe.gob.onpe.pradminbackend.utils.PrUtils;
import pe.gob.onpe.pradminbackend.utils.enums.AmbitoGeograficoEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionMayusculaEnum;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActaReporteServiceImpl implements ActaReporteService {

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
	public ReporteRespuestaDto obtenerActasReporte(ActaRequestDto filtro) {
		filtro.setIdAmbitoGeografico(filtro.getIdAmbitoGeografico()==null?0:filtro.getIdAmbitoGeografico());
		filtro.setCodigoLocalVotacion(filtro.getCodigoLocalVotacion()==null?0:filtro.getCodigoLocalVotacion());
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom.findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(filtro);

		List<Long> idsActa = lstVwPrActa.stream()
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.map(VwPrActa::getId).toList();
		List<TabArchivo> lstTabArchivo = null;
		if (!lstVwPrActa.isEmpty()) {
			lstTabArchivo = this.tabArchivoRepository.findAllByIdActa(idsActa);
		}
		return construirRespuestaActaReporte(lstVwPrActa,lstTabArchivo,filtro.getCodigoOp());
	}

	@Override
	public ReporteRespuestaCsvDto obtenerActasReporteSinArchivo(ActaRequestDto filtro) {
		filtro.setIdAmbitoGeografico(filtro.getIdAmbitoGeografico()==null?0:filtro.getIdAmbitoGeografico());
		filtro.setCodigoLocalVotacion(filtro.getCodigoLocalVotacion()==null?0:filtro.getCodigoLocalVotacion());
		List<VwPrActa> lstVwPrActa = actaRepositoryCustom.findByIdEleccionAndCodigoEstadoActaAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacionReporte(filtro);
		
		// Ordenar la lista por idMesa
		lstVwPrActa.sort(Comparator.comparing(VwPrActa::getIdMesa));

		if(filtro.getIdEleccion()== 10) {
			return construirRespuestaActaReportePresidencialCsv(lstVwPrActa);
		} else {

			VwPrTotalCandidatosPorAgrupacionPolitica totalCandidatos;
			if( null != filtro.getIdDistritoElectoral() && filtro.getIdDistritoElectoral() != 0) {
				totalCandidatos = vwTotalCandidatos.findByEleccionAndDistritoElectoral(filtro.getIdEleccion(),filtro.getIdDistritoElectoral())
						.stream()
						.findAny()
						.orElse(null);
			} else {
				totalCandidatos = vwTotalCandidatos.findByEleccion(filtro.getIdEleccion())
						.stream()
						.findAny()
						.orElse(null);
			}

			return construirRespuestaActaReportePorCandidatosCsv(lstVwPrActa,totalCandidatos);
		}
	}

	@Override
	public ReporteRespuestaCsvDto obtenerActasReporteObservadasCsv(ActaRequestDto filtro) {
		List<VwPrActa> lstVwPrActa =  actaRepositoryCustom.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(filtro);

		if(filtro.getIdEleccion()== 10) {
			return construirRespuestaActaReportePresidencialCsv(lstVwPrActa);
		} else {
			VwPrTotalCandidatosPorAgrupacionPolitica totalCandidatos;
			if( null != filtro.getIdDistritoElectoral() && filtro.getIdDistritoElectoral() != 0) {
				totalCandidatos = vwTotalCandidatos.findByEleccionAndDistritoElectoral(filtro.getIdEleccion(),filtro.getIdDistritoElectoral())
						.stream()
						.findAny()
						.orElse(null);
			} else {
				totalCandidatos = vwTotalCandidatos.findByEleccion(filtro.getIdEleccion())
						.stream()
						.findAny()
						.orElse(null);
			}

			return construirRespuestaActaReportePorCandidatosCsv(lstVwPrActa,totalCandidatos);
		}
	}
	@Override
	public ReporteRespuestaDto obtenerActasReporteObservadas(ActaRequestDto filtro) {
		List<VwPrActa> lstVwPrActa =  actaRepositoryCustom.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacionReporteObservados(filtro);
		List<Long> idsActa = lstVwPrActa.stream().map(VwPrActa::getId).toList();
		List<TabArchivo> lstTabArchivo = null;
		if (!lstVwPrActa.isEmpty()) {
			lstTabArchivo = this.tabArchivoRepository.findAllByIdActa(idsActa);
		}
		return construirRespuestaActaReporte(lstVwPrActa,lstTabArchivo,filtro.getCodigoOp());
	}

	private ReporteRespuestaDto construirRespuestaActaReporte(List<VwPrActa> listaActas, List<TabArchivo> listArchivos,Integer codigoOp){

		List<String> listaOrgPoliticaNombre = listaActas.stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.toList().stream().findAny()
				.map(acta -> acta.getDetalle().stream()
						.filter(Objects::nonNull)
						.filter(data -> data.getAdEstado() == 1)
						.filter(data -> data.getAdPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getAdPosicion))
						.map(VwPrActaDetalle::getAdDescripcion).toList()
				).orElse(Collections.emptyList());

		List<String> listaOrgPoliticaNombreFinal = new ArrayList<>(listaOrgPoliticaNombre);
		 if (!listaOrgPoliticaNombre.isEmpty()) {
			 listaOrgPoliticaNombreFinal.add("VOTOS EMITIDOS");//SE AGREGA TOTAL VOTOS EMITIDOS EN CABECERA
		 }


		List<ActasResponseReporteDto> listaActasTotal = listaActas.stream()
				.filter(Objects::nonNull)
				.map(acta -> {
					List<Integer> votosTodos = Collections.emptyList();
					int votosOrgPolitica = 0;
					if (null != acta.getCodigoEstadoActa() && acta.getCodigoEstadoActa().equals("C")){
						votosTodos =	acta.getDetalle().stream()
								.filter(Objects::nonNull)
								.filter(data -> data.getAdEstado() == 1)
								.filter(data -> data.getAdPosicion() < 82)
								.sorted(Comparator.comparing(VwPrActaDetalle::getAdPosicion))
								.map(data ->Optional.ofNullable(data.getAdVotos()).orElse(0))
								.toList();
						if (codigoOp != null ) {
							votosOrgPolitica =	acta.getDetalle().stream()
									.filter(Objects::nonNull)
									.filter(data -> data.getAdEstado() == 1)
									.filter(data -> data.getAdPosicion() < 82)
									.filter(d -> d.getAdAgrupacionPolitica().equals(codigoOp))
									.findFirst()
									.map(VwPrActaDetalle::getAdVotos)
									.orElse(0);
						}
					}
					List<Integer> votosTodosFinal = new ArrayList<>(votosTodos);
					if (!votosTodos.isEmpty()) {
						votosTodosFinal.add(acta.getTotalVotosEmitidos());//SE AGREGA TOTAL VOTOS EMITIDOS EN DETALLE
					}

					return ActaReporteServiceImpl.mapperActasActasReporte(acta, votosTodosFinal,votosOrgPolitica,null);
				}).toList();


		if (null != listArchivos) {
			listaActasTotal.stream()
					.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
					.filter(data -> data.getCodigoEstadoActa().equals("C"))
					.forEach(data -> {
				List<TabArchivo> archivoActa = listArchivos.stream().filter(ele -> ele.getIdActa().compareTo(data.getId())== 0 && (ele.getTipo() == 1 || ele.getTipo() == 0)).toList();
				List<TabArchivo> archivoResoluciones = listArchivos.stream().filter(ele -> ele.getIdActa().compareTo(data.getId())== 0 && ele.getTipo() == 2 ).toList();

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

	private ReporteRespuestaCsvDto construirRespuestaActaReportePresidencialCsv(List<VwPrActa> listaActas){

		List<String> listaOrgPoliticaNombre = listaActas.stream()
				.filter(Objects::nonNull)
				.filter(data -> Objects.nonNull(data.getCodigoEstadoActa()))
				.filter(data -> data.getCodigoEstadoActa().equals("C"))
				.toList().stream()
				.filter(data -> data.getDetalle() != null)
				.findAny()
				.map(acta -> acta.getDetalle().stream()
						.filter(Objects::nonNull)
						.filter(data -> data.getAdEstado() == 1)
						.filter(data -> data.getAdPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getAdPosicion))
						.map(VwPrActaDetalle::getAdDescripcion).toList()
				).orElse(Collections.emptyList());

		List<String> listaOrgPoliticaNombreFinal = new ArrayList<>(listaOrgPoliticaNombre);

		List<ActasResponseReporteDto> listaActasTotal = listaActas.stream()
				.filter(Objects::nonNull)
				.map(acta -> {
					List<Integer> votosTodos = Collections.emptyList();
					int votosOrgPolitica = 0;
					if (null != acta.getCodigoEstadoActa() && acta.getCodigoEstadoActa().equals("C")){
						votosTodos =	acta.getDetalle().stream()
								.filter(Objects::nonNull)
								.filter(data -> data.getAdEstado() == 1)
								.filter(data -> data.getAdPosicion() < 82)
								.sorted(Comparator.comparing(VwPrActaDetalle::getAdPosicion))
								.map(data ->Optional.ofNullable(data.getAdVotos()).orElse(0))
								.toList();
					}

					return ActaReporteServiceImpl.mapperActasActasReporte(acta, votosTodos,votosOrgPolitica,null);
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
						.filter(detalle -> detalle.getAdEstado() == 1)
						.filter(detalle -> detalle.getAdPosicion() < 82)
						.sorted(Comparator.comparing(VwPrActaDetalle::getAdPosicion)) //ordenamiento
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
											votosPorCandidato[c.getLista() - 1] =
													Optional.ofNullable(c.getVotos()).orElse(0L).intValue();
										}
									});

							// total votos organización
							int totalVotosOrg = Optional.ofNullable(detalle.getAdVotos()).orElse(0);

							// mapper recibe lista inmutable
							return ActaReporteServiceImpl.mapperActasActasReporte(
									acta,
									Arrays.stream(votosPorCandidato).boxed().toList(),
									totalVotosOrg,
									detalle // nombre organización política
							);
						}))
				.collect(Collectors.toList());

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
	    Integer ubigeoNivel01 = filtro.getUbigeoNivel01()!=null?PrUtils.parseStringToInteger(filtro.getUbigeoNivel01()):0;
	    Integer ubigeoNivel02 = filtro.getUbigeoNivel02()!=null?PrUtils.parseStringToInteger(filtro.getUbigeoNivel02()):0;
	    Long idUbigeo = filtro.getIdUbigeo()!=null? PrUtils.parseStringToLong(filtro.getIdUbigeo()):0;
	    Long codigoLocalVotacion = filtro.getCodigoLocalVotacion();
		Integer idDistritoElectoral = filtro.getIdDistritoElectoral() == null ?  0 : filtro.getIdDistritoElectoral();
		
	    Map<String, Object> lstVwPrActa = actaRepositoryCustom.findByIdEleccionAndIdAmbitoGeograficoAndNubigeoNivel01AndNubigeoNivel02AndIdUbigeoAndIdLocalVotacion(
				idEleccion,
				idAmbitoGeografico,
				ubigeoNivel01,
				ubigeoNivel02,
				idUbigeo,
				codigoLocalVotacion,
				idDistritoElectoral
				);
	    
		MaeFecha fechaProceso = maeFechaRepository.findById(1).orElse(MaeFecha.builder().fechaProceso(new Date()).build());
		
		return Optional.of(ResumenActasObservadasResDto.builder()
				.idEleccion(idEleccion)
				.contabilizadas(Integer.parseInt(lstVwPrActa.get("trueTotal").toString()))
				.enviadasJee(Integer.parseInt(lstVwPrActa.get("falseTotal").toString()))
				.totalActas(Integer.parseInt(lstVwPrActa.get("trueTotal").toString()) + Integer.parseInt(lstVwPrActa.get("falseTotal").toString()))
				.fechaActualizacion(fechaProceso.getFechaProceso())
				.build());
	}

    private static ActasResponseReporteDto mapperActasActasReporte(VwPrActa registro, List<Integer> nvotos, int votosOrgPolitica, VwPrActaDetalle actaDetalle) {

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
                .nombreOrganizacionPolitica(Objects.nonNull(actaDetalle) ? actaDetalle.getAdDescripcion() : StringUtils.EMPTY)
				.posicionOrganizacionPolitica(Objects.nonNull(actaDetalle) ? actaDetalle.getAdPosicion() : 0)
                .totalElectoresHabiles(registro.getTotalElectoresHabiles())
                .descripcionEstadoActa(registro.getDescripcionEstadoActa())
                .listaVotosOrgPolitica(nvotos)
                .totalVotosEmitidos(votosOrgPolitica)
                .build();
    }

}
