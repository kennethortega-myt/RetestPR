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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class ExcelUtil {

    public static final String ACTA = "::ACTA::";
    public static final String RESOLUCION = "::RESOLUCION::";
    public static final String REPORTE = "Reporte";

    @Value("${app.consulta.backend}")
    private String urlConsultaOpBackend;

    public ExcelDto generarExcelDto(ReporteRespuestaDto reporteRespuesta, String codigoOp) {

        ExcelDto excelDtoRetorno = new ExcelDto();
        List<SheetDto> sheetDtoListRetorno = new ArrayList<>();
        List<String> headerParaDevovler = new ArrayList<>();
        List<RowDto> rowDtoListRetorno = new ArrayList<>();

        List<String> cabeceraGeneral = List.of("TIPO DE ELECCIÓN","ÁMBITO","DEPARTAMENTO / CONTINENTE","PROVINCIA / PAÍS","DISTRITO / ESTADO","LOCAL DE VOTACIÓN","NÚMERO DE MESA","ESTADO DEL ACTA","ELECTORES HÁBILES");
        List<String> votosObtenidos = List.of("VOTOS OBTENIDOS");
        List<String> actaResoluciones = List.of("VER ACTA","VER RESOLUCIÓN(ES)");
        List<String> listaElementosVacios;
        //elemtos del detalle
        if(!reporteRespuesta.getRegistrosReporte().isEmpty()) {
            if (null != codigoOp)  {
                listaElementosVacios = Collections.emptyList();
                headerParaDevovler = Stream.of(cabeceraGeneral,votosObtenidos,actaResoluciones)
                        .flatMap(List::stream).toList();
            } else if(reporteRespuesta.getListaOrgPolitica() != null && !reporteRespuesta.getListaOrgPolitica().isEmpty()) {
                headerParaDevovler = Stream.of(cabeceraGeneral,reporteRespuesta.getListaOrgPolitica(),actaResoluciones)
                        .flatMap(List::stream).toList();
                listaElementosVacios = Stream.generate(() -> "")
                        .limit(reporteRespuesta.getListaOrgPolitica().size())
                        .toList();
            }else  {
                listaElementosVacios = Collections.emptyList();
                headerParaDevovler = Stream.of(cabeceraGeneral,actaResoluciones).flatMap(List::stream).toList();
            }

            reporteRespuesta.getRegistrosReporte().forEach(elemento -> {
                String urlActa = ACTA + urlConsultaOpBackend + "/actas/file?id=";
                String urlResolucion = RESOLUCION + urlConsultaOpBackend + "/actas/file?id=";

                //String idActa = elemento.getArchivoActaId() == null ? "" :elemento.getArchivoActaId();
                String idResoluciones = elemento.getArchivoResolucionId() == null || elemento.getArchivoResolucionId().isEmpty() ? "": elemento.getArchivoResolucionId().toString();
                List<String> rowsParaDevolver = new LinkedList<>();
                rowsParaDevolver.add(elemento.getDescripcionEleccion());
                rowsParaDevolver.add(elemento.getDescripcionAmbitoGeografico());
                rowsParaDevolver.add(elemento.getUbigeoNivel01());
                rowsParaDevolver.add(elemento.getUbigeoNivel02());
                rowsParaDevolver.add(elemento.getUbigeoNivel03());
                rowsParaDevolver.add(elemento.getNombreLocalVotacion());
                rowsParaDevolver.add(elemento.getCodigoMesa());
                rowsParaDevolver.add(elemento.getDescripcionEstadoActa());
                rowsParaDevolver.add(elemento.getTotalElectoresHabiles()+"");
                if (null != codigoOp)  {
                    rowsParaDevolver.add(elemento.getTotalVotosEmitidos()+"");
                } else if(reporteRespuesta.getListaOrgPolitica() != null && !reporteRespuesta.getListaOrgPolitica().isEmpty()) {
                    if (elemento.getListaVotosOrgPolitica() == null || elemento.getListaVotosOrgPolitica().isEmpty()){
                        rowsParaDevolver.addAll(listaElementosVacios);
                    } else {
                        elemento.getListaVotosOrgPolitica().forEach(obj -> rowsParaDevolver.add(obj+""));
                    }
                }
                //rowsParaDevolver.add(idActa.isEmpty() ? "" : (urlActa + idActa));
                rowsParaDevolver.add(idResoluciones.isEmpty() ? "" : obtenerCadenaResoluciones(elemento.getArchivoResolucionId(),urlResolucion));
                rowDtoListRetorno.add(RowDto.builder().values(rowsParaDevolver).build());
            });


        }

        sheetDtoListRetorno.add(SheetDto.builder().name(REPORTE).rowDtos(rowDtoListRetorno).headerDto(HeaderDto.builder().name(headerParaDevovler).build()).build());
        excelDtoRetorno.setSheetDtos(sheetDtoListRetorno);
        excelDtoRetorno.setName(REPORTE);
        return excelDtoRetorno;
    }

    private String obtenerCadenaResoluciones( List<String> idResuluciones, String urlServicio) {
        StringBuilder sb = new StringBuilder();

        if (idResuluciones == null || idResuluciones.isEmpty()) {
            return "";
        }

        for (int i = 0; i < idResuluciones.size(); i++) {
            sb.append(urlServicio).append(idResuluciones.get(i));
            if (i < idResuluciones.size() - 1) {
                sb.append(";"); // Agregar punto y coma entre elementos
            }
        }
        return sb.toString();
    }



    public byte[] generateExcelReporte(List<String> listaOrgPolitica,ExcelDto excelDto, String codigoOp, ActaRespuestaReporteDto totales, Integer tipoReporte) {
        byte[] bytes = null;
        try {

            int inicioColumnaAgrupar;
            int finColumnaAgrupar;
            final int filaCabecera = 8;

            if (null != codigoOp)  {
                inicioColumnaAgrupar = 11;
                finColumnaAgrupar = 13;
            } else if(listaOrgPolitica != null && !listaOrgPolitica.isEmpty()) {
                inicioColumnaAgrupar = 10 + listaOrgPolitica.size();
                finColumnaAgrupar = 13 + listaOrgPolitica.size();
            }else  {
                inicioColumnaAgrupar = 10;
                finColumnaAgrupar = 12;
            }

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet(REPORTE);
            sheet.setColumnWidth(0, 10 * 256);
            int numColumnas = excelDto.getSheetDtos().get(0).getHeaderDto().getName().size() -1;
            for(int k=1; k<=numColumnas; k++) {
                sheet.setColumnWidth(k, 30 * 256);
            }

            //logo onpe
            final String IMG_LOGO_ONPE = "imagenes/logo_onpe_2022.jpg";
            InputStream logoOnpe = this.getInputStream(IMG_LOGO_ONPE);
            byte[] inputImageBytes1 = IOUtils.toByteArray(logoOnpe);

            //icono acta

            CreationHelper helper = workbook.getCreationHelper();

            int inputImagePictureID1 = workbook.addPicture(inputImageBytes1, Workbook.PICTURE_TYPE_JPEG);
            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor ironManAnchor = new XSSFClientAnchor();
            ironManAnchor.setCol1(0); // Sets the column (0 based) of the first cell.
            ironManAnchor.setCol2(1); // Sets the column (0 based) of the Second cell.
            ironManAnchor.setRow1(0); // Sets the row (0 based) of the first cell.
            ironManAnchor.setRow2(2); // Sets the row (0 based) of the Second cell.
            drawing.createPicture(ironManAnchor, inputImagePictureID1);

            //elementos totales
            if(totales != null) {
                generarTotalesReporte(sheet,tipoReporte,totales,codigoOp);
            }

            //estilo1
            CellStyle style1 = workbook.createCellStyle();
            style1.setFillForegroundColor((short) 67);
            style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style1.setAlignment(HorizontalAlignment.CENTER);
            style1.setVerticalAlignment(VerticalAlignment.CENTER);

            //estilo2
            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);

            //estilo3
            CellStyle style3 = workbook.createCellStyle();
            style3.setFillForegroundColor((short) 67);
            style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style3.setAlignment(HorizontalAlignment.CENTER);
            style3.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
            font.setBold(true);
            style3.setFont(font);

            //estilo4
            CellStyle style4 = workbook.createCellStyle();
            style4.setAlignment(HorizontalAlignment.CENTER);
            style4.setVerticalAlignment(VerticalAlignment.CENTER);
            style4.setAlignment(HorizontalAlignment.CENTER);
            style4.setVerticalAlignment(VerticalAlignment.CENTER);
            style4.setFont(font);

            excelDto.getSheetDtos().forEach(sheetDto -> {
                int currentRowPosition = 8;
                //data cabecera
                if (sheetDto.getHeaderDto()!=null){
                    Row headerRow = sheet.createRow(currentRowPosition);
                    List<String> headerNames = sheetDto.getHeaderDto().getName();
                    // estilo header
                    CellStyle style = workbook.createCellStyle();
                    style.setFillForegroundColor((short) 71);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    Font font1 = workbook.createFont();
                    font1.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                    font1.setBold(true);
                    style.setFont(font1);

                    for (int i=0; i<headerNames.size(); i++){
                        String nameHeader = headerNames.get(i);
                        Cell cellHeader = headerRow.createCell(i);

                        cellHeader.setCellStyle(style);
                        cellHeader.setCellValue(nameHeader);
                    }
                    sheet.autoSizeColumn(currentRowPosition,false);

                    sheet.addMergedRegion(new CellRangeAddress(filaCabecera, filaCabecera, inicioColumnaAgrupar, finColumnaAgrupar));

                }

                // data detalle
                List<RowDto> rowDtoList = sheetDto.getRowDtos();
                if (rowDtoList!=null && !rowDtoList.isEmpty()){
                    for (int i=0; i<rowDtoList.size(); i++) {
                        currentRowPosition++;
                        Row row = sheet.createRow(currentRowPosition);
                        List<String> textosList = rowDtoList.get(i).getValues();
                        for(int k=0; k<textosList.size(); k++) {
                            Cell cell = row.createCell(k);
                            String value = textosList.get(k);
                            if (value.contains(ACTA))  {
                                value = value.replace(ACTA,"");
                                crearIconoEnlaceActa(cell,helper,value,style3,style4,i);
                                continue;
                            } else if (value.contains(RESOLUCION)) {
                                value = value.replace(RESOLUCION,"");
                                crearIconoEnlaceResoluciones(row,helper,value,k,style3,style4,i);
                                continue;
                            } else  {
                                cell.setCellValue(value);
                            }
                            if(i!=0 && i%2 != 0) {
                                cell.setCellStyle(style1);
                            } else {
                                cell.setCellStyle(style2);
                            }


                        }
                        sheet.autoSizeColumn(currentRowPosition);

                        if (null != codigoOp)  {
                            sheet.setColumnWidth(11, 3000);
                            sheet.setColumnWidth(12, 3000);
                            sheet.setColumnWidth(13, 3000);
                        } else if(listaOrgPolitica != null && !listaOrgPolitica.isEmpty()) {
                            sheet.setColumnWidth(10 + listaOrgPolitica.size(), 3000);
                            sheet.setColumnWidth(11 + listaOrgPolitica.size(), 3000);
                            sheet.setColumnWidth(12 + listaOrgPolitica.size(), 3000);
                            sheet.setColumnWidth(13 + listaOrgPolitica.size(), 3000);
                        } else  {
                            sheet.setColumnWidth(10, 3000);
                            sheet.setColumnWidth(11, 3000);
                            sheet.setColumnWidth(12, 3000);
                            sheet.setColumnWidth(13, 3000);
                        }

                    }

                    currentRowPosition += 2;
                    Row headerRow = sheet.createRow(currentRowPosition);
                    Cell cellHeader = headerRow.createCell(0);
                    cellHeader.setCellValue("Documento exportado desde el Sistema de Presentación de Resultados");
                    CellStyle style = workbook.createCellStyle();
                    style.setFillForegroundColor((short) 55);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Font font2 = workbook.createFont();
                    font2.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                    font2.setBold(true);
                    style.setFont(font2);
                    cellHeader.setCellStyle(style);
                    sheet.addMergedRegion(new CellRangeAddress(currentRowPosition, currentRowPosition, 0, numColumnas));
                    sheet.autoSizeColumn(currentRowPosition);

                }
            });

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            bytes = outputStream.toByteArray();


        } catch (IOException e) {
            log.error("Error generateExcelReporte: ", e);
        }
        return bytes;
    }

    private InputStream getInputStream(final String pathResource) {
        return this.getClass().getClassLoader().getResourceAsStream(pathResource);
    }

    private void generarTotalesReporte(Sheet sheet,Integer tipoReporte,ActaRespuestaReporteDto totales,String codigoOp) {


        CellStyle style5 = sheet.getWorkbook().createCellStyle();
        DataFormat format = sheet.getWorkbook().createDataFormat();
        style5.setDataFormat(format.getFormat("#,##0"));

        if (tipoReporte == 1) {

            //titulo

            // Crear un estilo de celda para el título
            CellStyle titleStyle = sheet.getWorkbook().createCellStyle();

            // Crear una fuente para el título
            Font titleFont = sheet.getWorkbook().createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 20); // Tamaño de la fuente
            titleFont.setColor(IndexedColors.WHITE.getIndex()); // Color de fuente blanco

            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor((short) 71); // Color de fondo azul
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Crear una celda y unir 3 celdas en la fila 0
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("REPORTE DEL MÓDULO ESPECIALIZADO");
            cell.setCellStyle(titleStyle);

            // Unir celdas A1, B1 y C1
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));

            //primera fila
            Row rowFecha = sheet.createRow(3);
            Cell cellFecha = rowFecha.createCell(0);
            Cell cellFechaValue = rowFecha.createCell(1);
            cellFecha.setCellValue("Fecha y hora de actualización: ");
            cellFechaValue.setCellValue(DateTimeUtil.dateToHoraFormatPR(totales.getFechaActualizacion()));

            Cell cellContabilizadoTitulo = rowFecha.createCell(2);
            cellContabilizadoTitulo.setCellValue("Acta contabilizadas: ");
            Cell cellContabilizadoValue = rowFecha.createCell(3);
            cellContabilizadoValue.setCellValue(totales.getContabilizadas());
            cellContabilizadoValue.setCellStyle(style5);

            Cell cellElectoresTitulo = rowFecha.createCell(5);
            cellElectoresTitulo.setCellValue("Electores hábiles: ");
            Cell cellElectoresValor = rowFecha.createCell(6);
            cellElectoresValor.setCellValue(totales.getTotalElectoresHabiles());
            cellElectoresValor.setCellStyle(style5);

            sheet.autoSizeColumn(0);

            //segunda fila
            Row rowHora = sheet.createRow(4);
            Cell cellActas = rowHora.createCell(0);
            Cell cellActasValue = rowHora.createCell(1);
            cellActas.setCellValue("Total de actas : ");
            cellActasValue.setCellValue(totales.getTotalActas());
            cellActasValue.setCellStyle(style5);

            Cell cellParaEnvio = rowHora.createCell(2);
            cellParaEnvio.setCellValue("Acta para envio al JEE: ");
            Cell cellParaEnvioValor = rowHora.createCell(3);
            cellParaEnvioValor.setCellValue(totales.getEnviadasJee());
            cellParaEnvioValor.setCellStyle(style5);

            Cell cellAsistentes = rowHora.createCell(5);
            cellAsistentes.setCellValue("Electores asistentes: ");
            Cell cellAsistentesValor = rowHora.createCell(6);
            cellAsistentesValor.setCellValue(totales.getTotalAsistentes());
            cellAsistentesValor.setCellStyle(style5);

            sheet.autoSizeColumn(4);
            //tercera fila
            Row rowUsuario = sheet.createRow(5);
            if (codigoOp != null) {
                Cell cellVotosOptenidos = rowUsuario.createCell(0);
                Cell rowVotosOptenidosValor = rowUsuario.createCell(1);
                cellVotosOptenidos.setCellValue("Votos obtenidos por la OP :");
                rowVotosOptenidosValor.setCellValue(totales.getVotosOrgPolitica());
                cellVotosOptenidos.setCellStyle(style5);
            }
            Cell cellActaPendiente = rowUsuario.createCell(2);
            cellActaPendiente.setCellValue("Actas pendientes: ");
            Cell cellActaPendienteValor = rowUsuario.createCell(3);
            cellActaPendienteValor.setCellValue(totales.getPendientesJee());
            cellActaPendienteValor.setCellStyle(style5);

            Cell cellAusentes = rowUsuario.createCell(5);
            cellAusentes.setCellValue("Electores ausentes: ");
            Cell cellAusentesValor = rowUsuario.createCell(6);
            cellAusentesValor.setCellValue(totales.getTotalAusentes());
            cellAusentesValor.setCellStyle(style5);




        } else  if (tipoReporte == 2) {

            //titulo

            // Crear un estilo de celda para el título
            CellStyle titleStyle = sheet.getWorkbook().createCellStyle();

            // Crear una fuente para el título
            Font titleFont = sheet.getWorkbook().createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 20); // Tamaño de la fuente
            titleFont.setColor(IndexedColors.WHITE.getIndex()); // Color de fuente blanco

            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor((short) 71); // Color de fondo azul
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Crear una celda y unir 3 celdas en la fila 0
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("REPORTE DEL MÓDULO ESPECIALIZADO");
            cell.setCellStyle(titleStyle);

            // Unir celdas A1, B1 y C1
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));

            //primera fila
            Row rowFecha = sheet.createRow(3);
            Cell cellFecha = rowFecha.createCell(0);
            Cell cellFechaValue = rowFecha.createCell(1);
            cellFecha.setCellValue("Fecha y hora de actualización: ");
            cellFechaValue.setCellValue(DateTimeUtil.dateToHoraFormatPR(totales.getFechaActualizacion()));

            Cell cellContabilizadoTitulo = rowFecha.createCell(2);
            cellContabilizadoTitulo.setCellValue("Acta contabilizadas: ");
            Cell cellContabilizadoValue = rowFecha.createCell(3);
            cellContabilizadoValue.setCellValue(totales.getContabilizadas());
            cellContabilizadoValue.setCellStyle(style5);

            sheet.autoSizeColumn(0);

            //segunda fila
            Row rowHora = sheet.createRow(4);
            Cell cellActas = rowHora.createCell(0);
            Cell cellActasValue = rowHora.createCell(1);
            cellActas.setCellValue("Total de actas : ");
            cellActasValue.setCellValue(totales.getTotalActas());

            Cell cellParaEnvio = rowHora.createCell(2);
            cellParaEnvio.setCellValue("Acta para envio al JEE: ");
            Cell cellParaEnvioValor = rowHora.createCell(3);
            cellParaEnvioValor.setCellValue(totales.getEnviadasJee());
            cellParaEnvioValor.setCellStyle(style5);

            sheet.autoSizeColumn(4);

        }
    }

    private void crearIconoEnlaceActa(Cell cell, CreationHelper helper, String value,CellStyle style1,CellStyle style2,int i) {

        // Crear un hipervínculo
        XSSFHyperlink hyperlink = (XSSFHyperlink)helper.createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(value);

        cell.setHyperlink(hyperlink);
        cell.setCellValue("Acta");

        if(i!=0 && i%2 != 0) {
            cell.setCellStyle(style1);
        } else {
            cell.setCellStyle(style2);
        }

    }

    private void crearIconoEnlaceResoluciones(Row row, CreationHelper helper, String value, int columna,CellStyle style1,CellStyle style2,int i) {
        String[] resoluciones = value.split(";");
        int inicio = 0;
        for(String resolucion: resoluciones) {

            // Crear un hipervínculo
            XSSFHyperlink hyperlink = (XSSFHyperlink)helper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(resolucion);

            Cell celldaLocal = row.createCell(columna + inicio);
            celldaLocal.setHyperlink(hyperlink);
            celldaLocal.setCellValue("Resolución " + (inicio +1));

            if(i!=0 && i%2 != 0) {
                celldaLocal.setCellStyle(style1);
            } else {
                celldaLocal.setCellStyle(style2);
            }

            inicio ++;
        }


    }


}
