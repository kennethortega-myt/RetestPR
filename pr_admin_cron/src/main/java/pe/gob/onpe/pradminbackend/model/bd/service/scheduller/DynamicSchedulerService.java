package pe.gob.onpe.pradminbackend.model.bd.service.scheduller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.pradminbackend.config.MongoIndexConfig;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.repository.*;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteAutomaticoRepository;
import pe.gob.onpe.pradminbackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private static final String ZONA_HORARIA = "America/Lima";
    private static final String ELECCION = "eleccion";
    private final TabReporteAutomaticoRepository tabProgramacionReporteRepository;
    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

    private final TaskScheduler taskScheduler;
    private final List<ScheduledFuture<?>> scheduledFutures;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final JwtTokenFactory jwtTokenFactory;
    private final MongoIndexConfig mongoIndexConfig;

    @Value("${admin.ciudadano.backend.url}")
    private String adminCiudadanoBackendUrl;

    public void scheduleTasksFromDatabase() {
        mongoIndexConfig.ensureIndexes();
        List<TabReporteAutomatico> taskConfigs = tabProgramacionReporteRepository.findByEstado(1);
        if (!taskConfigs.isEmpty()) {
            if (scheduledFutures != null) {
                log.info("Cancelando {} tareas programadas existentes.", scheduledFutures.size());
                scheduledFutures.forEach(future -> future.cancel(false));
            }
            taskConfigs.forEach(this::ejecutarGeneracionReportePorPorcentaje);
        } else {
            log.info("NO EXISTEN CONFIGURACIONES EN BD REPORTE AUTOMATICO");
        }
    }

    private void scheduleTaskWithCron(TabReporteAutomatico taskConfig) {
        executeWithCurrentValidation(taskConfig);
    }

    private void executeWithCurrentValidation(TabReporteAutomatico taskConfig) {

        if (taskConfig.getEstadoProceso().intValue() ==
                TipoEstadoProcesoEnum.PENDIENTE.getCodigo().intValue()) {

            Runnable task = () -> callGenerateReportEndpoint(taskConfig);

            if (validarEjecucionReporte(taskConfig)) {
                if (taskConfig.getTipoGeneracionReporte() == 1) { // Reporte por tiempo (cron)
                    log.info("Programando tarea por CRON para la elección '{}' con expresión: {}", taskConfig.getEleccion(), taskConfig.getExpresionCron());
                    CronTrigger cronTrigger = new CronTrigger(taskConfig.getExpresionCron(), ZoneId.of(ZONA_HORARIA));
                    scheduledFutures.add(taskScheduler.schedule(task, cronTrigger));
                } else if (taskConfig.getTipoGeneracionReporte() == 2) { // Reporte por porcentaje (intervalo)
                    Duration intervalo = Duration.ofMinutes(taskConfig.getTiempoConsultaReporte());
                    log.info("Programando tarea por INTERVALO para la elección '{}' cada {} minutos.", taskConfig.getEleccion(), taskConfig.getTiempoConsultaReporte());
                    scheduledFutures.add(taskScheduler.scheduleAtFixedRate(task, intervalo));
                }
            }

        } else {
            log.info("El proceso sigue EN_CURSO");
        }

    }

    private void callGenerateReportEndpoint(TabReporteAutomatico taskConfig) {
        String url = adminCiudadanoBackendUrl + "/reporte-ciudadano/generarreporte";
        log.info("EJECUTANDO TAREA: Llamando al endpoint de generación de reporte para la elección '{}'", taskConfig.getEleccion());

        try {
            // Generar un nuevo JWT de servicio para cada llamada
            String jwtToken = jwtTokenFactory.createServiceToServerToken();

            String requestBody = objectMapper.writeValueAsString(taskConfig);

            // Log del comando cURL equivalente para depuración
            String curlEquivalent = String.format("cURL equivalent: curl -X POST '%s' -H 'Content-Type: application/json' -H 'Authorization: Bearer %s' -d '%s'", url, jwtToken, requestBody);
            log.info(curlEquivalent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Llamada al endpoint exitosa para la elección '{}': {}", taskConfig.getEleccion(), response.getBody());
            } else {
                log.error("Error en la llamada al endpoint para la elección '{}': Código de estado {}", taskConfig.getEleccion(), response.getStatusCode());
            }

        } catch (JsonProcessingException e) {
            log.error("Error al serializar el objeto TabReporteAutomatico a JSON para la elección '{}': {}", taskConfig.getEleccion(), e.getMessage());
        } catch (HttpClientErrorException e) {
            log.error("Error de cliente (4xx) al llamar al endpoint para la elección '{}': {} - {}", taskConfig.getEleccion(), e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("Error de servidor (5xx) al llamar al endpoint para la elección '{}': {} - {}", taskConfig.getEleccion(), e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Ocurrió un error inesperado al llamar al endpoint para la elección '{}': {}", taskConfig.getEleccion(), e.getMessage());
        }
    }

    private void ejecutarGeneracionReportePorPorcentaje(TabReporteAutomatico taskConfig) {
        double porcentajeContabilizadas = obtenerPorcentageContabilizado(taskConfig.getEleccionId());

        if (esFechaValida(taskConfig) && porcentajeContabilizadas > 0) {
            log.info("Cumple validación de fecha, hora y porcentaje. Procediendo a programar la tarea para la elección {}", taskConfig.getEleccionId());
            scheduleTaskWithCron(taskConfig);
        } else {
            log.info("Elección {} no cumple con la condición para programar la tarea. Porcentaje contabilizado: {}", taskConfig.getEleccionId(), porcentajeContabilizadas);
        }
    }

    private boolean esFechaValida(TabReporteAutomatico taskConfig) {
        if (taskConfig.getFechaInicio() == null || taskConfig.getHoraInicio() == null) {
            return false;
        }
        LocalDateTime momentoProgramado = LocalDateTime.of(
                taskConfig.getFechaInicio(),
                taskConfig.getHoraInicio()
        );
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.isBefore(momentoProgramado);
    }

    private double obtenerPorcentageContabilizado(Integer idEleccion) {
        if (idEleccion == null) return 0.0;
        String tipoEleccion = TipoEleccionEnum.obtenerDescripcion(idEleccion.longValue());

        switch (tipoEleccion) {
            case "Presidencial":
                return vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrPresidenciales::getPorcentajeActasContabilizadas).orElse(0.0);
            case "Diputados":
                return vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrDiputados::getPorcentajeActasContabilizadas).orElse(0.0);
            case "Parlamento Andino":
                return vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrParlamentoAndino::getPorcentajeActasContabilizadas).orElse(0.0);
            case "Senadores 27":
                return vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrSenadoresDistritoElectoralMultiple::getPorcentajeActasContabilizadas).orElse(0.0);
            case "Senadores 33":
                return vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrSenadoresDistritoNacionalUnico::getPorcentajeActasContabilizadas).orElse(0.0);
            case "Revocatoria Distrital":
                return vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION)
                        .stream().findFirst().map(VwPrRevocatoriaDistrital::getPorcentajeActasContabilizadas).orElse(0.0);
            default:
                return 0.0;
        }
    }

    private boolean validarEjecucionReporte(TabReporteAutomatico taskConfig) {
        Integer electionType = taskConfig.getEleccionId();
        boolean typeElectionPermitted = (electionType == 10 || electionType == 13 || electionType == 12 || electionType == 14 || electionType == 15);

        if (!typeElectionPermitted) {
            log.info("Reporte no mapeado para su ejecución: " + electionType);
        }
        return typeElectionPermitted;
    }
}
