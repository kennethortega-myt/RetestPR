package pe.gob.onpe.presentacionbackend.model.bd.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrSenadoresDistritoElectoralMultiple;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrTotalCandidatosPorAgrupacionPolitica;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrSenadoresDistritoElectoralMultipleRepository;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrSenadoresDistritoElectoralMultipleRepositoryCustom;
import pe.gob.onpe.presentacionbackend.model.bd.repository.primary.VwPrTotalCandidatosPorAgrupacionPoliticaRepository;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrSenadoresDistritoElectoralMultipleService;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.OrganizacionPoliticaDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteSenadorDistritoMultipleDto;

import org.springframework.stereotype.Service;

import static pe.gob.onpe.presentacionbackend.utils.ConstantesComunes.OP_ESTADO_NOPARTICIPA;

@Service
@Slf4j
@RequiredArgsConstructor
public class VwPrSenadoresDistritoElectoralMultipleServiceImpl implements VwPrSenadoresDistritoElectoralMultipleService {

	private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
	private final VwPrSenadoresDistritoElectoralMultipleRepositoryCustom vwPrSenadoresDistritoElectoralMultipleRepositoryCustom;
	private final VwPrTotalCandidatosPorAgrupacionPoliticaRepository vwPrTotalCandidatosPorAgrupacionPoliticaRepository;

	@Override
	public void save(VwPrSenadoresDistritoElectoralMultiple k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.save(k);
	}

	@Override
	public void saveAll(List<VwPrSenadoresDistritoElectoralMultiple> k) {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.vwPrSenadoresDistritoElectoralMultipleRepository.deleteAll();
	}

	@Override
	public List<VwPrSenadoresDistritoElectoralMultiple> findAll() {
		return this.vwPrSenadoresDistritoElectoralMultipleRepository.findAll();
	}

	@Override
	public List<ResponseParticipanteSenadorDistritoMultipleDto> listaParticipantesUbicacionGeograficaNombre(RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteDto) {
		Optional<VwPrTotalCandidatosPorAgrupacionPolitica> candidatos = this.vwPrTotalCandidatosPorAgrupacionPoliticaRepository.findByEleccionAndDistritoElectoral(filtroParticipanteDto.getIdEleccion(),filtroParticipanteDto.getIdDistritoElectoral())
				.stream()
				.findFirst();

		int totalCandidatosEntero;

        totalCandidatosEntero = candidatos.map(vwPrTotalCandidatosPorAgrupacionPolitica ->
				Integer.parseInt(vwPrTotalCandidatosPorAgrupacionPolitica.getTotalCandidatos())).orElse(0);

		List<ResponseParticipanteSenadorDistritoMultipleDto> senadores = this.vwPrSenadoresDistritoElectoralMultipleRepositoryCustom.participantesUbicacionGeograficaNombre(filtroParticipanteDto);
		
		senadores.forEach(agruPolitica -> agruPolitica.setTotalCandidatos(totalCandidatosEntero));
		
		return senadores;
	}
	
	@Override
	public List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> listaParticipantesCandidato(
			RequestParticipanteCandidatoSenadorDistritoMultipleDto filtroParticipanteCandidatoDto) {
		return this.vwPrSenadoresDistritoElectoralMultipleRepositoryCustom.participantesCandidato(filtroParticipanteCandidatoDto);
	}
	
	@Override
	public List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> listaParticipantesCandidatoOrganizacion(
			RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto filtroParticipanteCandidatoNombreDto) {
		return this.vwPrSenadoresDistritoElectoralMultipleRepositoryCustom.participantesCandidatoOrganizacion(filtroParticipanteCandidatoNombreDto);
	}
	@Override
	public List<OrganizacionPoliticaDto> listarOrganizacionPolitica(RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteDto) {
		List<VwPrSenadoresDistritoElectoralMultiple> lista= vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndDistritoElectoralAndTipoFiltro(filtroParticipanteDto.getIdEleccion(), filtroParticipanteDto.getIdDistritoElectoral(), filtroParticipanteDto.getTipoFiltro());
		return lista.stream()
				.flatMap(org -> org.getDetalle().stream())
				.filter(det -> det.getGrafico().compareTo(1) == 0)
				.filter(det -> det.getEstado().compareTo(1) == 0)
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
