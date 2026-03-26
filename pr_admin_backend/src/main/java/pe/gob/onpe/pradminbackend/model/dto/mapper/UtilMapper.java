package pe.gob.onpe.pradminbackend.model.dto.mapper;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.dto.bd.importar.*;
import pe.gob.onpe.pradminbackend.utils.DateUtil;
import pe.gob.onpe.pradminbackend.utils.LocalVotacionConTildeUtil;
import pe.gob.onpe.pradminbackend.utils.UbigeoConTildeUtil;
import pe.gob.onpe.pradminbackend.utils.PresentacionConstantes;

@Slf4j
public class UtilMapper {

	private UtilMapper() {
		throw new IllegalStateException("UtilMapper class");
	}
	private static <T> List<T> convertJsonToList(String json, Class<T> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (JsonProcessingException e) {
			log.error("Error al convertir JSON a lista: ", e);
			return Collections.emptyList();
		}
	}

	public static MaeAgrupacionPolitica convertirAgrupacionPolitica(AgrupacionPoliticaDto dto) {
		return MaeAgrupacionPolitica.builder()
				.id(dto.getId())
				.codigo(dto.getCodigo())
				.descripcion(dto.getDescripcion())
				.tipoAgrupacionPolitica(dto.getTipoAgrupacionPolitica())
				.estado(dto.getEstado())
				.ubigeoMaximo(dto.getUbigeoMaximo())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}


	public static MaeProcesoElectoral convertirProcesoElectoral(ProcesoElectoralDto dto) {
		return MaeProcesoElectoral.builder()
				.id(dto.getId())
				.nombre(dto.getNombre())
				.acronimo(dto.getAcronimo())
				.tipoAmbitoElectoral(dto.getTipoAmbitoElectoral())
				.activo(dto.getActivo())
				.fechaConvocatoria(dto.getFechaConvocatoria()!=null?DateUtil.getDate(dto.getFechaConvocatoria(), PresentacionConstantes.FORMATO_FECHA):null)
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static MaeEleccion convertirEleccion(EleccionDto dto) {
		return MaeEleccion.builder()
				.id(dto.getId())
				.codigo(dto.getCodigo())
				.procesoElectoral(dto.getIdProcesoElectoral() != null?new MaeProcesoElectoral(dto.getIdProcesoElectoral()):null)
				.nombre(dto.getNombre())
				.nombreVista(dto.getNombreVista())
				.principal(dto.getPrincipal())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static MaeLocalVotacion convertirLocalVotacion(LocalVotacionDto dto) {
		return MaeLocalVotacion.builder()
				.id(dto.getId())
				.ubigeo(new MaeUbigeo(dto.getIdUbigeo()))
				.cNombre(LocalVotacionConTildeUtil.corregirNombre(dto.getNombre().trim()))
				.cDireccion(dto.getDireccion())
				.cReferencia(dto.getReferencia())
				.cCentroPoblado(dto.getCentroPoblado())
				.nCantidadMesas(dto.getCantidadMesas())
				.nCantidadElectores(dto.getCantidadElectores())
				.nEstado(dto.getEstado())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static MaeUbigeo convertirUbigeo(UbigeoDto dto) {
		return MaeUbigeo.builder()
				.id(dto.getId())
				.ubigeoPadre(dto.getIdPadre() != null ? new MaeUbigeo(dto.getIdPadre()) : null)
				.nUbigeoPadre(dto.getIdPadre())
				.nDistritoElectoral(dto.getIdDistritoElectoral())
				.cDepartamento(UbigeoConTildeUtil.corregirNombre(dto.getDepartamento()))
				.cProvincia(UbigeoConTildeUtil.corregirNombre(dto.getProvincia()))
				.cDistrito(UbigeoConTildeUtil.corregirNombre(dto.getDistrito()))
				.cNombre(UbigeoConTildeUtil.corregirNombre(dto.getNombre()))
				.cUbigeo(dto.getCodigo())
				.nTipoAmbitoGeografico(dto.getTipoAmbitoGeografico())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(
						dto.getAudFechaCreacion() != null
								? DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA)
								: null
				)
				.build();
	}

	public static DetUbigeoEleccion convertirUbigeoEleccion(UbigeoEleccionDto dto) {
		return DetUbigeoEleccion.builder()
				.id(dto.getId())
				.ubigeo(dto.getIdUbigeo() != null ? new MaeUbigeo(dto.getIdUbigeo()) : null)
				.eleccion(dto.getCodigoEleccion() != null ? new MaeEleccion(dto.getIdEleccion()) : null)
				.idUbigeo(dto.getIdUbigeo())
				.codigoEleccion(dto.getCodigoEleccion())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(
						dto.getAudFechaCreacion() != null
								? DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA)
								: null
				)
				.build();
	}


	public static DetUbigeoEleccionAgrupacionPolitica convertirUbigeoEleccionAgrupacionPolitica(DetUbigeoEleccionAgrupacionPoliticaDto dto) {
		return DetUbigeoEleccionAgrupacionPolitica.builder()
				.id(dto.getId())
				.ubigeoEleccion(dto.getIdDetUbigeoEleccion() != null
						? new DetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
						: null)
				.agrupacionPolitica(dto.getIdAgrupacionPolitica() != null
						? new MaeAgrupacionPolitica(dto.getIdAgrupacionPolitica())
						: null)
				.estado(dto.getEstado())
				.posicion(dto.getPosicion())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(
						dto.getAudFechaCreacion() != null
								? DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA)
								: null
				)
				.build();
	}

