package pe.gob.onpe.consultaopcron.model.bd.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopcron.model.bd.documents.*;
import pe.gob.onpe.consultaopcron.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopcron.model.bd.repository.primary.*;
import pe.gob.onpe.consultaopcron.model.bd.repository.secondary.TabProgramacionReporteRepository;
import pe.gob.onpe.consultaopcron.model.bd.service.ProcesarReporteService;
import pe.gob.onpe.consultaopcron.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.consultaopcron.utils.enums.TipoEstadoProcesoEnum;

import java.util.function.Function;
import java.time.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private static final String ZONA_HORARIA = "America/Lima";
    private static final String ELECCION = "eleccion";
    private final TabProgramacionReporteRepository tabProgramacionReporteRepository;
    private final TaskScheduler taskScheduler;
    private final List<ScheduledFuture<?>> scheduledFutures;
    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;

    private final ProcesarReporteService procesarReporteService;

    public void scheduleTasksFromDatabase() {

        List<TabReporteAutomatico> taskConfigs = tabProgramacionReporteRepository.findByEstado(1);
        if (!taskConfigs.isEmpty()) {

            if (scheduledFutures != null) {
                scheduledFutures.forEach(future -> future.cancel(false));
                scheduledFutures.clear();
            }

            taskConfigs.forEach(this::ejecutarValidandoPorcentageContabilizado);

        } else {
            log.info("NO EXISTEN CONFIGURACIONES EN BD REPORTE AUTOMATICO");
            if (scheduledFutures != null) {
                scheduledFutures.forEach(future -> future.cancel(false));
                scheduledFutures.clear();
            }
        }
    }

    private void ejecutarValidandoPorcentageContabilizado(TabReporteAutomatico taskConfig) {

        double porcentajeContabilizadas = obtenerPorcentageContabilizado(taskConfig.getEleccionId());

        if (esFechaValida(taskConfig) && porcentajeContabilizadas > 0) {
            scheduleTaskWithCron(taskConfig);
        } else {
            log.info("Eleccion {} no cumple con la condicion para ejecutar el reporte por porcentaje: {}",
                    taskConfig.getEleccionId(), porcentajeContabilizadas);
        }

    }

    private void scheduleTaskWithCron(TabReporteAutomatico taskConfig) {

        if (taskConfig.getEstadoProceso().intValue() !=
                TipoEstadoProcesoEnum.EN_CURSO.getCodigo().intValue()) {

            if (taskConfig.getTipoGeneracionReporte() == 1) { // reporte por tiempo
                if (validarEjecucionReporte(taskConfig.getEleccionId())) {
                    scheduledFutures.add(taskScheduler.schedule(() -> {
                        log.info("Ejecutando tarea por cron: {}", taskConfig.getEleccion());
                        ejecutarGeneracionReportePorCron(taskConfig);
                    }, new CronTrigger(taskConfig.getExpresionCron(), ZoneId.of(ZONA_HORARIA))));
                }
            } else if (taskConfig.getTipoGeneracionReporte() == 2) { // reporte por porcentaje
                Duration intervalo = Duration.ofMinutes(taskConfig.getTiempoConsultaReporte());

                if (validarEjecucionReporte(taskConfig.getEleccionId())) {
                    scheduledFutures.add(taskScheduler.scheduleAtFixedRate(() -> {
                        log.info("Ejecutando tarea por porcentaje: {}", taskConfig.getEleccion());
                        ejecutarGeneracionReportePorPorcentaje(taskConfig);
                    }, intervalo));
                }
            }

        } else {
            log.info("El proceso sigue EN_CURSO");
        }

    }

    private boolean validarEjecucionPorcentaje(TabReporteAutomatico taskConfig, double actasContabilizadasBD) {
        Integer tipoReporteValor = taskConfig.getTipoGeneracionReporteValor();
        if (tipoReporteValor.compareTo(5) == 0) {
            return validarEjecucion5Porciento(actasContabilizadasBD, taskConfig);
        } else if (tipoReporteValor.compareTo(10) == 0) {
            return validarEjecucion10Porciento(actasContabilizadasBD, taskConfig);
        } else if (tipoReporteValor.compareTo(20) == 0) {
            return validarEjecucion20Porciento(actasContabilizadasBD, taskConfig);
        } else {
            return false;
        }
    }

    private void ejecutarGeneracionReportePorCron(TabReporteAutomatico taskConfig) {

        procesarReporteService.enviarPeticionProcesarReporte(taskConfig.getId());

    }

    private void ejecutarGeneracionReportePorPorcentaje(TabReporteAutomatico taskConfig) {

        double porcentajeContabilizadas = obtenerPorcentageContabilizado(taskConfig.getEleccionId());

        if (validarEjecucionPorcentaje(taskConfig, porcentajeContabilizadas)) {
            procesarReporteService.enviarPeticionProcesarReporte(taskConfig.getId());
        } else {
            log.info("No cumple con la condicion para ejecutar el reporte por porcentaje: {}",
                    porcentajeContabilizadas);
        }
    }

    private boolean validarEjecucion5Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        int paso = (int) Math.floor(actasContabilizadasBD / 5.0);
        return taskConfig.getReporte5porciento().procesarGeneracion(paso);
    }

    private boolean validarEjecucion10Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        int paso = (int) Math.floor(actasContabilizadasBD / 10.0);
        return taskConfig.getReporte10porciento().procesarGeneracion(paso);
    }

    private boolean validarEjecucion20Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        int paso = (int) Math.floor(actasContabilizadasBD / 20.0);
        return taskConfig.getReporte20porciento().procesarGeneracion(paso);
    }

    private double obtenerPorcentageContabilizado(Integer idEleccion) {
        switch (TipoEleccionEnum.obtenerDescripcion(idEleccion.longValue())) {
            case "Presidencial":
                return obtenerPorcentajeSafe(
                        vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION),
                        VwPrPresidenciales::getPorcentajeActasContabilizadas);
            case "Diputados":
                return obtenerPorcentajeSafe(
                        vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION),
                        VwPrDiputados::getPorcentajeActasContabilizadas);
            case "Parlamento Andino":
                return obtenerPorcentajeSafe(
                        vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION),
                        VwPrParlamentoAndino::getPorcentajeActasContabilizadas);
            case "Senadores 27":
                return obtenerPorcentajeSafe(
                        vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(idEleccion,
                                ELECCION),
                        VwPrSenadoresDistritoElectoralMultiple::getPorcentajeActasContabilizadas);
            case "Senadores 33":
                return obtenerPorcentajeSafe(
                        vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(idEleccion,
                                ELECCION),
                        VwPrSenadoresDistritoNacionalUnico::getPorcentajeActasContabilizadas);
            case "Revocatoria Distrital":
                return obtenerPorcentajeSafe(
                        vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION),
                        VwPrRevocatoriaDistrital::getPorcentajeActasContabilizadas);
            default:
                return 0.0;
        }
    }

    private <T> double obtenerPorcentajeSafe(List<T> lista, Function<T, Double> extractor) {
        return lista.isEmpty() ? 0.0 : extractor.apply(lista.get(0));
    }

    private boolean validarEjecucionReporte(Integer electionType) {

        boolean typeElectionPermitted = false;
        if (electionType == 10 || electionType == 13 || electionType == 12 || electionType == 14
                || electionType == 15) {
            typeElectionPermitted = true;
        } else {
            log.info("Reporte no mapeado para su ejecución: {}", electionType);
        }
        return typeElectionPermitted;
    }

    private boolean esFechaValida(TabReporteAutomatico taskConfig) {

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
