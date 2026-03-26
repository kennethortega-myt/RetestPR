package pe.gob.onpe.pradminbackend.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateTimeUtil {

    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String AMERICA_LIMA = "America/Lima";
    public static final String DD_MM_YYYY_FORMAT = "dd/MM/yyyy";
    public static final String YYYY_MM_DD_HH_MM_SS_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDate getDateByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDate();
    }

    public static LocalDateTime getDateTimeByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDateTime();
    }

    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
        return LocalDate.parse(date, formatter);
    }

    public static String getDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT));
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
        return instant.atZone(ZoneId.of(AMERICA_LIMA)).toLocalDateTime();
    }

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    public static String getDateTimeFormat(LocalDateTime date, String formato) {
        return date.format(DateTimeFormatter.ofPattern(formato));
    }

    public static String dateToFormat(Date date) {
        DateFormat dfD = new SimpleDateFormat(DD_MM_YYYY_FORMAT);
        return dfD.format(date);
    }

    public static String dateToHoraFormat(Date date) {
        DateFormat dfD = new SimpleDateFormat("hh:mm a");
        return dfD.format(date);
    }

    public static String dateToHoraFormatPR(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_FORMAT);
        return DateUtil.dateFormatter(YYYY_MM_DD_HH_MM_SS_FORMAT,
                "dd/MM/yyyy ' - ' HH:mm:ss ' h'", sdf.format(date));
    }

    public static String dateToHoraFormatReporte(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_FORMAT);
        return DateUtil.dateFormatter(YYYY_MM_DD_HH_MM_SS_FORMAT,
                "yyyy-MM-dd_HH-mm-ss", sdf.format(date));
    }

       public static String dateToHoraFormatReporte(LocalDateTime date) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_FORMAT);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        // Primero formateamos al inputFormat (innecesario si ya tienes LocalDateTime bien formado)
        String formattedDate = date.format(inputFormatter);

        // Luego parseamos de nuevo para convertir al formato de salida
        return LocalDateTime.parse(formattedDate, inputFormatter).format(outputFormatter);
    }
    
    
}
