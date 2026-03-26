package pe.gob.onpe.consultaopbackend.rest.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.exception.NotFoundException;
import pe.gob.onpe.consultaopbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.service.ActaService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabArchivoService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.*;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.nfs.FileApp;
import pe.gob.onpe.consultaopbackend.nfs.NfsService;
import pe.gob.onpe.consultaopbackend.security.TokenDecoder;
import pe.gob.onpe.consultaopbackend.utils.PrConstantes;

import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/actas")
@RequiredArgsConstructor
public class ActaController {

	public static final String SE_EJECUTO_CORRECTAMENTE_LA_OPERACION = "Se ejecutó correctamente la operación";
	private final ActaService actaService;
	private final TabArchivoService tabArchivoService;
	private final NfsService nfsService;
	private final TokenDecoder tokenDecoder;

	/**
	 * Listado de actas páginado por 18 registros //Filtra por ámbito, n_eleccion, n_ambito_geografico, n_ubigeo, c_codigo_local_votacion
	 * @param filtro
	 * @return pendientes, observadas, contabilizadas (codigo del estado), total registros // n_mesa, c_codigo_mesa, c_estado_acta
	 */
	@PostMapping("/")
	public ResponseEntity<GenericResponse<ActaPaginaResponseDto>> obtenerActas(@RequestBody ActaReqDto filtro, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanio, HttpServletRequest request) {
		// Extract Authorization header
        String authorizationHeader = request.getHeader(PrConstantes.AUTHORIZATION_HEADER);
        String codigoOp = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PrConstantes.BEARER_TOKEN_PREFIX)) {
            String token = authorizationHeader.substring(PrConstantes.LENGTH_BEARER); // Assuming LENGTH_BEARER is the length of "Bearer "
            Claims claims = this.tokenDecoder.decodeToken(token);
            codigoOp = claims.get("codigoOp", String.class);
        }
		
		GenericResponse<ActaPaginaResponseDto> genericResponse = new GenericResponse<>();
		Optional<ActaPaginaResponseDto> d = actaService.obtenerActas(filtro,codigoOp,pagina,tamanio);
		if (d.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
	    	genericResponse.setMessage(SE_EJECUTO_CORRECTAMENTE_LA_OPERACION);
	    	genericResponse.setData(d.get());
	    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/observadas")
	public ResponseEntity<GenericResponse<ActaPaginaResponseDto>> obtenerActasO(@RequestBody ActaRequestDto filtro, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanio) {
		GenericResponse<ActaPaginaResponseDto> genericResponse = new GenericResponse<>();
		Optional<ActaPaginaResponseDto> d = actaService.obtenerActasObservadas(filtro,pagina,tamanio);
		if(d.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
	    	genericResponse.setMessage(SE_EJECUTO_CORRECTAMENTE_LA_OPERACION);
	    	genericResponse.setData(d.get());
	    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	
	}
	
	@PostMapping("/resumen-observadas")
	public ResponseEntity<GenericResponse<ResumenActasObservadasResDto>> obtenerResumenActasObservadas(@RequestBody ResumenActasObservadasReqDto filtro) {
		GenericResponse<ResumenActasObservadasResDto> genericResponse = new GenericResponse<>();
		Optional<ResumenActasObservadasResDto> d = actaService.obtenerResumenActasObservadas(filtro);
		if(d.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
	    	genericResponse.setMessage(SE_EJECUTO_CORRECTAMENTE_LA_OPERACION);
	    	genericResponse.setData(d.get());
	    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	
	}
	
	/**
	 * Filtra por Mesa de sufrágio
	 * @return
	 */
	@PostMapping("/buscar/mesa")
	public ResponseEntity<GenericResponse<List<ActasResponseDto>>> obtenerActasMesa(@RequestBody ActaMesaRequestDto filtro){
		GenericResponse<List<ActasResponseDto>> genericResponse = new GenericResponse<>();
		List<ActasResponseDto> lstActas = actaService.obtenerActaMesa(filtro);
		if(!lstActas.isEmpty()) {
			genericResponse.setSuccess(Boolean.TRUE);
    		genericResponse.setMessage(SE_EJECUTO_CORRECTAMENTE_LA_OPERACION);
    		genericResponse.setData(lstActas);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<GenericResponse<ActaResponseDto>> obtenerActaLocales(@PathVariable("id") Long id) {
		GenericResponse<ActaResponseDto> genericResponse = new GenericResponse<>();
    	genericResponse.setSuccess(Boolean.TRUE);
    	genericResponse.setMessage(SE_EJECUTO_CORRECTAMENTE_LA_OPERACION);
    	genericResponse.setData(actaService.obtenerActaPorId(id));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/agrupacion/{idAgru}/candidatos")
	public ResponseEntity<GenericResponse<List<ActaAgrupacionCandidatoRes>>> listarCandidatos(@PathVariable("id") Long id, @PathVariable("idAgru") Long idAgru){
		GenericResponse<List<ActaAgrupacionCandidatoRes>> genericResponse = new GenericResponse<>();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(actaService.listarCandidatos(id, idAgru));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
	@GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam("id") String actaId) throws Exception {
        try {
            Optional<TabArchivo> oTabArchivo = this.tabArchivoService.getArchivoById(actaId);
			if (oTabArchivo.isEmpty()) {
				throw new NotFoundException("File not found");
			}
            TabArchivo tabArchivo = oTabArchivo.get();
            FileApp file = this.nfsService.downloadActa(tabArchivo.getCNombreOriginal());
            String contentType = tabArchivo.getCFormato();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + tabArchivo.getCNombreOriginal() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file.getFile());
        } catch (NotFoundException nfe) {
        	throw new NotFoundException("Archivo no encontrado " + nfe.getMessage());
        }
    }

}
