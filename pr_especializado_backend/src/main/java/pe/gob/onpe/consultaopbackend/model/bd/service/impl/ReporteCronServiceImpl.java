package pe.gob.onpe.consultaopbackend.model.bd.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.bd.documents.*;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabCronReporteActas;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.*;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabProgramacionReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteArchivoService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteCronService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ValidarPorcentajeService;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRequest;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.utils.ReporteUtils;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEstadoProcesoEnum;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteCronServiceImpl implements ReporteCronService {
    private static final String ELECCION = "eleccion";
    public static final String AUTOMATICO = "automatico";
    private final TabProgramacionReporteRepository tabProgramacionReporteRepository;
    private final ValidarPorcentajeService validarPorcentajeService;

    private final TabReporteRepository tabReporteRepository;
    private final ReporteArchivoService generarReporteAsincrono;
    private final VwPrPresidencialesRepository vwPrPresidencialesRepository;
    private final VwPrDiputadosRepository vwPrDiputadosRepository;
    private final VwPrSenadoresDistritoElectoralMultipleRepository vwPrSenadoresDistritoElectoralMultipleRepository;
    private final VwPrSenadoresDistritoNacionalUnicoRepository vwPrSenadoresDistritoNacionalUnicoRepository;
    private final VwPrParlamentoAndinoRepository vwPrParlamentoAndinoRepository;
    private final VwPrRevocatoriaDistritalRepository vwPrRevocatoriaDistritalRepository;
    private final ValidacionCronServiceImpl validacionCronService;

    @Override
    public ResponseEntity<ReporteCronResponse> generarReporteProgramado(String idReporte) {

        GenericResponse<String>  response = new GenericResponse<> ();

        tabProgramacionReporteRepository.findById(idReporte)
        .ifPresent(taskConfig -> {

            // Pone en curso el estado de ejecucion del cron
            validacionCronService.actualizarEstadoReporteAutomaticoCronEjecucion(taskConfig,
                    TipoEstadoProcesoEnum.EN_CURSO);

            if (validarEjecucionReporte(taskConfig)) {

                if (taskConfig.getTipoGeneracionReporte().compareTo(1) == 0) { //reporte por tiempo - cron programado
                    ejecutarGeneracionReportePorCron(taskConfig);
                } else if (taskConfig.getTipoGeneracionReporte().compareTo(2) == 0) { //reporte por porcentaje - programado
                    ejecutarGeneracionReportePorPorcentaje(taskConfig);
                }

            }

            // Cambia el estado a pendiente al terminar el proceso
            validacionCronService.actualizarEstadoReporteAutomaticoCronEjecucion(taskConfig,
                    TipoEstadoProcesoEnum.PENDIENTE);

        });
        response.setMessage("Se esta procesando su reporte.");
        response.setSuccess(true);
        return  new ResponseEntity<>(ReporteCronResponse.builder().response(response).build(), HttpStatus.OK);
    }

    private void ejecutarGeneracionReportePorCron(TabReporteAutomatico taskConfig) {

        if(taskConfig.getEstado() == 0) {
            log.info("no se ejecuta el reporte por estar deshabilitado");
            return;
        }
        ReporteRequest requestReporte = obtenerPeticionPorEleccion(taskConfig);

        String filtrosIdsJson = ReporteUtils.getJsonFromRequestIds(requestReporte);
        String filtrosJson = ReporteUtils.getJsonFromRequest(requestReporte);

        String idReporte = UUID.randomUUID().toString();
        TabReporte registroReporteInicial = TabReporte.builder()
                .id(idReporte)
                .filtro(filtrosIdsJson)
                .filtroValores(filtrosJson)
                .codigoUsuario(AUTOMATICO)
                .cAudUsuarioCreacion(AUTOMATICO)
                .fechaCreacion(new Date())
                .tipoEleccion(requestReporte.getIdEleccion())
                .tipoReporte(requestReporte.getTipoReporte())
                .porcentaje(obtenerPorcentageContabilizado(taskConfig.getEleccionId()))
                .nActivo(1)
                .estado(0)//registrado
                .build();
        tabReporteRepository.save(registroReporteInicial);

        log.info("Reporte---> " + taskConfig.getEleccion() + " - " + taskConfig.getTipoReporte() + ":::Registrado");
        if (requestReporte.getTipoReporte() == 1) {
            generarReporteAsincrono.generarReporteCsv(requestReporte, registroReporteInicial);
        } else if (requestReporte.getTipoReporte() == 2) {
            generarReporteAsincrono.generarReporteObservadosCsv(requestReporte, registroReporteInicial);
        } else {
            log.warn("TIPO DE REPORTE AUTOMATICO NO MAPEADO EN SISCOP ");
        }

    }

    private void ejecutarGeneracionReportePorPorcentaje(TabReporteAutomatico taskConfig) {

        if(taskConfig.getEstado() == 0) {
            log.info("no se ejecuta el reporte por estar deshabilitado");
            return;
        }

        double porcentajeContabilizadas = obtenerPorcentageContabilizado(taskConfig.getEleccionId());

        if (validarEjecucionPorcentaje(taskConfig, porcentajeContabilizadas)) {

            ReporteRequest requestReporte = obtenerPeticionPorEleccion(taskConfig);

            String filtrosIdsJson = ReporteUtils.getJsonFromRequestIds(requestReporte);
            String filtrosJson = ReporteUtils.getJsonFromRequest(requestReporte);

            String idReporte = UUID.randomUUID().toString();
            TabReporte registroReporteInicial = TabReporte.builder()
                    .id(idReporte)
                    .filtro(filtrosIdsJson)
                    .filtroValores(filtrosJson)
                    .codigoUsuario(AUTOMATICO)
                    .cAudUsuarioCreacion(AUTOMATICO)
                    .fechaCreacion(new Date())
                    .tipoEleccion(requestReporte.getIdEleccion())
                    .tipoReporte(requestReporte.getTipoReporte())
                    .porcentaje(obtenerPorcentageContabilizado(taskConfig.getEleccionId()))
                    .nActivo(1)
                    .estado(0)//registrado
                    .build();
            tabReporteRepository.save(registroReporteInicial);

            log.info("Reporte porcentaje ---> " + taskConfig.getEleccion() + " - " + taskConfig.getTipoReporte() + ":::Registrado");
            if (requestReporte.getTipoReporte() == 1) {
                generarReporteAsincrono.generarReporte(requestReporte, registroReporteInicial);
            } else if (requestReporte.getTipoReporte() == 2) {
                generarReporteAsincrono.generarReporteObservados(requestReporte, registroReporteInicial);
            } else {
                log.info("TIPO DE REPORTE AUTOMATICO PORCENTAJE NO MAPEADO EN SISCOP");
            }

            //actualiza el registro de reporte automatico , generacion n = true
            tabProgramacionReporteRepository.save(taskConfig);

        } else {
            log.info("No cumple con la condicion para ejecutar el reporte por porcentaje:" + porcentajeContabilizadas);
        }

    }

    private ReporteRequest obtenerPeticionPorEleccion(TabReporteAutomatico taskConfig) {

        return ReporteRequest.builder()
                .tipoReporte(taskConfig.getTipoReporte())
                .idEleccion(taskConfig.getEleccionId())
                .tipoFiltro(ELECCION)
                .build();

    }

    private double obtenerPorcentageContabilizado(Integer idEleccion) {

        switch (TipoEleccionEnum.obtenerDescripcion(idEleccion.longValue())) {
            case "Presidencial":
                List<VwPrPresidenciales> lista = vwPrPresidencialesRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return lista.stream()
                        .findFirst()
                        .map(VwPrPresidenciales::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            case "Diputados":
                List<VwPrDiputados> listaDiputados = vwPrDiputadosRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return listaDiputados.stream()
                        .findFirst()
                        .map(VwPrDiputados::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            case "Parlamento Andino":
                List<VwPrParlamentoAndino> listaParlamento = vwPrParlamentoAndinoRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return listaParlamento.stream()
                        .findFirst()
                        .map(VwPrParlamentoAndino::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            case "Senadores Distrito Electoral Múltiple":
                List<VwPrSenadoresDistritoElectoralMultiple> listaSenadoresMultiple = vwPrSenadoresDistritoElectoralMultipleRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return listaSenadoresMultiple.stream()
                        .findFirst()
                        .map(VwPrSenadoresDistritoElectoralMultiple::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            case "Senadores Distrito Electoral Único":
                List<VwPrSenadoresDistritoNacionalUnico> listaDistritoUnico = vwPrSenadoresDistritoNacionalUnicoRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return listaDistritoUnico.stream()
                        .findFirst()
                        .map(VwPrSenadoresDistritoNacionalUnico::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            case "Revocatoria Distrital":
                List<VwPrRevocatoriaDistrital> listaRevocatoria = vwPrRevocatoriaDistritalRepository.findByTipoEleccionAndTipoFiltro(idEleccion, ELECCION);
                return listaRevocatoria.stream()
                        .findFirst()
                        .map(VwPrRevocatoriaDistrital::getPorcentajeActasContabilizadas)
                        .orElse(0.0);
            default:
                return 0.0;
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

    private boolean validarEjecucion5Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        int actas = (int) Math.floor(actasContabilizadasBD / 5.0) * 5; // Redondear hacia abajo a múltiplos de 5

        return switch (actas) {
            case 5 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion1, taskConfig.getReporte5porciento()::setGeneracion1);
            case 10 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion2, taskConfig.getReporte5porciento()::setGeneracion2);
            case 15 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion3, taskConfig.getReporte5porciento()::setGeneracion3);
            case 20 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion4, taskConfig.getReporte5porciento()::setGeneracion4);
            case 25 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion5, taskConfig.getReporte5porciento()::setGeneracion5);
            case 30 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion6, taskConfig.getReporte5porciento()::setGeneracion6);
            case 35 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion7, taskConfig.getReporte5porciento()::setGeneracion7);
            case 40 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion8, taskConfig.getReporte5porciento()::setGeneracion8);
            case 45 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion9, taskConfig.getReporte5porciento()::setGeneracion9);
            case 50 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion10, taskConfig.getReporte5porciento()::setGeneracion10);
            case 55 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion11, taskConfig.getReporte5porciento()::setGeneracion11);
            case 60 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion12, taskConfig.getReporte5porciento()::setGeneracion12);
            case 65 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion13, taskConfig.getReporte5porciento()::setGeneracion13);
            case 70 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion14, taskConfig.getReporte5porciento()::setGeneracion14);
            case 75 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion15, taskConfig.getReporte5porciento()::setGeneracion15);
            case 80 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion16, taskConfig.getReporte5porciento()::setGeneracion16);
            case 85 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion17, taskConfig.getReporte5porciento()::setGeneracion17);
            case 90 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion18, taskConfig.getReporte5porciento()::setGeneracion18);
            case 95 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion19, taskConfig.getReporte5porciento()::setGeneracion19);
            case 100 -> actualizarGeneracion(taskConfig.getReporte5porciento()::isGeneracion20, taskConfig.getReporte5porciento()::setGeneracion20);
            default -> false;
        };

    }

    private boolean validarEjecucion10Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        // Redondear hacia abajo a múltiplos de 10
        int actas = (int) Math.floor(actasContabilizadasBD / 10.0) * 10;

        // Map para asociar los múltiplos de 10 con los métodos de generación
        return switch (actas) {
            case 10 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion1, taskConfig.getReporte10porciento()::setGeneracion1);
            case 20 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion2, taskConfig.getReporte10porciento()::setGeneracion2);
            case 30 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion3, taskConfig.getReporte10porciento()::setGeneracion3);
            case 40 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion4, taskConfig.getReporte10porciento()::setGeneracion4);
            case 50 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion5, taskConfig.getReporte10porciento()::setGeneracion5);
            case 60 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion6, taskConfig.getReporte10porciento()::setGeneracion6);
            case 70 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion7, taskConfig.getReporte10porciento()::setGeneracion7);
            case 80 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion8, taskConfig.getReporte10porciento()::setGeneracion8);
            case 90 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion9, taskConfig.getReporte10porciento()::setGeneracion9);
            case 100 -> actualizarGeneracion(taskConfig.getReporte10porciento()::isGeneracion10, taskConfig.getReporte10porciento()::setGeneracion10);
            default -> false;
        };
    }

    private boolean actualizarGeneracion(BooleanSupplier isGeneracion, Consumer<Boolean> setGeneracion) {
        if (!isGeneracion.getAsBoolean()) {
            setGeneracion.accept(true);
            return true;
        }
        return false;
    }

    private boolean validarEjecucion20Porciento(double actasContabilizadasBD, TabReporteAutomatico taskConfig) {
        // Redondear hacia abajo a múltiplos de 20
        int actas = (int) Math.floor(actasContabilizadasBD / 20.0) * 20;

        return switch (actas) {
            case 20 -> actualizarGeneracion(taskConfig.getReporte20porciento()::isGeneracion1, taskConfig.getReporte20porciento()::setGeneracion1);
            case 40 -> actualizarGeneracion(taskConfig.getReporte20porciento()::isGeneracion2, taskConfig.getReporte20porciento()::setGeneracion2);
            case 60 -> actualizarGeneracion(taskConfig.getReporte20porciento()::isGeneracion3, taskConfig.getReporte20porciento()::setGeneracion3);
            case 80 -> actualizarGeneracion(taskConfig.getReporte20porciento()::isGeneracion4, taskConfig.getReporte20porciento()::setGeneracion4);
            case 100 -> actualizarGeneracion(taskConfig.getReporte20porciento()::isGeneracion5, taskConfig.getReporte20porciento()::setGeneracion5);
            default -> false;
        };

    }

    private boolean validarEjecucionReporte(TabReporteAutomatico taskConfig) {
        Integer electionType = taskConfig.getEleccionId();
        boolean typeElectionPermitted = false;

        if (validarEjecucionReportePorProcentaje(taskConfig)) {

            if (electionType == 10 || electionType == 13 || electionType == 12 || electionType == 14 || electionType == 15) {
                typeElectionPermitted = true;
            } else  {
                log.info("Reporte no mapeado para su ejecución: " + electionType);
            }
        }

        return typeElectionPermitted;
    }

    public Boolean validarEjecucionReportePorProcentaje(TabReporteAutomatico taskConfig) {
        Double porcentajeContabilizadasActual = obtenerPorcentageContabilizado(taskConfig.getEleccionId());
        Double porcentajeAnteriorCron = ObjectUtils.defaultIfNull(taskConfig.getPorcentaje(),0.0);
        Boolean ejecutar = false;
        log.info("porcentajeContabilizadasActual : " + porcentajeContabilizadasActual);
        if (porcentajeContabilizadasActual > 0 && porcentajeContabilizadasActual <= 100) {

            if (porcentajeContabilizadasActual > porcentajeAnteriorCron) {
                log.info("✅ Eleccion" + taskConfig.getEleccionId() +
                        " Porcentaje aumentó ({} > {}" , porcentajeContabilizadasActual +
                        " mayor al anterior:" + porcentajeAnteriorCron);

                ejecutar = true;

            } else {
                log.info("❌ Eleccion" + taskConfig.getEleccionId() +
                                " Porcentaje no aumentó ({} <= {}), no ejecutar",
                        porcentajeContabilizadasActual, porcentajeAnteriorCron);
                ejecutar = false;
            }

        } else {
            log.info("Eleccion" + taskConfig.getEleccionId() +
                    " no cumple con la condicion para ejecutar el reporte por porcentaje:" + porcentajeContabilizadasActual);
            ejecutar = false;
        }

        return ejecutar;
    }

    @Override
    public ResponseEntity<ReporteCronResponse> generarReporteManual(String idConfiguracion) {
        GenericResponse<String> response = new GenericResponse<> ();
        Optional<TabReporteAutomatico> configOpt = tabProgramacionReporteRepository.findById(idConfiguracion);
        if (configOpt.isPresent()) {

            TabReporteAutomatico taskConfig = configOpt.get();
            Double porcentajeContabilizadasActual = validarPorcentajeService
                    .obtenerPorcentageContabilizado(taskConfig.getEleccionId());
            if (porcentajeContabilizadasActual == 100) {

                if (taskConfig.getTipoGeneracionReporte().compareTo(1) == 0) { //reporte por tiempo - cron programado
                    ejecutarGeneracionReportePorCron(taskConfig);
                } else if (taskConfig.getTipoGeneracionReporte().compareTo(2) == 0) { //reporte por porcentaje - programado
                    ejecutarGeneracionReportePorPorcentaje(taskConfig);
                }

                response.setMessage("Se esta procesando su reporte manual.");
                response.setSuccess(true);
            } else {
                response.setMessage("El reporte solo puede generarse cuando las actas contabilizadas estén al 100%");
                response.setSuccess(false);
            }

        }

        return  new ResponseEntity<>(ReporteCronResponse.builder().response(response).build(), HttpStatus.OK);
    }


}