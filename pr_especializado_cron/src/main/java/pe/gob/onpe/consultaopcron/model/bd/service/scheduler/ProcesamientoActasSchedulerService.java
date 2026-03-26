package pe.gob.onpe.consultaopcron.model.bd.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteActa;
import pe.gob.onpe.consultaopcron.model.bd.repository.secondary.TabProgramacionReporteActasRepository;
import pe.gob.onpe.consultaopcron.model.bd.service.ProcesamientoActasService;
import pe.gob.onpe.consultaopcron.model.bd.service.ValidacionCronService;
import pe.gob.onpe.consultaopcron.utils.enums.TipoEstadoProcesoEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Servicio para programar tareas automáticas de procesamiento de actas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcesamientoActasSchedulerService {

    private static final String ZONA_HORARIA = "America/Lima";

    private final TabProgramacionReporteActasRepository repository;
    private final TaskScheduler taskScheduler;
    private final ProcesamientoActasService procesamientoActasService;
    private final ValidacionCronService validacionCronService;

    /**
     * Lista de tareas programadas
     */
    private final List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();

    /**
     * Programa tareas desde la base de datos
     * Este método se invoca periódicamente para recargar configuraciones
     */
    public void scheduleTasksFromDatabase() {

        try {
            // Obtener configuraciones activas
            List<TabReporteActa> configs = repository.findByEstado(1);

            if (!configs.isEmpty()) {

                // Cancelar tareas existentes
                if (!scheduledFutures.isEmpty()) {
                    scheduledFutures.forEach(future -> future.cancel(false));
                    scheduledFutures.clear();
                }

                // Programar nuevas tareas
                configs.forEach(this::scheduleTask);

            } else {

                if (!scheduledFutures.isEmpty()) {
                    scheduledFutures.forEach(future -> future.cancel(false));
                    scheduledFutures.clear();
                }
            }

        } catch (Exception e) {
            log.error("❌ Error al recargar configuraciones", e);
        }

    }

    /**
     * Programa una tarea individual
     */
    private void scheduleTask(TabReporteActa config) {

        try {

            if (esFechaValida(config)) {

                ScheduledFuture<?> future = taskScheduler.schedule(
                        () -> ejecutarTarea(config),
                        new CronTrigger(config.getExpresionCron(), ZoneId.of(ZONA_HORARIA)));
                scheduledFutures.add(future);

            }

        } catch (Exception e) {
            log.error("❌ Error al programar tarea para {}", config.getEleccion(), e);
        }
    }

    /**
     * Ejecuta la tarea programada
     */
    private void ejecutarTarea(TabReporteActa config) {

        try {

            if (config.getEstadoProceso().intValue() !=
                    TipoEstadoProcesoEnum.EN_CURSO.getCodigo().intValue()) {
                procesamientoActasService.enviarPeticionProcesarActas(config.getId());
            } else {
                log.info("El proceso sigue EN_CURSO");
            }

        } catch (Exception e) {
            log.error("❌ Error al ejecutar tarea", e);
        }
    }

    /**
     * Obtiene el número de tareas programadas
     */
    public int getTareasProgramadas() {
        return scheduledFutures.size();
    }

    private boolean esFechaValida(TabReporteActa taskConfig) {

        if (taskConfig.getFechaInicio() == null || taskConfig.getHoraInicio() == null) {
            return false;
        }

        // 1. Combinamos la fecha y hora de la configuración
        LocalDateTime momentoProgramado = LocalDateTime.of(
                taskConfig.getFechaInicio(),
                taskConfig.getHoraInicio());

        // 2. Obtenemos el momento actual (fecha + hora)
        LocalDateTime ahora = LocalDateTime.now();

        // 3. Validamos
        // Retorna true si 'ahora' es después o igual a 'momentoProgramado'
        return !ahora.isBefore(momentoProgramado);
    }

}
