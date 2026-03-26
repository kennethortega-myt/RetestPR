package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeProcesoElectoral;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeEleccionService;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.pradminbackend.model.dto.FiltroProcesoAmbitoDto;
import pe.gob.onpe.pradminbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.pradminbackend.model.dto.response.EleccionesMenuResponse;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.model.dto.response.ProcesoElectoralActivoResponse;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/proceso")
@RequiredArgsConstructor
public class ProcesoController {


    private final MaeProcesoElectoralService maeProcesoElectoralService;

    private final MaeEleccionService maeEleccionService;

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

        LocalDate fechaProceso = proceso.getFechaConvocatoria()
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
        genericResponse.setMessage("Se ejecutó correctamente la operación");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


    @PostMapping("/tipo-ambito-por-acronimo/")
    public ResponseEntity<GenericResponse<ProcesoAmbitoDto>> getTipoAmbito(@RequestBody FiltroProcesoAmbitoDto filtroProcesoAmbitoDto) {
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
