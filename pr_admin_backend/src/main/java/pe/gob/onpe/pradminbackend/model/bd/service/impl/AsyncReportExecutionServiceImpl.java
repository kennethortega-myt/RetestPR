package pe.gob.onpe.pradminbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.service.AsyncReportExecutionService;
import pe.gob.onpe.pradminbackend.model.bd.service.cron.ValidacionCronService;
import pe.gob.onpe.pradminbackend.model.bd.service.scheduller.DynamicSchedulerService;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEstadoProcesoEnum;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncReportExecutionServiceImpl implements AsyncReportExecutionService {

    private final DynamicSchedulerService dynamicSchedulerService;
    private final ValidacionCronService validacionCronService;
    @Override
    @Async("taskExecutor")
    public void executeReportTask(TabReporteAutomatico taskConfig) {
        try {
            log.info("Iniciando ejecución asíncrona del reporte para la elección: {}", taskConfig.getEleccion());
            dynamicSchedulerService.executeTask(taskConfig);
            log.info("Finalizada la llamada al scheduler para la elección: {}", taskConfig.getEleccion());
        } catch (Exception e) {
            log.error("Error en la ejecución asíncrona del reporte para la elección {}: {}", taskConfig.getEleccion(), e.getMessage(), e);
            try {
                validacionCronService.actualizarEstadoReporteAutomaticoCronEjecucion(taskConfig, TipoEstadoProcesoEnum.ERROR);
            } catch (Exception ex) {
                log.error("Error al intentar actualizar el estado del reporte a ERROR: {}", ex.getMessage(), ex);
            }
        }
    }
}
