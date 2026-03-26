package pe.gob.onpe.consultaopbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteCronService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabReporteAutomaticoService;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico.TabReporteAutomaticoResDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/reporte-automatico")
@RequiredArgsConstructor
public class TabReporteAutomaticoController {
	
	private final TabReporteAutomaticoService tabReporteAutomaticoService;
	private final ReporteCronService reporteCronService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<List<TabReporteAutomaticoResDto>>> get() {
		GenericResponse<List<TabReporteAutomaticoResDto>> genericResponse = new GenericResponse<>();
        try {
            List<TabReporteAutomaticoResDto> lstTabReporteAutomatico = this.tabReporteAutomaticoService.obtenerTodos();
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
	public ResponseEntity<GenericResponse<TabReporteAutomaticoResDto>> getById(@PathVariable("id") String id) {
		GenericResponse<TabReporteAutomaticoResDto> genericResponse = new GenericResponse<>();
		TabReporteAutomaticoResDto tabReporteAutomatico = this.tabReporteAutomaticoService.obtenerPorId(id);
		if (tabReporteAutomatico == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else  {
			genericResponse.setData(tabReporteAutomatico);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}		
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<TabReporteAutomaticoResDto>> create(@RequestBody TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto) {
		GenericResponse<TabReporteAutomaticoResDto> genericResponse = new GenericResponse<>();
		TabReporteAutomaticoResDto tabReporteAutomaticoResDto = this.tabReporteAutomaticoService.crear(tabReporteAutomaticoReqDto);
		
		if(tabReporteAutomaticoResDto == null) {
			return new ResponseEntity<>(genericResponse, HttpStatus.NOT_MODIFIED);
		} else {
			genericResponse.setData(tabReporteAutomaticoResDto);
			genericResponse.setSuccess(true);
			return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
		}
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<TabReporteAutomaticoResDto>> update(@PathVariable("id") String id, @RequestBody TabReporteAutomaticoReqDto tabReporteAutomaticoReqDto) {
		GenericResponse<TabReporteAutomaticoResDto> genericResponse = new GenericResponse<>();
		TabReporteAutomaticoResDto tabReporteAutomaticoResDto = this.tabReporteAutomaticoService.actualizar(id, tabReporteAutomaticoReqDto);
		genericResponse.setData(tabReporteAutomaticoResDto);
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/generar-reporte/{id}")
	public ResponseEntity<ReporteCronResponse> generarReporte(
			@PathVariable("id") String id) {
		return reporteCronService.generarReporteManual(id);
	}
	

}
