package pe.gob.onpe.pradminbackend.model.bd.service.reportes.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.gob.onpe.pradminbackend.exception.WriteReportException;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabArchivo;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporte;
import pe.gob.onpe.pradminbackend.model.bd.repository.MaeFechaRepository;
import pe.gob.onpe.pradminbackend.model.bd.secondary.repository.TabReporteRepository;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ActaReporteService;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ReporteArchivoService;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ResumenGeneralReporteService;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.TabArchivoReporteService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.*;
import pe.gob.onpe.pradminbackend.utils.DateTimeUtil;
import pe.gob.onpe.pradminbackend.utils.DateUtil;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionMayusculaEnum;
import pe.gob.onpe.pradminbackend.utils.excel.NewExcelUtil;
import pe.gob.onpe.pradminbackend.utils.PrUtils;
import pe.gob.onpe.pradminbackend.utils.enums.DistritoElectoralEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoReporteEnum;
import pe.gob.onpe.pradminbackend.utils.excel.ExcelDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteArchivoServiceImpl implements ReporteArchivoService {

    public static final String ADMIN = "admin";
    private final TabReporteRepository tabReporteRepository;
    private final ActaReporteService actaService;
    private final NewExcelUtil newExcelUtil;
    private final TabArchivoReporteService tabArchivoReporteService;
    private final MaeFechaRepository maeFechaRepository;

    private final ResumenGeneralReporteService resumenGeneralService;

    private static final String MSG_LOG_NO_EXISTEN_REGISTROS_PARA_GENERAR_REPORTE = "No existen registros en BD para generar el reporte";
    private static final String MSG_LOG_REPORTE_EN_PROCESO = "Reporte en proceso";
    private static final String MSG_LOG_REPORTE_TERMINADO = "Reporte terminado";
    private static final String MSG_LOG_ARVHIVO_NO_SUBIDO_AL_NFS = "Archivo no subido al NFS";

    @Value("${pr.nfs.path}")
    private String pathNfs;

    @Value("${pr.nfs.files}")
    private String pathFiles;

    @Async
    @Override
    public void generarReporte(ReporteRequest requestReporte, TabReporte reporteBd) {
        log.info(":::REPORTE 1::: {} ::: {}", requestReporte, reporteBd);

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
                actualizarEstadoReporte(reporteBd, 3, MSG_LOG_NO_EXISTEN_REGISTROS_PARA_GENERAR_REPORTE);
                return;
            }

            // 3. Generar archivo
            actualizarEstadoReporte(reporteBd, 1, MSG_LOG_REPORTE_EN_PROCESO);

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
                    pathNfs.concat(pathFiles),
                    "xlsx"

            );

            reporteBd.setArchivo(archivoGuardado);

            if (archivoGuardado != null) {
                actualizarEstadoReporte(reporteBd, 2, MSG_LOG_REPORTE_TERMINADO);
            } else {
                actualizarEstadoReporte(reporteBd, 4, MSG_LOG_ARVHIVO_NO_SUBIDO_AL_NFS);
            }

        } catch (Exception ex) {
            actualizarEstadoReporte(reporteBd, 5, "error en generar reporte actas generales: " + ex.getMessage());
            log.error("Error en generar reporte actas generales: {}", ex.getMessage(), ex);

        } finally {
            System.gc();
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
                                .build();

                        var reporteData = actaService.obtenerActasReporteObservadas(requestActas);

                        if (reporteData.getRegistrosReporte().isEmpty()) {
                            actualizarEstado(reporte, 3, MSG_LOG_NO_EXISTEN_REGISTROS_PARA_GENERAR_REPORTE);
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
                                pathNfs + pathFiles,
                                "xlsx"
                        );

                        reporte.setArchivo(archivo);
                        actualizarEstado(reporte, archivo != null ? 2 : 4, archivo != null ? MSG_LOG_REPORTE_TERMINADO : MSG_LOG_ARVHIVO_NO_SUBIDO_AL_NFS);

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
        log.info(":::REPORTE - CSV 2::: {} ::: {}", request, reporte);

        String fechaHoraReporte = DateTimeUtil.dateToHoraFormatReporte(LocalDateTime.now());

        var requestActas = ActaRequestDto.builder()
                .codigoOp(Optional.ofNullable(request.getCodigoOp()).map(Integer::valueOf).orElse(null))
                .idEleccion(request.getIdEleccion())
                .idAmbitoGeografico(request.getIdAmbitoGeografico())
                .ubigeoNivel01(request.getUbigeoNivel01())
                .ubigeoNivel02(request.getUbigeoNivel02())
                .idUbigeo(request.getIdUbigeo())
                .codigoLocalVotacion(request.getCodigoLocalVotacion())
                .idDistritoElectoral(request.getIdDistritoElectoral())
                .build();

        var reporteDataObservadas = actaService.obtenerActasReporteObservadasCsv(requestActas);

        if (reporteDataObservadas.getRegistrosReporte().isEmpty()) {
            actualizarEstado(reporte, 3, MSG_LOG_NO_EXISTEN_REGISTROS_PARA_GENERAR_REPORTE);
            return;
        }

        actualizarEstado(reporte, 1, MSG_LOG_REPORTE_EN_PROCESO);

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

                String eleccion = TipoEleccionMayusculaEnum.obtenerDescripcion(Long.valueOf(request.getIdEleccion()));
                String tipoDescripcionReporte = TipoReporteEnum.obtenerDescripcion(request.getTipoReporte());
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
                    pathNfs.concat(pathFiles),
                    "zip"
            );

            reporte.setArchivo(archivoGuardado);
            if (archivoGuardado != null) {
                actualizarEstadoReporte(reporte, 2, MSG_LOG_REPORTE_TERMINADO);
            } else {
                actualizarEstadoReporte(reporte, 4, MSG_LOG_ARVHIVO_NO_SUBIDO_AL_NFS);
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
        tabReporteRepository.save(reporte);
        log.info("Estado del reporte observados actualizado a {}: {}", estado, logMessage);
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
        log.info(":::REPORTE - CSV 3::: {} ::: {}", requestReporte, reporteBd);

        // Determinar cuántos CSV generar
        int cantidadCsv = validarRequest(requestReporte);
        boolean seGeneroAlMenosUnCsv = false;
        // 1. Estado inicial
        actualizarEstadoReporte(reporteBd, 1, MSG_LOG_REPORTE_EN_PROCESO);

        try (var zipBaos = new ByteArrayOutputStream();
             var zos = new ZipOutputStream(zipBaos, StandardCharsets.UTF_8)) {

            String eleccion = TipoEleccionMayusculaEnum.obtenerDescripcion(Long.valueOf(requestReporte.getIdEleccion()));

            // Si la elección es 13 o 14 → generar 27 CSV (uno por distrito electoral)
            if ((requestReporte.getIdEleccion() == 13 || requestReporte.getIdEleccion() == 14)
                    && (requestReporte.getIdDistritoElectoral() == null ||  requestReporte.getIdDistritoElectoral() == 0))
            {

                for (int distrito = 1; distrito <= cantidadCsv; distrito++) {
                    var requestActas = buildRequestActas(requestReporte, distrito);

                    boolean generado = generarCsvIndividual(requestReporte, reporteBd,  requestActas, zos, eleccion, distrito);
                    if (generado) {
                        seGeneroAlMenosUnCsv = true;
                    }
                }

            } else {
                // Caso normal → generar 1 solo CSV
                var requestActas = buildRequestActas(requestReporte, null == requestReporte.getIdDistritoElectoral() ? 0: requestReporte.getIdDistritoElectoral());

                seGeneroAlMenosUnCsv = generarCsvIndividual(requestReporte,reporteBd, requestActas, zos, eleccion, null);
            }

            // Solo guardar ZIP si se generó algo
            if (seGeneroAlMenosUnCsv) {
                zos.finish();
                byte[] zipBytes = zipBaos.toByteArray();

                var archivoGuardado = tabArchivoReporteService.guardarArchivoReporte(
                        reporteBd,
                        zipBytes,
                        pathNfs.concat(pathFiles),
                        "zip"
                );

                reporteBd.setArchivo(archivoGuardado);

                if (archivoGuardado != null) {
                    actualizarEstadoReporte(reporteBd, 2, MSG_LOG_REPORTE_TERMINADO);
                } else {
                    actualizarEstadoReporte(reporteBd, 4, MSG_LOG_ARVHIVO_NO_SUBIDO_AL_NFS);
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
                nombreArchivo = "PR_" + eleccion + "_" + nombreDistritoElectoral + "_" + fechaFormateada + "_" +  reporte.getPorcentaje();
            } else {
                nombreArchivo = "PR_" + eleccion + "_" +  fechaFormateada + "_" +  reporte.getPorcentaje();
            }
            nombreArchivo += ".csv";

            zos.putNextEntry(new ZipEntry(nombreArchivo));
            zos.write(baos.toByteArray());
            zos.closeEntry();
        }

        return true;
    }
}