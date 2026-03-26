package pe.gob.onpe.pradminbackend.config;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.gob.onpe.pradminbackend.model.bd.service.scheduller.DynamicSchedulerService;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ReloadTaskConfig {

    private final DynamicSchedulerService dynamicSchedulerService;

    // Ejecutar esta tarea cada minuto(60000 milisegundos), por ejemplo, para revisar cambios en la configuración
    @Scheduled(fixedRateString  = "${tiempo.programacion.bd}")  // Cada minuto
    public void reloadConfigurations() {
        dynamicSchedulerService.scheduleTasksFromDatabase();
    }

}
