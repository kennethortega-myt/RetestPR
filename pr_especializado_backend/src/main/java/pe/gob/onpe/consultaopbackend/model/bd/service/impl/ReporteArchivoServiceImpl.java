package pe.gob.onpe.consultaopbackend.model.bd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.exception.WriteReportException;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.consultaopbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.consultaopbackend.model.bd.repository.primary.MaeFechaRepository;
import pe.gob.onpe.consultaopbackend.model.bd.repository.secondary.TabReporteRepository;
import pe.gob.onpe.consultaopbackend.model.bd.service.ActaService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteArchivoService;
import pe.gob.onpe.consultaopbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.consultaopbackend.model.bd.service.TabArchivoReporteService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ResumenActasObservadasReqDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ActasResponseReporteDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRequest;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.ActaRespuestaReporteDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.FiltroActaEleccionReporteDto;
import pe.gob.onpe.consultaopbackend.utils.DateTimeUtil;
import pe.gob.onpe.consultaopbackend.utils.DateUtil;
import pe.gob.onpe.consultaopbackend.utils.PrUtils;
import pe.gob.onpe.consultaopbackend.utils.enums.DistritoElectoralEnum;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoEleccionEnum;
import pe.gob.onpe.consultaopbackend.utils.enums.TipoReporteEnum;
import pe.gob.onpe.consultaopbackend.utils.excel.ExcelDto;
import pe.gob.onpe.consultaopbackend.utils.excel.NewExcelUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteArchivoServiceImpl implements ReporteArchivoService {

    public static final String ADMIN = "admin";
    private final TabReporteRepository tabReporteRepository;
    private final ActaService actaService;
    private final NewExcelUtil newExcelUtil;
    private final TabArchivoReporteService tabArchivoReporteService;
    private final MaeFechaRepository maeFechaRepository;

    private final ResumenGeneralService resumenGeneralService;

    @Value("${cop.nfs.path}")
    private String pathNfs;

    @Value("${cop.nfs.files}")
    private String pathFiles;

    @Async
    @Override
    public void generarReporte(ReporteRequest requestReporte, TabReporte reporteBd) {
        log.info(":::REPORTE::: {} ::: {}", requestReporte, reporteBd);

        ActaRespuestaReporteDto totales = null;
        ReporteRespuestaDto reporteData = null;
        ExcelDto excelDto = null;
        byte[] reporteExcel = null;
        TabArchivo archivoGuardado = null;

        try {
            // 1. Obtener Totales (si aplica)
            if (requestReporte.getTipoReporte() == 1) {
                var requestTotales = FiltroActaEleccionReporteDto.builder()
                        .idEleccion(requestReporte.getIdEleccion())
                        .idAmbitoGeografico(requestReporte.getIdAmbitoGeografico())
                        .idUbigeoDepartamento(parseSafeInt(requestReporte.getUbigeoNivel01()))
                        .idUbigeoProvincia(parseSafeInt(requestReporte.getUbigeoNivel02()))
                        .idUbigeoDistrito(parseSafeInt(requestReporte.getIdUbigeo()))
                        .tipoFiltro(requestReporte.getTipoFiltro())
                        .codigoOp(requestReporte.getCodigoOp())
                        .build();

                Optional<ActaRespuestaReporteDto> actaTotales =
                        resumenGeneralService.obtenerTotalesPorEleccionParaReporte(requestTotales);

                if (actaTotales.isPresent()) {
                    totales = actaTotales.get();
                    totales.setFechaActualizacion(DateUtil.sumarHoras(reporteBd.getFechaCreacion(), -5));
                }
            }

            // 2. Obtener datos del reporte
            var requestActas = ActaRequestDto.builder()
                    .codigoOp(parseSafeInt(requestReporte.getCodigoOp()))
                    .idEleccion(requestReporte.getIdEleccion())
                    .idAmbitoGeografico(requestReporte.getIdAmbitoGeografico())
                    .ubigeoNivel01(requestReporte.getUbigeoNivel01())
                    .ubigeoNivel02(requestReporte.getUbigeoNivel02())
                    .idUbigeo(requestReporte.getIdUbigeo())
                    .codigoLocalVotacion(requestReporte.getCodigoLocalVotacion())
                    .build();

            reporteData = actaService.obtenerActasReporte(requestActas);

            if (reporteData.getRegistrosReporte().isEmpty()) {
                actualizarEstadoReporte(reporteBd, 3, "No existen registros en BD para generar el reporte");
                return;
            }

            // 3. Generar archivo
            actualizarEstadoReporte(reporteBd, 1, "Reporte en proceso");

            excelDto = newExcelUtil.generarExcelDto(reporteData, requestReporte.getCodigoOp());
            reporteExcel = newExcelUtil.generateExcelReporte(
                    reporteData.getListaOrgPolitica(),
                    excelDto,
                    requestReporte.getCodigoOp(),
                    totales,
                    requestReporte.getTipoReporte()
            );

            archivoGuardado = tabArchivoReporteService.guardarArchivoReporte(
                    reporteBd,
                    reporteExcel,
                    pathNfs.concat(pathFiles).concat(pathNfs),
                    "xlsx"
            );

            reporteBd.setArchivo(archivoGuardado);

            if (archivoGuardado != null) {
                actualizarEstadoReporte(reporteBd, 2, "Reporte terminado");
            } else {
                actualizarEstadoReporte(reporteBd, 4, "Archivo no subido al NFS");
            }

        } catch (Exception ex) {
            actualizarEstadoReporte(reporteBd, 5, "error en generar reporte actas generales: " + ex.getMessage());
            log.error("Error en generar reporte actas generales: {}", ex.getMessage(), ex);

        } finally {
            // Libera recursos manualmente
            totales = null;
            reporteData = null;
            reporteExcel = null;
            excelDto = null;
            archivoGuardado = null;
            System.gc(); // Opcional y controlado si usas G1GC
        }
    }

    private void actualizarEstadoReporte(TabReporte reporte, int estado, String logMessage) {
        reporte.setEstado(estado);
        reporte.setCAudUsuarioModificacion(ADMIN);
        reporte.setDAudFechaModificacion(new Date());
        reporte.setFechaProceso(maeFechaRepository.findAll().getFirst().getFechaProceso());
        tabReporteRepository.save(reporte);
        log.info("Estado del reporte general actualizado a {}: {}", estado, logMessage);
    }

    private int parseSafeInt(String value) {
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void simularDemora(Long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async
    @Override
    public void generarReporteObservados(ReporteRequest request, TabReporte reporte) {
        try {
            var requestTotales = ResumenActasObservadasReqDto.builder()
                    .idEleccion(request.getIdEleccion())
                    .idAmbitoGeografico(request.getIdAmbitoGeografico())
                    .ubigeoNivel01(request.getUbigeoNivel01())
                    .ubigeoNivel02(request.getUbigeoNivel02())
                    .idUbigeo(request.getIdUbigeo())
                    .build();

            actaService.obtenerResumenActasObservadas(requestTotales)
                    .ifPresentOrElse(totales -> {
                        var totalesFinal = ActaRespuestaReporteDto.builder()
                                .contabilizadas(totales.getContabilizadas())
                                .enviadasJee(totales.getEnviadasJee())
                                .totalActas(totales.getTotalActas())
                                .fechaActualizacion(DateUtil.sumarHoras(reporte.getFechaCreacion(), -5))
                                .build();

                        var requestActas = ActaRequestDto.builder()
                                .codigoOp(Optional.ofNullable(request.getCodigoOp()).map(Integer::valueOf).orElse(null))
                                .idEleccion(request.getIdEleccion())
                                .idAmbitoGeografico(request.getIdAmbitoGeografico())
                                .ubigeoNivel01(request.getUbigeoNivel01())
                                .ubigeoNivel02(request.getUbigeoNivel02())
                                .idUbigeo(request.getIdUbigeo())
                                .codigoLocalVotacion(request.getCodigoLocalVotacion())
                                .codigoEstadoActa("E") // Solo para envio al JEE
                                .build();

                        var reporteData = actaService.obtenerActasReporteObservadas(requestActas);

                        if (reporteData.getRegistrosReporte().isEmpty()) {
                            actualizarEstado(reporte, 3, "No existen registros en BD para generar el reporte");
                            log.info("No hay registros para generar el reporte: {}", request);
                            return;
                        }

                        actualizarEstado(reporte, 1,":::Reporte en proceso:::");
                        log.info("Generando archivo Excel para el reporte solicitado...");

                        ExcelDto excelDto = newExcelUtil.generarExcelDto(reporteData, request.getCodigoOp());
                        byte[] archivoExcel = newExcelUtil.generateExcelReporte(
                                reporteData.getListaOrgPolitica(),
                                excelDto,
                                request.getCodigoOp(),
                                totalesFinal,
                                request.getTipoReporte()
                        );

                        var archivo = tabArchivoReporteService.guardarArchivoReporte(
                                reporte,
                                archivoExcel,
                                pathNfs + pathFiles + pathNfs,
                                "xlsx"
                        );

                        reporte.setArchivo(archivo);
                        actualizarEstado(reporte, archivo != null ? 2 : 4, archivo != null ? "Reporte terminado":"Archivo no subido al NFS");

                        log.info("Finalizado reporte: estado={}, archivo={}", reporte.getEstado(), archivo != null ? "OK" : "No guardado");

                    }, () -> {
                        actualizarEstado(reporte, 3,"No se encontró resumen de actas observadas");
                        log.info("No se encontró resumen de actas observadas con los filtros: {}", request);
                    });

        } catch (Exception ex) {
            actualizarEstado(reporte, 5,"error en generar reporte observados: " + ex.getMessage());
            log.error("Error en generarReporteObservados: {}", ex.getMessage(), ex);
        }
    }

    @Async
    @Override
    public void generarReporteObservadosCsv(ReporteRequest request, TabReporte reporte) {
        log.info(":::REPORTE OBSERVADOS - CSV::: {} ::: {}", request, reporte);

        var requestActas = ActaRequestDto.builder()
                .codigoOp(Optional.ofNullable(request.getCodigoOp()).map(Integer::valueOf).orElse(null))
                .idEleccion(request.getIdEleccion())
                .idAmbitoGeografico(request.getIdAmbitoGeografico())
                .ubigeoNivel01(request.getUbigeoNivel01())
                .ubigeoNivel02(request.getUbigeoNivel02())
                .idUbigeo(request.getIdUbigeo())
                .codigoLocalVotacion(request.getCodigoLocalVotacion())
                .idDistritoElectoral(request.getIdDistritoElectoral())
                .codigoEstadoActa("E") // Solo para envio al JEE
                .build();

        var reporteDataObservadas = actaService.obtenerActasReporteObservadasCsv(requestActas);

        if (reporteDataObservadas.getRegistrosReporte().isEmpty()) {
            actualizarEstado(reporte, 3, "No existen registros en BD para generar el reporte");
            return;
        }

        actualizarEstado(reporte, 1, "Reporte en proceso");

        List<String> cabeceraReporte = PrUtils.cabecerasPorEleccion(
                request.getIdEleccion(),
                reporteDataObservadas.getListaOrgPolitica()
        );

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(cabeceraReporte.toArray(new String[0]))
                .get();

        try {
            // 1. Generar el CSV en memoria
            byte[] csvBytes;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {

                int longitud = reporteDataObservadas.getListaOrgPolitica().size();
                for (var elemento : reporteDataObservadas.getRegistrosReporte()) {
                    List<String> datos = getCamposGenericos(elemento, request.getIdEleccion());
                    List<Integer> votos = elemento.getListaVotosOrgPolitica();

                    if (votos == null || votos.isEmpty()) {
                        if (longitud > 0) {
                            datos.addAll(Collections.nCopies(longitud - 1, ""));
                        }
                    } else {
                        datos.addAll(votos.stream().map(String::valueOf).toList());
                    }
                    csvPrinter.printRecord(datos);
                }
                csvPrinter.flush();
                csvBytes = baos.toByteArray();
            }

            // 2. Comprimir el CSV en ZIP
            byte[] zipBytes;
            try (ByteArrayOutputStream baosZip = new ByteArrayOutputStream();
                 ZipOutputStream zipOut = new ZipOutputStream(baosZip)) {

                String eleccion = TipoEleccionEnum.obtenerDescripcion(Long.valueOf(request.getIdEleccion()));
                String tipoDescripcionReporte = TipoReporteEnum.obtenerDescripcion(request.getTipoReporte());
                String fechaHoraReporte = DateTimeUtil.formatLocalDateTimeToReportTimestamp(LocalDateTime.now());
                String nombreCsv = eleccion + "_" + tipoDescripcionReporte + "_" + fechaHoraReporte + ".csv";
                ZipEntry entry = new ZipEntry(nombreCsv);
                zipOut.putNextEntry(entry);
                zipOut.write(csvBytes);
                zipOut.closeEntry();
                zipOut.finish();

                zipBytes = baosZip.toByteArray();
            }

            // 3. Guardar archivo ZIP en NFS/NFS
            TabArchivo archivoGuardado = tabArchivoReporteService.guardarArchivoReporte(
                    reporte,
                    zipBytes,
                    pathNfs.concat(pathFiles).concat(pathNfs),
                    "zip"
            );

            reporte.setArchivo(archivoGuardado);
            if (archivoGuardado != null) {
                actualizarEstadoReporte(reporte, 2, "Reporte terminado");
            } else {
                actualizarEstadoReporte(reporte, 4, "Archivo no subido al NFS");
            }

        } catch (Exception ex) {
            actualizarEstadoReporte(reporte, 5, "error en generar reporte observados: " + ex.getMessage());
            log.error("Error en generar reporte observados: {}", ex.getMessage(), ex);
        }
    }

    private void actualizarEstado(TabReporte reporte, int estado, String logMessage) {
        reporte.setEstado(estado);
        reporte.setCAudUsuarioModificacion(ADMIN);
        reporte.setDAudFechaModificacion(new Date());
        reporte.setFechaProceso(maeFechaRepository.findAll().getFirst().getFechaProceso());
        tabReporteRepository.save(reporte);
        log.info("Estado del reporte observados actualizado a {}: {}", estado, logMessage);
    }

    @Async
    public void generarReporteCsvUnico(ReporteRequest requestReporte, TabReporte reporteBd) {
        log.info(":::REPORTE UNICO - CSV::: {} ::: {}", requestReporte, reporteBd);

        // 1. Obtener datos del reporte
        var requestActas = ActaRequestDto.builder()
                .codigoOp(parseSafeInt(requestReporte.getCodigoOp()))
                .idEleccion(requestReporte.getIdEleccion())
                .idAmbitoGeografico(requestReporte.getIdAmbitoGeografico())
                .ubigeoNivel01(requestReporte.getUbigeoNivel01())
                .ubigeoNivel02(requestReporte.getUbigeoNivel02())
                .idUbigeo(requestReporte.getIdUbigeo())
                .codigoLocalVotacion(requestReporte.getCodigoLocalVotacion())
                .idDistritoElectoral(requestReporte.getIdDistritoElectoral())
                .build();

        var reporteData = actaService.obtenerActasReporteSinArchivo(requestActas);

        if (reporteData.getRegistrosReporte().isEmpty()) {
            actualizarEstadoReporte(reporteBd, 3, "No existen registros en BD para generar el reporte");
            return;
        }

        var cabeceraReporte = PrUtils.cabecerasPorEleccion(
                requestReporte.getIdEleccion(),
                reporteData.getListaOrgPolitica()
        );

        var format = CSVFormat.DEFAULT.builder()
                .setHeader(cabeceraReporte.toArray(String[]::new))
                .get();

        try (var baos = new ByteArrayOutputStream();
             var writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             var csvPrinter = new CSVPrinter(writer, format)) {

            // 2. Estado inicial
            actualizarEstadoReporte(reporteBd, 1, "Reporte en proceso");
            int longitud = reporteData.getListaOrgPolitica().size();

            reporteData.getRegistrosReporte().forEach(elemento -> {
                try {
                    var datos = new ArrayList<>(getCamposGenericos(elemento, requestReporte.getIdEleccion()));
                    var votos = elemento.getListaVotosOrgPolitica();

                    if (votos == null || votos.isEmpty()) {
                        if (longitud > 0) {
                            datos.addAll(Collections.nCopies(longitud - 1, "0"));
                        }
                    } else {
                        datos.addAll(votos.stream().map(String::valueOf).toList());
                    }
                    csvPrinter.printRecord(datos);
                } catch (IOException e) {
                    log.error("ERROR EN ESCRIBIR CSV {}", e.getMessage());
                    throw new WriteReportException("ERROR EN ESCRIBIR CSV " + e.getMessage());
                }
            });

            csvPrinter.flush();

            // 3. Comprimir a ZIP
            byte[] zipBytes;
            try (var zipBaos = new ByteArrayOutputStream();
                 var zos = new ZipOutputStream(zipBaos, StandardCharsets.UTF_8)) {

                String eleccion = TipoEleccionEnum.obtenerDescripcion(Long.valueOf(requestReporte.getIdEleccion()));
                String tipoDescripcionReporte = TipoReporteEnum.obtenerDescripcion(requestReporte.getTipoReporte());
                String fechaHoraReporte = DateTimeUtil.formatLocalDateTimeToReportTimestamp(LocalDateTime.now());

                zos.putNextEntry(new ZipEntry(eleccion+"_"+tipoDescripcionReporte+ "_"+fechaHoraReporte+".csv"));
                zos.write(baos.toByteArray());
                zos.closeEntry();
                zos.finish();

                zipBytes = zipBaos.toByteArray();
            }

            // 4. Guardar archivo ZIP en NFS
            var archivoGuardado = tabArchivoReporteService.guardarArchivoReporte(
                    reporteBd,
                    zipBytes,
                    pathNfs.concat(pathFiles).concat(pathNfs),
                    "zip"
            );

            reporteBd.setArchivo(archivoGuardado);

            if (archivoGuardado != null) {
                actualizarEstadoReporte(reporteBd, 2, "Reporte terminado");
            } else {
                actualizarEstadoReporte(reporteBd, 4, "Archivo no subido al NFS");
            }

        } catch (Exception ex) {
            actualizarEstadoReporte(reporteBd, 5, "error en generar reporte actas generales: " + ex.getMessage());
            log.error("Error en generar reporte actas generales: {}", ex.getMessage(), ex);
        }
    }

    private int validarRequest(ReporteRequest requestReporte) {
        int cantidadCsv = 1;
        if( (requestReporte.getIdEleccion() == 13 || requestReporte.getIdEleccion() == 14)
                &&  ( null == requestReporte.getIdDistritoElectoral() || requestReporte.getIdDistritoElectoral() == 0))
        {
            cantidadCsv = 27;
        }
        return cantidadCsv;
    }

    private static List<String> getCamposGenericos(ActasResponseReporteDto elemento, Integer idEleccion) {
        List<String> datos = new ArrayList<>();
        datos.add(elemento.getDescripcionEleccion());
        datos.add(elemento.getDescripcionAmbitoGeografico());
        datos.add(elemento.getUbigeoNivel01());
        datos.add(elemento.getUbigeoNivel02());
        datos.add(elemento.getUbigeoNivel03());
        datos.add(elemento.getCentroPoblado());
        datos.add(elemento.getNombreLocalVotacion());
        datos.add(elemento.getCodigoMesa());
        datos.add(elemento.getDescripcionEstadoActa());
        datos.add(String.valueOf(elemento.getTotalElectoresHabiles()));
        if(idEleccion != 10) {
            datos.add(String.valueOf(elemento.getNombreOrganizacionPolitica()));
            datos.add(String.valueOf(elemento.getTotalVotosEmitidos()));
        }

        return datos;
    }

    @Async
    @Override
    public void generarReporteCsv(ReporteRequest requestReporte, TabReporte reporteBd) {
        log.info(":::REPORTE - CSV::: {} ::: {}", requestReporte, reporteBd);

        // Determinar cuántos CSV generar
        int cantidadCsv = validarRequest(requestReporte);
        boolean seGeneroAlMenosUnCsv = false;
        // 1. Estado inicial
        log.info("TIPO ELECCION : " + reporteBd.getTipoEleccion());
        actualizarEstadoReporte(reporteBd, 1, "Reporte en proceso");

        try (var zipBaos = new ByteArrayOutputStream();
             var zos = new ZipOutputStream(zipBaos, StandardCharsets.UTF_8)) {

            String eleccion = TipoEleccionEnum.obtenerDescripcion(Long.valueOf(requestReporte.getIdEleccion()));
            String tipoDescripcionReporte = TipoReporteEnum.obtenerDescripcion(requestReporte.getTipoReporte());
            String fechaHoraReporte = DateTimeUtil.formatLocalDateTimeToReportTimestamp(LocalDateTime.now());

            // Si la elección es 13 o 14 → generar 27 CSV (uno por distrito electoral)
            if ((requestReporte.getIdEleccion() == 13 || requestReporte.getIdEleccion() == 14)
                    && (requestReporte.getIdDistritoElectoral() == null ||  requestReporte.getIdDistritoElectoral() == 0))
            {

                for (int distrito = 1; distrito <= cantidadCsv; distrito++) {
                    var requestActas = buildRequestActas(requestReporte, distrito);

                    boolean generado = generarCsvIndividual(requestReporte, reporteBd, requestActas, zos, eleccion, tipoDescripcionReporte, distrito);
                    if (generado) {
                        seGeneroAlMenosUnCsv = true;
                    }
                }

            } else {
                // Caso normal → generar 1 solo CSV
                var requestActas = buildRequestActas(requestReporte, null == requestReporte.getIdDistritoElectoral() ? 0: requestReporte.getIdDistritoElectoral());

                seGeneroAlMenosUnCsv = generarCsvIndividual(requestReporte, reporteBd, requestActas, zos, eleccion, tipoDescripcionReporte, null);
            }

            // Solo guardar ZIP si se generó algo
            if (seGeneroAlMenosUnCsv) {
                zos.finish();
                byte[] zipBytes = zipBaos.toByteArray();

                var archivoGuardado = tabArchivoReporteService.guardarArchivoReporte(
                        reporteBd,
                        zipBytes,
                        pathNfs.concat(pathFiles).concat(pathNfs),
                        "zip"
                );

                reporteBd.setArchivo(archivoGuardado);

                if (archivoGuardado != null) {
                    actualizarEstadoReporte(reporteBd, 2, "Reporte terminado");
                } else {
                    actualizarEstadoReporte(reporteBd, 4, "Archivo no subido al NFS");
                }
            } else {
                actualizarEstadoReporte(reporteBd, 3, "No se encontraron registros para generar CSV");
            }

        } catch (Exception ex) {
            actualizarEstadoReporte(reporteBd, 5, "Error en generar reporte: " + ex.getMessage());
            log.error("Error en generar reporte actas generales: {}", ex.getMessage(), ex);
        }
    }

    private ActaRequestDto buildRequestActas(ReporteRequest requestReporte, int idDistritoElectoral) {
        return ActaRequestDto.builder()
                .codigoOp(parseSafeInt(requestReporte.getCodigoOp()))
                .idEleccion(requestReporte.getIdEleccion())
                .idAmbitoGeografico(requestReporte.getIdAmbitoGeografico())
                .ubigeoNivel01(requestReporte.getUbigeoNivel01())
                .ubigeoNivel02(requestReporte.getUbigeoNivel02())
                .idUbigeo(requestReporte.getIdUbigeo())
                .codigoLocalVotacion(requestReporte.getCodigoLocalVotacion())
                .idDistritoElectoral(idDistritoElectoral)
                .build();
    }

    private boolean generarCsvIndividual(ReporteRequest requestReporte,
                                         TabReporte reporte,
                                         ActaRequestDto requestActas,
                                         ZipOutputStream zos,
                                         String eleccion,
                                         String tipoDescripcionReporte,
                                         Integer distrito) throws IOException {

        var reporteData = actaService.obtenerActasReporteSinArchivo(requestActas);

        if (reporteData.getRegistrosReporte().isEmpty()) {
            log.info("No existen registros en BD para distrito {}", distrito);
            return false;
        }

        var cabeceraReporte = PrUtils.cabecerasPorEleccion(
                requestReporte.getIdEleccion(),
                reporteData.getListaOrgPolitica()
        );

        var format = CSVFormat.DEFAULT.builder()
                .setHeader(cabeceraReporte.toArray(String[]::new))
                .get();

        try (var baos = new ByteArrayOutputStream();
             var writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             var csvPrinter = new CSVPrinter(writer, format)) {

            int longitud = reporteData.getListaOrgPolitica().size();

            reporteData.getRegistrosReporte().forEach(elemento -> {
                try {
                    var datos = new ArrayList<>(getCamposGenericos(elemento, requestReporte.getIdEleccion()));
                    var votos = elemento.getListaVotosOrgPolitica();

                    if (votos == null || votos.isEmpty()) {
                        if (longitud > 0) {
                            datos.addAll(Collections.nCopies(longitud - 1, "0"));
                        }
                    } else {
                        datos.addAll(votos.stream().map(String::valueOf).toList());
                    }
                    csvPrinter.printRecord(datos);
                } catch (IOException e) {
                    throw new WriteReportException("ERROR EN ESCRIBIR CSV " + e.getMessage());
                }
            });

            csvPrinter.flush();

            // Nombre de archivo
            String nombreArchivo = "";
            String nombreDistritoElectoral = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-a");
            String fechaFormateada = sdf.format(reporte.getFechaProceso());
            if (distrito != null) {
                nombreDistritoElectoral = DistritoElectoralEnum.obtenerDescripcion(distrito);
                nombreArchivo = "PR-ESP_" + eleccion + "_" + nombreDistritoElectoral + "_" + fechaFormateada + "_" +  reporte.getPorcentaje();
            } else {
                nombreArchivo = "PR-ESP_" + eleccion + "_" +  fechaFormateada + "_" +  reporte.getPorcentaje();
            }
            nombreArchivo += ".csv";

            zos.putNextEntry(new ZipEntry(nombreArchivo));
            zos.write(baos.toByteArray());
            zos.closeEntry();
        }

        return true;
    }
}