	public static CabCatalogo convertirCabCatalogo(CatalogoDto dto) {
		return CabCatalogo.builder()
				.id(dto.getId())
				.catalogoPadre(dto.getIdPadre()!=null?new CabCatalogo(dto.getIdPadre()):null)
				.maestro(dto.getMaestro())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static DetCatalogoReferencia convertirDetCatalogoReferencia(DetCatalogoReferenciaDto dto) {
		return DetCatalogoReferencia.builder()
				.id(dto.getId())
				.catalogo(dto.getIdCatalogo()!=null?new CabCatalogo(dto.getIdCatalogo()):null)
				.tablaReferencia(dto.getTablaReferencia())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static DetCatalogoEstructura convertirDetCatalogoEstructura(DetCatalogoEstructuraDto dto) {
		return DetCatalogoEstructura.builder()
				.id(dto.getId())
				.catalogo(dto.getIdCatalogo()!=null?new CabCatalogo(dto.getIdCatalogo()):null)
				.columna(dto.getColumna())
				.nombre(dto.getNombre())
				.codigo(dto.getCodigoI())
				.scodigo(dto.getCodigoS())
				.orden(dto.getOrden())
				.tipo(dto.getTipo())
				.informacionAdicional(dto.getInformacionAdicional())
				.obligatorio(dto.getObligatorio())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static VwPrActa convertirActa(VwPrActaDto dto, List<MaeModulo> lstMaeModulo) {
		VwPrActa entity = new VwPrActa(dto.getIdActa());
		entity.setIdMesa(dto.getIdMesa());
		entity.setCodigoMesa(dto.getCodigoMesa());
		entity.setIdSolucionTecnologica(dto.getIdSolucionTecnologica());
		entity.setDescripcionSolucionTecnologica(dto.getDescripcionSolucionTecnologica());
		entity.setNumeroCopia(dto.getNumeroCopia());
		entity.setDigitoChequeoEscrutinio(dto.getDigitoChequeoEscrutinio());
		entity.setDigitoChequeoInstalacion(dto.getDigitoChequeoInstalacion());
		entity.setDigitoChequeoSufragio(dto.getDigitoChequeoSufragio());
		entity.setIdUbigeoEleccion(dto.getIdUbigeoEleccion());
		entity.setIdEleccion(dto.getIdEleccion());
		Optional<MaeModulo> modulo = lstMaeModulo.stream()
				.filter(m -> m.getEleccion().equals(dto.getIdEleccion()))
				.findFirst();
		modulo.ifPresent(m -> entity.setOrden(m.getOrden()));
		entity.setIdAmbitoGeografico(dto.getIdAmbitoGeografico());
		entity.setNubigeoNivel01(Integer.parseInt(dto.getUbigeoNivel01().toString()));
		entity.setNubigeoNivel02(Integer.parseInt(dto.getUbigeoNivel02().toString()));
		entity.setIdUbigeo(dto.getIdUbigeo());
		entity.setIdDistritoElectoral(dto.getIdDistritoElectoral());
		entity.setUbigeoNombreNivel01(UbigeoConTildeUtil.corregirNombre(dto.getUbigeoNombreNivel01()));
		entity.setUbigeoNombreNivel02(UbigeoConTildeUtil.corregirNombre(dto.getUbigeoNombreNivel02()));
		entity.setUbigeoNombreNivel03(UbigeoConTildeUtil.corregirNombre(dto.getUbigeoNombreNivel03()));
		entity.setCentroPoblado(dto.getCentroPoblado());
		entity.setIdLocalVotacion(dto.getIdLocalVotacion());
		entity.setNombreLocalVotacion(LocalVotacionConTildeUtil.corregirNombre(dto.getNombreLocalVotacion()));
		entity.setCodigoLocalVotacion(dto.getCodigoLocalVotacion());
		entity.setTotalElectoresHabiles(dto.getTotalElectoresHabiles());
		entity.setTotalVotosEmitidos(dto.getTotalVotosEmitidos());
		entity.setTotalVotosValidos(dto.getTotalVotosValidos());
		entity.setTotalAsistentes(dto.getTotalAsistentes());
		entity.setPorcentajeParticipacionCiudadana(dto.getPorcentajeParticipacionCiudadana()!=null?dto.getPorcentajeParticipacionCiudadana():0);
		entity.setEstadoActa(dto.getEstadoActa());
		entity.setEstadoComputo(dto.getEstadoComputo());
		entity.setCodigoEstadoActa(dto.getCodigoEstadoActa());
		entity.setDescripcionEstadoActa(dto.getDescripcionEstadoActa());
		entity.setEstadoActaResolucion(dto.getEstadoActaResolucion());
		entity.setEstadoDescripcionActaResolucion(dto.getEstadoDescripcionActaResolucion());
		entity.setDescripcionSubEstadoActa(dto.getDescripcionSubEstadoActa());
		ObjectMapper objectMapper = new ObjectMapper();
		List<VwPrActaDetalleDto> vwPrActaDetalleDto = new ArrayList<>();
		try {
			vwPrActaDetalleDto = objectMapper.readValue(dto.getDetalle(), new TypeReference<List<VwPrActaDetalleDto>>() {});
		} catch (JsonProcessingException e) {
			log.error("error en convertir detalle en Acta", e);
		}
		List<VwPrActaDetalle> lstEleccionDistritalDetalle = vwPrActaDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrActaDetalleCandidato> candidatos = vwPrEleccionDetalleDto.getMigraCandidato().stream()
							.map(candidatoDto -> VwPrActaDetalleCandidato.builder()
									.id(candidatoDto.getMigraId())
									.lista(candidatoDto.getMigraLista())
									.votos(getVotos(candidatoDto))
									
									.posicionOpcionVoto(candidatoDto.getMigraPosicionOpcionVoto())
									.codigoOpcionVoto(candidatoDto.getMigraCodigoOpcionVoto())
									.descripcionOpcionVoto(candidatoDto.getMigraDescripcionOpcionVoto())
									.posicion(candidatoDto.getMigraPosicion())
									.porcentajeVotosValidos(candidatoDto.getMigraPorcentajeVotosValidos())
									.porcentajeVotosEmitidos(candidatoDto.getMigraPorcentajeVotosEmitidos())
									.build())
							.toList();

					return VwPrActaDetalle.builder()
							.adAgrupacionPolitica(vwPrEleccionDetalleDto.getMigraAgrupacionPolitica())
							.adCodigo(vwPrEleccionDetalleDto.getMigraCodigo())
							.adDescripcion(vwPrEleccionDetalleDto.getMigraDescripcion())
							.adEstado(vwPrEleccionDetalleDto.getMigraEstado())
							.adVotos(vwPrEleccionDetalleDto.getMigraVotos()!=null?vwPrEleccionDetalleDto.getMigraVotos():0)
							.adTotalVotosValidos(vwPrEleccionDetalleDto.getMigraTotalVotosValidos()!=null?vwPrEleccionDetalleDto.getMigraTotalVotosValidos():0)
							.adCargo(vwPrEleccionDetalleDto.getMigraCargo())
							.adPorcentajeVotosValidos(vwPrEleccionDetalleDto.getMigraPorcentajeVotosValidos()!=null?vwPrEleccionDetalleDto.getMigraPorcentajeVotosValidos():0)
							.adPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getMigraPorcentajeVotosEmitidos()!=null?vwPrEleccionDetalleDto.getMigraPorcentajeVotosEmitidos():0)
							.adGrafico(vwPrEleccionDetalleDto.getMigraGrafico())
							.adPosicion(vwPrEleccionDetalleDto.getMigraPosicion())
							.candidato(candidatos)
							.build();
				}).toList();
		entity.setDetalle(lstEleccionDistritalDetalle);

		List<VwPrActaLineaTiempoDto> lstVwPrActaLineaTiempoDto = new ArrayList<>();
		try {
			lstVwPrActaLineaTiempoDto = objectMapper.readValue(dto.getLineaTiempo(), new TypeReference<List<VwPrActaLineaTiempoDto>>() {});
		} catch (JsonProcessingException e) {
			log.error("error en convertir linea de tiempo en Acta", e);
		}
		List<VwPrActaLineaTiempo> lstVwPrActaLineaTiempo = lstVwPrActaLineaTiempoDto.stream()
				.map(vwPrEleccionDetalleDto ->
						VwPrActaLineaTiempo.builder()
								.codigoEstadoActa(vwPrEleccionDetalleDto.getCodigoEstadoActa())
								.descripcionEstadoActa(vwPrEleccionDetalleDto.getDescripcionEstadoActa())
								.descripcionEstadoActaResolucion(vwPrEleccionDetalleDto.getDescripcionEstadoActaResolucion())
								.fechaRegistro(vwPrEleccionDetalleDto.getFechaRegistro())
								.build()
				).toList();

		entity.setLineaTiempo(lstVwPrActaLineaTiempo);
		return entity;
	}

