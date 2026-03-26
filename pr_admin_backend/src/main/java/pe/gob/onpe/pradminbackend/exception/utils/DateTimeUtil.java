package pe.gob.onpe.pradminbackend.exception.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final String AMERICA_LIMA = "America/Lima";

    private DateTimeUtil() {

    }
    public static LocalDate getDateByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDate();
    }

    public static LocalDateTime getDateTimeByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDateTime();
    }

    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

    public static String getDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
