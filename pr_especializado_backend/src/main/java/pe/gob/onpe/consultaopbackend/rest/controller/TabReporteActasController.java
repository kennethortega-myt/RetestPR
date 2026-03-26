package pe.gob.onpe.consultaopbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ProcesamientoActasService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteActasService;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteactas.TabReporteActasResDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/reporte-actas")
@RequiredArgsConstructor
public class TabReporteActasController {
	
	private final TabReporteActasService tabReporteActasService;
	private final ProcesamientoActasService procesamientoActasService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<List<TabReporteActasResDto>>> get() {
		GenericResponse<List<TabReporteActasResDto>> genericResponse = new GenericResponse<>();
        try {
            List<TabReporteActasResDto> lstTabReporteAutomatico = this.tabReporteActasService.obtenerTodos();
            if(lstTabReporteAutomatico.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                genericResponse.setData(lstTabReporteAutomatico);
                genericResponse.setSuccess(Boolean.TRUE);
                genericResponse.setMessage("Se ejecutó correctamente la operación");
                return new ResponseEntity<>(genericResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Error al obtener reportes");
            return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<TabReporteActasResDto>> getById(@PathVariable("id") String id) {
		GenericResponse<TabReporteActasResDto> genericResponse = new GenericResponse<>();
        TabReporteActasResDto tabReporteActasResDto = this.tabReporteActasService.obtenerPorId(id);
		if (tabReporteActasResDto == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else  {
			genericResponse.setData(tabReporteActasResDto);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}		
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<TabReporteActasResDto>> create(
            @RequestBody TabReporteActasReqDto tabReporteActasReqDto) {
		GenericResponse<TabReporteActasResDto> genericResponse = new GenericResponse<>();
        TabReporteActasResDto tabReporteActasResDto = this.tabReporteActasService.crear(tabReporteActasReqDto);
		
		if(tabReporteActasResDto == null) {
			return new ResponseEntity<>(genericResponse, HttpStatus.NOT_MODIFIED);
		} else {
			genericResponse.setData(tabReporteActasResDto);
			genericResponse.setSuccess(true);
			return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
		}
	}

	@PostMapping(value = "/actualizar",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<TabReporteActasResDto>> update(
            @RequestBody TabReporteActasReqDto tabReporteActasReqDto) {
		GenericResponse<TabReporteActasResDto> genericResponse = new GenericResponse<>();
        TabReporteActasResDto tabReporteActasResDto = this.tabReporteActasService.actualizar(tabReporteActasReqDto);
		genericResponse.setData(tabReporteActasResDto);
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/generar-reporte/{id}")
	public ResponseEntity<ReporteCronResponse> generarReporte(
			@PathVariable("id") String id) {
		return procesamientoActasService.generarReporteManual(id);
	}

}
