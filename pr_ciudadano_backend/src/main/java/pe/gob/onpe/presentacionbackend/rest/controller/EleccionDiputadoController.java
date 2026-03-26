package pe.gob.onpe.presentacionbackend.rest.controller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrDiputadosService;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.FiltroParticipanteNombreDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.OrganizacionPoliticaDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteCandidatoDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados.ParticipanteDiputadoDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/eleccion-diputado")
public class EleccionDiputadoController {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private VwPrDiputadosService vwPrDiputadosService;
	
	public EleccionDiputadoController(VwPrDiputadosService vwPrDiputadosService) {
		super();
		this.vwPrDiputadosService = vwPrDiputadosService;
	}

	@GetMapping("/participantes-ubicacion-geografica")
	public ResponseEntity<GenericResponse<List<ParticipanteDiputadoDto>>> listaParticipantes(@ModelAttribute FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteDiputadoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteDiputadoDto> lstParticipanteDto = vwPrDiputadosService.listarParticipantesUbicacionGeografica(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-ubicacion-geografica-nombre")
	public ResponseEntity<GenericResponse<List<ParticipanteDiputadoDto>>> listaParticipantesUbicacionGeograficaNombre(@ModelAttribute FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteDiputadoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteDiputadoDto> lstParticipanteDto = vwPrDiputadosService.listarParticipantesUbicacionGeograficaNombre(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-por-candidato")
	public ResponseEntity<GenericResponse< List<ParticipanteCandidatoDiputadoDto>>> obtenerParticipantes(@ModelAttribute FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		GenericResponse<List<ParticipanteCandidatoDiputadoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoDiputadoDto> lstParticipanteDto = vwPrDiputadosService.obtenerParticipantes(filtroParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			List<ParticipanteCandidatoDiputadoDto> filtrados = lstParticipanteDto.stream().filter(p -> p.getTotalVotosValidos() > 0).toList();
			if (filtrados.size() > 56) {
            	filtrados = filtrados.subList(0, 56);
         	}	
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setData(filtrados);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes-por-candidato-nombre")
	public ResponseEntity<GenericResponse< List<ParticipanteCandidatoDiputadoDto>>> obtenerParticipantesNombre(@ModelAttribute FiltroParticipanteNombreDiputadoDto filtroParticipanteNombreDto) {
		GenericResponse<List<ParticipanteCandidatoDiputadoDto>> genericResponse = new GenericResponse<>();
		List<ParticipanteCandidatoDiputadoDto> lstParticipanteDto = vwPrDiputadosService.obtenerParticipantesNombre(filtroParticipanteNombreDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);

			Document matchInicial = new Document("$match", new Document()
				.append("c_tipo_filtro", filtroParticipanteNombreDto.getTipoFiltro())
				.append("n_tipo_eleccion", filtroParticipanteNombreDto.getIdEleccion())
				.append("n_distrito_electoral", filtroParticipanteNombreDto.getIdDistritoElectoral())
    		);
			Document unwindDetalle = new Document("$unwind", "$c_detalle");
			Document matchAgrupacion = new Document("$match", new Document()
				.append("c_detalle.n_agrupacion_politica", filtroParticipanteNombreDto.getIdAgrupacionPolitica())
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
				.getCollection("vw_pr_diputados")
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
	public ResponseEntity<GenericResponse<List<OrganizacionPoliticaDto>>> listaOrganizacionPolitica(@ModelAttribute FiltroParticipanteDiputadoDto filtroParticipanteDto) {
		GenericResponse<List<OrganizacionPoliticaDto>> genericResponse = new GenericResponse<>();
		List<OrganizacionPoliticaDto> lstParticipanteDto = vwPrDiputadosService.listarOrganizacionPolitica(filtroParticipanteDto);
		genericResponse.setData(lstParticipanteDto);
		if(lstParticipanteDto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
}
