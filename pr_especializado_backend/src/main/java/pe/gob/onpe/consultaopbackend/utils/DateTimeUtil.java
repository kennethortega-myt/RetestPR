package pe.gob.onpe.consultaopbackend.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateTimeUtil {

    private DateTimeUtil() {
        // Private constructor to hide the implicit public one
    }
    public static final String AMERICA_LIMA = "America/Lima";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

    public static LocalDate getDateByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDate();
    }

    public static LocalDateTime getDateTimeByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDateTime();
    }

    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
        return LocalDate.parse(date, formatter);
    }

    public static String getDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
    }

    public static Date convertirLocalDateTimeAFecha(LocalDateTime localDateTime) {
        // Convertir LocalDateTime a Instant
        Instant instant = localDateTime.atZone(ZoneId.of(AMERICA_LIMA)).toInstant();

        // Convertir Instant a Date
        return Date.from(instant);
    }

    public static LocalDateTime convertirFechaALocalDateTime(Date date) {
        // Convertir Date a Instant
        Instant instant = date.toInstant();

        // Convertir Instant a LocalDateTime usando la zona horaria del sistema
        return instant.atZone(ZoneId.of(AMERICA_LIMA)).toLocalDateTime();
    }

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM));
    }
    
    public static String getDateTimeFormat(LocalDateTime date, String formato) {
        return date.format(DateTimeFormatter.ofPattern(formato));
    }

    public static String dateToFormat(Date date) {
        DateFormat dfD = new SimpleDateFormat(DD_MM_YYYY);
        return dfD.format(date);
    }

    public static String dateToHoraFormat(Date date) {
        DateFormat dfD = new SimpleDateFormat("hh:mm a");
        return dfD.format(date);
    }

    public static String dateToHoraFormatPR(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return DateUtil.dateFormatter(YYYY_MM_DD_HH_MM_SS,
                "dd/MM/yyyy ' - ' HH:mm:ss ' h'", sdf.format(date));
    }

    public static String formatDateToReportTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return DateUtil.dateFormatter(YYYY_MM_DD_HH_MM_SS,
                "yyyy-MM-dd_HH-mm-ss", sdf.format(date));
    }

    public static String formatLocalDateTimeToReportTimestamp(LocalDateTime date) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return date.format(outputFormatter);
    }
    
    
}
