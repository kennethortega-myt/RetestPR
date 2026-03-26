package pe.gob.onpe.presentacionbackend.rest.controller;

import static pe.gob.onpe.presentacionbackend.utils.ArchivoUtils.addIfNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrParlamentoAndinoService;
import pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino.*;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/parlamento-andino")
@RequiredArgsConstructor
public class ParlamentoAndinoController {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final VwPrParlamentoAndinoService vwPrParlamentoAndinoService;

	@GetMapping("/participantes-ubicacion-geografica")
	public ResponseEntity<GenericResponse<List<ParticipanteParlamentoAndinoDto>>> listarParticipantesPorUbicacionGeografica(@ModelAttribute @Valid FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteParlamentoAndinoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteParlamentoAndinoDto> lstParticipanteDto = vwPrParlamentoAndinoService.listarParticipantesUbicacionGeografica(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-ubicacion-geografica-nombre")
	public ResponseEntity<GenericResponse<List<ParticipanteParlamentoAndinoDto>>> listarParticipantesPorUbicacionGeograficaNombre(@ModelAttribute @Valid FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteParlamentoAndinoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteParlamentoAndinoDto> lstParticipanteDto = vwPrParlamentoAndinoService.listarParticipantesUbicacionGeograficaNombre(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/participantes-por-candidato")
	public ResponseEntity<GenericResponse<List<ParticipanteCandidatoParlamentoAndinoDto>>> listaParticipantesPorCandidato(@ModelAttribute @Valid FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteCandidatoParlamentoAndinoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoParlamentoAndinoDto> lstParticipanteDto = vwPrParlamentoAndinoService.listarParticipantesPorCandidato(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			List<ParticipanteCandidatoParlamentoAndinoDto> filtrados = lstParticipanteDto.stream().filter(p -> p.getTotalVotosValidos() > 0).toList();
			if (filtrados.size() > 56) {
            	filtrados = filtrados.subList(0, 56);
         	}	
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(filtrados);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/participantes-por-candidato-paginado")
	public ResponseEntity<GenericResponse<ParlamentoCandidatosPaginaResponseDto>> listarParticipantesPorCandidatoPaginado(
			@ModelAttribute @Valid FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamanio) {
		GenericResponse<ParlamentoCandidatosPaginaResponseDto> genericResponse = new GenericResponse<>();

		Optional<ParlamentoCandidatosPaginaResponseDto> data = vwPrParlamentoAndinoService.listarParticipantesUbicacionGeograficaPaginado(filtroParticipanteDto,pagina,tamanio);
		if(data.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setMessage("Se ejecutó correctamente la operación");
			genericResponse.setData(data.get());
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@GetMapping("/organizacion-politica")
	public ResponseEntity<GenericResponse<List<OrganizacionPoliticaDto>>> listaOrganizacionPolitica() {
		GenericResponse<List<OrganizacionPoliticaDto>> genericResponse = new GenericResponse<>();
		List<OrganizacionPoliticaDto> lstParticipanteDto = vwPrParlamentoAndinoService.listarOrganizacionPolitica();
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/participantes-realizar-busqueda")
	public ResponseEntity<GenericResponse<List<ParticipanteCandidatoParlamentoAndinoDto>>> listaParticipantesPorBusqueda(@ModelAttribute @Valid FiltroParticipanteParlamentoAndinoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteCandidatoParlamentoAndinoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoParlamentoAndinoDto> lstParticipanteDto = vwPrParlamentoAndinoService.listarParticipantesPorOrganizacionPoliticaNombreCandidato(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			Document matchFieldsParlamento = new Document();
			matchFieldsParlamento.append("c_tipo_filtro", filtroParticipanteDto.getTipoFiltro());
			matchFieldsParlamento.append("n_tipo_eleccion", filtroParticipanteDto.getIdEleccion());

			addIfNotNull(matchFieldsParlamento, "n_ambito_geografico", filtroParticipanteDto.getIdAmbitoGeografico());
			addIfNotNull(matchFieldsParlamento, "n_ubigeo_nivel_01", filtroParticipanteDto.getUbigeoNivel1());
			addIfNotNull(matchFieldsParlamento, "n_ubigeo_nivel_02", filtroParticipanteDto.getUbigeoNivel2());
			addIfNotNull(matchFieldsParlamento, "n_ubigeo_nivel_03", filtroParticipanteDto.getUbigeoNivel3());

			Document matchInicial = new Document("$match", matchFieldsParlamento);

			Document unwindDetalle = new Document("$unwind", "$c_detalle");
			Document matchAgrupacion = new Document("$match", new Document()
				.append("c_detalle.n_agrupacion_politica", filtroParticipanteDto.getIdAgrupacionPolitica())
			);
			Document project = new Document("$project", new Document()
				.append("_id", 0)
				.append("totalVotosPorOP", "$c_detalle.n_votos")
				.append("porcentajeVotoValido", "$c_detalle.n_porcentaje_votos_validos")
				.append("porcentajeVotoEmitido", "$c_detalle.n_porcentaje_votos_emitidos")
			);
			List<Document> pipeline = Arrays.asList(
				matchInicial,
				unwindDetalle,
				matchAgrupacion,
				project
			);
			Document resultado = mongoTemplate
				.getCollection("vw_pr_parlamento_andino")
				.aggregate(pipeline)
				.first();
			if(resultado != null) {
				genericResponse.setTotalVotosPorOP(resultado.getInteger("totalVotosPorOP"));
				genericResponse.setPorcentajeVotoValido(resultado.getDouble("porcentajeVotoValido"));
				genericResponse.setPorcentajeVotoEmitido(resultado.getDouble("porcentajeVotoEmitido"));
			}
			genericResponse.setData(lstParticipanteDto);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

}
