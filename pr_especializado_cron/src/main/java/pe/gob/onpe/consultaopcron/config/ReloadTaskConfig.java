package pe.gob.onpe.consultaopcron.config;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.gob.onpe.consultaopcron.model.bd.service.scheduler.DynamicSchedulerService;
import pe.gob.onpe.consultaopcron.model.bd.service.scheduler.ProcesamientoActasSchedulerService;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ReloadTaskConfig {

    private final DynamicSchedulerService dynamicSchedulerService;
    private final ProcesamientoActasSchedulerService procesamientoActasSchedulerService;

    // Ejecutar esta tarea cada minuto(60000 milisegundos), por ejemplo, para revisar cambios en la configuración
    @Scheduled(fixedRateString  = "${tiempo.programacion.bd}")  // Cada minuto
    public void reloadConfigurations() {
        dynamicSchedulerService.scheduleTasksFromDatabase();
    }

    /**
     * Recarga configuraciones de procesamiento de actas cada 60 segundos
     */
    @Scheduled(fixedRateString  = "${tiempo.programacion.bd}")
    public void reloadProcesamientoActasTasks() {
        procesamientoActasSchedulerService.scheduleTasksFromDatabase();
    }
}
