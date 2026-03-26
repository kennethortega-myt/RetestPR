package pe.gob.onpe.presentacionbackend.rest.controller;

import static pe.gob.onpe.presentacionbackend.utils.ArchivoUtils.addIfNotNull;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrSenadoresDistritoNacionalUnicoService;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.FiltroParticipanteSenadoresUnicosDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.OrganizacionPoliticaSenadoresDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.ParticipanteCandidatoSenadoresUnicosDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritounico.ParticipanteSenadoresUnicosDto;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/senadores-distrito-unico")
@RequiredArgsConstructor
public class EleccionSenadoresDistritoNacionalUnicoController {

	@Autowired
	private MongoTemplate mongoTemplate;

	private final VwPrSenadoresDistritoNacionalUnicoService vwPrSenadoresUnicosService;

	@GetMapping("/participantes-ubicacion-geografica")
	public ResponseEntity<GenericResponse<List<ParticipanteSenadoresUnicosDto>>> listarParticipantesPorUbicacionGeografica(@ModelAttribute @Valid FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteSenadoresUnicosDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteSenadoresUnicosDto> lstParticipanteDto = vwPrSenadoresUnicosService.listarParticipantesUbicacionGeografica(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-ubicacion-geografica-nombre")
	public ResponseEntity<GenericResponse<List<ParticipanteSenadoresUnicosDto>>> listarParticipantesPorUbicacionGeograficaNombre(@ModelAttribute @Valid FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteSenadoresUnicosDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteSenadoresUnicosDto> lstParticipanteDto = vwPrSenadoresUnicosService.listarParticipantesUbicacionGeograficaNombre(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/participantes-por-candidato")
	public ResponseEntity<GenericResponse<List<ParticipanteCandidatoSenadoresUnicosDto>>> listaParticipantesPorCandidato(@ModelAttribute @Valid FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteCandidatoSenadoresUnicosDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoSenadoresUnicosDto> lstParticipanteDto = vwPrSenadoresUnicosService.listarParticipantesPorCandidato(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {

			List<ParticipanteCandidatoSenadoresUnicosDto> filtrados = lstParticipanteDto.stream().filter(p -> p.getTotalVotosValidos() > 0).toList();
			if (filtrados.size() > 56) {
            	filtrados = filtrados.subList(0, 56);
         	}	
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(filtrados);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}


	@GetMapping("/organizacion-politica")
	public ResponseEntity<GenericResponse<List<OrganizacionPoliticaSenadoresDto>>> listaOrganizacionPolitica() {
		GenericResponse<List<OrganizacionPoliticaSenadoresDto>> genericResponse = new GenericResponse<>();
		List<OrganizacionPoliticaSenadoresDto> lstParticipanteDto = vwPrSenadoresUnicosService.listarOrganizacionPolitica();
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/participantes-realizar-busqueda")
	public ResponseEntity<GenericResponse<List<ParticipanteCandidatoSenadoresUnicosDto>>> listaParticipantesPorBusqueda(@ModelAttribute @Valid FiltroParticipanteSenadoresUnicosDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteCandidatoSenadoresUnicosDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoSenadoresUnicosDto> lstParticipanteDto = vwPrSenadoresUnicosService.listarParticipantesPorOrganizacionPoliticaNombreCandidato(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);

			Document matchFieldsSenadorUnico = new Document();
			matchFieldsSenadorUnico.append("c_tipo_filtro", filtroParticipanteDto.getTipoFiltro());
			matchFieldsSenadorUnico.append("n_tipo_eleccion", filtroParticipanteDto.getIdEleccion());

			addIfNotNull(matchFieldsSenadorUnico, "n_ambito_geografico", filtroParticipanteDto.getIdAmbitoGeografico());
			addIfNotNull(matchFieldsSenadorUnico, "n_ubigeo_nivel_01", filtroParticipanteDto.getUbigeoNivel1());
			addIfNotNull(matchFieldsSenadorUnico, "n_ubigeo_nivel_02", filtroParticipanteDto.getUbigeoNivel2());
			addIfNotNull(matchFieldsSenadorUnico, "n_ubigeo_nivel_03", filtroParticipanteDto.getUbigeoNivel3());

			Document matchInicial = new Document("$match", matchFieldsSenadorUnico);

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
				.getCollection("vw_pr_senadores_distrito_unico")
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
