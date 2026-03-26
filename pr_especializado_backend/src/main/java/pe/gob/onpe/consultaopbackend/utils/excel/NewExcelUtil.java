package pe.gob.onpe.consultaopbackend.utils.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRespuestaDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.ActaRespuestaReporteDto;
import pe.gob.onpe.consultaopbackend.utils.DateTimeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class NewExcelUtil {

    public static final String ACTA = "::ACTA::";
    public static final String RESOLUCION = "::RESOLUCION::";
    public static final String REPORTE = "Reporte";

    @Value("${app.consulta.backend}")
    private String urlConsultaOpBackend;

    public ExcelDto generarExcelDto(ReporteRespuestaDto reporteRespuesta, String codigoOp) {
        var excelDto = new ExcelDto();
        var sheetDtos = new ArrayList<SheetDto>();
        var rowDtos = new ArrayList<RowDto>();
        List<String> header = null;
        List<String> emptyValues = Collections.emptyList();

        final var cabeceraGeneral = List.of(
                "TIPO DE ELECCIÓN", "ÁMBITO", "DEPARTAMENTO / CONTINENTE", "PROVINCIA / PAÍS",
                "DISTRITO / ESTADO", "LOCAL DE VOTACIÓN", "NÚMERO DE MESA", "ESTADO DEL ACTA", "ELECTORES HÁBILES"
        );
        final var actaResoluciones = List.of("VER ACTAS","", "VER RESOLUCIÓN(ES)");

        if (!reporteRespuesta.getRegistrosReporte().isEmpty()) {
            if (codigoOp != null) {
                header = Stream.of(cabeceraGeneral, List.of("VOTOS OBTENIDOS"), actaResoluciones)
                        .flatMap(List::stream)
                        .toList();
            } else if (reporteRespuesta.getListaOrgPolitica() != null && !reporteRespuesta.getListaOrgPolitica().isEmpty()) {
                header = Stream.of(cabeceraGeneral, reporteRespuesta.getListaOrgPolitica(), actaResoluciones)
                        .flatMap(List::stream)
                        .toList();
                emptyValues = Stream.generate(() -> "").limit(reporteRespuesta.getListaOrgPolitica().size()).toList();
            } else {
                header = Stream.of(cabeceraGeneral, actaResoluciones).flatMap(List::stream).toList();
            }

            for (var elemento : reporteRespuesta.getRegistrosReporte()) {
                var row = new ArrayList<String>();
                var urlActa = ACTA + urlConsultaOpBackend + "/actas/file?id=";
                var urlResol = RESOLUCION + urlConsultaOpBackend + "/actas/file?id=";

                row.add(elemento.getDescripcionEleccion());
                row.add(elemento.getDescripcionAmbitoGeografico());
                row.add(elemento.getUbigeoNivel01());
                row.add(elemento.getUbigeoNivel02());
                row.add(elemento.getUbigeoNivel03());
                row.add(elemento.getNombreLocalVotacion());
                row.add(elemento.getCodigoMesa());
                row.add(elemento.getDescripcionEstadoActa());
                row.add(String.valueOf(elemento.getTotalElectoresHabiles()));

                if (codigoOp != null) {
                    row.add(String.valueOf(elemento.getTotalVotosEmitidos()));
                } else if (reporteRespuesta.getListaOrgPolitica() != null && !reporteRespuesta.getListaOrgPolitica().isEmpty()) {
                    if (elemento.getListaVotosOrgPolitica() == null || elemento.getListaVotosOrgPolitica().isEmpty()) {
                        row.addAll(emptyValues);
                    } else {
                        elemento.getListaVotosOrgPolitica().forEach(v -> row.add(String.valueOf(v)));
                    }
                }

                var idActa = Optional.ofNullable(elemento.getArchivoActaId()).orElse(Collections.emptyList());
                var idResol = Optional.ofNullable(elemento.getArchivoResolucionId()).orElse(Collections.emptyList());

                row.add(idActa.isEmpty() ? "" : obtenerCadenaActaResoluciones(idActa, urlActa));
                row.add(idResol.isEmpty() ? "" : obtenerCadenaActaResoluciones(idResol, urlResol));

                rowDtos.add(RowDto.builder().values(row).build());
            }
        }

        sheetDtos.add(SheetDto.builder()
                .name(REPORTE)
                .rowDtos(rowDtos)
                .headerDto(HeaderDto.builder().name(header).build())
                .build());

        excelDto.setSheetDtos(sheetDtos);
        excelDto.setName(REPORTE);
        return excelDto;
    }

    private String obtenerCadenaActaResoluciones(List<String> idResoluciones, String urlServicio) {
        if (idResoluciones == null || idResoluciones.isEmpty()) {
            return "";
        }

        return idResoluciones.stream()
                .map(id -> urlServicio + id)
                .collect(Collectors.joining(";"));
    }



    public byte[] generateExcelReporte(List<String> listaOrgPolitica, ExcelDto excelDto, String codigoOp, ActaRespuestaReporteDto totales, Integer tipoReporte) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final int filaCabecera = 8;

            int actaInicioColumnaAgrupar = 9;
            int actaFinColumnaAgrupar = 10;
            int resolucionInicioColumnaAgrupar = 11;
            int resolucionFinColumnaAgrupar = 13;

            if (codigoOp != null) {
                resolucionInicioColumnaAgrupar = 12;
                resolucionFinColumnaAgrupar = 14;
                actaInicioColumnaAgrupar = 10;
                actaFinColumnaAgrupar = 11;
            } else if (listaOrgPolitica != null && !listaOrgPolitica.isEmpty()) {
                resolucionInicioColumnaAgrupar = 11 + listaOrgPolitica.size();
                resolucionFinColumnaAgrupar = 13 + listaOrgPolitica.size();
                actaInicioColumnaAgrupar = 9 + listaOrgPolitica.size();
                actaFinColumnaAgrupar = 10 + listaOrgPolitica.size();
            }

            Sheet sheet = workbook.createSheet(REPORTE);
            sheet.setColumnWidth(0, 10 * 256);
            int numColumnas = excelDto.getSheetDtos().get(0).getHeaderDto().getName().size() - 1;
            for (int k = 1; k <= numColumnas + 2 ; k++) {
                sheet.setColumnWidth(k, 30 * 256);
            }

            try (InputStream logoOnpe = this.getInputStream()) {
                byte[] inputImageBytes1 = IOUtils.toByteArray(logoOnpe);
                int inputImagePictureID1 = workbook.addPicture(inputImageBytes1, Workbook.PICTURE_TYPE_JPEG);
                XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 1, 2);
                drawing.createPicture(anchor, inputImagePictureID1);
            }

            if (totales != null) {
                generarTotalesReporte(sheet, tipoReporte, totales, codigoOp);
            }

            CellStyle estiloAlterno = crearEstiloAlterno(workbook,false);
            CellStyle estiloNormal = crearEstiloNormal(workbook,false);
            CellStyle estiloAlternoFuente = crearEstiloAlterno(workbook,true);
            CellStyle estiloNormalFuente = crearEstiloNormal(workbook,true);

            for (SheetDto sheetDto : excelDto.getSheetDtos()) {
                int currentRowPosition = filaCabecera;
                if (sheetDto.getHeaderDto() != null) {
                    Row headerRow = sheet.createRow(currentRowPosition);
                    List<String> headerNames = sheetDto.getHeaderDto().getName();
                    CellStyle estiloCabecera = crearEstiloCabecera(workbook);
                    for (int i = 0; i < headerNames.size(); i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellStyle(estiloCabecera);
                        cell.setCellValue(headerNames.get(i));
                    }
                    sheet.addMergedRegion(new CellRangeAddress(filaCabecera, filaCabecera, actaInicioColumnaAgrupar, actaFinColumnaAgrupar));
                    sheet.addMergedRegion(new CellRangeAddress(filaCabecera, filaCabecera, resolucionInicioColumnaAgrupar, resolucionFinColumnaAgrupar));
                }

                List<RowDto> rowDtoList = sheetDto.getRowDtos();
                if (rowDtoList != null && !rowDtoList.isEmpty()) {
                    for (int i = 0; i < rowDtoList.size(); i++) {
                        currentRowPosition++;
                        Row row = sheet.createRow(currentRowPosition);
                        List<String> textosList = rowDtoList.get(i).getValues();
                        for (int k = 0; k < textosList.size(); k++) {

                            String value = textosList.get(k);
                            if (value.contains(ACTA)) {
                                crearEnlaceActasResoluciones("Acta",row, workbook.getCreationHelper(), value.replace(ACTA, ""), k, estiloAlternoFuente, estiloNormalFuente, i);
                            } else if (value.contains(RESOLUCION)) {
                                crearEnlaceActasResoluciones("Resolución",row, workbook.getCreationHelper(), value.replace(RESOLUCION, ""), k + 1 , estiloAlternoFuente, estiloNormalFuente, i);
                            } else {
                                if( k + 1 != textosList.size()) {
                                    Cell cell = row.createCell(k);
                                    cell.setCellValue(value);
                                    cell.setCellStyle(i % 2 != 0 ? estiloAlterno : estiloNormal);
                                }

                            }
                        }
                    }

                    currentRowPosition += 2;
                    Row footerRow = sheet.createRow(currentRowPosition);
                    Cell footerCell = footerRow.createCell(0);
                    footerCell.setCellValue("Documento exportado desde el Sistema de Presentación de Resultados");
                    CellStyle footerStyle = workbook.createCellStyle();
                    footerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    footerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Font footerFont = workbook.createFont();
                    footerFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                    footerFont.setBold(true);
                    footerStyle.setFont(footerFont);
                    footerCell.setCellStyle(footerStyle);
                    sheet.addMergedRegion(new CellRangeAddress(currentRowPosition, currentRowPosition, 0, numColumnas));
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generateExcelReporte: ", e);
            return new byte[0];
        }

    }

    private CellStyle crearEstiloAlterno(Workbook workbook, boolean conFuente) {
        CellStyle estilo = workbook.createCellStyle();

        if (conFuente) {
            Font fuente = workbook.createFont();
            fuente.setUnderline(Font.U_SINGLE);
            fuente.setColor(IndexedColors.BLUE.getIndex());
            estilo.setFont(fuente);
        }
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setWrapText(true);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        return estilo;
    }

    private CellStyle crearEstiloNormal(Workbook workbook, boolean conFuente) {
        CellStyle estilo = workbook.createCellStyle();

        if (conFuente) {
            Font fuente = workbook.createFont();
            fuente.setUnderline(Font.U_SINGLE);
            fuente.setColor(IndexedColors.BLUE.getIndex());
            estilo.setFont(fuente);
        }

        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setWrapText(true);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        return estilo;
    }
    private CellStyle crearEstiloCabecera(Workbook workbook) {
        Font fuenteCabecera = workbook.createFont();
        fuenteCabecera.setBold(true);
        fuenteCabecera.setColor(IndexedColors.WHITE.getIndex());

        CellStyle estiloCabecera = workbook.createCellStyle();
        estiloCabecera.setFont(fuenteCabecera);
        estiloCabecera.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecera.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecera.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        estiloCabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloCabecera.setWrapText(true);
        estiloCabecera.setBorderTop(BorderStyle.THIN);
        estiloCabecera.setBorderBottom(BorderStyle.THIN);
        estiloCabecera.setBorderLeft(BorderStyle.THIN);
        estiloCabecera.setBorderRight(BorderStyle.THIN);

        return estiloCabecera;
    }

    private InputStream getInputStream() {
        return this.getClass().getClassLoader().getResourceAsStream("imagenes/logo_onpe_2022.jpg");
    }

    private void generarTotalesReporte(Sheet sheet, Integer tipoReporte, ActaRespuestaReporteDto totales, String codigoOp) {
        Workbook workbook = sheet.getWorkbook();

        // Crear estilos una sola vez
        CellStyle numericStyle = createNumericStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // Agregar título
        agregarTitulo(sheet, titleStyle);

        // Fila 1: Fecha, Actas contabilizadas, Electores hábiles
        Row row1 = sheet.createRow(3);
        setCell(row1, 0, "Fecha y hora de actualización: ");
        setCell(row1, 1, DateTimeUtil.dateToHoraFormatPR(totales.getFechaActualizacion()));
        setCell(row1, 2, "Acta contabilizadas: ");
        setCell(row1, 3, totales.getContabilizadas(), numericStyle);

        if (tipoReporte == 1) {
            setCell(row1, 5, "Electores hábiles: ");
            setCell(row1, 6, totales.getTotalElectoresHabiles(), numericStyle);
        }

        // Fila 2: Total de actas, Actas para envío, Electores asistentes
        Row row2 = sheet.createRow(4);
        setCell(row2, 0, "Total de actas : ");
        setCell(row2, 1, totales.getTotalActas(), numericStyle);
        setCell(row2, 2, "Acta para envío al JEE: ");
        setCell(row2, 3, totales.getEnviadasJee(), numericStyle);

        if (tipoReporte == 1) {
            setCell(row2, 5, "Electores asistentes: ");
            setCell(row2, 6, totales.getTotalAsistentes(), numericStyle);
        }

        // Fila 3 (solo para tipo 1): Votos OP, Actas pendientes, Electores ausentes
        if (tipoReporte == 1) {
            Row row3 = sheet.createRow(5);
            if (codigoOp != null) {
                setCell(row3, 0, "Votos obtenidos por la OP :");
                setCell(row3, 1, totales.getVotosOrgPolitica(), numericStyle);
            }
            setCell(row3, 2, "Actas pendientes: ");
            setCell(row3, 3, totales.getPendientesJee(), numericStyle);
            setCell(row3, 5, "Electores ausentes: ");
            setCell(row3, 6, totales.getTotalAusentes(), numericStyle);
        }

        sheet.autoSizeColumn(0); // solo ajustar lo más relevante
        sheet.autoSizeColumn(4);
    }

    private CellStyle createNumericStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 20);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void agregarTitulo(Sheet sheet, CellStyle titleStyle) {
        Row row = sheet.createRow(1);
        Cell cell = row.createCell(1);
        cell.setCellValue("REPORTE DEL MÓDULO ESPECIALIZADO");
        cell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));
    }

    private void setCell(Row row, int col, String value) {
        row.createCell(col).setCellValue(value);
    }

    private void setCell(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void crearEnlace(Cell cell, CreationHelper helper, String texto, String url, CellStyle estiloPar, CellStyle estiloImpar, int fila) {
        XSSFHyperlink hyperlink = (XSSFHyperlink) helper.createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(url);

        cell.setHyperlink(hyperlink);
        cell.setCellValue(texto);
        cell.setCellStyle(fila % 2 != 0 ? estiloPar : estiloImpar);
    }


    private void crearEnlaceActasResoluciones(String texto,Row row, CreationHelper helper, String urls, int columnaInicio, CellStyle estiloPar, CellStyle estiloImpar, int fila) {
        if (urls == null || urls.isBlank()) return;

        String[] resoluciones = urls.split(";");
        for (int idx = 0; idx < resoluciones.length; idx++) {
            String resolucionUrl = resoluciones[idx].trim();
            if (!resolucionUrl.isEmpty()) {
                Cell celda = row.createCell(columnaInicio + idx);
                crearEnlace(celda, helper, texto + " " + (idx + 1), resolucionUrl, estiloPar, estiloImpar, fila);
            }
        }
    }


}