	private static Long getVotos(VwPrActaDetalleCandidatoDto candidatoDto) {
		if(candidatoDto.getMigraVotos() != null){
			return candidatoDto.getMigraVotos();
		}else{
			if(candidatoDto.getMigraVotosRevo() != null){
				return candidatoDto.getMigraVotosRevo().longValue();
			}else{
				return 0L;
			}
		}
	}

	public static VwPrActa convertirActaParaTransmision(VwPrActaDto dto) {
		VwPrActa vwPrActaTransm = new VwPrActa(dto.getIdActa());
		vwPrActaTransm.setIdMesa(dto.getIdMesa());
		vwPrActaTransm.setCodigoMesa(dto.getCodigoMesa());
		vwPrActaTransm.setIdSolucionTecnologica(dto.getIdSolucionTecnologica());
		vwPrActaTransm.setDescripcionSolucionTecnologica(dto.getDescripcionSolucionTecnologica());
		vwPrActaTransm.setNumeroCopia(dto.getNumeroCopia());
		vwPrActaTransm.setDigitoChequeoEscrutinio(dto.getDigitoChequeoEscrutinio());
		vwPrActaTransm.setDigitoChequeoInstalacion(dto.getDigitoChequeoInstalacion());
		vwPrActaTransm.setDigitoChequeoSufragio(dto.getDigitoChequeoSufragio());
		vwPrActaTransm.setIdUbigeoEleccion(dto.getIdUbigeoEleccion());
		vwPrActaTransm.setIdEleccion(dto.getIdEleccion());
		vwPrActaTransm.setIdAmbitoGeografico(dto.getIdAmbitoGeografico());
		vwPrActaTransm.setNubigeoNivel01( null != dto.getUbigeoNivel01() ? Integer.parseInt(dto.getUbigeoNivel01().toString()):null);
		vwPrActaTransm.setNubigeoNivel02( null != dto.getUbigeoNivel02() ? Integer.parseInt(dto.getUbigeoNivel02().toString()): null);
		vwPrActaTransm.setIdUbigeo(dto.getIdUbigeo());
		vwPrActaTransm.setIdDistritoElectoral(dto.getIdDistritoElectoral());
		vwPrActaTransm.setCentroPoblado(dto.getCentroPoblado());
		vwPrActaTransm.setIdLocalVotacion(dto.getIdLocalVotacion());
		vwPrActaTransm.setCodigoLocalVotacion(dto.getCodigoLocalVotacion());
		vwPrActaTransm.setTotalElectoresHabiles(dto.getTotalElectoresHabiles());
		vwPrActaTransm.setTotalVotosEmitidos(dto.getTotalVotosEmitidos());
		vwPrActaTransm.setTotalVotosValidos(dto.getTotalVotosValidos());
		vwPrActaTransm.setTotalAsistentes(dto.getTotalAsistentes());
		vwPrActaTransm.setPorcentajeParticipacionCiudadana(dto.getPorcentajeParticipacionCiudadana());
		vwPrActaTransm.setEstadoActa(dto.getEstadoActa());
		vwPrActaTransm.setEstadoComputo(dto.getEstadoComputo());
		vwPrActaTransm.setCodigoEstadoActa(dto.getCodigoEstadoActa());
		vwPrActaTransm.setDescripcionEstadoActa(dto.getDescripcionEstadoActa());
		vwPrActaTransm.setEstadoActaResolucion(dto.getEstadoActaResolucion());
		vwPrActaTransm.setEstadoDescripcionActaResolucion(dto.getEstadoDescripcionActaResolucion());
		vwPrActaTransm.setDescripcionSubEstadoActa(dto.getDescripcionSubEstadoActa());
		ObjectMapper objectMapper = new ObjectMapper();
		List<VwPrActaDetalleDto> vwPrActaDetalleDto = new ArrayList<>();
		try {
			vwPrActaDetalleDto = objectMapper.readValue(dto.getDetalle(), new TypeReference<List<VwPrActaDetalleDto>>() {});
		} catch (JsonProcessingException e) {
			log.error("error en convertirActaParaTransmision 1 ", e);
			throw new UncheckedIOException(e);
		}
		List<VwPrActaDetalle> lstEleccionDistritalDetalle = vwPrActaDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrActaDetalleCandidato> candidatos = vwPrEleccionDetalleDto.getMigraCandidato().stream()
							.map(candidatoDto -> VwPrActaDetalleCandidato.builder()
									.id(candidatoDto.getMigraId())
									.lista(candidatoDto.getMigraLista())
									.votos(getVotos(candidatoDto))
									.posicionOpcionVoto(candidatoDto.getMigraPosicionOpcionVoto())
									.codigoOpcionVoto(candidatoDto.getMigraCodigoOpcionVoto())
									.descripcionOpcionVoto(candidatoDto.getMigraDescripcionOpcionVoto())
									.posicion(candidatoDto.getMigraPosicion())
									.porcentajeVotosValidos(candidatoDto.getMigraPorcentajeVotosValidos())
									.porcentajeVotosEmitidos(candidatoDto.getMigraPorcentajeVotosEmitidos())
									.build())
							.toList();

					return VwPrActaDetalle.builder()
							.adAgrupacionPolitica(vwPrEleccionDetalleDto.getMigraAgrupacionPolitica())
							.adCodigo(vwPrEleccionDetalleDto.getMigraCodigo())
							.adDescripcion(vwPrEleccionDetalleDto.getMigraDescripcion())
							.adVotos(vwPrEleccionDetalleDto.getMigraVotos())
							.adTotalVotosValidos(vwPrEleccionDetalleDto.getMigraTotalVotosValidos()!=null?vwPrEleccionDetalleDto.getMigraTotalVotosValidos():0)
							.adCargo(vwPrEleccionDetalleDto.getMigraCargo())
							.adPorcentajeVotosValidos(vwPrEleccionDetalleDto.getMigraPorcentajeVotosValidos())
							.adPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getMigraPorcentajeVotosEmitidos())
							.adGrafico(vwPrEleccionDetalleDto.getMigraGrafico())
							.adPosicion(vwPrEleccionDetalleDto.getMigraPosicion())
							.adEstado(vwPrEleccionDetalleDto.getMigraEstado())
							.candidato(candidatos)
							.build();
				}).toList();
		vwPrActaTransm.setDetalle(lstEleccionDistritalDetalle);

		List<VwPrActaLineaTiempoDto> lstVwPrActaLineaTiempoDto = new ArrayList<>();
		try {
			lstVwPrActaLineaTiempoDto = objectMapper.readValue(dto.getLineaTiempo(), new TypeReference<List<VwPrActaLineaTiempoDto>>() {});
		} catch (JsonProcessingException e) {
			log.error("error en convertirActaParaTransmision 2 ", e);
			throw new UncheckedIOException(e);
		}
		List<VwPrActaLineaTiempo> lstVwPrActaLineaTiempo = lstVwPrActaLineaTiempoDto.stream()
				.map(vwPrEleccionDetalleDto -> VwPrActaLineaTiempo.builder()
						.codigoEstadoActa(vwPrEleccionDetalleDto.getCodigoEstadoActa())
						.descripcionEstadoActa(vwPrEleccionDetalleDto.getDescripcionEstadoActa())
						.descripcionEstadoActaResolucion(vwPrEleccionDetalleDto.getDescripcionEstadoActaResolucion())
						.fechaRegistro(vwPrEleccionDetalleDto.getFechaRegistro())
						.build()
				).toList();

		vwPrActaTransm.setLineaTiempo(lstVwPrActaLineaTiempo);
		return vwPrActaTransm;
	}

	public static VwPrParticipacionCiudadana convertirParticipacionCiudadana(VwPrParticipacionCiudadanaDto dto) {
		return VwPrParticipacionCiudadana.builder()
				.id(dto.getIdFila())
				.tipoFiltro(dto.getTipoFiltro())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.idLocalVotacion(dto.getIdLocalVotacion())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.totalAsistentes(dto.getTotalAsistentes())
				.totalAusentes(dto.getTotalAusentes())
				.porcentajeAsistentes(dto.getPorcentajeAsistentes()!=null?dto.getPorcentajeAsistentes():0)
				.porcentajeAusentes(dto.getPorcentajeAusentes()!=null?dto.getPorcentajeAusentes():0)
				.build();
	}

	public static VwPrParticipacionCiudadana convertirParticipacionCiudadanaParaTransmision(VwPrParticipacionCiudadanaDto dto) {
		return VwPrParticipacionCiudadana.builder()
				.id(dto.getIdFila())
				.tipoFiltro(dto.getTipoFiltro())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.idLocalVotacion(dto.getIdLocalVotacion())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.totalAsistentes(dto.getTotalAsistentes())
				.totalAusentes(dto.getTotalAusentes())
				.porcentajeAsistentes(dto.getPorcentajeAsistentes())
				.porcentajeAusentes(dto.getPorcentajeAusentes())
				.build();
	}

	public static VwPrPresidenciales convertirEleccionPresidencial(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleUnCandidatoDto> lstEleccionDistritalDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleUnCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDistritalDetalleDto.stream()
				.map(data -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
					if(data.getCandidato() != null) {
						candidatoList = data.getCandidato().stream()
					        .map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoEp = new VwPrEleccionBaseDetalleCandidato();
									candidatoEp.setApellidoMaterno(candidatoDto.getApellidoMaterno());
									candidatoEp.setApellidoPaterno(candidatoDto.getApellidoPaterno());
									candidatoEp.setDocumentoIdentidad(candidatoDto.getDocumentoIdentidad());
									candidatoEp.setNombres(candidatoDto.getNombres());
									candidatoEp.setCargo(candidatoDto.getCargo());
					            return candidatoEp;
								})
								.toList();
					}

					VwPrEleccionBaseDetalle detalleEp = new VwPrEleccionBaseDetalle();
					detalleEp.setAgrupacionPolitica(data.getAgrupacionPolitica());
					detalleEp.setCodigo(data.getCodigo());
					detalleEp.setEstado(data.getEstado());
					detalleEp.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
					detalleEp.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
					detalleEp.setDescripcion(data.getDescripcion());
					detalleEp.setVotos(data.getVotos());
					detalleEp.setGrafico(data.getGrafico());
					detalleEp.setPosicion(data.getPosicion());
					detalleEp.setCandidato(candidatoList);
					return detalleEp;
				}).toList();

		return VwPrPresidenciales.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos()!=null?dto.getTotalVotosValidos():0)
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}

	public static VwPrPresidenciales convertirEleccionPresidencialParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleUnCandidatoDto> lstEleccionDistritalDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleUnCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDistritalDetalleDto.stream()
				.map(data -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatoList = Collections.emptyList();
					if(data.getCandidato() != null) {
						candidatoList = data.getCandidato().stream()
					        .map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoEpresiT = new VwPrEleccionBaseDetalleCandidato();
									candidatoEpresiT.setApellidoMaterno(candidatoDto.getApellidoMaterno());
									candidatoEpresiT.setApellidoPaterno(candidatoDto.getApellidoPaterno());
									candidatoEpresiT.setDocumentoIdentidad(candidatoDto.getDocumentoIdentidad());
									candidatoEpresiT.setNombres(candidatoDto.getNombres());
									candidatoEpresiT.setCargo(candidatoDto.getCargo());
									return candidatoEpresiT;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleEpT = new VwPrEleccionBaseDetalle();
					detalleEpT.setAgrupacionPolitica(data.getAgrupacionPolitica());
					detalleEpT.setCodigo(data.getCodigo());
					detalleEpT.setEstado(data.getEstado());
					detalleEpT.setPorcentajeVotosEmitidos(data.getPorcentajeVotosEmitidos());
					detalleEpT.setPorcentajeVotosValidos(data.getPorcentajeVotosValidos());
					detalleEpT.setDescripcion(data.getDescripcion());
					detalleEpT.setVotos(data.getVotos());
					detalleEpT.setGrafico(data.getGrafico());
					detalleEpT.setPosicion(data.getPosicion());
					detalleEpT.setCandidato(candidatoList);
					return detalleEpT;
				}).toList();

		return VwPrPresidenciales.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}


	public static VwPrDiputados convertirEleccionDiputados(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstDiputadosDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {					
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
					        .map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoDiputado = new VwPrEleccionBaseDetalleCandidato();
									candidatoDiputado.setId(candidatoDto.getId());
									candidatoDiputado.setLista(candidatoDto.getLista());
									candidatoDiputado.setVotos(candidatoDto.getTotalVotos());
									return candidatoDiputado;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleDiputado = new VwPrEleccionBaseDetalle();
					detalleDiputado.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleDiputado.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleDiputado.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleDiputado.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleDiputado.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleDiputado.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleDiputado.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleDiputado.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleDiputado.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleDiputado.setCandidato(candidatos);
					return detalleDiputado;
				}).toList();

		return VwPrDiputados.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.distritoElectoral(dto.getDistritoElectoral())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos()!=null?dto.getTotalVotosValidos():0)
				.detalle(lstDiputadosDetalle)
				.build();
	}

	public static VwPrSenadoresDistritoElectoralMultiple convertirEleccionSenadoresDistritoElectoralMultiple(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstDiputadosDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoSenadoresDEM = new VwPrEleccionBaseDetalleCandidato();
									candidatoSenadoresDEM.setId(candidatoDto.getId());
									candidatoSenadoresDEM.setLista(candidatoDto.getLista());
									candidatoSenadoresDEM.setVotos(candidatoDto.getTotalVotos());
									return candidatoSenadoresDEM;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleSenadoresDEM = new VwPrEleccionBaseDetalle();
					detalleSenadoresDEM.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleSenadoresDEM.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleSenadoresDEM.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleSenadoresDEM.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleSenadoresDEM.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleSenadoresDEM.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleSenadoresDEM.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleSenadoresDEM.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleSenadoresDEM.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleSenadoresDEM.setCandidato(candidatos);
					return detalleSenadoresDEM;
				}).toList();

		return VwPrSenadoresDistritoElectoralMultiple.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.distritoElectoral(dto.getDistritoElectoral())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos()!=null?dto.getTotalVotosValidos():0)
				.detalle(lstDiputadosDetalle)
				.build();
	}

	public static VwPrSenadoresDistritoElectoralMultiple convertirEleccionSenadoresDistritoElectoralMultipleParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstDiputadosDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoSenadoresDemT = new VwPrEleccionBaseDetalleCandidato();
									candidatoSenadoresDemT.setId(candidatoDto.getId());
									candidatoSenadoresDemT.setLista(candidatoDto.getLista());
									candidatoSenadoresDemT.setVotos(candidatoDto.getTotalVotos());
									return candidatoSenadoresDemT;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleSenadoresDemT = new VwPrEleccionBaseDetalle();
					detalleSenadoresDemT.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleSenadoresDemT.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleSenadoresDemT.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleSenadoresDemT.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleSenadoresDemT.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleSenadoresDemT.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleSenadoresDemT.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleSenadoresDemT.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleSenadoresDemT.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleSenadoresDemT.setCandidato(candidatos);
					return detalleSenadoresDemT;
				}).toList();

		return VwPrSenadoresDistritoElectoralMultiple.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.distritoElectoral(dto.getDistritoElectoral())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstDiputadosDetalle)
				.build();
	}

	public static VwPrDiputados convertirEleccionDiputadosParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstDiputadosDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
					        .map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoDiputadoT = new VwPrEleccionBaseDetalleCandidato();
									candidatoDiputadoT.setId(candidatoDto.getId());
									candidatoDiputadoT.setLista(candidatoDto.getLista());
									candidatoDiputadoT.setVotos(candidatoDto.getTotalVotos());
									return candidatoDiputadoT;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleDiputadoT = new VwPrEleccionBaseDetalle();
					detalleDiputadoT.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleDiputadoT.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleDiputadoT.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleDiputadoT.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleDiputadoT.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleDiputadoT.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleDiputadoT.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleDiputadoT.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleDiputadoT.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleDiputadoT.setCandidato(candidatos);
					return detalleDiputadoT;
				}).toList();

		return VwPrDiputados.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.distritoElectoral(dto.getDistritoElectoral())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstDiputadosDetalle)
				.build();
	}

	public static VwPrParlamentoAndino convertirParlamentoAndino(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoParlamento = new VwPrEleccionBaseDetalleCandidato();
									candidatoParlamento.setId(candidatoDto.getId());
									candidatoParlamento.setLista(candidatoDto.getLista());
									candidatoParlamento.setVotos(candidatoDto.getTotalVotos());
									return candidatoParlamento;
								})
								.toList();
					}

					VwPrEleccionBaseDetalle detalleParlamento = new VwPrEleccionBaseDetalle();
					detalleParlamento.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleParlamento.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleParlamento.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleParlamento.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleParlamento.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleParlamento.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleParlamento.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleParlamento.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleParlamento.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleParlamento.setCandidato(candidatos);
					return detalleParlamento;
				}).toList();

		return VwPrParlamentoAndino.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos()!=null?dto.getTotalVotosValidos():0)
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}

	public static VwPrSenadoresDistritoNacionalUnico convertirSenadoresDistritoNacionalUnico(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = Collections.emptyList();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoSenadoresDnu = new VwPrEleccionBaseDetalleCandidato();
									candidatoSenadoresDnu.setId(candidatoDto.getId());
									candidatoSenadoresDnu.setLista(candidatoDto.getLista());
									candidatoSenadoresDnu.setVotos(candidatoDto.getTotalVotos());
									return candidatoSenadoresDnu;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleSenadoresDnu = new VwPrEleccionBaseDetalle();
					detalleSenadoresDnu.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleSenadoresDnu.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleSenadoresDnu.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleSenadoresDnu.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleSenadoresDnu.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleSenadoresDnu.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleSenadoresDnu.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleSenadoresDnu.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleSenadoresDnu.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleSenadoresDnu.setCandidato(candidatos);
					return detalleSenadoresDnu;
				}).toList();

		return VwPrSenadoresDistritoNacionalUnico.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}

	public static VwPrSenadoresDistritoNacionalUnico convertirSenadoresDistritoNacionalUnicoParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = Collections.emptyList();
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoSenadoresDnuT = new VwPrEleccionBaseDetalleCandidato();
									candidatoSenadoresDnuT.setId(candidatoDto.getId());
									candidatoSenadoresDnuT.setLista(candidatoDto.getLista());
									candidatoSenadoresDnuT.setVotos(candidatoDto.getTotalVotos());
									return candidatoSenadoresDnuT;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleSenadoresDnuT = new VwPrEleccionBaseDetalle();
					detalleSenadoresDnuT.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleSenadoresDnuT.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleSenadoresDnuT.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleSenadoresDnuT.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleSenadoresDnuT.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleSenadoresDnuT.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleSenadoresDnuT.setVotos(vwPrEleccionDetalleDto.getVotos());
					detalleSenadoresDnuT.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleSenadoresDnuT.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleSenadoresDnuT.setCandidato(candidatos);
					return detalleSenadoresDnuT;
				}).toList();

		return VwPrSenadoresDistritoNacionalUnico.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}
	
	public static VwPrRevocatoriaDistrital convertirRevocatoriaDistrital(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstELeccionBaseDetalles = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoRevocatoriaDistrital = new VwPrEleccionBaseDetalleCandidato();
									candidatoRevocatoriaDistrital.setPosicionOpcionVoto(candidatoDto.getPosicionOpcionVoto());
									candidatoRevocatoriaDistrital.setCodigoOpcionVoto(candidatoDto.getCodigoOpcionVoto());
									candidatoRevocatoriaDistrital.setDescripcionOpcionVoto(candidatoDto.getDescripcionOpcionVoto());
									candidatoRevocatoriaDistrital.setVotos(candidatoDto.getVotos()!=null?candidatoDto.getVotos():0);
									candidatoRevocatoriaDistrital.setPorcentajeVotosValidos(candidatoDto.getPorcentajeVotosValidos());
									candidatoRevocatoriaDistrital.setPorcentajeVotosEmitidos(candidatoDto.getPorcentajeVotosEmitidos());
									return candidatoRevocatoriaDistrital;
								})
								.toList();
					}
					
					VwPrEleccionBaseDetalle detalleRevocatoriaDistrital = new VwPrEleccionBaseDetalle();
					detalleRevocatoriaDistrital.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleRevocatoriaDistrital.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleRevocatoriaDistrital.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleRevocatoriaDistrital.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleRevocatoriaDistrital.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleRevocatoriaDistrital.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleRevocatoriaDistrital.setVotos(vwPrEleccionDetalleDto.getVotos()!=null?vwPrEleccionDetalleDto.getVotos():0);
					detalleRevocatoriaDistrital.setTotalVotosValidos(vwPrEleccionDetalleDto.getTotalVotosValidos()!=null?vwPrEleccionDetalleDto.getTotalVotosValidos():0);
					detalleRevocatoriaDistrital.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleRevocatoriaDistrital.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleRevocatoriaDistrital.setCargo(vwPrEleccionDetalleDto.getCargo());
					detalleRevocatoriaDistrital.setCantidad(vwPrEleccionDetalleDto.getCantidad());
					detalleRevocatoriaDistrital.setCandidato(candidatos);
					return detalleRevocatoriaDistrital;
				})
				.toList();
		
		return VwPrRevocatoriaDistrital.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles()!=null?dto.getTotalElectoresHabiles():0)
				.participacionCiudadana(dto.getParticipacionCiudadana()!=null?dto.getParticipacionCiudadana():0)
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana()!=null?dto.getPorcentParticipacionCiudadana():0.0)
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas()!=null?dto.getActasContabilizadas():0)
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas()!=null?dto.getPorcentajeActasContabilizadas():0)
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas()!=null?dto.getActasObservadasEnviadas():0)
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas()!=null?dto.getPorcentajeActasObservadasEnviadas():0)
				.actasPendientes(dto.getActasPendientes()!=null?dto.getActasPendientes():0)
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes()!=null?dto.getPorcentajeActasPendientes():0)
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos()!=null?dto.getTotalVotosEmitidos():0)
				.totalVotosValidos(dto.getTotalVotosValidos()!=null?dto.getTotalVotosValidos():0)
				.detalle(lstELeccionBaseDetalles)
				.build();
	}
	
	public static VwPrRevocatoriaDistrital convertirRevocatoriaDistritalParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstELeccionBaseDetalles = lstEleccionDetalleDto.stream()
				.map(vwPrEleccionDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					
					if (vwPrEleccionDetalleDto.getCandidato() != null && !vwPrEleccionDetalleDto.getCandidato().isEmpty()) {
						candidatos = vwPrEleccionDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoRevocatoriaDistritalT = new VwPrEleccionBaseDetalleCandidato();
									candidatoRevocatoriaDistritalT.setPosicionOpcionVoto(candidatoDto.getPosicionOpcionVoto());
									candidatoRevocatoriaDistritalT.setCodigoOpcionVoto(candidatoDto.getCodigoOpcionVoto());
									candidatoRevocatoriaDistritalT.setDescripcionOpcionVoto(candidatoDto.getDescripcionOpcionVoto());
									candidatoRevocatoriaDistritalT.setVotos(candidatoDto.getVotos()!=null?candidatoDto.getVotos():0);
									candidatoRevocatoriaDistritalT.setPorcentajeVotosValidos(candidatoDto.getPorcentajeVotosValidos());
									candidatoRevocatoriaDistritalT.setPorcentajeVotosEmitidos(candidatoDto.getPorcentajeVotosEmitidos());
									return candidatoRevocatoriaDistritalT;
								})
								.toList();
					}
					
					VwPrEleccionBaseDetalle detalleRevocatoriaDistritalT = new VwPrEleccionBaseDetalle();
					detalleRevocatoriaDistritalT.setAgrupacionPolitica(vwPrEleccionDetalleDto.getAgrupacionPolitica());
					detalleRevocatoriaDistritalT.setCodigo(vwPrEleccionDetalleDto.getCodigo());
					detalleRevocatoriaDistritalT.setEstado(vwPrEleccionDetalleDto.getEstado());
					detalleRevocatoriaDistritalT.setPorcentajeVotosEmitidos(vwPrEleccionDetalleDto.getPorcentajeVotosEmitidos());
					detalleRevocatoriaDistritalT.setPorcentajeVotosValidos(vwPrEleccionDetalleDto.getPorcentajeVotosValidos());
					detalleRevocatoriaDistritalT.setDescripcion(vwPrEleccionDetalleDto.getDescripcion());
					detalleRevocatoriaDistritalT.setVotos(vwPrEleccionDetalleDto.getVotos()!=null?vwPrEleccionDetalleDto.getVotos():0);
					detalleRevocatoriaDistritalT.setTotalVotosValidos(vwPrEleccionDetalleDto.getTotalVotosValidos()!=null?vwPrEleccionDetalleDto.getTotalVotosValidos():0);
					detalleRevocatoriaDistritalT.setGrafico(vwPrEleccionDetalleDto.getGrafico());
					detalleRevocatoriaDistritalT.setPosicion(vwPrEleccionDetalleDto.getPosicion());
					detalleRevocatoriaDistritalT.setCargo(vwPrEleccionDetalleDto.getCargo());
					detalleRevocatoriaDistritalT.setCantidad(vwPrEleccionDetalleDto.getCantidad());
					detalleRevocatoriaDistritalT.setCandidato(candidatos);
					return detalleRevocatoriaDistritalT;
				})
				.toList();
		
		return VwPrRevocatoriaDistrital.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstELeccionBaseDetalles)
				.build();
	}

	public static VwPrParlamentoAndino convertirParlamentoAndinoParaTransmision(VwPrEleccionDto dto) {
		List<VwPrEleccionDetalleMultiCandidatoDto> lstEleccionDetalleDto = convertJsonToList(dto.getDetalle(), VwPrEleccionDetalleMultiCandidatoDto.class);
		List<VwPrEleccionBaseDetalle> lstEleccionDistritalDetalle = lstEleccionDetalleDto.stream()
				.map(vwPrParlamentoDetalleDto -> {
					List<VwPrEleccionBaseDetalleCandidato> candidatos = new ArrayList<>();
					if (vwPrParlamentoDetalleDto.getCandidato() != null && !vwPrParlamentoDetalleDto.getCandidato().isEmpty()) {
						candidatos =  vwPrParlamentoDetalleDto.getCandidato().stream()
								.map(candidatoDto -> {
									VwPrEleccionBaseDetalleCandidato candidatoParlamentoT = new VwPrEleccionBaseDetalleCandidato();
									candidatoParlamentoT.setId(candidatoDto.getId());
									candidatoParlamentoT.setLista(candidatoDto.getLista());
									candidatoParlamentoT.setVotos(candidatoDto.getTotalVotos());
									return candidatoParlamentoT;
								})
								.toList();
					}
					VwPrEleccionBaseDetalle detalleParlamentoT = new VwPrEleccionBaseDetalle();
					detalleParlamentoT.setAgrupacionPolitica(vwPrParlamentoDetalleDto.getAgrupacionPolitica());
					detalleParlamentoT.setCodigo(vwPrParlamentoDetalleDto.getCodigo());
					detalleParlamentoT.setEstado(vwPrParlamentoDetalleDto.getEstado());
					detalleParlamentoT.setPorcentajeVotosEmitidos(vwPrParlamentoDetalleDto.getPorcentajeVotosEmitidos());
					detalleParlamentoT.setPorcentajeVotosValidos(vwPrParlamentoDetalleDto.getPorcentajeVotosValidos());
					detalleParlamentoT.setDescripcion(vwPrParlamentoDetalleDto.getDescripcion());
					detalleParlamentoT.setVotos(vwPrParlamentoDetalleDto.getVotos());
					detalleParlamentoT.setGrafico(vwPrParlamentoDetalleDto.getGrafico());
					detalleParlamentoT.setPosicion(vwPrParlamentoDetalleDto.getPosicion());
					detalleParlamentoT.setCandidato(candidatos);
					return detalleParlamentoT;
				}).toList();

		return VwPrParlamentoAndino.builder()
				.id(dto.getIdFila())
				.tipoEleccion(dto.getTipoEleccion())
				.tipoFiltro(dto.getTipoFiltro())
				.idDetUbigeoEleccion(dto.getIdDetUbigeoEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalElectoresHabiles(dto.getTotalElectoresHabiles())
				.participacionCiudadana(dto.getParticipacionCiudadana())
				.porcentajeParticipacionCiudadana(dto.getPorcentParticipacionCiudadana())
				.totalActas(dto.getTotalActas())
				.actasContabilizadas(dto.getActasContabilizadas())
				.porcentajeActasContabilizadas(dto.getPorcentajeActasContabilizadas())
				.actasObservadasEnviadas(dto.getActasObservadasEnviadas())
				.porcentajeActasObservadasEnviadas(dto.getPorcentajeActasObservadasEnviadas())
				.actasPendientes(dto.getActasPendientes())
				.porcentajeActasPendientes(dto.getPorcentajeActasPendientes())
				.audFechaModificacion(new Date())
				.totalVotosEmitidos(dto.getTotalVotosEmitidos())
				.totalVotosValidos(dto.getTotalVotosValidos())
				.detalle(lstEleccionDistritalDetalle)
				.build();
	}

	public static MaeDistritoElectoral convertirDistritoElectoral(DistritoElectoralDto dto) {
		return MaeDistritoElectoral.builder()
				.id(dto.getId())
				.distritoElectoralPadre(dto.getIdDistritoElectoralPadre()!=null?new MaeDistritoElectoral(Integer.parseInt(dto.getIdDistritoElectoralPadre().toString())):null)
				.codigo(dto.getCodigo())
				.nombre(dto.getNombre().equals("LIMA")? "LIMA METROPOLITANA" : UbigeoConTildeUtil.corregirNombre(dto.getNombre()))
				.cantidadCurules(dto.getCantidadCurules())
				.cantidadCandidatos(dto.getCantidadCandidatos())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(dto.getAudFechaCreacion()!=null?DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA):null)
				.build();
	}

	public static VwPrMesa convertirVistaMesa(VwPrMesaDto dto) {
		return VwPrMesa.builder()
				.id(dto.getId())
				.tipoFiltro(dto.getTipoFiltro())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.distritoElectoral(dto.getIdDistritoElectoral())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalMesas(dto.getTotalMesas())
				.mesasInstaladas(dto.getMesasInstaladas())
				.mesasNoInstaladas(dto.getMesasNoInstaladas())
				.mesasPorInformar(dto.getMesasPorInformar())
				.build();
	}

	public static VwPrMesa convertirVistaMesaParaTransmision(VwPrMesaDto dto) {
		return VwPrMesa.builder()
				.id(dto.getId())
				.tipoFiltro(dto.getTipoFiltro())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.distritoElectoral(dto.getIdDistritoElectoral())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeoNivel03(dto.getUbigeoNivel03())
				.totalMesas(dto.getTotalMesas())
				.mesasInstaladas(dto.getMesasInstaladas())
				.mesasNoInstaladas(dto.getMesasNoInstaladas())
				.mesasPorInformar(dto.getMesasPorInformar())
				.build();
	}

	public static MaeCandidato convertirCandidato(CandidatoDto dto) {
		return MaeCandidato.builder()
				.id(dto.getId())
				.distritoElectoral(dto.getIdDistritoElectoral()==null?null:new MaeDistritoElectoral(Integer.parseInt(dto.getIdDistritoElectoral().toString())))
				.eleccion(new MaeEleccion(dto.getIdEleccion()))
				.agrupacionPolitica(new MaeAgrupacionPolitica(dto.getIdAgrupacionPolitica()))
				.cargo(dto.getCargo())
				.documentoIdentidad(dto.getDocumentoIdentidad())
				.apellidoPaterno(dto.getApellidoPaterno())
				.apellidoMaterno(dto.getApellidoMaterno())
				.nombres(dto.getNombres())
				.sexo(dto.getSexo())
				.estado(dto.getEstado())
				.lista(dto.getLista())
				.activo(dto.getActivo())
				.audUsuarioCreacion(dto.getAudUsuarioCreacion())
				.audFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), PresentacionConstantes.FORMATO_FECHA))
				.build();
	}

	public static MaePadron convertirPadron(MaePadronDto dto) {
		return MaePadron.builder()
				.id(dto.getDocumentoIdentidad())
				.mesa(dto.getCodigoMesa())
				.build();
	}

	public static VwPrTotalCandidatosPorAgrupacionPolitica convertirVistaTotalCandidatosPorAgrupacionPolitica(VwPrTotalCandidatosPorAgrupacionPoliticaExportDto dto) {
		return VwPrTotalCandidatosPorAgrupacionPolitica.builder()
				.id(dto.getIdFila())
				.eleccion(dto.getEleccion())
				.ambitoGeografico(dto.getAmbitoGeografico())
				.distritoElectoral(dto.getDistritoElectoral())
				.ubigeoNivel01(dto.getUbigeoNivel01())
				.ubigeoNivel02(dto.getUbigeoNivel02())
				.ubigeo(dto.getUbigeo())
				.detUbigeoEleccion(dto.getDetUbigeoEleccion())
				.agrupacionPolitica(dto.getAgrupacionPolitica())
				.posicion(dto.getPosicion())
				.codigo(dto.getCodigo())
				.descripcion(dto.getDescripcion())
				.totalCandidatos(dto.getTotalCandidatos())
				.build();
	}
	
}