package pe.gob.onpe.consultaopbackend.rest.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.exception.DownloadActaException;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabReporteCandidato;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteConsultaOPService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabArchivoReporteService;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteAutomaticoRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteHistorialPaginado;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteHistorialRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRequest;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.nfs.FileApp;
import pe.gob.onpe.consultaopbackend.nfs.NfsService;
import pe.gob.onpe.consultaopbackend.security.TokenDecoder;
import pe.gob.onpe.consultaopbackend.utils.PrConstantes;

import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequiredArgsConstructor
@CrossOrigin(
        origins = "*",
        exposedHeaders = {"Content-Disposition"}
)
@RequestMapping("/reportes")
@Slf4j
public class ReporteController {


	public static final String CODIGO_OP = "codigoOp";
	private final ReporteConsultaOPService reporteConsultaOPService;
	private final TabArchivoReporteService tabArchivoReporteService;
	private final NfsService nfsService;
	private final TokenDecoder tokenDecoder;

	@PostMapping(value = "/generar",consumes = MediaType.APPLICATION_JSON_VALUE
			,produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<String>> generarReporteActasGenerales(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION)
			@NotBlank(message = "token es obligatorio") String tokenHeader,
			@RequestBody ReporteRequest request) {

		String token = tokenHeader.substring(PrConstantes.LENGTH_BEARER);
		Claims claims = this.tokenDecoder.decodeToken(token);
		Optional<String> codigoOp = Optional.ofNullable(claims.get(CODIGO_OP, String.class));
		codigoOp.ifPresent(request::setCodigoOp);

		return reporteConsultaOPService.registrarReporteBackground(request);
	}

	@PostMapping(value = "/generarObservadas",consumes = MediaType.APPLICATION_JSON_VALUE
			,produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<String>> generarReporteActasObservadas(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION)
			@NotBlank(message = "token es obligatorio") String tokenHeader,
			@RequestBody ReporteRequest request) {

		String token = tokenHeader.substring(PrConstantes.LENGTH_BEARER);
		Claims claims = this.tokenDecoder.decodeToken(token);
		Optional<String> codigoOp = Optional.ofNullable(claims.get(CODIGO_OP, String.class));
		codigoOp.ifPresent(request::setCodigoUsuario);

		return reporteConsultaOPService.registrarReporteBackground(request);
	}


	@PostMapping("/listar")
	public ResponseEntity<GenericResponse<ReporteHistorialPaginado>> listarReportes(
			@RequestBody ReporteHistorialRequestDto request,
			@RequestHeader(value = HttpHeaders.AUTHORIZATION)
			@NotBlank(message = "token es obligatorio") String tokenHeader,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamanio
	) {

		String token = tokenHeader.substring(PrConstantes.LENGTH_BEARER);
		Claims claims = this.tokenDecoder.decodeToken(token);
		Optional<String> codigoOp = Optional.ofNullable(claims.get(CODIGO_OP, String.class));
		codigoOp.ifPresent(request::setUsuarioConsulta);
		GenericResponse<ReporteHistorialPaginado> genericResponse = new GenericResponse<>();
		ReporteHistorialPaginado reporteHistorialDtoList = reporteConsultaOPService.listarReportesPorUsuario(request, pagina, tamanio);
		genericResponse.setData(reporteHistorialDtoList);
		if(reporteHistorialDtoList == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@PostMapping("/automatico")
	public ResponseEntity<GenericResponse<ReporteHistorialPaginado>> listarReportesAutomatico(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION)
			@NotBlank(message = "token es obligatorio") String tokenHeader,
            @RequestBody ReporteAutomaticoRequestDto request,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamanio
	) {

        String token = tokenHeader.substring(PrConstantes.LENGTH_BEARER);
        Claims claims = this.tokenDecoder.decodeToken(token);
        Optional<String> codigoOp = Optional.ofNullable(claims.get(CODIGO_OP, String.class));
        codigoOp.ifPresent(request::setUsuarioConsulta);

		GenericResponse<ReporteHistorialPaginado> genericResponse = new GenericResponse<>();
		ReporteHistorialPaginado reporteHistorialDtoList = reporteConsultaOPService
                .listarReportesAutomaticos(request, pagina,tamanio);

		genericResponse.setData(reporteHistorialDtoList);
		if(reporteHistorialDtoList == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			genericResponse.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

	@GetMapping("/file")
	public ResponseEntity<byte[]> getFile(@RequestParam("id") String idArchivo) throws Exception {

			Optional<TabArchivo> oTabArchivo = this.tabArchivoReporteService.getArchivoById(idArchivo);
			if (oTabArchivo.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			TabArchivo tabArchivo = oTabArchivo.get();
			FileApp file;
			try{
				file = this.nfsService.download(tabArchivo.getCNombreOriginal());
				if (file == null || file.getFile() == null || file.getFile().length == 0) {
					return ResponseEntity.noContent().build();
				}
			} catch (DownloadActaException e){
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body(e.getMessage().getBytes());
			}

			String contentType = tabArchivo.getCFormato();
			return ResponseEntity.ok()
					.header("Content-Disposition", "attachment; filename=\"" + tabArchivo.getCNombreOriginal() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
					.body(file.getFile());
	}

	@GetMapping("/reporteCandidato")
	public ResponseEntity<?> getFileReporteCandidato(@RequestParam("id") Integer id) throws Exception {

		TabReporteCandidato reporte = tabArchivoReporteService
				.findByIdAndActivo(id, 1)
				.orElse(null);

		if (reporte == null || reporte.getCRuta() == null || reporte.getCRuta().isBlank()) {
			return ResponseEntity.noContent().build();
		}

		Path path = Paths.get(reporte.getCRuta());
		String fileName = path.getFileName().toString();

		if (!fileName.toLowerCase().endsWith(".csv")) {
			return ResponseEntity.noContent().build();
		}

		String moduleFolder = path.getParent().getFileName().toString();
		String pathForFileToDownload = moduleFolder + "/" + fileName;

		FileApp file;
		try{
			file = nfsService.downloadActa(pathForFileToDownload);
			if (file == null || file.getFile() == null || file.getFile().length == 0) {
				return ResponseEntity.noContent().build();
			}
		} catch (DownloadActaException e){
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		}

		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + fileName + "\"")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(file.getFile());
	}

}
