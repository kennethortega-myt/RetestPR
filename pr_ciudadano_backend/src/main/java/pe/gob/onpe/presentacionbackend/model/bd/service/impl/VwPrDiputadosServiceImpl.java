package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrDiputados;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrEleccionBaseDetalle;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrEleccionBaseDetalleCandidato;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrDiputadosRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrDiputadosRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrTotalCandidatosPorAgrupacionPoliticaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrDiputadosService;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteNombreDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.OrganizacionPoliticaDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteCandidatoDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.utils.ConstantesComunes;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VwPrDiputadosServiceImpl implements VwPrDiputadosService {

	private static final String CADENA_INICIO = "(?i).*";
	private static final String CADENA_FIN = ".*";
	public static final String LOS_FILTROS_INDICADOS_NO_CORRESPONDEN_A_UN_REGISTRO_EN_LA_BD_PR_SIZE = "Los filtros indicados no corresponden a un registro en la bd PR, size: {} ";
	private final VwPrDiputadosRepository vwPrDiputadosRepository;
	private final VwPrDiputadosRepositoryCustom vwPrDiputadosRepositoryCustom;
	private final VwPrTotalCandidatosPorAgrupacionPoliticaRepository vwPrTotalCandidatosPorAgrupacionPoliticaRepository;

	@Override
	public void save(VwPrDiputados k) {
		this.vwPrDiputadosRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrDiputados> k) {
		this.vwPrDiputadosRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrDiputadosRepository.deleteAll();
	}

	@Override
	public List<VwPrDiputados> findAll() {
		return this.vwPrDiputadosRepository.findAll();
	}
	
	List<ParticipanteCandidatoDiputadoDto> participanteCandidatoDiputadoDtos = new ArrayList<>();

	@Override
	public List<ParticipanteDiputadoDto> listarParticipantesUbicacionGeografica(FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		List<VwPrDiputados> registros = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltroAndDistritoElectoral(filtroParticipanteDto.getIdEleccion(), filtroParticipanteDto.getTipoFiltro(), filtroParticipanteDto.getIdDistritoElectoral());
		return construirRespuesta(registros, 1, null);
	}

	@Override
	public List<ParticipanteDiputadoDto> listarParticipantesUbicacionGeograficaNombre(FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		Optional<VwPrTotalCandidatosPorAgrupacionPolitica> candidatos1 = vwPrTotalCandidatosPorAgrupacionPoliticaRepository
				.findByEleccionAndDistritoElectoral(filtroParticipanteDto.getIdEleccion(),filtroParticipanteDto.getIdDistritoElectoral())
				.stream().findFirst();

		int totalCandidatosEntero;

		totalCandidatosEntero = candidatos1.map(vwPrTotalCandidatosPorAgrupacionPolitica ->
				Integer.parseInt(vwPrTotalCandidatosPorAgrupacionPolitica.getTotalCandidatos())).orElse(0);

		List<ParticipanteDiputadoDto> lstParticipanteDiputadoDto = vwPrDiputadosRepositoryCustom.buscarAgrupacionPoliticaNombre(filtroParticipanteDto);

		lstParticipanteDiputadoDto.forEach(agruPolitica -> agruPolitica.setTotalCandidatos(totalCandidatosEntero));
		return lstParticipanteDiputadoDto;
	}

	private List<ParticipanteDiputadoDto> construirRespuesta(List<VwPrDiputados> registros, Integer grafico, String nombreApellidoAgrupacion){

		if(registros.isEmpty()) {
			return Collections.emptyList();
		} else if(registros.size() > 1) {
			log.info(LOS_FILTROS_INDICADOS_NO_CORRESPONDEN_A_UN_REGISTRO_EN_LA_BD_PR_SIZE, registros.size());
			return Collections.emptyList();
		}
		List<VwPrEleccionBaseDetalle> detalle = registros.get(0).getDetalle().stream()
				.filter(diputado -> !Objects.equals(diputado.getEstado(), ConstantesComunes.OP_ESTADO_NOPARTICIPA)) //participante achurado
				// .sorted(Comparator.comparingLong(VwPrEleccionBaseDetalle::getVotos).reversed())
				// .sorted(Comparator.comparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.sorted(Comparator.comparingLong(VwPrEleccionBaseDetalle::getVotos)
						.reversed()
						.thenComparingInt(VwPrEleccionBaseDetalle::getPosicion))
				.toList();
		if(grafico != null) {
			detalle = detalle.stream().filter(data -> data.getGrafico().compareTo(grafico) == 0).toList();
		}
		if(nombreApellidoAgrupacion != null && !nombreApellidoAgrupacion.isEmpty()) {
			detalle = detalle.stream()
					 .filter(data -> data.getGrafico().compareTo(1) == 0)
					 .toList();
		}

		return  detalle.stream().map(VwPrDiputadosServiceImpl::mapperCampos)
				.toList();
	}
	private static ParticipanteDiputadoDto mapperCampos(VwPrEleccionBaseDetalle registro){


		return ParticipanteDiputadoDto.builder()
				.totalVotosValidos(registro.getVotos())
				.porcentajeVotosEmitidos(registro.getPorcentajeVotosEmitidos())
				.porcentajeVotosValidos(registro.getPorcentajeVotosValidos())
				.codigoAgrupacionPolitica(registro.getCodigo())
				.nombreAgrupacionPolitica(registro.getDescripcion())
				.posicion(registro.getPosicion())
				.totalCandidatos(registro.getCandidato().size())
				.build();
	}
	@Override
	public  List<ParticipanteCandidatoDiputadoDto> obtenerParticipantes(FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		return vwPrDiputadosRepositoryCustom.buscarCandidatosGraficoAll(filtroParticipanteDto);
	}
	
	@Override
	public  List<ParticipanteCandidatoDiputadoDto> obtenerParticipantesNombre(FiltroParticipanteNombreDiputadoDto filtroParticipanteNombreDto) {
		return vwPrDiputadosRepositoryCustom.buscarCandidatosNombre(filtroParticipanteNombreDto);
	}

	@Override
	public List<OrganizacionPoliticaDto> listarOrganizacionPolitica(FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		List<VwPrDiputados> lista= vwPrDiputadosRepository.findByTipoEleccionAndDistritoElectoralAndTipoFiltro(filtroParticipanteDto.getIdEleccion(),filtroParticipanteDto.getIdDistritoElectoral(),filtroParticipanteDto.getTipoFiltro());

		return lista.stream().flatMap(org -> org.getDetalle().stream())
				.filter(det -> det.getEstado().compareTo(1) == 0 && det.getGrafico().compareTo(1) == 0)
				.map(detalle ->
					OrganizacionPoliticaDto.builder()
							.idAgrupacionPolitica(detalle.getAgrupacionPolitica())
							.nombreAgrupacionPolitica(detalle.getDescripcion())
							.build()
				)
				.sorted(Comparator.comparing(OrganizacionPoliticaDto::getNombreAgrupacionPolitica))
				.toList();
	}

	
}
