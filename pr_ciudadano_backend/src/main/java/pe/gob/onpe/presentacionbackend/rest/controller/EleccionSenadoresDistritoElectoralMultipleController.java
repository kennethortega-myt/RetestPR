package pe.gob.onpe.presentacionbackend.rest.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrSenadoresDistritoElectoralMultipleService;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.OrganizacionPoliticaDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.RequestParticipanteSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteCandidatoSenadorDistritoMultipleDto;
import pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple.ResponseParticipanteSenadorDistritoMultipleDto;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senadores-distrital-multiple")
public class EleccionSenadoresDistritoElectoralMultipleController {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private VwPrSenadoresDistritoElectoralMultipleService vwPrSenadoresDistritoElectoralMultipleService;
	
	
	public EleccionSenadoresDistritoElectoralMultipleController(VwPrSenadoresDistritoElectoralMultipleService vwPrSenadoresDistritoElectoralMultipleService) {
		this.vwPrSenadoresDistritoElectoralMultipleService = vwPrSenadoresDistritoElectoralMultipleService;
	}
	
	@GetMapping("/participantes-ubicacion-geografica")
	public ResponseEntity<GenericResponse<List<ResponseParticipanteSenadorDistritoMultipleDto>>> listaParticipantesUbicacionGeograficaNombre(@ModelAttribute @Valid RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteDto) {
		GenericResponse<List<ResponseParticipanteSenadorDistritoMultipleDto>> genericResponse = new GenericResponse<>();
		List<ResponseParticipanteSenadorDistritoMultipleDto> lstParticipanteDto = this.vwPrSenadoresDistritoElectoralMultipleService.listaParticipantesUbicacionGeograficaNombre(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(lstParticipanteDto);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-candidato")
	public ResponseEntity<GenericResponse<List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto>>> listaParticipantesCandidato(@ModelAttribute @Valid RequestParticipanteCandidatoSenadorDistritoMultipleDto filtroParticipanteCandidatoDto) {
		GenericResponse<List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto>> genericResponse = new GenericResponse<>();
		List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> lstParticipanteDto = this.vwPrSenadoresDistritoElectoralMultipleService.listaParticipantesCandidato(filtroParticipanteCandidatoDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> filtrados = lstParticipanteDto.stream().filter(p -> p.getTotalVotosValidos() > 0).toList();
			if (filtrados.size() > 56) {
            	filtrados = filtrados.subList(0, 56);
         	}																
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(filtrados);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-candidato-organizacion")
	public ResponseEntity<GenericResponse<List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto>>> listaParticipantesCandidatoOrganizacion(@ModelAttribute @Valid RequestParticipanteCandidatoOrganizacionSenadorDistritoMultipleDto filtroParticipanteCandidatoNombreDto) {
		GenericResponse<List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto>> genericResponse = new GenericResponse<>();
		List<ResponseParticipanteCandidatoSenadorDistritoMultipleDto> lstParticipanteDto = this.vwPrSenadoresDistritoElectoralMultipleService.listaParticipantesCandidatoOrganizacion(filtroParticipanteCandidatoNombreDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);

			Document matchInicial = new Document("$match", new Document()
				.append("c_tipo_filtro", filtroParticipanteCandidatoNombreDto.getTipoFiltro())
				.append("n_tipo_eleccion", filtroParticipanteCandidatoNombreDto.getIdEleccion())
				.append("n_distrito_electoral", filtroParticipanteCandidatoNombreDto.getIdDistritoElectoral())
    		);
			Document unwindDetalle = new Document("$unwind", "$c_detalle");
			Document matchAgrupacion = new Document("$match", new Document()
				.append("c_detalle.n_agrupacion_politica", filtroParticipanteCandidatoNombreDto.getIdAgrupacionPolitica())
			);
			Document project = new Document("$project", new Document()
				.append("_id", 0)
				.append("totalVotosPorOP", "$c_detalle.n_votos")
				.append("porcentajeVotoEmitido", "$c_detalle.n_porcentaje_votos_emitidos")
				.append("porcentajeVotoValido", "$c_detalle.n_porcentaje_votos_validos")
			);
			List<Document> pipeline = Arrays.asList(
				matchInicial,
				unwindDetalle,
				matchAgrupacion,
				project
			);
			Document resultado = mongoTemplate
				.getCollection("vw_pr_senadores_distrito_multiple")
				.aggregate(pipeline)
				.first();
			if(resultado != null) {
				genericResponse.setTotalVotosPorOP(resultado.getInteger("totalVotosPorOP"));
				genericResponse.setPorcentajeVotoEmitido(resultado.getDouble("porcentajeVotoEmitido"));
				genericResponse.setPorcentajeVotoValido(resultado.getDouble("porcentajeVotoValido"));
			}
			genericResponse.setData(lstParticipanteDto);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/organizacion-politica")
	public ResponseEntity<GenericResponse<List<OrganizacionPoliticaDto>>> listaOrganizacionPolitica(@ModelAttribute RequestParticipanteSenadorDistritoMultipleDto filtroParticipanteDto) {
		GenericResponse<List<OrganizacionPoliticaDto>> genericResponse = new GenericResponse<>();
		List<OrganizacionPoliticaDto> lstParticipanteDto = vwPrSenadoresDistritoElectoralMultipleService.listarOrganizacionPolitica(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
}
