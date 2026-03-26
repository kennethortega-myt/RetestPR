package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeEleccionService;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.presentacionbackend.model.dto.FiltroProcesoAmbitoDto;
import pe.gob.onpe.presentacionbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.EleccionesMenuResponse;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.model.dto.response.ProcesoElectoralActivoResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/proceso")
@RequiredArgsConstructor
public class ProcesoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesoController.class);

    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final MaeEleccionService maeEleccionService;
    
    private static final String MSG_EXITO = "Se ejecutó correctamente la operación";

    @GetMapping("/")
    public ResponseEntity<GenericResponse<List<MaeProcesoElectoral>>> listProcesos() {
        GenericResponse<List<MaeProcesoElectoral>> genericResponse = new GenericResponse<>();

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.maeProcesoElectoralService.findAll());

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/proceso-electoral-activo")
    public ResponseEntity<GenericResponse<ProcesoElectoralActivoResponse>> obtenerProcesoActivo() {
        GenericResponse<ProcesoElectoralActivoResponse> genericResponse = new GenericResponse<>();

        ProcesoElectoralActivoResponse proceso = this.maeProcesoElectoralService.findByActivo();

        LocalDate fechaProceso = proceso.getFechaProceso()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        LocalDate hoy = LocalDate.now();

        long diasDiferencia = ChronoUnit.DAYS.between(hoy, fechaProceso);

        boolean activoPorFecha = diasDiferencia <= 2;

        proceso.setActivoFechaProceso(activoPorFecha);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(proceso);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/elecciones")
    public ResponseEntity<GenericResponse<List<EleccionesMenuResponse>>> listEleccionesByProceso(@PathVariable("id") Long id) {
        GenericResponse<List<EleccionesMenuResponse>> genericResponse = new GenericResponse<>();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.maeEleccionService.findEleccionesByProceso(id, 1));
        genericResponse.setMessage(MSG_EXITO);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    
    @GetMapping("/tipo-ambito-por-acronimo/")
    public ResponseEntity<GenericResponse<ProcesoAmbitoDto>> getTipoAmbito(@ModelAttribute FiltroProcesoAmbitoDto filtroProcesoAmbitoDto) {
        GenericResponse<ProcesoAmbitoDto> genericResponse = new GenericResponse<>();
        String acronimo = filtroProcesoAmbitoDto.getAcronimo();
        ProcesoAmbitoDto procesoAmb = this.maeProcesoElectoralService.getTipoAmbito(acronimo);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/{id-proceso}/tipo-ambito/")
    public ResponseEntity<GenericResponse<ProcesoAmbitoDto>> getTipoAmbitoPorIdProceso(@PathVariable("id-proceso") Long id) {
        GenericResponse<ProcesoAmbitoDto> genericResponse = new GenericResponse<>();
        Long idProceso = id;
        ProcesoAmbitoDto procesoAmb = this.maeProcesoElectoralService.getTipoAmbitoPorIdProceso(idProceso);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
}
