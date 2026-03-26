package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.exception.NotFoundException;
import pe.gob.onpe.presentacionbackend.model.bd.documents.TabArchivo;
import pe.gob.onpe.presentacionbackend.model.bd.service.ActaService;
import pe.gob.onpe.presentacionbackend.model.bd.service.TabArchivoService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.*;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.nfs.FileApp;
import pe.gob.onpe.presentacionbackend.nfs.NfsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/actas")
@RequiredArgsConstructor
public class ActaController {
	
	private final ActaService actaService;
	private final TabArchivoService tabArchivoService;
	private final NfsService nfsService;

	/**
	 * Listado de actas páginado por 18 registros //Filtra por ámbito, n_eleccion, n_ambito_geografico, n_ubigeo, c_codigo_local_votacion
	 * @param filtro
	 * @return pendientes, observadas, contabilizadas (codigo del estado), total registros // n_mesa, c_codigo_mesa, c_estado_acta
	 */
	@GetMapping
	public ResponseEntity<GenericResponse<ActaPaginaResponseDto>> obtenerActas(@ModelAttribute ActaRequestDto filtro, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "18") int tamanio) {
		GenericResponse<ActaPaginaResponseDto> genericResponse = new GenericResponse<>();
		Optional<ActaPaginaResponseDto> d = actaService.obtenerActas(filtro,pagina,tamanio);
		if (d.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
	    	genericResponse.setData(d.get());
	    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/observadas")
	public ResponseEntity<GenericResponse<ActaPaginaResponseDto>> obtenerActasO(@ModelAttribute ActaRequestDto filtro, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "18") int tamanio) {
		GenericResponse<ActaPaginaResponseDto> genericResponse = new GenericResponse<>();
		Optional<ActaPaginaResponseDto> d = actaService.obtenerActasObservadas(filtro,pagina,tamanio);
		if(d.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
	    	genericResponse.setData(d.get());
	    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	
	}
	
	/**
	 * Filtra por Mesa de sufrágio
	 * @return
	 */
	@GetMapping("/buscar/mesa")
	public ResponseEntity<GenericResponse<List<ActasResponseDto>>> obtenerActasMesa(@ModelAttribute ActaMesaRequestDto filtro){
		GenericResponse<List<ActasResponseDto>> genericResponse = new GenericResponse<>();
		List<ActasResponseDto> lstActas = actaService.obtenerActaMesa(filtro);
		if(!lstActas.isEmpty()) {
			genericResponse.setSuccess(Boolean.TRUE);
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
			FileApp file = this.nfsService.download(tabArchivo.getCNombreOriginal());
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
