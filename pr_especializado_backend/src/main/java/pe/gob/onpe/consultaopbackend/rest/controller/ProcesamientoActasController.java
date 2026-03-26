package pe.gob.onpe.consultaopbackend.rest.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ProcesamientoActasService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.TramaScePuestaCeroDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/procesamientoActas")
@Slf4j
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"})
public class ProcesamientoActasController {

    private final ProcesamientoActasService procesamientoActasService;

    @GetMapping(value = "/procesar/{idConfiguracion}")
    public ResponseEntity<ReporteCronResponse> procesarActas(
            @NotBlank(message = "idConfiguracion es obligatorio")
            @PathVariable("idConfiguracion") String idConfiguracion) {

        log.info("=== ENDPOINT: Procesar Actas ===");
        log.info("ID Configuración: {}", idConfiguracion);

        return procesamientoActasService.procesarActas(idConfiguracion);
    }

    /**
     * Endpoint para descargar un ZIP específico
     *
     * @param tipoEleccion Tipo de elección (ej: "Presidencial")
     * @param region Región (ej: "LIMA")
     * @return Archivo ZIP para descarga
     */
    @GetMapping("/descargarZip")
    public ResponseEntity<Resource> descargarZip(
            @RequestParam String tipoEleccion,
            @RequestParam String region) {

        log.info("Descargando ZIP: {} - {}", tipoEleccion, region);

        Resource resource = procesamientoActasService.obtenerZipParaDescarga(tipoEleccion, region);

        if (resource == null || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String nombreArchivo = tipoEleccion + "_" + region + ".zip";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(resource);
    }

    /**
     * Endpoint para listar ZIPs disponibles
     *
     * @param tipoEleccion (Opcional) Tipo de elección para filtrar.
     *                     Si no se proporciona, lista ZIPs de todos los tipos.
     * @return Lista de ZIPs disponibles agrupados por tipo de elección y región
     */
    @GetMapping("/listarZips")
    public ResponseEntity<Map<String, List<Map<String, String>>>> listarZips(
            @RequestParam(required = false) String tipoEleccion) {

        if (tipoEleccion != null && !tipoEleccion.isEmpty()) {
            log.info("Listando ZIPs para tipo de elección: {}", tipoEleccion);
        } else {
            log.info("Listando ZIPs para todos los tipos de elección");
        }

        Map<String, List<Map<String, String>>> zips = procesamientoActasService
                .listarZipsDisponibles(tipoEleccion);

        return ResponseEntity.ok(zips);
    }

    @PostMapping(value = "/eliminarCarpetaDescargaActas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TramaScePuestaCeroDto> eliminarCarpetaDescargaActas() {
        log.info("=== ENDPOINT: Eliminar Carpeta Descarga Actas ===");
        return procesamientoActasService.eliminarCarpetaDescargaActas();
    }

}