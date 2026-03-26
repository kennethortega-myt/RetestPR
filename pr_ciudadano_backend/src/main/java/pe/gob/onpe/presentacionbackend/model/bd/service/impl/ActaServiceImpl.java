package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeDistritoElectoral;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeEleccion;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActaDetalle;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActaDetalleCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActaLineaTiempo;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.ActaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.ActaRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.MaeCandidatoRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.TabArchivoRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.ActaService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.*;
import pe.gob.onpe.presentacionbackend.utils.ConstantesComunes;
import pe.gob.onpe.presentacionbackend.utils.enums.TipoEleccionEnum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ActaServiceImpl implements ActaService {

	private final ActaRepository actaRepository;
	private final ActaRepositoryCustom actaRepositoryCustom;
	private final MaeCandidatoRepository maeCandidatoRepository;
	private final MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom;
	private final TabArchivoRepository tabArchivoRepository;
	
	public ActaServiceImpl(ActaRepository actaRepository, ActaRepositoryCustom actaRepositoryCustom,
			MaeCandidatoRepository maeCandidatoRepository, TabArchivoRepository tabArchivoRepository
			,MaeCandidatoRepositoryCustom maeCandidatoRepositoryCustom) {
		super();
		this.actaRepository = actaRepository;
		this.actaRepositoryCustom = actaRepositoryCustom;
		this.maeCandidatoRepository = maeCandidatoRepository;
		this.maeCandidatoRepositoryCustom = maeCandidatoRepositoryCustom;
		this.tabArchivoRepository = tabArchivoRepository;
	}

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
		if(oVwPrActa.isEmpty()) {
			return null;
		}
		VwPrActa vwPrActa = oVwPrActa.get();
		List<ActaResponseDetalleDto> detallesActualizados = new ArrayList<>();
		if(esEleccionPresidencial(vwPrActa.getIdEleccion())) {
			List<MaeCandidato> lstCandidatos = this.maeCandidatoRepository.findByEleccion(new MaeEleccion(vwPrActa.getIdEleccion()));
			manejarDetallesParaPresidencial(vwPrActa, lstCandidatos, detallesActualizados);
		} else if(esEleccionParlamentoAndinoOSenadores33(vwPrActa.getIdEleccion())){
			List<ActaAgrupacion> lstActaCandidato = this.maeCandidatoRepositoryCustom.findByEleccionGroupedByAgrupacionPolitica(vwPrActa.getIdEleccion());
			manejarDetallesParaOtros(vwPrActa, lstActaCandidato, detallesActualizados);
		} else if(esEleccionDiputadosOSenadores27(vwPrActa.getIdEleccion())) {
			List<ActaAgrupacion> lstActaCandidato = this.maeCandidatoRepositoryCustom.findByEleccionAndDistritoElectoralGroupedByAgrupacionPolitica(vwPrActa.getIdEleccion(), vwPrActa.getIdDistritoElectoral());
			manejarDetallesParaOtros(vwPrActa, lstActaCandidato, detallesActualizados);
		} else if(esEleccionRevocatoriaDistrital(vwPrActa.getIdEleccion())) {
			manejarDetallesParaRevocatoria(vwPrActa, detallesActualizados);
		}
		//la siguiente linea de código se cae si estado es null, por ello siempre debe transmitirse desde SCE el campo Estado (es importante el estado que llegue de SCE)
		if(vwPrActa.getIdEleccion().equals(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo())) {
			Collections.sort(detallesActualizados, (a,b) -> Objects.equals(a.getCargo(), b.getCargo()) ? Integer.compare(a.getEstado(), b.getEstado()) : Integer.compare(b.getNVotos(), a.getNVotos()));
		} else {
			Collections.sort(detallesActualizados, (a,b) -> Objects.equals(a.getNVotos(), b.getNVotos()) ? Integer.compare(a.getNPosicion(), b.getNPosicion()) : Integer.compare(b.getNVotos(), a.getNVotos()));
		}

		List<VwPrActaLineaTiempo> lineaTiempoActualizado = new ArrayList<>();
		if (vwPrActa.getLineaTiempo() != null) {
			lineaTiempoActualizado = vwPrActa.getLineaTiempo().stream()
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(
							VwPrActaLineaTiempo::getFechaRegistro,
							Comparator.nullsLast(Comparator.naturalOrder())))
					.toList();
		}

		List<TabArchivo> lstTabArchivo = this.tabArchivoRepository.findByIdActa(id);
		List<TabArchivoDownDto> lstTabArchivoDownDto = new ArrayList<>();

		if(!lstTabArchivo.isEmpty()) {
			List<TabArchivo> lstTabArchivo1 = lstTabArchivo.stream()
					.sorted(Comparator.comparing(TabArchivo::getTipo).thenComparing(Comparator.comparing(TabArchivo::getAudFechaCreacion).reversed()))
			        .toList();
			Long resoucionTotal = lstTabArchivo.stream().filter(f->f.getTipo().equals(ConstantesComunes.TIPO_ARCHIVO_RESOLUCION)).count();
			AtomicInteger counter = new AtomicInteger(resoucionTotal.intValue());
			lstTabArchivoDownDto = lstTabArchivo1.stream()
				.map(data -> {						
					TabArchivoDownDto resp = new TabArchivoDownDto();
					resp.setId(data.getId());
					resp.setTipo(data.getTipo());
					resp.setNombre(data.getCNombreOriginal());
					String descripcion = ConstantesComunes.obtenerDescripcion(data.getTipo(), counter);
					resp.setDescripcion(descripcion);
					resp.setDAudFechaCreacion(data.getAudFechaCreacion());
					return resp;
				})
				.toList();
		}
	        
		return ActaResponseDto.builder()
				.id(vwPrActa.getId())
				.idEleccion(vwPrActa.getIdEleccion())
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
				.archivos(lstTabArchivoDownDto)
				.codigoSolucionTecnologica(vwPrActa.getNSolucionTecnologica())
				.descripcionSolucionTecnologica(vwPrActa.getCDescripcionSolucionTecnologica())
				.build();
	}

	private boolean esEleccionPresidencial(Long idEleccion) {
		return idEleccion.equals(TipoEleccionEnum.PRESIDENCIAL.getCodigo());
	}

	private boolean esEleccionParlamentoAndinoOSenadores33(Long idEleccion) {
		return idEleccion.equals(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo()) ||
				idEleccion.equals(TipoEleccionEnum.SENADORES_33.getCodigo());
	}

	private boolean esEleccionDiputadosOSenadores27(Long idEleccion) {
		return idEleccion.equals(TipoEleccionEnum.DIPUTADOS.getCodigo()) ||
				idEleccion.equals(TipoEleccionEnum.SENADORES_27.getCodigo());
	}

	private boolean esEleccionRevocatoriaDistrital(Long idEleccion) {
		return idEleccion.equals(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo());
	}

	private void manejarDetallesParaPresidencial(VwPrActa vwPrActa, List<MaeCandidato> lstCandidatos, List<ActaResponseDetalleDto> detallesActualizados) {
	    for (VwPrActaDetalle detalle : vwPrActa.getDetalle()) {
	        List<ActaResponseDetalleCandidatoDto> candidatosAgrupacion = lstCandidatos.stream()
	                .filter(candidato -> candidato.getCargo() == 10 && candidato.getAgrupacionPolitica().getId().equals(Long.parseLong(detalle.getAdAgrupacionPolitica().toString())))
	                .map(candidato -> ActaResponseDetalleCandidatoDto.builder()
	                        .cDocumentoIdentidad(candidato.getDocumentoIdentidad())
	                        .apellidoPaterno(candidato.getApellidoPaterno())
	                        .apellidoMaterno(candidato.getApellidoMaterno())
	                        .nombres(candidato.getNombres())
	                        .build())
	                .toList();
	        ActaResponseDetalleDto d = ActaResponseDetalleDto.builder()
	                .nAgrupacionPolitica(detalle.getAdAgrupacionPolitica())
	                .nPosicion(detalle.getAdPosicion())
	                .cCodigo(detalle.getAdCodigo())
	                .descripcion(detalle.getAdDescripcion())
	                .nVotos(detalle.getAdVotos()!=null?detalle.getAdVotos():0)
	                .estado(detalle.getAdEstado())
	                .nPorcentajeVotosValidos(detalle.getAdPorcentajeVotosValidos())
	                .nPorcentajeVotosEmitidos(detalle.getAdPorcentajeVotosEmitidos())
	                .grafico(detalle.getAdGrafico())
	                .candidato(candidatosAgrupacion)
	                .build();
	        detallesActualizados.add(d);
	    }
	}

	private void manejarDetallesParaOtros(VwPrActa vwPrActa, List<ActaAgrupacion> lstActaCandidato, List<ActaResponseDetalleDto> detallesActualizados) {
	    for (VwPrActaDetalle detalle : vwPrActa.getDetalle()) {
	        int n = lstActaCandidato.stream()
	                .filter(candidato -> candidato.getId().getId().equals(Long.parseLong(detalle.getAdAgrupacionPolitica().toString())))
	                .map(ActaAgrupacion::getTotal)
	                .findFirst()
	                .orElse(0);
	        ActaResponseDetalleDto d = ActaResponseDetalleDto.builder()
	                .nAgrupacionPolitica(detalle.getAdAgrupacionPolitica())
	                .nPosicion(detalle.getAdPosicion())
	                .cCodigo(detalle.getAdCodigo())
	                .descripcion(detalle.getAdDescripcion())
	                .estado(detalle.getAdEstado())
	                .nVotos(detalle.getAdVotos()!=null?detalle.getAdVotos():0)
	                .nPorcentajeVotosValidos(detalle.getAdPorcentajeVotosValidos())
	                .nPorcentajeVotosEmitidos(detalle.getAdPorcentajeVotosEmitidos())
	                .grafico(detalle.getAdGrafico())
	                .totalCandidatos(n)
	                .build();
	        detallesActualizados.add(d);
	    }
	}
	
	private void manejarDetallesParaRevocatoria(VwPrActa vwPrActa, List<ActaResponseDetalleDto> detallesActualizados) {
		List<MaeCandidato> lstCandidatos = this.maeCandidatoRepository.findByEleccion(new MaeEleccion(TipoEleccionEnum.REVOCATORIA_DISTRITAL.getCodigo()));
	    for (VwPrActaDetalle detalle : vwPrActa.getDetalle()) {
	        List<ActaResponseDetalleCandidatoDto> candidatosAgrupacion = detalle.getCandidato().stream()
	                //.filter(candidato -> candidato.getCargo() == 10 && candidato.getAgrupacionPolitica().getId().equals(Long.parseLong(detalle.getAdAgrupacionPolitica().toString())))
	                .map(candidato -> ActaResponseDetalleCandidatoDto.builder()
	                        .votos(candidato.getVotos())
	                        .posicionOpcionVoto(candidato.getPosicionOpcionVoto())
	                        .codigoOpcionVoto(candidato.getCodigoOpcionVoto())
	                        .descripcionOpcionVoto(candidato.getDescripcionOpcionVoto())
	                        .porcentajeVotosValidos(candidato.getPorcentajeVotosValidos())
	                        .porcentajeVotosEmitidos(candidato.getPorcentajeVotosEmitidos())
	                        .build())
	                .toList();
	        
	        Integer sexo = lstCandidatos.stream()
            		.filter(f->f.getDocumentoIdentidad().equals(detalle.getAdCodigo()))
            		.map(MaeCandidato::getSexo)
            		.findFirst()
            		.orElse(0);
	        
	        ActaResponseDetalleDto d = ActaResponseDetalleDto.builder()
	                .nAgrupacionPolitica(detalle.getAdAgrupacionPolitica())
	                .nPosicion(detalle.getAdPosicion())
	                .cCodigo(detalle.getAdCodigo())
	                .descripcion(detalle.getAdDescripcion())
	                .nVotos(detalle.getAdVotos()!=null?detalle.getAdVotos():0)
	                .estado(detalle.getAdEstado())
	                .nPorcentajeVotosValidos(detalle.getAdPorcentajeVotosValidos())
	                .nPorcentajeVotosEmitidos(detalle.getAdPorcentajeVotosEmitidos())
	                .grafico(detalle.getAdGrafico())
	                .cargo(detalle.getAdCargo())
	                .sexo(sexo)
	                .candidato(candidatosAgrupacion)
	                .build();
	        detallesActualizados.add(d);
	    }
	}

	@Override
	public Optional<ActaPaginaResponseDto> obtenerActas(ActaRequestDto filtro, int pagina, int tamanio) {
		Predicate<ActaRequestDto> tieneIdAmbitoGeografico = data -> data.getIdAmbitoGeografico() != null && data.getIdAmbitoGeografico() != 0;
		Predicate<ActaRequestDto> tieneIdUbigeo = data -> data.getIdUbigeo() != null && data.getIdUbigeo() != 0;
		Predicate<ActaRequestDto> tieneCodigoLocalVotacion = data -> data.getCodigoLocalVotacion() != null && data.getCodigoLocalVotacion() != 0;
		
		Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Order.asc("idMesa"), Sort.Order.asc("orden")));
		Page<VwPrActa> lstVwPrActa = null;
		List<VwPrActa> lstVwPrActaTotal = null;
		if (tieneIdAmbitoGeografico.and(tieneIdUbigeo.negate()).and(tieneCodigoLocalVotacion.negate()).test(filtro)) {
			lstVwPrActaTotal = actaRepository.findByIdAmbitoGeografico(filtro.getIdAmbitoGeografico());
			lstVwPrActa = actaRepository.findByIdAmbitoGeografico(filtro.getIdAmbitoGeografico(), pageable);
			return construirRespuesta(lstVwPrActa,lstVwPrActaTotal);
		} else if (tieneIdAmbitoGeografico.and(tieneIdUbigeo).and(tieneCodigoLocalVotacion.negate()).test(filtro)) {
			lstVwPrActaTotal = actaRepository.findByIdAmbitoGeograficoAndIdUbigeo(filtro.getIdAmbitoGeografico(), filtro.getIdUbigeo());
						lstVwPrActa = actaRepository.findByIdAmbitoGeograficoAndIdUbigeo(filtro.getIdAmbitoGeografico(), filtro.getIdUbigeo(), pageable);
						return construirRespuesta(lstVwPrActa,lstVwPrActaTotal);
		} else if (tieneIdAmbitoGeografico.and(tieneIdUbigeo).and(tieneCodigoLocalVotacion).test(filtro)) {
			lstVwPrActaTotal = actaRepository.findByIdAmbitoGeograficoAndIdUbigeoAndIdLocalVotacion(filtro.getIdAmbitoGeografico(), filtro.getIdUbigeo(), filtro.getCodigoLocalVotacion());
			lstVwPrActa = actaRepository.findByIdAmbitoGeograficoAndIdUbigeoAndIdLocalVotacion(filtro.getIdAmbitoGeografico(), filtro.getIdUbigeo(), filtro.getCodigoLocalVotacion(), pageable);
			return construirRespuesta(lstVwPrActa,lstVwPrActaTotal);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<ActaPaginaResponseDto> obtenerActasObservadas(ActaRequestDto filtro, int pagina, int tamanio) {
		Pageable paginacion = PageRequest.of(pagina, tamanio, Sort.by(Sort.Order.asc("idMesa"), Sort.Order.asc("orden")));
		Page<VwPrActa> lstVwPrActa = null;
		lstVwPrActa = actaRepositoryCustom.obtenerPorEleccionAmbitoGeograficoUbigeoCodigoLocalVotacion(filtro, paginacion);
		return construirRespuesta(lstVwPrActa,null);
	}

	@Override
	public List<ActasResponseDto> obtenerActaMesa(ActaMesaRequestDto filtro) {
		return this.actaRepositoryCustom.obtenerActaMesa(filtro);
	}

	@Override
	public List<ActaLocalesResponseDto> obtenerActaLocales(ActaLocalesRequestDto filtro) {
		List<VwPrActa> lstActa = this.actaRepository.findByIdEleccionAndIdUbigeo(filtro.getIdEleccion(), filtro.getIdUbigeo());
		Map<String, ActaLocalesResponseDto> actaMap = lstActa != null ? lstActa.stream()
	            .collect(Collectors.toMap(
	                    VwPrActa::getCodigoLocalVotacion,
	                    data -> ActaLocalesResponseDto.builder()
	                            .codigoLocalVotacion(data.getCodigoLocalVotacion())
	                            .nombreLocalVotacion(data.getNombreLocalVotacion())
	                            .build(),
	                    (existing, replacement) -> existing
	            )) : Collections.emptyMap();
	    return actaMap.values().stream().toList();
	}

	private Optional<ActaPaginaResponseDto> construirRespuesta(Page<VwPrActa> registros, List<VwPrActa> registros1){

		List<ActasResponseDto> actas = registros.getContent().stream()
				.sorted(Comparator
						.comparing(VwPrActa::getIdMesa)
						.thenComparing(VwPrActa::getOrden, Comparator.nullsLast(Integer::compareTo)))
				.map(ActaServiceImpl::mapperActas)
				.toList();
				
		long conteoPendientes=0L;
		long conteoContabilizadas=0L;
		long conteoObservadas=0L;
		if(registros1!=null) {
			conteoPendientes =  registros1.stream().filter(acta -> acta.getDescripcionEstadoActa()!=null && acta.getCodigoEstadoActa().equals("P")).count();
			conteoContabilizadas = registros1.stream().filter(acta -> acta.getDescripcionEstadoActa()!=null && acta.getCodigoEstadoActa().equals("C")).count();
			conteoObservadas = registros1.stream().filter(acta -> acta.getDescripcionEstadoActa()!=null && acta.getCodigoEstadoActa().equals("E")).count();
		}
		return Optional.of(ActaPaginaResponseDto.builder()
				.content(actas)
				.paginaActual(registros.getNumber())
				.totalPaginas(registros.getTotalPages())
				.totalRegistros(registros.getTotalElements())
				.contabilizada(conteoContabilizadas)
				.observada(conteoObservadas)
				.pendiente(conteoPendientes)
				.build());
	}
	
	
	private static ActasResponseDto mapperActas(VwPrActa registro) {

		return ActasResponseDto.builder()
				.id(registro.getId())
				.idMesa(registro.getIdMesa())
				.codigoMesa(registro.getCodigoMesa())
				.numeroCopia(registro.getNumeroCopia())
				.idAmbitoGeografico(registro.getIdAmbitoGeografico())
				.idUbigeo(registro.getIdUbigeo())
				.idEleccion(registro.getIdEleccion())
				.estadoActa(registro.getEstadoActa())
				.estadoComputo(registro.getEstadoComputo())
				.codigoEstadoActa(registro.getCodigoEstadoActa())
				.descripcionEstadoActa(registro.getDescripcionEstadoActa())
				.build();
	}

	@Override
	public List<ActaAgrupacionCandidatoRes> listarCandidatos(Long id, Long idAgru) {
		Optional<VwPrActa> oVwPrActa = this.actaRepository.findById(id);
		if(oVwPrActa.isPresent()) {
			List<MaeCandidato> lstCandidato = new ArrayList<>();
			if(oVwPrActa.get().getIdEleccion().equals(TipoEleccionEnum.DIPUTADOS.getCodigo()) || oVwPrActa.get().getIdEleccion().equals(TipoEleccionEnum.SENADORES_27.getCodigo())) {
				lstCandidato = this.maeCandidatoRepository.findByEleccionAndDistritoElectoralAndAgrupacionPolitica(new MaeEleccion(oVwPrActa.get().getIdEleccion()),
					new MaeDistritoElectoral(oVwPrActa.get().getIdDistritoElectoral()),
					new MaeAgrupacionPolitica(idAgru));
			}
			if(oVwPrActa.get().getIdEleccion().equals(TipoEleccionEnum.PARLAMENTO_ANDINO.getCodigo()) || oVwPrActa.get().getIdEleccion().equals(TipoEleccionEnum.SENADORES_33.getCodigo())) {
				lstCandidato = this.maeCandidatoRepository.findByEleccionAndAgrupacionPolitica(new MaeEleccion(oVwPrActa.get().getIdEleccion()),
					new MaeAgrupacionPolitica(idAgru));
			}
			
			Collections.sort(lstCandidato, Comparator.comparingInt(MaeCandidato::getLista));
		
			return lstCandidato.stream()
					.map(candidato -> {								
						Optional<VwPrActaDetalleCandidato> candi =  oVwPrActa.get().getDetalle().stream()
								.filter(f -> f.getAdAgrupacionPolitica().equals(idAgru.intValue()))
								.flatMap(c -> c.getCandidato().stream())
								.filter(f1 -> f1.getId().equals(candidato.getId()))
								.findFirst();
						
						return ActaAgrupacionCandidatoRes.builder()
								.nombreCompleto(candidato.getNombres()+" "+candidato.getApellidoPaterno()+" "+candidato.getApellidoMaterno())
								.lista(candidato.getLista())
								.votos(candi.isPresent()?candi.get().getVotos():0)
								.build();
					})
					.toList();
		}
		return Collections.emptyList();
	}
	
}
