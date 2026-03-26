package pe.gob.onpe.pradminbackend.model.bd.service.scheduller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.model.bd.documents.*;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.repository.*;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteAutomaticoRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ReporteCronService;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionEnum;

import java.time.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private static final String ZONA_HORARIA = "America/Lima";
    private static final String ELECCION = "eleccion";
    private final TabReporteAutomaticoRepository tabProgramacionReporteRepository;
    private final TaskScheduler taskScheduler;
    private final List<ScheduledFuture<?>> scheduledFutures;
    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;
    private final ReporteCronService reporteCronService;

    public void executeTask(TabReporteAutomatico taskConfig) {

        if (taskConfig.getTipoGeneracionReporte() == 1) { //reporte por tiempo
            if (validarEjecucionReporte(taskConfig)) {
                log.info("Ejecutando tarea por cron: " + taskConfig.getEleccion());
                reporteCronService.generarReporteProgramado(taskConfig.getId());
            }
        } else if (taskConfig.getTipoGeneracionReporte() == 2) { //reporte por porcentaje
            if (validarEjecucionReporte(taskConfig)) {
                log.info("Ejecutando tarea por porcentaje: " + taskConfig.getEleccion());
                reporteCronService.generarReporteProgramado(taskConfig.getId());
            }
        }
    }

    private boolean validarEjecucionReporte(TabReporteAutomatico taskConfig) {
        Integer electionType = taskConfig.getEleccionId();
        boolean typeElectionPermitted = false;

        if (electionType == 10 || electionType == 13 || electionType == 12 || electionType == 14 || electionType == 15) {
            typeElectionPermitted = true;
        } else  {
            log.info("Reporte no mapeado para su ejecución: " + electionType);
        }

        log.info("typeElectionPermitted : " + typeElectionPermitted + ", eleccion : " + taskConfig.getEleccion());
        return typeElectionPermitted;
    }

}
